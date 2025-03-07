package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.Alien;

import java.util.Optional;

public class CabinComponent extends Component {

    private int humans;
    private Optional<Alien> alien;
    private final boolean isStarting;

    public CabinComponent(ConnectorType[] connectors, int humans, Optional<Alien> alien, boolean isStarting) {
        super(connectors);
        this.humans = humans;
        this.alien = Optional.empty();
        this.isStarting = isStarting;
    }

    public int getHumans() {
        return humans;
    }

    public Optional<Alien> getAlien() {
        return alien;
    }

    public void setAlien(Alien alien) {
        this.alien = Optional.of(alien);
    }

    public boolean getIsStarting() {
        return isStarting;
    }
}
