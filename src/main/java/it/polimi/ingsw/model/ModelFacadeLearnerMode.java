package it.polimi.ingsw.model;

import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.model.game.BoardLearnerMode;

import java.util.List;
import java.util.function.Consumer;

public class ModelFacadeLearnerMode extends ModelFacade {

    public ModelFacadeLearnerMode(List<String> usernames) {
        super(usernames);
        this.board = new BoardLearnerMode(usernames);
    }

    @Override
    protected void manageChooseAlienPhase(int playerIndex) {
        // There isn't alien phase in learner mode
        board.pickNewCard(this);
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
    public void moveHourglass(String username, Consumer<List<GameEvent>> callback) {
        throw new IllegalArgumentException("Match is in learner mode");
    }

    @Override
    public void endFlight(String username) {
        throw new IllegalArgumentException("Match is in learner mode");
    }

}
