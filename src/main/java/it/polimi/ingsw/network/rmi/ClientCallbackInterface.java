package it.polimi.ingsw.network.rmi;

import it.polimi.ingsw.network.messages.MessageType;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientCallbackInterface extends Remote {

    void notifyGameEvent(MessageType eventType, Object... params) throws RemoteException;

}
