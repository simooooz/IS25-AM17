package it.polimi.ingsw.view;

import it.polimi.ingsw.network.messages.Message;

public interface UserInterface {

    void displayUpdate(Message message);

    void displayError(String message);

}
