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
import it.polimi.ingsw.model.exceptions.IllegalStateException;
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
        checkBuildingState(username);

        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");

        component.pickComponent(board, player);
    }

    public void releaseComponent(String username, int componentId) {
        checkBuildingState(username);

        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");

        component.releaseComponent(board, player);
    }

    public void reserveComponent(String username, int componentId) {
        checkBuildingState(username);

        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");

        component.reserveComponent(player);
    }

    public void insertComponent(String username, int componentId, int row, int col, int rotations, boolean weld) {
        checkBuildingState(username);

        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");

        component.insertComponent(player, row, col, rotations, weld);
    }

    public void moveComponent(String username, int componentId, int row, int col, int rotations) {
        checkBuildingState(username);

        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");

        component.moveComponent(player, row, col, rotations);
    }

    public void rotateComponent(String username, int componentId, int num) {
        checkBuildingState(username);

        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");

        component.rotateComponent(player, num);
    }

    public void lookCardPile(String username, int deckIndex) {
        if (getPlayerState(username) == PlayerState.LOOK_CARD_PILE) releaseCardPile(username);
        else if (getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");

        if (deckIndex < 0 || deckIndex > 2) throw new IllegalArgumentException("Invalid deck index");
        else if (board.getCardPilesWatchMap().containsValue(deckIndex)) throw new IllegalArgumentException("Another player is already looking this card pile");

        PlayerData player = board.getPlayerEntityByUsername(username);
        Ship ship = player.getShip();
        boolean valid = false;
        for (Optional<Component>[] row : ship.getDashboard())
            for (Optional<Component> component : row)
                if (component.isPresent() && (component.get().getX() != 3 || component.get().getY() != 2))
                    valid = true;

        if (!valid)
            throw new IllegalArgumentException("You have to insert at least one component to see a card pile");

        player.getShip().getHandComponent().ifPresent(c -> c.releaseComponent(board, player));

        setPlayerState(username, PlayerState.LOOK_CARD_PILE);
        board.getCardPilesWatchMap().put(username, deckIndex);

        int startingDeckIndex = deckIndex == 0 ? 0 : (deckIndex == 1 ? 3 : 6);
        int endingDeckIndex = startingDeckIndex + 3;
        EventContext.emit(new CardPileLookedEvent(username, deckIndex, board.getCardPile().subList(startingDeckIndex, endingDeckIndex)));
        EventContext.emit(new CardPileLookedEvent(username, deckIndex, null));
    }

    public void releaseCardPile(String username) {
        setPlayerState(username, PlayerState.BUILD);
        board.getCardPilesWatchMap().remove(username);
        EventContext.emit(new CardPileReleasedEvent(username));
    }

    public void moveHourglass(String username) {
        if (getPlayerState(username) == PlayerState.LOOK_CARD_PILE) releaseCardPile(username);
        else if (
            getPlayerState(username) != PlayerState.BUILD &&
            (getPlayerState(username) != PlayerState.WAIT || board.getPlayerEntityByUsername(username).hasEndedInAdvance())
        ) throw new IllegalStateException("State is not BUILDING or WAIT");

        board.moveHourglass(username, this);
        EventContext.emit(new HourglassMovedEvent());
    }

    public void setReady(String username) {
        if (getPlayerState(username) == PlayerState.LOOK_CARD_PILE) releaseCardPile(username);
        else if (getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");

        PlayerData player = board.getPlayerEntityByUsername(username);
        player.setReady(board);
        setPlayerState(username, PlayerState.WAIT);

        if (arePlayersReady())
            moveStateAfterBuilding();
    }

    private boolean arePlayersReady() {
        return board.getStartingDeck().stream()
                .filter(p -> !p.hasEndedInAdvance())
                .toList()
                .isEmpty();
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
        if (getPlayerState(username) != PlayerState.CHECK) throw new IllegalStateException("State is not CHECKING");
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
        totalPlayers = Stream.concat(
                board.getPlayers().stream().map(AbstractMap.SimpleEntry::getKey),
                board.getStartingDeck().stream().filter(p -> !p.hasEndedInAdvance()))
            .toList();
        for (PlayerData player : totalPlayers)
            if (playersState.get(player.getUsername()) != PlayerState.WAIT)
                return false;
        return true;
    }

    public void chooseAlien(String username, Map<Integer, AlienType> aliensIds) {
        if (getPlayerState(username) != PlayerState.WAIT_ALIEN) throw new IllegalStateException("State is not WAIT_ALIEN");

        for (int id : aliensIds.keySet()) { // Put alien in all cabins in aliensIds list
            if (!(board.getMapIdComponents().get(id) instanceof CabinComponent cabin)) throw new CabinComponentNotValidException("Component is not a cabin");
            cabin.setAlien(aliensIds.get(id), board.getPlayerEntityByUsername(username).getShip());
        }
        setPlayerState(username, PlayerState.WAIT);

        int playerIndex = board.getPlayersByPos().indexOf(board.getPlayerEntityByUsername(username)) + 1;
        manageChooseAlienPhase(playerIndex);
    }

    public void chooseShipPart(String username, int partIndex) {
        if (getPlayerState(username) != PlayerState.WAIT_SHIP_PART) throw new IllegalStateException("State is not WAIT_SHIP_PART");

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

    public void drawCard(String username) {
        if (getPlayerState(username) != PlayerState.DRAW_CARD) throw new IllegalStateException("State is not DRAW_CARD");

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
        if (getPlayerState(username) != PlayerState.WAIT_CANNONS) throw new IllegalStateException("State is not WAIT_CANNONS");

        List<BatteryComponent> batteries = batteriesIds.stream()
                .filter(id -> board.getMapIdComponents().get(id) != null && board.getMapIdComponents().get(id).matchesType(BatteryComponent.class))
                .map(id -> board.getMapIdComponents().get(id).castTo(BatteryComponent.class))
                .toList();

        List<CannonComponent> cannonComponents = cannonComponentsIds.stream()
                .filter(id -> board.getMapIdComponents().get(id) != null && board.getMapIdComponents().get(id).matchesType(CannonComponent.class))
                .map(id -> board.getMapIdComponents().get(id).castTo(CannonComponent.class))
                .toList();

        Card card = board.getCardPile().get(board.getCardPilePos());
        Command command = new CannonCommand(this, board, username, batteries, cannonComponents);
        boolean finish = command.execute(card);

        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    public void activateEngines(String username, List<Integer> batteriesIds, List<Integer> engineComponentsIds) {
        if (getPlayerState(username) != PlayerState.WAIT_ENGINES) throw new IllegalStateException("State is not WAIT_ENGINES");

        List<BatteryComponent> batteries = batteriesIds.stream()
                .filter(id -> board.getMapIdComponents().get(id) != null && board.getMapIdComponents().get(id).matchesType(BatteryComponent.class))
                .map(id -> board.getMapIdComponents().get(id).castTo(BatteryComponent.class))
                .toList();

        List<EngineComponent> engineComponents = engineComponentsIds.stream()
                .filter(id -> board.getMapIdComponents().get(id) != null && board.getMapIdComponents().get(id).matchesType(EngineComponent.class))
                .map(id -> board.getMapIdComponents().get(id).castTo(EngineComponent.class))
                .toList();

        Card card = board.getCardPile().get(board.getCardPilePos());
        Command command = new EngineCommand(this, board, username, batteries, engineComponents);
        boolean finish = command.execute(card);

        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    public void activateShield(String username, Integer batteryId) {
        if (getPlayerState(username) != PlayerState.WAIT_SHIELD) throw new IllegalStateException("State is not WAIT_SHIELD");

        BatteryComponent component;
        if (batteryId != null) {
            if (board.getMapIdComponents().get(batteryId) == null || !board.getMapIdComponents().get(batteryId).matchesType(BatteryComponent.class))
                throw new ComponentNotValidException("Invalid component id");
            component = board.getMapIdComponents().get(batteryId).castTo(BatteryComponent.class);
        }
        else
            component = null;

        Card card = board.getCardPile().get(board.getCardPilePos());
        Command command = new ShieldCommand(this, board, username, component);
        boolean finish = command.execute(card);

        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    public void updateGoods(String username, Map<Integer, List<ColorType>> cargoHoldsIds, List<Integer> batteriesIds) {
        if (getPlayerState(username) != PlayerState.WAIT_GOODS && getPlayerState(username) != PlayerState.WAIT_REMOVE_GOODS) throw new IllegalStateException("State is not WAIT_GOODS or WAIT_REMOVE_GOODS");

        List<BatteryComponent> batteries = batteriesIds.stream()
                .filter(id -> board.getMapIdComponents().get(id) != null && board.getMapIdComponents().get(id).matchesType(BatteryComponent.class))
                .map(id -> board.getMapIdComponents().get(id).castTo(BatteryComponent.class))
                .toList();

        Map<SpecialCargoHoldsComponent, List<ColorType>> cargoHolds = new HashMap<>();
        cargoHoldsIds.keySet().stream()
                .filter(id -> board.getMapIdComponents().get(id) != null && board.getMapIdComponents().get(id).matchesType(SpecialCargoHoldsComponent.class))
                .forEach(id -> cargoHolds.put(board.getMapIdComponents().get(id).castTo(SpecialCargoHoldsComponent.class), cargoHoldsIds.get(id)));

        Card card = board.getCardPile().get(board.getCardPilePos());
        Command command = new GoodCommand(this, board, username, cargoHolds, batteries);
        boolean finish = command.execute(card);

        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    public void removeCrew(String username, List<Integer> cabinsIds) {
        if (getPlayerState(username) != PlayerState.WAIT_REMOVE_CREW) throw new IllegalStateException("State is not WAIT_REMOVE_CREW");

        List<CabinComponent> cabins = cabinsIds.stream()
                .filter(id -> board.getMapIdComponents().get(id) != null && board.getMapIdComponents().get(id).matchesType(CabinComponent.class))
                .map(id -> board.getMapIdComponents().get(id).castTo(CabinComponent.class))
                .toList();

        Card card = board.getCardPile().get(board.getCardPilePos());
        Command command = new RemoveCrewCommand(this, board, username, cabins);
        boolean finish = command.execute(card);

        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    public void rollDices(String username) {
        if (getPlayerState(username) != PlayerState.WAIT_ROLL_DICES) throw new IllegalStateException("State is not WAIT_ROLL_DICES");

        Card card = board.getCardPile().get(board.getCardPilePos());
        RollDicesCommand command = new RollDicesCommand(this, board, username);

        boolean finish = command.execute(card);
        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    public void getBoolean(String username, boolean value) {
        if (getPlayerState(username) != PlayerState.WAIT_BOOLEAN) throw new IllegalStateException("State is not WAIT_BOOLEAN");

        Card card = board.getCardPile().get(board.getCardPilePos());
        boolean finish = card.doCommandEffects(PlayerState.WAIT_BOOLEAN, value, this, board, username);
        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    public void getIndex(String username, Integer value) {
        if (getPlayerState(username) != PlayerState.WAIT_INDEX) throw new IllegalStateException("State is not WAIT_INDEX");

        Card card = board.getCardPile().get(board.getCardPilePos());
        boolean finish = card.doCommandEffects(PlayerState.WAIT_INDEX, value, this, board, username);
        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    public void endFlight(String username) {
        if (getPlayerState(username) == PlayerState.BUILD || getPlayerState(username) == PlayerState.LOOK_CARD_PILE) throw new IllegalStateException("You can't end flight in this state");
        PlayerData player = board.getPlayerEntityByUsername(username);

        boolean isDrawOrAlienPhase = true;
        for (PlayerData p : board.getPlayersByPos())
            if (playersState.get(p.getUsername()) != PlayerState.WAIT && playersState.get(p.getUsername()) != PlayerState.DRAW_CARD && playersState.get(p.getUsername()) != PlayerState.WAIT_ALIEN)
                isDrawOrAlienPhase = false;

        player.endFlight();
        if (isDrawOrAlienPhase) {
            board.moveToStartingDeck(player);
            setPlayerState(player.getUsername(), PlayerState.WAIT);
        }
    }

    public void leaveGame(String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        player.setLeftGame(true);
        player.endFlight();

        board.moveToStartingDeck(player);
        setPlayerState(player.getUsername(), PlayerState.WAIT);
    }

    public void rejoinGame(String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        player.setLeftGame(false);
    }

    public void endGame() {
        for (String username : usernames)
            setPlayerState(username, PlayerState.END);
        board.calcRanking();
    }

    private void checkBuildingState(String username) {
        if (getPlayerState(username) == PlayerState.LOOK_CARD_PILE) releaseCardPile(username);
        else if (getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
    }

    protected abstract void manageChooseAlienPhase(int playerIndex);

    /**
     * only for tests
     */
    public Board getBoard() {
        return board;
    }

}
