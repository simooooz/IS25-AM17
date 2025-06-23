package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.components.ClientComponent;
import it.polimi.ingsw.client.model.player.ClientShip;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.common.model.events.game.*;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.GUI.App;
import it.polimi.ingsw.view.GUI.MessageDispatcher;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.*;

/**
 * Controller for the ship building interface. Handles component placement on the ship grid
 * and manages the visual representation of available components. Implements performance
 * optimizations through caching and incremental updates.
 */
public class BuildController implements MessageHandler, Initializable {

    @FXML private ImageView shipImageView;
    @FXML private GridPane shipGrid;
    @FXML private FlowPane componentsFlowPane;

    private Client client;

    private final Map<String, Rectangle> slotMap = new HashMap<>();
    private final Map<Integer, ImageView> componentMap = new HashMap<>();

    EventHandler<DragEvent> slotDragOverHandler = (DragEvent event) -> {
        if (event.getDragboard().getString().contains("component_"))
            event.acceptTransferModes(TransferMode.MOVE);
        event.consume();
    };


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MessageDispatcher.getInstance().registerHandler(this);
        this.client = App.getClientInstance();

        loadShipImage();
        setupComponentMap();
        setupInitialShipGrid();
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

    /**
     * Initial setup of the ship grid (performed once).
     * Creates all slots and caches them for performance.
     */
    private void setupInitialShipGrid() {
        ClientShip ship = getClientShip();

        shipGrid.setPrefWidth(500.0);
        shipGrid.setPrefHeight(400.0);

        for (int row = 0; row < Constants.SHIP_ROWS; row++) {
            for (int col = 0; col < Constants.SHIP_COLUMNS; col++) {
                if (ship.validPositions(row, col)) {
                    Rectangle slot = createShipSlot(row, col);
                    shipGrid.add(slot, col, row);

                    Optional<ClientComponent> component = ship.getDashboard(row, col);
                    if (component.isPresent())
                        shipGrid.add(componentMap.get(component.get().getId()), col, row);

                }
                else {
                    Rectangle emptySlot = new Rectangle(70, 70);
                    emptySlot.setFill(Color.TRANSPARENT);
                    emptySlot.setStroke(Color.TRANSPARENT);
                    emptySlot.setMouseTransparent(true);
                    shipGrid.add(emptySlot, col, row);
                }
            }
        }

    }

    /**
     * Places a component (incremental update).
     * Updates only the affected slot and removes the component card.
     *
     * @param component The component to place
     * @param row       The row position
     * @param col       The column position
     * @param rotation  The rotation angle for the component
     */
    private void placeComponent(GridPane grid, ClientComponent component, int row, int col, int rotation) {
        ImageView iv = componentMap.get(component.getId());
        grid.add(iv, col, row);
    }

    private void removeComponent(GridPane grid, ClientComponent component, int row, int col, int rotation) {
        ImageView iv = componentMap.get(component.getId());
        grid.getChildren().remove(iv);
    }

    /**
     * Creates a ship slot Rectangle with drag and drop functionality.
     *
     * @param row The row position of the slot
     * @param col The column position of the slot
     * @return A configured Rectangle representing the ship slot
     */
    private Rectangle createShipSlot(int row, int col) {
        Rectangle slot = new Rectangle(70, 70);
        slot.setFill(Color.TRANSPARENT);
        slot.setId("slot_" + row + "_" + col);
        slot.setStroke(Color.rgb(150, 150, 255, 0.6));
        slot.setStrokeWidth(1.5);
        slot.setOpacity(0.8);
        slotMap.put(slot.getId(), slot);

        slot.setOnMouseEntered(_ -> {
            slot.setFill(Color.LIGHTBLUE.deriveColor(0, 1, 1, 0.4));
            slot.setStroke(Color.LIGHTBLUE);
            slot.setStrokeWidth(2);
        });

        slot.setOnMouseExited(_ -> {
            slot.setFill(Color.TRANSPARENT);
            slot.setStroke(Color.rgb(150, 150, 255, 0.6));
            slot.setStrokeWidth(1.5);
        });

        slot.setOnDragOver(slotDragOverHandler);

        return slot;
    }

