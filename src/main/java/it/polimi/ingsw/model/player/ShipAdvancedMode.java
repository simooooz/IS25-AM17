package it.polimi.ingsw.model.player;

public class ShipAdvancedMode extends Ship {

    public ShipAdvancedMode() {
        super();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public boolean validPositions(int row, int col) {
        return (col < 0 || col > 6) || (row < 0 || row > 5) || (row == 0 && col == 0) || (row == 0 && col == 1) || (row == 0 && col == 3) || (row == 0 && col == 5) || (row == 0 && col == 6) || (row == 1 && col == 0) || (row == 1 && col == 6) || (row == 4 && col == 3);
    }

}
