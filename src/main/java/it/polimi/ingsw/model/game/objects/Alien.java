package it.polimi.ingsw.model.game.objects;

public class Alien {

    private final AlienType type;

    public Alien(AlienType type) {
        this.type = type;
    }

    public AlienType getType() {
        return type;
    }
}
