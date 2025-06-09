package it.polimi.ingsw.view.GUI;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.factory.ComponentFactory;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.messages.MessageType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.*;

public class BuildController implements MessageHandler, Initializable {

    @FXML
    private ImageView shipImageView;

    @FXML
    private GridPane shipGrid;

    @FXML
    private FlowPane componentsFlowPane;

    @FXML
    private Label componentsCountLabel;

    private Client client;
    private Map<Integer, Component> availableComponents;
    private Set<Integer> revealedComponentIds;
    private Map<String, Component> placedComponents;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("BuildController initialized");

        this.availableComponents = JavaFxInterface.getClientInstance().getGameController().getModel().getBoard().getMapIdComponents();
        this.revealedComponentIds = new HashSet<>();
        this.placedComponents = new HashMap<>();

        setClient();
        setupShipGrid();
        setupComponentsArea();
        loadShipImage();
    }

    public void setClient() {
        this.client = JavaFxInterface.getClientInstance();
    }

    private void setupShipGrid() {
        Ship ship = client.getGameController().getModel().getBoard().getPlayerEntityByUsername(client.getUsername()).getShip();
        shipGrid.getChildren().clear();
        shipGrid.setPrefWidth(500.0);
        shipGrid.setPrefHeight(400.0);

        for (int row = 0; row < Constants.SHIP_ROWS; row++) {
            for (int col = 0; col < Constants.SHIP_COLUMNS; col++) {
                if (ship.validPositions(row, col)) {
                    Rectangle slot = createShipSlot(row, col);
                    if (row == 2 && col == 3) placeComponentInSlot(slot, ship.getDashboard(2, 3).get() ,row, col, 0);
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

    private Rectangle createShipSlot(int row, int col) {
        Rectangle slot = new Rectangle(70, 70);
        slot.setFill(Color.TRANSPARENT);
        slot.setStroke(Color.rgb(150, 150, 255, 0.6));
        slot.setStrokeWidth(1.5);
        slot.setOpacity(0.8);

        // Effetto hover
        slot.setOnMouseEntered(e -> {
            slot.setFill(Color.LIGHTBLUE.deriveColor(0, 1, 1, 0.4));
            slot.setStroke(Color.LIGHTBLUE);
            slot.setStrokeWidth(2);
        });

        slot.setOnMouseExited(e -> {
            if (slot.getFill() != Color.LIGHTGREEN.deriveColor(0, 1, 1, 0.6)) {
                slot.setFill(Color.TRANSPARENT);
                slot.setStroke(Color.rgb(150, 150, 255, 0.6));
                slot.setStrokeWidth(1.5);
            }
        });

        // Setup drop target per i componenti
        slot.setOnDragOver(event -> {
            if (event.getGestureSource() != slot &&
                    event.getDragboard().hasString()) {
                // MODIFICATO: Verifica che il componente sia valido per questa posizione
                String componentIdString = event.getDragboard().getString();
                int componentId = Integer.parseInt(componentIdString.replace("component_", ""));
                Component component = availableComponents.get(componentId);

                if (component != null && canPlaceComponent(component, row, col)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
            }
            event.consume();
        });

        slot.setOnDragEntered(event -> {
            if (event.getGestureSource() != slot &&
                    event.getDragboard().hasString()) {
                // MODIFICATO: Colore diverso se il componente può essere piazzato
                String componentIdString = event.getDragboard().getString();
                int componentId = Integer.parseInt(componentIdString.replace("component_", ""));
                Component component = availableComponents.get(componentId);

                if (component != null && canPlaceComponent(component, row, col)) {
                    slot.setFill(Color.GREEN.deriveColor(0, 1, 1, 0.5));
                } else {
                    slot.setFill(Color.RED.deriveColor(0, 1, 1, 0.5));
                }
                slot.setStrokeWidth(3);
            }
            event.consume();
        });

        slot.setOnDragExited(event -> {
            if (slot.getFill() != Color.LIGHTGREEN.deriveColor(0, 1, 1, 0.6)) {
                slot.setFill(Color.TRANSPARENT);
                slot.setStrokeWidth(1.5);
            }
            event.consume();
        });

        slot.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                String componentIdString = db.getString();
                int componentId = Integer.parseInt(componentIdString.replace("component_", ""));
                Component component = availableComponents.get(componentId);

                // MODIFICATO: Verifica che il componente sia valido prima di piazzarlo
                if (component != null && canPlaceComponent(component, row, col)) {
                    placeComponentInSlot(slot, component, row, col, 0);
                    success = true;
                } else {
                    System.out.println("❌ Impossibile piazzare il componente " + componentId + " in posizione (" + row + "," + col + ")");
                    // AGGIUNTO: Mostra informazioni sul perché non può essere piazzato
                    if (component != null) {
                        System.out.println("   Connettori richiesti: " + Arrays.toString(component.getConnectors()));
                        System.out.println("   Verificare compatibilità con componenti adiacenti");
                    }
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });

        return slot;
    }

    // NUOVO METODO: Verifica se un componente può essere piazzato in una posizione
    private boolean canPlaceComponent(Component component, int row, int col) {
        // Implementa qui la logica di validazione specifica per il tuo gioco
        // Ad esempio, verifica i connettori con i componenti adiacenti

        // Esempio base: verifica che la posizione sia libera
        String slotKey = row + "," + col;
        if (placedComponents.containsKey(slotKey)) {
            return false; // Posizione già occupata
        }

        // Aggiungi qui altre verifiche specifiche del gioco:
        // - Compatibilità dei connettori
        // - Regole di piazzamento specifiche
        // - Verifiche di continuità della nave

        return true; // Per ora accetta tutti i piazzamenti validi
    }

    private void setupComponentsArea() {
        componentsFlowPane.getChildren().clear();

        // MODIFICATO: Usa gli ID reali dei componenti dalla factory
        for (Integer componentId : availableComponents.keySet()) {
            ImageView componentCard = createCoveredComponent(componentId);
            componentsFlowPane.getChildren().add(componentCard);
        }

        updateComponentsCount();
    }

    private ImageView createCoveredComponent(int componentId) {
        ImageView cardView = new ImageView();
        cardView.setFitWidth(70);
        cardView.setFitHeight(70);
        cardView.setPreserveRatio(true);

        cardView.setImage(createBlackSquareImage());
        cardView.setId("component_" + componentId);

        cardView.setStyle(
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 6, 0, 2, 2);" +
                        "-fx-cursor: hand;"
        );

        cardView.setOnMouseEntered(e -> {
            cardView.setScaleX(1.05);
            cardView.setScaleY(1.05);
            cardView.setStyle(
                    "-fx-effect: dropshadow(gaussian, rgba(100,150,255,0.8), 8, 0, 3, 3);" +
                            "-fx-cursor: hand;"
            );
        });

        cardView.setOnMouseExited(e -> {
            cardView.setScaleX(1.0);
            cardView.setScaleY(1.0);
            cardView.setStyle(
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 6, 0, 2, 2);" +
                            "-fx-cursor: hand;"
            );
        });

        cardView.setOnMouseClicked(event -> {
            pickComponent(cardView, componentId);
        });

        cardView.setOnDragDetected(event -> {
            if (revealedComponentIds.contains(componentId)) {
                Dragboard db = cardView.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("component_" + componentId);
                db.setContent(content);
                db.setDragView(cardView.snapshot(null, null), 35, 35);

                // MODIFICATO: Mostra informazioni sul componente reale
                Component component = availableComponents.get(componentId);
                System.out.println("Iniziato drag del componente " + componentId);
                System.out.println("  - Tipo: " + component.getClass().getSimpleName());
                System.out.println("  - Connettori: " + Arrays.toString(component.getConnectors()));
            }
            event.consume();
        });

        cardView.setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                System.out.println("✓ Componente " + componentId + " piazzato con successo!");
                componentsFlowPane.getChildren().remove(cardView);
            } else {
                System.out.println("✗ Drag del componente " + componentId + " annullato");
                if (client != null) {
                    client.send(MessageType.RELEASE_COMPONENT, componentId);
                }
            }
            event.consume();
        });

        return cardView;
    }

    private void pickComponent(ImageView cardView, int componentId) {
        if (!revealedComponentIds.contains(componentId)) {
            System.out.println("Componente " + componentId + " rivelato!");

            // MODIFICATO: Ottieni informazioni dal componente reale
            Component component = availableComponents.get(componentId);
            if (component != null) {
                System.out.println("  - Tipo: " + component.getClass().getSimpleName());
                System.out.println("  - Connettori: " + Arrays.toString(component.getConnectors()));

                // Mostra informazioni specifiche per tipo di componente
                printComponentSpecificInfo(component);
            }

            Image componentImage = loadSpecificComponentImage(componentId);
            cardView.setImage(componentImage);
            revealedComponentIds.add(componentId);

            cardView.setStyle(
                    "-fx-effect: dropshadow(gaussian, rgba(0,255,0,0.8), 8, 0, 3, 3);" +
                            "-fx-cursor: hand;"
            );

            if (client != null) {
                client.send(MessageType.PICK_COMPONENT, componentId);
            }
        }
    }

    // NUOVO METODO: Stampa informazioni specifiche per tipo di componente
    private void printComponentSpecificInfo(Component component) {
        String componentType = component.getClass().getSimpleName();

        switch (componentType) {
            case "BatteryComponent":
                // Cast sicuro e stampa info specifiche
                System.out.println("  - Info: Componente batteria");
                break;
            case "EngineComponent":
                System.out.println("  - Info: Componente motore");
                break;
            case "CannonComponent":
                System.out.println("  - Info: Componente cannone");
                break;
            case "CabinComponent":
                System.out.println("  - Info: Componente cabina");
                break;
            case "CargoHoldsComponent":
                System.out.println("  - Info: Componente cargo");
                break;
            case "ShieldComponent":
                System.out.println("  - Info: Componente scudo");
                break;
            default:
                System.out.println("  - Info: Componente generico");
                break;
        }
    }

    // MODIFICATO: Ora lavora con il componente reale
    private void placeComponentInSlot(Rectangle slot, Component component, int row, int col, int rotation) {
        System.out.println("🚀 POSIZIONAMENTO COMPONENTE:");
        System.out.println("  - ID: " + component.getId());
        System.out.println("  - Tipo: " + component.getClass().getSimpleName());
        System.out.println("  - Connettori: " + Arrays.toString(component.getConnectors()));
        System.out.println("  - Posizione: (" + row + "," + col + ")");

        slot.setFill(Color.LIGHTGREEN.deriveColor(0, 1, 1, 0.6));
        slot.setStroke(Color.GREEN);
        slot.setStrokeWidth(2);

        // AGGIUNTO: Traccia il componente piazzato
        String slotKey = row + "," + col;
        placedComponents.put(slotKey, component);

        if (client != null) {
            client.getGameController().insertComponent(client.getUsername(), component.getId(), row, col, rotation, false);
            client.send(MessageType.INSERT_COMPONENT, component.getId(), row, col, 0);
        }

        loadSpecificComponentImageForShip(component.getId(), row, col, rotation);
    }

    // NUOVO METODO: Ottieni componente da una posizione
    public Component getComponentAt(int row, int col) {
        String slotKey = row + "," + col;
        return placedComponents.get(slotKey);
    }

    // NUOVO METODO: Ottieni tutti i componenti piazzati
    public Map<String, Component> getPlacedComponents() {
        return new HashMap<>(placedComponents);
    }

    private void loadShipImage() {
        try {
            Image shipImage = new Image(getClass().getResourceAsStream("/images/cardboard/cardboard-1b.jpg"));
            if (shipImageView != null) {
                shipImageView.setImage(shipImage);
                shipImageView.setPreserveRatio(true);
                shipImageView.setOpacity(0.3);
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dell'immagine della nave: " + e.getMessage());
            if (shipImageView != null) {
                shipImageView.setImage(null);
            }
        }
    }

    private Image createBlackSquareImage() {
        try {
            return new Image(getClass().getResourceAsStream("/images/sfondo.jpg"));
        } catch (Exception e) {
            return new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAFUlEQVR42mNkYPhfz0AEYBxVSF+FAP//D/H/9Wz0AAAAAElFTkSuQmCC");
        }
    }

    private Image loadSpecificComponentImage(int componentId) {
        try {
            String imagePath = "/images/tiles/GT-new_tiles_16_for web" + componentId + ".jpg";
            return new Image(getClass().getResourceAsStream(imagePath));
        } catch (Exception e) {
            System.err.println("Errore nel caricamento del componente " + componentId + ": " + e.getMessage());
            return createPlaceholderImage();
        }
    }

    private void loadSpecificComponentImageForShip(int componentId, int row, int col, int rotation) {
        try {
            String imagePath = "/images/tiles/GT-new_tiles_16_for web" + componentId + ".jpg";
            Image image = new Image(getClass().getResourceAsStream(imagePath));

            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(64);  // Imposta la dimensione desiderata
            imageView.setFitHeight(64);
            imageView.setPreserveRatio(true);

            imageView.setRotate(rotation); // Rotazione in gradi

            shipGrid.add(imageView, col, row); // Attenzione: GridPane usa (col, row)

        } catch (Exception e) {
            System.err.println("Errore nel caricamento del componente " + componentId + ": " + e.getMessage());

            // In caso d'errore puoi aggiungere un'immagine segnaposto
            ImageView placeholder = new ImageView(createPlaceholderImage());
            placeholder.setFitWidth(64);
            placeholder.setFitHeight(64);
            placeholder.setPreserveRatio(true);

            shipGrid.add(placeholder, col, row);
        }
    }


    private Image createPlaceholderImage() {
        try {
            return new Image(getClass().getResourceAsStream("/images/sfondo.png"));
        } catch (Exception e) {
            return new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");
        }
    }

    private void updateComponentsCount() {
        if (componentsCountLabel != null) {
            int totalComponents = availableComponents.size();
            int revealedCount = revealedComponentIds.size();
            int placedCount = placedComponents.size();

            componentsCountLabel.setText(
                    "Componenti: " + totalComponents +
                            " (Rivelati: " + revealedCount +
                            ", Piazzati: " + placedCount + ")"
            );
        }
    }

    @Override
    public void handleMessage(MessageType eventType, String username, Object... args) {
        switch (eventType) {
            case MessageType.RELEASE_COMPONENT:
                int componentId = Integer.parseInt((String) args[1]);
                // MODIFICATO: Usa l'ID reale del componente
                if (availableComponents.containsKey(componentId)) {
                    loadSpecificComponentImage(componentId);
                    updateComponentsCount();
                }
                break;
            case ERROR:
                System.err.println("Errore ricevuto: " + (args.length > 0 ? args[0] : "Errore sconosciuto"));
                break;
            default:
                System.out.println("Messaggio ricevuto: " + eventType);
                break;
        }
    }

    @Override
    public boolean canHandle(MessageType messageType) {
        return messageType == MessageType.ERROR ||
                messageType == MessageType.RESERVE_COMPONENT ||
                messageType == MessageType.INSERT_COMPONENT ||
                messageType == MessageType.RELEASE_COMPONENT;
    }
}