package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.client.model.ClientEventBus;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.common.model.events.game.ErrorEvent;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.rmi.RMIClient;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.view.UserInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.text.Font;
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
    public void start(Stage stage) {
        Font.loadFont(getClass().getResource("/fonts/Audiowide-Regular.ttf").toExternalForm(), 14);
        SceneManager.init(stage);
    }

    public static Client getClientInstance() {
        return client;
    }

    @Override
    public void onEvent(List<Event> events) {
        for (Event event : events)
            Platform.runLater(() -> MessageDispatcher.getInstance().dispatchMessage(event));
    }

    @Override
    public void displayError(String message) {
        MessageDispatcher.getInstance().dispatchMessage(new ErrorEvent(message));
    }

}
