package it.polimi.ingsw.model.game;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.exceptions.PlayerNotFoundException;
import it.polimi.ingsw.model.factory.CardFactory;
import it.polimi.ingsw.model.factory.ComponentFactory;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.game.objects.Time;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.view.TUI.Chroma;


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
    private final boolean learnerMode;

    public Board(List<String> usernames, boolean learnerMode) {
        this.learnerMode = learnerMode;
        this.startingDeck = new ArrayList<>();
        for (String username : usernames)
            this.startingDeck.add(new PlayerData(username, learnerMode));

        this.players = new ArrayList<>();
        this.timeManagement = learnerMode ? null : new Time();

        ComponentFactory componentFactory = new ComponentFactory();
        this.commonComponents = new ArrayList<>(componentFactory.getComponents());
        this.mapIdComponents = new HashMap<>(componentFactory.getComponentsMap());
        this.cardPile = new ArrayList<>(new CardFactory(learnerMode).getCards());
        this.cardPilePos = 0;
        this.availableGoods = new HashMap<>();
    }

    public List<SimpleEntry<PlayerData, Integer>> getPlayers() {
        return players;
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

    public int getCardPilePos() {
        return cardPilePos;
    }

    public Time getTimeManagement() {
        return timeManagement;
    }

    public Map<ColorType, Integer> getAvailableGoods() {
        return availableGoods;
    }

    public void shuffleCards(boolean learnerMode) {
        do {
            Collections.shuffle(cardPile);
        } while (cardPile.getFirst().getLevel() == 2 && !learnerMode);
    }

    public void pickNewCard(ModelFacade model) {
        cardPilePos++;
        if (cardPilePos == cardPile.size()) // All cards are resolved
            model.endGame();
        else { // Change card
            for (PlayerData p : getPlayersByPos())
                model.setPlayerState(p.getUsername(), PlayerState.WAIT);
            model.setPlayerState(getPlayersByPos().getFirst().getUsername(), PlayerState.DRAW_CARD);
        }
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

        players.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

    }

    public void moveToStartingDeck(PlayerData player) {
        players.stream()
                .filter(el -> el.getKey().equals(player))
                .findFirst()
                .ifPresent(players::remove);
        startingDeck.add(player);
    }

    public void moveToBoard(PlayerData player, boolean learnerMode) {
        startingDeck.stream()
                .filter(p -> p.equals(player))
                .findFirst()
                .orElseThrow(PlayerNotFoundException::new);
        int pos;
        if(!learnerMode)
            pos = players.isEmpty() ? 6 : (players.size() == 1 ? 3 : (players.size() == 2 ? 1 : 0));
        else
            pos = players.isEmpty() ? 4 : (players.size() == 1 ? 2 : (players.size() == 2 ? 1 : 0));
        players.add(new SimpleEntry<>(player, pos));

        players.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        startingDeck.remove(player);
    }

    public List<PlayerData> getRanking() {
        List<PlayerData> players = Stream.concat(
                this.getPlayersByPos().stream(),
                this.getStartingDeck().stream()
        ).toList();

        int[] credits = learnerMode ? new int[]{4, 3, 2, 1} : new int[]{8, 6, 4, 2} ;


        Map<ColorType, Integer> CREDIT_MULTIPLIERS = Map.of(
                ColorType.RED, 4,
                ColorType.YELLOW, 3,
                ColorType.GREEN, 2,
                ColorType.BLUE, 1
        );

        IntStream.range(0, players.size())
                .forEach(i -> {
                    PlayerData player = players.get(i);
                    // reward for the order of arrival (only not dropped out players)
                    if (this.getPlayersByPos().contains(player))
                        player.setCredits(player.getCredits() + credits[i]);

                    // handling sale of goods - calculating total goods value first
                    int totalGoodsCredits = 0;
                    for (ColorType good : player.getShip().getGoods().keySet()) {
                        totalGoodsCredits += CREDIT_MULTIPLIERS.get(good) * player.getShip().getGoods().get(good);
                    }

                    // apply starting deck penalty if applicable (divide total by 2 and round up)
                    if (this.getStartingDeck().contains(player)) {
                        totalGoodsCredits = (int) Math.ceil(totalGoodsCredits / 2.0);
                    }

                    // add total goods credits to player's credits
                    player.setCredits(player.getCredits() + totalGoodsCredits);
                    // component leaks
                    player.setCredits(player.getCredits() - player.getShip().getDiscards().size() - player.getShip().getReserves().size());
                });

        // reward for the most beautiful ship
        int[] exposedConnectors = players.stream()
                .mapToInt(p -> p.getShip().countExposedConnectors())
                .toArray();
        players.stream()
                .filter(p -> p.getShip().countExposedConnectors() == Arrays.stream(exposedConnectors).min().orElseThrow())
                .forEach(p -> p.setCredits(p.getCredits() + (learnerMode ? 2 : 4)));

        return players.stream()
                .sorted(Comparator.comparingInt(PlayerData::getCredits).reversed())
                .toList();
    }

    public String toString(String username, PlayerState state) {
        StringBuilder sb = new StringBuilder();

        switch (state) {
            case BUILD -> {
                sb.append(Constants.displayComponents(commonComponents, 10));

                if (!learnerMode) {
                    sb.append("\nHourglass position: ").append(timeManagement.getHourglassPos());
                    sb.append("\nTime left: ").append(timeManagement.getTimeLeft()).append("\n");
                }

                for (PlayerData player : startingDeck)
                    sb.append("- ").append(player.getUsername()).append(Chroma.color(" not ready\n", Chroma.RED));
                for (SimpleEntry<PlayerData, Integer> entry : players)
                    sb.append("- ").append(entry.getKey().getUsername()).append(Chroma.color(" READY\n", Chroma.GREEN));
            }

            case LOOK_CARD_PILE -> {
                int deckIndex = PlayerState.LOOK_CARD_PILE.getDeckIndex().get(username);
                int startingDeckIndex = deckIndex == 0 ? 0 : (deckIndex == 1 ? 3 : 6);
                int endingDeckIndex = startingDeckIndex + 3;
                cardPile.subList(startingDeckIndex, endingDeckIndex).forEach(card -> sb.append(card).append("\n") );
                // todo CHANGE with displayCards

                if (!learnerMode) {
                    sb.append("\nHourglass position: ").append(timeManagement.getHourglassPos());
                    sb.append("\nTime left: ").append(timeManagement.getTimeLeft()).append("\n");
                }

                for (PlayerData player : startingDeck)
                    sb.append("- ").append(player.getUsername()).append(Chroma.color(" not ready\n", Chroma.RED));
                for (SimpleEntry<PlayerData, Integer> entry : players)
                    sb.append("- ").append(entry.getKey().getUsername()).append(Chroma.color(" READY\n", Chroma.GREEN));
            }

            case DRAW_CARD, WAIT, WAIT_CANNONS, WAIT_ENGINES, WAIT_GOODS, WAIT_REMOVE_GOODS, WAIT_ROLL_DICES, WAIT_REMOVE_CREW, WAIT_SHIELD, WAIT_BOOLEAN, WAIT_INDEX, DONE -> {
                sb.append(Chroma.color("Cards resolved so far " + getCardPilePos() + "/" + getCardPile().size(), Chroma.GREY_BOLD)).append("\n");

                sb.append("Players in game:\n");
                for (SimpleEntry<PlayerData, Integer> entry : players)
                    sb.append("  ").append(entry.getKey().getUsername()).append(" | ").append("flight days: ").append(entry.getValue()).append(" | ").append("$").append(entry.getKey().getCredits()).append("\n");

                if (!startingDeck.isEmpty()) {
                    sb.append("Starting deck:\n");
                    for (PlayerData player : startingDeck)
                        sb.append("  ").append(player.getUsername()).append(" | ").append("$").append(player.getCredits()).append("\n");
                }
            }

            case END -> {
                sb.append("\nRanking:\n");
                for (PlayerData player : this.getRanking())
                    sb.append("-  ").append(player.getUsername()).append(" $").append(player.getCredits()).append("\n");
            }
        }
        return sb.toString();
    }

}
