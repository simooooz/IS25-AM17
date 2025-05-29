package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.properties.DirectionType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.*;

public class SlaversCard extends Card {

    private final int crew;
    private final int credits;
    private final int days;
    private final int slaversFirePower;

    private List<PlayerData> players;
    private boolean slaversDefeated;
    private int playerIndex;

    public SlaversCard(int id, int level, boolean isLearner, int crew, int credits, int days, int slaversFirePower) {
        super(id, level, isLearner);
        this.crew = crew;
        this.credits = credits;
        this.days = days;
        this.slaversFirePower = slaversFirePower;
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.slaversDefeated = false;
        this.playerIndex = 0;
        this.players = new ArrayList<>(board.getPlayersByPos());

        for (PlayerData player : players)
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);
        return autoCheckPlayers(model, board);
    }

    private boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < players.size(); playerIndex++) {
            PlayerData player = players.get(playerIndex);

            double freeCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(cannon -> !cannon.getIsDouble())
                    .mapToDouble(CannonComponent::calcPower).sum();
            if (freeCannonsPower > 0 && player.getShip().getCannonAlien())
                freeCannonsPower += 2;

            double doubleCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(CannonComponent::getIsDouble)
                    .mapToDouble(cannon -> cannon.getDirection() == DirectionType.NORTH ? 2 : 1).sum();

            if (slaversDefeated)
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower > slaversFirePower) { // User wins automatically
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_BOOLEAN);
                slaversDefeated = true;
                return false;
            }
            else if (freeCannonsPower == slaversFirePower && doubleCannonsPower == 0)
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower + doubleCannonsPower >= slaversFirePower) { // User could win
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_CANNONS);
                return false;
            }
            else { // User loses automatically
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_REMOVE_CREW);
                return false;
            }
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : players)
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone) {
            if (!model.isLearnerMode())
                endCard(board);
            return true;
        }
        return false;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Double value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_CANNONS) {
            if (value > slaversFirePower && !slaversDefeated) { // Ask if user wants to redeem rewards
                model.setPlayerState(username, PlayerState.WAIT_BOOLEAN);
                slaversDefeated = true;
            }
            else if (value >= slaversFirePower) { // Tie or slavers already defeated
                model.setPlayerState(username, PlayerState.DONE);
                playerIndex++;
                return autoCheckPlayers(model, board);
            }
            else // Player is defeated
                model.setPlayerState(username, PlayerState.WAIT_REMOVE_CREW);

            return false;
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_BOOLEAN) {
            model.setPlayerState(username, PlayerState.DONE);
            if (value) {
                PlayerData player = board.getPlayerEntityByUsername(username);
                board.movePlayer(player, -1 * days);
                player.setCredits(credits + player.getCredits());
            }
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_REMOVE_CREW) {
            model.setPlayerState(username, PlayerState.DONE);
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

        String title = " " + vBorder + Constants.inTheMiddle("Slavers" + (getIsLearner() ? "(L)" : ""), 21) + vBorder + " ";
        cardLines.add(title);

        // First row divider
        String divider = " " + leftDivider + Constants.repeat(hBorder, 21) + rightDivider + " ";
        cardLines.add(divider);

        String firePowerRow = " " + vBorder + "\u2009" + Constants.inTheMiddle(slaversFirePower + " 💥", 20) +
                "\u2009" + "\u200A" + vBorder + " ";
        cardLines.add(firePowerRow);

        cardLines.add(divider);

        String crewRow = " " + vBorder + "\u2009" + Constants.inTheMiddle(crew + " 🧑🏻‍🚀❌", 24) +
                "\u2009" + vBorder + " ";
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
                case WAIT_BOOLEAN -> Chroma.println("- " + player.getUsername() + " is choosing if take the reward or not", Chroma.YELLOW_BOLD);
                case WAIT_REMOVE_CREW -> Chroma.println("- " + player.getUsername() + " has to pay his penalty (removing crew)", Chroma.YELLOW_BOLD);
                case WAIT_CANNONS -> Chroma.println("- " + player.getUsername() + " is choosing if activate double cannons or not", Chroma.YELLOW_BOLD);
            }
        }
        Chroma.println("Slavers are" + (slaversDefeated ? " " : " not ") + "defeated", Chroma.YELLOW_BOLD);
    }

}