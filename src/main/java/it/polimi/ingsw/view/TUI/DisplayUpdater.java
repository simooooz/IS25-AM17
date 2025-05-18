package it.polimi.ingsw.view.TUI;

import it.polimi.ingsw.network.Client;

public class DisplayUpdater implements Runnable {

    private final Client client;

    public DisplayUpdater(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                String message = client.getViewTui().getNetworkMessageQueue().poll();
                if (message != null) {
                    if (message.equals("ERROR")) {
                        displayError();
                    }
                    else {
                        client.getViewTui().clear();
                        updateDisplay();
                    }
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // Nothing to do
            }
        }
    }

    public void updateDisplay() {
        switch (client.getState()) {
            case DISCONNECT:
                client.getViewTui().handleDisconnect();
                break;
            case USERNAME:
                System.out.print("Username: ");
                break;
            case LOBBY_SELECTION:
                displayLobbySelection();
                break;
            case IN_LOBBY:
                displayLobbyInfo();
                break;
            case IN_GAME:
                displayGame();
                break;
        }
    }

    private void displayLobbyInfo() {
        System.out.println("✅ Lobby ID: " + client.getLobby().getGameID());
        System.out.println((client.getLobby().isLearnerMode() ? "🔵" : "🟣").concat(" Game Mode: ".concat(client.getLobby().isLearnerMode() ? "Learner Flight" : "Standard")));
        System.out.println("👥 " + client.getLobby().getPlayers().size() + "/" + client.getLobby().getMaxPlayers() + " players:");

        for (String player : client.getLobby().getPlayers()) {
            System.out.println("- " + player);
        }

        Chroma.println("\n\nWaiting for players to join in...", Chroma.WHITE_BOLD);
        System.out.println("Press 'q' to go back to the menu.");
    }

    private void displayLobbySelection() {
        Chroma.println("MENU", Chroma.WHITE_BOLD);
        System.out.println("1. Create a new lobby");
        System.out.println("2. Join a lobby");
        System.out.println("3. Join in a random lobby");
        System.out.println("4. Quit the game");
        System.out.print("\nChoose an option (1-4): ");
    }

    private void displayGame() {
        System.out.println("Gioco!");
    }

    public void displayError() {
        Chroma.println("Remote error :/ please try again", Chroma.RED);
    }

}
