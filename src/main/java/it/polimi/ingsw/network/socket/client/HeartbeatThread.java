package it.polimi.ingsw.network.socket.client;

import it.polimi.ingsw.network.exceptions.ClientException;
import it.polimi.ingsw.network.socket.Heartbeat;

public class HeartbeatThread extends Thread {

    private final ClientSocket clientSocket;
    private final int heartbeatMsInterval;

    public HeartbeatThread(ClientSocket clientSocket, int heartbeatMsInterval) {
        this.clientSocket = clientSocket;
        this.heartbeatMsInterval = heartbeatMsInterval;
    }

    @Override
    public void run() {
        while(isAlive()) {

            try {
                clientSocket.sendObject(new Heartbeat());
                Thread.sleep(heartbeatMsInterval);
            } catch (ClientException | InterruptedException e) {
                // Something went wrong...
                // Do nothing, in case of ClientException, connection will be closed by ClientSocket
            }

        }
    }

}
