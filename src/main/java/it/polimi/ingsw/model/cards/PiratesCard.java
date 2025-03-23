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
    public void resolve(Board board) throws Exception {
    }

    public void startCard(Board board) {
        this.piratesDefeated = false;
        this.playerIndex = 0;
        this.cannonIndex = 0;
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
            case WAIT_BOOLEAN, WAIT_SHIELD -> playersState.put(username, CardState.DONE);
            case WAIT_CANNON -> {
                if (userCannonPower > piratesFirePower && !piratesDefeated) { // Ask if user wants to redeem rewards
                    playersState.put(username, CardState.WAIT_BOOLEAN);
                    piratesDefeated = true;
                }
                else if (userCannonPower >= piratesFirePower) { // Tie or pirates already defeated
                    playersState.put(username, CardState.DONE);
                }
                else { // Player is defeated
                    defeatedPlayers.add(board.getPlayerEntityByUsername(username));
                    playersState.put(username, CardState.DONE);
                }
            }
            case WAIT_ROLL_DICE -> {
                for (PlayerData player : defeatedPlayers) {
                    CardState newState = cannonFires.get(cannonIndex).hit(player.getShip(), coord);
                    playersState.put(player.getUsername(), newState);
                }
                cannonIndex++;
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
                    .mapToDouble(CannonComponent::calcPower)
                    .boxed()
                    .sorted(Comparator.reverseOrder())
                    .limit(player.getShip().getBatteries())
                    .mapToDouble(v -> v)
                    .sum();

            if (piratesDefeated)
                playersState.put(player.getUsername(), CardState.DONE);
            else if (freeCannonsPower > piratesFirePower) { // User wins automatically
                playersState.put(player.getUsername(), CardState.WAIT_BOOLEAN);
                piratesDefeated = true;
            }
            else if (freeCannonsPower == piratesFirePower && doubleCannonsPower == 0) // User draws automatically
                playersState.put(player.getUsername(), CardState.DONE);
            else if (freeCannonsPower + doubleCannonsPower >= piratesFirePower) { // User could win
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
            if (cannonIndex >= cannonFires.size()) {
                endCard();
            }
            else {
                for (PlayerData player : defeatedPlayers)
                    playersState.put(player.getUsername(), CardState.WAIT);
                playersState.put(defeatedPlayers.getFirst().getUsername(), CardState.WAIT_ROLL_DICE);
            }
        }
    }

    public void endCard() {

    }

    public void doCommandEffects(CardState commandType, Integer value) {
        if (commandType == CardState.WAIT_ROLL_DICE)
            this.coord = value;
    }

    public void doCommandEffects(CardState commandType, Double value) {
        if (commandType == CardState.WAIT_CANNON) {
            userCannonPower = value;
        }
    }

    public void doCommandEffects(CardState commandType, Boolean value, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == CardState.WAIT_BOOLEAN && value) {
            board.movePlayer(player, -1*days);
            player.setCredits(credits + player.getCredits());
        }
        else if (commandType == CardState.WAIT_SHIELD && !value) {
            Optional<Component> target = cannonFires.get(cannonIndex).getTarget(player.getShip(), coord);
            if (target.isPresent())
                target.get().destroyComponent(player.getShip());
        }
    }

}
