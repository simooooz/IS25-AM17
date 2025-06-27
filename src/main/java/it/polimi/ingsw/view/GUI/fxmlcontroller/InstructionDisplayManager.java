package it.polimi.ingsw.view.GUI.fxmlcontroller;

import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.network.Client;
import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * Manages the display of instructions and status messages for the GUI,
 * providing dynamic guidance to players based on their current game state.
 *
 * <p>This class serves as the GUI equivalent of the TUI DisplayUpdater,
 * automatically updating instruction labels and status messages to help
 * players understand what actions they can take in each phase of the game.</p>
 */
public class InstructionDisplayManager {

    /**
     * Client instance for accessing game state and player information.
     */
    private final Client client;

    /**
     * Label displaying detailed instructions for the current game state.
     */
    private final Label instructionLabel;

    /**
     * Label displaying brief status messages about the current phase.
     */
    private final Label statusMessageLabel;

    /**
     * Flag indicating whether the game is running in learner mode,
     * affecting which instructions are displayed.
     */
    private final boolean isLearnerMode;

    /**
     * Color hex code for informational messages (blue).
     */
    private static final String INFO_COLOR = "#4A90E2";

    /**
     * Color hex code for warning messages (orange).
     */
    private static final String WARNING_COLOR = "#F5A623";

    /**
     * Color hex code for error messages (red).
     */
    private static final String ERROR_COLOR = "#D0021B";

    /**
     * Color hex code for success messages (green).
     */
    private static final String SUCCESS_COLOR = "#7ED321";

    /**
     * Color hex code for waiting state messages (purple).
     */
    private static final String WAITING_COLOR = "#9013FE";

    /**
     * Constructs a new InstructionDisplayManager with the specified client and labels.
     *
     * @param client The client instance for accessing game state
     * @param instructionLabel The label to display detailed instructions
     * @param statusMessageLabel The label to display status messages
     */
    public InstructionDisplayManager(Client client, Label instructionLabel, Label statusMessageLabel) {
        this.client = client;
        this.instructionLabel = instructionLabel;
        this.statusMessageLabel = statusMessageLabel;
        this.isLearnerMode = client.getLobby().isLearnerMode();
    }

    /**
     * Updates both instructions and status messages based on the current player state.
     * This method should be called whenever the game state changes to ensure
     * the displayed information remains current and relevant.
     */
    public void updateInstructions() {
        if (client.getLobby() != null) { // Player has left
            Platform.runLater(() -> {
                PlayerState state = client.getGameController().getModel().getPlayerState(client.getUsername());
                updateInstructionsForState(state);
                updateStatusMessage(state);
            });
        }
    }

    private void updateInstructionsForState(PlayerState state) {
        String instructions = getInstructionsForState(state);
        String color = getColorForState(state);

        setLabelText(instructionLabel, instructions, color);
    }

    private void updateStatusMessage(PlayerState state) {
        String statusMessage = getStatusMessageForState(state);
        String color = getStatusColorForState(state);

        setLabelText(statusMessageLabel, statusMessage, color);
    }

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
        };
    }

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
        };
    }

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

    private String getStatusColorForState(PlayerState state) {
        return switch (state) {
            case CHECK, WAIT_SHIP_PART -> ERROR_COLOR;
            case WAIT, DONE -> WAITING_COLOR;
            case END -> SUCCESS_COLOR;
            default -> INFO_COLOR;
        };
    }

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
        return """
                • Your ship configuration is invalid
                • Click on components you want to remove
                • Selected components will be highlighted in red
                • Click 'Done' when you've selected all components to remove""";
    }

    private String getWaitAlienInstructions() {
        return """
                • Drag aliens from the panel to cabin components
                • Engine aliens provide movement bonuses
                • Cannon aliens provide combat bonuses
                • Each cabin can hold one alien
                • Click 'Done' when finished placing aliens""";
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
        return """
                • Click 'Draw Card' to reveal the next adventure card
                • The card will determine your next actions
                • Wait for the card to be revealed""";
    }

    private String getWaitCannonsInstructions() {
        return """
                • Drag batteries to double cannons to activate them
                • Activated cannons provide combat advantages
                • Each double cannon needs one battery
                • You can choose which cannons to activate
                • Click 'Done' when finished""";
    }

    private String getWaitEnginesInstructions() {
        return """
                • Drag batteries to double engines to activate them
                • Activated engines provide movement advantages
                • Each double engine needs one battery
                • You can choose which engines to activate
                • Click 'Done' when finished""";
    }

    private String getWaitGoodsInstructions() {
        return """
                • Drag goods from the panel to cargo holds
                • Match goods to appropriate cargo hold types
                • Red, Blue, and Yellow goods available
                • Each cargo hold has limited capacity
                • Click 'Done' when finished arranging goods""";
    }

    private String getWaitRemoveGoodsInstructions() {
        return """
                • You have too many goods and must remove some
                • Drag goods from cargo holds back to the panel
                • Or use batteries to avoid removing goods
                • Drag batteries from battery components if needed
                • Click 'Done' when finished""";
    }

    private String getWaitRollDicesInstructions() {
        return """
                • Click 'Roll Dices' to determine event outcomes
                • Dice results will affect your ship and crew
                • Results are random and cannot be changed""";
    }

    private String getWaitRemoveCrewInstructions() {
        return """
                • Remove crew members from cabins as required
                • Drag humans or aliens from cabins to the panel
                • Follow the card requirements for removal
                • Click 'Done' when finished removing crew""";
    }

    private String getWaitShieldInstructions() {
        return """
                • Optionally activate shields for protection
                • Drag one battery to a shield component
                • Shields can prevent damage from events
                • You can choose not to activate shields
                • Click 'Done' to confirm your choice""";
    }

    private String getWaitBooleanInstructions() {
        return """
                • Choose whether to take the offered reward
                • Click 'Take Reward' to accept the bonus
                • Click 'Decline' to refuse the reward
                • Consider the risks and benefits carefully
                • Click 'Done' to confirm your decision""";
    }

    private String getWaitIndexInstructions() {
        return """
                • Choose which planet to land on
                • Click the numbered buttons to select a planet
                • Each planet offers different rewards and risks
                • Consider your ship's capabilities
                • Click 'Done' to confirm your choice""";
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
        return """
                • Game has finished!
                • Check final scores and rankings
                • View other players' final ship configurations
                • Click 'Leave Game' to return to main menu
                • Congratulations on completing your space adventure!""";
    }

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

    private void setLabelText(Label label, String text, String colorHex) {
        if (label != null) {
            label.setText(text);
            label.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-size: 15px; -fx-font-weight: normal;");
        }
    }


    /**
     * Displays a temporary success message that automatically reverts after 3 seconds.
     * Success messages are shown in green color to indicate positive outcomes.
     *
     * @param successMessage The success message to display
     */
    public void showSuccessMessage(String successMessage) {
        Platform.runLater(() -> {
            setLabelText(statusMessageLabel, successMessage, SUCCESS_COLOR);
            // Restore normal message after 3 seconds
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
     * Triggers an update of instructions when the player state changes.
     * This method should be called by controllers when they detect
     * state changes that require updated guidance.
     */
    public void onPlayerStateChanged() {
        updateInstructions();
    }

    /**
     * Resets both labels to initial state with default messages.
     * Used when starting a new game or initializing the interface.
     */
    public void reset() {
        Platform.runLater(() -> {
            setLabelText(instructionLabel, "Waiting for game to start...", INFO_COLOR);
            setLabelText(statusMessageLabel, "Game initializing...", INFO_COLOR);
        });
    }
}