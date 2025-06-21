package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.common.model.enums.DirectionType;

public final class CannonComponent extends Component {

    private DirectionType direction;
    private final boolean isDouble;

    public CannonComponent(int id, ConnectorType[] connectors, DirectionType direction, boolean isDouble) {
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

    public double calcPower() {
        int factor = direction == DirectionType.NORTH ? 1 : 2;
        return (isDouble ? 2.0 : 1.0) / factor;
    }

    @Override
    public void rotateComponent(PlayerData player, int rotations) {
        super.rotateComponent(player, rotations);
        DirectionType[] directions = DirectionType.values(); // NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3
        this.direction = directions[((this.direction.ordinal() + rotations) % 4)];
    }

    @Override
    public boolean checkComponent(Ship ship) {
        return super.checkComponent(ship) &&
                (direction == DirectionType.NORTH && ship.getDashboard(y - 1, x).isEmpty()) ||
                (direction == DirectionType.EAST && ship.getDashboard(y, x + 1).isEmpty()) ||
                (direction == DirectionType.SOUTH && ship.getDashboard(y + 1, x).isEmpty()) ||
                (direction == DirectionType.WEST && ship.getDashboard(y, x - 1).isEmpty());
    }

    @Override
    public <T> boolean matchesType(Class<T> type) {
        return type == CannonComponent.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == CannonComponent.class) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast CannonComponent to " + type.getName());
    }

}