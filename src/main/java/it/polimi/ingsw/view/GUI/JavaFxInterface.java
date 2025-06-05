package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.view.UserInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class JavaFxInterface extends Application implements UserInterface {

    private static Client client;
    private Parent root;

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
                client = new ClientSocket(this, false);
                if (client == null) {
                    Platform.runLater(() -> displayError("Errore durante l'inizializzazione del client"));
                }
            } catch (Exception e) {
                Platform.runLater(() -> displayError("Errore durante la connessione: " + e.getMessage()));
            }
        }).start();
    }

    public static Client getClientInstance() {
        return client;
    }

    @Override
    public void displayUpdate(Message message) {
        Platform.runLater(() -> {
            MessageDispatcher.getInstance().dispatchMessage(
                    message.getMessageType(), client.getUsername(), message.getArguments());
        });
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
}