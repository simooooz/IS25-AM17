package it.polimi.ingsw.view;

import it.polimi.ingsw.client.model.ClientEventObserver;

public interface UserInterface extends ClientEventObserver {
    void displayError(String message);
    void start(int networkType);
    // void shutdown();
    // void displayUpdate();
}
