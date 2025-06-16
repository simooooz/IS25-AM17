package it.polimi.ingsw.client.model.player;

import it.polimi.ingsw.client.model.components.ClientComponent;
import it.polimi.ingsw.view.TUI.Chroma;

public class ClientShipLearnerMode extends ClientShip {

    public ClientShipLearnerMode() {
        super();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public boolean validPositions(int row, int col) {
        return !((col < 1 || col > 5) || (row < 0 || row > 5) || (row == 0 && col == 1) || (row == 0 && col == 2) || (row == 0 && col == 4) || (row == 0 && col == 5) || (row == 1 && col == 1) || (row == 1 && col == 5) || (row == 4 && col == 3));
    }

    @Override
    public String getShipBgColor(int row, int col) {
        boolean isPlayable = validPositions(row, col);
        if (isPlayable)
            return Chroma.BLUE_BACKGROUND;
        return Chroma.RESET;
    }

}
