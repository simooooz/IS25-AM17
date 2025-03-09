package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.game.objects.Time;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.*;
import java.util.stream.Collectors;

public class Board {

    private final List<AbstractMap.SimpleEntry<PlayerData, Integer>> players;
    private final List<PlayerData> startingDeck;
    private final List<Card> cardPile;
    private int cardPilePos;
    private final Time timeManagment;
    private final Map<ColorType, Integer> availableGoods;

    public Board(List<AbstractMap.SimpleEntry<PlayerData, Integer>> players) {
        this.players = players;
        this.startingDeck = new ArrayList<>();
        this.cardPile = new ArrayList<>();
        this.cardPilePos = 0;
        this.timeManagment = new Time();
        this.availableGoods = new HashMap<>();

        List<PlayerData> playersDataList = players.stream().map(el -> el.getKey()).collect(Collectors.toList());
        startingDeck.addAll(playersDataList);
    }

    public List<AbstractMap.SimpleEntry<PlayerData, Integer>> getPlayers() {
        players.sort(Comparator.comparing(AbstractMap.SimpleEntry::getValue, Comparator.reverseOrder()));
        return players;
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
        else throw new Exception(); // Eccezione carte finite
    }

    public void movePlayer(PlayerData playerData, int position) {
        if (position == 0) return;
        players.forEach(entry -> {
            if (entry.getKey().equals(playerData)) {
                for (int d = 0; d < Math.abs(position); d++) {
                    int currentPosition = entry.getValue();
                    int nextPosition = (position > 0) ?  currentPosition+1 : currentPosition-1;
                    boolean moved = false; // check if we've moved

                    while (nextPosition >= 0 && !moved) {
                        boolean positionOccupied = false; // check if the position in occupied

                        // iterate on the player to check if the player are in the previous position
                        for (AbstractMap.SimpleEntry<PlayerData, Integer> otherEntry : players) {
                            if (!otherEntry.equals(entry) && otherEntry.getValue() == nextPosition) {
                                positionOccupied = true;
                            }
                        }
                        // now we know that the position is free
                        if (!positionOccupied) {
                            entry.setValue(nextPosition);
                            moved = true;
                        } else {
                            nextPosition = (position > 0) ? nextPosition+1 : nextPosition-1;
                        }
                    }
                }
            }
        });
        // Eccezione giocatore non trovato
    }

    public void moveToStartingDeck(PlayerData player) {
        players.forEach(entry -> {
            if (entry.getKey().equals(player)) {
                startingDeck.add(player);
                players.remove(entry);
                return;
            }
        });
        // Eccezione giocatore non trovato
    }

    public void moveToBoard(PlayerData player) {
        startingDeck.forEach(p -> {
            if (p.equals(player)) {
                players.add(new AbstractMap.SimpleEntry<>(player, 0));
                startingDeck.remove(p);
                return;
            }
        });
        // Eccezione giocatore non trovato
    }

}