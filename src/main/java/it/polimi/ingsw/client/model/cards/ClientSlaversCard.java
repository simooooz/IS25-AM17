package it.polimi.ingsw.client.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.game.ClientBoard;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.common.model.enums.PlayerState;

import java.util.ArrayList;
import java.util.List;

public class ClientSlaversCard extends ClientEnemiesCard {

    @JsonProperty private int crew;
    @JsonProperty private int credits;

    public ClientSlaversCard() {}

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

        String title = vBorder + Constants.inTheMiddle("Slavers" + (isLearner ? " (L)" : ""), 22) + vBorder;
        cardLines.add(title);

        // First row divider
        String divider = leftDivider + Constants.repeat(hBorder, 22) + rightDivider;
        cardLines.add(divider);

        String firePowerRow = vBorder + "         " + enemyFirePower + " 💥" + "\t   " + vBorder;
        cardLines.add(firePowerRow);

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
    public String printCardInfo(ClientGameModel model, ClientBoard board) {
        StringBuilder str = new StringBuilder();
        for (ClientPlayer player : board.getPlayersByPos()) {
            PlayerState state = model.getPlayerState(player.getUsername());

            switch (state) {
                case DONE -> str.append("- ").append(player.getUsername()).append(" has done\n");
                case WAIT -> str.append("- ").append(player.getUsername()).append(" is waiting\n");
                case WAIT_BOOLEAN -> str.append("- ").append(player.getUsername()).append(" is choosing if take the reward or not\n");
                case WAIT_REMOVE_CREW -> str.append("- ").append(player.getUsername()).append(" has to pay his penalty (removing crew)\n");
                case WAIT_CANNONS -> str.append("- ").append(player.getUsername()).append(" is choosing if activate double cannons or not\n");
            }
        }
        str.append("Slavers are").append(enemiesDefeated ? " " : " not ").append("defeated");
        return str.toString();
    }



}
