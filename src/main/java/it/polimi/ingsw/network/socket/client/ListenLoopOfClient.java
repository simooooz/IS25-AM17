package it.polimi.ingsw.network.socket.client;

import it.polimi.ingsw.network.exceptions.ClientException;
import it.polimi.ingsw.network.messages.ErrorMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.socket.Sense;

public class ListenLoopOfClient extends Thread {

    private final ClientSocket clientSocket;

    public ListenLoopOfClient(ClientSocket clientSocket) {
        this.clientSocket = clientSocket;
        this.start();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Object read = this.clientSocket.readObject();

                if (!(read instanceof Sense)) {
                    Message message = (Message) read;
                    System.out.println("[CLIENT LISTEN LOOP] Received message: " + message.getMessageType());
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
