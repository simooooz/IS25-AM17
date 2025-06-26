package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.client.model.ClientEventBus;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.common.model.events.game.GameErrorEvent;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.rmi.RMIClient;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.view.UserInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.List;

public class App extends Application implements UserInterface {

    private static Client client;

    @Override
    public void start(int networkType, String ip) {
        ClientEventBus.getInstance().subscribe(this);

        Platform.startup(() -> {
            start(new Stage());
            SceneManager.navigateToScene("/fxml/loading.fxml", null, null);

            new Thread(() -> {
                if (ip.isBlank())
                    client = networkType == 1 ? new ClientSocket(this) : new RMIClient(this);
                else
                    client = networkType == 1 ? new ClientSocket(this, ip) : new RMIClient(this, ip);
                Platform.runLater(() -> SceneManager.navigateToScene("/fxml/login.fxml", null, null));
            }).start();
        });
    }

    @Override
    public void start(Stage stage) { SceneManager.init(stage); }

    public static Client getClientInstance() { return client; }

    @Override
    public void onEvent(List<GameEvent> events) {
        for (GameEvent event : events)
            Platform.runLater(() -> MessageDispatcher.getInstance().dispatchMessage(event));
    }

    @Override
    public void displayError(String message) {
        MessageDispatcher.getInstance().dispatchMessage(new GameErrorEvent(message));
    }

}
