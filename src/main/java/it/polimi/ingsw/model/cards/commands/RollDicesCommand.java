package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.Dice;

public class RollDicesCommand implements Command {

    private final ModelFacade model;
    private final String username;
    private final Board board;

    public RollDicesCommand(ModelFacade model, Board board, String username) {
        this.model = model;
        this.username = username;
        this.board = board;
    }

    @Override
    public boolean execute(Card card) {
        int res = Dice.roll() + Dice.roll();
        return card.doCommandEffects(PlayerState.WAIT_ROLL_DICES, res, model, board, username);
    }

}