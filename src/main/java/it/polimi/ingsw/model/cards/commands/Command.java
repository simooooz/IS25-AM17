package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.cards.Card;

public interface Command {

    boolean execute(Card card);

}
