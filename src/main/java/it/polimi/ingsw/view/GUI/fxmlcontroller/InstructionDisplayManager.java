package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.game.ClientBoard;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.player.ClientShip;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.network.Client;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * Gestisce la visualizzazione delle istruzioni e dei messaggi di stato per la GUI
 * in modo simile al DisplayUpdater della TUI.
 */
public class InstructionDisplayManager {

    private final Client client;
    private final Label instructionLabel;
    private final Label statusMessageLabel;
    private final boolean isLearnerMode;

    // Colori per diversi tipi di messaggi
    private static final String INFO_COLOR = "#4A90E2";      // Blu per istruzioni
    private static final String WARNING_COLOR = "#F5A623";   // Arancione per avvertimenti
    private static final String ERROR_COLOR = "#D0021B";     // Rosso per errori
    private static final String SUCCESS_COLOR = "#7ED321";   // Verde per successo
    private static final String WAITING_COLOR = "#9013FE";   // Viola per attesa

    public InstructionDisplayManager(Client client, Label instructionLabel, Label statusMessageLabel) {
        this.client = client;
        this.instructionLabel = instructionLabel;
        this.statusMessageLabel = statusMessageLabel;
        this.isLearnerMode = client.getLobby().isLearnerMode();
    }

    /**
     * Aggiorna le istruzioni e i messaggi di stato basandosi sullo stato del giocatore
     */
    public void updateInstructions() {
        Platform.runLater(() -> {
            PlayerState state = client.getGameController().getModel().getPlayerState(client.getUsername());
            updateInstructionsForState(state);
            updateStatusMessage(state);
        });
    }

    /**
     * Aggiorna le istruzioni specifiche per ogni stato
     */
    private void updateInstructionsForState(PlayerState state) {
        String instructions = getInstructionsForState(state);
        String color = getColorForState(state);

        setLabelText(instructionLabel, instructions, color);
    }

    /**
     * Aggiorna il messaggio di stato
     */
    private void updateStatusMessage(PlayerState state) {
        String statusMessage = getStatusMessageForState(state);
        String color = getStatusColorForState(state);

        setLabelText(statusMessageLabel, statusMessage, color);
    }

    /**
     * Restituisce le istruzioni per lo stato corrente
     */
    private String getInstructionsForState(PlayerState state) {
        return switch (state) {
            case BUILD, LOOK_CARD_PILE -> getBuildPhaseInstructions();
            case CHECK -> getCheckPhaseInstructions();
            case WAIT_ALIEN -> getWaitAlienInstructions();
            case WAIT_SHIP_PART -> getWaitShipPartInstructions();
            case DRAW_CARD -> getDrawCardInstructions();
            case WAIT_CANNONS -> getWaitCannonsInstructions();
            case WAIT_ENGINES -> getWaitEnginesInstructions();
            case WAIT_GOODS -> getWaitGoodsInstructions();
            case WAIT_REMOVE_GOODS -> getWaitRemoveGoodsInstructions();
            case WAIT_ROLL_DICES -> getWaitRollDicesInstructions();
            case WAIT_REMOVE_CREW -> getWaitRemoveCrewInstructions();
            case WAIT_SHIELD -> getWaitShieldInstructions();
            case WAIT_BOOLEAN -> getWaitBooleanInstructions();
            case WAIT_INDEX -> getWaitIndexInstructions();
            case WAIT, DONE -> getWaitingInstructions();
            case END -> getEndGameInstructions();
            default -> "Waiting for game to start...";
        };
    }

    /**
     * Restituisce il messaggio di stato per lo stato corrente
     */
    private String getStatusMessageForState(PlayerState state) {
        return switch (state) {
            case BUILD, LOOK_CARD_PILE -> "Building Phase - Construct your ship";
            case CHECK -> "Ship Validation - Fix your ship configuration";
            case WAIT_ALIEN -> "Alien Assignment - Place aliens in cabins";
            case WAIT_SHIP_PART -> "Ship Repair - Your ship has broken apart";
            case DRAW_CARD -> "Card Draw - Ready to draw a new card";
            case WAIT_CANNONS -> "Cannon Activation - Activate double cannons";
            case WAIT_ENGINES -> "Engine Activation - Activate double engines";
            case WAIT_GOODS -> "Goods Management - Add goods to cargo holds";
            case WAIT_REMOVE_GOODS -> "Goods Removal - Remove excess goods";
            case WAIT_ROLL_DICES -> "Dice Roll - Roll dice for events";
            case WAIT_REMOVE_CREW -> "Crew Removal - Remove crew members";
            case WAIT_SHIELD -> "Shield Activation - Activate protective shields";
            case WAIT_BOOLEAN -> "Decision Required - Choose reward option";
            case WAIT_INDEX -> "Planet Selection - Choose landing destination";
            case WAIT, DONE -> "Waiting Phase - Other players are taking actions";
            case END -> "Game Complete - Final results available";
            default -> "Game in progress...";
        };
    }

