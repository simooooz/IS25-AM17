package it.polimi.ingsw.model.cards.utils;

import java.util.List;

public class CannonFirePenaltyCombatZone extends PenaltyCombatZone {
    private final List<CannonFire> cannonFires;

    public CannonFirePenaltyCombatZone(List<CannonFire> cannonFires) {
        this.cannonFires = cannonFires;
    }

    public List<CannonFire> getCannonFires() {
        return cannonFires;
    }
}
