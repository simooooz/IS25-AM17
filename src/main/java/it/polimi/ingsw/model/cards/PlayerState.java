package it.polimi.ingsw.model.cards;

import java.util.HashMap;
import java.util.Map;

public enum PlayerState {
    BUILD,
    LOOK_CARD_PILE {
        private final Map<String, Integer> deckIndex = new HashMap<>();

        @Override
        public Map<String, Integer> getDeckIndex() {
            return deckIndex;
        }
    },
    CHECK,
    WAIT_ALIEN,
    WAIT_SHIP_PART,
    DRAW_CARD,
    END,

    WAIT,
    WAIT_CANNONS,
    WAIT_ENGINES,
    WAIT_GOODS,
    WAIT_REMOVE_GOODS,
    WAIT_ROLL_DICES,
    WAIT_REMOVE_CREW,
    WAIT_SHIELD,
    WAIT_BOOLEAN,
    WAIT_INDEX,
    DONE;

    public Map<String, Integer> getDeckIndex() {
        throw new RuntimeException("Illegal call");
    }

}
