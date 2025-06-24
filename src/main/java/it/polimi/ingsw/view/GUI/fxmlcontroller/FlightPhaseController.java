package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.client.model.components.*;
import it.polimi.ingsw.client.model.events.CardRevealedEvent;
import it.polimi.ingsw.client.model.events.CardUpdatedEvent;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.player.ClientShip;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
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
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

public class FlightPhaseController implements MessageHandler {

    @FXML public Label playersInGameLabel;
    @FXML public Label playersStartingDeckLabel;

    private OverlayManager overlayManager;
    @FXML public AnchorPane root;

    @FXML private VBox otherPlayersContainer;
    @FXML private ImageView currentCardImage;

    @FXML private ImageView playerShipImage;
    @FXML private GridPane shipGrid;
    @FXML public FlowPane flowPane;

    @FXML private Button mainButton;
    @FXML private Button endFlightButton;

    // Data structures
    private ObservableList<String> logMessages;
    private List<OtherPlayerShipContainer> otherPlayersShips = new ArrayList<>();

    private Client client;
    private ClientGameModel model;
    private PlayerState state;

    private final Map<Integer, Pane> paneMap = new HashMap<>();
    private Map<Integer, ImageView> imageMap = new HashMap<>();
    private final Map<Integer, List<ImageView>> objectsMap = new HashMap<>();
    private final List<Integer> list1 = new ArrayList<>();
    private final List<Integer> list2 = new ArrayList<>();
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

