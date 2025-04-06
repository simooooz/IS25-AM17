package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.cards.commands.*;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.exceptions.IllegalStateException;
import it.polimi.ingsw.model.exceptions.NoEnoughPlayerException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ModelFacade {

    private final Board board;
    private GameState state;

    public ModelFacade(List<String> usernames) {
        this.board = new Board(usernames);
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void startMatch() {
        if (this.board.getPlayers().size() < 2) throw new NoEnoughPlayerException("MIN players required: 2");

        state = GameState.BUILD;
        board.getTimeManagement().startTimer(this);
    }

    public void showComponent(int componentId) {
        board.getMapIdComponents().get(componentId).showComponent();
    }

    public void pickComponent(String username, int componentId) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        board.getMapIdComponents().get(componentId).pickComponent(board, ship);
    }

    public void releaseComponent(String username, int componentId) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        board.getMapIdComponents().get(componentId).releaseComponent(board, ship);
    }

    public void reserveComponent(String username, int componentId) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        board.getMapIdComponents().get(componentId).reserveComponent(board, ship);
    }

    public void insertComponent(String username,  int componentId, int row, int col) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        board.getCommonComponents().get(componentId).insertComponent(ship, row, col);
    }

    public void moveComponent(String username, int componentId, int row, int col) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        board.getMapIdComponents().get(componentId).moveComponent(ship, row, col);
    }

    public void rotateComponent(String username, int componentId, boolean clockwise) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        board.getMapIdComponents().get(componentId).rotateComponent(ship, clockwise);
    }

    public void lookCardPile(String username, int deckIndex) {
        if (deckIndex < 0 || deckIndex > 2) throw new IllegalArgumentException("Invalid deck index");
        Ship ship = board.getPlayerEntityByUsername(username).getShip();

        ship.getHandComponent().ifPresent(Component::weldComponent);

        int startingDeckIndex = deckIndex == 0 ? 0 : (deckIndex == 1 ? 3 : 6);
        int endingDeckIndex = startingDeckIndex + 3;
        List<Card> pile = board.getCardPile().subList(startingDeckIndex, endingDeckIndex); // TODO capiamo il getter
    }

    public void moveHourglass(String username) {
        if (board.getTimeManagement().getHourglassPos() == 1)
            board.getPlayersByPos().stream()
                    .filter(player -> player.getUsername().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Player " + username + " has not finished"));

        board.getTimeManagement().startTimer(this);
    }

    public void setReady(String username) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        ship.getHandComponent().ifPresent(Component::weldComponent);
        board.moveToBoard(board.getPlayerEntityByUsername(username));

        if (arePlayersReady())
            moveStateAfterBuilding();
    }

    public boolean isPlayerReady(String username) {
        return board.getStartingDeck().stream()
            .anyMatch(p -> p.getUsername().equals(username));
    }

    private boolean arePlayersReady() {
        return board.getStartingDeck().isEmpty();
    }

    public void playerJoined(String username) {
        // todo
    }

    public void playerLeft(String username) {
        this.board.removePlayer(username);
    }

    public void moveStateAfterBuilding() {
        state = GameState.CHECK;
        board.shuffleCards();
        if (areShipsReady())
            state = GameState.DRAW_CARD;
    }

    public void checkShip(String username, List<Integer> toRemove) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        if (ship.checkShip()) throw new RuntimeException("Ship was already ready");

        for (int componentId : toRemove)
            board.getMapIdComponents().get(componentId).affectDestroy(ship);

        if (areShipsReady())
            state = GameState.DRAW_CARD;
    }

    public boolean areShipsReady() {
        for (PlayerData player : board.getPlayersByPos())
            if (!player.getShip().checkShip())
                return false;
        return true;
    }

    public void nextCard(String username) {
        this.state = board.drawCard(board.getPlayerEntityByUsername(username));
    }

    public void activateCannons(String username, List<Integer> batteriesIds, List<Integer> cannonComponentsIds) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        if (card.getPlayersState().get(username) != PlayerState.WAIT_CANNONS) throw new IllegalStateException("Player " + username + " state is not WAIT_CANNONS");

        List<BatteryComponent> batteries = batteriesIds.stream().map(id -> (BatteryComponent) board.getMapIdComponents().get(id)).toList();
        List<CannonComponent> cannonComponents = cannonComponentsIds.stream().map(id -> (CannonComponent) board.getMapIdComponents().get(id)).toList();
        Command command = new CannonCommand(username, board, batteries, cannonComponents);
        command.execute(card);
        card.changeCardState(board, username);
    }

    public void activateEngines(String username, List<Integer> batteriesIds, List<Integer> engineComponentsIds) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        if (card.getPlayersState().get(username) != PlayerState.WAIT_ENGINES) throw new IllegalStateException("Player " + username + " state is not WAIT_ENGINES");

        List<BatteryComponent> batteries = batteriesIds.stream().map(id -> (BatteryComponent) board.getMapIdComponents().get(id)).toList();
        List<EngineComponent> engineComponents = engineComponentsIds.stream().map(id -> (EngineComponent) board.getMapIdComponents().get(id)).toList();
        Command command = new EngineCommand(username, board, batteries, engineComponents);
        command.execute(card);
        setState(card.changeCardState(board, username));
    }

    public void activateShield(String username, int batteryId) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        if (card.getPlayersState().get(username) != PlayerState.WAIT_SHIELD) throw new IllegalStateException("Player " + username + " state is not WAIT_SHIELD");

        Command command = new ShieldCommand(username, board, (BatteryComponent) board.getMapIdComponents().get(batteryId));
        command.execute(card);
        setState(card.changeCardState(board, username));
    }

    public void updateGoods(String username, Map<Integer, List<ColorType>> cargoHoldsIds, List<Integer> batteriesIds) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        if (card.getPlayersState().get(username) != PlayerState.WAIT_GOODS && card.getPlayersState().get(username) != PlayerState.WAIT_REMOVE_GOODS) throw new IllegalStateException("Player " + username + " state is not WAIT_GOODS or WAIT_REMOVE_GOODS");

        List<BatteryComponent> batteries = batteriesIds.stream().map(id -> (BatteryComponent) board.getMapIdComponents().get(id)).toList();
        Map<SpecialCargoHoldsComponent, List<ColorType>> cargoHolds = new HashMap<>();
        cargoHoldsIds.forEach((id, value) -> cargoHolds.put((SpecialCargoHoldsComponent) board.getMapIdComponents().get(id), value));

        Command command = new GoodCommand(username, board, cargoHolds, batteries);
        command.execute(card);
        setState(card.changeCardState(board, username));
    }

    public void removeCrew(String username, List<Integer> cabinsIds) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        if (card.getPlayersState().get(username) != PlayerState.WAIT_REMOVE_CREW) throw new IllegalStateException("Player " + username + " state is not WAIT_REMOVE_CREW");

        List<CabinComponent> cabins = cabinsIds.stream().map(id -> (CabinComponent) board.getMapIdComponents().get(id)).toList();
        Command command = new RemoveCrewCommand(username, board, cabins);
        command.execute(card);
        setState(card.changeCardState(board, username));
    }

    public void rollDices(String username) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        if (card.getPlayersState().get(username) != PlayerState.WAIT_ROLL_DICES) throw new IllegalStateException("Player " + username + " state is not WAIT_ROLL_DICES");

        Command command = new RollDicesCommand(username, board);
        command.execute(card);
        setState(card.changeCardState(board, username));
    }

    public void getBoolean(String username, boolean value) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        if (card.getPlayersState().get(username) != PlayerState.WAIT_BOOLEAN) throw new IllegalStateException("Player " + username + " state is not WAIT_BOOLEAN");

        card.doCommandEffects(PlayerState.WAIT_BOOLEAN, value, username, board);
        setState(card.changeCardState(board, username));
    }

    public void getIndex(String username, int value) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        if (card.getPlayersState().get(username) != PlayerState.WAIT_INDEX) throw new IllegalStateException("Player " + username + " state is not WAIT_INDEX");

        card.doCommandEffects(PlayerState.WAIT_INDEX, value, username, board);
        setState(card.changeCardState(board, username));
    }

    public void endFlight(String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (state == GameState.DRAW_CARD) {
            player.endFlight();
            board.moveToStartingDeck(player);
        }
        else
            player.endFlight();
    }

    /**
     * only for tests
     */
    public Board getBoard() {
        return board;
    }
}
