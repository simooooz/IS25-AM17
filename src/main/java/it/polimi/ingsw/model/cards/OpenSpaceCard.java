package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenSpaceCard extends Card {

    private int playerIndex;
    private Map<PlayerData, Integer> enginesActivated = new HashMap<>();

    public OpenSpaceCard(int id, int level, boolean isLearner) {
        super(id, level, isLearner);
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        playerIndex = 0;
        this.enginesActivated = new HashMap<>();

        board.getPlayersByPos().forEach(player ->
                model.setPlayerState(player.getUsername(), PlayerState.WAIT)
        );
        return autoCheckPlayers(model, board);
    }

    public boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < board.getPlayersByPos().size(); playerIndex++) {
            PlayerData player = board.getPlayersByPos().get(playerIndex);

            int singleEnginesPower = player.getShip().getComponentByType(EngineComponent.class).stream()
                    .filter(engine -> !engine.getIsDouble())
                    .mapToInt(EngineComponent::calcPower)
                    .sum();
            if (singleEnginesPower > 0 && player.getShip().getEngineAlien())
                singleEnginesPower += 2;

            int doubleEnginesPower = player.getShip().getComponentByType(EngineComponent.class).stream()
                    .filter(EngineComponent::getIsDouble)
                    .mapToInt(EngineComponent::calcPower)
                    .limit(player.getShip().getBatteries())
                    .sum();

            if (doubleEnginesPower != 0) {
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_ENGINES);
                return false;
            } else {
                enginesActivated.put(player, singleEnginesPower);
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            }
        }

        // Check if everyone has finished
        if (playerIndex >= board.getPlayersByPos().size()) {

            for (PlayerData player : board.getPlayersByPos())
                board.movePlayer(player, enginesActivated.get(player));

            if (!model.isLearnerMode())
                endCard(board);
            return true;
        }
        return false;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Integer power, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_ENGINES) {
            model.setPlayerState(username, PlayerState.DONE);

            PlayerData player = board.getPlayerEntityByUsername(username);
            enginesActivated.put(player, power);

            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    protected void endCard(Board board) {
        for (PlayerData player : board.getPlayersByPos())
            if (enginesActivated.get(player) == 0)
                board.moveToStartingDeck(player);
        super.endCard(board);
    }

    @Override
    public String toString() {
        String hBorder = "─";
        String vBorder = "│";
        String[] angles = {"┌", "┐", "└", "┘"};
        String hDivider = "┼";
        String leftDivider = "├";
        String rightDivider = "┤";

        List<String> cardLines = new ArrayList<>();

        // Title box
        String topBorder = " " + angles[0] + Constants.repeat(hBorder, 21) + angles[1] + " ";
        cardLines.add(topBorder);

        String title = " " + vBorder + Constants.inTheMiddle("Open Space" + (getIsLearner() ? "(L)" : ""), 21) + vBorder + " ";
        cardLines.add(title);

        // First row divider
        String divider = " " + leftDivider + Constants.repeat(hBorder, 21) + rightDivider + " ";
        cardLines.add(divider);

        String row = " " + vBorder + "     🚀  " + "\u200A" + "⬆️" + "\u200A" + "  📅     " + vBorder + "";
        cardLines.add(row);


        // Bottom border
        String bottomBorder = " " + angles[2] + Constants.repeat(hBorder, 21) + angles[3] + " ";
        cardLines.add(bottomBorder);

        return String.join("\n", cardLines);
    }

    @Override
    public void printCardInfo(ModelFacade model, Board board) {
        for (PlayerData player : board.getPlayersByPos()) {
            PlayerState state = model.getPlayerState(player.getUsername());

            switch (state) {
                case DONE -> Chroma.println("- " + player.getUsername() + " has done", Chroma.YELLOW_BOLD);
                case WAIT -> Chroma.println("- " + player.getUsername() + " is waiting", Chroma.YELLOW_BOLD);
                case WAIT_ENGINES -> Chroma.println("- " + player.getUsername() + " is choosing if activate double engines or not", Chroma.YELLOW_BOLD);
            }
        }
    }

}