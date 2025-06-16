package it.polimi.ingsw.client.model.components;

import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientCabinComponent extends ClientComponent {

    private AlienType alien;
    private int humans;
    private final boolean isStarting;

    public ClientCabinComponent(int id, ConnectorType[] connectors, boolean isStarting) {
        super(id, connectors);
        this.alien = null;
        this.humans = 2;
        this.isStarting = isStarting;
    }

    public void setAlien(AlienType alien) {
        this.alien = alien;
    }

    public void setHumans(int humans) {
        this.humans = humans;
    }

    @Override
    public void insertComponent(ClientPlayer player, int row, int col, int rotations, boolean weld) {
        if (isStarting) {
            this.setShown(true);
            this.setX(col);
            this.setY(row);
            player.getShip().getDashboard()[row][col] = Optional.of(this);
            this.setInserted();
        }
        else
            super.insertComponent(player, row, col, rotations, weld);
    }

    @Override
    public List<String> icon() {
        if (alien == null) {
            return new ArrayList<>(List.of(
                    Chroma.color("│   " + "👨" + "  \t│", Chroma.GREY_BOLD),
                    Chroma.color("└─  " + humans + "/2  ─┘", Chroma.GREY_BOLD))
            );
        }
        else {
            String color = alien.equals(AlienType.CANNON) ? Chroma.PURPLE_BOLD : Chroma.ORANGE_BOLD;
            return new ArrayList<>(List.of(
                    Chroma.color("│   " + "👽" + "  \t│", color),
                    Chroma.color("└─────────┘", color))
            );
        }
    }

} 