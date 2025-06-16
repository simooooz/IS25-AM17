package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.events.game.CrewUpdatedEvent;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.model.exceptions.CabinComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.Optional;

public class CabinComponent extends Component {

    private int humans;
    private Optional<AlienType> alien;
    private final boolean isStarting;

    public CabinComponent(int id, ConnectorType[] connectors, boolean isStarting) {
        super(id, connectors);
        this.humans = 2;
        this.alien = Optional.empty();
        this.isStarting = isStarting;
    }

    public boolean getIsStarting() {
        return isStarting;
    }

    public int getHumans() {
        return humans;
    }

    public void setHumans(int humans, Ship ship) {
        if (ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this))
            throw new ComponentNotValidException("Tile not valid");
        if (humans < 0) humans = 0;
        if (alien.isPresent()) setAlien(null, ship);
        int delta = humans - this.humans;
        ship.setCrew(ship.getCrew() + delta);
        this.humans = humans;

        EventContext.emit(new CrewUpdatedEvent(getId(), humans, alien.orElse(null)));
    }

    public Optional<AlienType> getAlien() {
        return alien;
    }

    public void setAlien(AlienType newAlien, Ship ship) {
        if (ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this))
            throw new ComponentNotValidException("Tile not valid");
        if (isStarting && newAlien != null) throw new ComponentNotValidException("Alien isn't compatible with staring cabin tile");
        else if (this.alien.isEmpty() && newAlien != null) { // Should set new alien

            // Check if exists an odd component
            this.getLinkedNeighbors(ship).stream()
                    .filter(c -> c instanceof OddComponent)
                    .map(c -> (OddComponent) c)
                    .filter(c -> c.getType() == newAlien)
                    .findFirst()
                    .orElseThrow(() -> new CabinComponentNotValidException("Alien " + newAlien + " is not compatible with this cabin"));

            if (newAlien == AlienType.CANNON && !ship.getCannonAlien()) { ship.setCannonAlien(true); }
            else if (newAlien == AlienType.CANNON && ship.getCannonAlien()) throw new CabinComponentNotValidException("Alien " + newAlien + " is already present");
            else if (newAlien == AlienType.ENGINE && !ship.getEngineAlien()) { ship.setEngineAlien(true); }
            else if (newAlien == AlienType.ENGINE && ship.getEngineAlien()) throw new CabinComponentNotValidException("Alien " + newAlien + " is already present");

            setHumans(0, ship);
            ship.setCrew(ship.getCrew() + 1);
        }
        else if (this.alien.isPresent() && newAlien == null) { // Should remove alien
            ship.setCrew(ship.getCrew() - 1);
            if (this.alien.get() == AlienType.CANNON) { ship.setCannonAlien(false); }
            else { ship.setEngineAlien(false); }
        }
        this.alien = Optional.ofNullable(newAlien);

        EventContext.emit(new CrewUpdatedEvent(getId(), humans, alien.orElse(null)));
    }

    @Override
    public void insertComponent(PlayerData player, int row, int col, int rotations, boolean weld) {
        if (isStarting) {
            this.showComponent();
            this.x = col;
            this.y = row;
            player.getShip().getDashboard()[row][col] = Optional.of(this);
            this.weldComponent();
        }
        else
            super.insertComponent(player, row, col, rotations, weld);

        player.getShip().setCrew(player.getShip().getCrew() + 2);
    }

    @Override
    public void affectDestroy(PlayerData player) {
        setHumans(0, player.getShip());
        setAlien(null, player.getShip());
        super.affectDestroy(player);
    }

}