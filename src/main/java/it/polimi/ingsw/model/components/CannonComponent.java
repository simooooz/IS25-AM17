package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.properties.DirectionType;

public class CannonComponent extends Component {

    private DirectionType direction;
    private final boolean isDouble;

    public CannonComponent(ConnectorType[] connectors, DirectionType direction, boolean isDouble) {
        super(connectors);
        this.direction = direction;
        this.isDouble = isDouble;
    }

    public DirectionType getDirection() {
        return direction;
    }

    public boolean getIsDouble() {
        return isDouble;
    }

    @Override
    public void rotateComponent(boolean clockwise) {
        DirectionType[] directions = DirectionType.values(); // NORTH = 0, EAST = 1, SOUTH = 2, EAST = 3
        if (clockwise) { this.direction = directions[(this.direction.ordinal() + 1 % 4)]; }
        else { this.direction = directions[(this.direction.ordinal() + 3 % 4)]; }
        super.rotateComponent(clockwise);
    }

    @Override
    public void checkComponent(Ship ship) throws Exception {
        super.checkComponent(ship);
        if (
            (direction == DirectionType.NORTH && ship.getDashboard(y-1, x).isPresent()) ||
            (direction == DirectionType.EAST && ship.getDashboard(y, x+1).isPresent()) ||
            (direction == DirectionType.SOUTH && ship.getDashboard(y+1, x).isPresent()) ||
            (direction == DirectionType.WEST && ship.getDashboard(y, x-1).isPresent())
        ) throw new Exception();
    }

    public double calcPower() {
        int factor = direction == DirectionType.NORTH ? 2 : 1;
        return (isDouble ? 2.0 : 1.0) / factor;
    }
}
