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

    public ClientAbandonedStationCard() {}

    public List<ColorType> getReward(String username) {
        List<ColorType> rewardsList = new ArrayList<>();
        goods.forEach((c, num) -> {
            for (int i=0; i<num; i++)
                rewardsList.add(c);
        });
        return rewardsList;
    }

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

        String title = vBorder + Constants.inTheMiddle("Abandoned Station" + (isLearner ? " (L)" : ""), 22) + vBorder;
        cardLines.add(title);

        // First row divider
        String divider = leftDivider + Constants.repeat(hBorder, 22) + rightDivider;
        cardLines.add(divider);

        String crewRow = vBorder + "         " + crew + " 👨" + "\t   " + vBorder;

        cardLines.add(crewRow);
        cardLines.add(divider);

        StringBuilder good = new StringBuilder("  ");
        for (ColorType c : goods.keySet()) {
            for (int k = 0; k < goods.get(c); k++)
                good.append(c.toString()).append("  ");
        }
        String goodsRow = vBorder + Constants.inTheMiddle(good.toString(), 22) + vBorder;
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
    public String printCardInfo(ClientGameModel model, ClientBoard board) {
        StringBuilder str = new StringBuilder();
        for (ClientPlayer player : board.getPlayersByPos()) {
            PlayerState state = model.getPlayerState(player.getUsername());

            switch (state) {
                case DONE -> {
                    str.append("- " + player.getUsername() + " has done\n");
                }
                case WAIT -> {
                    str.append("- " + player.getUsername() + " is waiting\n");
                }
                case WAIT_BOOLEAN -> {
                    str.append("- " + player.getUsername() + " is choosing if visit station or not\n");
                }
                case WAIT_GOODS -> {
                    str.append("- " + player.getUsername() + " is adding goods\n");
                }
            }
        }
        return str.toString();
    }


}
