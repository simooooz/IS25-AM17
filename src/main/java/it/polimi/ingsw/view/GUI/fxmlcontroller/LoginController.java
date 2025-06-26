package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.common.model.events.game.ErrorEvent;
import it.polimi.ingsw.common.model.events.lobby.UsernameOkEvent;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.GUI.App;
import it.polimi.ingsw.view.GUI.MessageDispatcher;
import it.polimi.ingsw.view.GUI.MessageHandler;
import it.polimi.ingsw.view.GUI.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.util.List;

public class LoginController implements MessageHandler {

    /**
     * Container for the username input field
     */
    @FXML private VBox username;

    /**
     * Label for displaying status messages and errors
     */
    @FXML private Label status;

    /**
     * Label for displaying input hints to the user
     */
    @FXML private Label hintLabel;

    /**
     * Sets up the username input field, validation listeners,
     * and keyboard event handlers.
     */
    @FXML
    public void initialize() {
        MessageDispatcher.getInstance().registerHandler(this);

        TextField input = new TextField();
        input.setPromptText("username");
        input.setMaxWidth(256);
        input.setPrefHeight(40);

        // Real-time validation listener
        input.textProperty().addListener((_, _, newValue) -> validateUsername(newValue.trim()));

        // ENTER key press for login submission
        input.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String usernameText = input.getText().trim();
                if (isValidUsername(usernameText)) {

                    status.getStyleClass().clear();
                    status.getStyleClass().add("status-text");
                    status.setText("Connecting...");
                    status.setVisible(true);

                    hideHint();
                    App.getClientInstance().send(MessageType.SET_USERNAME, usernameText);
                }
                else
                    event.consume();
            }
        });


        Platform.runLater(input::requestFocus);
        username.getChildren().add(input);
        hideHint();
    }

    /**
     * Validates if the provided username meets the requirements.
     *
     * @param username the username string to validate
     * @return true if username is between 3 and 18 characters, false otherwise
     */
    private boolean isValidUsername(String username) {
        return username.length() >= 3 && username.length() <= 18;
    }

    /**
     * Performs real-time validation of the username input and updates the UI accordingly.
     * Shows appropriate error messages or hints based on the current input state.
     *
     * @param username the current username input to validate
     */
    private void validateUsername(String username) {
        if (username.isEmpty()) {
            clearMessages();
            hideHint();
        } else if (username.length() < 3) {
            showError("Too short (min 3 characters)");
            hideHint();
        } else if (username.length() > 18) {
            showError("Too long (max 18 characters)");
            hideHint();
        } else {
            // valid username
            clearMessages();
            hintLabel.setVisible(true);
        }
    }

    /**
     * Displays an error message in the status label with error styling.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        if (status != null) {
            status.getStyleClass().clear();
            status.getStyleClass().add("error-text");
            status.setText(message);
            status.setVisible(true);
        }
    }

    /**
     * Clears all messages from the status label and hides it.
     */
    private void clearMessages() {
        status.setText("");
        status.setVisible(false);
    }

    /**
     * Hides the hint label from the user interface.
     */
    private void hideHint() {
        if (hintLabel != null)
            hintLabel.setVisible(false);
    }

    /**
     * Handles incoming network messages related to the login process.
     *
     * @param event the game event received from the network layer
     */
    @Override
    public void handleMessage(Event event) {
        switch (event) {
            case UsernameOkEvent _ -> SceneManager.navigateToScene("/fxml/menu.fxml", this, null);
            case ErrorEvent _ -> {
                showError("Username already taken");
                hideHint();
            }
            default -> {}
        }
    }

    /**
     * Determines if this controller can handle the specified message type.
     *
     * @param messageType the type of message to check
     * @return true if this controller can handle the message type, false otherwise
     */
    @Override
    public boolean canHandle(MessageType messageType) {
        return List.of(
                MessageType.USERNAME_OK_EVENT,
                MessageType.ERROR
        ).contains(messageType);
    }

}
