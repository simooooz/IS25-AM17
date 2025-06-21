package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenSpaceCard extends Card {

    private int playerIndex;
    private Map<String, Integer> enginesActivated = new HashMap<>();

    public OpenSpaceCard(int id, int level, boolean isLearner) {
        super(id, level, isLearner);
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        playerIndex = 0;
        this.enginesActivated = new HashMap<>();

        board.getPlayersByPos().forEach(player ->
                model.setPlayerState(player.getUsername(), PlayerState.WAIT)
        );
        return autoCheckPlayers(model, board);
    }

    public boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < board.getPlayersByPos().size(); playerIndex++) {
            PlayerData player = board.getPlayersByPos().get(playerIndex);

            int singleEnginesPower = player.getShip().getComponentByType(EngineComponent.class).stream()
                    .filter(engine -> !engine.getIsDouble())
                    .mapToInt(EngineComponent::calcPower)
                    .sum();
            if (singleEnginesPower > 0 && player.getShip().getEngineAlien())
                singleEnginesPower += 2;

            int doubleEnginesPower = player.getShip().getComponentByType(EngineComponent.class).stream()
                    .filter(EngineComponent::getIsDouble)
                    .mapToInt(EngineComponent::calcPower)
                    .boxed()
                    .sorted(Comparator.reverseOrder())
                    .limit(player.getShip().getBatteries())
                    .mapToInt(Integer::intValue)
                    .sum();

            if (doubleEnginesPower != 0) {
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_ENGINES);
                return false;
            }
            else {
                enginesActivated.put(player.getUsername(), singleEnginesPower);
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            }
        }

        // Check if everyone has finished
        if (playerIndex >= board.getPlayersByPos().size()) {

            List<PlayerData> players = board.getPlayersByPos();
            for (PlayerData player : players)
                board.movePlayer(player, enginesActivated.get(player.getUsername()));

            return true;
        }
        return false;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Integer power, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_ENGINES) {
            model.setPlayerState(username, PlayerState.DONE);
            enginesActivated.put(username, power);

            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid");
    }

    @Override
    public void endCard(Board board) {
        for (PlayerData player : board.getPlayersByPos())
            if (enginesActivated.get(player.getUsername()) == 0)
                player.endFlight();
        super.endCard(board);
    }

}