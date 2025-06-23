package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.client.model.components.ClientBatteryComponent;
import it.polimi.ingsw.client.model.components.ClientCabinComponent;
import it.polimi.ingsw.client.model.components.ClientCargoHoldsComponent;
import it.polimi.ingsw.client.model.components.ClientComponent;
import it.polimi.ingsw.client.model.events.CardRevealedEvent;
import it.polimi.ingsw.client.model.events.CardUpdatedEvent;
import it.polimi.ingsw.client.model.game.ClientBoard;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.player.ClientShip;
import it.polimi.ingsw.common.dto.ModelDTO;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.events.game.*;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

/**
 * Main class for the client-side model.
 * It holds a read-only representation of the game state and notifies the View of any changes.
 * This class is the single point of access for the UI to the game data.
 * DIFFERENCES FROM SERVER MODEL:
 * - No business logic: It doesn't enforce game rules.
 * - Observable: It notifies observers (the View) when its state changes.
 * - Update methods: State is modified only through `update...` methods,
 *   which are called in response to server events.
 * - Read-only query methods: Provides getters for the UI.
 * - Lightweight: Uses simplified data classes (ClientPlayer, ClientShip, etc.).
 */
public abstract class ClientGameModel {

    protected ClientBoard board;

    protected Map<String, PlayerState> playersState;

    public ClientGameModel() {
        this.playersState = new HashMap<>();
    }

    public ClientGameModel(ModelDTO dto) {
        this.playersState = dto.playersState;
    }

    public PlayerState getPlayerState(String username) {
        return playersState.get(username);
    }
    
    // --- UPDATE METHODS (called by the Network layer) ---

    public void matchStarted() {
        board.startMatch(this);

        ClientEventBus.getInstance().publish(new MatchStartedEvent());
    }

    public void componentPicked(String username, int componentId) {
        ClientShip ship = board.getPlayerEntityByUsername(username).getShip();
        ClientComponent component = board.getMapIdComponents().get(componentId);

        board.getCommonComponents().remove(component);
        component.setShown(true);
        ship.setComponentInHand(component);

        ClientEventBus.getInstance().publish(new ComponentPickedEvent(username, componentId));
    }

    public void componentReleased(String username, int componentId) {
        ClientShip ship = board.getPlayerEntityByUsername(username).getShip();
        ClientComponent component = board.getMapIdComponents().get(componentId);

        if (ship.getComponentInHand().isPresent() && ship.getComponentInHand().get().equals(component)) // Component to release is in hand
            ship.setComponentInHand(null);
        else // Component to release is in reserves
            ship.getDashboard()[component.getY()][component.getX()] = Optional.empty();

        board.getCommonComponents().add(component);

        ClientEventBus.getInstance().publish(new ComponentReleasedEvent(username, componentId));
    }

    public void componentReserved(String username, int componentId) {
        ClientShip ship = board.getPlayerEntityByUsername(username).getShip();
        ClientComponent component = board.getMapIdComponents().get(componentId);

        if (ship.getComponentInHand().isPresent() && ship.getComponentInHand().get().equals(component)) // Component to reserve is in hand
            ship.setComponentInHand(null);
        else if (ship.getDashboard(component.getY(), component.getX()).isPresent() && ship.getDashboard(component.getY(), component.getX()).get().equals(component)) // Component to reserve is in dashboard
            ship.getDashboard()[component.getY()][component.getX()] = Optional.empty();

        ship.getReserves().add(component);

        ClientEventBus.getInstance().publish(new ComponentReservedEvent(username, componentId));
    }

    public void componentInserted(String username, int componentId, int row, int col) {
        ClientPlayer player = board.getPlayerEntityByUsername(username);
        ClientComponent component = board.getMapIdComponents().get(componentId);
        component.insertComponent(player, row, col, 0, true);

        ClientEventBus.getInstance().publish(new ComponentInsertedEvent(username, componentId, row, col));
    }

    public void componentMoved(String username, int componentId, int row, int col) {
        ClientShip ship = board.getPlayerEntityByUsername(username).getShip();
        ClientComponent component = board.getMapIdComponents().get(componentId);

        ship.getDashboard()[component.getY()][component.getX()] = Optional.empty();
        component.setX(col);
        component.setY(row);
        ship.getDashboard()[row][col] = Optional.of(component);

        ClientEventBus.getInstance().publish(new ComponentMovedEvent(username, componentId, row, col));
    }

    public void componentRotated(int componentId, int num) {
        ClientComponent component = board.getMapIdComponents().get(componentId);
        for (int i=0; i<(num % 4); i++)
            component.rotateComponent();

        ClientEventBus.getInstance().publish(new ComponentRotatedEvent(componentId, num));
    }

    public void componentDestroyed(String username, int componentId) {
        ClientShip ship = board.getPlayerEntityByUsername(username).getShip();
        ClientComponent component = board.getMapIdComponents().get(componentId);

        ship.getDiscards().add(component);
        ship.getDashboard()[component.getY()][component.getX()] = Optional.empty();

        ClientEventBus.getInstance().publish(new ComponentDestroyedEvent(username, componentId));
    }

    public void cardPileReleased(String username) {
        board.getLookedCards().clear();
        ClientEventBus.getInstance().publish(new CardPileReleasedEvent(username));
    }

    public void hourglassMoved() {
        board.moveHourglass();

        ClientEventBus.getInstance().publish(new HourglassMovedEvent());
    }

