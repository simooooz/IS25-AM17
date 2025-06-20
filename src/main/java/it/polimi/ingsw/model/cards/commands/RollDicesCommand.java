package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.game.Board;

public class RollDicesCommand implements Command {

    private final ModelFacade model;
    private final String username;
    private final Board board;
    private final int value;

    public RollDicesCommand(ModelFacade model, Board board, String username) {
        this.model = model;
        this.username = username;
        this.board = board;
        this.value = ((int)(Math.random() * 6) + 1) + ((int)(Math.random() * 6) + 1);
    }

    @Override
    public boolean execute(Card card) {
        return card.doCommandEffects(PlayerState.WAIT_ROLL_DICES, value, model, board, username);
    }

}