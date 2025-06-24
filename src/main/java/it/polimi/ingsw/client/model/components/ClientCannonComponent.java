package it.polimi.ingsw.client.model.components;

import it.polimi.ingsw.common.dto.CannonComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.DirectionType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;

public final class ClientCannonComponent extends ClientComponent {

    private DirectionType direction;
    private final boolean isDouble;

    public ClientCannonComponent(int id, ConnectorType[] connectors, DirectionType direction, boolean isDouble) {
        super(id, connectors);
        this.direction = direction;
        this.isDouble = isDouble;
    }

    public ClientCannonComponent(CannonComponentDTO dto) {
        super(dto);
        this.direction = dto.direction;
        this.isDouble = dto.isDouble;
    }

    @Override
    public void rotateComponent() {
        super.rotateComponent();
        DirectionType[] directions = DirectionType.values(); // NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3
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
                Chroma.color("┌    " + arrow + "    ┐", Chroma.PURPLE),
                isDouble ? Chroma.color("│  💥" + "💥\t│", Chroma.PURPLE)
                        : Chroma.color("│   " + "💥" + "  \t│", Chroma.PURPLE)
        ));
    }

}