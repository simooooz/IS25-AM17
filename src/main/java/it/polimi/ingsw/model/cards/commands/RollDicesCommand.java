package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.game.objects.Dice;

public class RollDicesCommand implements Command<Integer> {

    @Override
    public Integer execute() {
        return Dice.roll() + Dice.roll();
    }

}