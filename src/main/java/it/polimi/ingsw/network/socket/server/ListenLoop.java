package it.polimi.ingsw.network.socket.server;

import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.socket.Heartbeat;
import it.polimi.ingsw.network.socket.Sense;

public class ListenLoop extends Thread {

    private final ClientHandler clientHandler;

    public ListenLoop(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.start();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {

            try {
                Object read = clientHandler.readObject();

                if (!(read instanceof Heartbeat)) {
                    Message message = (Message) read;
                    System.out.println("[SERVER LISTEN LOOP] Received message: " + message.getMessageType());
                    clientHandler.receive(message);
                } else
                    clientHandler.sendObject(new Sense());

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
