package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.common.model.events.Event;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class OverlayManager {

    private StackPane overlayPane;
    private HBox centralHBox;
    private Pane rootPane; // Il tuo pane principale della scena

    public OverlayManager(Pane rootPane) {
        this.rootPane = rootPane;
    }

    public void showOverlay(Runnable callback) {
        overlayPane = new StackPane();

        Rectangle background = new Rectangle();
        background.setFill(Color.BLACK);
        background.setOpacity(0.6); // 40% di opacità

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

        centralHBox.setOnMouseClicked(Event::consume);

        overlayPane.setPrefWidth(scene.getWidth());
        overlayPane.setPrefHeight(scene.getHeight());
        overlayPane.setLayoutX(0);
        overlayPane.setLayoutY(0);

        rootPane.getChildren().add(overlayPane);
        overlayPane.toFront();
    }

    public void closeOverlay(Runnable callback) {
        if (overlayPane != null && overlayPane.getParent() != null) {
            callback.run();
            ((Pane) overlayPane.getParent()).getChildren().remove(overlayPane);
            overlayPane = null;
            centralHBox = null;
        }
    }

    public HBox getCentralHBox() {
        return centralHBox;
    }

}