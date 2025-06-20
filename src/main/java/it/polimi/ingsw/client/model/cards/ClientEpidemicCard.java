package it.polimi.ingsw.client.model.cards;

import it.polimi.ingsw.Constants;

import java.util.ArrayList;
import java.util.List;

public class ClientEpidemicCard extends ClientCard {

    public ClientEpidemicCard() {}

    @SuppressWarnings("Duplicates")
    @Override
    public String toString() {
        String hBorder = "─";
        String vBorder = "│";
        String[] angles = {"┌", "┐", "└", "┘"};
        String leftDivider = "├";
        String rightDivider = "┤";

        List<String> cardLines = new ArrayList<>();

        // Title box
        String topBorder = angles[0] + Constants.repeat(hBorder, 22) + angles[1];
        cardLines.add(topBorder);

        String title = vBorder + Constants.inTheMiddle("Epidemic" + (isLearner ? " (L)" : ""), 22) + vBorder;
        cardLines.add(title);

        // First row divider
        String divider = leftDivider + Constants.repeat(hBorder, 22) + rightDivider;
        cardLines.add(divider);

        String row = vBorder + "     👨  " + " " + "❌" + " " + "  👽\t   "+ vBorder;
        cardLines.add(row);


        // Bottom border
        String bottomBorder =  angles[2] + Constants.repeat(hBorder, 22) + angles[3];
        cardLines.add(bottomBorder);

        return String.join("\n", cardLines);
    }

}
