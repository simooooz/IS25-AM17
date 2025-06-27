package it.polimi.ingsw.model;

import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.common.model.events.Event;
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
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Abstract facade class that provides the main interface for game model operations.
 * This class manages the game state, player interactions, and coordinates all game phases
 * including building, alien selection, card management, and combat phases.
 *
 * <p>The ModelFacade follows the Facade design pattern to provide a simplified interface
 * to the complex game model subsystem. It handles state transitions, validates player actions,
 * and ensures game rules are enforced throughout the gameplay.</p>
 *
 */
public abstract class ModelFacade {

    /** The game board containing all game state and components */
    protected Board board;

    /** List of player usernames participating in the game */
    private final List<String> usernames;

    /** Map tracking the current state of each player */
    protected final Map<String, PlayerState> playersState;

    /**
     * Constructs a new ModelFacade with the specified list of player usernames.
     * Initializes the player state tracking map.
     *
     * @param usernames the list of player usernames participating in the game
     * @throws NullPointerException if usernames is null
     * @throws IllegalArgumentException if usernames is empty
     */
    public ModelFacade(List<String> usernames) {
        this.usernames = usernames;
        this.playersState = new HashMap<>();
    }

    /**
     * Returns a copy of the current players' state map.
     *
     * @return an unmodifiable map containing each player's current state
     */
    public Map<String, PlayerState> getPlayersState() {
        return playersState;
    }

    /**
     * Gets the current state of a specific player.
     *
     * @param username the username of the player
     * @return the current PlayerState of the specified player, or null if player not found
     */
    public PlayerState getPlayerState(String username) {
        return playersState.get(username);
    }

    /**
     * Sets the state of a specific player and emits a state update event.
     * This method also triggers a PlayersStateUpdatedEvent to notify observers.
     *
     * @param username the username of the player
     * @param newState the new PlayerState to set
     */
    public void setPlayerState(String username, PlayerState newState) {
        this.playersState.put(username, newState);
        EventContext.emit(new PlayersStateUpdatedEvent(playersState));
    }

    /**
     * Starts a new match by emitting a match started event, setting all players
     * to BUILD state, and initializing the board for gameplay.
     *
     * @throws IllegalStateException if the game is already started
     */
    public void startMatch() {
        EventContext.emit(new MatchStartedEvent());
        for (String username : usernames)
            setPlayerState(username, PlayerState.BUILD);
        board.startMatch(this);
    }

    /**
     * Allows a player to pick up a component from the board during the building phase.
     * The component becomes available in the player's hand for placement.
     *
     * @param username the username of the player picking the component
     * @param componentId the unique identifier of the component to pick
     * @throws ComponentNotValidException if the component ID is invalid
     * @throws IllegalStateException if the player is not in building state
     */
    public void pickComponent(String username, int componentId) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");
        checkBuildingState(username);

