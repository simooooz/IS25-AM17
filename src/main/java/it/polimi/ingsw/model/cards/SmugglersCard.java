package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
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

    private List<PlayerData> players;
    private boolean defeated;
    private int playerIndex;

    public SmugglersCard(int level, boolean isLearner, int smugglersFirePower, int penalty, Map<ColorType, Integer> reward, int days) {
        super(level, isLearner);
        this.smugglersFirePower = smugglersFirePower;
        this.penalty = penalty;
        this.reward = reward;
        this.days = days;
    }

    @Override
    public boolean startCard(ModelFacade model, Board board) {
        this.defeated = false;
        this.playerIndex = 0;
        this.players = new ArrayList<>(board.getPlayersByPos());

        board.getPlayers().forEach(player ->
                model.setPlayerState(player.getKey().getUsername(), PlayerState.WAIT)
        );
        return autoCheckPlayers(model, board);
    }

    private boolean autoCheckPlayers(ModelFacade model, Board board) {
        for (; playerIndex < players.size(); playerIndex++) {
            PlayerData player = players.get(playerIndex);

            double freeCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(cannon -> !cannon.getIsDouble())
                    .mapToDouble(CannonComponent::calcPower)
                    .sum();
            if (freeCannonsPower > 0 && player.getShip().getCannonAlien())
                freeCannonsPower += 2;

            double doubleCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(CannonComponent::getIsDouble)
                    .mapToDouble(CannonComponent::calcPower)
                    .boxed()
                    .sorted(Comparator.reverseOrder())
                    .limit(player.getShip().getBatteries())
                    .mapToDouble(Double::doubleValue)
                    .sum();

            if (defeated)
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower > smugglersFirePower) {      // user win automatically
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_BOOLEAN);
                defeated = true;
                return false;
            }
            else if (freeCannonsPower == smugglersFirePower && doubleCannonsPower == 0)        // user draws automatically
                model.setPlayerState(player.getUsername(), PlayerState.DONE);
            else if (freeCannonsPower + doubleCannonsPower >= smugglersFirePower) {            // user could win
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_CANNONS);
                return false;
            }
            else {      // user loses automatically
                model.setPlayerState(player.getUsername(), PlayerState.WAIT_REMOVE_GOODS);
                return false;
            }
        }

        boolean hasDone = true;
        for (PlayerData p : players)
            if (model.getPlayerState(p.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone) {
            if (!model.isLearnerMode())
                endCard(board);
            return true;
        }
        return false;
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Double power, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_CANNONS) {
            if (power > smugglersFirePower && !defeated) { // ask if user wants to redeem rewards
                model.setPlayerState(username, PlayerState.WAIT_BOOLEAN);
                this.defeated = true;
            }
            else if (power >= smugglersFirePower) { // tie or smugglers already defeated
                model.setPlayerState(username, PlayerState.DONE);
                playerIndex++;
                return autoCheckPlayers(model, board);
            }
            else // Player is defeated;
                model.setPlayerState(username, PlayerState.WAIT_REMOVE_GOODS);

            return false;
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_BOOLEAN) {
            if (value) {
                PlayerData player = board.getPlayerEntityByUsername(username);
                board.movePlayer(player, -1 * this.days);
                model.setPlayerState(username, PlayerState.WAIT_GOODS);
                return false;
            }
            else {
                model.setPlayerState(username, PlayerState.DONE);
                playerIndex++;
                return autoCheckPlayers(model, board);
            }
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_GOODS || commandType == PlayerState.WAIT_REMOVE_GOODS) {
            model.setPlayerState(username, PlayerState.DONE);
            playerIndex++;
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
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
