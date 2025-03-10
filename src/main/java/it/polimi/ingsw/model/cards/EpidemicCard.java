package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.game.Board;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Optional;

import it.polimi.ingsw.model.game.Board;

public class EpidemicCard extends Card{
    public EpidemicCard(int level, boolean isLearner) {
        super(level, isLearner);
    }


// metodo che decrementa i membri di una cabina (sia nel caso umani che nel caso alieni)
    private void uploadCabin(List<CabinComponent> cabines, int i, PlayerData playerData) throws Exception {
        if (cabines.get(i).getHumans() > 0) {
            cabines.get(i).setHumans(cabines.get(i).getHumans() - 1, playerData.getShip());
        }
        else if (cabines.get(i).getAlien().isPresent()) {
            cabines.get(i).setAlien(null, playerData.getShip());

        }
    }

    @Override
    public void resolve(Board board) throws Exception {
        //per ogni giocatore
        for (SimpleEntry<PlayerData, Integer> entry : board.getPlayers()) {
            PlayerData player = entry.getKey();
            // ottengo tutte le sue cabine
            List<CabinComponent> cabins = player.getShip().getComponentByType(CabinComponent.class);
            boolean[] checkEpidemic = new boolean[cabins.size()]; //inizializzato tutto a false di default
            for (int i = 0; i < cabins.size(); i++) {
                for (int j = i+1; j < cabins.size(); j++) {
                    //se due cabine sono vicine
                    if (cabins.get(i).isNearTo(cabins.get(j))) {
                        // se non le ho già visitate, riimuovo l'equipaggio e segno che le ho visitate
                        if (!checkEpidemic[i]) {
                            uploadCabin(cabins, i, player);
                            checkEpidemic[i] = true;
                        };
                        if (!checkEpidemic[j]) {
                            uploadCabin(cabins, j, player);
                            checkEpidemic[j] = true;
                        }
                    }
                }
            }
        }
    }

}
