package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.Alien;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.player.Ship;

import java.util.Optional;

public class CabinComponent extends Component {

    private int humans;
    private Optional<AlienType> alien;
    private final boolean isStarting;

    public CabinComponent(ConnectorType[] connectors, boolean isStarting) {
        super(connectors);
        this.humans = 2;
        this.alien = Optional.empty();
        this.isStarting = isStarting;
    }

    public int getHumans() {
        return humans;
    }

    public Optional<AlienType> getAlien() {
        return alien;
    }

    public void setAlien(AlienType alien) {
        this.alien = Optional.of(alien);
    }

    public boolean getIsStarting() {
        return isStarting;
    }

    @Override
    public void insertComponent(Ship ship, int row, int col) throws Exception {
        super.insertComponent(ship, row, col);
        ship.setCrew(ship.getCrew() + 2);
    }

}
