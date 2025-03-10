package it.polimi.ingsw.model.cards.utils;

import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.Dice;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;

public class CannonFirePenaltyCombatZone extends PenaltyCombatZone {

    private final List<CannonFire> cannonFires;

    public CannonFirePenaltyCombatZone(List<CannonFire> cannonFires) {
        this.cannonFires = cannonFires;
    }

    @Override
    public void resolve(Board board, PlayerData player) throws Exception {
        for(CannonFire cannonFire : cannonFires) {
            int coord = Dice.roll() + Dice.roll(); // View => The player rolls dices
            cannonFire.hit(player.getShip(), coord);
        }
    }

}
