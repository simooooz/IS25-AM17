package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
    public void resolve(Board board) throws Exception {
        List<AbstractMap.SimpleEntry<PlayerData, Integer>> players = board.getPlayers();
        players.sort(Comparator.comparing(AbstractMap.SimpleEntry::getValue, Comparator.reverseOrder()));

        for (AbstractMap.SimpleEntry<PlayerData, Integer> entry : players) {
            PlayerData player = entry.getKey();

            List<CannonComponent> cannons = player.getShip().getComponentByType(CannonComponent.class);

            int power = 0;
            for (CannonComponent c : cannons) {
                if (!c.getIsDouble()) {
                    power += 1;
                } else {
                    // ask the user if he wants to use it to activate the double cannon
                    // this part will be implemented with the view
                    // for now I add double power every time
                    power += 2;
                    Optional<BatteryComponent> chosenComponentOpt = Optional.empty(); // View
                    BatteryComponent chosenComponent = chosenComponentOpt.orElseThrow();
                    if (chosenComponent.getBatteries() > 0) {
                        chosenComponent.useBattery(player.getShip());
                    }
                }
            }


            if (power >= getFirePower()) {
                // ask the user if he wants to be rewarded or stay in his position
                // thi part will be implemented with the view
                // for now I suppose he chooses every time to be rewarded
                player.setCredits(player.getCredits() + getCredits());

                board.movePlayer(player, getDays() * -1);

                return;
            } else {
                int cardCrew = getCrew();
                if (crew > cardCrew) {
                    // player need to choose if he wants to get rid of alien and or human
                    while (cardCrew > 0) {
                        Optional<CabinComponent> chosenComponentOpt = Optional.empty(); // View
                        CabinComponent chosenComponent = chosenComponentOpt.orElseThrow();
                        if (chosenComponent.getAlien().isEmpty() && chosenComponent.getHumans() > 0) {
                            chosenComponent.setHumans(chosenComponent.getHumans() - 1, player.getShip());
                            cardCrew--;
                        } else if (chosenComponent.getAlien().isPresent() && chosenComponent.getHumans() == 0) {
                            chosenComponent.setAlien(null, player.getShip());
                            cardCrew = -2;
                        }

                    }
                }
            }

        }
    }
}