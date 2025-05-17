package it.polimi.ingsw.view.TUI.graphics;

import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.view.TUI.TUIColors;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ShipBoardTUI {
    private final Ship ship;
    private final Map<Position, ComponentsTUI.ComponentUI> board;
    private final int rows;
    private final int cols;

    public ShipBoardTUI(Ship ship, int rows, int cols) {
        this.ship = ship;
        this.rows = rows;
        this.cols = cols;
        this.board = new HashMap<>();

        // Prepopulate with central cabin if using default ship layout
        if (ship != null) {
            // todo: adding the starting cabin
        }
    }

    private static final Set<Position> VALID_PLAYABLE_CELLS = Set.of(
            // row 0
            new Position(4, 0),
            new Position(4, 1),
            new Position(4, 2),
            new Position(4, 4),
            new Position(4, 5),
            new Position(4, 6),

            // row 1
            new Position(3, 0),
            new Position(3, 1),
            new Position(3, 2),
            new Position(3, 3),
            new Position(3, 4),
            new Position(3, 5),
            new Position(3, 6),

            // row 2
            new Position(2, 0),
            new Position(2, 1),
            new Position(2, 2),
            new Position(2, 3),
            new Position(2, 4),
            new Position(2, 5),
            new Position(2, 6),

            // row 3
            new Position(1, 1),
            new Position(1, 2),
            new Position(1, 3),
            new Position(1, 4),
            new Position(1, 5),

            // row 4
            new Position(0, 2),
            new Position(0, 4)
    );

    private boolean isValidPlayableCell(int row, int col) {
        return VALID_PLAYABLE_CELLS.contains(new Position(row, col));
    }


    public void printBoard() {
        StringBuilder output = new StringBuilder();

        output.append("   ");
        for (int col = 0; col < cols; col++) {
            output.append(String.format("  %-2d   ", col));
        }
        output.append("\n");

        for (int row = 0; row < rows; row++) {
            // Print each of the 3 component rows
            for (int componentRow = 0; componentRow < 3; componentRow++) {
                // Row label for the middle row (also starts from 4)
                if (componentRow == 1) {
                    output.append((row)).append("  ");
                } else {
                    output.append("   ");
                }

                for (int col = 0; col < cols; col++) {
                    Position pos = new Position(row, col);

                    boolean isPlayable = isValidPlayableCell(row, col);

                    if (board.containsKey(pos)) {
                        String[] componentLines = board.get(pos).print().split("\n");
                        output.append(componentLines[componentRow]).append(" ");
                    } else {
                        String bgColor = isPlayable ? TUIColors.PURPLE_BACKGROUND : TUIColors.DARK_PURPLE_BACKGROUND;

                        if (componentRow == 0) {
                            output.append(bgColor + "┌──────┐" + TUIColors.RESET + " ");
                        } else if (componentRow == 1) {
                            output.append(bgColor + "│      │" + TUIColors.RESET + " ");
                        } else {
                            output.append(bgColor + "└──────┘" + TUIColors.RESET + " ");
                        }
                    }
                }
                output.append("\n");
            }
        }

        TUIColors.printlnColored(output.toString(), TUIColors.PURPLE);
    }



    /**
     * Attempts to place a component at the specified position
     * @param component The component to place
     * @param position The position to place it at
     * @return true if placement was successful, false otherwise
     */
    public boolean placeComponent(ComponentsTUI.ComponentUI component, Position position) {
        // Check if position is valid
        if (position.row < 0 || position.row >= rows ||
                position.col < 0 || position.col >= cols) {
            return false;
        }

        // Check if position is empty
        if (board.containsKey(position)) {
            return false;
        }

        // Check if the component would be adjacent to another component (except for the first cabin)
        if (!board.isEmpty()) {
            boolean hasAdjacent = false;
            for (Position adjPos : getAdjacentPositions(position)) {
                if (board.containsKey(adjPos)) {
                    hasAdjacent = true;
                    break;
                }
            }

            if (!hasAdjacent) {
                return false; // Component must connect to existing structure
            }
        }

        // Place the component
        board.put(position, component);
        return true;
    }

    /**
     * Get valid adjacent positions
     */
    private Position[] getAdjacentPositions(Position pos) {
        return new Position[] {
                new Position(pos.row - 1, pos.col),  // Up
                new Position(pos.row + 1, pos.col),  // Down
                new Position(pos.row, pos.col - 1),  // Left
                new Position(pos.row, pos.col + 1)   // Right
        };
    }

    /**
     * Prompts the user to place a component and handles the interaction
     */
    public boolean promptForPlacement(ComponentsTUI.ComponentUI component, Scanner scanner) {
        boolean placed = false;

        while (!placed) {
            System.out.println("\nWhere would you like to place " + component.getId() + "?");
            System.out.print("Enter position (row,col) or 'q' to cancel: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("Q")) {
                return false;
            }

            String[] parts = input.split(",");
            if (parts.length != 2) {
                TUIColors.printlnColored("Invalid position format. Use format like '4,5'.", TUIColors.RED);
                continue;
            }

            try {
                int row = Integer.parseInt(parts[0].trim());
                int col = Integer.parseInt(parts[1].trim());

                Position position = new Position(row, col);
                placed = placeComponent(component, position);

                if (!placed) {
                    TUIColors.printlnColored("Cannot place component there. Try another position.", TUIColors.RED);
                }
            } catch (NumberFormatException e) {
                TUIColors.printlnColored("Invalid numbers. Please enter valid integers.", TUIColors.RED);
            }
        }

        return true;
    }


    /**
     * Simple class to represent a position on the board
     */
    public static class Position {
        final int row;
        final int col;

        public Position(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return row == position.row && col == position.col;
        }

        @Override
        public int hashCode() {
            return 31 * row + col;
        }
    }
}