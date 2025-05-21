package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.properties.DirectionType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;

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
    public void rotateComponent(Ship ship) {
        DirectionType[] directions = DirectionType.values();
        this.direction = directions[((this.direction.ordinal() + 1) % 4)];
        super.rotateComponent(ship);
    }

    @Override
    public boolean checkComponent(Ship ship) {
        return super.checkComponent(ship) && (direction == DirectionType.SOUTH && ship.getDashboard(y+1, x).isEmpty());
    }

    public int calcPower() {
        return isDouble ? 2 : 1;
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
                getIsDouble() ? Chroma.color("│🚀" + "\u200A" + arrow + "\u200A" + "🚀│", Chroma.ORANGE)
                        : Chroma.color("│ 🚀" + "\u200A" + "\u200A" + "\u200A" + arrow + " │", Chroma.ORANGE),
                Chroma.color("└───────┘", Chroma.ORANGE)
        ));
    }

}
