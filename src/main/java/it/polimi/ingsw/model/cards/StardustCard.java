package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;

public class StardustCard extends Card {

    public StardustCard(int level, boolean isLearner) {
        super(level, isLearner);
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        board.getPlayersByPos().reversed().forEach(player -> {
            Ship ship = player.getShip();
            board.movePlayer(player, -1 * ship.countExposedConnectors());
        });

        if (!model.isLearnerMode())
            endCard(board);
        return true;
    }

}
