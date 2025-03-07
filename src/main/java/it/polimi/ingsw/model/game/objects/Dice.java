package it.polimi.ingsw.model.game.objects;

public class Dice {

    public static int roll() {
        return (int)(Math.random() * 6) + 1;
    }

}
