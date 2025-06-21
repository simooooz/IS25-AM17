package it.polimi.ingsw.client.model.components;

import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.common.dto.ShieldComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.DirectionType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;

public final class ClientShieldComponent extends ClientComponent {

    private final DirectionType[] directionsProtected;

    public ClientShieldComponent(int id, ConnectorType[] connectors, DirectionType[] directionsProtected) {
        super(id, connectors);
        this.directionsProtected = directionsProtected;
    }

    public ClientShieldComponent(ShieldComponentDTO dto) {
        super(dto);
        this.directionsProtected = dto.directionsProtected;
    }

    @Override
    public void rotateComponent() {
        super.rotateComponent();
        DirectionType[] directions = DirectionType.values();
        this.directionsProtected[0] = directions[((this.directionsProtected[0].ordinal() + 1) % 4)];
        this.directionsProtected[1] = directions[((this.directionsProtected[1].ordinal() + 1) % 4)];
    }

    @Override
    public void rotateComponent(ClientPlayer player, int rotations) {
        super.rotateComponent(player, rotations);
        DirectionType[] directions = DirectionType.values();
        this.directionsProtected[0] = directions[((this.directionsProtected[0].ordinal() + rotations) % 4)];
        this.directionsProtected[1] = directions[((this.directionsProtected[1].ordinal() + rotations) % 4)];
    }

    @Override
    public List<String> icon() {
        List<String> icon = new ArrayList<>();
        icon.add("    " + "🛡️" + "  \t ");
        if (directionsProtected[0] == DirectionType.NORTH || directionsProtected[1] == DirectionType.NORTH) {
            if (directionsProtected[0] == DirectionType.EAST || directionsProtected[1] == DirectionType.EAST) {

                icon.add(Chroma.color("   ↑   →   ", Chroma.BLUE_BOLD));
            }
            else if (directionsProtected[0] == DirectionType.WEST || directionsProtected[1] == DirectionType.WEST) {
                icon.add(Chroma.color("   ←   ↑   ", Chroma.BLUE_BOLD));
            }
        }
        else if (directionsProtected[0] == DirectionType.SOUTH || directionsProtected[1] == DirectionType.SOUTH){
            if (directionsProtected[0] == DirectionType.EAST || directionsProtected[1] == DirectionType.EAST) {
                icon.add(Chroma.color("   ↓   →   ", Chroma.BLUE_BOLD));
            }
            else if (directionsProtected[0] == DirectionType.WEST || directionsProtected[1] == DirectionType.WEST) {
                icon.add(Chroma.color("   ←   ↓   ", Chroma.BLUE_BOLD));
            }
        }

        return icon;
    }

}