package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;
import java.util.Optional;

public class CannonFirePenaltyCombatZone extends PenaltyCombatZone {

    @JsonProperty private final List<CannonFire> cannonFires;
    @JsonProperty private List<Integer> coords;
    @JsonProperty private int cannonIndex;

    public CannonFirePenaltyCombatZone(List<CannonFire> cannonFires) {
        this.cannonFires = cannonFires;
    }

    @Override
    public PlayerState resolve(ModelFacade model, Board board, String username) {
        return PlayerState.WAIT_ROLL_DICES;
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_ROLL_DICES) {
            PlayerData player = board.getPlayerEntityByUsername(username);
            this.coords.add(value);

            PlayerState newState = cannonFires.get(cannonIndex).hit(player, coords.getLast());
            if (newState == PlayerState.DONE && cannonIndex < cannonFires.size() - 1) { // Cannot go in done if it's not really finished because is a deeper state.
                newState = PlayerState.WAIT_ROLL_DICES;
                cannonIndex++;
            }

            model.setPlayerState(username, newState);
        }
        else
            throw new RuntimeException("Command type not valid");
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_SHIELD) {
            PlayerData player = board.getPlayerEntityByUsername(username);

            if (!value) { // Component destroyed
                Optional<Component> target = cannonFires.get(cannonIndex).getTarget(player.getShip(), coords.getLast());
                target.ifPresent(component -> {
                    PlayerState newState = component.destroyComponent(player); // DONE or WAIT_SHIP_PART
                    model.setPlayerState(player.getUsername(), newState);
                });
            }

            if (model.getPlayerState(player.getUsername()) == PlayerState.WAIT_SHIP_PART) // Not finished yet, user has to choose part of ship to remove and then cannon index can be increased
                return;

            cannonIndex++;
            if (cannonIndex < cannonFires.size())
                model.setPlayerState(username, PlayerState.WAIT_ROLL_DICES);
            else
                model.setPlayerState(username, PlayerState.DONE);
        }
        else
            throw new RuntimeException("Command type not valid");
    }

    @Override
    public void doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_SHIP_PART) {
            cannonIndex++;
            if (cannonIndex < cannonFires.size())
                model.setPlayerState(username, PlayerState.WAIT_ROLL_DICES);
            else
                model.setPlayerState(username, PlayerState.DONE);
        }
        else
            throw new RuntimeException("Command type not valid");
    }

}
