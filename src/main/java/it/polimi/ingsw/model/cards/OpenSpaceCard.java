package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

public class OpenSpaceCard extends Card {
    private int playerIndex;

    public OpenSpaceCard(int level, boolean isLearner) {
        super(level, isLearner);
    }

    public void startCard(Board board) {
        playerIndex = 0;

        board.getPlayersByPos().forEach(player ->
                playersState.put(player.getUsername(), CardState.WAIT)
        );
        playersState.put(board.getPlayersByPos().get(playerIndex).getUsername(), CardState.WAIT_ENGINE);

        autoCheckPlayers(board);
    }

    public void changeState(Board board, String username) {
        CardState state = playersState.get(username);

        if (state == CardState.WAIT_ENGINE)
            playersState.put(username, CardState.DONE);

        playerIndex++;
        autoCheckPlayers(board);

        if (playerIndex >= board.getPlayersByPos().size())
            endCard();
    }

    public void autoCheckPlayers(Board board) {
        for (; playerIndex < board.getPlayersByPos().size(); playerIndex++) {
            PlayerData player = board.getPlayersByPos().get(playerIndex);
            int singleEnginesPower = (player.getShip().getEngineAlien() ? 2 : 0) + player.getShip().getComponentByType(EngineComponent.class).stream()
                            .filter(engine -> !engine.getIsDouble())
                            .mapToInt(EngineComponent::calcPower)
                            .sum();
            int doubleEnginesPower = player.getShip().getComponentByType(EngineComponent.class).stream()
                    .filter(EngineComponent::getIsDouble)
                    .mapToInt(EngineComponent::calcPower)
                    .limit(player.getShip().getBatteries())
                    .sum();

            if (doubleEnginesPower != 0) {
                playersState.put(player.getUsername(), CardState.WAIT_ENGINE);
                return;
            } else {
                board.movePlayer(player, singleEnginesPower);
                playersState.put(player.getUsername(), CardState.DONE);
            }
        }
    }

    public void doCommandEffects(CardState commandType, Integer power, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == CardState.WAIT_ENGINE) {
            board.movePlayer(player, power);
        }
    }

    void endCard() {
        // todo
    }

}
