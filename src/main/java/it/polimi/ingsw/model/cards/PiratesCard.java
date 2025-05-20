package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.CannonFire;
import it.polimi.ingsw.model.cards.utils.Meteor;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PiratesCard extends Card{

    private final int piratesFirePower;
    private final int credits;
    private final int days;
    private final List<CannonFire> cannonFires;

    private List<PlayerData> players;
    private List<PlayerData> defeatedPlayers;
    private boolean piratesDefeated;
    private int playerIndex;
    private int cannonIndex;
    private int coord;

    public PiratesCard(int level, boolean isLearner, int piratesFirePower, int credits, int days, List<CannonFire> cannonFires) {
        super(level, isLearner);
        this.piratesFirePower = piratesFirePower;
        this.credits = credits;
        this.days = days;
        this.cannonFires = cannonFires;
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.piratesDefeated = false;
        this.playerIndex = 0;
        this.cannonIndex = 0;
        this.defeatedPlayers = new ArrayList<>();
        this.players = new ArrayList<>(board.getPlayersByPos());

        for (PlayerData player : players) {
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);
        }
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
                    .mapToDouble(CannonComponent::calcPower)
                    .boxed()
                    .sorted(Comparator.reverseOrder())
                    .limit(player.getShip().getBatteries())
                    .mapToDouble(v -> v)
                    .sum();

            if (piratesDefeated)
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower > piratesFirePower) { // User wins automatically
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_BOOLEAN);
                piratesDefeated = true;
                return false;
            }
            else if (freeCannonsPower == piratesFirePower && doubleCannonsPower == 0) // User draws automatically
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower + doubleCannonsPower >= piratesFirePower) { // User could win
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_CANNONS);
                return false;
            }
            else { // User loses automatically
                defeatedPlayers.add(player);
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            }
        }

        // Check if everyone has finished
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
            if (value > piratesFirePower && !piratesDefeated) { // Ask if user wants to redeem rewards
                model.setPlayerState(username, PlayerState.WAIT_BOOLEAN);
                piratesDefeated = true;
                return false;
            }
            else if (value >= piratesFirePower) { // Tie or pirates already defeated
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

        String firePowerRow = " " + vBorder + "\u2009" + Constants.inTheMiddle(piratesFirePower + " 💥", 20) +
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

}
