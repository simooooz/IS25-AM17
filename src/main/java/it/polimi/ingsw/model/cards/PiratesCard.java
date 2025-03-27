package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.utils.CannonFire;
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
    private double userCannonPower;
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
    public boolean startCard(Board board) {
        this.piratesDefeated = false;
        this.playerIndex = 0;
        this.cannonIndex = 0;
        this.defeatedPlayers = new ArrayList<>();
        this.players = board.getPlayersByPos();

        for (PlayerData player : players) {
            playersState.put(player.getUsername(), PlayerState.WAIT);
        }
        return autoCheckPlayers(board);
    }

    @Override
    protected boolean changeState(Board board, String username) {

        PlayerState actState = playersState.get(username);

        switch (actState) {
            case WAIT_BOOLEAN, WAIT_SHIELD -> playersState.put(username, PlayerState.DONE);
            case WAIT_CANNONS -> {
                if (userCannonPower > piratesFirePower && !piratesDefeated) { // Ask if user wants to redeem rewards
                    playersState.put(username, PlayerState.WAIT_BOOLEAN);
                    piratesDefeated = true;
                }
                else if (userCannonPower >= piratesFirePower) { // Tie or pirates already defeated
                    playersState.put(username, PlayerState.DONE);
                }
                else { // Player is defeated
                    defeatedPlayers.add(board.getPlayerEntityByUsername(username));
                    playersState.put(username, PlayerState.DONE);
                }
            }
            case WAIT_ROLL_DICES -> {
                for (PlayerData player : defeatedPlayers) {
                    PlayerState newState = cannonFires.get(cannonIndex).hit(player.getShip(), coord);
                    playersState.put(player.getUsername(), newState);
                }
                cannonIndex++;
            }
        }

        playerIndex++;
        return autoCheckPlayers(board);

    }

    private boolean autoCheckPlayers(Board board) {
        for (; playerIndex < players.size(); playerIndex++) {
            PlayerData player = players.get(playerIndex);

            double freeCannonsPower = (player.getShip().getCannonAlien() ? 2 : 0) + player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(cannon -> !cannon.getIsDouble())
                    .mapToDouble(CannonComponent::calcPower).sum();
            double doubleCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(CannonComponent::getIsDouble)
                    .mapToDouble(CannonComponent::calcPower)
                    .boxed()
                    .sorted(Comparator.reverseOrder())
                    .limit(player.getShip().getBatteries())
                    .mapToDouble(v -> v)
                    .sum();

            if (piratesDefeated)
                playersState.put(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower > piratesFirePower) { // User wins automatically
                playersState.put(player.getUsername(), PlayerState.WAIT_BOOLEAN);
                piratesDefeated = true;
            }
            else if (freeCannonsPower == piratesFirePower && doubleCannonsPower == 0) // User draws automatically
                playersState.put(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower + doubleCannonsPower >= piratesFirePower) { // User could win
                playersState.put(player.getUsername(), PlayerState.WAIT_CANNONS);
                return false;
            }
            else { // User loses automatically
                defeatedPlayers.add(player);
                playersState.put(player.getUsername(), PlayerState.DONE);
            }
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : players)
            if (playersState.get(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone && defeatedPlayers.isEmpty()) {
            endCard(board);
            return true;
        }
        else if (hasDone) {
            if (cannonIndex >= cannonFires.size()) {
                endCard(board);
                return true;
            }
            else {
                for (PlayerData player : defeatedPlayers)
                    playersState.put(player.getUsername(), PlayerState.WAIT);
                playersState.put(defeatedPlayers.getFirst().getUsername(), PlayerState.WAIT_ROLL_DICES);
            }
        }
        return false;
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Integer value, String username, Board board) {
        if (commandType == PlayerState.WAIT_ROLL_DICES)
            this.coord = value;
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Double value, String username, Board board) {
        if (commandType == PlayerState.WAIT_CANNONS) {
            userCannonPower = value;
        }
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Boolean value, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_BOOLEAN && value) {
            board.movePlayer(player, -1*days);
            player.setCredits(credits + player.getCredits());
        }
        else if (commandType == PlayerState.WAIT_SHIELD && !value) {
            Optional<Component> target = cannonFires.get(cannonIndex).getTarget(player.getShip(), coord);
            target.ifPresent(component -> component.destroyComponent(player.getShip()));
        }
    }

}
