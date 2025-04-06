package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.HashMap;
import java.util.Map;

public class OpenSpaceCard extends Card {

    private int playerIndex;
    private Map<PlayerData, Integer> enginesActivated = new HashMap<>();

    public OpenSpaceCard(int level, boolean isLearner) {
        super(level, isLearner);
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

    @Override
    protected boolean changeState(ModelFacade model, Board board, String username) {
        PlayerState state = model.getPlayerState(username);

        if (state == PlayerState.WAIT_ENGINES)
            model.setPlayerState(username, PlayerState.DONE);

        playerIndex++;
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
                    .limit(player.getShip().getBatteries())
                    .sum();

            if (doubleEnginesPower != 0) {
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_ENGINES);
                return false;
            } else {
                enginesActivated.put(player, singleEnginesPower);
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            }
        }

        // Check if everyone has finished
        if (playerIndex >= board.getPlayersByPos().size()) {

            for (PlayerData player : board.getPlayersByPos())
                board.movePlayer(player, enginesActivated.get(player));

            endCard(board);
            return true;
        }
        return false;
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Integer power, String username, Board board) {
        if (commandType == PlayerState.WAIT_ENGINES) {
            PlayerData player = board.getPlayerEntityByUsername(username);
            enginesActivated.put(player, power);
        }
    }

    @Override
    protected void endCard(Board board) {
        for (PlayerData player : board.getPlayersByPos())
            if (enginesActivated.get(player) == 0)
                board.moveToStartingDeck(player);
        super.endCard(board);
    }

}