    /**
     * Restituisce il colore appropriato per lo stato
     */
    private String getColorForState(PlayerState state) {
        return switch (state) {
            case CHECK, WAIT_SHIP_PART -> ERROR_COLOR;
            case WAIT_ALIEN, WAIT_GOODS, WAIT_REMOVE_GOODS, WAIT_CANNONS,
                 WAIT_ENGINES, WAIT_SHIELD -> WARNING_COLOR;
            case WAIT, DONE -> WAITING_COLOR;
            case END -> SUCCESS_COLOR;
            default -> INFO_COLOR;
        };
    }

    /**
     * Restituisce il colore per il messaggio di stato
     */
    private String getStatusColorForState(PlayerState state) {
        return switch (state) {
            case CHECK, WAIT_SHIP_PART -> ERROR_COLOR;
            case WAIT, DONE -> WAITING_COLOR;
            case END -> SUCCESS_COLOR;
            default -> INFO_COLOR;
        };
    }

    // Metodi per le istruzioni specifiche di ogni fase

    private String getBuildPhaseInstructions() {
        StringBuilder sb = new StringBuilder();
        sb.append("• Click components to select them\n");
        sb.append("• Drag components to place them on your ship\n");
        sb.append("• Right-click to rotate components\n");
        sb.append("• Use 'Pick' to select components from the market\n");
        sb.append("• Use 'Insert' to place components at specific coordinates\n");
        if (!isLearnerMode) {
            sb.append("• Use 'Reserve' to reserve components for later\n");
            sb.append("• Use 'Rotate Hourglass' to end your turn\n");
        }
        sb.append("• Click 'Ready' when you finish building");
        return sb.toString();
    }

    private String getCheckPhaseInstructions() {
        return "• Your ship configuration is invalid\n" +
                "• Click on components you want to remove\n" +
                "• Selected components will be highlighted in red\n" +
                "• Click 'Done' when you've selected all components to remove";
    }

    private String getWaitAlienInstructions() {
        return "• Drag aliens from the panel to cabin components\n" +
                "• Engine aliens provide movement bonuses\n" +
                "• Cannon aliens provide combat bonuses\n" +
                "• Each cabin can hold one alien\n" +
                "• Click 'Done' when finished placing aliens";
    }

    private String getWaitShipPartInstructions() {
        StringBuilder sb = new StringBuilder();
        sb.append("• Your ship has broken into multiple parts\n");
        sb.append("• Choose which part of your ship to keep\n");
        sb.append("• Click on the ship section you want to preserve\n");
        if (!isLearnerMode) {
            sb.append("• Or use 'End Flight' to return to starting deck\n");
        }
        sb.append("• Click 'Done' to confirm your choice");
        return sb.toString();
    }

    private String getDrawCardInstructions() {
        return "• Click 'Draw Card' to reveal the next adventure card\n" +
                "• The card will determine your next actions\n" +
                "• Wait for the card to be revealed";
    }

    private String getWaitCannonsInstructions() {
        return "• Drag batteries to double cannons to activate them\n" +
                "• Activated cannons provide combat advantages\n" +
                "• Each double cannon needs one battery\n" +
                "• You can choose which cannons to activate\n" +
                "• Click 'Done' when finished";
    }

    private String getWaitEnginesInstructions() {
        return "• Drag batteries to double engines to activate them\n" +
                "• Activated engines provide movement advantages\n" +
                "• Each double engine needs one battery\n" +
                "• You can choose which engines to activate\n" +
                "• Click 'Done' when finished";
    }

    private String getWaitGoodsInstructions() {
        return "• Drag goods from the panel to cargo holds\n" +
                "• Match goods to appropriate cargo hold types\n" +
                "• Red, Blue, and Yellow goods available\n" +
                "• Each cargo hold has limited capacity\n" +
                "• Click 'Done' when finished arranging goods";
    }

