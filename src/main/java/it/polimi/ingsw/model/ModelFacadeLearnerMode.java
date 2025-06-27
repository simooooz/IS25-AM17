package it.polimi.ingsw.model;

import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.model.game.BoardLearnerMode;

import java.util.List;
import java.util.function.Consumer;

/**
 * Learner mode implementation of the ModelFacade
 */
public class ModelFacadeLearnerMode extends ModelFacade {

    /**
     * Constructs a new ModelFacadeLearnerMode with the specified list of player usernames.
     * Initializes the learner mode board with simplified gameplay mechanics.
     *
     * @param usernames the list of player usernames participating in the learner mode game
     * @throws NullPointerException if usernames is null
     * @throws IllegalArgumentException if usernames is empty or contains invalid usernames
     */
    public ModelFacadeLearnerMode(List<String> usernames) {
        super(usernames);
        this.board = new BoardLearnerMode(usernames);
    }

    /**
     * Manages the alien selection phase in learner mode by skipping it entirely.
     *
     */
    @Override
    protected void manageChooseAlienPhase(int playerIndex) {
        // There isn't alien phase in learner mode
        board.pickNewCard(this);
    }

    /**
     * Prevents component reservation in learner mode.
     */
    @Override
    public void reserveComponent(String username, int componentId) {
        throw new IllegalArgumentException("Match is in learner mode");
    }

    /**
     * Prevents card pile viewing in learner mode.
     */
    @Override
    public void lookCardPile(String username, int deckIndex) {
        throw new IllegalArgumentException("Match is in learner mode");
    }

    /**
     * Prevents hourglass movement in learner mode.
     */
    @Override
    public void moveHourglass(String username, Consumer<List<Event>> callback) {
        throw new IllegalArgumentException("Match is in learner mode");
    }

    /**
     * Prevents early flight ending in learner mode.
     */
    @Override
    public void endFlight(String username) {
        throw new IllegalArgumentException("You can't end flight in advance in learner mode");
    }

}