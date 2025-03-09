package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;

public class SlaversCard extends Card {
    private final int crew;
    private final int credits;
    private final int days;
    private final int firePower;

    public SlaversCard(int level, boolean isLearner, int crew, int credits, int days, int firePower) {
        super(level, isLearner);
        this.crew = crew;
        this.credits = credits;
        this.days = days;
        this.firePower = firePower;
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

    public int getFirePower() {
        return firePower;
    }

    @Override
    public void resolve(Board board) {
        List<AbstractMap.SimpleEntry<PlayerData, Integer>> players = board.getPlayers();
        players.sort(Comparator.comparing(AbstractMap.SimpleEntry::getValue, Comparator.reverseOrder()));

        for (AbstractMap.SimpleEntry<PlayerData, Integer> entry : players) {
            PlayerData player = entry.getKey();

            List<CannonComponent> cannons = player.getShip().getCannons();

            int power = 0;
            for (CannonComponent c : cannons) {
                if (!c.getIsDouble()) {
                    power += 1;
                } else {
                    if (player.getShip().getBatteries() > 0) {
                        // ask the user if he wants to use it to activate the double cannon
                        // this part will be implemented with the view
                        // for now I add double power every time
                        power += 2;
                        player.getShip().setBatteries(player.getShip().getBatteries() - 1);
                    }
                }
            }

            if (power >= getFirePower()) {
                // ask the user if he wants to be rewarded or stay in his position
                // thi part will be implemented with the view
                // for now I suppose he chooses every time to be rewarded
                player.setCredits(player.getCredits() + getCredits());

                for (int d = 0; d < getDays(); d++) {
                    int currentPosition = entry.getValue();
                    int nextPosition = currentPosition - 1;
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
                            nextPosition--;
                        }
                    }
                }

                return;
            } else {
                int remainingCrew = player.getShip().getCrew() - getCrew();
                player.getShip().setCrew(Math.max(remainingCrew, 0));
            }
        }

    }
}
