package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.utils.Planet;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanetCard extends Card{

    private final List<Planet> planets
    private Map<PlayerData, Planet> landedPlayers;
    private final int days;
    private int playerIndex;


    public PlanetCard(int level, boolean isLearner, List<Planet> planets, int days) {
        super(level, isLearner);
        this.planets = planets;
        this.days = days;
    }


    public void startCard(Board board){
        this.playerIndex = 0;
        this.landedPlayers = new HashMap<>();
        for (PlayerData player: board.getPlayersByPos()){
            playersState.put(player.getUsername(), CardState.WAIT);
        }
        playersState.put(board.getPlayersByPos().getFirst().getUsername(), CardState.WAIT_INDEX);
    }

    public void changeState(Board board, String username) throws Exception {

        CardState actState = playersState.get(username);

        switch (actState){
            case WAIT_GOODS -> playersState.put(username, CardState.DONE);
            case WAIT_INDEX -> {
                if (landedPlayers.containsKey(board.getPlayerEntitybyUsername(username))) {
                    playersState.put(username, CardState.WAIT_GOODS);
                }
                else {
                    playersState.put(username, CardState.DONE);
                }
            }
        }

        playerIndex++;
        if (playerIndex < board.getPlayersByPos().size())
            playersState.put(board.getPlayersByPos().get(playerIndex).getUsername(), CardState.WAIT_INDEX);

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : board.getPlayersByPos())
            if (playersState.get(player.getUsername()) != CardState.DONE)
                hasDone = false;

        if (hasDone) {
            for (PlayerData player : board.getPlayersByPos().reversed())
                if (landedPlayers.containsKey(player))
                    board.movePlayer(player, days * -1);
            endCard();
        }
    }

    public void endCard() {

    }

    public void doCommandEffects(CardState commandType, Integer value, String username, Board board) {
        PlayerData player = board.getPlayerEntitybyUsername(username);
        // if player decides to land on a planet
        if (commandType == CardState.WAIT_INDEX && value != -1)
            landedPlayers.put(player, planets.get(value));
    }


}
