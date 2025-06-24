package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.client.model.components.ClientComponent;
import it.polimi.ingsw.client.model.events.CardPileLookedEvent;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.player.ClientShip;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.common.model.events.game.*;
import it.polimi.ingsw.common.model.events.lobby.JoinedLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.LeftLobbyEvent;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.GUI.App;
import it.polimi.ingsw.view.GUI.MessageDispatcher;
import it.polimi.ingsw.view.GUI.OverlayManager;
import it.polimi.ingsw.view.GUI.SceneManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

/**
 * Controller for the ship building interface. Handles component placement on the ship grid
 * and manages the visual representation of available components. Implements performance
 * optimizations through caching and incremental updates.
 */
public class BuildController implements MessageHandler, Initializable {

    @FXML public Label timeLeft;
    @FXML public Label hourglassPos;
    @FXML public VBox timerContainer;

    @FXML public Button setReadyButton;
    @FXML public Label statusLabel;

    private OverlayManager overlayManager;
    @FXML public AnchorPane root;
    @FXML public ImageView pile_0;
    @FXML public ImageView pile_1;
    @FXML public ImageView pile_2;

    @FXML public HBox otherPlayersContainer;

    @FXML private ImageView shipImageView;
    @FXML private GridPane shipGrid;
    @FXML private FlowPane componentsFlowPane;

    private Client client;

    private final Map<Integer, Rectangle> slotMap = new HashMap<>();
    private final Map<Integer, ImageView> componentMap = new HashMap<>();

    private String localCommand = "";
    private Timeline timeline;
    private int timeRemaining;

    private static final double TILE_WIDTH = 70.0;
    private static final double TILE_HEIGHT = 70.0;
    private static final double OTHER_PLAYERS_TILE_WIDTH = 36.0;
    private static final double OTHER_PLAYERS_TILE_HEIGHT = 35.8;


    EventHandler<DragEvent> slotDragOverHandler = (DragEvent event) -> {
        event.acceptTransferModes(TransferMode.MOVE);
        event.consume();
    };

    EventHandler<DragEvent> slotDragDroppedHandler = event -> {
        Node targetNode = (Node) event.getTarget();
        Node componentImageView = (Node) event.getGestureSource();
        String destId = targetNode.getId();
        String sourceId = event.getDragboard().getString();
        int componentId = Integer.parseInt(componentImageView.getId().split("_")[1]);

        if (destId.equals("slot_0_5") || destId.equals("slot_0_6")) { // Reserved
            event.setDropCompleted(true);
            localCommand = "";
            client.send(MessageType.RESERVE_COMPONENT, componentId);
        }
        else if ((sourceId.equals("componentsFlowPane") || sourceId.equals("slot_0_5") || sourceId.equals("slot_0_6")) && destId.contains("slot")) { // Inserted
            int slotRow = Integer.parseInt(destId.split("_")[1]);
            int slotCol = Integer.parseInt(destId.split("_")[2]);
            String input = String.join(" ", "insert", String.valueOf(componentId), String.valueOf(slotRow), String.valueOf(slotCol));
            client.getGameController().insertComponent(client.getUsername(), componentId, slotRow, slotCol, 0);

            if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("rotate")) // Previous local command was "rotate"
                localCommand = String.join(" ", "insert", String.valueOf(componentId), String.valueOf(slotRow), String.valueOf(slotCol), localCommand.split(" ")[2]);
            else if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) { // Previous local command was "insert" (ex. of a reserve)
                client.send(MessageType.INSERT_COMPONENT, Integer.parseInt(localCommand.split(" ")[1]), Integer.parseInt(localCommand.split(" ")[2]), Integer.parseInt(localCommand.split(" ")[3]), Integer.parseInt(localCommand.split(" ")[4]));
                localCommand = input + " 0";
            } else // No previous local command
                localCommand = input + " 0";

