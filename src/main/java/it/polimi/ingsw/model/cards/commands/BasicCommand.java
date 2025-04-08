package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.game.Board;

public class BasicCommand implements Command {

    private final ModelFacade model;
    private final Board board;
    private final String username;

    public BasicCommand(ModelFacade model, Board board, String username) {
        this.model = model;
        this.board = board;
        this.username = username;
    }

    @Override
    public boolean execute(Card card) {
        return card.doCommandEffects(model.getPlayerState(username), model, board, username);
    }

}
