package it.polimi.ingsw.view.TUI.graphics;

import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.Set;

/**
 * Implementation of ShipBoardTUI for the standard game (purple board)
 */
public class StandardShipBoardTUI extends ShipBoardTUI {

    public StandardShipBoardTUI(Ship ship, int rows, int cols) {
        super(ship, rows, cols);
    }

    @Override
    protected String getPlayableCellBgColor() {
        return Chroma.PURPLE_BACKGROUND;
    }

    @Override
    protected String getReserveCellBgColor() {
        return Chroma.DARKPURPLE_BACKGROUND;
    }

    @Override
    protected Set<Position> getValidPlayableCells() {
        return Set.of(
                // row 0
                new Position(4, 0),
                new Position(4, 1),
                new Position(4, 2),
                new Position(4, 4),
                new Position(4, 5),
                new Position(4, 6),

                // row 1
                new Position(3, 0),
                new Position(3, 1),
                new Position(3, 2),
                new Position(3, 3),
                new Position(3, 4),
                new Position(3, 5),
                new Position(3, 6),

                // row 2
                new Position(2, 0),
                new Position(2, 1),
                new Position(2, 2),
                new Position(2, 3),
                new Position(2, 4),
                new Position(2, 5),
                new Position(2, 6),

                // row 3
                new Position(1, 1),
                new Position(1, 2),
                new Position(1, 3),
                new Position(1, 4),
                new Position(1, 5),

                // row 4
                new Position(0, 2),
                new Position(0, 4)
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