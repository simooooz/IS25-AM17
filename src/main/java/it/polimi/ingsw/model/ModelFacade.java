package it.polimi.ingsw.model;

import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.common.model.events.game.*;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.cards.commands.*;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.events.CardPileLookedEvent;
import it.polimi.ingsw.model.events.CardRevealedEvent;
import it.polimi.ingsw.model.events.CardUpdatedEvent;
import it.polimi.ingsw.model.exceptions.CabinComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.*;
import java.util.stream.Stream;

public abstract class ModelFacade {

    protected Board board;
    private final List<String> usernames;
    protected final Map<String, PlayerState> playersState;

    public ModelFacade(List<String> usernames) {
        this.usernames = usernames;
        this.playersState = new HashMap<>();
    }

    public PlayerState getPlayerState(String username) {
        return playersState.get(username);
    }

    public void setPlayerState(String username, PlayerState newState) {
        this.playersState.put(username, newState);
        EventContext.emit(new PlayersStateUpdatedEvent(playersState));
    }

    public void startMatch() {
        EventContext.emit(new MatchStartedEvent());
        for (String username : usernames)
            setPlayerState(username, PlayerState.BUILD);
        board.startMatch(this);
    }

    public void pickComponent(String username, int componentId) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");

        component.pickComponent(board, player);
    }

    public void releaseComponent(String username, int componentId) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");

        component.releaseComponent(board, player);
    }

    public void reserveComponent(String username, int componentId) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");

        component.reserveComponent(player);
    }

    public void insertComponent(String username, int componentId, int row, int col, int rotations, boolean weld) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");

        component.insertComponent(player, row, col, rotations, weld);
    }

    public void moveComponent(String username, int componentId, int row, int col, int rotations) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");

        component.moveComponent(player, row, col, rotations);
    }

    public void rotateComponent(String username, int componentId, int num) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");

        component.rotateComponent(player, num);
    }

    public void lookCardPile(String username, int deckIndex) {
        if (deckIndex < 0 || deckIndex > 2) throw new IllegalArgumentException("Invalid deck index");
        else if (PlayerState.LOOK_CARD_PILE.getDeckIndex().containsValue(deckIndex)) throw new IllegalArgumentException("Another player is already looking this card pile");

        PlayerData player = board.getPlayerEntityByUsername(username);
        player.getShip().getHandComponent().ifPresent(c -> c.releaseComponent(board, player));

        setPlayerState(username, PlayerState.LOOK_CARD_PILE);
        PlayerState.LOOK_CARD_PILE.getDeckIndex().put(username, deckIndex);

        int startingDeckIndex = deckIndex == 0 ? 0 : (deckIndex == 1 ? 3 : 6);
        int endingDeckIndex = startingDeckIndex + 3;
        EventContext.emit(new CardPileLookedEvent(username, deckIndex, board.getCardPile().subList(startingDeckIndex, endingDeckIndex)));
        EventContext.emit(new CardPileLookedEvent(username, deckIndex, null));
    }

    public void releaseCardPile(String username) {
        setPlayerState(username, PlayerState.BUILD);
        PlayerState.LOOK_CARD_PILE.getDeckIndex().remove(username);
        EventContext.emit(new CardPileReleasedEvent(username));
    }

    public void moveHourglass(String username) {
        board.moveHourglass(username, this);
        EventContext.emit(new HourglassMovedEvent());
    }

    public void setReady(String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        player.setReady(board);
        setPlayerState(username, PlayerState.WAIT);

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

    public void moveStateAfterBuilding() {
        for (PlayerData player : board.getPlayersByPos())
            if (!player.getShip().checkShip()) {
                setPlayerState(player.getUsername(), PlayerState.CHECK);
                board.moveToStartingDeck(player);
            }
            else
                setPlayerState(player.getUsername(), PlayerState.WAIT);

        for (PlayerData player : board.getPlayersByPos()) {
            int index = board.getPlayersByPos().indexOf(player);
            if (index == 0 || index == 1 || index == 2)
                board.movePlayer(player, board.getBoardOrderPos()[index] - board.getPlayers().stream().filter(entry -> entry.getKey().equals(player)).findFirst().orElseThrow().getValue());
        }

        board.shuffleCards();

        if (areShipsReady())
            manageChooseAlienPhase(0);
    }

    public void checkShip(String username, List<Integer> toRemove) {
        PlayerData player = board.getPlayerEntityByUsername(username);

        for (int componentId : toRemove) {
            Component component = board.getMapIdComponents().get(componentId);
            if (component == null) throw new ComponentNotValidException("Invalid component id");
            component.affectDestroy(player);
        }

        if (player.getShip().checkShip()) { // If now ship is ready
            setPlayerState(username, PlayerState.WAIT);
            board.moveToBoard(board.getPlayerEntityByUsername(username));
        }

        if (areShipsReady())
            manageChooseAlienPhase(0);
    }

    private boolean areShipsReady() {
        List<PlayerData> totalPlayers;
        totalPlayers = Stream.concat(board.getPlayers().stream().map(AbstractMap.SimpleEntry::getKey), board.getStartingDeck().stream()).toList();
        for (PlayerData player : totalPlayers)
            if (playersState.get(player.getUsername()) != PlayerState.WAIT)
                return false;
        return true;
    }

    public void chooseAlien(String username, Map<Integer, AlienType> aliensIds) {
        for (int id : aliensIds.keySet()) { // Put alien in all cabins in aliensIds list
            if (!(board.getMapIdComponents().get(id) instanceof CabinComponent cabin)) throw new CabinComponentNotValidException("Component is not a cabin");
            cabin.setAlien(aliensIds.get(id), board.getPlayerEntityByUsername(username).getShip());
        }
        setPlayerState(username, PlayerState.WAIT);

        int playerIndex = board.getPlayersByPos().indexOf(board.getPlayerEntityByUsername(username)) + 1;
        manageChooseAlienPhase(playerIndex);
    }

    public void chooseShipPart(String username, int partIndex) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        Ship ship = player.getShip();
        List<List<Component>> groups = ship.calcShipParts();

        if (partIndex < 0 || partIndex >= groups.size()) throw new IndexOutOfBoundsException("Part index not valid");

        // Remove other parts
        for (int i = 0; i < groups.size(); i++)
            if (i != partIndex)
                for (Component componentToRemove : groups.get(i))
                    componentToRemove.affectDestroy(player);

        Card card = board.getCardPile().get(board.getCardPilePos());
        boolean finish = card.doCommandEffects(PlayerState.WAIT_SHIP_PART, this, board, username);
        if (finish) { board.pickNewCard(this); }
    }

    public void drawCard() {
        if (board.getCardPilePos() < board.getCardPile().size()) {
            Card card = board.getCardPile().get(board.getCardPilePos());
            EventContext.emit(new CardRevealedEvent(card));

            boolean finished = card.startCard(this, this.board);
            EventContext.emit(new CardUpdatedEvent(card));
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
        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    public void activateEngines(String username, List<Integer> batteriesIds, List<Integer> engineComponentsIds) {
        List<BatteryComponent> batteries = batteriesIds.stream().map(id -> (BatteryComponent) board.getMapIdComponents().get(id)).toList();
        List<EngineComponent> engineComponents = engineComponentsIds.stream().map(id -> (EngineComponent) board.getMapIdComponents().get(id)).toList();

        Card card = board.getCardPile().get(board.getCardPilePos());
        Command command = new EngineCommand(this, board, username, batteries, engineComponents);
        boolean finish = command.execute(card);
        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    public void activateShield(String username, Integer batteryId) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        BatteryComponent component = batteryId == null ? null : (BatteryComponent) board.getMapIdComponents().get(batteryId);
        Command command = new ShieldCommand(this, board, username, component);
        boolean finish = command.execute(card);
        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    public void updateGoods(String username, Map<Integer, List<ColorType>> cargoHoldsIds, List<Integer> batteriesIds) {
        List<BatteryComponent> batteries = batteriesIds.stream().map(id -> (BatteryComponent) board.getMapIdComponents().get(id)).toList();
        Map<SpecialCargoHoldsComponent, List<ColorType>> cargoHolds = new HashMap<>();
        cargoHoldsIds.forEach((id, value) -> cargoHolds.put((SpecialCargoHoldsComponent) board.getMapIdComponents().get(id), value));

        Card card = board.getCardPile().get(board.getCardPilePos());
        Command command = new GoodCommand(this, board, username, cargoHolds, batteries);

        boolean finish = command.execute(card);
        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    public void removeCrew(String username, List<Integer> cabinsIds) {
        List<CabinComponent> cabins = cabinsIds.stream().map(id -> (CabinComponent) board.getMapIdComponents().get(id)).toList();

        Card card = board.getCardPile().get(board.getCardPilePos());
        Command command = new RemoveCrewCommand(this, board, username, cabins);
        boolean finish = command.execute(card);
        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    public void rollDices(String username, Integer value) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        RollDicesCommand command = new RollDicesCommand(this, board, username, value);

        boolean finish = command.execute(card);
        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    public void getBoolean(String username, boolean value) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        boolean finish = card.doCommandEffects(PlayerState.WAIT_BOOLEAN, value, this, board, username);
        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    public void getIndex(String username, Integer value) {
        Card card = board.getCardPile().get(board.getCardPilePos());
        boolean finish = card.doCommandEffects(PlayerState.WAIT_INDEX, value, this, board, username);
        EventContext.emit(new CardUpdatedEvent(card));
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

    public void leaveGame(String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        setPlayerState(username, PlayerState.END);
        player.setLeftGame(true);
        endFlight(username);
    }

    public void endGame() {
        for (String username : usernames)
            setPlayerState(username, PlayerState.END);
        board.calcRanking();
    }

    protected abstract void manageChooseAlienPhase(int playerIndex);

    /**
     * only for tests
     */
    public Board getBoard() {
        return board;
    }

}
