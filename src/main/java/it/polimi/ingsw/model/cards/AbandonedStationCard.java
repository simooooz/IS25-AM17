package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AbandonedStationCard extends Card{

    private final int crew;
    private final int days;
    private final Map<ColorType, Integer> goods;
    private List<PlayerData> players;

    private int playerIndex;
    private boolean shipConquered;

    public AbandonedStationCard(int level, boolean isLearner, int crew, int days, Map<ColorType, Integer> goods) {
        super(level, isLearner);
        this.crew = crew;
        this.days = days;
        this.goods = goods;
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.playerIndex = 0;
        this.shipConquered = false;
        this.players = new ArrayList<>(board.getPlayersByPos());

        for (PlayerData player : this.players)
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);
        return autoCheckPlayers(model, board);
    }

    private boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < this.players.size(); playerIndex++) {
            PlayerData player = this.players.get(playerIndex);

            if (shipConquered)
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (player.getShip().getCrew() < crew) // User loses automatically
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else { // User could win
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_BOOLEAN);
                return false;
            }
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : this.players)
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone) {
            if (!model.isLearnerMode())
                endCard(board);
            return true;
        }

        return false;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_BOOLEAN && value) {
            model.setPlayerState(username, PlayerState.WAIT_GOODS);
            shipConquered = true;
            return false;
        }
        else if (commandType == PlayerState.WAIT_BOOLEAN) {
            model.setPlayerState(username, PlayerState.DONE);
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_GOODS) {
            model.setPlayerState(username, PlayerState.DONE);

            PlayerData player = board.getPlayerEntityByUsername(username);
            board.movePlayer(player, days * -1);

            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, Map<ColorType, Integer> r, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        super.doSpecificCheck(commandType, this.goods, deltaGood, batteries, username, board);
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

        String title = " " + vBorder + Constants.inTheMiddle("AbandonedStation" + (getIsLearner() ? "(L)" : ""), 21) + vBorder + " ";
        cardLines.add(title);

        // First row divider
        String divider = " " + leftDivider + Constants.repeat(hBorder, 21) + rightDivider + " ";
        cardLines.add(divider);

        String crewRow = " " + vBorder + "\u2009" + Constants.inTheMiddle(crew + " 👨‍🚀", 23) +
                "\u2009"  + "\u200A" + vBorder + " ";
        cardLines.add(crewRow);
        cardLines.add(divider);

        String good = "  ";
        for (ColorType c : goods.keySet()) {
            for (int k = 0; k < goods.get(c); k++)
                good = good + c.toString() + "  ";
        }
        String goodsRow = " " + vBorder + Constants.inTheMiddle(good, 21) + vBorder + " ";
        cardLines.add(goodsRow);
        cardLines.add(divider);

        String dayRow = " " + vBorder + "\u2009" + Constants.inTheMiddle(days + " 📅", 20) +
                "\u2009" + "\u200A" + vBorder + " ";
        cardLines.add(dayRow);

        // Bottom border
        String bottomBorder = " " + angles[2] + Constants.repeat(hBorder, 21) + angles[3] + " ";
        cardLines.add(bottomBorder);

        return String.join("\n", cardLines);

    }


}
