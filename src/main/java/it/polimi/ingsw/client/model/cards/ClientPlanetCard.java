package it.polimi.ingsw.client.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.game.ClientBoard;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.cards.utils.ClientPlanet;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientPlanetCard extends ClientCard {

    @JsonProperty private List<ClientPlanet> planets;
    @JsonProperty private Map<String, ClientPlanet> landedPlayers;
    @JsonProperty private int days;

    public ClientPlanetCard() {}

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

        String title = vBorder + Constants.inTheMiddle("Planets" + (isLearner ? " (L)" : ""), 22) + vBorder;
        cardLines.add(title);

        // First row divider
        String divider = leftDivider + Constants.repeat(hBorder, 22) + rightDivider;
        cardLines.add(divider);

        //Planets

        for (ClientPlanet p : planets) {
            StringBuilder goods = new StringBuilder("  ");
            for (ColorType c : p.getRewards().keySet()) {
                for (int k = 0; k < p.getRewards().get(c); k++)
                    goods.append(c.toString()).append("  ");
            }
            String row = vBorder + Constants.inTheMiddle(goods.toString(), 22) + vBorder;
            cardLines.add(row);
            cardLines.add(divider);
        }

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
            String landInfo = landedPlayers.containsKey(player.getUsername()) ? "(landed at planet n." + (planets.indexOf(landedPlayers.get(player.getUsername()))+1) + ")" : "(not landed)";

            switch (state) {
                case DONE -> Chroma.println("- " + player.getUsername() + " has done " + landInfo, Chroma.YELLOW_BOLD);
                case WAIT -> Chroma.println("- " + player.getUsername() + " is waiting", Chroma.YELLOW_BOLD);
                case WAIT_GOODS -> Chroma.println("- " + player.getUsername() + " is collecting the reward (updating goods) " + landInfo, Chroma.YELLOW_BOLD);
                case WAIT_INDEX -> Chroma.println("- " + player.getUsername() + " is choosing the planet", Chroma.YELLOW_BOLD);
            }
        }
    }

}
