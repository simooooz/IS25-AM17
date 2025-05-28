package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.game.Board;

public class RollDicesCommand implements Command {

    private final ModelFacade model;
    private final String username;
    private final Board board;
    private final int value;

    public RollDicesCommand(ModelFacade model, Board board, String username, int value) {
        this.model = model;
        this.username = username;
        this.board = board;
        this.value = value;
    }

    @Override
    public boolean execute(Card card) {
        return card.doCommandEffects(PlayerState.WAIT_ROLL_DICES, value, model, board, username);
    }

}