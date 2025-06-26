package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;

/**
 * Card implementation representing a stardust field encounter in the game.
 * This card creates an environmental hazard that affects all players based
 * on their ship's structural integrity and exposed connection points.
 * <p>
 * The stardust field represents a region of space filled with cosmic particles
 * that can interfere with ship systems and slow down travel. Ships with more
 * exposed connectors (representing incomplete hull protection or damaged
 * sections) are more vulnerable to the stardust interference and experience
 * greater propulsion impedance.
 * <p>
 * This card provides an immediate effect that executes automatically without
 * requiring player interaction, representing the unavoidable nature of
 * environmental space hazards.
 *
 * @author Generated Javadoc
 * @version 1.0
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
     * The stardust field affects all players simultaneously, with the impact
     * proportional to their ship's structural vulnerabilities:
     * - Ships with more exposed connectors suffer greater movement penalties
     * - Ships with better hull integrity (fewer exposed connectors) are less affected
     * - Movement penalty equals the negative value of exposed connectors count
     * <p>
     * Players are processed in reverse order to maintain proper positioning
     * relationships on the flight path, ensuring that relative positions are
     * preserved correctly after the environmental effect is applied.
     * <p>
     * The negative movement represents being slowed down or pushed backward
     * by the cosmic particle interference, simulating the realistic effects
     * of navigating through a hazardous stardust field.
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