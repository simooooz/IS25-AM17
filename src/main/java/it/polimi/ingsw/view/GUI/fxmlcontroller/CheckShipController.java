package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.components.ClientComponent;
import it.polimi.ingsw.client.model.player.ClientShip;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.common.model.events.game.ComponentDestroyedEvent;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.GUI.App;
import it.polimi.ingsw.view.GUI.MessageDispatcher;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.*;

public class CheckShipController implements MessageHandler, Initializable {

    private Client client;

    private final Map<Integer, Rectangle> selectedComponents = new HashMap<>();
    private final Map<Integer, ImageView> componentMap = new HashMap<>();

    @FXML
    GridPane shipGrid;

    @FXML
    Button checkBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MessageDispatcher.getInstance().registerHandler(this);
        this.client = App.getClientInstance();

        loadComponentMap();
        loadShip();

        updateCheckBtnStatus();
    }

    private Rectangle createShipSlot(Optional<ClientComponent> component) {
        Rectangle slot = new Rectangle(70, 70);

        if (component.isPresent()) {
            Integer componentId = component.get().getId();
            slot.setOnMouseEntered(_ -> {

                if (selectedComponents.containsKey(componentId))
                    slot.setFill(Color.DARKRED);
                else
                    slot.setFill(Color.ORANGE);
            });

            slot.setOnMouseExited(_ -> {
                if (selectedComponents.containsKey(componentId))
                    slot.setFill(Color.RED);
                else
                    slot.setFill(Color.TRANSPARENT);
            });

            slot.setOnMouseClicked(_ -> {
                if (selectedComponents.containsKey(componentId))
                    selectedComponents.remove(componentId);
                else
                    selectedComponents.put(componentId, slot);

                updateCheckBtnStatus();
            });
        }

        return slot;
    }

    private void loadShip() {
        ClientShip ship = client.getGameController().getModel().getBoard().getPlayerEntityByUsername(client.getUsername()).getShip();

        shipGrid.setPrefWidth(500.0);
        shipGrid.setPrefHeight(400.0);

        for (int row = 0; row < Constants.SHIP_ROWS; row++) {
            for (int col = 0; col < Constants.SHIP_COLUMNS; col++) {
                if (ship.validPositions(row, col)) {
                    Optional<ClientComponent> component = ship.getDashboard(row, col);

                    Rectangle slot = createShipSlot(component);
                    if (component.isPresent()) {
                        // adding image on the grid
                        shipGrid.add(componentMap.get(component.get().getId()), col, row);

                        // can't remove the main cabin
                        if (col == 2 && row == 3)
                            slot.setMouseTransparent(true);

                        slot.setFill(Color.TRANSPARENT);
                    } else {
                        slot.setMouseTransparent(true);
                    }
                    shipGrid.add(slot, col, row);

                } else {
                    Rectangle emptySlot = new Rectangle(70, 70);
                    emptySlot.setFill(Color.TRANSPARENT);
                    emptySlot.setStroke(Color.TRANSPARENT);
                    emptySlot.setMouseTransparent(true);
                    shipGrid.add(emptySlot, col, row);
                }
            }
        }

    }

    private ImageView loadComponentImage(ClientComponent component) {
        ImageView iv = new ImageView();
        iv.setFitWidth(70);
        iv.setFitHeight(70);
        iv.setPreserveRatio(true);

        String imagePath = "/images/tiles/GT-new_tiles_16_for web" + component.getId() + ".jpg";
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        // todo: rotations?
        iv.setImage(image);

        return iv;
    }

    private void loadComponentMap() {
        List<ClientComponent> components = client.getGameController().getModel().getBoard().getMapIdComponents().values().stream().toList();
        for (ClientComponent c : components) {
            ImageView iv = loadComponentImage(c);
            // todo: se vengono cambiati glli id (random) allora come?
            if (c.getId() >= 32 || c.getId() <= 36)
                iv.setMouseTransparent(true);
            componentMap.put(c.getId(), iv);
        }
    }

    private void updateCheckBtnStatus() {
        checkBtn.setDisable(selectedComponents.isEmpty());
    }

    @FXML
    public void updateShip() {
        List<Integer> componentIds = new ArrayList<>(selectedComponents.keySet());
        client.send(MessageType.CHECK_SHIP, componentIds);
    }


    @Override
    public void handleMessage(GameEvent event) {
        if (Objects.requireNonNull(event) instanceof ComponentDestroyedEvent) {
            for (Map.Entry<Integer, Rectangle> entry : selectedComponents.entrySet()) {
                Integer componentId = entry.getKey();

                ImageView imageToRm = componentMap.get(componentId);
                shipGrid.getChildren().remove(imageToRm);

                Rectangle slot = entry.getValue();
                slot.setFill(Color.TRANSPARENT);
                slot.setMouseTransparent(true);
            }

            selectedComponents.clear();
            updateCheckBtnStatus();
        }
    }

    @Override
    public boolean canHandle(MessageType messageType) {
        return false;
    }

}
