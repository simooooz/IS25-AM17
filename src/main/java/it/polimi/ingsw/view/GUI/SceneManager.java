package it.polimi.ingsw.view.GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Utility class for managing scene navigation in the JavaFX application.
 *
 * The class follows a static utility pattern and maintains references to
 * the primary stage and current scene for efficient scene switching.
 */
public class SceneManager {

    private static Stage primaryStage;
    private static Scene scene = null;

    /**
     * Initializes the SceneManager with the primary stage.
     */
    public static void init(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Navigates to a new scene by loading the specified FXML file.
     *
     * @param <T> the type of the controller, must extend MessageHandler
     * @param fxmlPath the path to the FXML file to load (relative to resources)
     * @param oldController the previous controller to unregister, or null if none
     * @param callback a consumer that receives the new controller for initialization, or null if not needed
     */
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
            e.printStackTrace();
        } finally {
            MessageDispatcher.getInstance().setTransitioning(false);
        }
    }

}