package it.polimi.ingsw.client.model.player;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.components.ClientComponent;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.*;

/**
 * Represents a read-only view of a player's ship on the client side.
 * Contains the grid of components and other ship-related data.
 */
public sealed abstract class ClientShip permits
    ClientShipLearnerMode, ClientShipAdvancedMode
{

    private final Optional<ClientComponent>[][] dashboard;
    private ClientComponent componentInHand;
    private final List<ClientComponent> discards;
    private final List<ClientComponent> reserves;

    private final List<List<ClientComponent>> brokenParts;

    @SuppressWarnings("unchecked")
    public ClientShip() {
        this.dashboard = new Optional[Constants.SHIP_ROWS][Constants.SHIP_COLUMNS];
        this.discards = new ArrayList<>();
        this.reserves = new ArrayList<>();
        this.brokenParts = new ArrayList<>();
        this.componentInHand = null;

        for (int row = 0; row < Constants.SHIP_ROWS; row++) {
            for (int col = 0; col < Constants.SHIP_COLUMNS; col++) {
                this.dashboard[row][col] = Optional.empty();
            }
        }
    }

    public Optional<ClientComponent>[][] getDashboard() {
        return dashboard;
    }

    public Optional<ClientComponent> getDashboard(int row, int col) {
        if (row < 0 || col < 0 || row >= dashboard.length || col >= dashboard[0].length) return Optional.empty();
        return dashboard[row][col];
    }

    public List<ClientComponent> getDiscards() {
        return discards;
    }

    public List<ClientComponent> getReserves() {
        return reserves;
    }
    
    public Optional<ClientComponent> getComponentInHand() {
        return Optional.ofNullable(componentInHand);
    }

    public void setComponentInHand(ClientComponent component) {
        this.componentInHand = component;
    }

    public String toString(String username, PlayerState state) {
        StringBuilder output = new StringBuilder();

        switch (state) {
            case BUILD -> {
                output.append(Chroma.color("\nreserves:\n", Chroma.GREY_BOLD)).append(Chroma.color(reserves.isEmpty() ? "none" : Constants.displayComponents(reserves, 2), Chroma.GREY_BOLD)).append("\n\n");
                output.append(Chroma.color(username + "'s ship:\n", Chroma.YELLOW_BOLD));
                printShip(output);
                output.append("\nyour hand:\n").append(componentInHand == null ? "empty" : Constants.displayComponents(new ArrayList<>(List.of(componentInHand)), 1));
            }
            case LOOK_CARD_PILE, CHECK, WAIT_ALIEN, DRAW_CARD, WAIT, WAIT_CANNONS, WAIT_ENGINES, WAIT_GOODS, WAIT_REMOVE_GOODS, WAIT_ROLL_DICES, WAIT_REMOVE_CREW, WAIT_SHIELD, WAIT_BOOLEAN, WAIT_INDEX, DONE -> {
                output.append(Chroma.color(username + "'s ship:\n", Chroma.YELLOW_BOLD));
                printShip(output);
            }
            case WAIT_SHIP_PART -> printShipWithColoredParts(output);
        }

        return output.toString();
    }

    public List<List<ClientComponent>> getBrokenParts() {
        return brokenParts;
    }

    @SuppressWarnings("Duplicates")
    public void printShip(StringBuilder output) {
        output.append("    ");
        for (int col = 0; col < Constants.SHIP_COLUMNS; col++) // Column label
            output.append(Chroma.color(String.format("       %-2d       ", col + 4), Chroma.RESET));
        output.append("\n");

        for (int row = 0; row < Constants.SHIP_ROWS; row++) {
            for (int componentRow = 0; componentRow < 5; componentRow++) {

                if (componentRow == 2) // Row label
                    output.append((row + 5)).append("   ");
                else
                    output.append("    ");

                for (int col = 0; col < Constants.SHIP_COLUMNS; col++) {
                    Optional<ClientComponent> componentOpt = getDashboard(row, col);

                    if (componentOpt.isPresent()) {
                        String[] cellLines = componentOpt.get().toString().split("\n");
                        output.append(cellLines[componentRow]).append(" ");
                    }
                    else {
                        String bgColor = getShipBgColor(row, col);

                        if (componentRow == 0)
                            output.append(Chroma.color(" ┌───────────┐ ", bgColor)).append(" ");
                        else if (componentRow == 4) {
                            output.append(Chroma.color(" └───────────┘ ", bgColor)).append(" ");
                        }
                        else
                            output.append(Chroma.color(" │           │ ", bgColor)).append(" ");
                    }

                }

                if (componentRow == 2) // Row label
                    output.append("  ").append(row + 5);

                output.append("\n");
            }

            output.append("\n");
        }

        output.append("    ");
        for (int col = 0; col < Constants.SHIP_COLUMNS; col++) // Column label
            output.append(Chroma.color(String.format("       %-2d       ", col + 4), Chroma.RESET));
        output.append("\n");
    }

    @SuppressWarnings("Duplicates")
    private void printShipWithColoredParts(StringBuilder output) {
        String[] partColors = {Chroma.ORANGE, Chroma.CYAN, Chroma.MAGENTA, Chroma.MAROON};
        Map<ClientComponent, Integer> componentToGroupIndex = new HashMap<>();
        for (int i = 0; i < brokenParts.size(); i++)
            for (ClientComponent component : brokenParts.get(i))
                componentToGroupIndex.put(component, i);

        output.append("    ");
        for (int col = 0; col < Constants.SHIP_COLUMNS; col++)
            output.append(Chroma.color(String.format("       %-2d       ", col + 4), Chroma.RESET));
        output.append("\n");

        // Print ship grid
        for (int row = 0; row < Constants.SHIP_ROWS; row++) {
            for (int componentRow = 0; componentRow < 5; componentRow++) {

                if (componentRow == 2)
                    output.append((row + 5)).append("   ");
                else
                    output.append("    ");

                for (int col = 0; col < Constants.SHIP_COLUMNS; col++) {
                    Optional<ClientComponent> componentOpt = getDashboard(row, col);

                    if (componentOpt.isPresent()) {
                        // Get group color
                        ClientComponent component = componentOpt.get();
                        Integer groupIndex = componentToGroupIndex.get(component);
                        String partColor = partColors[groupIndex];

                        // Apply color to component
                        String[] cellLines = component.toString().split("\n");
                        String coloredLine = Chroma.color(cellLines[componentRow], partColor);
                        output.append(coloredLine).append(" ");
                    } else {
                        String bgColor = getShipBgColor(row, col);
                        if (componentRow == 0)
                            output.append(Chroma.color(" ┌───────────┐ ", bgColor)).append(" ");
                        else if (componentRow == 4)
                            output.append(Chroma.color(" └───────────┘ ", bgColor)).append(" ");
                        else
                            output.append(Chroma.color(" │           │ ", bgColor)).append(" ");
                    }
                }

                if (componentRow == 2)
                    output.append("  ").append(row + 5);

                output.append("\n");
            }
            output.append("\n");
        }

        output.append("    ");
        for (int col = 0; col < Constants.SHIP_COLUMNS; col++)
            output.append(Chroma.color(String.format("       %-2d       ", col + 4), Chroma.RESET));
        output.append("\n\n");

        // Print color legend
        output.append("    Groups Colors Legend\n");
        for (int i = 0; i < brokenParts.size(); i++) {
            String partColor = partColors[i];
            List<ClientComponent> group = brokenParts.get(i);

            // Show info
            output.append(String.format("Part %d: ", i));
            output.append(Chroma.color("████", partColor));
            output.append(String.format(" (%d components)", group.size()));

            output.append("\n");
        }
    }

    public abstract boolean validPositions(int row, int col);

    public abstract String getShipBgColor(int row, int col);

} 