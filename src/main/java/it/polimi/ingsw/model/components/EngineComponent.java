package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.dto.ComponentDTO;
import it.polimi.ingsw.common.dto.EngineComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.common.model.enums.DirectionType;

public final class EngineComponent extends Component {

    private DirectionType direction;
    private final boolean isDouble;

    public EngineComponent(int id, ConnectorType[] connectors, DirectionType direction, boolean isDouble) {
        super(id, connectors);
        this.direction = direction;
        this.isDouble = isDouble;
    }

    public boolean getIsDouble() {
        return isDouble;
    }

    public int calcPower() {
        return isDouble ? 2 : 1;
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

    @Override
    public <T> boolean matchesType(Class<T> type) {
        return type == EngineComponent.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == EngineComponent.class) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast EngineComponent to " + type.getName());
    }

    @Override
    public ComponentDTO toDTO() {
        return new EngineComponentDTO(getId(), getConnectors(), getX(), getY(), isInserted(), isShown(), direction, isDouble);
    }

}
