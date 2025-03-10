package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
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

    public void setHumans(int humans, Ship ship) throws Exception {
        if (alien.isPresent()) setAlien(null, ship);
        int delta = humans - this.humans;
        ship.setCrew(ship.getCrew() + delta);
        this.humans = humans;
    }

    public Optional<AlienType> getAlien() {
        return alien;
    }

    public void setAlien(AlienType alien, Ship ship) throws Exception {
        if (isStarting) throw new Exception();
        if (this.alien.isEmpty() && alien != null) {
            setHumans(0, ship);
            ship.setCrew(ship.getCrew() + 2);
        }
        else if (this.alien.isPresent() && alien == null) {
            ship.setCrew(ship.getCrew() - 2);
        }
        this.alien = Optional.ofNullable(alien);
    }

    public boolean getIsStarting() {
        return isStarting;
    }

    @Override
    public void insertComponent(Ship ship, int row, int col) throws Exception {
        super.insertComponent(ship, row, col);
        ship.setCrew(ship.getCrew() + 2);
    }

    @Override
    public void affectDestroy(Ship ship) {
        super.affectDestroy(ship);
        ship.setCrew(ship.getCrew() - (alien.isPresent() ? 2 : humans));
    }

}
