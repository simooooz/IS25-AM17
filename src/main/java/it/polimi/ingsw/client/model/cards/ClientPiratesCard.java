package it.polimi.ingsw.client.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.game.ClientBoard;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.cards.utils.ClientCannonFire;
import it.polimi.ingsw.common.model.enums.PlayerState;

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
            String def = defeatedPlayers.contains(player.getUsername()) ? "(defeated)\n" : "\n";

            switch (state) {
                case DONE -> str.append("- ").append(player.getUsername()).append(" has done ").append(def);
                case WAIT -> str.append("- ").append(player.getUsername()).append(" is waiting ").append(def);
                case WAIT_BOOLEAN -> str.append("- ").append(player.getUsername()).append(" is choosing if take the reward or not\n");
                case WAIT_SHIELD -> str.append("- ").append(player.getUsername()).append(" is choosing if activate a shield or not ").append(def);
                case WAIT_CANNONS -> str.append("- ").append(player.getUsername()).append(" is choosing if activate double cannons or not ").append(def);
                case WAIT_ROLL_DICES -> str.append("- ").append(player.getUsername()).append(" is rolling dices ").append(def);
                case WAIT_SHIP_PART -> str.append("- ").append(player.getUsername()).append(" might have lost part of his ship ").append(def);
            }
        }
        str.append("Pirates are").append(enemiesDefeated ? " " : " not ").append("defeated\n");

        if (enemiesDefeated && board.getPlayersByPos().stream().noneMatch(p -> model.getPlayerState(p.getUsername()) == PlayerState.WAIT_ROLL_DICES))
            str.append("Cannon fire n.").append(cannonIndex + 1).append(" is hitting at coord: ").append(coords.getLast());
        else if (cannonIndex > 0)
            str.append("Previous cannon fire n.").append(cannonIndex).append(" has come at coord: ").append(coords.getLast());

        return str.toString();
    }


}