        System.out.println("Source: " + sourceId);
        if (state == PlayerState.WAIT_CANNONS && object.equals("battery") && destId.contains("component") && sourceId.contains("component")) { // Battery in WAIT_CANNONS
            List<Class<?>> allowedClasses = Arrays.asList(ClientCannonComponent.class, ClientBatteryComponent.class);
            if (allowedClasses.containsAll(List.of(destComponent.getClass(), sourceComponent.getClass()))) {
                System.out.println("Tra le classi permesse, source: " + sourceComponent.getClass() + " dest: " + destComponent.getClass());
                if (
                    (destComponent instanceof ClientCannonComponent && objectsMap.get(destComponentId).isEmpty() && ((ClientCannonComponent) destComponent).isDouble()) ||
                    (destComponent instanceof ClientBatteryComponent && objectsMap.get(destComponentId).size() < ((ClientBatteryComponent) destComponent).getBatteries() )
                ) {
                    System.out.println("Mi muovo");
                    moveComponentObject(objectImageView, destId);
                    event.setDropCompleted(true);
                }
            }
        }
        else if (state == PlayerState.WAIT_ENGINES && object.equals("battery") && destId.contains("component") && sourceId.contains("component")) { // Battery in WAIT_ENGINES
            System.out.println("Tra le classi permesse, source: " + sourceComponent.getClass() + " dest: " + destComponent.getClass());
            List<Class<?>> allowedClasses = Arrays.asList(ClientEngineComponent.class, ClientBatteryComponent.class);
            if (allowedClasses.containsAll(List.of(destComponent.getClass(), sourceComponent.getClass()))) {
                if (
                    (destComponent instanceof ClientEngineComponent && objectsMap.get(destComponentId).isEmpty()) && ((ClientEngineComponent) destComponent).isDouble() ||
                    (destComponent instanceof ClientBatteryComponent && objectsMap.get(destComponentId).size() < ((ClientBatteryComponent) destComponent).getBatteries() )
                ) {
                    System.out.println("Mi muovo");
                    moveComponentObject(objectImageView, destId);
                    event.setDropCompleted(true);
                }
            }
        }
        else if (state == PlayerState.WAIT_SHIELD && object.equals("battery") && destId.contains("component") && sourceId.contains("component")) { // Battery in WAIT_SHIELD
            System.out.println("Tra le classi permesse, source: " + sourceComponent.getClass() + " dest: " + destComponent.getClass());
            List<Class<?>> allowedClasses = Arrays.asList(ClientShieldComponent.class, ClientBatteryComponent.class);
            if (allowedClasses.containsAll(List.of(destComponent.getClass(), sourceComponent.getClass()))) {
                if (
                    (destComponent instanceof ClientShieldComponent && objectsMap.get(destComponentId).isEmpty()) ||
                    (destComponent instanceof ClientBatteryComponent && objectsMap.get(destComponentId).size() < ((ClientBatteryComponent) destComponent).getBatteries() )
                ) {
                    System.out.println("Mi muovo");
                    moveComponentObject(objectImageView, destId);
                    event.setDropCompleted(true);
                }
            }
        }
        else if (state == PlayerState.WAIT_REMOVE_GOODS && object.equals("battery")) { // Battery in WAIT_REMOVE_GOODS
            List<Class<?>> allowedClasses = List.of(ClientBatteryComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                if (
                    destId.contains("component") && allowedClasses.contains(destComponent.getClass()) && // Dest is flow pane or allowed component
                    destComponent instanceof ClientBatteryComponent && objectsMap.get(destComponentId).size() < ((ClientBatteryComponent) destComponent).getBatteries()
                ) {
                    System.out.println("Mi muovo");
                    moveComponentObject(objectImageView, destId);
                    event.setDropCompleted(true);
                }
            }
        }
        else if ((state == PlayerState.WAIT_REMOVE_GOODS || state == PlayerState.WAIT_GOODS) && object.contains("good")) { // Good in WAIT_REMOVE_GOODS or WAIT_GOODS
            List<Class<?>> allowedClasses = List.of(ClientCargoHoldsComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                if (
                    destId.contains("component") && allowedClasses.contains(destComponent.getClass()) && // Dest is flow pane or allowed component
                    destComponent instanceof ClientCargoHoldsComponent && objectsMap.get(destComponentId).size() < ((ClientCargoHoldsComponent) destComponent).getNumber()
                ) {
                    System.out.println("Mi muovo");
                    moveComponentObject(objectImageView, destId);
                    event.setDropCompleted(true);
                }
            }
        }
        else if (state == PlayerState.WAIT_REMOVE_CREW && object.equals("human")) { // Human in WAIT_REMOVE_CREW
            List<Class<?>> allowedClasses = List.of(ClientCabinComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                if (
                    destId.contains("component") && allowedClasses.contains(destComponent.getClass()) && // Dest is flow pane or allowed component
                    destComponent instanceof ClientCabinComponent && objectsMap.get(destComponentId).size() < ((ClientCabinComponent) destComponent).getHumans()
                ) {
                    System.out.println("Mi muovo");
                    moveComponentObject(objectImageView, destId);
                    event.setDropCompleted(true);
                }
            }
        }
        else if (state == PlayerState.WAIT_REMOVE_CREW && object.contains("alien")) { // Alien in WAIT_REMOVE_CREW
            List<Class<?>> allowedClasses = List.of(ClientCabinComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                if (
                    destId.contains("component") && allowedClasses.contains(destComponent.getClass()) && // Dest is flow pane or allowed component
                    objectsMap.get(destComponentId).isEmpty() &&
                    ((ClientCabinComponent) destComponent).getAlien() != null && ((ClientCabinComponent) destComponent).getAlien().toString().equals(object.toUpperCase().split("-")[1])
                ) {
                    System.out.println("Mi muovo");
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

        if (state == PlayerState.WAIT_REMOVE_GOODS && object.equals("battery")) { // Battery in WAIT_REMOVE_GOODS
            List<Class<?>> allowedClasses = List.of(ClientBatteryComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                System.out.println("Mi muovo");
                moveComponentObject(objectImageView, destId);
                event.setDropCompleted(true);
            }
        }
        else if ((state == PlayerState.WAIT_REMOVE_GOODS || state == PlayerState.WAIT_GOODS) && object.contains("good")) { // Good in WAIT_REMOVE_GOODS or WAIT_GOODS
            List<Class<?>> allowedClasses = List.of(ClientCargoHoldsComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                System.out.println("Mi muovo");
                moveComponentObject(objectImageView, destId);
                event.setDropCompleted(true);
            }
        }
        else if (state == PlayerState.WAIT_REMOVE_CREW && object.equals("human")) { // Human in WAIT_REMOVE_CREW
            List<Class<?>> allowedClasses = List.of(ClientCabinComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                System.out.println("Mi muovo");
                moveComponentObject(objectImageView, destId);
                event.setDropCompleted(true);
            }
        }
        else if (state == PlayerState.WAIT_REMOVE_CREW && object.contains("alien")) { // Alien in WAIT_REMOVE_CREW
            List<Class<?>> allowedClasses = List.of(ClientCabinComponent.class);
            if (sourceId.equals(flowPane.getId()) || (sourceId.contains("component") && allowedClasses.contains(Objects.requireNonNull(sourceComponent).getClass()))) { // Source is flow pane or allowed component
                System.out.println("Mi muovo");
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
            (state == PlayerState.WAIT_REMOVE_CREW && (object.contains("alien") || object.equals("human")))
        ) {
            Dragboard db = iv.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();

            content.putString(contentString);
            db.setContent(content);
            db.setDragView(iv.snapshot(null, null), 10, 10);
        }

        event.consume();
    };

    @FXML
    public void initialize() {
        MessageDispatcher.getInstance().registerHandler(this);
        this.client = App.getClientInstance();
        this.model = client.getGameController().getModel();
        this.overlayManager = new OverlayManager(root);
    }

    private void loadImages() {
        Image playerShipImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cardboard/cardboard-1b.jpg")));
        playerShipImage.setImage(playerShipImg);

        Image cardBackImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cards/GT-cards_II_IT_0121.jpg")));
        currentCardImage.setImage(cardBackImg);
    }

    public void setImageMap(Map<Integer, ImageView> imageMap) {
        this.imageMap = imageMap;
        loadImages();
        updateBoard();
        setupPlayerShipTilesContainer();
        syncAction();
    }

    private void setupPlayerShipTilesContainer() {
        for (ClientPlayer player : client.getGameController().getModel().getBoard().getAllPlayers()) {
            ClientShip ship = player.getShip();
            if (player.getUsername().equals(client.getUsername())) {
                shipGrid.setLayoutX(playerShipImage.getLayoutX());
                shipGrid.setLayoutY(playerShipImage.getLayoutY());
                shipGrid.setPrefWidth(playerShipImage.getFitWidth());
                shipGrid.setPrefHeight(playerShipImage.getFitHeight());
                shipGrid.setPadding(new Insets(14.0, 10.0, 20.0, 17.0));
                addTilesToPlayerShip(shipGrid, ship, TILE_WIDTH, TILE_HEIGHT);
            }
            else {
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
        shipImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cardboard/cardboard-1b.jpg"))));
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
        // tilesGrid.setStyle("-fx-grid-lines-visible: true;");
        tilesGrid.setPadding(new Insets(9.8, 10.0, 9.8, 10.0)); // Circa la metà dei valori originali

        // Add all
        addTilesToPlayerShip(tilesGrid, ship, OTHER_PLAYERS_TILE_WIDTH, OTHER_PLAYERS_TILE_HEIGHT);
        shipPane.getChildren().addAll(shipImageView, tilesGrid);

        OtherPlayerShipContainer containerData = new OtherPlayerShipContainer(shipPane, tilesGrid, shipImageView);
        otherPlayersShips.add(containerData);

        // Set click animation
        shipPane.setOnMouseClicked(_ -> {
            ParallelTransition zoomIn = createZoomToCenterAnimation(shipPane, Duration.millis(500));

            zoomIn.setOnFinished(_ -> {
                Scene scene = shipPane.getScene();
                EventHandler<MouseEvent> clickOutsideHandler = new EventHandler<>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (!shipPane.contains(shipPane.sceneToLocal(event.getSceneX(), event.getSceneY()))) { // Click outside
                            ParallelTransition zoomOut = createZoomBackAnimation(shipPane, Duration.millis(500));
                            zoomOut.play();
                            scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                        }
                    }
                };

                scene.addEventHandler(MouseEvent.MOUSE_CLICKED, clickOutsideHandler); // Add event handler

            });

            zoomIn.play();
        });

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
                                changeComponentObjects(c, 1, List.of("/images/objects/alien-"+c.getAlien()+".png"));
                            else
                                changeComponentObjects(c, c.getHumans(), List.of("/images/objects/human.png"));
                        }
                        case ClientBatteryComponent c -> changeComponentObjects(c, c.getBatteries(), List.of("/images/objects/battery.png"));
                        case ClientCargoHoldsComponent c -> changeComponentObjects(c, c.getGoods().size(), c.getGoods().stream().map(good -> "/images/objects/good-"+good.name().toLowerCase()+".png").toList());
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

    public Pane createShipComponent(ClientComponent component, double width, double height) {
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

    public void changeComponentObjects(ClientComponent component, int n, List<String> imagesPath) {
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

        Bounds boundsInScene = componentPane.localToScene(componentPane.getBoundsInLocal());
        double baseX = boundsInScene.getMinX();
        double baseY = boundsInScene.getMinY();
        double overlaySize = Math.min(baseWidth / 3.2, baseHeight / 3.2);

        if (diff > 0) { // Add images
            for (int i = 0; i < diff; i++) {
                ImageView overlayImage = new ImageView();
                String path = i < imagesPath.size() ? imagesPath.get(i) : imagesPath.getLast();
                overlayImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(path))));

                if (path.contains("battery"))
                    overlayImage.setUserData("battery");
                else if (path.contains("good"))
                    overlayImage.setUserData("good-"+path.split("-")[1].split("\\.")[0]);
                else if (path.contains("human"))
                    overlayImage.setUserData("human");
                else if (path.contains("alien"))
                    overlayImage.setUserData("alien-"+path.split("-")[1].split("\\.")[0]);

                overlayImage.setFitWidth(overlaySize);
                overlayImage.setFitHeight(overlaySize);
                overlayImage.setPreserveRatio(true);

                double spacing3 = (baseWidth - (3 * overlaySize)) / 4;
                double overlayX = baseX + spacing3 + (objects.size() * (overlaySize + spacing3));
                double overlayY = baseY + ((objects.size() % 2 == 0 ? 1 : -1)* baseHeight / 5) + (baseHeight - overlaySize) / 2;

                overlayImage.setLayoutX(overlayX);
                overlayImage.setLayoutY(overlayY);

                overlayImage.addEventHandler(MouseEvent.DRAG_DETECTED, objectDragDetectedHandler);

                componentPane.getChildren().add(overlayImage);
                objects.add(overlayImage);
            }
        }
        else { // Remove images
            for (int i = 0; i < Math.abs(diff); i++) {
                ImageView iw = objects.getLast();

                iw.removeEventHandler(MouseEvent.DRAG_DETECTED, objectDragDetectedHandler);

                iw.setImage(null);
                ((Pane) componentPane.getParent()).getChildren().remove(iw);
                objects.remove(iw);

            }
        }
    }

    public void moveComponentObject(ImageView object, String destId) {
        // Remove from source
        String sourceId = objectsMap.entrySet().stream()
            .filter(e -> e.getValue().contains(object))
            .map(e -> "component_"+e.getKey())
            .findFirst()
            .orElse(flowPane.getId());

        if (sourceId.equals(flowPane.getId())) { // It was in flow pane
            flowPane.getChildren().remove(object);
        }
        else { // It was in a component
            paneMap.get(Integer.parseInt(sourceId.split("_")[1])).getChildren().remove(object);
            objectsMap.get(Integer.parseInt(sourceId.split("_")[1])).remove(object);
            reorderComponentObjects(Integer.parseInt(sourceId.split("_")[1]));
        }

        if (destId.equals(flowPane.getId())) { // Dragged over flow pane
            flowPane.getChildren().add(object);
        }
        else { // Dragged over a component
            // Get dimensions and position of base image
            int componentDestId = Integer.parseInt(destId.split("_")[1]);
            Pane destPane = paneMap.get(componentDestId);
            double baseWidth = destPane.getBoundsInLocal().getWidth();
            double baseHeight = destPane.getBoundsInLocal().getHeight();

            double overlaySize = Math.min(baseWidth / 3.2, baseHeight / 3.2);
            double spacing3 = (baseWidth - (3 * overlaySize)) / 4;
            double overlayX = spacing3 + (objectsMap.get(componentDestId).size() * (overlaySize + spacing3));
            double overlayY = ((objectsMap.get(componentDestId).size() % 2 == 0 ? 1 : -1)* baseHeight / 5) + (baseHeight - overlaySize) / 2;

            object.setLayoutX(overlayX);
            object.setLayoutY(overlayY);

            if (!destPane.getChildren().contains(object))
                destPane.getChildren().add(object);

            objectsMap.get(componentDestId).add(object);
        }
    }

    public void reorderComponentObjects(int componentId) {
        Pane pane = paneMap.get(componentId);
        List<ImageView> objects = objectsMap.get(componentId);
        for (int i = 0; i < objects.size(); i++) {
            double baseWidth = pane.getBoundsInLocal().getWidth();
            double baseHeight = pane.getBoundsInLocal().getHeight();

            double overlaySize = Math.min(baseWidth / 3.2, baseHeight / 3.2);
            double spacing3 = (baseWidth - (3 * overlaySize)) / 4;
            double overlayX = spacing3 + (i * (overlaySize + spacing3));
            double overlayY = ((i % 2 == 0 ? 1 : -1)* baseHeight / 5) + (baseHeight - overlaySize) / 2;

            objects.get(i).setLayoutX(overlayX);
            objects.get(i).setLayoutY(overlayY);
        }
    }

    public ParallelTransition createZoomToCenterAnimation(Node node, Duration duration) {
        Scene scene = node.getScene();

        Parent originalParent = node.getParent();
        Bounds originalBounds = node.getBoundsInParent();
        node.setUserData(new RestoreData(originalParent, originalBounds.getMinX(), originalBounds.getMinY()));

        Parent root = scene.getRoot();
        if (root instanceof Pane rootPane) {

            // Remove from original container
            if (originalParent instanceof Pane) {
                ((Pane) originalParent).getChildren().remove(node);
            }

            // Convert coordinates
            Bounds boundsInScene = originalParent.localToScene(originalBounds);
            double newX = boundsInScene.getMinX();
            double newY = boundsInScene.getMinY();

            // Find absolute position
            node.setLayoutX(newX);
            node.setLayoutY(newY);
            node.setTranslateX(0);
            node.setTranslateY(0);

            // Add fixed
            rootPane.getChildren().add(node);
        }

        double sceneWidth = scene.getWidth();
        double sceneHeight = scene.getHeight();
        double centerX = sceneWidth / 2;
        double centerY = sceneHeight / 2;

        // Actual position
        Bounds boundsInScene = node.localToScene(node.getBoundsInLocal());
        double currentCenterX = boundsInScene.getMinX() + boundsInScene.getWidth() / 2;
        double currentCenterY = boundsInScene.getMinY() + boundsInScene.getHeight() / 2;

        double translateX = centerX - currentCenterX;
        double translateY = centerY - currentCenterY;

        // Zoom 2x
        ScaleTransition scaleTransition = new ScaleTransition(duration, node);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(2.0);
        scaleTransition.setToY(2.0);

        // Translate
        TranslateTransition translateTransition = new TranslateTransition(duration, node);
        translateTransition.setFromX(node.getTranslateX());
        translateTransition.setFromY(node.getTranslateY());
        translateTransition.setToX(node.getTranslateX() + translateX);
        translateTransition.setToY(node.getTranslateY() + translateY);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(scaleTransition, translateTransition);

        return parallelTransition;
    }

    public ParallelTransition createZoomBackAnimation(Node node, Duration duration) {

        RestoreData restoreData = (RestoreData) node.getUserData();
        if (restoreData == null) return null;

        ScaleTransition scaleTransition = new ScaleTransition(duration, node);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);

        TranslateTransition translateTransition = new TranslateTransition(duration, node);
        translateTransition.setToX(0);
        translateTransition.setToY(0);

        ParallelTransition parallelTransition = new ParallelTransition();
        parallelTransition.getChildren().addAll(scaleTransition, translateTransition);

        parallelTransition.setOnFinished(e -> {
            Scene scene = node.getScene();
            Parent root = scene.getRoot();

            if (root instanceof Pane) {
                ((Pane) root).getChildren().remove(node);
            }

            if (restoreData.originalParent instanceof Pane) {
                node.setLayoutX(restoreData.originalX);
                node.setLayoutY(restoreData.originalY);
                node.setTranslateX(0);
                node.setTranslateY(0);
                node.setScaleX(1.0);
                node.setScaleY(1.0);
                ((Pane) restoreData.originalParent).getChildren().add(node);
            }
            node.setUserData(null);

        });

        return parallelTransition;
    }

