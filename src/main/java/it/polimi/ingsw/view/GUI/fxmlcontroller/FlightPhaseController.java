package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.client.model.cards.ClientPlanetCard;
import it.polimi.ingsw.client.model.components.*;
import it.polimi.ingsw.client.model.events.CardRevealedEvent;
import it.polimi.ingsw.client.model.events.CardUpdatedEvent;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.player.ClientShip;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.common.model.events.game.*;
import it.polimi.ingsw.common.model.events.lobby.JoinedLobbyEvent;
import it.polimi.ingsw.common.model.events.lobby.LeftLobbyEvent;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.GUI.*;
import it.polimi.ingsw.view.GUI.MessageHandler;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;


/**
 * Controller class for managing the flight phase of the game in the GUI.
 * This class handles the main game board, player interactions, drag and drop operations,
 * and various game states during the flight phase.
 */
public class FlightPhaseController implements MessageHandler {

    @FXML public Text playersInGameLabel;
    @FXML public Text playersStartingDeckLabel;

    @FXML public AnchorPane root;

    @FXML private VBox otherPlayersContainer;
    @FXML private ImageView currentCardImage;
    @FXML public ImageView previousCardImage;

    @FXML private ImageView pointImage;
    @FXML private ImageView playerShipImage;
    @FXML private GridPane shipGrid;
    @FXML public FlowPane flowPane;
    @FXML public HBox indexBooleanButtonsContainer;

    @FXML private Button mainButton;
    @FXML private Button endFlightButton;

    @FXML private Label logLabel;
    @FXML private ScrollPane statusScrollPane;

    @FXML private Label mainStatusLabel;
    @FXML private Label instructionLabel;

    @FXML private Label errorLabel;

    @FXML
    private Label cardCounterLabel;
    private int totalCards = 0;

    private Client client;
    private ClientGameModel model;
    private PlayerState state;
    private InstructionDisplayManager instructionManager;

    private final Map<Integer, Pane> paneMap = new HashMap<>();
    private Map<Integer, ImageView> imageMap = new HashMap<>();
    private final Map<Integer, List<ImageView>> objectsMap = new HashMap<>();

    private final List<Integer> list1 = new ArrayList<>();
    private final List<Integer> list2 = new ArrayList<>();
    private List<List<Integer>> shipParts = new ArrayList<>();
    private Integer index = null;
    private boolean decision = false;

    private static final double TILE_WIDTH = 57.6;
    private static final double TILE_HEIGHT = 57.5;
    private static final double OTHER_PLAYERS_TILE_WIDTH = 36.0;
    private static final double OTHER_PLAYERS_TILE_HEIGHT = 35.8;

    EventHandler<DragEvent> acceptDragOverHandler = (DragEvent event) -> {
        event.acceptTransferModes(TransferMode.MOVE);
        event.consume();
    };

    EventHandler<DragEvent> componentDragDroppedHandler = event -> {
        Node targetNode = (Node) event.getTarget();
        ImageView objectImageView = (ImageView) event.getGestureSource();

        String object = (String) objectImageView.getUserData();

        String destId = targetNode.getId();
        int destComponentId = Integer.parseInt(destId.split("_")[1]);
        ClientComponent destComponent = client.getGameController().getModel().getBoard().getMapIdComponents().get(destComponentId);

        String sourceId = event.getDragboard().getString();
        int sourceComponentId;
        ClientComponent sourceComponent = null;
        if (sourceId.contains("component")) {
            sourceComponentId = Integer.parseInt(sourceId.split("_")[1]);
            sourceComponent = client.getGameController().getModel().getBoard().getMapIdComponents().get(sourceComponentId);
        }

        if (state == PlayerState.WAIT_ALIEN && object.contains("alien")) { // Alien in WAIT_ALIEN
            List<Class<?>> allowedClasses = List.of(ClientCabinComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                if (
                    destId.contains("component") && allowedClasses.contains(destComponent.getClass()) && // Dest is allowed component
                    destComponent instanceof ClientCabinComponent && objectsMap.get(destComponentId).size() == 2
                ) {
                    changeComponentObjects(destComponent, 0, List.of());
                    moveComponentObject(objectImageView, destId);
                    if (sourceId.contains("component")) {
                        changeComponentObjects(sourceComponent, 2, List.of("/images/objects/human.png"));
                    }
                    event.setDropCompleted(true);
                }
            }
        } else if (state == PlayerState.WAIT_CANNONS && object.equals("battery") && destId.contains("component") && sourceId.contains("component")) { // Battery in WAIT_CANNONS
            List<Class<?>> allowedClasses = Arrays.asList(ClientCannonComponent.class, ClientBatteryComponent.class);
            if (allowedClasses.containsAll(List.of(destComponent.getClass(), sourceComponent.getClass()))) {
                if (
                    (destComponent instanceof ClientCannonComponent && objectsMap.get(destComponentId).isEmpty() && ((ClientCannonComponent) destComponent).isDouble()) ||
                    (destComponent instanceof ClientBatteryComponent && objectsMap.get(destComponentId).size() < ((ClientBatteryComponent) destComponent).getBatteries() )
                ) {
                    moveComponentObject(objectImageView, destId);
                    event.setDropCompleted(true);
                }
            }
        } else if (state == PlayerState.WAIT_ENGINES && object.equals("battery") && destId.contains("component") && sourceId.contains("component")) { // Battery in WAIT_ENGINES
            List<Class<?>> allowedClasses = Arrays.asList(ClientEngineComponent.class, ClientBatteryComponent.class);
            if (allowedClasses.containsAll(List.of(destComponent.getClass(), sourceComponent.getClass()))) {
                if (
                    (destComponent instanceof ClientEngineComponent && objectsMap.get(destComponentId).isEmpty()) && ((ClientEngineComponent) destComponent).isDouble() ||
                    (destComponent instanceof ClientBatteryComponent && objectsMap.get(destComponentId).size() < ((ClientBatteryComponent) destComponent).getBatteries() )
                ) {
                    moveComponentObject(objectImageView, destId);
                    event.setDropCompleted(true);
                }
            }
        } else if (state == PlayerState.WAIT_SHIELD && object.equals("battery") && destId.contains("component") && sourceId.contains("component")) { // Battery in WAIT_SHIELD
            List<Class<?>> allowedClasses = Arrays.asList(ClientShieldComponent.class, ClientBatteryComponent.class);
            if (allowedClasses.containsAll(List.of(destComponent.getClass(), sourceComponent.getClass()))) {
                if (
                    (destComponent instanceof ClientShieldComponent && objectsMap.get(destComponentId).isEmpty()) ||
                    (destComponent instanceof ClientBatteryComponent && objectsMap.get(destComponentId).size() < ((ClientBatteryComponent) destComponent).getBatteries() )
                ) {
                    moveComponentObject(objectImageView, destId);
                    event.setDropCompleted(true);
                }
            }
        } else if (state == PlayerState.WAIT_REMOVE_GOODS && object.equals("battery")) { // Battery in WAIT_REMOVE_GOODS
            List<Class<?>> allowedClasses = List.of(ClientBatteryComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                if (
                    destId.contains("component") && allowedClasses.contains(destComponent.getClass()) && // Dest is flow pane or allowed component
                    destComponent instanceof ClientBatteryComponent && objectsMap.get(destComponentId).size() < ((ClientBatteryComponent) destComponent).getBatteries()
                ) {
                    moveComponentObject(objectImageView, destId);
                    event.setDropCompleted(true);
                }
            }
        }
        else if ((state == PlayerState.WAIT_REMOVE_GOODS || state == PlayerState.WAIT_GOODS) && object.contains("good")) { // Good in WAIT_REMOVE_GOODS or WAIT_GOODS
            List<Class<?>> allowedClasses = List.of(ClientCargoHoldsComponent.class, ClientSpecialCargoHoldsComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                if (
                    destId.contains("component") && allowedClasses.contains(destComponent.getClass()) && // Dest is allowed component
                    destComponent instanceof ClientSpecialCargoHoldsComponent && objectsMap.get(destComponentId).size() < ((ClientSpecialCargoHoldsComponent) destComponent).getNumber()
                ) {
                    if (!(destComponent instanceof ClientCargoHoldsComponent) || !object.contains("red")) {
                        moveComponentObject(objectImageView, destId);
                        event.setDropCompleted(true);
                    }
                }
            }
        } else if (state == PlayerState.WAIT_REMOVE_CREW && object.equals("human")) { // Human in WAIT_REMOVE_CREW
            List<Class<?>> allowedClasses = List.of(ClientCabinComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                if (
                    destId.contains("component") && allowedClasses.contains(destComponent.getClass()) && // Dest is flow pane or allowed component
                    destComponent instanceof ClientCabinComponent && objectsMap.get(destComponentId).size() < ((ClientCabinComponent) destComponent).getHumans()
                ) {
                    moveComponentObject(objectImageView, destId);
                    event.setDropCompleted(true);
                }
            }
        } else if (state == PlayerState.WAIT_REMOVE_CREW && object.contains("alien")) { // Alien in WAIT_REMOVE_CREW
            List<Class<?>> allowedClasses = List.of(ClientCabinComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                if (
                    destId.contains("component") && allowedClasses.contains(destComponent.getClass()) && // Dest is flow pane or allowed component
                    objectsMap.get(destComponentId).isEmpty() &&
                    ((ClientCabinComponent) destComponent).getAlien() != null && ((ClientCabinComponent) destComponent).getAlien().toString().equals(object.toUpperCase().split("-")[1])
                ) {
                    moveComponentObject(objectImageView, destId);
                    event.setDropCompleted(true);
                }
            }
        }

        if (!event.isDropCompleted())
            event.setDropCompleted(false);
        event.consume();
    };

