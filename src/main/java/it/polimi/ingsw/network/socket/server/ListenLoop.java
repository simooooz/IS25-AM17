package it.polimi.ingsw.network.socket.server;

import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.messages.ErrorMessage;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.socket.Heartbeat;
import it.polimi.ingsw.network.socket.Sense;

public class ListenLoop extends Thread {

    private final String code;
    private final User user;

    public ListenLoop(String connectionCode, User user) {
        this.code = connectionCode;
        this.user = user;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Object read = Server.getInstance().receiveObject(this.code);

                if (!(read instanceof Heartbeat)) { // Input from client is a message
                    Message message = (Message) read;
                    System.out.println("[SERVER LISTEN LOOP] Received message: " + message.getMessageType());
                    user.receive(message);
                }
                else // Input from client is a heartbeat
                    Sense.sendSense(this.code);

                // TODO delayer?

            } catch (ServerException e) {
                // Connection is already closed by Server
                // This Thread will be interrupted by ClientConnection
            } catch (ClassCastException e) {
                this.user.send(new ErrorMessage());
            }
        }
    }


}
