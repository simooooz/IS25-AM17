package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.properties.DirectionType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;

public class CannonComponent extends Component {

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

    @Override
    public void rotateComponent(Ship ship) {
        DirectionType[] directions = DirectionType.values(); // NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3
        this.direction = directions[(this.direction.ordinal() + 1 % 4)];
        super.rotateComponent(ship);
    }

    @Override
    public boolean checkComponent(Ship ship) {
        return super.checkComponent(ship) &&
                (direction == DirectionType.NORTH && ship.getDashboard(y - 1, x).isEmpty()) ||
                (direction == DirectionType.EAST && ship.getDashboard(y, x + 1).isEmpty()) ||
                (direction == DirectionType.SOUTH && ship.getDashboard(y + 1, x).isEmpty()) ||
                (direction == DirectionType.WEST && ship.getDashboard(y, x - 1).isEmpty());
    }

    public double calcPower() {
        int factor = direction == DirectionType.NORTH ? 1 : 2;
        return (isDouble ? 2.0 : 1.0) / factor;
    }

    @Override
    public List<String> icon() {
        String arrow = "";
        switch (this.direction) {
            case NORTH -> arrow = "⬆️";
            case EAST -> arrow = "➡️";
            case WEST -> arrow = "⬅️️";
            case SOUTH -> arrow = "⬇️️";
        }
        return new ArrayList<>(List.of(
                getIsDouble() ? Chroma.color("│🔥" + "\u200A" + arrow + "\u200A" + "🔥│", Chroma.PURPLE)
                        : Chroma.color("│ 🔥" + "\u200A" + "\u200A" + "\u200A" + arrow + " │", Chroma.PURPLE),
                Chroma.color("└───────┘", Chroma.PURPLE)
        ));
    }

}