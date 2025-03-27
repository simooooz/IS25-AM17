package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.Dice;

public class RollDicesCommand implements Command {

    private final String username;
    private final Board board;

    public RollDicesCommand(String username, Board board) {
        this.username = username;
        this.board = board;
    }

    @Override
    public void execute(Card card) {
        int res = Dice.roll() + Dice.roll();
        card.doCommandEffects(PlayerState.WAIT_ROLL_DICES, res, username, board);
    }

}