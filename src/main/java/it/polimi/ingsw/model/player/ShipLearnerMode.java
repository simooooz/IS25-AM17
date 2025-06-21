package it.polimi.ingsw.model.player;

public final class ShipLearnerMode extends Ship {

    public ShipLearnerMode() {
        super();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public boolean validPositions(int row, int col) {
        return (col < 1 || col > 5) || (row < 0 || row > 5) || (row == 0 && col == 1) || (row == 0 && col == 2) || (row == 0 && col == 4) || (row == 0 && col == 5) || (row == 1 && col == 1) || (row == 1 && col == 5) || (row == 4 && col == 3);
    }

}
