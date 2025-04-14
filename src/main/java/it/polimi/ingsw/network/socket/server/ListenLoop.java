package it.polimi.ingsw.network.socket.server;

import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.messages.ErrorMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.socket.Heartbeat;
import it.polimi.ingsw.network.socket.Sense;

import java.util.concurrent.CompletableFuture;

public class ListenLoop extends Thread {

    private final String connectionCode;
    private final RefToUser user;

    public ListenLoop(String connectionCode, RefToUser user) {
        this.connectionCode = connectionCode;
        this.user = user;
        this.start();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {

            try {
                Object read = Server.getInstance().receive(this.connectionCode);

                if (!(read instanceof Heartbeat)) {
                    // then input is a message
                    Message message = (Message) read;
                    System.out.println("[SERVER LISTEN LOOP] Received message: " + message.getMessageType());
                    user.receive(message);
                } else
                    // then input is an heartbeat
                    Sense.sendSense(this.connectionCode);

                // TODO delayer?

            } catch (ServerException e) {
                // Connection is already closed by Server
                // This Thread will be interrupted by ClientConnection
            } catch (ClassCastException e) {
                this.user.send(new ErrorMessage(), new CompletableFuture<>());
            }

        }
    }

}
