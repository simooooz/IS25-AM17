package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.CannonFire;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PiratesCard extends EnemiesCard {

    private final int credits;
    private final List<CannonFire> cannonFires;

    private final List<PlayerData> defeatedPlayers;
    private int cannonIndex;
    private int coord;

    public PiratesCard(int id, int level, boolean isLearner, int piratesFirePower, int credits, int days, List<CannonFire> cannonFires) {
        super(id, level, isLearner, days, piratesFirePower);
        this.credits = credits;
        this.cannonFires = cannonFires;
        this.defeatedPlayers = new ArrayList<>();
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.cannonIndex = 0;
        return super.startCard(model, board);
    }

    @Override
    public boolean calcHasDone(ModelFacade model, Board board) {
        boolean hasDone = true;
        for (PlayerData player : players)
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone && defeatedPlayers.isEmpty()) {
            if (!model.isLearnerMode())
                endCard(board);
            return true;
        }
        else if (hasDone) {
            if (cannonIndex >= cannonFires.size()) {
                if (!model.isLearnerMode())
                    endCard(board);
                return true;
            }
            else {
                for (PlayerData player : defeatedPlayers)
                    model.setPlayerState(player.getUsername(), PlayerState.WAIT);
                model.setPlayerState(defeatedPlayers.getFirst().getUsername(), PlayerState.WAIT_ROLL_DICES);
            }
        }
        return false;
    }

    @Override
    public boolean defeatedMalus(ModelFacade model, PlayerData player) {
        defeatedPlayers.add(player);
        model.setPlayerState(player.getUsername(), PlayerState.DONE);
        return false;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_ROLL_DICES) {
            this.coord = value;
            for (PlayerData player : defeatedPlayers) {
                PlayerState newState = cannonFires.get(cannonIndex).hit(player.getShip(), coord);
                model.setPlayerState(player.getUsername(), newState);
            }
            cannonIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Double value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_CANNONS) {
            if (value > enemyFirePower && !enemiesDefeated) { // Ask if user wants to redeem rewards
                model.setPlayerState(username, PlayerState.WAIT_BOOLEAN);
                enemiesDefeated = true;
                return false;
            }
            else if (value >= enemyFirePower) { // Tie or pirates already defeated
                model.setPlayerState(username, PlayerState.DONE);
            }
            else { // Player is defeated
                defeatedPlayers.add(board.getPlayerEntityByUsername(username));
                model.setPlayerState(username, PlayerState.DONE);
            }
            playerIndex++;
            return autoCheckPlayers(model,board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_BOOLEAN) {
            model.setPlayerState(username, PlayerState.DONE);
            if (value) {
                board.movePlayer(player, -1*days);
                player.setCredits(credits + player.getCredits());
            }
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        else if (commandType == PlayerState.WAIT_SHIELD) {
            if (value) // Shield activated
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else { // Not activated => find target and if present calc new state
                Optional<Component> target = cannonFires.get(cannonIndex).getTarget(player.getShip(), coord);
                target.ifPresent(component -> {
                    PlayerState newState = component.destroyComponent(player.getShip()); // DONE or WAIT_SHIP_PART
                    model.setPlayerState(player.getUsername(), newState);
                });
            }
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_SHIP_PART) {
            model.setPlayerState(username, PlayerState.DONE);
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
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

        String title = " " + vBorder + Constants.inTheMiddle("Pirates" + (getIsLearner() ? "(L)" : ""), 21) + vBorder + " ";
        cardLines.add(title);

        // First row divider
        String divider = " " + leftDivider + Constants.repeat(hBorder, 21) + rightDivider + " ";
        cardLines.add(divider);

        String firePowerRow = " " + vBorder + "\u2009" + Constants.inTheMiddle(enemyFirePower + " 💥", 20) +
                "\u2009"  + "\u200A" + vBorder + " ";
        cardLines.add(firePowerRow);

        cardLines.add(divider);

        for (CannonFire c : cannonFires) {
            String meteorRow = " " + vBorder + "     " + c.toString() +"\u200A" + "\u2005" + "     " + vBorder + " ";
            cardLines.add(meteorRow);
        }

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
            String def = defeatedPlayers.contains(player) ? "(defeated)" : "";

            switch (state) {
                case DONE -> Chroma.println("- " + player.getUsername() + " has done " + def, Chroma.YELLOW_BOLD);
                case WAIT -> Chroma.println("- " + player.getUsername() + " is waiting " + def, Chroma.YELLOW_BOLD);
                case WAIT_BOOLEAN -> Chroma.println("- " + player.getUsername() + " is choosing if take the reward or not", Chroma.YELLOW_BOLD);
                case WAIT_SHIELD -> Chroma.println("- " + player.getUsername() + " is choosing if activate a shield or not " + def, Chroma.YELLOW_BOLD);
                case WAIT_CANNONS -> Chroma.println("- " + player.getUsername() + " is choosing if activate double cannons or not " + def, Chroma.YELLOW_BOLD);
                case WAIT_ROLL_DICES -> Chroma.println("- " + player.getUsername() + " is rolling dices " + def, Chroma.YELLOW_BOLD);
                case WAIT_SHIP_PART -> Chroma.println("- " + player.getUsername() + " might have lost part of his ship " + def, Chroma.YELLOW_BOLD);
            }
        }
        Chroma.println("Pirates are" + (enemiesDefeated ? " " : " not ") + "defeated", Chroma.YELLOW_BOLD);

        if (enemiesDefeated && board.getPlayersByPos().stream().noneMatch(p -> model.getPlayerState(p.getUsername()) == PlayerState.WAIT_ROLL_DICES))
            Chroma.println("Cannon fire n." + (cannonIndex+1) + " is hitting at coord: " + coord, Chroma.YELLOW_BOLD);
        else if (cannonIndex > 0)
            Chroma.println("Previous cannon fire n." + (cannonIndex) + " has come at coord: " + coord, Chroma.YELLOW_BOLD);

    }

}
