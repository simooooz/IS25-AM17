package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.controller.MatchController;

import java.util.HashMap;

public class TUIColors {

    public static final String RESET = "\033[0m";  // reset

    // regular
    public static final String BLACK = "\033[0;30m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String YELLOW = "\033[0;33m";
    public static final String BLUE = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String CYAN = "\033[0;36m";
    public static final String WHITE = "\033[0;37m";

    // bold
    public static final String BLACK_BOLD = "\033[1;30m";
    public static final String RED_BOLD = "\033[1;31m";
    public static final String GREEN_BOLD = "\033[1;32m";
    public static final String YELLOW_BOLD = "\033[1;33m";
    public static final String BLUE_BOLD = "\033[1;34m";
    public static final String PURPLE_BOLD = "\033[1;35m";
    public static final String CYAN_BOLD = "\033[1;36m";
    public static final String WHITE_BOLD = "\033[1;37m";

    // background
    public static final String BLACK_BACKGROUND = "\033[40m";
    public static final String RED_BACKGROUND = "\033[41m";
    public static final String GREEN_BACKGROUND = "\033[42m";
    public static final String YELLOW_BACKGROUND = "\033[43m";
    public static final String BLUE_BACKGROUND = "\033[44m";
    public static final String PURPLE_BACKGROUND = "\033[45m";
    public static final String CYAN_BACKGROUND = "\033[46m";
    public static final String WHITE_BACKGROUND = "\033[47m";// grigio scuro
    public static final String DARK_PURPLE_BACKGROUND = "\u001B[48;5;234m";

    private TUIColors() {}

    /**
     * colored System.out.println
     *
     * @param text to print
     * @param color text color
     */
    public static void printlnColored(String text, String color) {
        System.out.println(color + text + RESET);
    }

    public static void printColored(String text, String color) {
        System.out.print(color + text + RESET);
    }

    // method to check if the terminal supports ANSI colors
    public static boolean supportsColors() {
        String term = System.getenv("TERM");
        return term != null && !term.equals("dumb") ||
                System.console() != null;
    }

}
