package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.network.messages.MessageType;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class ErrorOverlayController implements MessageHandler {

    @FXML private Label errorMessageLabel;
    @FXML private Node overlayRoot;

    public void setErrorMessage(String message) {
        errorMessageLabel.setText(message);
    }

    @FXML
    private void closeOverlay() {
        ((StackPane) overlayRoot.getParent()).getChildren().remove(overlayRoot);
    }

    @Override
    public void handleMessage(MessageType eventType, String username, Object... args) {

    }

    @Override
    public boolean canHandle(MessageType messageType) {
        return messageType == MessageType.ERROR;
    }
}