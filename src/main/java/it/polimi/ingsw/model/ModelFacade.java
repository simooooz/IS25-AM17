package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.cards.commands.*;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.exceptions.CabinComponentNotValidException;
import it.polimi.ingsw.model.exceptions.NoEnoughPlayerException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.*;

public class ModelFacade {

    private final Board board;
    private final List<String> usernames;
    private final Map<String, PlayerState> playersState;

    public ModelFacade(List<String> usernames) {
        this.usernames = usernames;
        this.board = new Board(usernames);
        this.playersState = new HashMap<>();
    }

    public PlayerState getPlayerState(String username) {
        return playersState.get(username);
    }

    public void setPlayerState(String username, PlayerState newState) {
        this.playersState.put(username, newState);
    }

    public void startMatch() {
        if (this.board.getStartingDeck().size() < 2) throw new NoEnoughPlayerException("MIN players required: 2");

        for (String username : usernames)
            playersState.put(username, PlayerState.BUILD);

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
        board.getMapIdComponents().get(componentId).insertComponent(ship, row, col);
    }

    public void moveComponent(String username, int componentId, int row, int col) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        board.getMapIdComponents().get(componentId).moveComponent(ship, row, col);
    }

    public void rotateComponent(String username, int componentId, int num) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        for (int i=0; i<num; i++)
            board.getMapIdComponents().get(componentId).rotateComponent(ship);
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
            .noneMatch(p -> p.getUsername().equals(username));
    }

    private boolean arePlayersReady() {
        return board.getStartingDeck().isEmpty();
    }

    public void playerJoined(String username) {
        // todo
    }

    public void playerLeft(String username) {
        // todo this.board.removePlayer(username);
    }

    public void moveStateAfterBuilding() {
        for (PlayerData player : board.getPlayersByPos())
            if (!player.getShip().checkShip())
                playersState.put(player.getUsername(), PlayerState.CHECK);
            else
                playersState.put(player.getUsername(), PlayerState.WAIT);

        board.shuffleCards();

        if (areShipsReady())
            manageChooseAlienPhase(0);
    }

    public void checkShip(String username, List<Integer> toRemove) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();

        for (int componentId : toRemove)
            board.getMapIdComponents().get(componentId).affectDestroy(ship);

        if (ship.checkShip()) // If now ship is ready
            playersState.put(username, PlayerState.WAIT);

        if (areShipsReady())
            manageChooseAlienPhase(0);
    }

    private boolean areShipsReady() {
        for (PlayerData player : board.getPlayersByPos())
            if (playersState.get(player.getUsername()) != PlayerState.WAIT)
                return false;
        return true;
    }

    private void manageChooseAlienPhase(int playerIndex) {
        boolean phaseDone = true;
        for (; playerIndex < board.getPlayersByPos().size(); playerIndex++) { // Check if next players have to choose alien
            PlayerData player = board.getPlayers().get(playerIndex).getKey();
            List<CabinComponent> cabins = player.getShip().getComponentByType(CabinComponent.class);
            for (CabinComponent cabin : cabins) {
                if (!cabin.getLinkedNeighbors(player.getShip()).stream()
                        .filter(c -> c instanceof OddComponent)
                        .toList().isEmpty()
                ) { // There is a cabin with an odd near odd component => player has to choose and phase isn't done
                    phaseDone = false;
                    playersState.put(player.getUsername(), PlayerState.WAIT_ALIEN);
                    break;
                }
            }
            if (!phaseDone) break;
        }

        if (phaseDone)
            playersState.put(board.getPlayersByPos().getFirst().getUsername(), PlayerState.DRAW_CARD);
    }

    public void chooseAlien(String username, Map<Integer, AlienType> aliensIds) {
        for (int id : aliensIds.keySet()) { // Put alien in all cabins in aliensIds list
            if (!(board.getMapIdComponents().get(id) instanceof CabinComponent cabin)) throw new CabinComponentNotValidException("Component is not a cabin");
            cabin.setAlien(aliensIds.get(id), board.getPlayerEntityByUsername(username).getShip());
        }
        playersState.put(username, PlayerState.WAIT);

        int playerIndex = board.getPlayersByPos().indexOf(board.getPlayerEntityByUsername(username)) + 1;
        manageChooseAlienPhase(playerIndex);
    }

    public void chooseShipPart(String username, int partIndex) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        List<List<Component>> groups = ship.calcShipParts();

        if (partIndex < 0 || partIndex >= groups.size()) throw new IndexOutOfBoundsException("Part index not valid");

        // Remove other parts
        for (int i = 0; i < groups.size(); i++)
            if (i != partIndex)
                for (Component componentToRemove : groups.get(i))
                    componentToRemove.affectDestroy(ship);

        Card card = board.getCardPile().get(board.getCardPilePos());
        boolean finish = card.doCommandEffects(PlayerState.WAIT_SHIP_PART, this, board, username);
        if (finish) { board.pickNewCard(this); }
    }

    public void nextCard() {
        if (board.getCardPilePos() < board.getCardPile().size()) {
            Card card = board.getCardPile().get(board.getCardPilePos());
            boolean finished = card.startCard(this, this.board);
            if (finished)
                board.pickNewCard(this);
        }
        else throw new RuntimeException("Card index out of bound");
    }

    public void activateCannons(String username, List<Integer> batteriesIds, List<Integer> cannonComponentsIds) {
        List<BatteryComponent> batteries = batteriesIds.stream().map(id -> (BatteryComponent) board.getMapIdComponents().get(id)).toList();
        List<CannonComponent> cannonComponents = cannonComponentsIds.stream().map(id -> (CannonComponent) board.getMapIdComponents().get(id)).toList();

        Card card = board.getCardPile().get(board.getCardPilePos());
        Command command = new CannonCommand(this, board, username, batteries, cannonComponents);
        boolean finish = command.execute(card);
        if (finish) { board.pickNewCard(this); }
    }

    public void activateEngines(String username, List<Integer> batteriesIds, List<Integer> engineComponentsIds) {
        List<BatteryComponent> batteries = batteriesIds.stream().map(id -> (BatteryComponent) board.getMapIdComponents().get(id)).toList();
        List<EngineComponent> engineComponents = engineComponentsIds.stream().map(id -> (EngineComponent) board.getMapIdComponents().get(id)).toList();

        Card card = board.getCardPile().get(board.getCardPilePos());
        Command command = new EngineCommand(this, board, username, batteries, engineComponents);
        boolean finish = command.execute(card);
        if (finish) { board.pickNewCard(this); }
    }

    public void activateShield(String username, Integer batteryId) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        BatteryComponent component = batteryId == null ? null : (BatteryComponent) board.getMapIdComponents().get(batteryId);
        Command command = new ShieldCommand(this, board, username, component);
        boolean finish = command.execute(card);
        if (finish) { board.pickNewCard(this); }
    }

    public void updateGoods(String username, Map<Integer, List<ColorType>> cargoHoldsIds, List<Integer> batteriesIds) {
        List<BatteryComponent> batteries = batteriesIds.stream().map(id -> (BatteryComponent) board.getMapIdComponents().get(id)).toList();
        Map<SpecialCargoHoldsComponent, List<ColorType>> cargoHolds = new HashMap<>();
        cargoHoldsIds.forEach((id, value) -> cargoHolds.put((SpecialCargoHoldsComponent) board.getMapIdComponents().get(id), value));

        Card card = board.getCardPile().get(board.getCardPilePos());
        Command command = new GoodCommand(this, board, username, cargoHolds, batteries);
        boolean finish = command.execute(card);
        if (finish) { board.pickNewCard(this); }
    }

    public void removeCrew(String username, List<Integer> cabinsIds) {
        List<CabinComponent> cabins = cabinsIds.stream().map(id -> (CabinComponent) board.getMapIdComponents().get(id)).toList();

        Card card = board.getCardPile().get(board.getCardPilePos());
        Command command = new RemoveCrewCommand(this, board, username, cabins);
        boolean finish = command.execute(card);
        if (finish) { board.pickNewCard(this); }
    }

    public void rollDices(String username) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        Command command = new RollDicesCommand(this, board, username);
        boolean finish = command.execute(card);
        if (finish) { board.pickNewCard(this); }
    }

    public void getBoolean(String username, boolean value) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        boolean finish = card.doCommandEffects(PlayerState.WAIT_INDEX, value, this, board, username);
        if (finish) { board.pickNewCard(this); }
    }

    public void getIndex(String username, int value) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        boolean finish = card.doCommandEffects(PlayerState.WAIT_INDEX, value, this, board, username);
        if (finish) { board.pickNewCard(this); }
    }

    public void endFlight(String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);

        boolean isDrawPhase = true;
        for (PlayerData p : board.getPlayersByPos())
            if (playersState.get(p.getUsername()) != PlayerState.WAIT || playersState.get(p.getUsername()) != PlayerState.DRAW_CARD)
                isDrawPhase = false;

        player.endFlight();
        if (isDrawPhase) {
            board.moveToStartingDeck(player);
        }
    }

    /**
     * only for tests
     */
    public Board getBoard() {
        return board;
    }
}
