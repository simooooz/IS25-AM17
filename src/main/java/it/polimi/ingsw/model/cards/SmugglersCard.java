package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.components.SpecialCargoHoldsComponent;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.properties.DirectionType;

import java.util.*;
import java.util.stream.Collectors;

public class SmugglersCard extends Card {
    private final int firePower;                // firePower of the smugglers' ship
    private final int penalty;                  // number of goods to lose if player doesn't win the battle with smugglers
    private final List<ColorType> reward;       // reward
    private final int days;                     // number of days to lose if player win the battle and decides to pick the reward (goods)

    private boolean defeated;                   // smugglers defeated?
    private int playerIndex;
    private List<PlayerData> defeatedPlayers;

    // todo: maybe not appropriate
    private double power;

    public SmugglersCard(
            int level,
            boolean isLearner,

            int firePower,
            int penalty,
            List<ColorType> reward,
            int days
    ) {
        super(level, isLearner);
        this.firePower = firePower;
        this.penalty = penalty;
        this.reward = reward;
        this.days = days;
    }

    public void startCard(Board board) {
        this.defeated = false;
        this.playerIndex = 0;
        this.defeatedPlayers = new ArrayList<>();

        board.getPlayers().forEach(player ->
            playersState.put(player.getKey().getUsername(), CardState.WAIT)
        );
        autoCheckPlayers(board);
    }

    public void changeState(Board board, String username) {
        CardState state = playersState.get(username);

        switch (state) {
            case WAIT_BOOLEAN -> playersState.put(username, CardState.DONE);
            case WAIT_CANNON -> {
                if (power > firePower && !defeated) {       // ask if user wants to redeem rewards
                    playersState.put(username, CardState.WAIT_BOOLEAN);
                    this.defeated = true;
                }
                else if (power >= firePower)                // tie or smugglers already defeated
                    playersState.put(username, CardState.DONE);
                else {                                      // player is defeated
                    defeatedPlayers.add(board.getPlayerEntityByUsername(username));
                    playersState.put(username, CardState.DONE);
                }
            }
            case WAIT_GOODS -> {
                defeatedPlayers.remove(board.getPlayerEntityByUsername(username));
                playersState.put(username, CardState.DONE);
            }
        }

        playerIndex++;
        autoCheckPlayers(board);
    }

    private void autoCheckPlayers(Board board) {
        for (; playerIndex < board.getPlayers().size(); playerIndex++) {
            PlayerData player = board.getPlayersByPos().get(playerIndex);

            double singleEnginesPower = (player.getShip().getCannonAlien() ? 2 : 0) + player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(cannon -> !cannon.getIsDouble())
                    .mapToDouble(CannonComponent::calcPower)
                    .sum();
            double doubleEnginesPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(CannonComponent::getIsDouble)
                    .mapToDouble(CannonComponent::calcPower)
                    .boxed()
                    .sorted(Comparator.reverseOrder())
                    .limit(player.getShip().getBatteries())
                    .mapToDouble(Double::doubleValue)
                    .sum();

            if (defeated)
                playersState.put(player.getUsername(), CardState.DONE);
            else if (singleEnginesPower > firePower) {      // user win automatically
                playersState.put(player.getUsername(), CardState.WAIT_BOOLEAN);
                defeated = true;
            }
            else if (singleEnginesPower == firePower && doubleEnginesPower == 0)        // user draws automatically
                playersState.put(player.getUsername(), CardState.DONE);
            else if (singleEnginesPower + doubleEnginesPower >= firePower) {            // user could win
                playersState.put(player.getUsername(), CardState.WAIT_CANNON);
                return;
            }
            else {      // user loses automatically
                defeatedPlayers.add(player);
                playersState.put(player.getUsername(), CardState.DONE);
            }

            boolean hasDone = true;
            for (PlayerData p : board.getPlayersByPos())
                if (playersState.get(p.getUsername()) != CardState.DONE)
                    hasDone = false;

            if (hasDone && defeatedPlayers.isEmpty())
                endCard();
            else if (hasDone) {
                defeatedPlayers.forEach(p -> {
                    playersState.put(p.getUsername(), CardState.WAIT_GOODS);
                });
            }
        }
    }

    // handle cannons
    public void doCommandEffect(CardState commandType, Double power, String username, Board board) {
        if (commandType == CardState.WAIT_CANNON) {
            this.power = power;
        }
    }

    // handle boolean
    public void doCommandEffect(CardState commandType, Boolean value, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == CardState.WAIT_BOOLEAN && value) {
            board.movePlayer(player, -1*this.days);
            playersState.put(username, CardState.WAIT_GOODS);
        } else if (!value) {
            playersState.put(username, CardState.DONE);
        }
    }

    // todo: goods?

    public void endCard() {}

}
