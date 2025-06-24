package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.components.ClientCabinComponent;
import it.polimi.ingsw.client.model.components.ClientComponent;
import it.polimi.ingsw.client.model.components.ClientOddComponent;
import it.polimi.ingsw.client.model.player.ClientShip;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.common.model.events.game.ComponentDestroyedEvent;
import it.polimi.ingsw.common.model.events.game.CrewUpdatedEvent;
import it.polimi.ingsw.common.model.events.game.PlayersStateUpdatedEvent;
import it.polimi.ingsw.common.model.events.game.ShipBrokenEvent;
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
import java.util.stream.IntStream;

public class CheckShipController implements MessageHandler, Initializable {

    private Client client;

    private PlayerState mode;

    private final Map<Integer, Rectangle> selectedComponents = new HashMap<>();
    private final Map<Integer, ImageView> componentMap = new HashMap<>();

    // alien mode
    private final Map<Integer, Rectangle> cabinSlots = new HashMap<>();
    private AlienType selectedAlienType;
    private final Map<Integer, AlienType> alienPlacements = new HashMap<>();

    // ship_parts mode
    private List<List<Integer>> shipParts;
    private final Map<Integer, Color> partColors = new HashMap<>();        // partId -> colore
    private Integer selectedPartId;


    @FXML
    private GridPane shipGrid;

    @FXML
    private Button doneBtn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MessageDispatcher.getInstance().registerHandler(this);
        this.client = App.getClientInstance();

        loadComponentMap();
        loadShip();

