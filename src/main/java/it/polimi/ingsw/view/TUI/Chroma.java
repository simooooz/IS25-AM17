package it.polimi.ingsw.view.TUI;

/**
 * Utility class providing ANSI color codes and formatting for terminal output.
 * This class offers a comprehensive set of color constants and utility methods to enhance
 * the visual appearance of text-based user interfaces in terminal environments.
 */
public class Chroma {

    /** ANSI reset code to clear all formatting and return to default terminal appearance. */
    public static final String RESET = "\033[0m";  // reset

    // Regular text colors
    /** ANSI code for black text color. */
    public static final String BLACK = "\033[0;30m";
    /** ANSI code for red text color. */
    public static final String RED = "\033[0;31m";
    /** ANSI code for green text color. */
    public static final String GREEN = "\033[0;32m";
    /** ANSI code for dark green text color (256-color mode). */
    public static final String DARK_GREEN = "\033[38;5;22m";
    /** ANSI code for blue text color. */
    public static final String BLUE = "\033[0;34m";
    /** ANSI code for yellow text color. */
    public static final String YELLOW = "\033[0;33m";
    /** ANSI code for brown text color (256-color mode). */
    public static final String BROWN = "\033[38;5;94m";
    /** ANSI code for brown background color (256-color mode). */
    public static final String BROWN_BACKGROUND = "\033[48;5;94m";
    /** ANSI code for orange text color (256-color mode). */
    public static final String ORANGE = "\033[38;5;214m";
    /** ANSI code for purple text color. */
    public static final String PURPLE = "\033[0;35m";
    /** ANSI code for cyan text color. */
    public static final String CYAN = "\033[0;36m";
    /** ANSI code for magenta text color. */
    public static final String MAGENTA = "\033[0;35m";
    /** ANSI code for maroon text color (256-color mode). */
    public static final String MAROON = "\033[38;5;88m";

    // Bold text colors
    /** ANSI code for bold black text color. */
    public static final String BLACK_BOLD = "\033[1;30m";
    /** ANSI code for bold grey text color. */
    public static final String GREY_BOLD = "\033[1;37m";
    /** ANSI code for bold red text color. */
    public static final String RED_BOLD = "\033[1;31m";
    /** ANSI code for bold green text color. */
    public static final String GREEN_BOLD = "\033[1;32m";
    /** ANSI code for bold blue text color. */
    public static final String BLUE_BOLD = "\033[1;34m";
    /** ANSI code for bold yellow text color. */
    public static final String YELLOW_BOLD = "\033[1;33m";
    /** ANSI code for bold orange text color (256-color mode). */
    public static final String ORANGE_BOLD = "\033[1;38;5;214m";
    /** ANSI code for bold purple text color. */
    public static final String PURPLE_BOLD = "\033[1;35m";

    // Background colors
    /** ANSI code for black background color. */
    public static final String BLACK_BACKGROUND = "\033[40m";
    /** ANSI code for white background color (RGB mode). */
    public static final String WHITE_BACKGROUND = "\033[48;2;255;255;255m";
    /** ANSI code for red background color. */
    public static final String RED_BACKGROUND = "\033[41m";
    /** ANSI code for dark green background color (256-color mode). */
    public static final String DARK_GREEN_BACKGROUND = "\033[48;5;22m";
    /** ANSI code for blue background color (256-color mode). */
    public static final String BLUE_BACKGROUND = "\033[48;5;25m";
    /** ANSI code for dark blue background color (256-color mode). */
    public static final String DARKBLUE_BACKGROUND = "\033[48;5;18m";
    /** ANSI code for yellow background color. */
    public static final String YELLOW_BACKGROUND = "\033[43m";
    /** ANSI code for light grey background color (256-color mode). */
    public static final String LIGHT_GREY_BACKGROUND = "\033[48;5;250m";
    /** ANSI code for dark grey background color (256-color mode). */
    public static final String DARK_GREY_BACKGROUND = "\033[48;5;240m";
    /** ANSI code for orange background color (256-color mode). */
    public static final String ORANGE_BACKGROUND = "\033[48;5;214m";
    /** ANSI code for purple background color (256-color mode). */
    public static final String PURPLE_BACKGROUND = "\033[48;5;97m";
    /** ANSI code for dark purple background color (RGB mode). */
    public static final String DARKPURPLE_BACKGROUND = "\033[48;2;38;21;70m";

    private Chroma() {
    }

    /**
     * Prints text to standard output with the specified color, followed by a newline.
     * The text is automatically reset to default formatting after printing.
     *
     * @param text the text to print
     * @param color the ANSI color code to apply to the text (use class constants)
     * @see System#out
     */
    public static void println(String text, String color) {
        System.out.println(color + text + RESET);
    }

    /**
     * Prints text to standard output with the specified color, without adding a newline.
     * The text is automatically reset to default formatting after printing.
     *
     * @param text the text to print
     * @param color the ANSI color code to apply to the text (use class constants)
     * @see System#out
     */
    public static void print(String text, String color) {
        System.out.print(color + text + RESET);
    }

    /**
     * Returns a string with the specified text wrapped in the given color formatting.
     * The returned string includes the color code, the text, and the reset code.
     * This method is useful for creating colored strings that can be stored or
     * concatenated with other text before printing.
     *
     * @param text the text to colorize
     * @param color the ANSI color code to apply to the text (use class constants)
     * @return a formatted string containing the colored text with proper reset codes
     */
    public static String color(String text, String color) {
        return color + text + RESET;
    }

}