    EventHandler<DragEvent> flowPaneDragDroppedHandler = event -> {
        Node targetNode = (Node) event.getTarget();
        ImageView objectImageView = (ImageView) event.getGestureSource();
        String object = (String) objectImageView.getUserData();
        String destId = targetNode.getId();

        String sourceId = event.getDragboard().getString();
        int sourceComponentId;
        ClientComponent sourceComponent = null;
        if (sourceId.contains("component")) {
            sourceComponentId = Integer.parseInt(sourceId.split("_")[1]);
            sourceComponent = client.getGameController().getModel().getBoard().getMapIdComponents().get(sourceComponentId);
        }

        if (state == PlayerState.WAIT_ALIEN && object.contains("alien")) { // Alien in WAIT_ALIEN
            List<Class<?>> allowedClasses = List.of(ClientCabinComponent.class);
            if (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass())) { // Source is allowed component
                moveComponentObject(objectImageView, destId);
                changeComponentObjects(sourceComponent, 2, List.of("/images/objects/human.png"));
                event.setDropCompleted(true);
            }
        } else if (state == PlayerState.WAIT_REMOVE_GOODS && object.equals("battery")) { // Battery in WAIT_REMOVE_GOODS
            List<Class<?>> allowedClasses = List.of(ClientBatteryComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                moveComponentObject(objectImageView, destId);
                event.setDropCompleted(true);
            }
        }
        else if ((state == PlayerState.WAIT_REMOVE_GOODS || state == PlayerState.WAIT_GOODS) && object.contains("good")) { // Good in WAIT_REMOVE_GOODS or WAIT_GOODS
            List<Class<?>> allowedClasses = List.of(ClientCargoHoldsComponent.class, ClientSpecialCargoHoldsComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                moveComponentObject(objectImageView, destId);
                event.setDropCompleted(true);
            }
        } else if (state == PlayerState.WAIT_REMOVE_CREW && object.equals("human")) { // Human in WAIT_REMOVE_CREW
            List<Class<?>> allowedClasses = List.of(ClientCabinComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                moveComponentObject(objectImageView, destId);
                event.setDropCompleted(true);
            }
        } else if (state == PlayerState.WAIT_REMOVE_CREW && object.contains("alien")) { // Alien in WAIT_REMOVE_CREW
            List<Class<?>> allowedClasses = List.of(ClientCabinComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                moveComponentObject(objectImageView, destId);
                event.setDropCompleted(true);
            }
        }

