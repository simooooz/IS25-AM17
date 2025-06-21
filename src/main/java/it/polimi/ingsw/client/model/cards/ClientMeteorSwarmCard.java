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

        for (ClientMeteor m : meteors) {
            String meteorRow = vBorder + "       " + m.toString() +"\t   " + vBorder;
            cardLines.add(meteorRow);
        }


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
                case WAIT_SHIP_PART -> Chroma.println("- " + player.getUsername() + " is choosing which part of ship to keep", Chroma.YELLOW_BOLD);
                case WAIT_SHIELD -> Chroma.println("- " + player.getUsername() + " is choosing if activate a shield or not", Chroma.YELLOW_BOLD);
                case WAIT_CANNONS -> Chroma.println("- " + player.getUsername() + " is choosing if activate a double cannon or not", Chroma.YELLOW_BOLD);
                case WAIT_ROLL_DICES -> Chroma.println("- " + player.getUsername() + " is rolling dices", Chroma.YELLOW_BOLD);
            }
        }

        if (board.getPlayersByPos().stream().noneMatch(p -> model.getPlayerState(p.getUsername()) == PlayerState.WAIT_ROLL_DICES))
            Chroma.println("Meteor n." + (meteorIndex+1) + " is hitting at coord: " + coords.getLast(), Chroma.YELLOW_BOLD);
        else if (meteorIndex > 0)
            Chroma.println("Previous meteor n." + (meteorIndex) + " has come at coord: " + coords.getLast(), Chroma.YELLOW_BOLD);
    }


}
