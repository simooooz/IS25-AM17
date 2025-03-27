package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.properties.DirectionType;

import java.util.*;

public class SlaversCard extends Card {

    private final int crew;
    private final int credits;
    private final int days;
    private final int slaversFirePower;

    private List<PlayerData> players;
    private List<PlayerData> defeatedPlayers;
    private boolean slaversDefeated;
    private double userCannonPower;
    private int playerIndex;

    public SlaversCard(int level, boolean isLearner, int crew, int credits, int days, int slaversFirePower) {
        super(level, isLearner);
        this.crew = crew;
        this.credits = credits;
        this.days = days;
        this.slaversFirePower = slaversFirePower;
    }

    @Override
    public boolean startCard(Board board) {
        this.playerIndex = 0;
        this.defeatedPlayers = new ArrayList<>();
        this.players = board.getPlayersByPos();

        for (PlayerData player : players)
            playersState.put(player.getUsername(), PlayerState.WAIT);
        return autoCheckPlayers(board);
    }

    @Override
    protected boolean changeState(Board board, String username) {

        PlayerState actState = playersState.get(username);

        switch (actState) {
            case WAIT_BOOLEAN -> playersState.put(username, PlayerState.DONE);
            case WAIT_CANNONS -> {
                if (userCannonPower > slaversFirePower && !slaversDefeated) { // Ask if user wants to redeem rewards
                    playersState.put(username, PlayerState.WAIT_BOOLEAN);
                    slaversDefeated = true;
                }
                else if (userCannonPower >= slaversFirePower) { // Tie or slavers already defeated
                    playersState.put(username, PlayerState.DONE);
                }
                else { // Player is defeated
                    defeatedPlayers.add(board.getPlayerEntityByUsername(username));
                    playersState.put(username, PlayerState.DONE);
                }
            }
            case WAIT_REMOVE_CREW -> {
                defeatedPlayers.remove(board.getPlayerEntityByUsername(username));
                playersState.put(username, PlayerState.DONE);
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
                    .mapToDouble(cannon -> cannon.getDirection() == DirectionType.NORTH ? 2 : 1).sum();

            if (slaversDefeated)
                playersState.put(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower > slaversFirePower) { // User wins automatically
                playersState.put(player.getUsername(), PlayerState.WAIT_BOOLEAN);
                slaversDefeated = true;
            }
            else if (freeCannonsPower == slaversFirePower)
                playersState.put(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower + doubleCannonsPower >= slaversFirePower) { // User could win
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
            for (PlayerData player : defeatedPlayers)
                playersState.put(player.getUsername(), PlayerState.WAIT_REMOVE_CREW);
        }

        return false;
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
            board.movePlayer(player, -1 * days);
            player.setCredits(credits + player.getCredits());
        }
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, List<CabinComponent> cabins, int toRemove, String username, Board board) {
        super.doSpecificCheck(commandType, cabins, this.crew, username, board);
    }

}