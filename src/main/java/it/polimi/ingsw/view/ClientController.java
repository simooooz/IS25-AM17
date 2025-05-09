package it.polimi.ingsw.view;

import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.messages.SingleArgMessage;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.network.socket.client.NetworkEventListener;
import it.polimi.ingsw.view.TUI.TUIColors;
import it.polimi.ingsw.view.TUI.UIState;
import it.polimi.ingsw.view.TUI.ViewTui;

public class ClientController implements NetworkEventListener {

    private final ClientSocket client;
    private final ViewTui viewTui;
//    private ViewGui viewGui;

    private Lobby currentLobby;

    /**
     * TUI
     */
    public ClientController(ClientSocket client) {
        this.client = client;

        // observer
        this.client.getUser().addNetworkEventListener(this);

        this.viewTui = new ViewTui(this);
        this.viewTui.addNetworkEventListener(this);
        viewTui.start();
    }

//    /**
//     * GUI
//     */
//    public ClientController(ClientSocket client, ViewGui view) {
//
//    }


    public Lobby getLobby() {
        return currentLobby;
    }

    /**
     * Send a message to the server
     *
     * @param message to be sent
     */
    public void sendMessage(Message message) {
        client.getUser().send(message);
    }

    /**
     * Close the connection with the server
     */
    // todo: maybe not necessary, or disconnetion can be managed in another way
    public void closeConnection() {
        client.close();
    }

    @Override
    public void onMessageReceived(Message message) {
        MessageType type = message.getMessageType();

        switch (type) {
            case DISCONNECT_OK:
                viewTui.setState(UIState.DICONNECT);
                break;

            case MessageType.USERNAME_OK:
                viewTui.setState(UIState.LOBBY_SELECTION);
                break;
            case MessageType.USERNAME_ALREADY_TAKEN:
                TUIColors.printlnColored("username already taken", TUIColors.RED);
                viewTui.handleUIState();
                break;

            case MessageType.CREATE_LOBBY_OK:
            case MessageType.JOIN_LOBBY_OK:
                SingleArgMessage<Lobby> lobbyMsg = (SingleArgMessage<Lobby>) message;
                currentLobby = lobbyMsg.getArg1();
                viewTui.setState(UIState.IN_LOBBY);
                break;

            case MessageType.LOBBY_UPDATE_OK:
                SingleArgMessage<Lobby> lobbyUpdateMsg = (SingleArgMessage<Lobby>) message;
                currentLobby = lobbyUpdateMsg.getArg1();
                viewTui.handleUIState();
                break;

            case MessageType.START_GAME_OK:
                viewTui.setState(UIState.IN_GAME);
                break;
            case MessageType.LEAVE_GAME_OK:
                viewTui.setState(UIState.LOBBY_SELECTION);
                break;

            case ERROR:
                viewTui.handleUIState();

            default:
                break;
        }
    }

    @Override
    public void onConnectionEstablished() {
        TUIColors.printlnColored("Connected to server successfully!", TUIColors.GREEN_BOLD);
    }

    @Override
    public void onConnectionClosed(String reason) {
        TUIColors.printlnColored("Connection closed: " + reason, TUIColors.YELLOW_BOLD);
        System.exit(1);
    }

    @Override
    public void onUIStateChanged() {
        viewTui.handleUIState();
    }

}
