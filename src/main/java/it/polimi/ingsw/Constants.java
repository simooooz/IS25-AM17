package it.polimi.ingsw;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.network.messages.*;

import java.util.List;

public abstract class Constants {

    public static final int DEFAULT_SOCKET_PORT = 4030;
    public static final int DEFAULT_RMI_PORT = 1099;
    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int HEARTBEAT_INTERVAL = 2000;
    public static final int SOCKET_TIMEOUT = 4000;

    public static final int DISCOVERY_PORT = 4040;
    public static final String DISCOVERY_MESSAGE = "GALAXY_TRUCKER_SERVER_DISCOVERY";
    public static final String DISCOVERY_RESPONSE = "GALAXY_TRUCKER_SERVER_FOUND";
    public static final int DISCOVERY_TIMEOUT = 5000;

    public static final int SHIP_ROWS = 5;
    public static final int SHIP_COLUMNS = 7;

    public static String repeat(String str, int n) {
        return String.valueOf(str).repeat(Math.max(0, n));
    }

    public static String inTheMiddle(String text, int width) {
        // Rimuovi eventuali caratteri di controllo ANSI o altri caratteri speciali per il calcolo della lunghezza visiva
        String visibleText = text.replaceAll("\\u001B\\[[;\\d]*m", "");
        int visibleLength = visibleText.length();

        if (visibleLength >= width) {
            return text; // Restituisci il testo originale completo (con i codici di colore)
        }

        int leftPadding = (width - visibleLength) / 2;
        int rightPadding = width - visibleLength - leftPadding;

        // Crea la stringa centrata
        StringBuilder centeredText = new StringBuilder();
        centeredText.append(" ".repeat(leftPadding));
        centeredText.append(text); // Aggiungi il testo originale con eventuali codici di colore
        centeredText.append(" ".repeat(rightPadding));

        return centeredText.toString();
    }

    public static String displayComponents(List<Component> components, int componentsPerRow) {
        StringBuilder output = new StringBuilder();

        for (int rowStart = 0; rowStart < components.size(); rowStart += componentsPerRow) {
            int rowEnd = Math.min(rowStart + componentsPerRow, components.size());

            // Collect all component for this row
            String[][] rowComponentLines = new String[rowEnd - rowStart][];

            for (int i = 0; i < rowEnd - rowStart; i++)
                rowComponentLines[i] = components.get(rowStart + i).toString().split("\n");

            // Print the row line by line
            int height = rowComponentLines[0].length;
            for (int lineIndex = 0; lineIndex < height; lineIndex++) {
                for (int compIndex = 0; compIndex < rowComponentLines.length; compIndex++) {
                    output.append(rowComponentLines[compIndex][lineIndex]);

                    // Add spacing between components, except after the last one
                    if (compIndex < rowComponentLines.length - 1)
                        output.append(" ");
                }
                output.append("\n");
            }
        }
        return output.toString();
    }

    public static String displayCards(List<Card> cards, int componentsPerRow) {
        StringBuilder output = new StringBuilder();

        for (int rowStart = 0; rowStart < cards.size(); rowStart += componentsPerRow) {
            int rowEnd = Math.min(rowStart + componentsPerRow, cards.size());

            // Collect all component for this row
            String[][] rowComponentLines = new String[rowEnd - rowStart][];

            for (int i = 0; i < rowEnd - rowStart; i++)
                rowComponentLines[i] = cards.get(rowStart + i).toString().split("\n");

            // Print the row line by line
            int height = rowComponentLines[0].length;
            for (int lineIndex = 0; lineIndex < height; lineIndex++) {
                for (int compIndex = 0; compIndex < rowComponentLines.length; compIndex++) {
                    output.append(rowComponentLines[compIndex][lineIndex]);

                    // Add spacing between components, except after the last one
                    if (compIndex < rowComponentLines.length - 1)
                        output.append("  ");
                }
                output.append("\n");
            }
        }
        return output.toString();
    }

    // Socket only
    public static Message createMessage(MessageType gameEvent, Object... args) {
        Message message;
        switch (args.length) {
            case 0 -> message = new ZeroArgMessage(gameEvent);
            case 1 -> message = new SingleArgMessage<>(gameEvent, args[0]);
            case 2 -> message = new DoubleArgMessage<>(gameEvent, args[0], args[1]);
            case 3 -> message = new TripleArgMessage<>(gameEvent, args[0], args[1], args[2]);
            case 4 -> message = new QuadrupleArgMessage<>(gameEvent, args[0], args[1], args[2], args[3]);
            case 5 -> message = new QuintupleArgMessage<>(gameEvent, args[0], args[1], args[2], args[3], args[4]);
            default -> message = new ErrorMessage("Unknown message");
        }
        return message;
    }


}
