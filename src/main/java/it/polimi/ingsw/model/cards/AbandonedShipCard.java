package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.AbstractMap;
import java.util.List;
import java.util.Optional;

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
    public void resolve(Board board) throws Exception {
        List<AbstractMap.SimpleEntry<PlayerData, Integer>> players = board.getPlayers();

        // iterate on the list of player -> for now I've not implemented if a player does not want to
        // play this card, it's only to iterate on the list of player
        for (AbstractMap.SimpleEntry<PlayerData, Integer> entry : players) {
            PlayerData player = entry.getKey();

            int cardCrew = getCrew();
            int crew = player.getShip().getCrew();
            int credits = player.getCredits();

            // check if the player can really play the card
            if (crew > cardCrew) {
                // player need to choose if he wants to get rid of alien and or human
                while (cardCrew > 0) {
                    Optional<CabinComponent> chosenComponentOpt = Optional.empty(); // View
                    CabinComponent chosenComponent = chosenComponentOpt.orElseThrow();
                    if (chosenComponent.getAlien().isEmpty() && chosenComponent.getHumans() > 0) {
                        chosenComponent.setHumans(chosenComponent.getHumans() - 1, player.getShip());
                        cardCrew --;
                    }
                    else if (chosenComponent.getAlien().isPresent() && chosenComponent.getHumans() == 0) {
                        chosenComponent.setAlien(null, player.getShip());
                        cardCrew =- 2;
                    }

                }
                board.movePlayer(player, getDays() * -1);
            }
        }
    }
}
