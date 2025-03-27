package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.cards.Card;

public interface Command {

    void execute(Card card);

}
