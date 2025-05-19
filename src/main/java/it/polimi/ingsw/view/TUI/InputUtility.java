package it.polimi.ingsw.view.TUI;

import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

public class InputUtility {
    private static final Scanner scanner = new Scanner(System.in);

    public static <T> T requestInput(
            String message,
            Function<String, T> parser,
            Predicate<T> validator,
            String errorMessage) {

        T result;
        do {
            System.out.print(message);
            try {
                String input = scanner.nextLine();
                result = parser.apply(input);
                if (result == null) return null; // Go back to menù

                if (validator.test(result)) {
                    return result;
                } else {
                    Chroma.println(errorMessage, Chroma.RED);
                }
            } catch (Exception e) {
                Chroma.println(errorMessage, Chroma.RED);
                result = null;
            }
        } while (true);
    }

    public static Integer requestInt(String message, boolean acceptQ, int min, int max) {
        return requestInput(
                message,
                s -> {
                    if (s.equals("q") && acceptQ) return null;
                    return Integer.parseInt(s);
                },
                x -> x >= min && x <= max,
                "not valid"
        );
    }

    public static Double requestDouble(String message, boolean acceptQ, double min, double max) {
        return requestInput(
                message,
                s -> {
                    if (s.equals("q") && acceptQ) return null;
                    return Double.parseDouble(s);
                },
                x -> x >= min && x <= max,
                "Insert a number between " + min + " and " + max + "."
        );
    }

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

    public static Boolean requestBoolean(String message, boolean acceptQ) {
        return requestInput(
                message,
                s -> {
                    s = s.toLowerCase();
                    if (s.equals("q") && acceptQ)
                        return null;
                    if (s.equals("yes") || s.equals("true")) {
                        return true;
                    } else if (s.equals("no") || s.equals("false")) {
                        return false;
                    } else {
                        throw new IllegalArgumentException();
                    }
                },
                x -> true, // Sempre valido una volta convertito
                "Insert yes/no, true/false"
        );
    }

}
