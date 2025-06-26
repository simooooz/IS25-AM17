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
