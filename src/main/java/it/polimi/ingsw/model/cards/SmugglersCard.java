package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.*;

public class SmugglersCard extends EnemiesCard {

    private final int penalty;
    private final Map<ColorType, Integer> reward;

    public SmugglersCard(int id, int level, boolean isLearner, int smugglersFirePower, int penalty, Map<ColorType, Integer> reward, int days) {
        super(id, level, isLearner, days, smugglersFirePower);
        this.penalty = penalty;
        this.reward = reward;
    }

    @Override
    public boolean defeatedMalus(ModelFacade model, PlayerData player) {
        model.setPlayerState(player.getUsername(), PlayerState.WAIT_REMOVE_GOODS);
        return true;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_BOOLEAN) {
            if (value) {
                PlayerData player = board.getPlayerEntityByUsername(username);
                board.movePlayer(player, -1 * this.days);
                model.setPlayerState(username, PlayerState.WAIT_GOODS);
                return false;
            }
            else {
                model.setPlayerState(username, PlayerState.DONE);
                playerIndex++;
                return autoCheckPlayers(model, board);
            }
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_GOODS || commandType == PlayerState.WAIT_REMOVE_GOODS) {
            model.setPlayerState(username, PlayerState.DONE);
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, int number, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        super.doSpecificCheck(commandType, penalty, deltaGood, batteries, username, board);
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, Map<ColorType, Integer> r, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        super.doSpecificCheck(commandType, this.reward, deltaGood, batteries, username, board);
    }

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
        String topBorder = " " + angles[0] + Constants.repeat(hBorder, 21) + angles[1] + " ";
        cardLines.add(topBorder);

        String title = " " + vBorder + Constants.inTheMiddle("Smugglers" + (getIsLearner() ? "(L)" : ""), 21) + vBorder + " ";
        cardLines.add(title);

        // First row divider
        String divider = " " + leftDivider + Constants.repeat(hBorder, 21) + rightDivider + " ";
        cardLines.add(divider);

        String firePowerRow = " " + vBorder + "\u2009" + Constants.inTheMiddle(enemyFirePower + " 💥", 20) +
                "\u2009"  + "\u200A" + vBorder + " ";
        cardLines.add(firePowerRow);

        cardLines.add(divider);

        String penaltyRow = " " + vBorder + "\u2009" + Constants.inTheMiddle(penalty + " 🔲❌", 19) +
                "\u2009"  + vBorder + " ";
        cardLines.add(penaltyRow);

        cardLines.add(divider);

        String good = "  ";
        for (ColorType c : reward.keySet()) {
            for (int k = 0; k < reward.get(c); k++)
                good = good + c.toString() + "  ";
        }
        String goodsRow = " " + vBorder + Constants.inTheMiddle(good, 21) + vBorder + " ";
        cardLines.add(goodsRow);
        cardLines.add(divider);

        String dayRow = " " + vBorder + "\u2009" + Constants.inTheMiddle(days + " 📅", 20) +
                "\u2009"  + "\u200A" + vBorder + " ";
        cardLines.add(dayRow);

        // Bottom border
        String bottomBorder = " " + angles[2] + Constants.repeat(hBorder, 21) + angles[3] + " ";
        cardLines.add(bottomBorder);

        return String.join("\n", cardLines);
    }

    @Override
    public void printCardInfo(ModelFacade model, Board board) {
        for (PlayerData player : board.getPlayersByPos()) {
            PlayerState state = model.getPlayerState(player.getUsername());

            switch (state) {
                case DONE -> Chroma.println("- " + player.getUsername() + " has done", Chroma.YELLOW_BOLD);
                case WAIT -> Chroma.println("- " + player.getUsername() + " is waiting", Chroma.YELLOW_BOLD);
                case WAIT_BOOLEAN -> Chroma.println("- " + player.getUsername() + " is choosing if take the reward or not", Chroma.YELLOW_BOLD);
                case WAIT_GOODS -> Chroma.println("- " + player.getUsername() + " is collecting the reward (updating goods)", Chroma.YELLOW_BOLD);
                case WAIT_REMOVE_GOODS -> Chroma.println("- " + player.getUsername() + " has to pay his penalty (removing goods)", Chroma.YELLOW_BOLD);
                case WAIT_CANNONS -> Chroma.println("- " + player.getUsername() + " is choosing if activate double cannons or not", Chroma.YELLOW_BOLD);
            }
        }
        Chroma.println("Smugglers are" + (enemiesDefeated ? " " : " not ") + "defeated", Chroma.YELLOW_BOLD);
    }

}
