package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.Planet;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanetCard extends Card{

    private final List<Planet> planets;
    private Map<PlayerData, Planet> landedPlayers;
    private final int days;

    private int playerIndex;

    public PlanetCard(int id, int level, boolean isLearner, List<Planet> planets, int days) {
        super(id, level, isLearner);
        this.planets = planets;
        this.days = days;
    }

    @Override
    public boolean startCard(ModelFacade model, Board board){
        this.playerIndex = 0;
        this.landedPlayers = new HashMap<>();

        for (PlayerData player: board.getPlayersByPos())
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);

        return autoCheckPlayers(model, board);
    }

    private boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < board.getPlayersByPos().size(); playerIndex++) {
            if (playerIndex < board.getPlayersByPos().size() && landedPlayers.size() < planets.size()) {
                model.setPlayerState(board.getPlayersByPos().get(playerIndex).getUsername(), PlayerState.WAIT_INDEX);
                return false;
            }
            else if (playerIndex < board.getPlayersByPos().size()) // Planets are finished
                model.setPlayerState(board.getPlayersByPos().get(playerIndex).getUsername(), PlayerState.DONE);
        }

        // Check if everyone has finished
        boolean hasLanded = true;
        boolean hasLandedAndSetGoods = true;
        for (PlayerData player : board.getPlayersByPos()) {
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE && model.getPlayerState(player.getUsername()) != PlayerState.WAIT)
                hasLanded = false;
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasLandedAndSetGoods = false;
        }

        if (hasLanded && !hasLandedAndSetGoods) { // First phase finished, start second one
            for (PlayerData player : board.getPlayersByPos())
                if (model.getPlayerState(player.getUsername()) == PlayerState.WAIT)
                    model.setPlayerState(player.getUsername(), PlayerState.WAIT_GOODS);
        }

        if (hasLandedAndSetGoods) { // Card finished
            for (PlayerData player : board.getPlayersByPos().reversed())
                if (landedPlayers.containsKey(player))
                    board.movePlayer(player, days * -1);
            if (!model.isLearnerMode())
                endCard(board);
            return true;
        }
        return false;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_INDEX) {
            if (value == null) // Players doesn't land
                model.setPlayerState(username, PlayerState.DONE);
            else if (value >= planets.size() || value < 0) // Invalid index
                throw new RuntimeException("Index not valid");
            else { // Land
                PlayerData player = board.getPlayerEntityByUsername(username);
                landedPlayers.put(player, planets.get(value));
                model.setPlayerState(username, PlayerState.WAIT);
            }
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_GOODS) {
            model.setPlayerState(username, PlayerState.DONE);
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, Map<ColorType, Integer> r, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        super.doSpecificCheck(commandType, landedPlayers.get(player).getRewards(), deltaGood, batteries, username, board);
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

        String title = " " + vBorder + Constants.inTheMiddle("Planets" + (getIsLearner() ? "(L)" : ""), 21) + vBorder + " ";
        cardLines.add(title);

        // First row divider
        String divider = " " + leftDivider + Constants.repeat(hBorder, 21) + rightDivider + " ";
        cardLines.add(divider);

        //Planets

        for (Planet p : planets) {
            String goods = "  ";
            for (ColorType c : p.getRewards().keySet()) {
                for (int k = 0; k < p.getRewards().get(c); k++)
                    goods =  goods + c.toString() + "  ";
            }
            String row = " " + vBorder + Constants.inTheMiddle(goods, 21) + vBorder + " ";
            cardLines.add(row);
            cardLines.add(divider);
        }

        String dayRow = " " + vBorder + "\u2009" + Constants.inTheMiddle(days + " 📅", 20) +
                "\u2009"  + "\u200A" + vBorder + " ";
        cardLines.add(dayRow);


        // Bottom border
        String bottomBorder = " " + angles[2] + Constants.repeat(hBorder, 21) + angles[3] + " ";
        cardLines.add(bottomBorder);

        return String.join("\n", cardLines);
    }
}
