package it.polimi.ingsw.view.GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

public class SceneManager {

    private static Stage primaryStage;
    private static Scene scene = null;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static <T extends MessageHandler> void navigateToScene(String fxmlPath, MessageHandler oldController, Consumer<T> callback) {
        MessageDispatcher.getInstance().setTransitioning(true);

        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent view = loader.load();

            if (callback != null)
                callback.accept(loader.getController());

            if (oldController != null)
                MessageDispatcher.getInstance().unregisterHandler(oldController);

            if (scene == null) {
                Scene scene = new Scene(view, 1280, 800);
                scene.getStylesheets().add(Objects.requireNonNull(SceneManager.class.getResource("/style.css")).toExternalForm());
                primaryStage.setScene(scene);
                primaryStage.setFullScreen(true);
                primaryStage.setTitle("Galaxy Trucker");
                primaryStage.setFullScreen(true);
                primaryStage.show();
            }
            else
                scene.setRoot(view);

            scene = view.getScene();

        } catch (IOException e) {
            // TODO cosa metto?
            e.printStackTrace();
        } finally {
            MessageDispatcher.getInstance().setTransitioning(false);
        }
    }

}
