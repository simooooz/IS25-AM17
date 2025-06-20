package it.polimi.ingsw.model.components;

import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.common.model.enums.DirectionType;


public final class ShieldComponent extends Component {

    private final DirectionType[] directionsProtected;

    public ShieldComponent(int id, ConnectorType[] connectors, DirectionType[] directionsProtected) {
        super(id, connectors);
        this.directionsProtected = directionsProtected;
    }

    @Override
    public void insertComponent(PlayerData player, int row, int col, int rotations, boolean weld) {
        super.insertComponent(player, row, col, rotations, weld);
        for (DirectionType direction : directionsProtected)
            player.getShip().getProtectedSides().add(direction);
    }

    @Override
    public void rotateComponent(PlayerData player, int rotations) {
        super.rotateComponent(player, rotations);

        player.getShip().getProtectedSides().remove(directionsProtected[0]);
        player.getShip().getProtectedSides().remove(directionsProtected[1]);

        DirectionType[] directions = DirectionType.values();
        this.directionsProtected[0] = directions[((this.directionsProtected[0].ordinal() + rotations) % 4)];
        this.directionsProtected[1] = directions[((this.directionsProtected[1].ordinal() + rotations) % 4)];

        player.getShip().getProtectedSides().add(directionsProtected[0]);
        player.getShip().getProtectedSides().add(directionsProtected[1]);
    }

    @Override
    public void affectDestroy(PlayerData player) {
        super.affectDestroy(player);
        player.getShip().getProtectedSides().remove(directionsProtected[0]);
        player.getShip().getProtectedSides().remove(directionsProtected[1]);
    }

    @Override
    public <T> boolean matchesType(Class<T> type) {
        return type == ShieldComponent.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T castTo(Class<T> type) {
        if (type == ShieldComponent.class) {
            return (T) this;
        }
        throw new ClassCastException("Cannot cast ShieldComponent to " + type.getName());
    }

}