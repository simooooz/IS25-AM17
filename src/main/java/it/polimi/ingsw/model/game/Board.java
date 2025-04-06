package it.polimi.ingsw.model.game;

import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.exceptions.PlayerNotFoundException;
import it.polimi.ingsw.model.factory.CardFactory;
import it.polimi.ingsw.model.factory.ComponentFactory;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.game.objects.Time;
import it.polimi.ingsw.model.player.PlayerData;


import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Board {

    private final List<SimpleEntry<PlayerData, Integer>> players;
    private final List<Component> commonComponents;
    private final List<PlayerData> startingDeck;
    private final List<Card> cardPile;
    private int cardPilePos;
    private final Time timeManagement;
    private final Map<Integer, Component> mapIdComponents;
    private final Map<ColorType, Integer> availableGoods;

    public Board(List<String> usernames) {
        this.startingDeck = new ArrayList<>();
        for (String username : usernames)
            this.startingDeck.add(new PlayerData(username));

        this.players = new ArrayList<>();

        ComponentFactory componentFactory = new ComponentFactory();
        this.commonComponents = new ArrayList<>(componentFactory.getComponents());
        this.mapIdComponents = new HashMap<>(componentFactory.getComponentsMap());
        this.cardPile = new ArrayList<>(new CardFactory().getCards());
        this.cardPilePos = 0;
        this.timeManagement = new Time();
        this.availableGoods = new HashMap<>();
    }

    public List<SimpleEntry<PlayerData, Integer>> getPlayers() {
        return players;
    }

    public void reconnectPlayer(SimpleEntry<PlayerData, Integer> player) {
        // todo
    }

    public void disconnectPlayer(String username) {
        boolean removed = this.players.removeIf(e -> e.getKey().getUsername().equals(username));
        if (!removed)
            this.startingDeck.removeIf(e -> e.getUsername().equals(username));
    }

    public List<PlayerData> getPlayersByPos() {
        return players.stream().map(SimpleEntry::getKey).collect(Collectors.toList());
    }

    public PlayerData getPlayerEntityByUsername(String username) {
        return Stream.concat(players.stream().map(SimpleEntry::getKey), startingDeck.stream())
            .filter(p -> p.getUsername().equals(username))
            .findFirst()
            .orElseThrow(PlayerNotFoundException::new);
    }

    public List<PlayerData> getStartingDeck() {
        return startingDeck;
    }

    public Map<Integer, Component> getMapIdComponents() {
        return mapIdComponents;
    }

    public List<Component> getCommonComponents() {
        return commonComponents;
    }

    public List<Card> getCardPile() {
        return cardPile;
    }

    public void setCardPilePos(int cardPilePos) {
        this.cardPilePos = cardPilePos;
    }

    public int getCardPilePos() {
        return cardPilePos;
    }

    public Time getTimeManagement() {
        return timeManagement;
    }

    public Map<ColorType, Integer> getAvailableGoods() {
        return availableGoods;
    }

    public void shuffleCards() {
        Collections.shuffle(cardPile);
    }

    public GameState drawCard(PlayerData player) {
        if (!player.equals(getPlayersByPos().getFirst())) throw new PlayerNotFoundException("Player is not the leader");

        if (cardPilePos < cardPile.size()) {
            Card card = cardPile.get(cardPilePos);
            boolean finish = card.startCard(this);
            if (finish) {
                cardPilePos++;
                if (cardPilePos == cardPile.size()) return GameState.END;
                else return GameState.DRAW_CARD;
            }
            return GameState.PLAY_CARD;
        }
        else throw new RuntimeException("Card index out of bound");
    }

    public void movePlayer(PlayerData playerData, int position) {
        if (position == 0) return;
        SimpleEntry<PlayerData, Integer> entry = players.stream()
                .filter(e -> e.getKey().equals(playerData))
                .findFirst()
                .orElseThrow(PlayerNotFoundException::new);

        for (int d = 0; d < Math.abs(position); d++) {
            int currentPosition = entry.getValue();
            int nextPosition = (position > 0) ? currentPosition + 1 : currentPosition - 1;
            boolean moved = false; // check if we've moved

            while (!moved) {
                boolean positionOccupied = false; // check if the position in occupied

                // iterate on the player to check if the player are in the previous position
                for (SimpleEntry<PlayerData, Integer> otherEntry : players) {
                    if (!otherEntry.equals(entry) && otherEntry.getValue() == nextPosition) {
                        positionOccupied = true;
                        break;
                    }
                }

                if (!positionOccupied) {
                    entry.setValue(nextPosition);
                    moved = true;
                } else
                    nextPosition = (position > 0) ? nextPosition + 1 : nextPosition - 1;
            }
        }
    }

    public void moveToStartingDeck(PlayerData player) {
        players.stream()
                .filter(el -> el.getKey().equals(player))
                .findFirst()
                .ifPresent(players::remove);
        startingDeck.add(player);
    }

    public void moveToBoard(PlayerData player) {
        startingDeck.stream()
                .filter(p -> p.equals(player))
                .findFirst()
                .orElseThrow(PlayerNotFoundException::new);

        int pos = players.isEmpty() ? 6 : (players.size() == 1 ? 3 : (players.size() == 2 ? 1 : 0));
        players.add(new SimpleEntry<>(player, pos));

        players.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        startingDeck.remove(player);
    }

    public List<PlayerData> getRanking() {
        List<PlayerData> players = Stream.concat(
                this.getPlayersByPos().stream(),
                this.getStartingDeck().stream()
        ).toList();

        int[] credits = {8, 6, 4, 2};       // 1°, 2°, 3°, 4°
        Map<ColorType, Integer> CREDIT_MULTIPLIERS = Map.of(
                ColorType.RED, 4,
                ColorType.YELLOW, 3,
                ColorType.GREEN, 2,
                ColorType.BLUE, 1
        );

        IntStream.range(0, players.size())
                .forEach(i -> {
                    PlayerData player = players.get(i);
                    // reward for the order of arrival (only not dropped ou players)
                    if (this.getPlayersByPos().contains(player))
                        player.setCredits(player.getCredits() + credits[i]);
                    // handling sale of goods
                    player.getShip().getGoods().keySet().forEach(good -> {
                        int c = CREDIT_MULTIPLIERS.get(good) * player.getShip().getGoods().get(good);
                        if (this.getStartingDeck().contains(player))
                            c = c / 2;
                        player.setCredits(player.getCredits() + c);
                    });
                    // component leaks
                    player.setCredits(player.getCredits() - player.getShip().getDiscards().size());
                });

        // reward for the most beautiful ship
        int[] exposedConnectors = players.stream()
                .mapToInt(p -> p.getShip().countExposedConnectors())
                .toArray();
        players.stream()
                .filter(p -> p.getShip().countExposedConnectors() == Arrays.stream(exposedConnectors).min().getAsInt())
                .forEach(p -> p.setCredits(p.getCredits() + 4));

        return players.stream()
                .sorted(Comparator.comparingInt(PlayerData::getCredits))
                .toList();
    }

}
