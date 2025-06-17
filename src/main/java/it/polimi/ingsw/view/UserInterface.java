package it.polimi.ingsw.view;

import it.polimi.ingsw.client.model.ClientEventObserver;

public interface UserInterface extends ClientEventObserver {
    void displayError(String message);
    void clear();
    // void shutdown();
    // void displayUpdate();
}
