package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.common.model.events.Event;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Manages overlay functionality for JavaFX applications, providing a modal-like
 * interface with a semi-transparent background and centered content area.
 *
 * This class creates and manages an overlay that appears on top of the main scene,
 * featuring a dark semi-transparent background and a centered rectangular content area.
 * The overlay can be dismissed by clicking outside the content area.
 *
 */
public class OverlayManager {

    /**
     * The main overlay container that holds all overlay elements.
     */
    private StackPane overlayPane;

    /**
     * The central content area of the overlay where UI elements can be placed.
     */
    private HBox centralHBox;

    /**
     * Reference to the root pane of the main scene where the overlay will be displayed.
     */
    private Pane rootPane;

    /**
     * Constructs a new OverlayManager for the specified root pane.
     *
     * @param rootPane the main pane of the scene where the overlay will be displayed.
     *                 Must not be null and should be the root container of the scene.
     */
    public OverlayManager(Pane rootPane) {
        this.rootPane = rootPane;
    }

    /**
     * Displays the overlay with a semi-transparent background and centered content area.
     *
     * @param callback a {@link Runnable} that will be executed when the overlay is closed.
     *                 This can be used to perform cleanup operations or update the UI state.
     *                 Can be null if no callback is needed.
     */
    public void showOverlay(Runnable callback) {
        overlayPane = new StackPane();

        Rectangle background = new Rectangle();
        background.setFill(Color.BLACK);
        background.setOpacity(0.6); // 60% opacity

        Scene scene = rootPane.getScene();
        background.setWidth(scene.getWidth());
        background.setHeight(scene.getHeight());

        background.widthProperty().bind(overlayPane.widthProperty());
        background.heightProperty().bind(overlayPane.heightProperty());

        centralHBox = new HBox();
        centralHBox.setPrefWidth(800);
        centralHBox.setPrefHeight(400);
        centralHBox.setMaxWidth(800);
        centralHBox.setMaxHeight(400);
        centralHBox.setStyle("-fx-background-color: #2a1a4a; -fx-background-radius: 10;");

        overlayPane.getChildren().addAll(background, centralHBox);

        overlayPane.setOnMouseClicked(event -> {
            if (!centralHBox.getBoundsInParent().contains(event.getX(), event.getY())) {
                closeOverlay(callback);
            }
            event.consume();
        });

        centralHBox.setOnMouseClicked(event -> event.consume());

        overlayPane.setPrefWidth(scene.getWidth());
        overlayPane.setPrefHeight(scene.getHeight());
        overlayPane.setLayoutX(0);
        overlayPane.setLayoutY(0);

        rootPane.getChildren().add(overlayPane);
        overlayPane.toFront();
    }

    /**
     * Closes and removes the overlay from the scene.
     *
     * If the overlay is not currently displayed, this method has no effect.
     *
     * @param callback a {@link Runnable} that will be executed before closing the overlay.
     *                 This can be used to perform cleanup operations or save state.
     *                 Can be null if no callback is needed.
     */
    public void closeOverlay(Runnable callback) {
        if (overlayPane != null && overlayPane.getParent() != null) {
            callback.run();
            ((Pane) overlayPane.getParent()).getChildren().remove(overlayPane);
            overlayPane = null;
            centralHBox = null;
        }
    }

    /**
     * Returns the central content area of the overlay.
     *
     * @return the central {@link HBox} container of the overlay, or null if
     *         the overlay is not currently displayed
     */
    public HBox getCentralHBox() {
        return centralHBox;
    }
}