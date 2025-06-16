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

public class ClientSlaversCard extends ClientEnemiesCard {

    @JsonProperty private int crew;
    @JsonProperty private int credits;

    public ClientSlaversCard(int id, int level, boolean isLearner, int crew, int credits, int days, int slaversFirePower) {
        super(id, level, isLearner, days, slaversFirePower);
        this.crew = crew;
        this.credits = credits;
    }

    public ClientSlaversCard() {}

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
    public void printCardInfo(ClientGameModel model, ClientBoard board) {
        for (ClientPlayer player : board.getPlayersByPos()) {
            PlayerState state = model.getPlayerState(player.getUsername());

            switch (state) {
                case DONE -> Chroma.println("- " + player.getUsername() + " has done", Chroma.YELLOW_BOLD);
                case WAIT -> Chroma.println("- " + player.getUsername() + " is waiting", Chroma.YELLOW_BOLD);
                case WAIT_BOOLEAN -> Chroma.println("- " + player.getUsername() + " is choosing if take the reward or not", Chroma.YELLOW_BOLD);
                case WAIT_REMOVE_CREW -> Chroma.println("- " + player.getUsername() + " has to pay his penalty (removing crew)", Chroma.YELLOW_BOLD);
                case WAIT_CANNONS -> Chroma.println("- " + player.getUsername() + " is choosing if activate double cannons or not", Chroma.YELLOW_BOLD);
            }
        }
        Chroma.println("Slavers are" + (enemiesDefeated ? " " : " not ") + "defeated", Chroma.YELLOW_BOLD);
    }


}