        component.pickComponent(board, player);
    }

    /**
     * Allows a player to release a component they have picked up, returning it
     * to the available components pool.
     *
     * @param username the username of the player releasing the component
     * @param componentId the unique identifier of the component to release
     * @throws ComponentNotValidException if the component ID is invalid
     * @throws IllegalStateException if the player is not in building state
     */
    public void releaseComponent(String username, int componentId) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");
        checkBuildingState(username);

        component.releaseComponent(board, player);
    }

    /**
     * Allows a player to reserve a component, preventing other players from picking it
     * while keeping it available for the current player.
     *
     * @param username the username of the player reserving the component
     * @param componentId the unique identifier of the component to reserve
     * @throws ComponentNotValidException if the component ID is invalid
     * @throws IllegalStateException if the player is not in building state
     */
    public void reserveComponent(String username, int componentId) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");
        checkBuildingState(username);

        component.reserveComponent(player);
    }

    /**
     * Inserts a component into the player's ship at the specified position with
     * the given rotation and welding option.
     *
     * @param username the username of the player inserting the component
     * @param componentId the unique identifier of the component to insert
     * @param row the row position on the ship's grid
     * @param col the column position on the ship's grid
     * @param rotations the number of 90-degree clockwise rotations to apply
     * @param weld whether to weld the component (making it permanent)
     * @throws ComponentNotValidException if the component ID is invalid
     * @throws IllegalStateException if the player is not in building state
     * @throws IllegalArgumentException if the position is invalid or occupied
     */
    public void insertComponent(String username, int componentId, int row, int col, int rotations, boolean weld) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");
        checkBuildingState(username);

        component.insertComponent(player, row, col, rotations, weld);
    }

    /**
     * Moves an already placed component to a new position on the player's ship
     * with optional rotation change.
     *
     * @param username the username of the player moving the component
     * @param componentId the unique identifier of the component to move
     * @param row the new row position on the ship's grid
     * @param col the new column position on the ship's grid
     * @param rotations the number of 90-degree clockwise rotations to apply
     * @throws ComponentNotValidException if the component ID is invalid
     * @throws IllegalStateException if the player is not in building state
     * @throws IllegalArgumentException if the new position is invalid or occupied
     */
    public void moveComponent(String username, int componentId, int row, int col, int rotations) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");
        checkBuildingState(username);

        component.moveComponent(player, row, col, rotations);
    }

    /**
     * Rotates a component on the player's ship by the specified number of
     * 90-degree clockwise turns.
     *
     * @param username the username of the player rotating the component
     * @param componentId the unique identifier of the component to rotate
     * @param num the number of 90-degree clockwise rotations to apply
     * @throws ComponentNotValidException if the component ID is invalid
     * @throws IllegalStateException if the player is not in building state
     */
    public void rotateComponent(String username, int componentId, int num) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        Component component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");
        checkBuildingState(username);

        component.rotateComponent(player, num);
    }

    /**
     * Allows a player to look at a specific card pile. The player must have
     * at least one component placed on their ship to perform this action.
     * Only one player can look at a specific card pile at a time.
     *
     * @param username the username of the player looking at the card pile
     * @param deckIndex the index of the deck to look at (0, 1, or 2)
     * @throws IllegalStateException if the player is not in building or look card pile state
     * @throws IllegalArgumentException if the deck index is invalid or already being viewed
     */
    public void lookCardPile(String username, int deckIndex) {
        if (getPlayerState(username) != PlayerState.BUILD && getPlayerState(username) != PlayerState.LOOK_CARD_PILE) throw new IllegalStateException("State is not BUILDING");
        else if (deckIndex < 0 || deckIndex > 2) throw new IllegalArgumentException("Invalid deck index");
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

        if (getPlayerState(username) == PlayerState.LOOK_CARD_PILE) releaseCardPile(username);
        player.getShip().getHandComponent().ifPresent(c -> c.releaseComponent(board, player));

        setPlayerState(username, PlayerState.LOOK_CARD_PILE);
        board.getCardPilesWatchMap().put(username, deckIndex);

        int startingDeckIndex = deckIndex == 0 ? 0 : (deckIndex == 1 ? 3 : 6);
        int endingDeckIndex = startingDeckIndex + 3;
        EventContext.emit(new CardPileLookedEvent(username, deckIndex, board.getCardPile().subList(startingDeckIndex, endingDeckIndex)));
    }

    /**
     * Releases the card pile that the player is currently looking at,
     * returning them to the build state.
     *
     * @param username the username of the player releasing the card pile
     */
    public void releaseCardPile(String username) {
        setPlayerState(username, PlayerState.BUILD);
        board.getCardPilesWatchMap().remove(username);
        EventContext.emit(new CardPileReleasedEvent(username));
    }

    /**
     * Moves the hourglass forward, triggering the next phase of the game.
     *
     * @param username the username of the player moving the hourglass
     * @param callback a callback function to handle the list of events generated
     * @throws IllegalStateException if the player cannot move the hourglass in their current state
     */
    public void moveHourglass(String username, Consumer<List<Event>> callback) {
        if (
                getPlayerState(username) != PlayerState.BUILD &&
                        getPlayerState(username) != PlayerState.LOOK_CARD_PILE &&
                        (getPlayerState(username) != PlayerState.WAIT || board.getPlayerEntityByUsername(username).hasEndedInAdvance())
        ) throw new IllegalStateException("State is not BUILDING or WAIT");

        board.moveHourglass(username, this, callback);
        if (getPlayerState(username) == PlayerState.LOOK_CARD_PILE) releaseCardPile(username);

        EventContext.emit(new HourglassMovedEvent());
    }

    /**
     * Marks a player as ready to proceed to the next phase. If the player is
     * looking at a card pile, they will be automatically moved out of that state.
     * When all players are ready, the game advances to the next phase.
     *
     * @param username the username of the player setting themselves as ready
     * @throws IllegalStateException if the player is not in building or look card pile state
     */
    public void setReady(String username) {
        if (getPlayerState(username) == PlayerState.LOOK_CARD_PILE) releaseCardPile(username);
        else if (getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");

        PlayerData player = board.getPlayerEntityByUsername(username);
        player.setReady(board);
        setPlayerState(username, PlayerState.WAIT);

        if (arePlayersReady())
            moveStateAfterBuilding();
    }

    /**
     * Checks if all players are ready to move to the next phase.
     *
     * @return true if all players who haven't ended in advance are ready, false otherwise
     */
    private boolean arePlayersReady() {
        return board.getStartingDeck().stream()
                .filter(p -> !p.hasEndedInAdvance())
                .toList()
                .isEmpty();
    }

    /**
     * Transitions the game state after the building phase is complete.
     * Validates all ships, moves players to appropriate positions, shuffles cards,
     * and initiates the alien selection phase if all ships are valid.
     */
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

    /**
     * Allows a player to fix their ship by removing invalid components.
     * The player must be in the CHECK state to perform this action.
     *
     * @param username the username of the player checking their ship
     * @param toRemove list of component IDs to remove from the ship
     * @throws IllegalStateException if the player is not in checking state
     * @throws ComponentNotValidException if any component ID is invalid
     */
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

    /**
     * Checks if all ships are ready (valid and all players are in WAIT state).
     *
     * @return true if all ships are ready and players are waiting, false otherwise
     */
    private boolean areShipsReady() {
        List<PlayerData> totalPlayers;
        totalPlayers = Stream.concat(
                        board.getPlayers().stream().map(AbstractMap.SimpleEntry::getKey).filter(p -> !p.hasEndedInAdvance()),
                        board.getStartingDeck().stream().filter(p -> !p.hasEndedInAdvance()))
                .toList();
        for (PlayerData player : totalPlayers)
            if (playersState.get(player.getUsername()) != PlayerState.WAIT)
                return false;
        return true;
    }

    /**
     * Allows a player to choose alien crew members for their cabin components.
     * The player must be in the WAIT_ALIEN state to perform this action.
     * (only for standard mode)
     *
     * @param username the username of the player choosing aliens
     * @param aliensIds map of component IDs to alien types to assign
     * @throws IllegalStateException if the player is not in wait alien state
     * @throws CabinComponentNotValidException if any component is not a cabin
     */
    public void chooseAlien(String username, Map<Integer, AlienType> aliensIds) {
        if (getPlayerState(username) != PlayerState.WAIT_ALIEN) throw new IllegalStateException("State is not WAIT_ALIEN");

        for (int id : aliensIds.keySet()) { // Put alien in all cabins in aliensIds list
            if (!board.getMapIdComponents().get(id).matchesType(CabinComponent.class))
                throw new CabinComponentNotValidException("Component is not a cabin");
            board.getMapIdComponents().get(id).castTo(CabinComponent.class).setAlien(aliensIds.get(id), board.getPlayerEntityByUsername(username).getShip());
        }
        setPlayerState(username, PlayerState.WAIT);

        int playerIndex = board.getPlayersByPos().indexOf(board.getPlayerEntityByUsername(username)) + 1;
        manageChooseAlienPhase(playerIndex);
    }

    /**
     * Allows a player to choose which part of their ship to keep when their ship
     * has been split into multiple disconnected parts.
     *
     * @param username the username of the player choosing a ship part
     * @param partIndex the index of the ship part to keep
     * @throws IllegalStateException if the player is not in wait ship part state
     * @throws IndexOutOfBoundsException if the part index is invalid
     */
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

    /**
     * Allows a player to draw the next card from the card pile.
     * The card's effects are immediately applied and events are emitted.
     *
     * @param username the username of the player drawing the card
     * @throws IllegalStateException if the player is not in draw card state
     * @throws RuntimeException if the card pile is empty
     */
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

    /**
     * Allows a player to activate cannons using battery components to power them.
     * The player must be in the WAIT_CANNONS state to perform this action.
     *
     * @param username the username of the player activating cannons
     * @param batteriesIds list of battery component IDs to use for power
     * @param cannonComponentsIds list of cannon component IDs to activate
     * @throws IllegalStateException if the player is not in wait cannons state
     */
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

    /**
     * Allows a player to activate engines using battery components to power them.
     * The player must be in the WAIT_ENGINES state to perform this action.
     *
     * @param username the username of the player activating engines
     * @param batteriesIds list of battery component IDs to use for power
     * @param engineComponentsIds list of engine component IDs to activate
     * @throws IllegalStateException if the player is not in wait engines state
     */
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

    /**
     * Allows a player to activate their shield using a battery component for power.
     * The player must be in the WAIT_SHIELD state to perform this action.
     *
     * @param username the username of the player activating the shield
     * @param batteryId the ID of the battery component to use for power, or null for no power
     * @throws IllegalStateException if the player is not in wait shield state
     * @throws ComponentNotValidException if the battery ID is invalid
     */
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

    /**
     * Allows a player to update goods in their cargo holds and eventually remove batteries.
     * The player must be in either WAIT_GOODS or WAIT_REMOVE_GOODS state.
     *
     * @param username the username of the player updating goods
     * @param cargoHoldsIds map of cargo hold component IDs to lists of color types
     * @param batteriesIds list of battery component IDs to use for power
     * @throws IllegalStateException if the player is not in the correct state
     */
    public void updateGoods(String username, Map<Integer, List<ColorType>> cargoHoldsIds, List<Integer> batteriesIds) {
        if (getPlayerState(username) != PlayerState.WAIT_GOODS && getPlayerState(username) != PlayerState.WAIT_REMOVE_GOODS) throw new IllegalStateException("State is not WAIT_GOODS or WAIT_REMOVE_GOODS");

        List<BatteryComponent> batteries = batteriesIds.stream()
                .filter(id -> board.getMapIdComponents().get(id) != null && board.getMapIdComponents().get(id).matchesType(BatteryComponent.class))
                .map(id -> board.getMapIdComponents().get(id).castTo(BatteryComponent.class))
                .toList();

        Map<SpecialCargoHoldsComponent, List<ColorType>> cargoHolds = new HashMap<>();
        cargoHoldsIds.keySet().stream()
                .filter(id -> board.getMapIdComponents().get(id) != null && (board.getMapIdComponents().get(id).matchesType(SpecialCargoHoldsComponent.class) || board.getMapIdComponents().get(id).matchesType(CargoHoldsComponent.class)))
                .forEach(id -> cargoHolds.put(board.getMapIdComponents().get(id).castTo(SpecialCargoHoldsComponent.class), cargoHoldsIds.get(id)));

        Card card = board.getCardPile().get(board.getCardPilePos());
        Command command = new GoodCommand(this, board, username, cargoHolds, batteries);
        boolean finish = command.execute(card);

        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    /**
     * Allows a player to remove crew members from their cabin components.
     * The player must be in the WAIT_REMOVE_CREW state to perform this action.
     *
     * @param username the username of the player removing crew
     * @param cabinsIds list of cabin component IDs to remove crew from
     * @throws IllegalStateException if the player is not in wait remove crew state
     */
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

    /**
     * Allows a player to roll dice as required by certain game cards.
     * The player must be in the WAIT_ROLL_DICES state to perform this action.
     *
     * @param username the username of the player rolling dice
     * @throws IllegalStateException if the player is not in wait roll dices state
     */
    public void rollDices(String username) {
        if (getPlayerState(username) != PlayerState.WAIT_ROLL_DICES) throw new IllegalStateException("State is not WAIT_ROLL_DICES");

        Card card = board.getCardPile().get(board.getCardPilePos());
        RollDicesCommand command = new RollDicesCommand(this, board, username);

        boolean finish = command.execute(card);
        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    /**
     * Handles a boolean response from a player when a card requires a yes/no decision.
     * The player must be in the WAIT_BOOLEAN state to perform this action.
     *
     * @param username the username of the player providing the boolean response
     * @param value the boolean value chosen by the player
     * @throws IllegalStateException if the player is not in wait boolean state
     */
    public void getBoolean(String username, boolean value) {
        if (getPlayerState(username) != PlayerState.WAIT_BOOLEAN) throw new IllegalStateException("State is not WAIT_BOOLEAN");

        Card card = board.getCardPile().get(board.getCardPilePos());
        boolean finish = card.doCommandEffects(PlayerState.WAIT_BOOLEAN, value, this, board, username);
        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    /**
     * Handles an index/number response from a player when a card requires selecting
     * from multiple options.
     * The player must be in the WAIT_INDEX state to perform this action.
     *
     * @param username the username of the player providing the index response
     * @param value the index value chosen by the player, or null if no selection
     * @throws IllegalStateException if the player is not in wait index state
     */
    public void getIndex(String username, Integer value) {
        if (getPlayerState(username) != PlayerState.WAIT_INDEX) throw new IllegalStateException("State is not WAIT_INDEX");

        Card card = board.getCardPile().get(board.getCardPilePos());
        boolean finish = card.doCommandEffects(PlayerState.WAIT_INDEX, value, this, board, username);
        EventContext.emit(new CardUpdatedEvent(card));
        if (finish) { board.pickNewCard(this); }
    }

    /**
     * Allows a player to end their flight early, removing them from active gameplay
     * while keeping them in the game for final scoring. This action can only be
     * performed during certain game phases.
     *
     * @param username the username of the player ending their flight
     * @throws IllegalStateException if the game is in a phase where ending flight is not allowed,
     *                               or if the player has already ended their flight
     */
    public void endFlight(String username) {
        for (String p : usernames) {
            if (
                    getPlayerState(p) == PlayerState.BUILD ||
                            getPlayerState(p) == PlayerState.LOOK_CARD_PILE ||
                            getPlayerState(p) == PlayerState.CHECK ||
                            getPlayerState(p) == PlayerState.WAIT_ALIEN
            ) throw new IllegalStateException("You can't end flight in this phase");
        }

        PlayerData player = board.getPlayerEntityByUsername(username);
        if (player.hasEndedInAdvance())
            throw new IllegalStateException("You have already ended flight");

        player.endFlight();
        boolean cardIsStarted = board.getPlayersByPos().stream().noneMatch(p -> getPlayerState(p.getUsername()) == PlayerState.DRAW_CARD);
        if (!cardIsStarted) {
            board.moveToStartingDeck(player);
            setPlayerState(player.getUsername(), PlayerState.WAIT);
        }
    }

    /**
     * Handles a player leaving the game permanently. This method manages the
     * game state transitions and ensures continuity for remaining players.
     * Depending on the current game phase, different cleanup actions are performed.
     *
     * @param username the username of the player leaving the game
     */
    public void leaveGame(String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        player.setLeftGame(true);
        player.endFlight();

        player.getShip().getHandComponent().ifPresent(c -> c.releaseComponent(board, player));

        PlayerState state = getPlayerState(username);
        if (state == PlayerState.LOOK_CARD_PILE)
            releaseCardPile(username);
        else if (state == PlayerState.CHECK && areShipsReady())
            manageChooseAlienPhase(0);
        else if (state == PlayerState.WAIT_ALIEN) {
            int playerIndex = board.getPlayersByPos().indexOf(board.getPlayerEntityByUsername(username)) + 1;
            manageChooseAlienPhase(playerIndex);
        }
        else if (state == PlayerState.DRAW_CARD) {
            if (board.getPlayersByPos().size() > 1)
                setPlayerState(board.getPlayersByPos().get(1).getUsername(), PlayerState.DRAW_CARD);
            else {
                endGame();
                board.moveToStartingDeck(player);
                return;
            }
        }
        else if (state == PlayerState.END) {
            board.moveToStartingDeck(player);
            return;
        }

        board.moveToStartingDeck(player);
        boolean noneInDrawCard = getPlayersState().values().stream().noneMatch(s -> s == PlayerState.DRAW_CARD);
        if (noneInDrawCard && board.getCardPilePos() >= 0 && board.getCardPilePos() < board.getCardPile().size()) {
            Card card = board.getCardPile().get(board.getCardPilePos());
            boolean finish = card.doLeftGameEffects(state, this, board, username);
            EventContext.emit(new CardUpdatedEvent(card));

            if (finish) { board.pickNewCard(this); }
        }

        if (getPlayerState(username) != PlayerState.END)
            setPlayerState(username, PlayerState.WAIT);
    }

    /**
     * Allows a player who previously left the game to rejoin.
     * This method resets their left game status to false.
     *
     * @param username the username of the player rejoining the game
     */
    public void rejoinGame(String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        player.setLeftGame(false);
    }

    /**
     * Ends the current game by setting all players to END state and
     * calculating the final ranking based on their performance.
     * This method triggers the final scoring phase.
     */
    public void endGame() {
        for (String username : usernames)
            setPlayerState(username, PlayerState.END);
        board.calcRanking();
    }

    /**
     * Validates that a player is in the correct state for building actions.
     * If the player is looking at a card pile, they are automatically moved
     * out of that state. Otherwise, the player must be in BUILD state.
     *
     * @param username the username of the player to check
     * @throws IllegalStateException if the player is not in building state
     */
    private void checkBuildingState(String username) {
        if (getPlayerState(username) == PlayerState.LOOK_CARD_PILE) releaseCardPile(username);
        else if (getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
    }

    /**
     * Abstract method that must be implemented by subclasses to manage the
     * alien selection phase. This method handles the turn-based alien selection
     * process for each player.
     *
     * @param playerIndex the index of the current player in the alien selection phase
     */
    protected abstract void manageChooseAlienPhase(int playerIndex);

    /**
     * Returns the game board instance.
     *
     * @return the Board instance containing the current game state
     */
    public Board getBoard() {
        return board;
    }

}