package it.polimi.ingsw.view.TUI.graphics;

import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.Set;

/**
 * Implementation of ShipBoardTUI for the learner flight (blue board)
 */
public class LearnerFlightShipBoardTUI extends ShipBoardTUI {

    public LearnerFlightShipBoardTUI(Ship ship, int rows, int cols) {
        super(ship, rows, cols);
    }

    @Override
    protected String getPlayableCellBgColor() {
        return Chroma.BLUE_BACKGROUND;
    }

    @Override
    protected String getReserveCellBgColor() {
        return Chroma.DARKBLUE_BACKGROUND;
    }

    @Override
    protected Set<Position> getValidPlayableCells() {
        return Set.of(
                new Position(4, 1),
                new Position(4, 2),
                new Position(4, 4),
                new Position(4, 5),

                // row 1
                new Position(3, 1),
                new Position(3, 2),
                new Position(3, 3),
                new Position(3, 4),
                new Position(3, 5),

                // row 2
                new Position(2, 1),
                new Position(2, 2),
                new Position(2, 3),
                new Position(2, 4),
                new Position(2, 5),

                // row 3
                new Position(1, 2),
                new Position(1, 3),
                new Position(1, 4),

                // row 4
                new Position(0, 3)
        );
    }

    @Override
    protected Set<Position> getReserveCells() {
        return Set.of(
                new Position(0, 5),
                new Position(0, 6)
        );
    }
}