    /**
     * Creates a component card ImageView with interaction handlers.
     *
     * @param component The component to create a card for
     * @return A configured ImageView representing the component card
     */
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
            iv.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,255,0,0.8), 8, 0, 3, 3); -fx-cursor: hand;");
        }
        else {
            iv.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/sfondo.jpg"))));
            iv.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 6, 0, 2, 2); -fx-cursor: hand;");
        }

        iv.setOnMouseEntered(_ -> {
            iv.setScaleX(1.05);
            iv.setScaleY(1.05);
        });

        iv.setOnMouseExited(_ -> {
            iv.setScaleX(1.0);
            iv.setScaleY(1.0);
        });

        iv.setOnDragDetected(event -> {
            if (client.getGameController().getModel().getBoard().getCommonComponents().contains(component)) { // Component picked from flow pane
                client.send(MessageType.PICK_COMPONENT, component.getId());
            }

            Dragboard db = iv.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("component_" + component.getId());
            db.setContent(content);
            db.setDragView(iv.snapshot(null, null), 35, 35);

            event.consume();
        });

        iv.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) { // Component dragged
                Node targetNode = (Node) event.getGestureTarget();
                Node sourceNode = (Node) event.getGestureSource();
                String destId = targetNode.getId();
                String sourceId = sourceNode.getId();

                System.out.println(sourceId);
                System.out.println(destId);
                if (sourceId.equals("componentsFlowPane")) { // Picked
                    int row = Integer.parseInt(destId.split("_")[1]);
                    int col = Integer.parseInt(destId.split("_")[2]);
                    client.send(MessageType.INSERT_COMPONENT, component.getId(), row, col, 0);
                }
                else if (sourceId.contains("slot") && destId.contains("slot")) { // Moved
                    int newRow = Integer.parseInt(destId.split("_")[1]);
                    int newCol = Integer.parseInt(destId.split("_")[2]);
                    client.send(MessageType.MOVE_COMPONENT, component.getId(), newRow, newCol, 0);
                }
                else if (sourceId.contains("client") && destId.equals("componentsFlowPane")) { // Released
                    client.send(MessageType.RELEASE_COMPONENT, component.getId());
                }
            }
            else { // Component not dragged
                client.send(MessageType.RELEASE_COMPONENT, component.getId());
            }
            event.consume();
        });

        return iv;
    }

    private void revealComponent(ClientComponent component) {
        ImageView iv = componentMap.get(component.getId());
        String imagePath = "/images/tiles/GT-new_tiles_16_for web" + component.getId() + ".jpg";
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        iv.setImage(image);
        iv.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,255,0,0.8), 8, 0, 3, 3); -fx-cursor: hand;");
    }

    /**
     * Gets the client ship from the game model.
     *
     * @return The ClientShip instance for the current player
     */
    private ClientShip getClientShip() {
        return client.getGameController().getModel().getBoard().getPlayerEntityByUsername(client.getUsername()).getShip();
    }

    /**
     * Loads the ship background image based on the game mode.
     * Runs asynchronously to avoid blocking the UI thread.
     */
    private void loadShipImage() {
        // Get game mode information
        String shipImagePath = getShipImagePathForGameMode();
        System.out.println("🚢 Loading ship image: " + shipImagePath);

        Image shipImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(shipImagePath)));
        shipImageView.setImage(shipImage);
        shipImageView.setPreserveRatio(true);
        shipImageView.setOpacity(0.3);
    }

    /**
     * Determines the ship image path based on the game mode.
     *
     * @return The path to the appropriate ship image
     */
    private String getShipImagePathForGameMode() {
        try {
            ClientShip ship = getClientShip();

            // Use class name to determine game mode
            String shipClassName = ship.getClass().getSimpleName();
            System.out.println("🔍 Ship mode: " + shipClassName);

            switch (shipClassName) {
                case "ClientShipLearnerMode" -> {
                    return "/images/cardboard/cardboard-1.jpg";
                }
                case "ClientShipAdvancedMode" -> {
                    return "/images/cardboard/cardboard-1b.jpg";
                }
                default -> {
                    System.out.println("⚠️ Unknown ship mode: " + shipClassName + ", using default");
                }
            }

        } catch (Exception e) {
            System.err.println("Error determining game mode: " + e.getMessage());
        }
        return "";
    }

    @Override
    public void handleMessage(GameEvent event) {
        switch (event) {
            case ComponentPickedEvent e -> {
                ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(e.id());
                componentsFlowPane.getChildren().remove(componentMap.get(e.id()));
                revealComponent(component);
            }
            case ComponentInsertedEvent e -> {
                ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(e.id());
                placeComponent(shipGrid, component, e.row(), e.col(), 0);
                Rectangle slot = slotMap.get("slot_"+e.row()+"_"+e.col());
                slot.removeEventHandler(DragEvent.DRAG_OVER, slotDragOverHandler);
            }
            case ComponentReleasedEvent e -> {
                ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(e.id());
                componentsFlowPane.getChildren().add(componentMap.get(component.getId()));

                if (shipGrid.lookup("component_"+e.id()) != null) { // Release from ship
                    removeComponent(shipGrid, component, component.getY(), component.getX(), 0);
                    Rectangle slot = slotMap.get("slot_"+component.getY()+"_"+component.getX());
                    slot.setOnDragOver(slotDragOverHandler);
                }
            }
            case ComponentMovedEvent e -> {
                ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(e.id());

                removeComponent(shipGrid, component, e.oldRow(), e.oldCol(), 0);
                placeComponent(shipGrid, component, e.row(), e.col(), 0);

                Rectangle sourceSlot = slotMap.get("slot_"+e.oldRow()+"_"+e.oldCol());
                Rectangle destSlot = slotMap.get("slot_"+e.row()+"_"+e.col());

                sourceSlot.setOnDragOver(slotDragOverHandler);
                destSlot.removeEventHandler(DragEvent.DRAG_OVER, slotDragOverHandler);
            }
            case GameErrorEvent e -> System.err.println("Error: " + e.message());
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
                MessageType.COMPONENT_MOVED_EVENT
        ).contains(messageType);
    }
}