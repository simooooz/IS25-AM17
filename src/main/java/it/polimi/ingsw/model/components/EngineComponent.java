package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.properties.DirectionType;

public class EngineComponent extends Component {

    private DirectionType direction;
    private final boolean isDouble;

    public EngineComponent(ConnectorType[] connectors, DirectionType direction, boolean isDouble) {
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
    public void rotateComponent(Ship ship, boolean clockwise) {
        DirectionType[] directions = DirectionType.values();
        if (clockwise) { this.direction = directions[(this.direction.ordinal() + 1 % 4)]; }
        else { this.direction = directions[((this.direction.ordinal() + 3) % 4)]; }
        super.rotateComponent(ship, clockwise);
    }

    @Override
    public boolean checkComponent(Ship ship) {
        return super.checkComponent(ship) && (direction == DirectionType.SOUTH && ship.getDashboard(y+1, x).isEmpty());
    }

    public int calcPower() {
        return isDouble ? 2 : 1;
    }

}
