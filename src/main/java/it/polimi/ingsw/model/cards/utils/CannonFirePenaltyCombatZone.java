package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.cards.CardState;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;
import java.util.Optional;

public class CannonFirePenaltyCombatZone extends PenaltyCombatZone {

    private final List<CannonFire> cannonFires;
    private CardState actState;
    private int coord;
    private int cannonIndex;

    public CannonFirePenaltyCombatZone(List<CannonFire> cannonFires) {
        this.cannonFires = cannonFires;
    }

    @Override
    public CardState resolve(Board board, PlayerData player) throws Exception {
        switch (actState) {
            case WAIT_SHIELD -> {
                if (cannonIndex < cannonFires.size())
                    actState = CardState.WAIT_ROLL_DICE;
                else
                    actState = CardState.DONE;
            }
            case WAIT_ROLL_DICE -> {
                this.actState = cannonFires.get(cannonIndex).hit(player.getShip(), coord);
                cannonIndex++;
                if (actState == CardState.DONE && cannonIndex < cannonFires.size()) // Cannot go in done if it's not really finished because is a sub-state.
                    actState = CardState.WAIT_ROLL_DICE;
            }
        }

        return actState;
    }

    @Override
    public void doCommandEffects(CardState commandType, Integer value) {
        if (commandType == CardState.WAIT_ROLL_DICE)
            this.coord = value;
    }

    @Override
    public void doCommandEffects(CardState commandType, Boolean value, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == CardState.WAIT_SHIELD && !value) {
            Optional<Component> target = cannonFires.get(cannonIndex).getTarget(player.getShip(), coord);
            if (target.isPresent())
                target.get().destroyComponent(player.getShip());
        }
    }

}
