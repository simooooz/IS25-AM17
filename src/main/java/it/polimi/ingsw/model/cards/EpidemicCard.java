package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.game.Board;

import java.util.List;


public class EpidemicCard extends Card {

    public EpidemicCard(int level, boolean isLearner) {
        super(level, isLearner);
    }

    public void startCard(Board board) throws Exception {
        for (PlayerData player : board.getPlayersByPos()) {
            List<CabinComponent> cabins = player.getShip().getComponentByType(CabinComponent.class);
            boolean[] checkEpidemic = new boolean[cabins.size()]; // to check if cabins are already visited

            for (int i = 0; i < cabins.size(); i++) {
                for (int j = i+1; j < cabins.size(); j++) {

                    if (cabins.get(i).isNearTo(cabins.get(j))) {
                        if (!checkEpidemic[i]) { // if not visited yet
                            decrementCrew(cabins, i, player);
                            checkEpidemic[i] = true;
                        }
                        if (!checkEpidemic[j]) { // if not visited yet
                            decrementCrew(cabins, j, player);
                            checkEpidemic[j] = true;
                        }
                    }

                }
            }
        }

        endCard();
    }


    private void decrementCrew(List<CabinComponent> cabines, int i, PlayerData playerData) throws Exception {
        if (cabines.get(i).getHumans() > 0)
            cabines.get(i).setHumans(cabines.get(i).getHumans() - 1, playerData.getShip());
        else if (cabines.get(i).getAlien().isPresent())
            cabines.get(i).setAlien(null, playerData.getShip());
    }

    public void endCard() {

    }

}
