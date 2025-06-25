package it.polimi.ingsw.client.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.game.ClientBoard;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.cards.utils.ClientCannonFire;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;

public class ClientPiratesCard extends ClientEnemiesCard {

    @JsonProperty private int credits;
    @JsonProperty private List<ClientCannonFire> cannonFires;

    @JsonProperty private List<String> defeatedPlayers;
    @JsonProperty private int cannonIndex;
    @JsonProperty private List<Integer> coords;

    public ClientPiratesCard() {}

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

        String title = vBorder + Constants.inTheMiddle("Pirates" + (isLearner ? " (L)" : ""), 22) + vBorder;
        cardLines.add(title);

        // First row divider
        String divider = leftDivider + Constants.repeat(hBorder, 22) + rightDivider;
        cardLines.add(divider);
        String firePowerRow = vBorder + "         " + enemyFirePower + " 💥" + "\t   " + vBorder;
        cardLines.add(firePowerRow);

        cardLines.add(divider);

        for (int i=0; i<cannonFires.size(); i++) {
            String meteorRow = vBorder + Constants.inTheMiddle(i<coords.size()? String.valueOf(coords.get(i)): "       ", 7) + cannonFires.get(i).toString() +"\t   " + vBorder;
            cardLines.add(meteorRow);
        }

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
            String def = defeatedPlayers.contains(player.getUsername()) ? "(defeated)" : "";

            switch (state) {
                case DONE -> str.append("- " + player.getUsername() + " has done " + def + "\n");
                case WAIT -> str.append("- " + player.getUsername() + " is waiting " + def + "\n");
                case WAIT_BOOLEAN -> str.append("- " + player.getUsername() + " is choosing if take the reward or not" + "\n");
                case WAIT_SHIELD -> str.append("- " + player.getUsername() + " is choosing if activate a shield or not " + def + "\n");
                case WAIT_CANNONS -> str.append("- " + player.getUsername() + " is choosing if activate double cannons or not " + def + "\n");
                case WAIT_ROLL_DICES -> str.append("- " + player.getUsername() + " is rolling dices " + def + "\n");
                case WAIT_SHIP_PART -> str.append("- " + player.getUsername() + " might have lost part of his ship " + def + "\n");
            }
        }
        str.append("Pirates are" + (enemiesDefeated ? " " : " not ") + "defeated" + "\n");

        if (enemiesDefeated && board.getPlayersByPos().stream().noneMatch(p -> model.getPlayerState(p.getUsername()) == PlayerState.WAIT_ROLL_DICES))
            str.append("Cannon fire n." + (cannonIndex+1) + " is hitting at coord: " + coords.getLast() + "\n");
        else if (cannonIndex > 0)
            str.append("Previous cannon fire n." + (cannonIndex) + " has come at coord: " + coords.getLast() + "\n");

        return str.toString();
    }


}
