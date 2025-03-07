package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.game.Board;

import java.util.AbstractMap;
import java.util.List;

import it.polimi.ingsw.model.game.Board;

public class EpidemicCard extends Card{
    public EpidemicCard(int level, boolean isLearner) {
        super(level, isLearner);
    }

    @Override
    public void resolve(Board board){
        for (AbstractMap.SimpleEntry<PlayerData, Integer> entry : board.getPlayers()){
            PlayerData player=entry.getKey();
            List<CabinComponent> cabines = player.getShip().getCabines();
            for (int i=0; i<cabines.size(); i++){
                for (int j=i+1; j<cabines.size(); j++){
                    if(cabines.get(i).isNearTo(cabines.get(j))){
                        //rimuovo un membro dell'equipaggio
                    }

                }
            }

        }
        }

}
