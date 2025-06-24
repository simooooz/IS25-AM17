package it.polimi.ingsw.client.model.components;

import it.polimi.ingsw.common.dto.EngineComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.DirectionType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;

public final class ClientEngineComponent extends ClientComponent {

    private DirectionType direction;
    private final boolean isDouble;

    public ClientEngineComponent(int id, ConnectorType[] connectors, DirectionType direction, boolean isDouble) {
        super(id, connectors);
        this.direction = direction;
        this.isDouble = isDouble;
    }

    public ClientEngineComponent(EngineComponentDTO dto) {
        super(dto);
        this.direction = dto.direction;
        this.isDouble = dto.isDouble;
    }

    public boolean isDouble() {
        return isDouble;
    }

    @Override
    public void rotateComponent() {
        super.rotateComponent();
        DirectionType[] directions = DirectionType.values();
        this.direction = directions[((this.direction.ordinal() + 1) % 4)];
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