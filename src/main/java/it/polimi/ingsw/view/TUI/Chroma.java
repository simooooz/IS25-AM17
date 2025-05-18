package it.polimi.ingsw.view.TUI;

/**
 * The Chroma class provides ANSI color codes for terminal output. It supports text
 * formatting such as regular, bold, and background colors, enabling users to print
 * terminal output in various styles.
 *
 * This class also includes utility methods for printing with colors, combining
 * text and colors, and checking if the terminal supports ANSI colors.
 */
public class Chroma {

    public static final String RESET = "\033[0m";  // reset

    // regular
    public static final String BLACK = "\033[0;30m";
    public static final String WHITE = "\033[0;37m";

    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";

    public static final String ORANGE = "\033[38;5;214m";
    public static final String PURPLE = "\033[0;35m";

    public static final String CYAN = "\033[0;36m";
    public static final String BLUE = "\033[0;34m";

    // bold
    public static final String BLACK_BOLD = "\033[1;30m";
    public static final String WHITE_BOLD = "\033[1;37m";

    public static final String RED_BOLD = "\033[1;31m";
    public static final String GREEN_BOLD = "\033[1;32m";

    public static final String ORANGE_BOLD = "\033[1;38;5;214m";
    public static final String PURPLE_BOLD = "\033[1;35m";

    public static final String CYAN_BOLD = "\033[1;36m";

    // background
    public static final String BLACK_BACKGROUND = "\033[40m";
    public static final String WHITE_BACKGROUND = "\033[47m";

    public static final String RED_BACKGROUND = "\033[41m";
    public static final String GREEN_BACKGROUND = "\033[42m";

    public static final String ORANGE_BACKGROUND = "\033[48;5;214m";

    public static final String DARKPURPLE_BACKGROUND = "\033[48;2;38;21;70m";
    public static final String PURPLE_BACKGROUND = "\033[48;5;97m";// DARK PURPLE

    public static final String DARKBLUE_BACKGROUND = "\033[48;5;18m";    // DARK BLUE
    public static final String BLUE_BACKGROUND = "\033[48;5;25m";

    public static final String CYAN_BACKGROUND = "\033[46m";




    private Chroma() {}

    /**
     * colored System.out.println
     *
     * @param text to print
     * @param color text color
     */
    public static void println(String text, String color) {
        System.out.println(color + text + RESET);
    }

    /**
     * colored System.out.print
     *
     * @param text to print
     * @param color text color
     */
    public static void print(String text, String color) {
        System.out.print(color + text + RESET);
    }

    public static String color(String text, String color, boolean bold) {
        return (bold ? "\033[1;" : "") + color + text + RESET;
    }

    public static String bg(String text, String bg) {
        return bg + text + RESET;
    }


    // method to check if the terminal supports ANSI colors
    public static boolean supportsColors() {
        String term = System.getenv("TERM");
        return term != null && !term.equals("dumb") ||
                System.console() != null;
    }

}
