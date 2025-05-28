package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;

import java.util.ArrayList;
import java.util.List;

public class StardustCard extends Card {

    public StardustCard(int id, int level, boolean isLearner) {
        super(id, level, isLearner);
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        board.getPlayersByPos().reversed().forEach(player -> {
            Ship ship = player.getShip();
            board.movePlayer(player, -1 * ship.countExposedConnectors());
        });

        if (!model.isLearnerMode())
            endCard(board);
        return true;
    }

    @Override
    public String toString() {
        String hBorder = "─";
        String vBorder = "│";
        String[] angles = {"┌", "┐", "└", "┘"};
        String hDivider = "┼";
        String leftDivider = "├";
        String rightDivider = "┤";

        List<String> cardLines = new ArrayList<>();

        // Title box
        String topBorder = " " + angles[0] + Constants.repeat(hBorder, 21) + angles[1] + " ";
        cardLines.add(topBorder);

        String title = " " + vBorder + Constants.inTheMiddle("Stardust" + (getIsLearner() ? "(L)" : ""), 21) + vBorder + " ";
        cardLines.add(title);

        // First row divider
        String divider = " " + leftDivider + Constants.repeat(hBorder, 21) + rightDivider + " ";
        cardLines.add(divider);

        String row = " " + vBorder + "     ✨  " + "\u2009" + "-1" + "\u2009" + "  📅     " + vBorder + " ";
        cardLines.add(row);


        // Bottom border
        String bottomBorder = " " + angles[2] + Constants.repeat(hBorder, 21) + angles[3] + " ";
        cardLines.add(bottomBorder);

        return String.join("\n", cardLines);
    }

}