    public void playersPositionUpdated(List<String> starting, List<SimpleEntry<String, Integer>> players) {
        List<ClientPlayer> newStartingDeck = starting.stream().map(s -> board.getPlayerEntityByUsername(s)).toList();
        List<SimpleEntry<ClientPlayer, Integer>> newPlayers = players.stream().map(e -> new SimpleEntry<>(board.getPlayerEntityByUsername(e.getKey()), e.getValue())).toList();
        board.setStartingDeck(newStartingDeck);
        board.setPlayers(newPlayers);

        ClientEventBus.getInstance().publish(new PlayersPositionUpdatedEvent(starting, players));
    }

    public void shipBroken(String username, List<List<Integer>> parts) {
        ClientShip ship = board.getPlayerEntityByUsername(username).getShip();

        List<List<ClientComponent>> newParts = new ArrayList<>();
        for (List<Integer> group : parts)
            newParts.add(group.stream().map(c -> board.getMapIdComponents().get(c)).toList());

        ship.getBrokenParts().clear();
        ship.getBrokenParts().addAll(newParts);
        ClientEventBus.getInstance().publish(new ShipBrokenEven(username, parts));
    }

    public void cardRevealed(ClientCard card) {
        board.getCardPile().add(card);

        ClientEventBus.getInstance().publish(new CardRevealedEvent(card));
    }

    public void batteriesUpdated(int id, int batteries) {
        ((ClientBatteryComponent) board.getMapIdComponents().get(id)).setBatteries(batteries);

        ClientEventBus.getInstance().publish(new BatteriesUpdatedEvent(id, batteries));
    }

    public void goodsUpdated(int id, List<ColorType> goods) {
        ClientCargoHoldsComponent component = (ClientCargoHoldsComponent) board.getMapIdComponents().get(id);
        component.getGoods().clear();
        component.getGoods().addAll(goods);

        ClientEventBus.getInstance().publish(new GoodsUpdatedEvent(id, goods));
    }

    public void crewUpdated(int id, int humans, AlienType alien) {
        ClientCabinComponent component = (ClientCabinComponent) board.getMapIdComponents().get(id);
        component.setAlien(alien);
        component.setHumans(humans);

        ClientEventBus.getInstance().publish(new CrewUpdatedEvent(id, humans, alien));
    }

    public void cardUpdated(ClientCard card) {
        board.getCardPile().removeLast();
        board.getCardPile().add(card);

        ClientEventBus.getInstance().publish(new CardUpdatedEvent(card));
    }

    public void creditsUpdated(String username, Integer credits) {
        ClientPlayer player = board.getPlayerEntityByUsername(username);
        player.setCredits(credits);

        ClientEventBus.getInstance().publish(new CreditsUpdatedEvent(username, credits));
    }

    public void playersStateUpdated(Map<String, PlayerState> newStates) {
        this.playersState = new HashMap<>(newStates);

        ClientEventBus.getInstance().publish(new PlayersStateUpdatedEvent(newStates));
    }

    public void flightEnded(String username) {
        board.getPlayerEntityByUsername(username).setEndedInAdvance(true);

        ClientEventBus.getInstance().publish(new FlightEndedEvent(username));
    }

    public void playerLeft(String username) {
        ClientPlayer player = board.getPlayerEntityByUsername(username);
        player.setEndedInAdvance(true);
        player.setLeftMatch(true);
    }

    public void playerRejoined(String username) {
        ClientPlayer player = board.getPlayerEntityByUsername(username);
        player.setLeftMatch(false);
    }

    public void rotateComponent(String username, int componentId, int num) {
        ClientPlayer player = board.getPlayerEntityByUsername(username);
        ClientComponent component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");
        if (num % 4 == 0) return;

        component.rotateComponent(player, num);
        ClientEventBus.getInstance().publish(new ComponentRotatedEvent(componentId, num));
    }

    public void insertComponent(String username, int componentId, int row, int col, int rotations) {
        ClientPlayer player = board.getPlayerEntityByUsername(username);
        ClientComponent component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");

        boolean weld = false;
        ClientShip ship = player.getShip();
        if (!ship.validPositions(row, col) || ship.getDashboard(row, col).isPresent())
            throw new ComponentNotValidException("The position where to insert it is not valid"); // Check if new position is valid
        else if (!component.isShown())
            throw new ComponentNotValidException("Component is hidden");

        if (ship.getComponentInHand().isPresent() && ship.getComponentInHand().get().equals(component)) // Component is in hand
            ship.setComponentInHand(null);
        else if (ship.getReserves().contains(component)) { // Component is in reserves, weld it
            ship.getReserves().remove(component);
            weld = true;
        }
        else
            throw new ComponentNotValidException("Component to insert isn't in hand or in reserves");

        component.insertComponent(player, row, col, rotations, weld);
        ClientEventBus.getInstance().publish(new ComponentInsertedEvent(username, componentId, row, col));
    }

    public void moveComponent(String username, int componentId, int row, int col, int rotations) {
        ClientPlayer player = board.getPlayerEntityByUsername(username);
        ClientComponent component = board.getMapIdComponents().get(componentId);
        if (component == null) throw new ComponentNotValidException("Invalid component id");

        ClientShip ship = player.getShip();
        if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
            throw new ComponentNotValidException("Component isn't in dashboard");
        else if (component.isInserted())
            throw new ComponentNotValidException("Component already welded");
        else if (!ship.validPositions(row, col) || ship.getDashboard(row, col).isPresent())
            throw new ComponentNotValidException("New position isn't valid or is already occupied"); // Check if new position is valid

        component.moveComponent(player, row, col, rotations);
        ClientEventBus.getInstance().publish(new ComponentMovedEvent(username, componentId, row, col));
    }

    public ClientBoard getBoard() {
        return board;
    }

    public abstract void cardPileLooked(String username, int deckIndex, List<ClientCard> cards);

}