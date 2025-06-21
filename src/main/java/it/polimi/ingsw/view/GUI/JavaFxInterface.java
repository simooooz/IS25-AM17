package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.client.model.ClientEventBus;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.view.UserInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class JavaFxInterface extends Application implements UserInterface {

    private static Client client;
    private Parent root;

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/preGame.fxml"));
        this.root = loader.load();

        // Registra il controller principale
        MainController mainController = loader.getController();
        MessageDispatcher.getInstance().registerHandler(mainController);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Galaxy Trucker");
        stage.setFullScreen(true);
        stage.show();

        // Crea il client passando questa interfaccia GUI e disabilitando l'avvio automatico della TUI
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