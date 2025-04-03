package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.*;

public class SmugglersCard extends Card {

    private final int smugglersFirePower;
    private final int penalty;
    private final Map<ColorType, Integer> reward;
    private final int days;

    private boolean defeated;
    private boolean redeem;
    private int playerIndex;
    private List<PlayerData> defeatedPlayers;
    private double power;

    public SmugglersCard(int level, boolean isLearner, int smugglersFirePower, int penalty, Map<ColorType, Integer> reward, int days) {
        super(level, isLearner);
        this.smugglersFirePower = smugglersFirePower;
        this.penalty = penalty;
        this.reward = reward;
        this.days = days;
    }

    @Override
    public boolean startCard(Board board) {
        this.defeated = false;
        this.redeem = false;
        this.playerIndex = 0;
        this.defeatedPlayers = new ArrayList<>();

        board.getPlayers().forEach(player ->
                playersState.put(player.getKey().getUsername(), PlayerState.WAIT)
        );
        return autoCheckPlayers(board);
    }

    @Override
    protected boolean changeState(Board board, String username) {
        PlayerState state = playersState.get(username);

        switch (state) {
            case WAIT_BOOLEAN -> {
                if (redeem) {playersState.put(username, PlayerState.WAIT_GOODS);}
                else {playersState.put(username, PlayerState.DONE);}
            }
            case WAIT_CANNONS -> {
                if (power > smugglersFirePower && !defeated) {       // ask if user wants to redeem rewards
                    playersState.put(username, PlayerState.WAIT_BOOLEAN);
                    this.defeated = true;
                }
                else if (power >= smugglersFirePower)                // tie or smugglers already defeated
                    playersState.put(username, PlayerState.DONE);
                else {                                      // player is defeated
                    defeatedPlayers.add(board.getPlayerEntityByUsername(username));
                    playersState.put(username, PlayerState.WAIT_REMOVE_GOODS);
                }
            }
            case WAIT_GOODS -> playersState.put(username, PlayerState.DONE);

            case WAIT_REMOVE_GOODS -> {
                defeatedPlayers.remove(board.getPlayerEntityByUsername(username));
                playersState.put(username, PlayerState.DONE);
            }
        }

        playerIndex++;
        return autoCheckPlayers(board);
    }

    private boolean autoCheckPlayers(Board board) {
        for (; playerIndex < board.getPlayers().size(); playerIndex++) {
            PlayerData player = board.getPlayersByPos().get(playerIndex);

            double freeCannonsPower = (player.getShip().getCannonAlien() ? 2 : 0) + player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(cannon -> !cannon.getIsDouble())
                    .mapToDouble(CannonComponent::calcPower)
                    .sum();
            double doubleCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(CannonComponent::getIsDouble)
                    .mapToDouble(CannonComponent::calcPower)
                    .boxed()
                    .sorted(Comparator.reverseOrder())
                    .limit(player.getShip().getBatteries())
                    .mapToDouble(Double::doubleValue)
                    .sum();

            if (defeated)
                playersState.put(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower > smugglersFirePower) {      // user win automatically
                playersState.put(player.getUsername(), PlayerState.WAIT_BOOLEAN);
                defeated = true;
            }
            else if (freeCannonsPower == smugglersFirePower && doubleCannonsPower == 0)        // user draws automatically
                playersState.put(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower + doubleCannonsPower >= smugglersFirePower) {            // user could win
                playersState.put(player.getUsername(), PlayerState.WAIT_CANNONS);
                return false;
            }
            else {      // user loses automatically
                defeatedPlayers.add(player);
                playersState.put(player.getUsername(), PlayerState.WAIT_REMOVE_GOODS);
            }

            boolean hasDone = true;
            for (PlayerData p : board.getPlayersByPos())
                if (playersState.get(p.getUsername()) != PlayerState.DONE)
                    hasDone = false;

            if (hasDone && defeatedPlayers.isEmpty()) {
                endCard(board);
                return true;
            }
        }
        return false;
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Double power, String username, Board board) {
        if (commandType == PlayerState.WAIT_CANNONS) {
            this.power = power;
        }
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Boolean value, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_BOOLEAN && value) {
            board.movePlayer(player, -1*this.days);
            redeem = true;
        }
        else if (!value)
            redeem = false;
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, int number, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        super.doSpecificCheck(commandType, penalty, deltaGood, batteries, username, board);
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, Map<ColorType, Integer> r, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        super.doSpecificCheck(commandType, this.reward, deltaGood, batteries, username, board);
    }

}
