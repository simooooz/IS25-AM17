//package it.polimi.ingsw.view.GUI;
//
//import it.polimi.ingsw.model.components.Component;
//import it.polimi.ingsw.network.Client;
//import it.polimi.ingsw.network.messages.MessageType;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.ClipboardContent;
//import javafx.scene.input.Dragboard;
//import javafx.scene.input.TransferMode;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Rectangle;
//
//import java.net.URL;
//import java.util.ResourceBundle;
//
//public class BuildController implements MessageHandler, Initializable {
//
//    private Client client;
//
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        // Inizializza la GUI
//        // setupImageFlowPane();
//
//        // Nascondi il pannello opzioni all'inizio
//        // optionsPanel.setVisible(false);
//        // optionsPanel.setManaged(false);
//
//        // Aggiungi alcune immagini di esempio (placeholder)
//        loadCommonComponents();
//    }
//
//    private void loadCommonComponents() {
//        for (Component component : client.getGameController().getModel().getBoard().getCommonComponents()) {
//
//            Image image = new Image("", 150, 150, false, true);
//            ImageView imageView = new ImageView(image);
//            // imageView.setId("component_"+component.getId());
//            imageView.setFitWidth(150);
//            imageView.setFitHeight(150);
//            imageView.setPreserveRatio(true);
//            imageView.setStyle(
//                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 2, 2);" +
//                            "-fx-cursor: hand;"
//            );
//
//            imageView.setOnMouseClicked(event -> {
//                System.out.println("Clicked image: ");
//            });
//
//            imageView.setOnDragDetected(event -> {
//                Dragboard db = imageView.startDragAndDrop(TransferMode.MOVE);
//                ClipboardContent content = new ClipboardContent();
//                // content.putString(imagePath);
//                content.putImage(image);
//                db.setContent(content);
//
//                // Salva riferimenti per il drag
//                // draggedImageView = imageView;
//                // draggedImagePath = imagePath;
//
//                event.consume();
//            });
//
//            imageView.setOnDragDone(event -> {
//                if (event.getTransferMode() == TransferMode.MOVE) {
//                    // Rimuovi l'immagine dalla lista solo se il drop è avvenuto con successo
//                    if (event.isDropCompleted()) {
//                        // imagePaths.remove(imagePath);
//                        // imageFlowPane.getChildren().remove(imageView);
//                    }
//                }
//            });
//
//        }
//    }
//
//    private void setupLearnerShipGrid() {
//        // Crea la griglia di gioco 7x7 (da coordinate 4,4 a 10,10)
//        //gameGrid.getChildren().clear();
//
//        for (int row = 4; row <= 10; row++) {
//            for (int col = 4; col <= 10; col++) {
//                Rectangle slot = new Rectangle(60, 60);
//                slot.setFill(Color.TRANSPARENT);
//                slot.setStroke(Color.LIGHTBLUE);
//                slot.setStrokeWidth(2);
//                slot.setOpacity(0.3);
//
//                String slotId = row + "," + col;
//                slot.setId(slotId);
//
//                // Setup drag over event
//                slot.setOnDragOver(event -> {
//                    if (event.getGestureSource() != slot && event.getDragboard().hasString()) {
//                        event.acceptTransferModes(TransferMode.MOVE);
//                        slot.setFill(Color.LIGHTBLUE.deriveColor(0, 1, 1, 0.3));
//                    }
//                    event.consume();
//                });
//
//                // Setup drag exited event
//                slot.setOnDragExited(event -> {
//                    slot.setFill(Color.TRANSPARENT);
//                    event.consume();
//                });
//
//                // Setup drag dropped event
//                slot.setOnDragDropped(event -> {
//                    Dragboard db = event.getDragboard();
//                    boolean success = false;
//
//                    if (db.hasString()) {
//                        String imagePath = db.getString();
//                        // placeImageInSlot(slot, imagePath);
//                        success = true;
//                    }
//
//                    event.setDropCompleted(success);
//                    event.consume();
//                });
//
//                // gameGrid.add(slot, col - 4, row - 4);
//            }
//        }
//    }
//
//    @Override
//    public void handleMessage(MessageType eventType, String username, Object... args) {
//
//    }
//
//    @Override
//    public boolean canHandle(MessageType messageType) {
//        return messageType == MessageType.ERROR;
//    }
//}
//
//

package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.messages.MessageType;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class BuildController implements MessageHandler, Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inizializzazione semplice
        System.out.println("IN GAME - BuildController initialized");
    }

    @Override
    public void handleMessage(MessageType eventType, String username, Object... args) {
        // Gestione messaggi di base
        System.out.println("Message received: " + eventType);
    }

    @Override
    public boolean canHandle(MessageType messageType) {
        return messageType == MessageType.ERROR;
    }
}