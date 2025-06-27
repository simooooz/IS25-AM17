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

/**
 * Controller for the login interface that handles username input and validation.
 *
 * <p>This controller manages the initial login screen where users enter their username
 * to join the game. It provides real-time validation feedback, handles user input,
 * and manages the authentication process with the server. The controller implements
 * the {@link MessageHandler} interface to receive and process authentication-related
 * network events.</p>
 */
public class LoginController implements MessageHandler {

    /**
     * Container for the dynamically created username input field.
     *
     * <p>This VBox serves as the parent container where the username TextField
     * will be added during initialization. The container is configured via FXML
     * and provides layout management for the input field.</p>
     */
    @FXML
    private VBox username;

    /**
     * Label for displaying status messages, validation errors, and connection feedback.
     *
     * <p>This label serves multiple purposes:</p>
     * <ul>
     *   <li>Showing validation errors (too short, too long)</li>
     *   <li>Displaying connection status ("Connecting...")</li>
     *   <li>Showing error messages for failed connections</li>
     * </ul>
     *
     * <p>The label's styling is dynamically updated based on the message type
     * using CSS classes like "error-text" and "status-text".</p>
     */
    @FXML
    private Label status;

    /**
     * Label for displaying input hints to guide the user.
     *
     * <p>This label provides helpful hints to the user about the input requirements
     * or submission process. It is shown when the username is valid and hidden
     * during error states or when the input is empty.</p>
     */
    @FXML
    private Label hintLabel;

    /**
     * Initializes the login controller and sets up the user interface components.
     * <p>The method uses Platform.runLater() to ensure proper focus management
     * after the scene is fully rendered.</p>
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

    private boolean isValidUsername(String username) {
        return username.length() >= 3 && username.length() <= 18;
    }

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

    private void showError(String message) {
        if (status != null) {
            status.getStyleClass().clear();
            status.getStyleClass().add("error-text");
            status.setText(message);
            status.setVisible(true);
        }
    }

    private void clearMessages() {
        status.setText("");
        status.setVisible(false);
    }

    private void hideHint() {
        if (hintLabel != null)
            hintLabel.setVisible(false);
    }

    /**
     * Handles incoming network messages related to the login and authentication process.
     *
     * <p>This method processes authentication-related events received from the server
     * and updates the UI accordingly. It uses pattern matching with switch expressions
     * to handle different event types efficiently.</p>
     *
     * <p>Upon successful username acceptance, the method navigates to the main menu
     * scene. For authentication errors, it displays a generic connection failure
     * message and hides any visible hints.</p>
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
     * @param messageType the type of message to check for compatibility,
     *                    must not be {@code null}
     * @return {@code true} if this controller can process the given message type,
     *         {@code false} otherwise
     *
     * @see MessageHandler#canHandle(MessageType)
     */
    @Override
    public boolean canHandle(MessageType messageType) {
        return List.of(
                MessageType.USERNAME_OK_EVENT,
                MessageType.ERROR
        ).contains(messageType);
    }
}