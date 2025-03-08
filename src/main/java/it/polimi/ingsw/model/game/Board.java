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
                // Set new position
                entry.setValue(entry.getValue() + position);

                // Check if there is another player in the new position and eventually update it
                Optional<AbstractMap.SimpleEntry<PlayerData, Integer>> conflict;
                do {
                    conflict = players.stream().filter(player2 -> !player2.getKey().equals(playerData) && Objects.equals(player2.getValue(), entry.getValue())).findFirst();
                    if (conflict.isPresent() && position > 0) entry.setValue(entry.getValue() + 1);
                    else if (conflict.isPresent()) entry.setValue(entry.getValue() - 1);
                } while(conflict.isPresent());

                return;
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