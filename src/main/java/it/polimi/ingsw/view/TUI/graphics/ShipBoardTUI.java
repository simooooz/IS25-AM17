package it.polimi.ingsw.view.TUI.graphics;

import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.view.TUI.Chroma;

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

    private static final Set<Position> RESERVE_CELL = Set.of(
            new Position(0,5),
            new Position(0,6)
    );

    private boolean isValidPlayableCell(int row, int col) {
        return VALID_PLAYABLE_CELLS.contains(new Position(row, col));
    }

    private boolean isReserveCell(int row, int col) {
        return RESERVE_CELL.contains(new Position(row, col));
    }


    public void printBoard() {
        StringBuilder output = new StringBuilder();

        output.append("   ");
        for (int col = 0; col < cols; col++) {
            output.append(Chroma.color(String.format("      %-2d     ", col + 4), Chroma.RESET, false));
        }
        output.append("\n");

        for (int row = 0; row < rows; row++) {
            // Print each of the component rows, now with 5 rows total instead of 3
            // (1 top row, 3 middle rows instead of 1, 1 bottom row)
            for (int componentRow = 0; componentRow < 5; componentRow++) {
                // Row label for the middle row (second of the three middle rows)
                if (componentRow == 2) {
                    output.append((row + 5)).append("  ");
                } else {
                    output.append("   ");
                }

                for (int col = 0; col < cols; col++) {
                    Position pos = new Position(row, col);

                    boolean isPlayable = isValidPlayableCell(row, col);
                    boolean isReserve = isReserveCell(row, col);

                    if (board.containsKey(pos)) {
                        String[] componentLines = board.get(pos).print().split("\n");

                        if (componentRow == 0) {
                            // Top row
                            output.append(componentLines[0]).append(" ");
                        } else if (componentRow == 4) {
                            // Bottom row
                            output.append(componentLines[2]).append(" ");
                        } else {
                            output.append(componentLines[1]).append(" ");
                        }
                    } else {
                        String bgColor;
                        if (isPlayable) {
                            bgColor = Chroma.PURPLE_BACKGROUND;
                        } else if (isReserve) {
                            bgColor = Chroma.DARKPURPLE_BACKGROUND;
                        } else {
                            bgColor = Chroma.RESET;
                        }
                        // Adjust the cell drawing for 5 rows total
                        if (componentRow == 0) {
                            // Top row
                            output.append(Chroma.bg(" ┌─────────┐ ", bgColor));
                        } else if (componentRow == 4) {
                            // Bottom row
                            output.append(Chroma.bg(" └─────────┘ ", bgColor));
                        } else {
                            // Middle rows (now 3 rows instead of 1)
                            output.append(Chroma.bg(" │         │ ", bgColor));
                        }
                    }

                }

                if (componentRow == 2) {
                    output.append("  ").append(row + 5);
                }

                output.append("\n");
            }
        }

        output.append("   ");
        for (int col = 0; col < cols; col++) {
            output.append(Chroma.color(String.format("      %-2d     ", col + 4), Chroma.RESET, false));
        }
        output.append("\n");

        Chroma.println(output.toString(), Chroma.PURPLE);
    }

    /**
     * Attempts to place a component at the specified position
     * @param component The component to place
     * @param position The position to place it at
     * @return true if placement was successful, false otherwise
     */
//    public boolean placeComponent(ComponentsTUI.ComponentUI component, Position position) {
//        // Check if position is valid
//        if (position.row < 0 || position.row >= rows ||
//                position.col < 0 || position.col >= cols) {
//            return false;
//        }
//
//        // Check if position is empty
//        if (board.containsKey(position)) {
//            return false;
//        }
//
//        // Check if the component would be adjacent to another component (except for the first cabin)
//        if (!board.isEmpty()) {
//            boolean hasAdjacent = false;
//            for (Position adjPos : getAdjacentPositions(position)) {
//                if (board.containsKey(adjPos)) {
//                    hasAdjacent = true;
//                    break;
//                }
//            }
//
//            if (!hasAdjacent) {
//                return false; // Component must connect to existing structure
//            }
//        }
//
//        // Place the component
//        board.put(position, component);
//        return true;
//    }
//
//    /**
//     * Get valid adjacent positions
//     */
//    private Position[] getAdjacentPositions(Position pos) {
//        return new Position[] {
//                new Position(pos.row - 1, pos.col),  // Up
//                new Position(pos.row + 1, pos.col),  // Down
//                new Position(pos.row, pos.col - 1),  // Left
//                new Position(pos.row, pos.col + 1)   // Right
//        };
//    }
//
//    /**
//     * Prompts the user to place a component and handles the interaction
//     */
//    public boolean promptForPlacement(ComponentsTUI.ComponentUI component, Scanner scanner) {
//        boolean placed = false;
//
//        while (!placed) {
//            System.out.println("\nWhere would you like to place " + component.getId() + "?");
//            System.out.print("Enter position (row,col) or 'q' to cancel: ");
//            String input = scanner.nextLine().trim().toUpperCase();
//
//            if (input.equals("Q")) {
//                return false;
//            }
//
//            String[] parts = input.split(",");
//            if (parts.length != 2) {
//                Chroma.println("Invalid position format. Use format like '4,5'.", Chroma.RED);
//                continue;
//            }
//
//            try {
//                int row = Integer.parseInt(parts[0].trim());
//                int col = Integer.parseInt(parts[1].trim());
//
//                Position position = new Position(row, col);
//                placed = placeComponent(component, position);
//
//                if (!placed) {
//                    Chroma.println("Cannot place component there. Try another position.", Chroma.RED);
//                }
//            } catch (NumberFormatException e) {
//                Chroma.println("Invalid numbers. Please enter valid integers.", Chroma.RED);
//            }
//        }
//
//        return true;
//    }


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