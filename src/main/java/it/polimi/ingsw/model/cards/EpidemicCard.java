package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;

import java.util.ArrayList;
import java.util.List;

/**
 * Card implementation representing an epidemic outbreak event in the game.
 * This card simulates the spread of disease among crew members in adjacent
 * cabin components, causing crew losses due to contagion.
 * <p>
 * The epidemic affects all occupied cabins that are positioned adjacent to
 * other occupied cabins on each player's ship. When the epidemic strikes,
 * every cabin that is both occupied (contains crew) and adjacent to another
 * occupied cabin loses one crew member.
 */
public class EpidemicCard extends Card {

    /**
     * Constructs a new EpidemicCard with the specified parameters.
     *
     * @param id        the unique identifier of the card
     * @param level     the level of the card
     * @param isLearner whether this card is for learner mode
     */
    public EpidemicCard(int id, int level, boolean isLearner) {
        super(id, level, isLearner);
    }

    /**
     * Executes the epidemic card by applying disease spread effects to all players' ships.
     * <p>
     * The epidemic process works as follows for each player:
     * 1. Identifies all cabin components on the player's ship
     * 2. Checks each pair of cabins to determine adjacency and occupancy
     * 3. Marks cabins for crew reduction if they are both occupied and adjacent to other occupied cabins
     * 4. Removes one crew member from each marked cabin
     * 5. Repeats the process for all players on the board
     * <p>
     * The epidemic affects cabins that meet both criteria:
     * - The cabin contains at least one crew member (human or alien)
     * - The cabin is physically adjacent to at least one other occupied cabin
     *
     * @param model the model facade providing access to game state
     * @param board the game board containing all players and their ships
     * @return true as the epidemic card executes immediately and completely
     */
    @Override
    public boolean startCard(ModelFacade model, Board board) {
        for (PlayerData player : board.getPlayersByPos()) {

            List<CabinComponent> cabins = player.getShip().getComponentByType(CabinComponent.class);
            boolean[] checkEpidemic = new boolean[cabins.size()];
            List<CabinComponent> toDecrease = new ArrayList<>();

            for (int i = 0; i < cabins.size(); i++) {
                for (int j = i + 1; j < cabins.size(); j++) {

                    if (cabins.get(i).isNearTo(cabins.get(j)) && (cabins.get(i).getHumans() > 0 || cabins.get(i).getAlien().isPresent()) && (cabins.get(j).getHumans() > 0 || cabins.get(j).getAlien().isPresent())) {
                        if (!checkEpidemic[i]) {
                            toDecrease.add(cabins.get(i));
                            checkEpidemic[i] = true;
                        }
                        if (!checkEpidemic[j]) {
                            toDecrease.add(cabins.get(j));
                            checkEpidemic[j] = true;
                        }
                    }

                }
            }

            for (CabinComponent c : toDecrease)
                decrementCrew(c, player.getShip());
            toDecrease.clear();

        }

        return true;
    }

    /**
     * Removes one crew member from the specified cabin component:
     * - If the cabin contains human crew members, removes one human
     * - If the cabin contains no humans but has an alien, removes the alien
     * - If the cabin is empty, no action is taken
     *
     * @param cabin the cabin component from which to remove crew
     * @param ship  the ship containing the cabin, used for state updates
     */
    private void decrementCrew(CabinComponent cabin, Ship ship) {
        if (cabin.getHumans() > 0)
            cabin.setHumans(cabin.getHumans() - 1, ship);
        else if (cabin.getAlien().isPresent())
            cabin.setAlien(null, ship);
    }

}