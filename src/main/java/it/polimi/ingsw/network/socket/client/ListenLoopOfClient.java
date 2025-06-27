package it.polimi.ingsw.network.socket.client;

import it.polimi.ingsw.network.exceptions.ClientException;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.socket.Sense;

/**
 * Thread that continuously listens for incoming messages from the server.
 * Filters out Sense objects and processes Message objects through the client.
 */
public class ListenLoopOfClient extends Thread {

    /** Client socket to listen on */
    private final ClientSocket clientSocket;

    /**
     * Creates and starts the listening thread.
     *
     * @param clientSocket the client socket to monitor for incoming messages
     */
    public ListenLoopOfClient(ClientSocket clientSocket) {
        this.clientSocket = clientSocket;
        this.start();
    }

    /**
     * Continuously reads and processes messages from the server.
     * Ignores Sense objects and forwards Message objects to the client for processing.
     */
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Object read = this.clientSocket.readObject();

                if (!(read instanceof Sense)) {
                    Message message = (Message) read;
                    // System.out.println("[CLIENT LISTEN LOOP] Received message: " + message.getMessageType());
                    this.clientSocket.receive(message);
                }

            } catch (ClientException e) {
                // Connection is already closed by ClientSocket
                // This Thread will be interrupted by ClientSocket
            } catch (ClassCastException e) {
                // Just ignore the received message
            }
        }
    }

}
