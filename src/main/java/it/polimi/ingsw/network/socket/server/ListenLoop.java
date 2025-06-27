package it.polimi.ingsw.network.socket.server;

import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.socket.Heartbeat;
import it.polimi.ingsw.network.socket.Sense;

/**
 * Server-side thread that listens for incoming messages from a client.
 * Handles heartbeat responses and processes regular messages.
 */
public class ListenLoop extends Thread {

    /**
     * Client handler to listen on
     */
    private final ClientHandler clientHandler;

    /**
     * Creates and starts the listening thread.
     *
     * @param clientHandler the client handler to monitor for incoming messages
     */
    public ListenLoop(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.start();
    }

    /**
     * Continuously reads messages from client.
     * Responds to heartbeats with Sense objects and forwards other messages for processing.
     */
    @Override
    public void run() {
        while (!Thread.interrupted()) {

            try {
                Object read = clientHandler.readObject();

                if (!(read instanceof Heartbeat)) {
                    Message message = (Message) read;
                    // System.out.println("[SERVER LISTEN LOOP] Received message: " + message.getMessageType());
                    clientHandler.receive(message);
                } else {
                    this.clientHandler.setLastPing(System.currentTimeMillis());
                    clientHandler.sendObject(new Sense());
                }

            } catch (ServerException e) {
                // Connection is already closed by Server
                // This Thread will be interrupted by ClientHandler
            } catch (ClassCastException e) {
                // There is an error during casting
                // Ignore it
            }

        }
    }

}
