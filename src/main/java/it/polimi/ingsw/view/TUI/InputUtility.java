package it.polimi.ingsw.view.TUI;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utility class for handling user input in the Text User Interface (TUI).
 * This class provides robust input validation and parsing methods for various data types,
 * with support for error handling, retry mechanisms, and optional quit functionality.
 *
 * @see Scanner
 * @see Function
 * @see Predicate
 */
public class InputUtility {

    /**
     * Shared Scanner instance for reading user input from System.in.
     * This scanner is reused across all input methods to maintain consistency.
     */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Generic input request method with custom parsing and validation.
     * This is the core method that handles input collection, parsing, validation,
     * and error recovery for any data type.
     *
     */
    public static <T> T requestInput(
            String message,
            Function<String, T> parser,
            Predicate<T> validator,
            String errorMessage) {

        T result;
        System.out.print(message);
        do {
            try {
                String input = scanner.nextLine();
                result = parser.apply(input);
                if (result == null) return null; // Go back to menù

                if (validator.test(result)) {
                    return result;
                } else {
                    Chroma.println(errorMessage, Chroma.RED);
                    System.out.print("> ");
                }
            } catch (Exception e) {
                Chroma.println(errorMessage, Chroma.RED);
                System.out.print("> ");
            }
        } while (true);
    }

    /**
     * Requests an integer input from the user with range validation.
     * The method validates that the input is a valid integer within the specified range.
     *
     * @param message the prompt message to display to the user
     * @param acceptQ whether to accept 'q' as input to quit (returns null)
     * @param min the minimum acceptable value (inclusive)
     * @param max the maximum acceptable value (inclusive)
     * @return the validated integer input, or null if 'q' was entered and acceptQ is true
     * @throws NumberFormatException if the input cannot be parsed as an integer
     *         (handled internally with error message display)
     */
    public static Integer requestInt(String message, boolean acceptQ, int min, int max) {
        return requestInput(
                message,
                s -> {
                    if (s.equals("q") && acceptQ) return null;
                    return Integer.parseInt(s);
                },
                x -> x >= min && x <= max,
                "Insert a number between " + min + " and " + max + "."
        );
    }

    /**
     * Requests a string input from the user with length validation.
     * The method validates that the input string length is within the specified bounds.
     *
     * @param message the prompt message to display to the user
     * @param acceptQ whether to accept 'q' as input to quit (returns null)
     * @param minLength the minimum acceptable string length (inclusive)
     * @param maxLength the maximum acceptable string length (inclusive)
     * @return the validated string input, or null if 'q' was entered and acceptQ is true
     */
    public static String requestString(String message, boolean acceptQ, int minLength, int maxLength) {
        return requestInput(
                message,
                s -> {
                    if (s.equals("q") && acceptQ) return null;
                    return s;
                },
                s -> s.length() >= minLength && s.length() <= maxLength,
                "Insert a string with length between " + minLength + " and " + maxLength + " characters."
        );
    }

    /**
     * Requests a boolean input from the user with flexible format support.
     * The method accepts multiple formats for boolean values and is case-insensitive.
     *
     * <p>Accepted formats for {@code true}:
     * <ul>
     * <li>"yes", "y", "true" (case-insensitive)</li>
     * </ul>
     *
     * <p>Accepted formats for {@code false}:
     * <ul>
     * <li>"no", "n", "false" (case-insensitive)</li>
     * </ul>
     *
     * @param message the prompt message to display to the user
     * @param acceptQ whether to accept 'q' as input to quit (returns null)
     * @return the parsed boolean value, or null if 'q' was entered and acceptQ is true
     * @throws IllegalArgumentException if the input doesn't match any accepted boolean format
     *         (handled internally with error message display)
     */
    public static Boolean requestBoolean(String message, boolean acceptQ) {
        return requestInput(
                message,
                s -> {
                    s = s.toLowerCase();
                    if (s.equals("q") && acceptQ)
                        return null;
                    if (s.equals("yes") || s.equals("true") || s.equals("y")) {
                        return true;
                    } else if (s.equals("no") || s.equals("false") || s.equals("n")) {
                        return false;
                    } else {
                        throw new IllegalArgumentException();
                    }
                },
                _ -> true,
                "Insert yes/no, true/false"
        );
    }

}