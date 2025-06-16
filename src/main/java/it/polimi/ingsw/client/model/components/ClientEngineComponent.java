package it.polimi.ingsw.client.model.components;

import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.DirectionType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;

public class ClientEngineComponent extends ClientComponent {

    private DirectionType direction;
    private final boolean isDouble;

    public ClientEngineComponent(int id, ConnectorType[] connectors, DirectionType direction, boolean isDouble) {
        super(id, connectors);
        this.direction = direction;
        this.isDouble = isDouble;
    }

    @Override
    public void rotateComponent() {
        super.rotateComponent();
        DirectionType[] directions = DirectionType.values();
        this.direction = directions[((this.direction.ordinal() + 1) % 4)];
    }

    @Override
    public void rotateComponent(ClientPlayer player, int rotations) {
        super.rotateComponent(player, rotations);
        DirectionType[] directions = DirectionType.values();
        this.direction = directions[((this.direction.ordinal() + rotations) % 4)];
    }

    @Override
    public List<String> icon() {
        String arrow = switch (this.direction) {
            case SOUTH -> "↓";
            case NORTH -> "↑";
            case WEST -> "←";
            case EAST -> "→";
        };
        return new ArrayList<>(List.of(
                isDouble ? Chroma.color("│  🚀" + "🚀\t│", Chroma.ORANGE)
                        : Chroma.color("│   " + "🚀" + "  \t│", Chroma.ORANGE),
                Chroma.color("└    " + arrow + "    ┘", Chroma.ORANGE)
        ));
    }

}