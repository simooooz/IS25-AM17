package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.Planet;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanetCard extends Card{

    @JsonProperty private final List<Planet> planets;
    @JsonProperty private final Map<String, Planet> landedPlayers;
    @JsonProperty private final int days;

    private int playerIndex;

    public PlanetCard(int id, int level, boolean isLearner, List<Planet> planets, int days) {
        super(id, level, isLearner);
        this.planets = planets;
        this.days = days;
        this.landedPlayers = new HashMap<>();
    }

    @Override
    public boolean startCard(ModelFacade model, Board board){
        this.playerIndex = 0;

        for (PlayerData player: board.getPlayersByPos())
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);

        return autoCheckPlayers(model, board);
    }

    private boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < board.getPlayersByPos().size(); playerIndex++) {
            if (landedPlayers.size() < planets.size()) {
                model.setPlayerState(board.getPlayersByPos().get(playerIndex).getUsername(), PlayerState.WAIT_INDEX);
                return false;
            }
            else // Planets are finished
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
                if (landedPlayers.containsKey(player.getUsername()))
                    board.movePlayer(player, days * -1);
            return true;
        }
        return false;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_INDEX) {
            if (value == null) // Players doesn't land
                model.setPlayerState(username, PlayerState.DONE);
            else if (value >= planets.size() || landedPlayers.containsValue(planets.get(value))) // Invalid index
                throw new IllegalArgumentException("Planet not valid or already occupied");
            else { // Land
                landedPlayers.put(username, planets.get(value));
                model.setPlayerState(username, PlayerState.WAIT);
            }
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_GOODS) {
            model.setPlayerState(username, PlayerState.DONE);
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, Map<ColorType, Integer> r, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        super.doSpecificCheck(commandType, landedPlayers.get(username).rewards(), deltaGood, batteries, username, board);
    }

}