    public void updateBoard() {
        StringBuilder sb = new StringBuilder();
        for (SimpleEntry<ClientPlayer, Integer> player : client.getGameController().getModel().getBoard().getPlayers()) {
            sb.append(player.getValue().toString()).append(" ").append(player.getKey().toRawString()).append(" | ").append(player.getKey().getCredits()).append(" credits\n");
        }
        if (sb.isEmpty())
            sb.append("none");
        playersInGameLabel.setText(sb.toString());

        sb.setLength(0);
        for (ClientPlayer player : client.getGameController().getModel().getBoard().getStartingDeck()) {
            sb.append(player.toRawString()).append(" | ").append(player.getCredits()).append(" credits\n");
        }
        if (sb.isEmpty())
            sb.append("none");
        playersStartingDeckLabel.setText(sb.toString());
    }

    @SuppressWarnings("Duplicates")
    @FXML
    private void viewPreviousCardsHandler() {

        overlayManager.showOverlay(() -> {});

        HBox centralBox = overlayManager.getCentralHBox();
        centralBox.setAlignment(Pos.CENTER);
        centralBox.setSpacing(10);

        for (ClientCard card : model.getBoard().getCardPile()) {
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

    @FXML
    private void leaveGameHandler() {
        client.send(MessageType.LEAVE_GAME);
    }

    @FXML
    private void endFlightHandler() {
        client.send(MessageType.END_FLIGHT);
    }

    private void syncAction() {
        this.state = client.getGameController().getModel().getPlayerState(client.getUsername());

        list1.clear();
        list2.clear();

        mainButton.setDisable(false);
        mainButton.setVisible(true);
        mainButton.setOnAction(null);
        for (Pane pane : paneMap.values()) {
            pane.setOnMouseClicked(null);
            pane.setOnMouseEntered(null);
            pane.setOnMouseExited(null);
        }

        // Disable flow pane
        flowPane.setVisible(false);
        flowPane.removeEventHandler(DragEvent.DRAG_OVER, acceptDragOverHandler);
        flowPane.removeEventHandler(DragEvent.DRAG_DROPPED, flowPaneDragDroppedHandler);

        switch (state) {
            case CHECK -> {
                mainButton.setText("Done");
                mainButton.setOnAction(_ -> client.send(MessageType.CHECK_SHIP, list1));

                paneMap.forEach((id, pane) -> {
                    pane.setOnMouseEntered(_ -> {

                        if (list1.contains(id))
                            pane.setStyle("-fx-background-color: darkred;");
                        else
                            pane.setStyle("-fx-background-color: orange;");
                    });

                    pane.setOnMouseExited(_ -> {
                        if (list1.contains(id))
                            pane.setStyle("-fx-background-color: red;");
                        else
                            pane.setStyle("-fx-background-color: transparent;");
                    });

                    pane.setOnMouseClicked(_ -> {
                        if (list1.contains(id))
                            list1.remove(id);
                        else
                            list1.add(id);

                        mainButton.setDisable(list1.isEmpty());
                    });
                });
            }
            case WAIT_ALIEN -> {
                mainButton.setText("Done");
                mainButton.setOnAction(_ -> {
                    Map<Integer, AlienType> alienMap = new HashMap<>();
                        for (int i = 0; i < list1.size(); i++) {
                            ClientOddComponent odd = (ClientOddComponent) client.getGameController().getModel().getBoard().getMapIdComponents().get(list2.get(i));
                            alienMap.put(list1.get(i), odd.getType());
                        }
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
                    objectsMap.forEach((id, objects) -> {
                        ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(id);
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
                    });
                    client.send(MessageType.ACTIVATE_CANNONS, list2, list1);
                });
            }
            case WAIT_ENGINES -> {
                mainButton.setText("Done");
                mainButton.setOnAction(_ -> {
                    objectsMap.forEach((id, objects) -> {
                        ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(id);
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
                    });
                    client.send(MessageType.ACTIVATE_ENGINES, list2, list1);
                });
            }
            case WAIT_ROLL_DICES -> {
                mainButton.setText("Roll dices");
                mainButton.setOnAction(_ -> client.send(MessageType.ROLL_DICES));
            }
            case WAIT_SHIELD -> {
                mainButton.setText("Done");
                mainButton.setOnAction(_ -> {
                    objectsMap.forEach((id, objects) -> {
                        ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(id);
                        if (component instanceof ClientBatteryComponent c) {
                            for (int i = objects.size(); i < c.getBatteries(); i++)
                                list2.add(id);
                        }
                    });
                    if (list2.size() > 1) {} // TODO set error troppi shield attivati
                    client.send(MessageType.ACTIVATE_SHIELD, list2.isEmpty() ? null : list2.getFirst());
                });
            }
            case WAIT_SHIP_PART -> {
                mainButton.setText("Done");
                mainButton.setOnAction(_ -> client.send(MessageType.CHOOSE_SHIP_PART, 0));
            }
            case WAIT_REMOVE_CREW -> {
                // Enable flow pane
                flowPane.setVisible(true);
                flowPane.addEventHandler(DragEvent.DRAG_OVER, acceptDragOverHandler);
                flowPane.addEventHandler(DragEvent.DRAG_DROPPED, flowPaneDragDroppedHandler);

                mainButton.setText("Done");
                mainButton.setOnAction(_ -> {
                    objectsMap.forEach((id, objects) -> {
                       ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(id);
                       if (component instanceof ClientCabinComponent c) {
                           int crewInCabin = c.getAlien() != null ? 1 : c.getHumans();
                           for (int i = objects.size(); i < crewInCabin; i++)
                               list1.add(id);
                       }
                    });

                    for (Integer i : list1)
                        System.out.println(i + " ");

                    client.send(MessageType.REMOVE_CREW, list1);
                });
            }
            case WAIT_REMOVE_GOODS, WAIT_GOODS -> {
                // Enable flow pane
                flowPane.setVisible(true);
                flowPane.addEventHandler(DragEvent.DRAG_OVER, acceptDragOverHandler);
                flowPane.addEventHandler(DragEvent.DRAG_DROPPED, flowPaneDragDroppedHandler);

                mainButton.setText("Done");
                mainButton.setOnAction(_ -> {
                    Map<Integer, List<ColorType>> newDisposition = new HashMap<>();
                    objectsMap.forEach((id, objects) -> {
                        ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(id);
                        switch (component) {
                            case ClientCargoHoldsComponent _ -> {
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
                    });
                    if (state == PlayerState.WAIT_GOODS)
                        client.send(MessageType.UPDATE_GOODS, newDisposition);
                    else {
                        client.send(MessageType.UPDATE_GOODS, newDisposition, list2);
                    }
                });
            }
            case WAIT_INDEX -> {
                mainButton.setText("Done");
                mainButton.setOnAction(_ -> client.send(MessageType.GET_INDEX, index));
            }
            case WAIT_BOOLEAN -> {
                mainButton.setText("Done");
                mainButton.setOnAction(_ -> client.send(MessageType.GET_BOOLEAN, decision));
            }
            case WAIT, DONE -> {
                mainButton.setVisible(false);
            }
        }
    }

    private void setToggleClickEvent(ClientComponent component, Pane p, List<Integer> list, DropShadow shadow) {
        p.setOnMouseClicked(_ -> {
            if (list.contains(component.getId())) {
                list.remove(component.getId());
                p.setEffect(null);
            }
            else {
                list.add(component.getId());
                p.setEffect(shadow);
            }
        });
    }

    private static class Effects {

        private static final DropShadow cannonShadow = new DropShadow();
        private static final DropShadow engineShadow = new DropShadow();
        private static final DropShadow batteryShadow = new DropShadow();
        private static final DropShadow grayShadow = new DropShadow();

        public Effects() {
            cannonShadow.setColor(Color.MEDIUMPURPLE);
            cannonShadow.setRadius(15.0);
            cannonShadow.setSpread(0.7);
            cannonShadow.setOffsetX(0);
            cannonShadow.setOffsetY(0);

            engineShadow.setColor(Color.DARKORANGE);
            engineShadow.setRadius(15.0);
            engineShadow.setSpread(0.7);
            engineShadow.setOffsetX(0);
            engineShadow.setOffsetY(0);

            batteryShadow.setColor(Color.GOLD);
            batteryShadow.setRadius(15.0);
            batteryShadow.setSpread(0.7);
            batteryShadow.setOffsetX(0);
            batteryShadow.setOffsetY(0);

            grayShadow.setColor(Color.GRAY);
            grayShadow.setRadius(15.0);
            grayShadow.setSpread(0.7);
            grayShadow.setOffsetX(0);
            grayShadow.setOffsetY(0);
        }

    }

    // Classe helper per contenere i dati di una nave di un altro giocatore
    private static class OtherPlayerShipContainer {
        private final Pane container;
        private final GridPane tilesPane;
        private final ImageView shipImageView;

        private final double width;
        private final double height;

        public OtherPlayerShipContainer(Pane container, GridPane tilesPane, ImageView shipImageView) {
            this.container = container;
            this.tilesPane = tilesPane;
            this.shipImageView = shipImageView;

            this.width = tilesPane.getChildren().getFirst().getBoundsInLocal().getWidth();
            this.height = tilesPane.getChildren().getFirst().getBoundsInLocal().getHeight();
        }

        public Pane getContainer() { return container; }
        public Pane getTilesPane() { return tilesPane; }
        public ImageView getShipImageView() { return shipImageView; }
    }

    private static class RestoreData {
        final Parent originalParent;
        final double originalX;
        final double originalY;

        RestoreData(Parent parent, double x, double y) {
            this.originalParent = parent;
            this.originalX = x;
            this.originalY = y;
        }
    }

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
                MessageType.CREDITS_UPDATED_EVENT,
                MessageType.PLAYERS_POSITION_UPDATED_EVENT,
                MessageType.JOINED_LOBBY_EVENT,
                MessageType.LEFT_LOBBY_EVENT,
                MessageType.CARD_REVEALED_EVENT,
                MessageType.CARD_UPDATED_EVENT
        ).contains(messageType);
    }

    @Override
    public void handleMessage(GameEvent event) {
        switch (event) {
            case PlayersStateUpdatedEvent e -> {

                if (state != e.states().get(client.getUsername()))
                    syncAction();

                // Check if card is covered
                if (e.states().containsValue(PlayerState.DRAW_CARD)) {
                    Image cardBackImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cards/GT-cards_II_IT_0121.jpg")));
                    currentCardImage.setImage(cardBackImg);
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
                    p.setOnMouseClicked(null);

                    GridPane grid = (GridPane) p.getParent();
                    grid.getChildren().remove(p);
                    grid.add(emptySlot, component.getX(), component.getY());

                    paneMap.remove(e.id());
                    objectsMap.remove(e.id());

                    if (state == PlayerState.CHECK) {
                        list1.clear();
                        mainButton.setDisable(true);
                    }
                }
            }
            case BatteriesUpdatedEvent e -> {
                ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(e.id());
                changeComponentObjects(component, e.batteries(), List.of("/images/objects/battery.png"));
            }
            case CrewUpdatedEvent e -> {
                ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(e.id());
                String path = "/images/objects/" + (e.alien() != null ? ("alien-"+e.alien().toString().toLowerCase()) : "human") + ".png";
                changeComponentObjects(component, e.alien() != null ? 1 : e.humans(), List.of(path));
            }
            case GoodsUpdatedEvent e -> {
                ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(e.id());
                List<String> paths = new ArrayList<>();
                for (ColorType good : e.goods())
                    paths.add("/images/objects/good-" + good.name().toLowerCase() + ".png");
                changeComponentObjects(component, e.goods().size(), paths);
            }
            case CreditsUpdatedEvent _, PlayersPositionUpdatedEvent _, JoinedLobbyEvent _ -> updateBoard();
            case LeftLobbyEvent e -> {
                if (e.username().equals(client.getUsername())) // Your action
                    SceneManager.navigateToScene("/fxml/menu.fxml", this, null);
                else // Not your action
                    updateBoard();
            }
            case CardRevealedEvent e -> {
                Image cardBackImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cards/"+e.card().getId()+".jpg")));
                currentCardImage.setImage(cardBackImg);
            }
            case CardUpdatedEvent e -> {

            }
            case FlightEndedEvent e -> {
                if (e.username().equals(client.getUsername()))
                    endFlightButton.setVisible(false);
                updateBoard();
            }
            case GameErrorEvent e -> {}
            default -> {}
        }
    }

}