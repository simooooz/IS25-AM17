package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.components.*;
import it.polimi.ingsw.client.model.events.CardRevealedEvent;
import it.polimi.ingsw.client.model.events.CardUpdatedEvent;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.player.ClientShip;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.common.model.events.game.*;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;
import it.polimi.ingsw.view.GUI.App;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import javafx.event.Event;
import java.net.URL;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class FlightPhaseController implements MessageHandler {

    @FXML public Label playersInGameLabel;
    @FXML public Label playersStartingDeckLabel;
    @FXML public StackPane previousCardsOverlay;
    @ FXML public HBox previousCardsContainer;
    // FXML Elements
    @FXML private VBox otherPlayersContainer;
    @FXML private ListView<String> logListView;
    @FXML private Pane currentCardContainer;
    @FXML private ImageView currentCardImage;
    @FXML private Button viewPreviousCardsButton;
    @FXML private Pane playerShipContainer;
    @FXML private ImageView playerShipImage;
    @FXML private GridPane playerShipTilesContainer;
    @FXML private StackPane diceRollOverlay;

    // Data structures
    private ObservableList<String> logMessages;
    private List<OtherPlayerShipContainer> otherPlayersShips;

    private Client client;
    private PlayerState state;
    private Map<ClientComponent, Pane> imageMap = new HashMap<>();
    private Map<ClientComponent, List<ImageView>> objectsMap = new HashMap<>();
    private List<Integer> list1 = new ArrayList<>();
    private List<Integer> list2 = new ArrayList<>();

    private static final double TILE_WIDTH = 57.6;
    private static final double TILE_HEIGHT = 57.5;
    private static final double OTHER_PLAYERS_TILE_WIDTH = 36.0;
    private static final double OTHER_PLAYERS_TILE_HEIGHT = 35.8;

    @FXML
    public void initialize() {
        this.client = App.getClientInstance();

        loadImages();
        updateBoard();
        initializeComponents();
        setupEventHandlers();
        setupplayerShipTilesContainer();
    }

    private void updateState() {
        this.state = client.getGameController().getModel().getPlayerState(client.getUsername());

    }

    private void syncAction() {
        updateState();

        switch (state) {
            case DRAW_CARD -> currentCardImage.setOnMouseClicked(e -> client.send(MessageType.DRAW_CARD));
            case WAIT_CANNONS -> {
                list1.clear();
                list2.clear();
                imageMap.forEach((cc, p) -> {
                    if (cc instanceof ClientCannonComponent)
                        setToggleClickEvent(cc, p, list1, Effects.cannonShadow);
                    else if (cc instanceof ClientBatteryComponent)
                        setToggleClickEvent(cc, p, list2, Effects.batteryShadow);
                });
            }
            case WAIT_ENGINES -> {
                list1.clear();
                list2.clear();
                imageMap.forEach((cc, p) -> {
                    if (cc instanceof ClientEngineComponent)
                        setToggleClickEvent(cc, p, list1, Effects.cannonShadow);
                    else if (cc instanceof ClientBatteryComponent)
                        setToggleClickEvent(cc, p, list2, Effects.batteryShadow);
                });
            }
            case WAIT_ROLL_DICES -> diceRollOverlay.setVisible(true);
            case WAIT_SHIELD -> {
                list2.clear();
                imageMap.forEach((cc, p) -> {
                    if (cc instanceof ClientBatteryComponent) {
                        imageMap.get(cc).setOnMouseClicked(_ -> {
                            if (!list2.isEmpty()) {
                                ClientComponent oldComponent = client.getGameController().getModel().getBoard().getMapIdComponents().get(list2.getFirst());
                                imageMap.get(oldComponent).setEffect(null);
                                list2.clear();
                            }
                            list2.add(cc.getId());
                            p.setEffect(Effects.batteryShadow);
                        });
                    }
                });
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

    @FXML
    private void onDiceRollOverlayClicked() {
        client.send(MessageType.ROLL_DICES);
    }

    private void setupplayerShipTilesContainer() {
        for (ClientPlayer player : client.getGameController().getModel().getBoard().getAllPlayers()) {
            ClientShip ship = player.getShip();
            if (player.getUsername().equals(client.getUsername()))
                updatePlayerShip(playerShipTilesContainer, ship);
            else {
                Label label = new Label(player.getUsername() + "'s ship");
                label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 20px 10px 10px 10px;");
                otherPlayersContainer.getChildren().add(label);

                Pane shipContainer = createOtherPlayerShipContainer(ship);
                otherPlayersContainer.getChildren().add(shipContainer);
            }
        }
    }

    private void initializeComponents() {
        logMessages = FXCollections.observableArrayList();
        logListView.setItems(logMessages);

        // Inizializza la lista dei container per le altre navi
        otherPlayersShips = new ArrayList<>();

        // Imposta stili per la ListView del log
        logListView.setStyle("-fx-background-color: #0d0520; -fx-text-fill: white;");
    }

    private void setupEventHandlers() {
        // Event handler già collegato tramite FXML per il bottone delle carte precedenti
    }

    private void loadImages() {
        Image playerShipImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cardboard/cardboard-1b.jpg")));
        playerShipImage.setImage(playerShipImg);

        Image cardBackImg = new Image(getClass().getResourceAsStream("/images/cards/GT-cards_II_IT_0121.jpg"));
        currentCardImage.setImage(cardBackImg);
    }

    // Metodi per aggiornare la GUI in base agli eventi del model

    private Pane createOtherPlayerShipContainer(ClientShip ship) {
        // Ship image view
        ImageView shipImageView = new ImageView();
        shipImageView.setFitWidth(280.0);
        //shipImageView.setFitHeight(196.0);
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


    /**
     * Aggiorna la nave del giocatore
     */
    public void updatePlayerShip(GridPane grid, ClientShip ship) {
        playerShipTilesContainer.setLayoutX(playerShipImage.getLayoutX());
        playerShipTilesContainer.setLayoutY(playerShipImage.getLayoutY());
        playerShipTilesContainer.setPrefWidth(playerShipImage.getFitWidth());
        playerShipTilesContainer.setPrefHeight(playerShipImage.getFitHeight());
        playerShipTilesContainer.setPadding(new Insets(14.0, 10.0, 20.0, 17.0));
        addTilesToPlayerShip(grid, ship, TILE_WIDTH, TILE_HEIGHT);
    }

    private void addTilesToPlayerShip(GridPane grid, ClientShip ship, double width, double height) {
        for (int row = 0; row < Constants.SHIP_ROWS; row++) {
            for (int col = 0; col < Constants.SHIP_COLUMNS; col++) {
                Optional<ClientComponent> component = ship.getDashboard(row, col);
                if (component.isPresent()) {
                    Pane imagePane = createShipComponent(component.get(), width, height);
                    imageMap.put(component.get(), imagePane);
                    grid.add(imagePane, col, row);

                    switch (component.get()) {
                        case ClientCabinComponent c -> {
                            if (c.getHumans() > 0)
                                changeComponentObjects(c, c.getHumans(), List.of("/images/objects/human.png"));
                            else if (c.getAlien() != null) {}
                                //changeComponentObjects(c, 1, List.of("/images/objects/alien"));
                        }
                        case ClientBatteryComponent c -> changeComponentObjects(c, c.getBatteries(), List.of("/images/objects/battery.png"));
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
        ImageView componentImage = new ImageView();
        String imagePath = "/images/tiles/GT-new_tiles_16_for web" + component.getId() + ".jpg";
        componentImage.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath))));

        imagePane.setPrefWidth(width);
        imagePane.setPrefHeight(height);
        componentImage.setFitWidth(width);
        componentImage.setFitHeight(height);
        imagePane.getChildren().add(componentImage);

        return imagePane;
    }

    public void changeComponentObjects(ClientComponent component, int n, List<String> imagesPath) {
        Pane componentPane = imageMap.get(component);
        List<ImageView> objects = objectsMap.get(component);
        if (objects == null) {
            objectsMap.put(component, new ArrayList<>());
            objects = objectsMap.get(component);
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

                overlayImage.setFitWidth(overlaySize);
                overlayImage.setFitHeight(overlaySize);
                overlayImage.setPreserveRatio(true);

                double spacing3 = (baseWidth - (3 * overlaySize)) / 4;
                double overlayX = baseX + spacing3 + (i * (overlaySize + spacing3));
                double overlayY = baseY + ((i % 2 == 0 ? 1 : -1)* baseHeight / 5) + (baseHeight - overlaySize) / 2;

                overlayImage.setLayoutX(overlayX);
                overlayImage.setLayoutY(overlayY);

                componentPane.getChildren().add(overlayImage);
                objects.add(overlayImage);
            }
        }
        else { // Remove images
            for (int i = 0; i < Math.abs(diff); i++) {
                ImageView iw = objects.getLast();
                iw.setImage(null);
                ((Pane) componentPane.getParent()).getChildren().remove(iw);
                objects.remove(iw);

            }
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

    /**
     * Aggiorna la carta attuale
     */
    public void updateCurrentCard(/* Card card */) {
        // TODO: Ricevere la carta attuale dal model

        if (/* card != null */ false) {
            // TODO: Mostra la carta specifica
            // currentCardImage.setImage(new Image("path/to/card/" + card.getId() + ".png"));
        } else {
            // Mostra la carta coperta
            // TODO: currentCardImage.setImage(new Image("path/to/card/back.png"));
        }
    }

    /**
     * Aggiunge un messaggio al log
     */
    public void addLogMessage(String message) {
        logMessages.add(0, message); // Aggiunge in cima

        // Mantiene solo gli ultimi 5 messaggi
        if (logMessages.size() > 5) {
            logMessages.remove(5);
        }
    }

    public void updateBoard() {
        StringBuilder sb = new StringBuilder();
        for (SimpleEntry<ClientPlayer, Integer> player : client.getGameController().getModel().getBoard().getPlayers()) {
            sb.append(player.getValue().toString()).append(" ").append(player.getKey().toString()).append(" | ").append(player.getKey().getCredits()).append(" credits\n");
        }
        if (sb.isEmpty())
            sb.append("none");
        playersInGameLabel.setText(sb.toString());

        sb.setLength(0);
        for (ClientPlayer player : client.getGameController().getModel().getBoard().getStartingDeck()) {
            sb.append(player.toString()).append(" | ").append(player.getCredits()).append(" credits\n");
        }
        if (sb.isEmpty())
            sb.append("none");
        playersStartingDeckLabel.setText(sb.toString());
    }

    // Event Handlers

    @FXML
    private void onViewPreviousCards() {
        for (int i=0; i<10; i++) {
            ImageView iw = new ImageView();
            iw.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cards/GT-cards_II_IT_0121.jpg"))));
            iw.setFitWidth(196.0);
            iw.setFitHeight(296.0);
            iw.setLayoutX(2.0);
            iw.setLayoutY(2.0);
            iw.setPickOnBounds(true);
            iw.setPreserveRatio(true);
            previousCardsContainer.getChildren().add(iw);
        }

        previousCardsOverlay.setVisible(true);

        Scene scene = previousCardsOverlay.getScene();
        EventHandler<MouseEvent> clickOutsideHandler = new EventHandler<>() {
            @Override
            public void handle(MouseEvent event) {
                if (!previousCardsOverlay.contains(previousCardsOverlay.sceneToLocal(event.getSceneX(), event.getSceneY()))) { // Click outside
                    previousCardsContainer.getChildren().removeAll();
                    previousCardsOverlay.setVisible(false);
                    scene.removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
                }
            }
        };

        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, clickOutsideHandler); // Add event handler
    }

    // Metodi di utilità per il calcolo delle posizioni



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
            System.out.println("Ship w " + width);
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
        return false;
    }

    @Override
    public void handleMessage(GameEvent event) {
        switch (event) {
            case PlayersStateUpdatedEvent e -> {

                // Check if card is covered
                if (e.states().containsValue(PlayerState.DRAW_CARD)) {
                    String shipPath = "/images/cardboard/cardboard-1b.jpg";
                    Image playerShipImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(shipPath)));
                    playerShipImage.setImage(playerShipImg);
                }

                syncAction();

            }
            case ComponentDestroyedEvent e -> {
                ClientComponent component = client.getGameController().getModel().getBoard().getMapIdComponents().get(e.id());
                Pane p = imageMap.get(component);

                if (p != null) {
                    double width = p.getBoundsInLocal().getWidth();
                    double height = p.getBoundsInLocal().getHeight();

                    Rectangle emptySlot = new Rectangle(width, height);
                    emptySlot.setFill(Color.TRANSPARENT);
                    emptySlot.setStroke(Color.TRANSPARENT);
                    emptySlot.setMouseTransparent(true);

                    GridPane grid = (GridPane) p.getParent();
                    grid.getChildren().remove(p);
                    grid.add(emptySlot, component.getX(), component.getY());
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
                    paths.add("/images/objects/good-" + good.toString().toLowerCase() + ".png");
                changeComponentObjects(component, e.goods().size(), paths);
            }
            case FlightEndedEvent _, CreditsUpdatedEvent _, PlayersPositionUpdatedEvent _ -> updateBoard();
            case CardRevealedEvent e -> {
                Image cardBackImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/cards/GT-cards_II_IT_0121.jpg")));
                currentCardImage.setImage(cardBackImg);
            }
            case CardUpdatedEvent e -> {

            }
            default -> {}
        }
    }

}