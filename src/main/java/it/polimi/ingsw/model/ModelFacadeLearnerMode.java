package it.polimi.ingsw.model;

import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.game.BoardLearnerMode;

import java.util.List;

public class ModelFacadeLearnerMode extends ModelFacade {

    public ModelFacadeLearnerMode(List<String> usernames) {
        super(usernames);
        this.board = new BoardLearnerMode(usernames);
    }

    @Override
    protected void manageChooseAlienPhase(int playerIndex) {
        // There isn't alien phase in learner moe
        playersState.put(board.getPlayersByPos().getFirst().getUsername(), PlayerState.DRAW_CARD);
    }

    @Override
    public void reserveComponent(String username, int componentId) {
        throw new IllegalArgumentException("Match is in learner mode");
    }

    @Override
    public void lookCardPile(String username, int deckIndex) {
        throw new IllegalArgumentException("Match is in learner mode");
    }

    @Override
    public void moveHourglass(String username) {
        throw new IllegalArgumentException("Match is in learner mode");
    }

    @Override
    public void endFlight(String username) {
        throw new IllegalArgumentException("Match is in learner mode");
    }

}
