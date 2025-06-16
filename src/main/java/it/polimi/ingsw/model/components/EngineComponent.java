package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.common.model.enums.DirectionType;

public class EngineComponent extends Component {

    private DirectionType direction;
    private final boolean isDouble;

    public EngineComponent(int id, ConnectorType[] connectors, DirectionType direction, boolean isDouble) {
        super(id, connectors);
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
    public void rotateComponent(PlayerData player, int rotations) {
        super.rotateComponent(player, rotations);
        DirectionType[] directions = DirectionType.values();
        this.direction = directions[((this.direction.ordinal() + rotations) % 4)];
    }

    @Override
    public boolean checkComponent(Ship ship) {
        return super.checkComponent(ship) && (direction == DirectionType.SOUTH && ship.getDashboard(y+1, x).isEmpty());
    }

    public int calcPower() {
        return isDouble ? 2 : 1;
    }

}
