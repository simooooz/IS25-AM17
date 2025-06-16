package it.polimi.ingsw.client.model.game;

import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.client.model.components.ClientComponent;
import it.polimi.ingsw.client.model.factory.ClientComponentFactory;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.exceptions.PlayerNotFoundException;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ClientBoard {

    private final Map<Integer, ClientComponent> mapIdComponents;
    protected final List<ClientComponent> commonComponents;
    protected final ClientComponentFactory componentFactory;

    protected List<SimpleEntry<ClientPlayer, Integer>> players;
    protected List<ClientPlayer> startingDeck;

    protected final List<ClientCard> cardPile;

    public ClientBoard() {
        this.componentFactory = new ClientComponentFactory();
        this.commonComponents = new ArrayList<>(componentFactory.getComponents());
        this.mapIdComponents = new HashMap<>(componentFactory.getComponentsMap());

        this.startingDeck = new ArrayList<>();
        this.players = new ArrayList<>();

        this.cardPile = new ArrayList<>();
    }

    public Map<Integer, ClientComponent> getMapIdComponents() {
        return mapIdComponents;
    }

    public List<ClientComponent> getCommonComponents() {
        return commonComponents;
    }

    public List<ClientPlayer> getPlayersByPos() {
        return players.stream().map(SimpleEntry::getKey).collect(Collectors.toList());
    }

    public void setPlayers(List<SimpleEntry<ClientPlayer, Integer>> players) {
        this.players = players;
    }

    public List<SimpleEntry<ClientPlayer, Integer>> getPlayers() {
        return players;
    }

    public void setStartingDeck(List<ClientPlayer> startingDeck) {
        this.startingDeck = startingDeck;
    }

    public List<ClientPlayer> getStartingDeck() {
        return startingDeck;
    }

    public List<ClientCard> getCardPile() {
        return cardPile;
    }

    public List<ClientPlayer> getAllPlayers() {
        return Stream.concat(players.stream().map(SimpleEntry::getKey), startingDeck.stream()).toList();
    }

    public ClientPlayer getPlayerEntityByUsername(String username) {
        return getAllPlayers().stream()
                .filter(p -> p.getUsername().equals(username))
                .findFirst()
                .orElseThrow(PlayerNotFoundException::new);
    }

    public abstract void startMatch(ClientGameModel model);

    public abstract List<ClientCard> getLookedCards();

    public abstract void moveHourglass();

    public abstract String toString(String username, PlayerState state);

}
