package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.Planet;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanetCard extends Card{

    private final List<Planet> planets;
    private Map<PlayerData, Planet> landedPlayers;
    private final int days;

    private int playerIndex;

    public PlanetCard(int level, boolean isLearner, List<Planet> planets, int days) {
        super(level, isLearner);
        this.planets = planets;
        this.days = days;
    }

    @Override
    public boolean startCard(ModelFacade model, Board board){
        this.playerIndex = 0;
        this.landedPlayers = new HashMap<>();

        for (PlayerData player: board.getPlayersByPos())
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);
        model.setPlayerState(board.getPlayersByPos().getFirst().getUsername(), PlayerState.WAIT_INDEX);
        return false;
    }

    @Override
    protected boolean changeState(ModelFacade model, Board board, String username) {

        PlayerState actState = model.getPlayerState(username);

        switch (actState) {
            case WAIT_GOODS -> model.setPlayerState(username, PlayerState.DONE);
            case WAIT_INDEX -> {
                if (landedPlayers.containsKey(board.getPlayerEntityByUsername(username))) {
                    model.setPlayerState(username, PlayerState.WAIT_GOODS);
                }
                else
                    model.setPlayerState(username, PlayerState.DONE);
            }
        }

        playerIndex++;
        if (playerIndex < board.getPlayersByPos().size() && landedPlayers.size() < planets.size())
            model.setPlayerState(board.getPlayersByPos().get(playerIndex).getUsername(), PlayerState.WAIT_INDEX);
        else if (playerIndex < board.getPlayersByPos().size()) // Planets are finished
            model.setPlayerState(board.getPlayersByPos().get(playerIndex).getUsername(), PlayerState.DONE);

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : board.getPlayersByPos())
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone) {
            for (PlayerData player : board.getPlayersByPos().reversed())
                if (landedPlayers.containsKey(player))
                    board.movePlayer(player, days * -1);
            endCard(board);
            return true;
        }

        return false;
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Integer value, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_INDEX && value != -1) {
            if (value >= planets.size() || value < 0)
                throw new RuntimeException("Index not valid");
            landedPlayers.put(player, planets.get(value));
        }
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, Map<ColorType, Integer> r, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        super.doSpecificCheck(commandType, landedPlayers.get(player).getRewards(), deltaGood, batteries, username, board);
    }

}
