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
        List<String> icon = new ArrayList<>();
        switch (this.direction) {
            case SOUTH ->
                icon = new ArrayList<>(List.of(
                    Chroma.color("  ┌───┐  ", Chroma.ORANGE_BOLD),
                    Chroma.color("  │" + "\u2009" + "🚀" + "\u2009" + "│  ", Chroma.ORANGE_BOLD),
                    getIsDouble() ? "  " + "\u200A" + "🔥" + "\u200A" + "🔥" + "\u200A" + "  " :
                            "   " + "\u2009" + "🔥" + "\u2009" + "   " + "\u200A"
                ));
            case EAST ->
                icon = new ArrayList<>(List.of(
                        "  ┌────  ",
                        getIsDouble() ? "  " + "\u200A" + "🔥" + "\u200A" + "🔥" + "\u200A" + "  " :
                                "   " + "\u2009" + "🔥" + "\u2009" + "   " + "\u200A",
                        "  │   🔥  ",
                        "  └────  "
                ));
            case NORTH ->
                icon = new ArrayList<>(List.of(
                        getIsDouble() ? "  " + "\u200A" + "🔥" + "\u200A" + "🔥" + "\u200A" + "  " :
                                "   " + "\u2009" + "🔥" + "\u2009" + "   " + "\u200A",
                        "  │   │  ",
                        "  └───┘  "
                ));
            case WEST ->
                icon = new ArrayList<>(List.of(
                        "  ────┐  ",
                        getIsDouble() ? "  " + "\u200A" + "🔥" + "\u200A" + "🔥" + "\u200A" + "  " :
                                "   " + "\u2009" + "🔥" + "\u2009" + "   " + "\u200A",
                        " 🔥   │  ",
                        "  ────┘  "
                ));

        }
        return icon;
    }
}
