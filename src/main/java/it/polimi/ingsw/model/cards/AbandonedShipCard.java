package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;

public class AbandonedShipCard extends Card {

    private final int crew;
    private final int credits;
    private final int days;

    private int playerIndex;
    private List<PlayerData> players;
    private boolean shipConquered;

    public AbandonedShipCard(int id, int level, boolean isLearner, int crew, int credits, int days) {
        super(id, level, isLearner);
        this.crew = crew;
        this.credits = credits;
        this.days = days;
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.playerIndex = 0;
        this.players = new ArrayList<>(board.getPlayersByPos());
        this.shipConquered = false;

        for (PlayerData player : board.getPlayersByPos())
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);

        return autoCheckPlayers(model, board);
    }

    private boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < players.size(); playerIndex++) {
            PlayerData player = players.get(playerIndex);

            if (shipConquered)
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (player.getShip().getCrew() < crew) // User loses automatically
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else { // User could win
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_BOOLEAN);
                return false;
            }
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : players)
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        return hasDone;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_BOOLEAN && value) {
            model.setPlayerState(username, PlayerState.WAIT_REMOVE_CREW);
            shipConquered = true;
            return false;
        }
        else if (commandType == PlayerState.WAIT_BOOLEAN) {
            model.setPlayerState(username, PlayerState.DONE);
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_REMOVE_CREW) {
            model.setPlayerState(username, PlayerState.DONE);

            PlayerData player = board.getPlayerEntityByUsername(username);
            board.movePlayer(player, days * -1);
            player.setCredits(credits + player.getCredits());

            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, List<CabinComponent> cabins, int toRemove, String username, Board board) {
        super.doSpecificCheck(commandType, cabins, this.crew, username, board);
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

        String title = " " + vBorder + Constants.inTheMiddle("AbandonedShip" + (getIsLearner() ? "(L)" : ""), 21) + vBorder + " ";
        cardLines.add(title);

        // First row divider
        String divider = " " + leftDivider + Constants.repeat(hBorder, 21) + rightDivider + " ";
        cardLines.add(divider);

        String crewRow = " " + vBorder + "\u2009" + Constants.inTheMiddle(crew + " 🧑🏻‍🚀❌", 24) +
                "\u2009"  + vBorder + " ";
        cardLines.add(crewRow);

        cardLines.add(divider);

        String creditRow = " " + vBorder + "\u2009" + Constants.inTheMiddle(credits + " 💲", 20) +
                "\u2009"  + "\u200A" + vBorder + " ";
        cardLines.add(creditRow);

        cardLines.add(divider);

        String dayRow = " " + vBorder + "\u2009" + Constants.inTheMiddle(days + " 📅", 20) +
                "\u2009"  + "\u200A" + vBorder + " ";
        cardLines.add(dayRow);

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
                case WAIT_BOOLEAN -> Chroma.println("- " + player.getUsername() + " is choosing if visit ship or not", Chroma.YELLOW_BOLD);
                case WAIT_REMOVE_CREW -> Chroma.println("- " + player.getUsername() + " is removing crew", Chroma.YELLOW_BOLD);
            }
        }
    }

}
