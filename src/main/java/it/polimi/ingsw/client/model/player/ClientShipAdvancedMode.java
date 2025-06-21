package it.polimi.ingsw.client.model.player;

import it.polimi.ingsw.view.TUI.Chroma;

public final class ClientShipAdvancedMode extends ClientShip {

    public ClientShipAdvancedMode() {
        super();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public boolean validPositions(int row, int col) {
        return !((col < 0 || col > 6) || (row < 0 || row > 5) || (row == 0 && col == 0) || (row == 0 && col == 1) || (row == 0 && col == 3) || (row == 0 && col == 5) || (row == 0 && col == 6) || (row == 1 && col == 0) || (row == 1 && col == 6) || (row == 4 && col == 3));
    }

    @Override
    public String getShipBgColor(int row, int col) {
        boolean isPlayable = validPositions(row, col);
        if (isPlayable)
            return Chroma.PURPLE_BACKGROUND;
        else if (row == 0 && (col == 5 || col == 6))
            return Chroma.DARKPURPLE_BACKGROUND;
        return Chroma.RESET;
    }

}