        if (!event.isDropCompleted())
            event.setDropCompleted(false);
        event.consume();
    };

    EventHandler<MouseEvent> objectDragDetectedHandler = event -> {
        ImageView iv = (ImageView) event.getTarget();
        String object = (String) iv.getUserData();

        String contentString = objectsMap.entrySet().stream()
                .filter(entry -> entry.getValue().contains(iv))
                .map(e -> "component_"+e.getKey())
                .findFirst()
                .orElse(flowPane.getId());

        if (
            ((state == PlayerState.WAIT_CANNONS || state == PlayerState.WAIT_ENGINES || state == PlayerState.WAIT_SHIELD || state == PlayerState.WAIT_REMOVE_GOODS) && object.equals("battery")) ||
            ((state == PlayerState.WAIT_GOODS || state == PlayerState.WAIT_REMOVE_GOODS) && object.contains("good")) ||
            (state == PlayerState.WAIT_REMOVE_CREW && (object.contains("alien") || object.equals("human"))) ||
            (state == PlayerState.WAIT_ALIEN && object.contains("alien"))
        ) {
            Dragboard db = iv.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            content.putString(contentString);
            db.setContent(content);
            db.setDragView(iv.snapshot(null, null), 10, 10);
        }

        event.consume();
    };

    EventHandler<MouseEvent> checkShipPaneMouseEnteredHandler = event -> {
        ((Pane) event.getSource()).setOpacity(0.3);
        event.consume();
    };

    EventHandler<MouseEvent> checkShipPaneMouseExitedHandler = event -> {
        ((Pane) event.getSource()).setOpacity(1);
        event.consume();
    };

    EventHandler<MouseEvent> checkShipPaneMouseClickedHandler = event -> {
        Pane pane = (Pane) event.getSource();
        ImageView imageView = pane.getChildren().stream()
                .filter(node -> node instanceof ImageView)
                .map(node -> (ImageView) node)
                .filter(iv -> iv.getId().startsWith("component"))
                .findFirst()
                .orElse(null);
        assert imageView != null;
        int id = Integer.parseInt(imageView.getId().split("_")[1]);

        if (list1.contains(id)) {
            list1.removeAll(List.of(id));
            pane.addEventHandler(MouseEvent.MOUSE_ENTERED, checkShipPaneMouseEnteredHandler);
            pane.addEventHandler(MouseEvent.MOUSE_EXITED, checkShipPaneMouseExitedHandler);
            pane.setOpacity(1);
        } else {
            list1.add(id);
            pane.removeEventHandler(MouseEvent.MOUSE_ENTERED, checkShipPaneMouseEnteredHandler);
            pane.removeEventHandler(MouseEvent.MOUSE_EXITED, checkShipPaneMouseExitedHandler);
            pane.setOpacity(0.3);
        }
        mainButton.setDisable(list1.isEmpty());
    };

    EventHandler<MouseEvent> chooseShipPartPaneMouseEnteredHandler = event -> {
        Pane pane = (Pane) event.getSource();
        ImageView imageView = pane.getChildren().stream()
                .filter(node -> node instanceof ImageView)
                .map(node -> (ImageView) node)
                .filter(iv -> iv.getId().startsWith("component"))
                .findFirst()
                .orElse(null);
        assert imageView != null;
        int componentId = Integer.parseInt(imageView.getId().split("_")[1]); // Hovered component id

        for (List<Integer> part : shipParts)
            if (part.contains(componentId)) { // Part to save
                for (Integer componentsToStyle : part)
                    paneMap.get(componentsToStyle).setOpacity(1);
            } else { // Part to drop
                for (Integer componentsToStyle : part)
                    paneMap.get(componentsToStyle).setOpacity(0.3);
            }

        event.consume();
    };

    EventHandler<MouseEvent> chooseShipPartPaneMouseExitedHandler = event -> {
        for (List<Integer> part : shipParts)
            for (Integer componentsToStyle : part)
                paneMap.get(componentsToStyle).setOpacity(1);
        event.consume();
    };

    EventHandler<MouseEvent> chooseShipPartPaneMouseClickedHandler = event -> {
        Pane pane = (Pane) event.getSource();
        ImageView imageView = pane.getChildren().stream()
                .filter(node -> node instanceof ImageView)
                .map(node -> (ImageView) node)
                .filter(iv -> iv.getId().startsWith("component"))
                .findFirst()
                .orElse(null);
        assert imageView != null;
        int id = Integer.parseInt(imageView.getId().split("_")[1]);

        Integer partId = null;
        for (List<Integer> part : shipParts)
            if (part.contains(id)) {
                partId = shipParts.indexOf(part);
                break;
            }

        assert partId != null;
        if (partId.equals(index)) { // Click on previous clicked part
            for (List<Integer> part : shipParts) // Set opacity 1 for all components and add listeners
                for (Integer componentsToStyle : part) {
                    paneMap.get(componentsToStyle).setOpacity(1);
                    paneMap.get(componentsToStyle).addEventHandler(MouseEvent.MOUSE_ENTERED, chooseShipPartPaneMouseEnteredHandler);
                    paneMap.get(componentsToStyle).addEventHandler(MouseEvent.MOUSE_EXITED, chooseShipPartPaneMouseExitedHandler);
                }
            index = null;
        } else { // Save partId
            for (List<Integer> part : shipParts)
                if (part.contains(id)) { // Part to save
                    for (Integer componentsToStyle : part)
                        paneMap.get(componentsToStyle).setOpacity(1);
                } else { // Part to drop
                    for (Integer componentsToStyle : part)
                        paneMap.get(componentsToStyle).setOpacity(0.3);
                }
            index = partId;

            for (List<Integer> part : shipParts) // Remove all listeners
                for (Integer componentsToStyle : part) {
                    paneMap.get(componentsToStyle).removeEventHandler(MouseEvent.MOUSE_ENTERED, chooseShipPartPaneMouseEnteredHandler);
                    paneMap.get(componentsToStyle).removeEventHandler(MouseEvent.MOUSE_EXITED, chooseShipPartPaneMouseExitedHandler);
                }
        }

        mainButton.setDisable(index == null);
        event.consume();
    };

    /**
     * Initializes the flight phase controller.
     * Sets up the client connection, game model, overlay manager, status display,
     * and instruction manager. Also configures the UI based on learner mode settings.
     */
    @FXML
    public void initialize() {
        MessageDispatcher.getInstance().registerHandler(this);
        this.client = App.getClientInstance();
        this.model = client.getGameController().getModel();

        initializeStatus();
        initializeInstructionManager();

        if (client.getLobby().isLearnerMode()) totalCards = 8;
        else totalCards = 12;
    }

    private void initializeInstructionManager() {
        this.instructionManager = new InstructionDisplayManager(
                client,
                instructionLabel,
                mainStatusLabel
        );
        instructionManager.reset();
    }

    private void loadImages() {
        Image playerShipImg;
        Image cardBackImg;
        Image points;

        // Setup card image
        System.out.println("CARD PULE SIZE " + model.getBoard().getCardPile().size());
        if (!model.getBoard().getCardPile().isEmpty()) {
            if (model.getBoard().getCardPile().size() > 1) {
                previousCardImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(("/images/cards/" + model.getBoard().getCardPile().get(model.getBoard().getCardPile().size() - 2).getId() + ".jpg")))));
                previousCardImage.setVisible(true);
            }
            cardBackImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cards/" + model.getBoard().getCardPile().getLast().getId() + ".jpg")));
        } else {
            previousCardImage.setVisible(false);
            if (!client.getLobby().isLearnerMode())
                cardBackImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cards/GT-cards_II_IT_0121.jpg")));
            else
                cardBackImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cards/GT-cards_I_IT_21.jpg")));
        }

        // Setup ship and points image
        if (!client.getLobby().isLearnerMode()) {
            playerShipImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cardboard/cardboard-1b.jpg")));
            points = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cardboard/StandardModePoints.png")));
        } else {
            playerShipImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cardboard/cardboard-1.jpg")));
            points = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cardboard/LearnerModePoints.png")));
        }

        playerShipImage.setImage(playerShipImg);
        currentCardImage.setImage(cardBackImg);
        pointImage.setImage(points);
    }

    private void updateCardCounter() {
        cardCounterLabel.setText("Cards: " + (client.getGameController().getModel().getBoard().getCardPile().size()) + " / " + totalCards);
    }

    /**
     * Sets the image map for game components and initializes the game board.
     * This method is called to provide the controller with the mapping between
     * component IDs and their corresponding ImageView objects.
     *
     * @param imageMap A map containing component IDs as keys and their corresponding
     *                 ImageView objects as values
     */
    public void setImageMap(Map<Integer, ImageView> imageMap) {
        this.imageMap = imageMap;
        loadImages();
        updateBoard();

        ClientPlayer player = model.getBoard().getPlayerEntityByUsername(client.getUsername());
        if (client.getLobby().isLearnerMode() || player.hasEndedInAdvance()) {
            endFlightButton.setVisible(false);
            endFlightButton.setDisable(true);
            endFlightButton.setManaged(false);
        }

        Platform.runLater(() -> {
            setupShips();
            syncAction();
        });
    }

    private void setupShips() {
        for (ClientPlayer player : client.getGameController().getModel().getBoard().getAllPlayers()) {
            ClientShip ship = player.getShip();
            if (player.getUsername().equals(client.getUsername())) {
                shipGrid.setLayoutX(playerShipImage.getLayoutX());
                shipGrid.setLayoutY(playerShipImage.getLayoutY());
                shipGrid.setPrefWidth(playerShipImage.getFitWidth());
                shipGrid.setPrefHeight(playerShipImage.getFitHeight());
                shipGrid.setPadding(new Insets(14.0, 10.0, 20.0, 17.0));
                addTilesToPlayerShip(shipGrid, ship, TILE_WIDTH, TILE_HEIGHT);
            } else {
                Label label = new Label(player.getUsername() + "'s ship");
                label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 20px 5px 20px 5px;");
                otherPlayersContainer.getChildren().add(label);

                Pane shipContainer = createOtherPlayerShipContainer(ship);
                otherPlayersContainer.getChildren().add(shipContainer);
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private Pane createOtherPlayerShipContainer(ClientShip ship) {
        ImageView shipImageView = new ImageView();
        shipImageView.setFitWidth(280.0);
        shipImageView.setLayoutX(2.0);
        shipImageView.setLayoutY(2.0);
        shipImageView.setPickOnBounds(true);
        shipImageView.setPreserveRatio(true);
        if (!client.getLobby().isLearnerMode()) {
            shipImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cardboard/cardboard-1b.jpg"))));
        } else {
            shipImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cardboard/cardboard-1.jpg"))));
        }
        shipImageView.setStyle("-fx-background-color: #0d0520; -fx-border-color: #8a2be2; -fx-border-width: 2; -fx-background-radius: 10;");

        Pane shipPane = new Pane();
        shipPane.setPrefHeight(shipImageView.getBoundsInLocal().getHeight());
        shipPane.setPrefWidth(shipImageView.getBoundsInLocal().getWidth());

        // Grid pane
        GridPane tilesGrid = new GridPane();
        tilesGrid.setLayoutX(shipImageView.getLayoutX());
        tilesGrid.setLayoutY(shipImageView.getLayoutY());
        tilesGrid.setPrefWidth(shipImageView.getFitWidth());
        tilesGrid.setPrefHeight(shipImageView.getFitHeight());
        tilesGrid.setPadding(new Insets(9.8, 10.0, 9.8, 10.0));

        for (int i = 0; i < Constants.SHIP_ROWS; i++) {
            RowConstraints rowConstraint = new RowConstraints();
            rowConstraint.setMinHeight(OTHER_PLAYERS_TILE_HEIGHT);
            rowConstraint.setPrefHeight(OTHER_PLAYERS_TILE_HEIGHT);
            rowConstraint.setMaxHeight(OTHER_PLAYERS_TILE_HEIGHT);
            tilesGrid.getRowConstraints().add(rowConstraint);
        }

        for (int i = 0; i < Constants.SHIP_COLUMNS; i++) {
            ColumnConstraints colConstraint = new ColumnConstraints();
            colConstraint.setMinWidth(OTHER_PLAYERS_TILE_WIDTH);
            colConstraint.setPrefWidth(OTHER_PLAYERS_TILE_WIDTH);
            colConstraint.setMaxWidth(OTHER_PLAYERS_TILE_WIDTH);
            tilesGrid.getColumnConstraints().add(colConstraint);
        }

        // Add all
        addTilesToPlayerShip(tilesGrid, ship, OTHER_PLAYERS_TILE_WIDTH, OTHER_PLAYERS_TILE_HEIGHT);
        shipPane.getChildren().addAll(shipImageView, tilesGrid);

        return shipPane;
    }

    private void addTilesToPlayerShip(GridPane grid, ClientShip ship, double width, double height) {
        for (int row = 0; row < Constants.SHIP_ROWS; row++) {
            for (int col = 0; col < Constants.SHIP_COLUMNS; col++) {
                Optional<ClientComponent> component = ship.getDashboard(row, col);
                if (component.isPresent()) {
                    Pane imagePane = createShipComponent(component.get(), width, height);
                    paneMap.put(component.get().getId(), imagePane);
                    grid.add(imagePane, col, row);

                    switch (component.get()) {
                        case ClientCabinComponent c -> {
                            if (c.getAlien() != null)
                                changeComponentObjects(c, 1, List.of("/images/objects/alien-" + c.getAlien() + ".png"));
                            else
                                changeComponentObjects(c, c.getHumans(), List.of("/images/objects/human.png"));
                        }
                        case ClientBatteryComponent c -> changeComponentObjects(c, c.getBatteries(), List.of("/images/objects/battery.png"));
                        case ClientCargoHoldsComponent c -> changeComponentObjects(c, c.getGoods().size(), c.getGoods().stream().map(good -> "/images/objects/good-"+good.name().toLowerCase()+".png").toList());
                        case ClientSpecialCargoHoldsComponent c -> changeComponentObjects(c, c.getGoods().size(), c.getGoods().stream().map(good -> "/images/objects/good-"+good.name().toLowerCase()+".png").toList());
                        case ClientCannonComponent _, ClientEngineComponent _, ClientShieldComponent _ -> objectsMap.put(component.get().getId(), new ArrayList<>());
                        default -> {}
                    }

                }
                else {
                    Rectangle emptySlot = new Rectangle(width, height);
                    emptySlot.setFill(Color.TRANSPARENT);
                    emptySlot.setStroke(Color.TRANSPARENT);
                    emptySlot.setMouseTransparent(true);
                    grid.add(emptySlot, col, row);
                }
            }
        }
    }

    private Pane createShipComponent(ClientComponent component, double width, double height) {
        Pane imagePane = new Pane();
        ImageView componentImage = imageMap.get(component.getId());
        componentImage.addEventHandler(DragEvent.DRAG_OVER, acceptDragOverHandler);
        componentImage.addEventHandler(DragEvent.DRAG_DROPPED, componentDragDroppedHandler);

        imagePane.setPrefWidth(width);
        imagePane.setPrefHeight(height);
        componentImage.setFitWidth(width);
        componentImage.setFitHeight(height);
        componentImage.setLayoutX(imagePane.getLayoutX());
        componentImage.setLayoutY(imagePane.getLayoutY());
        imagePane.getChildren().add(componentImage);

        return imagePane;
    }

    @SuppressWarnings("Duplicates")
    private void changeComponentObjects(ClientComponent component, int n, List<String> imagesPath) {
        Pane componentPane = paneMap.get(component.getId());
        List<ImageView> objects = objectsMap.get(component.getId());
        if (objects == null) {
            objectsMap.put(component.getId(), new ArrayList<>());
            objects = objectsMap.get(component.getId());
        }

        int diff = n - objects.size();
        if (diff == 0) return;

        // Get dimensions and position of base image
        double baseWidth = componentPane.boundsInLocalProperty().get().getWidth();
        double baseHeight = componentPane.boundsInLocalProperty().get().getHeight();

        double overlaySize = Math.min(baseWidth / 3.2, baseHeight / 3.2);

        if (diff > 0) { // Add images
            for (int i = 0; i < diff; i++) {
                ImageView overlayImage = new ImageView();
                String path = i < imagesPath.size() ? imagesPath.get(i) : imagesPath.getLast();
                overlayImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));

                if (path.contains("battery"))
                    overlayImage.setUserData("battery");
                else if (path.contains("good"))
                    overlayImage.setUserData("good-" + path.split("-")[1].split("\\.")[0]);
                else if (path.contains("human"))
                    overlayImage.setUserData("human");
                else if (path.contains("alien"))
                    overlayImage.setUserData("alien-" + path.split("-")[1].split("\\.")[0]);

                overlayImage.setFitWidth(overlaySize);
                overlayImage.setFitHeight(overlaySize);
                overlayImage.setPreserveRatio(true);

                double spacing3 = (baseWidth - (3 * overlaySize)) / 4;
                double overlayX = spacing3 + (objects.size() * (overlaySize + spacing3));
                double overlayY = ((objects.size() % 2 == 0 ? 1 : -1) * baseHeight / 5) + (baseHeight - overlaySize) / 2;

                overlayImage.setLayoutX(overlayX);
                overlayImage.setLayoutY(overlayY);

                overlayImage.addEventHandler(MouseEvent.DRAG_DETECTED, objectDragDetectedHandler);

                componentPane.getChildren().add(overlayImage);
                objects.add(overlayImage);
            }
        } else { // Remove images
            for (int i = 0; i < Math.abs(diff); i++) {
                ImageView iw = objects.getLast();

                iw.removeEventHandler(MouseEvent.DRAG_DETECTED, objectDragDetectedHandler);

                iw.setImage(null);
                ((Pane) componentPane.getParent()).getChildren().remove(iw);
                objects.remove(iw);

            }
        }
    }

    private void moveComponentObject(ImageView object, String destId) {
        // Remove from source
        String sourceId = objectsMap.entrySet().stream()
            .filter(e -> e.getValue().contains(object))
            .map(e -> "component_"+e.getKey())
            .findFirst()
            .orElse(flowPane.getId());

        if (sourceId.equals(flowPane.getId())) { // It was in flow pane
            flowPane.getChildren().remove(object);
        } else { // It was in a component
            paneMap.get(Integer.parseInt(sourceId.split("_")[1])).getChildren().remove(object);
            objectsMap.get(Integer.parseInt(sourceId.split("_")[1])).remove(object);
            reorderComponentObjects(Integer.parseInt(sourceId.split("_")[1]));
        }

        if (destId.equals(flowPane.getId())) { // Dragged over flow pane
            flowPane.getChildren().add(object);
        } else { // Dragged over a component
            // Get dimensions and position of base image
            int componentDestId = Integer.parseInt(destId.split("_")[1]);
            Pane destPane = paneMap.get(componentDestId);
            double baseWidth = destPane.getBoundsInLocal().getWidth();
            double baseHeight = destPane.getBoundsInLocal().getHeight();

            double overlaySize = Math.min(baseWidth / 3.2, baseHeight / 3.2);
            double spacing3 = (baseWidth - (3 * overlaySize)) / 4;
            double overlayX = spacing3 + (objectsMap.get(componentDestId).size() * (overlaySize + spacing3));
            double overlayY = ((objectsMap.get(componentDestId).size() % 2 == 0 ? 1 : -1) * baseHeight / 5) + (baseHeight - overlaySize) / 2;

            object.setLayoutX(overlayX);
            object.setLayoutY(overlayY);

            if (!destPane.getChildren().contains(object))
                destPane.getChildren().add(object);

            objectsMap.get(componentDestId).add(object);
        }
    }

    private void clearObjectsFromCannonsShieldsEngines() {
        objectsMap.forEach((id, _) -> {
            ClientComponent component = model.getBoard().getMapIdComponents().get(id);
            switch (component) {
                case ClientCannonComponent _, ClientEngineComponent _, ClientShieldComponent _ -> changeComponentObjects(component, 0, List.of());
                default -> {}
            }
        });
    }

    @SuppressWarnings("Duplicates")
    private void createObjectsInFlowPane(int n, List<String> imagesPath) {
        double overlaySize = Math.min(TILE_WIDTH / 3.2, TILE_HEIGHT / 3.2);
        for (int i = 0; i < n; i++) {
            ImageView overlayImage = new ImageView();
            String path = i < imagesPath.size() ? imagesPath.get(i) : imagesPath.getLast();
            overlayImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));

            if (path.contains("battery"))
                overlayImage.setUserData("battery");
            else if (path.contains("good"))
                overlayImage.setUserData("good-" + path.split("-")[1].split("\\.")[0]);
            else if (path.contains("human"))
                overlayImage.setUserData("human");
            else if (path.contains("alien"))
                overlayImage.setUserData("alien-" + path.split("-")[1].split("\\.")[0]);

            overlayImage.setFitWidth(overlaySize);
            overlayImage.setFitHeight(overlaySize);
            overlayImage.setPreserveRatio(true);

            overlayImage.addEventHandler(MouseEvent.DRAG_DETECTED, objectDragDetectedHandler);
            flowPane.getChildren().add(overlayImage);
        }
    }

    private void reorderComponentObjects(int componentId) {
        Pane pane = paneMap.get(componentId);
        List<ImageView> objects = objectsMap.get(componentId);
        for (int i = 0; i < objects.size(); i++) {
            double baseWidth = pane.getBoundsInLocal().getWidth();
            double baseHeight = pane.getBoundsInLocal().getHeight();

            double overlaySize = Math.min(baseWidth / 3.2, baseHeight / 3.2);
            double spacing3 = (baseWidth - (3 * overlaySize)) / 4;
            double overlayX = spacing3 + (i * (overlaySize + spacing3));
            double overlayY = ((i % 2 == 0 ? 1 : -1) * baseHeight / 5) + (baseHeight - overlaySize) / 2;

            objects.get(i).setLayoutX(overlayX);
            objects.get(i).setLayoutY(overlayY);
        }
    }

    private void updateBoard() {
        StringBuilder sb = new StringBuilder();
        for (SimpleEntry<ClientPlayer, Integer> player : model.getBoard().getPlayers()) {
            System.out.println("In game  " + player.getKey().toRawString());
            sb.append(player.getValue().toString()).append(" ").append(player.getKey().toRawString()).append(" | ").append(player.getKey().getCredits()).append(" credits\n");
        }
        if (sb.isEmpty())
            sb.append("none");
        playersInGameLabel.setText(sb.toString());
        playersInGameLabel.setStyle("-fx-fill: white !important;");


        sb.setLength(0);
        for (ClientPlayer player : client.getGameController().getModel().getBoard().getStartingDeck()) {
            sb.append(player.toRawString()).append(" | ").append(player.getCredits()).append(" credits\n");
        }
        if (sb.isEmpty())
            sb.append("none");
        playersStartingDeckLabel.setText(sb.toString());
        playersStartingDeckLabel.setStyle("-fx-fill: white !important;");
    }

    private void paneRemoveAllListeners(Pane p) {
        p.removeEventHandler(MouseEvent.MOUSE_ENTERED, chooseShipPartPaneMouseEnteredHandler);
        p.removeEventHandler(MouseEvent.MOUSE_EXITED, chooseShipPartPaneMouseExitedHandler);
        p.removeEventHandler(MouseEvent.MOUSE_CLICKED, chooseShipPartPaneMouseClickedHandler);
        p.removeEventHandler(MouseEvent.MOUSE_ENTERED, checkShipPaneMouseEnteredHandler);
        p.removeEventHandler(MouseEvent.MOUSE_EXITED, checkShipPaneMouseExitedHandler);
        p.removeEventHandler(MouseEvent.MOUSE_CLICKED, checkShipPaneMouseClickedHandler);
    }

    @FXML
    private void leaveGameHandler() { client.send(MessageType.LEAVE_GAME); }

    @FXML
    private void endFlightHandler() { client.send(MessageType.END_FLIGHT); }

    private void syncAction() {
        this.state = client.getGameController().getModel().getPlayerState(client.getUsername());

        clearObjectsFromCannonsShieldsEngines();

        mainButton.setDisable(false);
        mainButton.setVisible(true);
        mainButton.setOnAction(null);

        for (Pane pane : paneMap.values())
            paneRemoveAllListeners(pane);

        // Disable flow pane
        flowPane.getChildren().clear();
        flowPane.setVisible(false);
        flowPane.removeEventHandler(DragEvent.DRAG_OVER, acceptDragOverHandler);
        flowPane.removeEventHandler(DragEvent.DRAG_DROPPED, flowPaneDragDroppedHandler);

        // Disable WAIT_INDEX and WAIT_BOOLEAN buttons and reset index and decision
        indexBooleanButtonsContainer.setVisible(false);
        indexBooleanButtonsContainer.getChildren().clear();
        index = null;
        decision = false;

        switch (state) {
            case CHECK -> {
                mainButton.setText("Done");
                mainButton.setOnAction(_ -> client.send(MessageType.CHECK_SHIP, list1));

                paneMap.forEach((id, pane) -> {
                    if (shipGrid.lookup("#component_" + id) != null) {
                        pane.addEventHandler(MouseEvent.MOUSE_ENTERED, checkShipPaneMouseEnteredHandler);
                        pane.addEventHandler(MouseEvent.MOUSE_EXITED, checkShipPaneMouseExitedHandler);
                        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, checkShipPaneMouseClickedHandler);
                    }
                });
            }
            case WAIT_ALIEN -> {
                // Enable flow pane
                flowPane.setVisible(true);
                flowPane.addEventHandler(DragEvent.DRAG_OVER, acceptDragOverHandler);
                flowPane.addEventHandler(DragEvent.DRAG_DROPPED, flowPaneDragDroppedHandler);
                createObjectsInFlowPane(2, List.of("/images/objects/alien-engine.png", "/images/objects/alien-cannon.png"));

                mainButton.setText("Done");
                mainButton.setOnAction(_ -> {
                    Map<Integer, AlienType> alienMap = new HashMap<>();
                    objectsMap.forEach((id, objects) -> {
                        if (shipGrid.lookup("#component_" + id) != null) { // Count only objects in my components
                            if (objects.size() == 1) {
                                AlienType alien = ((String) objects.getFirst().getUserData()).contains("cannon") ? AlienType.CANNON : AlienType.ENGINE;
                                alienMap.put(id, alien);
                            }
                        }
                    });
                    client.send(MessageType.CHOOSE_ALIEN, alienMap);
                });
            }
            case DRAW_CARD -> {
                mainButton.setText("Draw card");
                mainButton.setOnAction(_ -> client.send(MessageType.DRAW_CARD));
            }
            case WAIT_CANNONS -> {
                mainButton.setText("Done");
                mainButton.setOnAction(_ -> {
                    list1.clear();
                    list2.clear();
                    objectsMap.forEach((id, objects) -> {
                        if (shipGrid.lookup("#component_" + id) != null) { // Count only objects in my components
                            ClientComponent component = model.getBoard().getMapIdComponents().get(id);
                            switch (component) {
                                case ClientCannonComponent _ -> {
                                    if (!objects.isEmpty())
                                        list1.add(id);
                                }
                                case ClientBatteryComponent c -> {
                                    for (int i = objects.size(); i < c.getBatteries(); i++)
                                        list2.add(id);
                                }
                                default -> {}
                            }
                        }
                    });
                    client.send(MessageType.ACTIVATE_CANNONS, list2, list1);
                });
            }
            case WAIT_ENGINES -> {
                mainButton.setText("Done");
                mainButton.setOnAction(_ -> {
                    list1.clear();
                    list2.clear();
                    objectsMap.forEach((id, objects) -> {
                        if (shipGrid.lookup("#component_" + id) != null) { // Count only objects in my components
                            ClientComponent component = model.getBoard().getMapIdComponents().get(id);
                            switch (component) {
                                case ClientEngineComponent _ -> {
                                    if (!objects.isEmpty())
                                        list1.add(id);
                                }
                                case ClientBatteryComponent c -> {
                                    for (int i = objects.size(); i < c.getBatteries(); i++)
                                        list2.add(id);
                                }
                                default -> {}
                            }
                        }
                    });
                    client.send(MessageType.ACTIVATE_ENGINES, list2, list1);
                });
            }
            case WAIT_ROLL_DICES -> {
                mainButton.setText("Roll dice");
                mainButton.setOnAction(_ -> client.send(MessageType.ROLL_DICES));
            }
            case WAIT_SHIELD -> {
                mainButton.setText("Done");
                mainButton.setOnAction(_ -> {
                    list2.clear();
                    objectsMap.forEach((id, objects) -> {
                        if (shipGrid.lookup("#component_" + id) != null) { // Count only objects in my components
                            ClientComponent component = model.getBoard().getMapIdComponents().get(id);
                            if (component instanceof ClientBatteryComponent c) {
                                for (int i = objects.size(); i < c.getBatteries(); i++)
                                    list2.add(id);
                            }
                        }
                    });
                    if (list2.size() > 1)
                        showError("Choose only one shield");
                    else
                        client.send(MessageType.ACTIVATE_SHIELD, list2.isEmpty() ? null : list2.getFirst());
                });
            }
            case WAIT_SHIP_PART -> {
                mainButton.setText("Done");
                mainButton.setOnAction(_ -> client.send(MessageType.CHOOSE_SHIP_PART, index));

                paneMap.forEach((id, pane) -> {
                    if (shipGrid.lookup("#component_" + id) != null) { // Only component that are in main ship
                        pane.addEventHandler(MouseEvent.MOUSE_ENTERED, chooseShipPartPaneMouseEnteredHandler);
                        pane.addEventHandler(MouseEvent.MOUSE_EXITED, chooseShipPartPaneMouseExitedHandler);
                        pane.addEventHandler(MouseEvent.MOUSE_CLICKED, chooseShipPartPaneMouseClickedHandler);
                    }
                });
            }
            case WAIT_REMOVE_CREW -> {
                // Enable flow pane
                flowPane.setVisible(true);
                flowPane.addEventHandler(DragEvent.DRAG_OVER, acceptDragOverHandler);
                flowPane.addEventHandler(DragEvent.DRAG_DROPPED, flowPaneDragDroppedHandler);

                mainButton.setText("Done");
                mainButton.setOnAction(_ -> {
                    list1.clear();
                    objectsMap.forEach((id, objects) -> {
                        if (shipGrid.lookup("#component_" + id) != null) { // Count only objects in my components
                            ClientComponent component = model.getBoard().getMapIdComponents().get(id);
                            if (component instanceof ClientCabinComponent c) {
                                int crewInCabin = c.getAlien() != null ? 1 : c.getHumans();
                                for (int i = objects.size(); i < crewInCabin; i++)
                                    list1.add(id);
                            }
                        }
                    });
                    client.send(MessageType.REMOVE_CREW, list1);
                });
            }
            case WAIT_REMOVE_GOODS, WAIT_GOODS -> {
                // Enable flow pane
                flowPane.setVisible(true);
                flowPane.addEventHandler(DragEvent.DRAG_OVER, acceptDragOverHandler);
                flowPane.addEventHandler(DragEvent.DRAG_DROPPED, flowPaneDragDroppedHandler);

                if (state == PlayerState.WAIT_GOODS) {
                    List<ColorType> reward = model.getBoard().getCardPile().getLast().getReward(client.getUsername());
                    createObjectsInFlowPane(reward.size(), reward.stream().map(c -> "/images/objects/good-" + c.name().toLowerCase() + ".png").toList());
                }

                mainButton.setText("Done");
                mainButton.setOnAction(_ -> {
                    list2.clear();
                    Map<Integer, List<ColorType>> newDisposition = new HashMap<>();
                    objectsMap.forEach((id, objects) -> {
                        if (shipGrid.lookup("#component_" + id) != null) { // Count only objects in my components
                            ClientComponent component = model.getBoard().getMapIdComponents().get(id);
                            switch (component) {
                                case ClientCargoHoldsComponent _, ClientSpecialCargoHoldsComponent _ -> {
                                    List<ColorType> goods = objects.stream()
                                        .map(iv -> ColorType.valueOf(((String) iv.getUserData()).split("-")[1].toUpperCase()))
                                        .toList();
                                    newDisposition.put(id, goods);
                                }
                                case ClientBatteryComponent c -> {
                                    for (int i = objects.size(); i < c.getBatteries(); i++)
                                        list2.add(id);
                                }
                                default -> {}
                            }
                        }
                    });
                    client.send(MessageType.UPDATE_GOODS, newDisposition, list2);
                });
            }
            case WAIT_INDEX -> {
                mainButton.setText("Done");

                // Show WAIT_INDEX buttons
                List<Button> planetsButtons = new ArrayList<>();
                if (model.getBoard().getCardPile().getLast() instanceof ClientPlanetCard card) {
                    for (int i = 0; i < card.getPlanets().size(); i++) {
                        Button planetButton = new Button(String.valueOf(i));
                        planetButton.getStyleClass().clear();
                        planetButton.getStyleClass().add("toggle-button");
                        planetButton.setStyle("-fx-pref-width: 20; -fx-padding: 5;");

                        Integer finalI = i;
                        planetButton.setOnAction(_ -> {
                            if (finalI.equals(index)) {
                                index = null;
                                planetButton.getStyleClass().clear();
                                planetButton.getStyleClass().add("toggle-button");
                            } else {
                                index = finalI;
                                planetButton.getStyleClass().clear();
                                planetButton.getStyleClass().add("toggle-button-selected");
                                planetsButtons.stream().filter(b -> !b.equals(planetButton)).forEach(b -> {
                                    b.getStyleClass().clear();
                                    b.getStyleClass().add("toggle-button");
                                });
                            }
                        });
                        planetsButtons.add(planetButton);
                    }
                    indexBooleanButtonsContainer.getChildren().addAll(planetsButtons);
                    indexBooleanButtonsContainer.setVisible(true);
                    indexBooleanButtonsContainer.setSpacing(5);
                }

                mainButton.setOnAction(_ -> client.send(MessageType.GET_INDEX, index));
            }
            case WAIT_BOOLEAN -> {
                mainButton.setText("Done");

                // Show WAIT_BOOLEAN buttons
                Button acceptBtn = new Button("Take reward");
                Button declineBtn = new Button("Decline"); // Default
                acceptBtn.getStyleClass().clear();
                acceptBtn.getStyleClass().add("toggle-button");
                declineBtn.getStyleClass().clear();
                declineBtn.getStyleClass().add("toggle-button-selected");
                Region spacer = new Region();
                spacer.setPrefWidth(10);

                acceptBtn.setOnAction(_ -> {
                    if (!decision) {
                        decision = true;
                        acceptBtn.getStyleClass().clear();
                        acceptBtn.getStyleClass().add("toggle-button-selected");
                        declineBtn.getStyleClass().clear();
                        declineBtn.getStyleClass().add("toggle-button");
                    }
                });
                declineBtn.setOnAction(_ -> {
                    if (decision) {
                        decision = false;
                        declineBtn.getStyleClass().clear();
                        declineBtn.getStyleClass().add("toggle-button-selected");
                        acceptBtn.getStyleClass().clear();
                        acceptBtn.getStyleClass().add("toggle-button");
                    }
                });
                indexBooleanButtonsContainer.getChildren().addAll(acceptBtn, spacer, declineBtn);
                indexBooleanButtonsContainer.setVisible(true);
                indexBooleanButtonsContainer.setSpacing(10);

                mainButton.setOnAction(_ -> client.send(MessageType.GET_BOOLEAN, decision));
            }
            case WAIT, DONE -> mainButton.setVisible(false);
            case END -> SceneManager.navigateToScene("/fxml/end.fxml", this, null);
        }

        if (instructionManager != null) {
            instructionManager.updateInstructions();
        }
    }

    private void showCardInfo(ClientCard card) {
        String cardInfo = card.printCardInfo(client.getGameController().getModel(), client.getGameController().getModel().getBoard());

        logLabel.setText(cardInfo);

        statusScrollPane.setVvalue(0.0);
    }

    private void initializeStatus() {
        logLabel.setText("Waiting for game events...");
        statusScrollPane.setVvalue(0.0);
    }

    private void showError(String messame) {
        errorLabel.setText(messame);
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(_ -> errorLabel.setText(""));
        pause.play();
    }

    /**
     * Determines whether this handler can process the given message type.
     *
     * @param messageType The type of message to check
     * @return true if this handler can process the message type, false otherwise
     */
    @Override
    public boolean canHandle(MessageType messageType) {
        return List.of(
                MessageType.ERROR,
                MessageType.PLAYERS_STATE_UPDATED_EVENT,
                MessageType.COMPONENT_DESTROYED_EVENT,
                MessageType.BATTERIES_UPDATED_EVENT,
                MessageType.CREW_UPDATED_EVENT,
                MessageType.GOODS_UPDATED_EVENT,
                MessageType.FLIGHT_ENDED_EVENT,
                MessageType.SHIP_BROKEN_EVENT,
                MessageType.CREDITS_UPDATED_EVENT,
                MessageType.PLAYERS_POSITION_UPDATED_EVENT,
                MessageType.JOINED_LOBBY_EVENT,
                MessageType.LEFT_LOBBY_EVENT,
                MessageType.CARD_UPDATED_EVENT,
                MessageType.CARD_REVEALED_EVENT
        ).contains(messageType);
    }

    /**
     * Handles incoming game events and updates the UI accordingly.
     * This method processes various types of game events such as player state updates,
     * component destruction, battery updates, crew updates, goods updates, and more.
     *
     * @param event The game event to handle
     */
    @Override
    public void handleMessage(Event event) {
        switch (event) {
            case PlayersStateUpdatedEvent e -> {

                if (state != e.states().get(client.getUsername()))
                    syncAction();

                if (instructionManager != null) {
                    instructionManager.onPlayerStateChanged();
                }

                // Check if card is covered
                if (e.states().containsValue(PlayerState.DRAW_CARD)) {
                    Image cardBackImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cards/GT-cards_II_IT_0121.jpg")));
                    currentCardImage.setImage(cardBackImg);
                    if (!model.getBoard().getCardPile().isEmpty()) {
                        previousCardImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(("/images/cards/" + model.getBoard().getCardPile().getLast().getId() + ".jpg")))));
                        previousCardImage.setVisible(true);
                    }
                }

            }
            case ComponentDestroyedEvent e -> {
                ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(e.id());
                Pane p = paneMap.get(e.id());
                if (p != null) {
                    double width = p.getBoundsInLocal().getWidth();
                    double height = p.getBoundsInLocal().getHeight();

                    Rectangle emptySlot = new Rectangle(width, height);
                    emptySlot.setFill(Color.TRANSPARENT);
                    emptySlot.setStroke(Color.TRANSPARENT);
                    emptySlot.setMouseTransparent(true);

                    changeComponentObjects(component, 0, List.of());
                    imageMap.get(e.id()).removeEventHandler(DragEvent.DRAG_OVER, acceptDragOverHandler);
                    imageMap.get(e.id()).removeEventHandler(DragEvent.DRAG_DROPPED, componentDragDroppedHandler);

                    paneRemoveAllListeners(p);

                    GridPane grid = (GridPane) p.getParent();
                    grid.getChildren().remove(p);
                    grid.add(emptySlot, component.getX(), component.getY());

                    paneMap.remove(e.id());
                    objectsMap.remove(e.id());

                    if (state == PlayerState.CHECK) { // User has to continue to check ship
                        list1.clear();
                        mainButton.setDisable(true);
                    }
                }
                if (instructionManager != null) {
                    instructionManager.showSuccessMessage("Component removed successfully");
                }
            }
            case ShipBrokenEvent e -> this.shipParts = new ArrayList<>(e.parts());
            case BatteriesUpdatedEvent e -> {
                ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(e.id());
                changeComponentObjects(component, e.batteries(), List.of("/images/objects/battery.png"));
                if (instructionManager != null) {
                    instructionManager.showSuccessMessage("Batteries updated");
                }
            }
            case CrewUpdatedEvent e -> {
                ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(e.id());
                String path = "/images/objects/" + (e.alien() != null ? ("alien-" + e.alien().toString().toLowerCase()) : "human") + ".png";
                changeComponentObjects(component, e.alien() != null ? 1 : e.humans(), List.of(path));
                if (instructionManager != null) {
                    instructionManager.showSuccessMessage("Crew updated successfully");
                }
            }
            case GoodsUpdatedEvent e -> {
                ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(e.id());
                List<String> paths = new ArrayList<>();
                for (ColorType good : e.goods())
                    paths.add("/images/objects/good-" + good.name().toLowerCase() + ".png");
                changeComponentObjects(component, e.goods().size(), paths);
                if (instructionManager != null) {
                    instructionManager.showSuccessMessage("Goods updated successfully");
                }
            }
            case CreditsUpdatedEvent _, PlayersPositionUpdatedEvent _, JoinedLobbyEvent _ -> updateBoard();
            case LeftLobbyEvent e -> {
                if (e.username().equals(client.getUsername())) // Your action
                    SceneManager.navigateToScene("/fxml/menu.fxml", this, null);
                else // Not your action
                    updateBoard();
            }
            case CardRevealedEvent e -> {
                Image cardBackImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cards/" + e.card().getId() + ".jpg")));
                currentCardImage.setImage(cardBackImg);
                showCardInfo(e.card());
                if (instructionManager != null) {
                    instructionManager.showSuccessMessage("New card revealed: " + e.card().getId());
                }
                updateCardCounter();
            }
            case CardUpdatedEvent e -> showCardInfo(e.card());
            case FlightEndedEvent e -> {
                if (e.username().equals(client.getUsername()))
                    endFlightButton.setVisible(false);
                updateBoard();
            }
            case ErrorEvent e -> showError(e.message());
            default -> {}
        }
    }

}
