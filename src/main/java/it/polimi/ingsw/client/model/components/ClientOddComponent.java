package it.polimi.ingsw.client.model.components;

import it.polimi.ingsw.common.dto.OddComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;

public final class ClientOddComponent extends ClientComponent {

    private final AlienType type;

    public ClientOddComponent(int id, ConnectorType[] connectors, AlienType type) {
        super(id, connectors);
        this.type = type;
    }

    public ClientOddComponent(OddComponentDTO dto) {
        super(dto);
        this.type = dto.type;
    }

    public AlienType getType() {
        return type;
    }

    @Override
    public List<String> icon() {
        String color = type.equals(AlienType.CANNON) ? Chroma.PURPLE_BOLD : Chroma.ORANGE_BOLD;
        return new ArrayList<>(List.of(
                Chroma.color("│   " + "🛸" + "  \t│", color),
                Chroma.color("└─────────┘", color)));
    }

} 