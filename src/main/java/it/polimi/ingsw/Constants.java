package it.polimi.ingsw;

import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.client.model.components.ClientComponent;
import it.polimi.ingsw.network.messages.*;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Constants {

    public static final int DEFAULT_SOCKET_PORT = 4030;
    public static final int DEFAULT_RMI_PORT = 1099;
    public static final int DISCOVERY_PORT = 4040;

    public static final int HEARTBEAT_INTERVAL = 5000;
    public static final int NETWORK_TIMEOUT = 15000;
    public static final int SERVER_CHECK_INTERVAL = 7500;
    public static final int DISCOVERY_TIMEOUT = 3000;

    public static final String DISCOVERY_MESSAGE = "GALAXY_TRUCKER_SERVER_DISCOVERY";
    public static final String DISCOVERY_RESPONSE = "GALAXY_TRUCKER_SERVER_FOUND";

    public static final int MAX_RETRIES = 5;
    public static final int BASE_DELAY = 1000;
    public static final int MAX_DELAY = 30000;

    public static final int SHIP_ROWS = 5;
    public static final int SHIP_COLUMNS = 7;

    public static String repeat(String str, int n) {
        return String.valueOf(str).repeat(Math.max(0, n));
    }

    public static String inTheMiddle(String text, int width) {
        String visibleText = text.replaceAll("\\u001B\\[[;\\d]*m", "");
        int visibleLength = visibleText.length();

        if (visibleLength >= width)
            return text;

        int leftPadding = (width - visibleLength) / 2;
        int rightPadding = width - visibleLength - leftPadding;

        return " ".repeat(leftPadding) +
                text + " ".repeat(rightPadding);
    }

    public static String displayComponents(List<ClientComponent> components, int componentsPerRow) {
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
                output.append("    ");
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

    public static String displayCards(List<ClientCard> cards, int cardsPerRow) {
        StringBuilder output = new StringBuilder();

        for (int rowStart = 0; rowStart < cards.size(); rowStart += cardsPerRow) {
            int rowEnd = Math.min(rowStart + cardsPerRow, cards.size());

            // Collect all components for this row and find max height in row
            String[][] rowComponentLines = new String[rowEnd - rowStart][];
            int rowMaxHeight = 0;

            for (int i = 0; i < rowEnd - rowStart; i++) {
                rowComponentLines[i] = cards.get(rowStart + i).toString().split("\n");
                rowMaxHeight = Math.max(rowMaxHeight, rowComponentLines[i].length);
            }

            // Pad each component to max height
            for (int i = 0; i < rowComponentLines.length; i++) {
                if (rowComponentLines[i].length < rowMaxHeight) {
                    String[] padded = new String[rowMaxHeight];
                    System.arraycopy(rowComponentLines[i], 0, padded, 0, rowComponentLines[i].length);
                    // Fill remaining lines with empty strings of appropriate length
                    int lineLength = rowComponentLines[i].length > 0 ? rowComponentLines[i][0].length() : 0;
                    String emptyLine = " ".repeat(lineLength);
                    for (int j = rowComponentLines[i].length; j < rowMaxHeight; j++) {
                        padded[j] = emptyLine;
                    }
                    rowComponentLines[i] = padded;
                }
            }

            // Print the row line by line
            for (int lineIndex = 0; lineIndex < rowMaxHeight; lineIndex++) {
                output.append("    ");  // Initial indentation
                for (int compIndex = 0; compIndex < rowComponentLines.length; compIndex++) {
                    output.append(rowComponentLines[compIndex][lineIndex]);

                    // Add spacing between components, except after the last one
                    if (compIndex < rowComponentLines.length - 1) {
                        output.append("        ");
                    }
                }
                output.append("\n");
            }
        }
        return output.toString();
    }

    public static Message createMessage(MessageType gameEvent, Object... args) {
        Message message;
        switch (args.length) {
            case 0 -> message = new ZeroArgMessage(gameEvent);
            case 1 -> message = new SingleArgMessage<>(gameEvent, args[0]);
            case 2 -> message = new DoubleArgMessage<>(gameEvent, args[0], args[1]);
            case 3 -> message = new TripleArgMessage<>(gameEvent, args[0], args[1], args[2]);
            case 4 -> message = new QuadrupleArgMessage<>(gameEvent, args[0], args[1], args[2], args[3]);
            default -> message = new SingleArgMessage<>(MessageType.ERROR, "Unknown message");
        }
        return message;
    }

    public static String getIPv4Address() throws SocketException {
        String address = null;

        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface ni = networkInterfaces.nextElement();

            // Ignore inactive and virtual interfaces
            if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                continue;
            }

            Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress ia = inetAddresses.nextElement();

                // Find IPv4 address
                if (ia instanceof java.net.Inet4Address &&
                        !ia.isLoopbackAddress() &&
                        !ia.isLinkLocalAddress()) {
                    address = ia.getHostAddress();
                    break;
                }
            }
            if (address != null) {
                break; // Address found
            }
        }

        return address;
    }

    public static boolean isValidIPv4(String ip) {
        if (ip == null || ip.isEmpty())
            return false;
        String ipv4Regex = "^(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

        Pattern pattern = Pattern.compile(ipv4Regex);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

}
