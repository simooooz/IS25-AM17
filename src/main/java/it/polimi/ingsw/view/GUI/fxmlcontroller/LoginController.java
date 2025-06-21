package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.GUI.App;
import it.polimi.ingsw.view.GUI.MessageDispatcher;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class LoginController implements MessageHandler {

    /**
     * Container for the username input field
     */
    @FXML
    private VBox username;

    /**
     * Label for displaying status messages and errors
     */
    @FXML
    private Label status;

    /**
     * Label for displaying input hints to the user
     */
    @FXML
    private Label hintLabel;

    /**
     * Sets up the username input field, validation listeners,
     * and keyboard event handlers.
     */
    @FXML
    public void initialize() {
        // textfield
        TextField input = new TextField();
        input.setPromptText("username");
        input.setMaxWidth(256);
        input.setPrefHeight(40);

        // real-time validation listener
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            validateUsername(newValue.trim());
        });

        // ENTER key press for login submission
        input.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String usernameText = input.getText().trim();
                if (isValidUsername(usernameText)) {
                    showStatus("Connecting...");
                    hideHint();
                    App.getClientInstance().send(MessageType.SET_USERNAME, usernameText);
                } else {
                    // consume event if username is invalid (error already shown by listener)
                    event.consume();
                }
            }
        });

        // set automatic focus on the input field
        Platform.runLater(input::requestFocus);

        // add input field to the container
        if (username != null) {
            username.getChildren().add(input);
        }

        // initially hide the hint label
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
            showHint();
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
     * Displays a status message in the status label with normal styling.
     *
     * @param message the status message to display
     */
    private void showStatus(String message) {
        if (status != null) {
            status.getStyleClass().clear();
            status.getStyleClass().add("status-text");
            status.setText(message);
            status.setVisible(true);
        }
    }

    /**
     * Clears all messages from the status label and hides it.
     */
    private void clearMessages() {
        if (status != null) {
            status.setText("");
            status.setVisible(false);
        }
    }

    /**
     * Shows the hint label to provide guidance to the user.
     */
    private void showHint() {
        if (hintLabel != null) {
            hintLabel.setVisible(true);
        }
    }

    /**
     * Hides the hint label from the user interface.
     */
    private void hideHint() {
        if (hintLabel != null) {
            hintLabel.setVisible(false);
        }
    }

    /**
     * Navigates to a new scene by loading the specified FXML file and controller.
     * Handles the transition between different application screens.
     *
     * @param <T>             the type of the target controller, must extend MessageHandler
     * @param fxmlPath        the path to the FXML file to load
     * @param controllerClass the class of the target controller
     */
    private <T extends MessageHandler> void navigateToScene(String fxmlPath, Class<T> controllerClass) {
        try {
            // Load the new FXML scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            // Get the controller and register it for message handling
            T controller = loader.getController();
            MessageDispatcher.getInstance().unregisterHandler(this);
            MessageDispatcher.getInstance().registerHandler(controller);

            // Replace the current scene
            Scene scene = username.getScene();
            if (scene != null) {
                scene.setRoot(view);
            }

        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading game interface");
        }
    }

    /**
     * Handles incoming network messages related to the login process.
     *
     * @param event the game event received from the network layer
     */
    @Override
    public void handleMessage(GameEvent event) {
        switch (event.eventType()) {
            case USERNAME_OK_EVENT -> Platform.runLater(() ->
                    navigateToScene("/fxml/menu.fxml", MenuController.class));
            case ERROR -> {
                // connection error
                Platform.runLater(() -> {
                    showError("Connection failed. Please try again.");
                    hideHint();
                });
            }
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
