package it.polimi.ingsw.model;

import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.OddComponent;
import it.polimi.ingsw.model.game.BoardAdvancedMode;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;

public class ModelFacadeAdvancedMode extends ModelFacade {

    public ModelFacadeAdvancedMode(List<String> usernames) {
        super(usernames);
        this.board = new BoardAdvancedMode(usernames);
    }

    @Override
    protected void manageChooseAlienPhase(int playerIndex) {
        boolean phaseDone = true;
        for (; playerIndex < board.getPlayersByPos().size(); playerIndex++) { // Check if next players have to choose alien
            PlayerData player = board.getPlayers().get(playerIndex).getKey();
            List<CabinComponent> cabins = player.getShip().getComponentByType(CabinComponent.class)
                    .stream().filter(c -> !c.getIsStarting()).toList();

            for (CabinComponent cabin : cabins) {
                if (!cabin.getLinkedNeighbors(player.getShip()).stream()
                        .filter(c -> c instanceof OddComponent)
                        .toList().isEmpty()
                ) { // There is a cabin with an odd near odd component => player has to choose and phase isn't done
                    phaseDone = false;
                    playersState.put(player.getUsername(), PlayerState.WAIT_ALIEN);
                    break;
                }
            }
            if (!phaseDone) break;
        }

        if (phaseDone)
            playersState.put(board.getPlayersByPos().getFirst().getUsername(), PlayerState.DRAW_CARD);
    }

}
