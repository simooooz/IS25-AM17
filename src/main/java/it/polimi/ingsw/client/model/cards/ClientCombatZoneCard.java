package it.polimi.ingsw.client.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.game.ClientBoard;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.cards.utils.ClientCriteriaType;
import it.polimi.ingsw.client.model.cards.utils.ClientPenaltyCombatZone;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientCombatZoneCard extends ClientCard {

    @JsonProperty private List<SimpleEntry<ClientCriteriaType, ClientPenaltyCombatZone>> warLines;
    @JsonProperty private int warLineIndex;
    @JsonProperty private SimpleEntry<SimpleEntry<Character, Optional<String>>, Double> worst;

    public ClientCombatZoneCard(int id, int level, boolean isLearner, List<SimpleEntry<ClientCriteriaType, ClientPenaltyCombatZone>> warLines) {
        super(id, level, isLearner);
        this.warLines = warLines;

        SimpleEntry<Character, Optional<String>> temp = new SimpleEntry<>('a', Optional.empty());
        this.worst = new SimpleEntry<>(temp, 0.0);
    }

    public ClientCombatZoneCard() {}

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

        String title = vBorder + Constants.inTheMiddle("Combat Zone" + (isLearner ? " (L)" : ""), 22) + vBorder;
        cardLines.add(title);

        // First row divider
        String divider = leftDivider + Constants.repeat(hBorder, 22) + rightDivider;
        cardLines.add(divider);

        String firstRow = vBorder + "  "  +
                warLines.getFirst().getKey().toString() + "        " +
                warLines.getFirst().getValue().toString() + "\t   " +
                vBorder;
        cardLines.add(firstRow);

        // Second row divider
        cardLines.add(divider);

        String secondRow = vBorder + "  "  +
                warLines.get(1).getKey().toString() + "        " +
                warLines.get(1).getValue().toString() + "\t   " +
                vBorder;
        cardLines.add(secondRow);

        // Third row divider
        cardLines.add(divider);

        String thirdRow = vBorder + "  "  +
                warLines.get(2).getKey().toString() + "        "  +
                warLines.get(2).getValue().toString() + "\t   " +
                vBorder;
        cardLines.add(thirdRow);

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
                case WAIT_REMOVE_GOODS -> Chroma.println("- " + player.getUsername() + " has to pay his penalty (removing goods)", Chroma.YELLOW_BOLD);
                case WAIT_REMOVE_CREW -> Chroma.println("- " + player.getUsername() + " has to pay his penalty (removing crew)", Chroma.YELLOW_BOLD);
                case WAIT_CANNONS -> Chroma.println("- " + player.getUsername() + " is choosing if activate double cannons or not", Chroma.YELLOW_BOLD);
                case WAIT_ENGINES -> Chroma.println("- " + player.getUsername() + " is choosing if activate double engines or not", Chroma.YELLOW_BOLD);
                case WAIT_ROLL_DICES -> Chroma.println("- " + player.getUsername() + " is rolling dices", Chroma.YELLOW_BOLD);
            }
        }
        Chroma.println("Fighting at war line n." + (warLineIndex+1), Chroma.YELLOW_BOLD);
        worst.getKey().getValue().ifPresent(p -> Chroma.println("Actually the worst player is " + p + " with a score of " + worst.getValue(), Chroma.YELLOW_BOLD));
    }


}
