package it.polimi.ingsw;

public abstract class Constants {

    public static final int DEFAULT_SOCKET_PORT = 4030;
    public static final int DEFAULT_RMI_PORT = 1099;
    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int HEARTBEAT_INTERVAL = 2000;
    public static final int SOCKET_TIMEOUT = 4000;

    // GAME CONSTANTS
    public static boolean calcValidPosition(boolean learnerFlight, int row, int col) {
        if (!learnerFlight)
            return !((col < 0 || col > 6) || (row < 0 || row > 5) || (row == 0 && col == 0) || (row == 0 && col == 1) || (row == 0 && col == 3) || (row == 0 && col == 5) || (row == 0 && col == 6) || (row == 1 && col == 0) || (row == 1 && col == 6) || (row == 4 && col == 3));
        return false;
    }

}
