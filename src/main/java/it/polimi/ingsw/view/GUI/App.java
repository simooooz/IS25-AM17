package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.client.model.ClientEventBus;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.view.GUI.fxmlcontroller.LoginController;
import it.polimi.ingsw.view.UserInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class App extends Application implements UserInterface {

    private static Client client;
    private Parent root;
    private static Stage primaryStage;

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start() {
        ClientEventBus.getInstance().subscribe(this);
        Platform.startup(() -> {
            try {
                start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        this.root = loader.load();

        // init main controller
        LoginController loginController = loader.getController();
        MessageDispatcher.getInstance().registerHandler(loginController);

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Galaxy Trucker");
        stage.setFullScreen(true);
        stage.show();

        // client init
        new Thread(() -> {
            try {
                client = new ClientSocket(this);
            } catch (Exception e) {
                Platform.runLater(() -> displayError("Errore durante la connessione: " + e.getMessage()));
            }
        }).start();
    }

    public static Client getClientInstance() {
        return client;
    }

    @Override
    public void onEvent(List<GameEvent> events) {
        for (GameEvent event : events)
            Platform.runLater(() -> MessageDispatcher.getInstance().dispatchMessage(event));
    }

    @Override
    public void displayError(String message) {
        // Assicurati che gli errori vengano mostrati nel thread JavaFX
        Platform.runLater(() -> {
            try {
                // Per ora mostra l'errore nella console, poi implementerai l'overlay
                System.err.println("GUI Error: " + message);

                // Se hai l'overlay, decommentare:
                /*
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/errorOverlay.fxml"));
                Parent overlay = loader.load();

                ErrorOverlayController controller = loader.getController();
                controller.setErrorMessage(message);

                // Aggiungi logica per mostrare l'overlay nella scena
                */

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error showing error message: " + message);
            }
        });
    }

    @Override
    public void clear() {}

}
