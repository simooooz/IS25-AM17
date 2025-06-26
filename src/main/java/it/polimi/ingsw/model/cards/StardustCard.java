package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;

/**
 * Card implementation representing a stardust field encounter in the game.
 * This card creates an environmental hazard that affects all players based
 * on their ship's structural integrity and exposed connection points.
 * <p>
 * This card provides an immediate effect that executes automatically without
 * requiring player interaction.
 */
public class StardustCard extends Card {

    /**
     * Constructs a new StardustCard with the specified parameters.
     *
     * @param id        the unique identifier of the card
     * @param level     the level of the card
     * @param isLearner whether this card is for learner mode
     */
    public StardustCard(int id, int level, boolean isLearner) {
        super(id, level, isLearner);
    }

    /**
     * Executes the stardust field encounter by applying movement penalties
     * based on each ship's exposed connector count.
     * <p>
     * The stardust field affects all players simultaneously, and the movement penalty
     * equals the negative value of exposed connectors count
     * <p>
     * Players are processed in reverse order ensuring that relative positions are
     * preserved correctly after the effect is applied.
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and their ships
     * @return true as the stardust encounter executes immediately and completely
     */
    @Override
    public boolean startCard(ModelFacade model, Board board) {
        board.getPlayersByPos().reversed().forEach(player -> {
            Ship ship = player.getShip();
            board.movePlayer(player, -1 * ship.countExposedConnectors());
        });
        return true;
    }

}