            event.setDropCompleted(true);
        }
        else if (sourceId.contains("slot") && destId.contains("slot")) { // Moved
            int newRow = Integer.parseInt(destId.split("_")[1]);
            int newCol = Integer.parseInt(destId.split("_")[2]);

            client.getGameController().moveComponent(client.getUsername(), componentId, newRow, newCol, 0);

            if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) // Previous local command was "insert"
                localCommand = String.join(" ", localCommand.split(" ")[0], localCommand.split(" ")[1], String.valueOf(newRow), String.valueOf(newCol), localCommand.split(" ")[4]);
            else // No previous local command
                localCommand = String.join(" ", "move", String.valueOf(componentId), String.valueOf(newRow), String.valueOf(newCol), "0");

            event.setDropCompleted(true);
        }
        else { // Released
            localCommand = "";
            event.setDropCompleted(sourceId.contains("slot") && destId.equals("componentsFlowPane"));
            client.send(MessageType.RELEASE_COMPONENT, componentId);
        }

        event.consume();
    };

    EventHandler<MouseEvent> slotOnMouseEnteredHandler = event -> {
        Rectangle slot = (Rectangle) event.getTarget();
        slot.setFill(Color.LIGHTBLUE.deriveColor(0, 1, 1, 0.4));
        slot.setStroke(Color.LIGHTBLUE);
        slot.setStrokeWidth(2);
        event.consume();
    };

    EventHandler<MouseEvent> slotOnMouseExitedHandler = event -> {
        Rectangle slot = (Rectangle) event.getTarget();
        slot.setFill(Color.TRANSPARENT);
        slot.setStroke(Color.rgb(150, 150, 255, 0.6));
        slot.setStrokeWidth(1.5);
        event.consume();
    };

    EventHandler<MouseEvent> componentOnMouseClickedHandler = event -> {
        if (event.getButton() == MouseButton.SECONDARY) { // Rotate locally
            try {
                int componentId = Integer.parseInt(((Node) event.getTarget()).getId().split("_")[1]);
                client.getGameController().rotateComponent(client.getUsername(), componentId, 1);

                if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("rotate")) // Previous local command was "rotate"
                    localCommand = String.join(" ", localCommand.split(" ")[0], localCommand.split(" ")[1], String.valueOf((1 + Integer.parseInt(localCommand.split(" ")[2])) % 4));
                else if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) // Previous local command was "insert"
                    localCommand = String.join(" ", localCommand.split(" ")[0], localCommand.split(" ")[1], localCommand.split(" ")[2], localCommand.split(" ")[3], String.valueOf((Integer.parseInt(localCommand.split(" ")[4]) + 1) % 4));
                else // No previous local command
                    localCommand = String.join(" ", "rotate", String.valueOf(componentId), "1");

                // componentMap.get(componentId).setRotate(componentMap.get(componentId).getRotate() + 90);
            } catch (RuntimeException e) {
                // Propagate general exceptions
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        event.consume();
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MessageDispatcher.getInstance().registerHandler(this);
        this.client = App.getClientInstance();

        loadShipImage();
        setupComponentMap();
        setupPlayersShip();
        startCountdown();

        this.overlayManager = new OverlayManager(root);

        componentsFlowPane.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });

        componentsFlowPane.setOnDragDropped(event -> {
            String sourceId = event.getDragboard().getString();
            Node componentImageView = (Node) event.getGestureSource();
            int componentId = Integer.parseInt(componentImageView.getId().split("_")[1]);

            if (sourceId.contains("slot")) {
                localCommand = "";
                client.send(MessageType.RELEASE_COMPONENT, componentId);
                event.setDropCompleted(true);
            }
            else
                event.setDropCompleted(false);

            event.consume();
        });
    }

    private void setupComponentMap() {
        List<ClientComponent> components = client.getGameController().getModel().getBoard().getMapIdComponents().values().stream().toList();
        for (ClientComponent c : components) {
            ImageView iv = createComponentImage(c);
            componentMap.put(c.getId(), iv);
            if (!c.isInserted())
                componentsFlowPane.getChildren().add(iv);
        }
    }

    @SuppressWarnings("Duplicates")
    private void setupPlayersShip() {
        for (ClientPlayer player : client.getGameController().getModel().getBoard().getAllPlayers()) {
            if (player.getUsername().equals(client.getUsername()))
                setupShipGrid();
            else {
                Label label = new Label(player.getUsername() + "'s ship");
                label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: white;");

                Label stateLabel = new Label(player.getUsername() + "'s ship");
                stateLabel.setId("state_"+player.getUsername());
                stateLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 0 0 0 15;");

                HBox hbox = new HBox();
                hbox.setStyle("-fx-padding: 0 0 10 0;");
                hbox.getChildren().addAll(label, stateLabel);

                Pane shipContainer = createOtherPlayerShip(player);

                VBox vbox = new VBox();
                vbox.setStyle("-fx-padding: 20;");
                vbox.getChildren().addAll(hbox, shipContainer);
                otherPlayersContainer.getChildren().add(vbox);
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private Pane createOtherPlayerShip(ClientPlayer player) {
        ImageView shipImageView = new ImageView();
        shipImageView.setFitWidth(280.0);
        shipImageView.setLayoutX(2.0);
        shipImageView.setLayoutY(2.0);
        shipImageView.setPickOnBounds(true);
        shipImageView.setPreserveRatio(true);
        shipImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cardboard/cardboard-1b.jpg"))));

        Pane shipPane = new Pane();
        shipPane.setPrefHeight(shipImageView.getBoundsInLocal().getHeight());
        shipPane.setPrefWidth(shipImageView.getBoundsInLocal().getWidth());

        // Grid pane
        GridPane tilesGrid = new GridPane();
        tilesGrid.setId("ship_" + player.getUsername());
        tilesGrid.setLayoutX(shipImageView.getLayoutX());
        tilesGrid.setLayoutY(shipImageView.getLayoutY());
        tilesGrid.setPrefWidth(shipImageView.getFitWidth());
        tilesGrid.setPrefHeight(shipImageView.getFitHeight());
        tilesGrid.setStyle("-fx-grid-lines-visible: true;");
        tilesGrid.setPadding(new Insets(9.8, 10.0, 9.8, 10.0));

        // Add all
        setupOtherPlayerShipGrid(tilesGrid, player.getShip());
        shipPane.getChildren().addAll(shipImageView, tilesGrid);

        // Set click animation
//        shipPane.setOnMouseClicked(_ -> {
//            ParallelTransition zoomIn = createZoomToCenterAnimation(shipPane, Duration.millis(500));
//
//            zoomIn.setOnFinished(_ -> {
//                Scene scene = shipPane.getScene();
//                EventHandler<MouseEvent> clickOutsideHandler = new EventHandler<>() {
//                    @Override
//                    public void handle(MouseEvent event) {
//                        if (!shipPane.contains(shipPane.sceneToLocal(event.getSceneX(), event.getSceneY()))) { // Click outside
//                            ParallelTransition zoomOut = createZoomBackAnimation(shipPane, Duration.millis(500));
//                            zoomOut.play();
//                            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
//                        }
//                    }
//                };
//
//                scene.addEventHandler(MouseEvent.MOUSE_CLICKED, clickOutsideHandler); // Add event handler
//
//            });
//
//            zoomIn.play();
//        });

        return shipPane;
    }

    private void setupOtherPlayerShipGrid(GridPane grid, ClientShip ship) {
        for (int row = 0; row < Constants.SHIP_ROWS; row++) {
            for (int col = 0; col < Constants.SHIP_COLUMNS; col++) {
                Optional<ClientComponent> component = ship.getDashboard(row, col);
                if (component.isPresent())
                    placeComponent(grid, componentMap.get(component.get().getId()), row, col, false);
                else {
                    Rectangle emptySlot = new Rectangle(OTHER_PLAYERS_TILE_WIDTH, OTHER_PLAYERS_TILE_HEIGHT);
                    emptySlot.setFill(Color.TRANSPARENT);
                    emptySlot.setStroke(Color.TRANSPARENT);
                    emptySlot.setMouseTransparent(true);
                    grid.add(emptySlot, col, row);
                }
            }
        }
    }

    private void setupShipGrid() {
        ClientShip ship = getClientShip(client.getUsername());

        shipGrid.setPrefWidth(500.0);
        shipGrid.setPrefHeight(400.0);

        for (int row = 0; row < Constants.SHIP_ROWS; row++) {
            for (int col = 0; col < Constants.SHIP_COLUMNS; col++) {
                if (ship.validPositions(row, col) || (row == 0 && col == 5) || (row == 0 && col == 6)) {

                    Optional<ClientComponent> component = ship.getDashboard(row, col);
                    if (component.isPresent())
                        placeComponent(shipGrid, componentMap.get(component.get().getId()), row, col, true);
                    else {
                        Rectangle slot = createShipSlot(row, col);
                        shipGrid.add(slot, col, row);
                    }

                }
                else {
                    Rectangle emptySlot = new Rectangle(TILE_WIDTH, TILE_WIDTH);
                    emptySlot.setFill(Color.TRANSPARENT);
                    emptySlot.setStroke(Color.TRANSPARENT);
                    emptySlot.setMouseTransparent(true);
                    shipGrid.add(emptySlot, col, row);
                }
            }
        }

    }

    private Rectangle createShipSlot(int row, int col) {
        Rectangle slot = new Rectangle(TILE_WIDTH, TILE_HEIGHT);
        slot.setFill(Color.TRANSPARENT);
        slot.setId("slot_" + row + "_" + col);
        slot.setStroke(Color.rgb(150, 150, 255, 0.6));
        slot.setStrokeWidth(1.5);
        slot.setOpacity(0.8);

        slot.addEventHandler(MouseEvent.MOUSE_ENTERED, slotOnMouseEnteredHandler);
        slot.addEventHandler(MouseEvent.MOUSE_EXITED, slotOnMouseExitedHandler);
        slot.addEventHandler(DragEvent.DRAG_OVER, slotDragOverHandler);
        slot.addEventHandler(DragEvent.DRAG_DROPPED, slotDragDroppedHandler);

        return slot;
    }

    @SuppressWarnings("Duplicates")
    private ImageView createComponentImage(ClientComponent component) {
        ImageView iv = new ImageView();
        iv.setFitWidth(70);
        iv.setFitHeight(70);
        iv.setPreserveRatio(true);
        iv.setId("component_" + component.getId());

        if (component.isShown()) {
            String imagePath = "/images/tiles/GT-new_tiles_16_for web" + component.getId() + ".jpg";
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            iv.setImage(image);
        }
        else {
            iv.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/sfondo.jpg"))));
        }

        if (!component.isInserted()) {
            iv.setOnMouseEntered(_ -> {
                iv.setScaleX(1.05);
                iv.setScaleY(1.05);
            });

            iv.setOnMouseExited(_ -> {
                iv.setScaleX(1.0);
                iv.setScaleY(1.0);
            });

            iv.setOnDragDetected(event -> {
                String contentString;

                if (client.getGameController().getModel().getBoard().getCommonComponents().contains(component)) { // Component picked from flow pane
                    if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) { // Previous local command was "insert"
                        client.send(MessageType.INSERT_COMPONENT, Integer.parseInt(localCommand.split(" ")[1]), Integer.parseInt(localCommand.split(" ")[2]), Integer.parseInt(localCommand.split(" ")[3]), Integer.parseInt(localCommand.split(" ")[4]));
                        System.out.println(localCommand);
                    }
                    localCommand = "";
                    client.send(MessageType.PICK_COMPONENT, component.getId());
                    contentString = componentsFlowPane.getId();
                }
                else if (component.equals(getClientShip(client.getUsername()).getComponentInHand().orElse(null))) {
                    contentString = componentsFlowPane.getId();
                }
                else if (getClientShip(client.getUsername()).getReserves().contains(component)) {
                    contentString = "slot_0_" + (4 + getClientShip(client.getUsername()).getReserves().size());
                }
                else
                    contentString = "slot_" + component.getY() + "_" + component.getX();

                Dragboard db = iv.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();

                content.putString(contentString);
                db.setContent(content);
                db.setDragView(iv.snapshot(null, null), 10, 10);

                event.consume();
            });
        }

        return iv;
    }

    public void startCountdown() {
        timeRemaining = 60;
        timeLeft.setStyle("-fx-text-fill: #f39c12;");

        updateTimerLabel();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
            timeRemaining--;
            updateTimerLabel();

            if (timeRemaining <= 0)
                timeline.stop();
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateTimerLabel() {
        timeLeft.setText(String.valueOf(timeRemaining));
        int hourglassPos = client.getGameController().getModel().getBoard().getHourglassPos();

        if (timeRemaining == 0 && (hourglassPos > 1) || (hourglassPos == 1 && client.getGameController().getModel().getBoard().getPlayersByPos().stream().anyMatch(p -> p.getUsername().equals(client.getUsername())))) {
            timeLeft.setText("ROTATE");
            timerContainer.setOnMouseClicked(_ -> client.send(MessageType.MOVE_HOURGLASS));
        }
        else if (timeRemaining <= 10 && hourglassPos == 0)
            timeLeft.setStyle("-fx-text-fill: red;");
    }

    @SuppressWarnings("Duplicates")
    @FXML
    private void lookCardPile(MouseEvent event) {
        if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) { // Previous local command was "insert", otherwise don't change it
            client.send(MessageType.INSERT_COMPONENT, Integer.parseInt(localCommand.split(" ")[1]), Integer.parseInt(localCommand.split(" ")[2]), Integer.parseInt(localCommand.split(" ")[3]), Integer.parseInt(localCommand.split(" ")[4]));
            localCommand = "";
        }

        int deckIndex = Integer.parseInt(((Node)event.getTarget()).getId().split("_")[1]);
        client.send(MessageType.LOOK_CARD_PILE, deckIndex);
    }

    @SuppressWarnings("Duplicates")
    @FXML
    private void leaveGameHandler() {
        if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) { // Previous local command was "insert", otherwise don't change it
            client.send(MessageType.INSERT_COMPONENT, Integer.parseInt(localCommand.split(" ")[1]), Integer.parseInt(localCommand.split(" ")[2]), Integer.parseInt(localCommand.split(" ")[3]), Integer.parseInt(localCommand.split(" ")[4]));
            localCommand = "";
        }
        client.send(MessageType.LEAVE_GAME);
    }

    @FXML
    public void setReadyHandler(ActionEvent event) {
        if (localCommand.split(" ").length > 0 && localCommand.split(" ")[0].equals("insert")) // Previous local command was "insert"
            client.send(MessageType.INSERT_COMPONENT, Integer.parseInt(localCommand.split(" ")[1]), Integer.parseInt(localCommand.split(" ")[2]), Integer.parseInt(localCommand.split(" ")[3]), Integer.parseInt(localCommand.split(" ")[4]));

        localCommand = "";

        client.send(MessageType.SET_READY);
        event.consume();
    }

    private void placeComponent(GridPane grid, ImageView iv, int row, int col, boolean yours) {
        iv.setFitHeight(yours ? TILE_HEIGHT : OTHER_PLAYERS_TILE_HEIGHT);
        iv.setFitWidth(yours ? TILE_WIDTH : OTHER_PLAYERS_TILE_WIDTH);
        grid.add(iv, col, row);
    }

    void revealComponent(ClientComponent component) {
        ImageView iv = componentMap.get(component.getId());
        String imagePath = "/images/tiles/GT-new_tiles_16_for web" + component.getId() + ".jpg";
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        iv.setImage(image);
    }

    /**
     * Gets the client ship from the game model.
     *
     * @return The ClientShip instance for the current player
     */
    private ClientShip getClientShip(String username) {
        return client.getGameController().getModel().getBoard().getPlayerEntityByUsername(username).getShip();
    }

    private void loadShipImage() {
        String shipImagePath = "/images/cardboard/cardboard-1b.jpg";
        Image shipImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(shipImagePath)));
        shipImageView.setImage(shipImage);
        shipImageView.setPreserveRatio(true);
        shipImageView.setOpacity(0.3);
    }

    @Override
    public void handleMessage(GameEvent event) {
        switch (event) {
            case ComponentPickedEvent e -> {
                ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(e.id());
                revealComponent(component);

                if (!e.username().equals(client.getUsername())) { // Not your action
                    componentsFlowPane.getChildren().remove(componentMap.get(e.id()));
                }
                else { // Your action
                    componentMap.get(e.id()).addEventHandler(MouseEvent.MOUSE_CLICKED, componentOnMouseClickedHandler);
                }

            }
            case ComponentInsertedEvent e -> {
                ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(e.id());
                ImageView iv = componentMap.get(component.getId());

                if (e.username().equals(client.getUsername())) { // Your action
                    componentsFlowPane.getChildren().remove(componentMap.get(e.id()));
                    shipGrid.getChildren().remove(iv);
                    placeComponent(shipGrid, iv, e.row(), e.col(), true);

                    if (slotMap.containsKey(e.id())) { // Was in reserves
                        Rectangle sourceSlot = slotMap.get(e.id());
                        sourceSlot.setOnMouseEntered(slotOnMouseEnteredHandler);
                        sourceSlot.setOnMouseExited(slotOnMouseExitedHandler);
                        sourceSlot.setOnDragOver(slotDragOverHandler);
                        sourceSlot.setOnDragDropped(slotDragDroppedHandler);
                    }

                    Rectangle slot = (Rectangle) shipGrid.lookup("#slot_"+e.row()+"_"+e.col());
                    if (slot != null) {
                        slot.removeEventHandler(MouseEvent.MOUSE_ENTERED, slotOnMouseEnteredHandler);
                        slot.removeEventHandler(MouseEvent.MOUSE_EXITED, slotOnMouseExitedHandler);
                        slot.removeEventHandler(DragEvent.DRAG_OVER, slotDragOverHandler);
                        slot.removeEventHandler(DragEvent.DRAG_DROPPED, slotDragDroppedHandler);
                        slotMap.put(component.getId(), slot);
                    }
                }
                else { // Not your action
                    GridPane playerGrid = (GridPane) otherPlayersContainer.lookup("#ship_"+e.username());
                    playerGrid.getChildren().remove(iv);
                    placeComponent(playerGrid, iv, e.row(), e.col(), false);
                }

                if (component.isInserted()) { // Component is welded, remove listeners
                    iv.setOnMouseEntered(null);
                    iv.setOnMouseExited(null);
                    iv.setOnMouseDragExited(null);
                    iv.setOnDragDetected(null);
                    iv.removeEventHandler(MouseEvent.MOUSE_CLICKED, componentOnMouseClickedHandler);
                }

            }
            case ComponentReleasedEvent e -> {
                ImageView iv = componentMap.get(e.id());

                if (e.username().equals(client.getUsername())) { // Your action
                    iv.removeEventHandler(MouseEvent.MOUSE_CLICKED, componentOnMouseClickedHandler);

                    if (slotMap.containsKey(e.id())) { // Release from ship
                        shipGrid.getChildren().remove(iv);
                        Rectangle slot = slotMap.get(e.id());
                        slot.setOnMouseEntered(slotOnMouseEnteredHandler);
                        slot.setOnMouseExited(slotOnMouseExitedHandler);
                        slot.setOnDragOver(slotDragOverHandler);
                        slot.setOnDragDropped(slotDragDroppedHandler);
                        slotMap.remove(e.id());
                    }
                }
                else { // Not your action
                    iv.setFitWidth(TILE_WIDTH);
                    iv.setFitHeight(TILE_HEIGHT);
                }

                if (!componentsFlowPane.getChildren().contains(iv))
                    componentsFlowPane.getChildren().add(iv);
                iv.setRotate(0);
            }
            case ComponentReservedEvent e -> {
                int reservesSize = getClientShip(e.username()).getReserves().size();

                int slotCol = 4 + reservesSize;
                Rectangle slot = (Rectangle) shipGrid.lookup("#slot_0_"+slotCol);
                ImageView iv = componentMap.get(e.id());

                if (e.username().equals(client.getUsername())) { // Your action
                    if (!slotMap.containsKey(e.id())) { // It was in flow pane
                        componentsFlowPane.getChildren().remove(componentMap.get(e.id()));
                    }
                    else { // It was in ship
                        shipGrid.getChildren().remove(iv);
                        Rectangle sourceSlot = slotMap.get(e.id());
                        sourceSlot.setOnMouseEntered(slotOnMouseEnteredHandler);
                        sourceSlot.setOnMouseExited(slotOnMouseExitedHandler);
                        sourceSlot.setOnDragOver(slotDragOverHandler);
                        sourceSlot.setOnDragDropped(slotDragDroppedHandler);
                    }

                    placeComponent(shipGrid, iv, 0, slotCol, true);
                    slotMap.put(e.id(), slot);
                    slot.removeEventHandler(MouseEvent.MOUSE_ENTERED, slotOnMouseEnteredHandler);
                    slot.removeEventHandler(MouseEvent.MOUSE_EXITED, slotOnMouseExitedHandler);
                    slot.removeEventHandler(DragEvent.DRAG_OVER, slotDragOverHandler);
                    slot.removeEventHandler(DragEvent.DRAG_DROPPED, slotDragDroppedHandler);
                }
                else { // Not your action
                    placeComponent((GridPane) otherPlayersContainer.lookup("#ship_"+e.username()), iv, 0, slotCol, false);

                    iv.setOnMouseEntered(null);
                    iv.setOnMouseExited(null);
                    iv.setOnMouseDragExited(null);
                    iv.setOnDragDetected(null);
                    iv.removeEventHandler(MouseEvent.MOUSE_CLICKED, componentOnMouseClickedHandler);
                }

            }
            case ComponentMovedEvent e -> {
                ImageView iv = componentMap.get(e.id());

                if (e.username().equals(client.getUsername())) { // Your action
                    shipGrid.getChildren().remove(iv);
                    placeComponent(shipGrid, iv, e.row(), e.col(), true);

                    Rectangle sourceSlot = slotMap.get(e.id());
                    Rectangle destSlot = (Rectangle) shipGrid.lookup("#slot_"+e.row()+"_"+e.col());
                    slotMap.put(e.id(), destSlot);

                    destSlot.removeEventHandler(MouseEvent.MOUSE_ENTERED, slotOnMouseEnteredHandler);
                    destSlot.removeEventHandler(MouseEvent.MOUSE_EXITED, slotOnMouseExitedHandler);
                    destSlot.removeEventHandler(DragEvent.DRAG_OVER, slotDragOverHandler);
                    destSlot.removeEventHandler(DragEvent.DRAG_DROPPED, slotDragDroppedHandler);

                    sourceSlot.setOnMouseEntered(slotOnMouseEnteredHandler);
                    sourceSlot.setOnMouseExited(slotOnMouseExitedHandler);
                    sourceSlot.setOnDragOver(slotDragOverHandler);
                    sourceSlot.setOnDragDropped(slotDragDroppedHandler);
                }
                else { // Not your action
                    GridPane grid = (GridPane) otherPlayersContainer.lookup("#ship_"+e.username());
                    grid.getChildren().remove(iv);
                    placeComponent(grid, iv, e.row(), e.col(), false);
                }

            }
            case ComponentRotatedEvent e -> {
                ImageView iv = componentMap.get(e.id());
                iv.setRotate(e.rotations()*90 + (e.rotations() == 1 ? iv.getRotate() : 0));
            }
            case CardPileLookedEvent e -> {

                overlayManager.showOverlay(() -> client.send(MessageType.RELEASE_CARD_PILE));

                HBox centralBox = overlayManager.getCentralHBox();
                centralBox.setAlignment(Pos.CENTER);
                centralBox.setSpacing(10);

                for (ClientCard card : e.cards()) {
                    ImageView iw = new ImageView();
                    iw.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cards/"+card.getId()+".jpg"))));
                    iw.setFitWidth(196.0);
                    iw.setFitHeight(296.0);
                    iw.setLayoutX(2.0);
                    iw.setLayoutY(2.0);
                    iw.setPickOnBounds(true);
                    iw.setPreserveRatio(true);
                    centralBox.getChildren().add(iw);
                }

            }
            case PlayersStateUpdatedEvent e -> {

                if (e.states().get(client.getUsername()) == PlayerState.WAIT)
                    setReadyButton.setVisible(false);

                boolean allReady = e.states().values().stream().noneMatch(s -> s == PlayerState.BUILD || s == PlayerState.LOOK_CARD_PILE);
                if (allReady) {
                    SceneManager.navigateToScene("/fxml/gameFlight.fxml", this, (FlightPhaseController controller) -> {
                        controller.setImageMap(componentMap);
                    });

                    return;
                }

                for (String username : e.states().keySet()) {
                    if (username.equals(client.getUsername())) continue;

                    ClientPlayer player = client.getGameController().getModel().getBoard().getPlayerEntityByUsername(username);
                    PlayerState state = e.states().get(username);

                    String text = "";
                    if (state == PlayerState.BUILD)
                        text = "building";
                    else if (state == PlayerState.LOOK_CARD_PILE)
                        text = "looking a card pile";
                    else if (state == PlayerState.WAIT && !player.hasEndedInAdvance())
                        text = "ready";

                    if (!text.isEmpty())
                        ((Label) otherPlayersContainer.lookup("#state_"+username)).setText(text);
                }

            }
            case LeftLobbyEvent e -> {
                if (e.username().equals(client.getUsername())) // Your action
                    SceneManager.navigateToScene("/fxml/menu.fxml", this, null);
                else // Not your action
                    ((Label) otherPlayersContainer.lookup("#state_"+e.username())).setText("left game");
            }
            case JoinedLobbyEvent e -> ((Label) otherPlayersContainer.lookup("#state_"+e.username())).setText("ended flight");
            case HourglassMovedEvent _ -> {
                hourglassPos.setText("Position: " + client.getGameController().getModel().getBoard().getHourglassPos());
                timerContainer.setOnMouseClicked(null);
                startCountdown();
            }
            case GameErrorEvent e -> statusLabel.setText(e.message());
            default -> {}
        }
    }

    @Override
    public boolean canHandle(MessageType messageType) {
        return List.of(
                MessageType.ERROR,
                MessageType.COMPONENT_PICKED_EVENT,
                MessageType.COMPONENT_INSERTED_EVENT,
                MessageType.COMPONENT_RELEASED_EVENT,
                MessageType.COMPONENT_RESERVED_EVENT,
                MessageType.COMPONENT_ROTATED_EVENT,
                MessageType.COMPONENT_MOVED_EVENT,
                MessageType.HOURGLASS_MOVED_EVENT,
                MessageType.CARD_PILE_LOOKED_EVENT,
                MessageType.CARD_PILE_RELEASED_EVENT,
                MessageType.PLAYERS_STATE_UPDATED_EVENT,
                MessageType.LEFT_LOBBY_EVENT
        ).contains(messageType);
    }

}