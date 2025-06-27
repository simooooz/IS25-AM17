package it.polimi.ingsw.model;

import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.OddComponent;
import it.polimi.ingsw.model.game.BoardAdvancedMode;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;

/**
 * Advanced mode implementation of the ModelFacade that provides enhanced gameplay mechanics
 * and enables alien selection rules.
 *
 */
public class ModelFacadeAdvancedMode extends ModelFacade {

    /**
     * Constructs a new ModelFacadeAdvancedMode with the specified list of player usernames.
     * Initializes the advanced mode board with enhanced gameplay mechanics.
     *
     * @param usernames the list of player usernames participating in the advanced mode game
     * @throws NullPointerException if usernames is null
     * @throws IllegalArgumentException if usernames is empty or contains invalid usernames
     */
    public ModelFacadeAdvancedMode(List<String> usernames) {
        super(usernames);
        this.board = new BoardAdvancedMode(usernames);
    }

    /**
     * Manages the alien selection phase in advanced mode.
     *
     */
    @Override
    protected void manageChooseAlienPhase(int playerIndex) {
        boolean phaseDone = true;
        for (; playerIndex < board.getPlayersByPos().size(); playerIndex++) { // Check if next players have to choose alien
            PlayerData player = board.getPlayers().get(playerIndex).getKey();
            List<CabinComponent> cabins = player.getShip().getComponentByType(CabinComponent.class)
                    .stream().filter(c -> !c.getIsStarting()).toList();

            for (CabinComponent cabin : cabins) {
                if (!cabin.getLinkedNeighbors(player.getShip()).stream()
                        .filter(c -> c.matchesType(OddComponent.class))
                        .map(c -> c.castTo(OddComponent.class))
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
            board.pickNewCard(this);
    }

}