package it.polimi.ingsw.client.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.game.ClientBoard;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientAbandonedStationCard extends ClientCard {

    @JsonProperty private int crew;
    @JsonProperty private int days;
    @JsonProperty private Map<ColorType, Integer> goods;

    public ClientAbandonedStationCard(int id, int level, boolean isLearner, int crew, int days, Map<ColorType, Integer> goods) {
        super(id, level, isLearner);
        this.crew = crew;
        this.days = days;
        this.goods = goods;
    }

    public ClientAbandonedStationCard() {}

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

        String title = vBorder + Constants.inTheMiddle("Abandoned Station" + (isLearner ? " (L)" : ""), 22) + vBorder;
        cardLines.add(title);

        // First row divider
        String divider = leftDivider + Constants.repeat(hBorder, 22) + rightDivider;
        cardLines.add(divider);

        String crewRow = vBorder + "         " + days + " 👨" + "\t   " + vBorder;

        cardLines.add(crewRow);
        cardLines.add(divider);

        String good = "  ";
        for (ColorType c : goods.keySet()) {
            for (int k = 0; k < goods.get(c); k++)
                good = good + c.toString() + "  ";
        }
        String goodsRow = vBorder + Constants.inTheMiddle(good, 22) + vBorder;
        cardLines.add(goodsRow);
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
                case WAIT_BOOLEAN -> Chroma.println("- " + player.getUsername() + " is choosing if visit station or not", Chroma.YELLOW_BOLD);
                case WAIT_GOODS -> Chroma.println("- " + player.getUsername() + " is adding goods", Chroma.YELLOW_BOLD);
            }
        }
    }


}
