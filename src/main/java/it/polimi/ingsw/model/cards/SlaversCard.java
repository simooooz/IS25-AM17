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

    public SlaversCard(int level, boolean isLearner, int crew, int credits, int days, int firePower) {
        super(level, isLearner);
        this.crew = crew;
        this.credits = credits;
        this.days = days;
        this.slaversFirePower = slaversFirePower;
    }

    public void startCard(Board board) {
        this.playerIndex = 0;
        this.defeatedPlayers = new ArrayList<>();
        this.players = board.getPlayersByPos();

        for (PlayerData player : players) {
            playersState.put(player.getUsername(), CardState.WAIT);
        }
        autoCheckPlayers();
    }

    public void changeState(Board board, String username) throws Exception {

        CardState actState = playersState.get(username);

        switch (actState) {
            case WAIT_BOOLEAN, WAIT_CREW -> playersState.put(username, CardState.DONE);
            case WAIT_CANNON -> {
                if (userCannonPower > slaversFirePower && !slaversDefeated) { // Ask if user wants to redeem rewards
                    playersState.put(username, CardState.WAIT_BOOLEAN);
                    slaversDefeated = true;
                }
                else if (userCannonPower >= slaversFirePower) { // Tie or slavers already defeated
                    playersState.put(username, CardState.DONE);
                }
                else { // Player is defeated
                    defeatedPlayers.add(board.getPlayerEntityByUsername(username));
                    playersState.put(username, CardState.DONE);
                }
            }
        }

        playerIndex++;
        autoCheckPlayers();

    }

    private void autoCheckPlayers() {
        for (; playerIndex < players.size(); playerIndex++) {
            PlayerData player = players.get(playerIndex);

            double freeCannonsPower = (player.getShip().getCannonAlien() ? 2 : 0) + player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(cannon -> !cannon.getIsDouble())
                    .mapToDouble(CannonComponent::calcPower).sum();
            double doubleCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(CannonComponent::getIsDouble)
                    .mapToDouble(cannon -> cannon.getDirection() == DirectionType.NORTH ? 2 : 1).sum();

            if (slaversDefeated)
                playersState.put(player.getUsername(), CardState.DONE);
            else if (freeCannonsPower > slaversFirePower) { // User wins automatically
                playersState.put(player.getUsername(), CardState.WAIT_BOOLEAN);
                slaversDefeated = true;
            }
            else if (freeCannonsPower == slaversFirePower)
                playersState.put(player.getUsername(), CardState.DONE);
            else if (freeCannonsPower + doubleCannonsPower >= slaversFirePower) { // User could win
                playersState.put(player.getUsername(), CardState.WAIT_CANNON);
                return;
            }
            else { // User loses automatically
                defeatedPlayers.add(player);
                playersState.put(player.getUsername(), CardState.DONE);
            }
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : players)
            if (playersState.get(player.getUsername()) != CardState.DONE)
                hasDone = false;

        if (hasDone && defeatedPlayers.isEmpty()) {
            endCard();
        }
        else if (hasDone) {
            for (PlayerData player : defeatedPlayers)
                playersState.put(player.getUsername(), CardState.WAIT_CREW);
        }
    }

    public void endCard() {

    }

    public void doCommandEffects(CardState commandType, Double value) {
        if (commandType == CardState.WAIT_CANNON) {
            userCannonPower = value;
        }
    }

    public void doCommandEffects(CardState commandType, Boolean value, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == CardState.WAIT_BOOLEAN && value) {
            board.movePlayer(player, -1 * days);
            player.setCredits(credits + player.getCredits());
        }
    }

}