        updateBtnStatus();
    }


    private void generatePartColors() {
        this.partColors.clear();

        for (int i = 0; i < shipParts.size(); i++) {
            // generate random color for part
            double hue = (360.0 / shipParts.size()) * i;
            Color distinctColor = Color.hsb(hue, 0.7, 0.8);
            // save ship ('i' will be the id of the part)
            partColors.put(i, distinctColor);
        }
    }

    private void setupShipPartMode(Rectangle slot, ClientComponent c) {
        int partId = IntStream.range(0, shipParts.size())
                .filter(i -> shipParts.get(i).contains(c.getId()))
                .findFirst().getAsInt();

        Color partColor = partColors.get(partId);
        slot.setFill(partColor.deriveColor(0, 1, 1, 0.5));

        // hover in
        slot.setOnMouseEntered(_ ->
            shipParts.get(partId).forEach(componentId -> {
                Rectangle s = selectedComponents.get(componentId);
                s.setFill(partColor.brighter());
            })
        );

        // hover out
        slot.setOnMouseExited(_ -> {
            Color exitColor = Objects.equals(selectedPartId, partId)
                    ? partColor.brighter()
                    : partColor.deriveColor(0, 1, 1, 0.5);
            shipParts.get(partId).forEach(componentId -> {
                Rectangle s = selectedComponents.get(componentId);
                s.setFill(exitColor);
            });
        });

        // on click
        slot.setOnMouseClicked(_ -> {
            // delete selection or new selection
            selectedPartId = Objects.equals(selectedPartId, partId) ? null : partId;
            updateBtnStatus();
        });
    }


    private void setupCheckMode(Rectangle slot, Integer componentId) {
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

            updateBtnStatus();
        });
    }


    private void clearAlienSelection() {
        selectedComponents.values().forEach(s -> s.setFill(Color.TRANSPARENT));
        selectedComponents.clear();
        selectedAlienType = null;
    }

    private void toggleCabinInteractions(boolean enable) {
        cabinSlots.forEach((cabinId, slot) -> {
            // appearance
            if (alienPlacements.containsKey(cabinId)) {
                AlienType type = alienPlacements.get(cabinId);
                slot.setFill((type == AlienType.ENGINE ? Color.GOLD : Color.MEDIUMPURPLE).deriveColor(0, 1, 1, 0.7));
            } else {
                slot.setFill(Color.TRANSPARENT);
            }
            slot.setStroke(Color.TRANSPARENT);
            slot.setStrokeWidth(0);

            // interaction
            boolean interactive = enable && selectedAlienType != null;
            slot.setMouseTransparent(!interactive);

            if (interactive) {
                Color alienColor = selectedAlienType == AlienType.ENGINE ? Color.GOLD : Color.MEDIUMPURPLE;
                slot.setOnMouseEntered(_ -> slot.setFill(alienColor.deriveColor(0, 1, 0.8, 0.8)));
                slot.setOnMouseExited(_ -> toggleCabinInteractions(true)); // recursive refresh
                slot.setOnMouseClicked(_ -> {
                    alienPlacements.put(cabinId, selectedAlienType);

                    clearAlienSelection();

                    toggleCabinInteractions(false); // full refresh
                    updateBtnStatus();
                });
            } else {
                slot.setOnMouseEntered(null);
                slot.setOnMouseExited(null);
                slot.setOnMouseClicked(null);
            }
        });
    }

    private void setupCabinComponent(Rectangle slot, ClientCabinComponent c) {
        cabinSlots.put(c.getId(), slot);
        toggleCabinInteractions(selectedAlienType != null);
    }

    private void setupAlienMode(Rectangle slot, ClientComponent c) {
        if (c instanceof ClientOddComponent odd) {
            Color alienColor = odd.getType() == AlienType.ENGINE ? Color.GOLD : Color.MEDIUMPURPLE;

            // hover in
            slot.setOnMouseEntered(_ -> slot.setFill(
                    selectedAlienType == odd.getType() ?
                            alienColor.brighter() :           // brighter if already selected
                            alienColor.deriveColor(0, 1, 0.8, 0.8)  // normal hover color
            ));

            // hover out
            slot.setOnMouseExited(_ -> slot.setFill(
                    selectedAlienType == odd.getType() ?
                            alienColor :           // keep color if selected
                            Color.TRANSPARENT      // back to transparent if not selected
            ));

            // on click
            slot.setOnMouseClicked(_ -> {
                if (selectedAlienType == odd.getType()) {  // deselect: click on the same odd
                    clearAlienSelection();

                    toggleCabinInteractions(false); // disable cabins
                } else { // select new: click on new odd
                    clearAlienSelection();

                    selectedAlienType = odd.getType();   // set new alien type
                    selectedComponents.put(c.getId(), slot);
                    slot.setFill(alienColor);
                    toggleCabinInteractions(true);  // enable cabins
                }

                updateBtnStatus();
            });
        }
        else if (c instanceof ClientCabinComponent)
            setupCabinComponent(slot, (ClientCabinComponent) c);
        else
            slot.setMouseTransparent(true);
    }


    private Rectangle createShipSlot(Optional<ClientComponent> component) {
        Rectangle slot = new Rectangle(70, 70);

        if (component.isPresent()) {
            ClientComponent c = component.get();
            switch (mode) {
                case CHECK -> setupCheckMode(slot, c.getId());
                case WAIT_ALIEN -> setupAlienMode(slot, c);
                case WAIT_SHIP_PART -> setupShipPartMode(slot, c);
            }
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
        iv.setImage(image);

        return iv;
    }

    private void loadComponentMap() {
        List<ClientComponent> components = client.getGameController().getModel().getBoard().getMapIdComponents().values().stream().toList();
        for (ClientComponent c : components) {
            ImageView iv = loadComponentImage(c);
            componentMap.put(c.getId(), iv);
        }
    }


    private void updateBtnStatus() {
        boolean enabled = switch (mode) {
            case CHECK -> !selectedComponents.isEmpty();
            case WAIT_ALIEN -> !alienPlacements.isEmpty();
            case WAIT_SHIP_PART -> selectedPartId != null;
            default -> false;
        };
        doneBtn.setDisable(!enabled);
    }

    @FXML
    public void updateShip() {
        switch (mode) {
            case CHECK -> {
                List<Integer> componentIds = new ArrayList<>(selectedComponents.keySet());
                client.send(MessageType.CHECK_SHIP, componentIds);
            }
            case WAIT_ALIEN -> client.send(MessageType.CHOOSE_ALIEN, alienPlacements);
            case WAIT_SHIP_PART -> client.send(MessageType.CHOOSE_SHIP_PART, selectedPartId);
        }
    }


    @Override
    public void handleMessage(GameEvent event) {
        switch (event) {
            case ComponentDestroyedEvent _ -> {
                if (mode.equals(PlayerState.WAIT_SHIP_PART)) {
                    List<Integer> ids = shipParts.get(selectedPartId);
                    ids.forEach(id -> {
                        ImageView imageToRm = componentMap.get(id);
                        shipGrid.getChildren().remove(imageToRm);

                        Rectangle slot = selectedComponents.get(id);
                        slot.setFill(Color.TRANSPARENT);
                        slot.setMouseTransparent(true);
                    });
                    selectedPartId = null;
                } else {
                    for (Map.Entry<Integer, Rectangle> entry : selectedComponents.entrySet()) {
                        Integer componentId = entry.getKey();

                        ImageView imageToRm = componentMap.get(componentId);
                        shipGrid.getChildren().remove(imageToRm);

                        Rectangle slot = entry.getValue();
                        slot.setFill(Color.TRANSPARENT);
                        slot.setMouseTransparent(true);
                    }
                    selectedComponents.clear();
                }
                updateBtnStatus();
            }
            case ShipBrokenEvent e -> {
                this.shipParts = e.parts();
                generatePartColors();
            }
            case CrewUpdatedEvent _ -> {
                // todo: da vedere gli alieni visivamente sulle cabine della mappa alienPlacements!
            }
            case PlayersStateUpdatedEvent e ->
                    this.mode = e.states().get(client.getUsername());
            default -> {}
        }
    }

    @Override
    public boolean canHandle(MessageType messageType) {
        return List.of(
                MessageType.COMPONENT_DESTROYED_EVENT,
                MessageType.SHIP_BROKEN_EVENT,
                MessageType.CREW_UPDATED_EVENT,
                MessageType.PLAYERS_STATE_UPDATED_EVENT
        ).contains(messageType);
    }

}
