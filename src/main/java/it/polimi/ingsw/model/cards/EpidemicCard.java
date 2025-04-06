package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.game.Board;

import java.util.ArrayList;
import java.util.List;


public class EpidemicCard extends Card {

    public EpidemicCard(int level, boolean isLearner) {
        super(level, isLearner);
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        for (PlayerData player : board.getPlayersByPos()) {
            List<CabinComponent> cabins = player.getShip().getComponentByType(CabinComponent.class);
            boolean[] checkEpidemic = new boolean[cabins.size()]; // to check if cabins are already visited
            List<CabinComponent> toDecrease = new ArrayList<>();

            for (int i = 0; i < cabins.size(); i++) {
                for (int j = i+1; j < cabins.size(); j++) {

                    if (cabins.get(i).isNearTo(cabins.get(j)) && (cabins.get(i).getHumans() > 0 || cabins.get(i).getAlien().isPresent()) && (cabins.get(j).getHumans() > 0 || cabins.get(j).getAlien().isPresent())) {
                        if (!checkEpidemic[i]) { // if not visited yet
                            toDecrease.add(cabins.get(i));
                            checkEpidemic[i] = true;
                        }
                        if (!checkEpidemic[j]) { // if not visited yet
                            toDecrease.add(cabins.get(j));
                            checkEpidemic[j] = true;
                        }
                    }

                }
            }

            for (CabinComponent c : toDecrease)
                decrementCrew(c, player);
            toDecrease.clear();

        }

        endCard(board);
        return true;
    }

    private void decrementCrew(CabinComponent cabin, PlayerData playerData) {
        if (cabin.getHumans() > 0)
            cabin.setHumans(cabin.getHumans() - 1, playerData.getShip());
        else if (cabin.getAlien().isPresent())
            cabin.setAlien(null, playerData.getShip());
    }

}