    private String getWaitRemoveGoodsInstructions() {
        return "• You have too many goods and must remove some\n" +
                "• Drag goods from cargo holds back to the panel\n" +
                "• Or use batteries to avoid removing goods\n" +
                "• Drag batteries to battery components if needed\n" +
                "• Click 'Done' when finished";
    }

    private String getWaitRollDicesInstructions() {
        return "• Click 'Roll Dices' to determine event outcomes\n" +
                "• Dice results will affect your ship and crew\n" +
                "• Results are random and cannot be changed";
    }

    private String getWaitRemoveCrewInstructions() {
        return "• Remove crew members from cabins as required\n" +
                "• Drag humans or aliens from cabins to the panel\n" +
                "• Follow the card requirements for removal\n" +
                "• Click 'Done' when finished removing crew";
    }

    private String getWaitShieldInstructions() {
        return "• Optionally activate shields for protection\n" +
                "• Drag one battery to a shield component\n" +
                "• Shields can prevent damage from events\n" +
                "• You can choose not to activate shields\n" +
                "• Click 'Done' to confirm your choice";
    }

    private String getWaitBooleanInstructions() {
        return "• Choose whether to take the offered reward\n" +
                "• Click 'Take Reward' to accept the bonus\n" +
                "• Click 'Decline' to refuse the reward\n" +
                "• Consider the risks and benefits carefully\n" +
                "• Click 'Done' to confirm your decision";
    }

    private String getWaitIndexInstructions() {
        return "• Choose which planet to land on\n" +
                "• Click the numbered buttons to select a planet\n" +
                "• Each planet offers different rewards and risks\n" +
                "• Consider your ship's capabilities\n" +
                "• Click 'Done' to confirm your choice";
    }

    private String getWaitingInstructions() {
        StringBuilder sb = new StringBuilder();
        String phase = getCurrentPhase();
        sb.append("• Waiting for other players to complete their actions\n");
        sb.append("• You can view other players' ships while waiting\n");
        if (phase.equals("build") && !isLearnerMode) {
            sb.append("• Use 'Rotate Hourglass' to speed up the phase\n");
        }
        if (phase.equals("flight") && !isLearnerMode) {
            sb.append("• Use 'End Flight' to return to starting deck\n");
        }
        sb.append("• Be patient, your turn will come again");
        return sb.toString();
    }

    private String getEndGameInstructions() {
        return "• Game has finished!\n" +
                "• Check final scores and rankings\n" +
                "• View other players' final ship configurations\n" +
                "• Click 'Leave Game' to return to main menu\n" +
                "• Congratulations on completing your space adventure!";
    }

    /**
     * Determina la fase corrente del gioco
     */
    private String getCurrentPhase() {
        ClientGameModel model = client.getGameController().getModel();
        for (ClientPlayer p : model.getBoard().getAllPlayers()) {
            PlayerState playerState = model.getPlayerState(p.getUsername());
            if (playerState == PlayerState.BUILD || playerState == PlayerState.LOOK_CARD_PILE)
                return "build";
            else if (playerState == PlayerState.WAIT_ALIEN)
                return "alien";
            else if (playerState == PlayerState.CHECK)
                return "check";
            else
                return "flight";
        }
        return "flight";
    }

    /**
     * Imposta il testo e il colore di una label
     */
    private void setLabelText(Label label, String text, String colorHex) {
        if (label != null) {
            label.setText(text);
            label.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-size: 12px;");
        }
    }

    /**
     * Mostra un messaggio temporaneo di errore
     */
    public void showErrorMessage(String errorMessage) {
        Platform.runLater(() -> {
            setLabelText(statusMessageLabel, "ERROR: " + errorMessage, ERROR_COLOR);
            // Ripristina il messaggio normale dopo 5 secondi
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    updateInstructions();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
    }

    /**
     * Mostra un messaggio temporaneo di successo
     */
    public void showSuccessMessage(String successMessage) {
        Platform.runLater(() -> {
            setLabelText(statusMessageLabel, successMessage, SUCCESS_COLOR);
            // Ripristina il messaggio normale dopo 3 secondi
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    updateInstructions();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
    }

    /**
     * Aggiorna le istruzioni quando lo stato del giocatore cambia
     */
    public void onPlayerStateChanged() {
        updateInstructions();
    }

    /**
     * Pulisce i messaggi e reimposta lo stato iniziale
     */
    public void reset() {
        Platform.runLater(() -> {
            setLabelText(instructionLabel, "Waiting for game to start...", INFO_COLOR);
            setLabelText(statusMessageLabel, "Game initializing...", INFO_COLOR);
        });
    }
}