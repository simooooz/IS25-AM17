package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.game.objects.Time;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;

public class Board {

    private final List<SimpleEntry<PlayerData, Integer>> players;
    private final List<PlayerData> startingDeck;
    private final List<Card> cardPile;
    private int cardPilePos;
    private final Time timeManagment;
    private final Map<ColorType, Integer> availableGoods;

    public Board(List<SimpleEntry<PlayerData, Integer>> players) {
        this.players = players;
        this.startingDeck = new ArrayList<>(getPlayersByPos());
        this.cardPile = new ArrayList<>();
        this.cardPilePos = 0;
        this.timeManagment = new Time();
        this.availableGoods = new HashMap<>();
    }

    public List<SimpleEntry<PlayerData, Integer>> getPlayers() {
        return players;
    }

    public List<PlayerData> getPlayersByPos() {
        return players.stream().map(SimpleEntry::getKey).collect(Collectors.toList());
    }

    public List<PlayerData> getStartingDeck() {
        return startingDeck;
    }

    public List<Card> getCardPile() {
        return cardPile;
    }

    public Time getTimeManagment() {
        return timeManagment;
    }

    public Map<ColorType, Integer> getAvailableGoods() {
        return availableGoods;
    }

    public void shuffleCards() {
        Collections.shuffle(cardPile);
    }

    public Card drawCard() throws Exception {
        if (cardPilePos < cardPile.size())
            return cardPile.get(cardPilePos++);
        else throw new Exception(); // No more cards
    }

    public void movePlayer(PlayerData playerData, int position) {
        if (position == 0) return;
        SimpleEntry<PlayerData, Integer> entry = players.stream()
                .filter(e -> e.getKey().equals(playerData))
                .findFirst()
                .orElseThrow();

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

    public void moveToStartingDeck(PlayerData player) throws Exception {
        players.stream()
                .filter(el -> el.getKey().equals(player))
                .findFirst()
                .ifPresent(players::remove);
        startingDeck.add(player);
    }

    public void moveToBoard(PlayerData player) throws Exception {
        startingDeck.stream()
                .filter(p -> p.equals(player))
                .findFirst()
                .orElseThrow(Exception::new);

        players.add(new SimpleEntry<>(player, 0));
        players.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        startingDeck.remove(player);
    }

}