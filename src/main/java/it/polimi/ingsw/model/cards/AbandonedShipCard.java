package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.AbstractMap;
import java.util.List;

public class AbandonedShipCard extends Card {
    private final int crew;
    private final int credits;
    private final int days;

    public AbandonedShipCard(int level, boolean isLearner, int crew, int credits, int days) {
        super(level, isLearner);
        this.crew = crew;
        this.credits = credits;
        this.days = days;
    }

    public int getCrew() {
        return crew;
    }

    public int getCredits() {
        return credits;
    }

    public int getDays() {
        return days;
    }

    @Override
    public void resolve(Board board) {
        List<AbstractMap.SimpleEntry<PlayerData, Integer>> players = board.getPlayers();

        // iterate on the list of player -> for now I've not implemented if a player does not want to
        // play this card, it's only to iterate on the list of player
        for (AbstractMap.SimpleEntry<PlayerData, Integer> entry : players) {
            PlayerData player = entry.getKey();

            int crew = player.getShip().getCrew();
            int credits = player.getCredits();

            // check if the player can really play the card
            if (crew > getCrew()) {
                player.getShip().setCrew(crew - getCrew());
                player.setCredits(credits + getCredits());

                // iterate on the days to update player position
                for (int d = 0; d < getDays(); d++) {
                    int currentPosition = entry.getValue();
                    int nextPosition = currentPosition - 1;
                    boolean moved = false; // check if we've moved

                    while (nextPosition >= 0 && !moved) {
                        boolean positionOccupied = false; // check if the position in occupied

                        // iterate on the player to check if the player are in the previus position
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
                            nextPosition--;
                        }
                    }
                }

            }
        }
    }
}
