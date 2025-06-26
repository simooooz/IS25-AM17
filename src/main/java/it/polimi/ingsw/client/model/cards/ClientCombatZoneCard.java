package it.polimi.ingsw.client.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.cards.utils.ClientWarLine;
import it.polimi.ingsw.client.model.game.ClientBoard;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.common.model.Pair;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientCombatZoneCard extends ClientCard {

    @JsonProperty private List<ClientWarLine> warLines;
    @JsonProperty private int warLineIndex;
    @JsonProperty private Pair<Optional<String>, Double> worst;

    public ClientCombatZoneCard() {}

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

        String title = vBorder + Constants.inTheMiddle("Combat Zone" + (isLearner ? " (L)" : ""), 22) + vBorder;
        cardLines.add(title);

        // First row divider
        String divider = leftDivider + Constants.repeat(hBorder, 22) + rightDivider;
        cardLines.add(divider);

        String firstRow = vBorder + "  "  +
                warLines.getFirst().getCriteriaType().toString() + "        " +
                warLines.getFirst().getPenalty().toString() + "\t   " +
                vBorder;
        cardLines.add(firstRow);

        // Second row divider
        cardLines.add(divider);

        String secondRow = vBorder + "  "  +
                warLines.get(1).getCriteriaType().toString() + "        " +
                warLines.get(1).getPenalty().toString() + "\t   " +
                vBorder;
        cardLines.add(secondRow);

        // Third row divider
        cardLines.add(divider);

        String thirdRow = vBorder + "  "  +
                warLines.get(2).getCriteriaType().toString() + "        "  +
                warLines.get(2).getPenalty().toString() + "\t   " +
                vBorder;
        cardLines.add(thirdRow);

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
                case DONE -> str.append("- " + player.getUsername()).append(" has done" + "\n");
                case WAIT -> str.append("- " + player.getUsername() + " is waiting" +"\n");
                case WAIT_SHIP_PART -> str.append("- " + player.getUsername() + " is choosing which part of ship to keep" + "\n");
                case WAIT_SHIELD -> str.append("- " + player.getUsername() + " is choosing if activate a shield or not" + "\n");
                case WAIT_REMOVE_GOODS -> str.append("- " + player.getUsername() + " has to pay his penalty (removing goods)" + "\n");
                case WAIT_REMOVE_CREW -> str.append("- " + player.getUsername() + " has to pay his penalty (removing crew)" + "\n");
                case WAIT_CANNONS -> str.append("- " + player.getUsername() + " is choosing if activate double cannons or not" + "\n");
                case WAIT_ENGINES -> str.append("- " + player.getUsername() + " is choosing if activate double engines or not" + "\n");
                case WAIT_ROLL_DICES -> str.append("- " + player.getUsername() + " is rolling dices" + "\n");
            }
        }
        str.append("Fighting at war line n." + (warLineIndex+1));
        str.append("\n");
        worst.getKey().ifPresent(p -> str.append("Actually the worst player is " + p + " with a score of " + worst.getValue() + "\n"));
        return str.toString();
    }


}
