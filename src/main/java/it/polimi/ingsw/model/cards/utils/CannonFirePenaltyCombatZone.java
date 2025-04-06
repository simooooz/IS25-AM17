package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;
import java.util.Optional;

public class CannonFirePenaltyCombatZone extends PenaltyCombatZone {

    private final List<CannonFire> cannonFires;
    private int coord;
    private int cannonIndex;

    public CannonFirePenaltyCombatZone(List<CannonFire> cannonFires) {
        this.cannonFires = cannonFires;
    }

    @Override
    public PlayerState resolve(ModelFacade model, Board board, PlayerData player) {
        PlayerState actState = model.getPlayerState(player.getUsername());

        switch (actState) {
            case DONE -> actState = PlayerState.WAIT_ROLL_DICES;
            case WAIT_SHIELD -> {
                cannonIndex++;
                if (cannonIndex < cannonFires.size())
                    actState = PlayerState.WAIT_ROLL_DICES;
                else
                    actState = PlayerState.DONE;
            }
            case WAIT_ROLL_DICES -> {
                actState = cannonFires.get(cannonIndex).hit(player.getShip(), coord);
                if (actState == PlayerState.DONE && cannonIndex < cannonFires.size() - 1) { // Cannot go in done if it's not really finished because is a sub-state.
                    actState = PlayerState.WAIT_ROLL_DICES;
                    cannonIndex++;
                }
            }
        }

        return actState;
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Integer value) {
        if (commandType == PlayerState.WAIT_ROLL_DICES)
            this.coord = value;
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Boolean value, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_SHIELD && !value) {
            Optional<Component> target = cannonFires.get(cannonIndex).getTarget(player.getShip(), coord);
            target.ifPresent(component -> component.destroyComponent(player.getShip()));
        }
    }

}
