package it.polimi.ingsw.network.messages.net;

import it.polimi.ingsw.network.exceptions.ServerException;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.network.messages.ZeroArgMessage;
import it.polimi.ingsw.network.socket.server.RefToUser;
import it.polimi.ingsw.network.socket.server.Server;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DisconnectMessage extends Message {

    private static final int DISCONNECT_TIMEOUT_MS = Integer.parseInt(
            System.getProperty("disconnect.timeout.ms", "2000"));

    public DisconnectMessage() {
        super(MessageType.DISCONNECT);
    }

    @Override
    public void execute(RefToUser user) {
        try {

            CompletableFuture<Void> future = new CompletableFuture<>();
            user.send(new ZeroArgMessage(MessageType.DISCONNECT_OK), future);

            try {
                future.get(DISCONNECT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                System.err.println("[SERVER] Timeout occurred while sending disconnect confirmation to: " + user.getConnectionCode());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[SERVER] Thread interrupted while waiting for disconnect confirmation to be sent");
            } catch (ExecutionException e) {
                System.err.println("[SERVER] Error during disconnect message sending: " + e.getCause().getMessage());
            } finally {
                Server.getInstance().closeConnection(user.getConnectionCode());
                System.out.println("[SERVER] User disconnected: " + user.getConnectionCode());
            }

        } catch (ServerException e) {
            System.err.println("[SERVER] Error during disconnect: " + e.getMessage());
        }
    }

}
