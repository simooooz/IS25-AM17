package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

abstract public class Card {

    private final int level;            // card level (1 or 2)
    private final boolean isLearner;    // learning flight

    public Card(int level, boolean isLearner) {
        this.level = level;
        this.isLearner = isLearner;
    }

    public int getLevel() {
        return level;
    }

    public boolean getIsLearner() {
        return isLearner;
    }

    // definisco il metodo final in modo tale che non possano esserci override nell classi figlie
    public final void resolve(Game game, PlayerData player) throws Exception {
        if (requiresPlayerInteraction(player)) {
            game.setState(GameState.WAIT);
        } else {
            complete(game, player, null);
        }
    }

    protected abstract boolean requiresPlayerInteraction(PlayerData player);
    protected abstract void doResolve(Game game, PlayerData player, Object data) throws Exception;

    public final void complete(Game game, PlayerData player, Object data) throws Exception {
        doResolve(game, player, data);

        game.setState(GameState.FLIGHT);
    }

}
