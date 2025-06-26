package it.polimi.ingsw.client.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.game.ClientBoard;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.enums.ColorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientSmugglersCard extends ClientEnemiesCard {

    @JsonProperty private int penalty;
    @JsonProperty private Map<ColorType, Integer> reward;

    public ClientSmugglersCard() {}

    @Override
    public List<ColorType> getReward(String username) {
        List<ColorType> rewardsList = new ArrayList<>();
        reward.forEach((c, num) -> {
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

        String title = vBorder + Constants.inTheMiddle("Smugglers" + (isLearner ? " (L)" : ""), 22) + vBorder;
        cardLines.add(title);

        // First row divider
        String divider = leftDivider + Constants.repeat(hBorder, 22) + rightDivider;
        cardLines.add(divider);

        String firePowerRow = vBorder + "         " + enemyFirePower + " 💥" + "\t   " + vBorder;
        cardLines.add(firePowerRow);

        cardLines.add(divider);

        String penaltyRow = vBorder + "        " + penalty + " 🔲❌" +"\t   " + vBorder;
        cardLines.add(penaltyRow);

        cardLines.add(divider);

        StringBuilder good = new StringBuilder("  ");
        for (ColorType c : reward.keySet()) {
            for (int k = 0; k < reward.get(c); k++)
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
                case DONE -> str.append("- ").append(player.getUsername()).append(" has done\n");
                case WAIT -> str.append("- ").append(player.getUsername()).append(" is waiting\n");
                case WAIT_BOOLEAN -> str.append("- ").append(player.getUsername()).append(" is choosing if take the reward or not\n");
                case WAIT_GOODS -> str.append("- ").append(player.getUsername()).append(" is collecting the reward (updating goods)\n");
                case WAIT_REMOVE_GOODS -> str.append("- ").append(player.getUsername()).append(" has to pay his penalty (removing goods)\n");
                case WAIT_CANNONS -> str.append("- ").append(player.getUsername()).append(" is choosing if activate double cannons or not\n");
            }
        }
        str.append("Smugglers are").append(enemiesDefeated ? " " : " not ").append("defeated\n");
        return str.toString();
    }

}
