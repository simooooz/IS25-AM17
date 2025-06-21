package it.polimi.ingsw.client.model.cards;


import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.game.ClientBoard;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;

public class ClientAbandonedShipCard extends ClientCard {

    @JsonProperty private int crew;
    @JsonProperty private int credits;
    @JsonProperty private int days;

    public ClientAbandonedShipCard() {}

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

        String title = vBorder + Constants.inTheMiddle("Abandoned Ship" + (isLearner ? " (L)" : ""), 22) + vBorder;
        cardLines.add(title);

        // First row divider
        String divider = leftDivider + Constants.repeat(hBorder, 22) + rightDivider;
        cardLines.add(divider);

        String crewRow = vBorder + "        " + crew + " 👨❌" +"\t   " + vBorder;
        cardLines.add(crewRow);

        cardLines.add(divider);

        String creditRow = vBorder + "         " + credits + " 💲" + "\t   " + vBorder;
        cardLines.add(creditRow);

        cardLines.add(divider);

        String dayRow = vBorder + "         " + days + " 📅" + "\t   " + vBorder;
        cardLines.add(dayRow);

        // Bottom border
        String bottomBorder = angles[2] + Constants.repeat(hBorder, 22) + angles[3];
        cardLines.add(bottomBorder);

        return String.join("\n", cardLines);
    }

    @Override
    public void printCardInfo(ClientGameModel model, ClientBoard board) {
        for (ClientPlayer player : board.getPlayersByPos()) {
            PlayerState state = model.getPlayerState(player.getUsername());

            switch (state) {
                case DONE -> Chroma.println("- " + player.getUsername() + " has done", Chroma.YELLOW_BOLD);
                case WAIT -> Chroma.println("- " + player.getUsername() + " is waiting", Chroma.YELLOW_BOLD);
                case WAIT_BOOLEAN -> Chroma.println("- " + player.getUsername() + " is choosing if visit ship or not", Chroma.YELLOW_BOLD);
                case WAIT_REMOVE_CREW -> Chroma.println("- " + player.getUsername() + " is removing crew", Chroma.YELLOW_BOLD);
            }
        }
    }


}
