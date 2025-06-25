package it.polimi.ingsw.client.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.game.ClientBoard;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.cards.utils.ClientMeteor;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;

public class ClientMeteorSwarmCard extends ClientCard {

    @JsonProperty private List<ClientMeteor> meteors;
    @JsonProperty private int meteorIndex;
    @JsonProperty private List<Integer> coords;

    public ClientMeteorSwarmCard() {}

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

        String title = vBorder + Constants.inTheMiddle("Meteor Swarm" + (isLearner ? " (L)" : ""), 22) + vBorder;
        cardLines.add(title);

        // First row divider
        String divider = leftDivider + Constants.repeat(hBorder, 22) + rightDivider;
        cardLines.add(divider);

        for (int i=0; i<meteors.size(); i++) {
            String meteorRow = vBorder + Constants.inTheMiddle(i<coords.size()? String.valueOf(coords.get(i)): "       ", 7) + meteors.get(i).toString() +"\t   " + vBorder;
            cardLines.add(meteorRow);
        }


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
                case DONE -> str.append("- " + player.getUsername() + " has done" + "\n");
                case WAIT -> str.append("- " + player.getUsername() + " is waiting" + "\n");
                case WAIT_SHIP_PART -> str.append("- " + player.getUsername() + " is choosing which part of ship to keep" + "\n");
                case WAIT_SHIELD -> str.append("- " + player.getUsername() + " is choosing if activate a shield or not" + "\n");
                case WAIT_CANNONS -> str.append("- " + player.getUsername() + " is choosing if activate a double cannon or not" + "\n");
                case WAIT_ROLL_DICES -> str.append("- " + player.getUsername() + " is rolling dices" + "\n");
            }
        }
        if (board.getPlayersByPos().stream().noneMatch(p -> model.getPlayerState(p.getUsername()) == PlayerState.WAIT_ROLL_DICES))
            str.append("Meteor n." + (meteorIndex+1) + " is hitting at coord: " + coords.getLast() + "\n");
        else if (meteorIndex > 0)
            str.append("Previous meteor n." + (meteorIndex) + " has come at coord: " + coords.getLast() + "\n");
        return str.toString();
    }


}
