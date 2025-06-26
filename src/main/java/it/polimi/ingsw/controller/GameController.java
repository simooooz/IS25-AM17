package it.polimi.ingsw.controller;

import it.polimi.ingsw.common.dto.ModelDTO;
import it.polimi.ingsw.common.dto.GameStateDTOFactory;
import it.polimi.ingsw.common.model.events.game.ErrorEvent;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.ModelFacadeAdvancedMode;
import it.polimi.ingsw.model.ModelFacadeLearnerMode;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.common.model.events.Event;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * It creates via {@link ModelFacade} a game instance
 * and coordinates the interaction between view and model
 */
public class GameController {

    /**
     * {@link ModelFacade} ref: exposed methods for interacting with the model
     */
    private final ModelFacade model;

    /**
     * Constructor
     *
     * @param usernames players' usernames in the game
     */
    public GameController(List<String> usernames, boolean learnerMode) {
        model = learnerMode ? new ModelFacadeLearnerMode(usernames) : new ModelFacadeAdvancedMode(usernames);
    }

    public synchronized void startMatch() {
        model.startMatch();
    }

    public synchronized List<Event> pickComponent(String username, int componentId) {
        EventContext.clear();

        try {
            model.pickComponent(username, componentId);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> releaseComponent(String username, int componentId) {
        EventContext.clear();

        try {
            model.releaseComponent(username, componentId);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> reserveComponent(String username, int componentId) {
        EventContext.clear();

        try {
            model.reserveComponent(username, componentId);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> insertComponent(String username, int componentId, int row, int col, int rotations, boolean weld) {
        EventContext.clear();

        try {
            model.insertComponent(username, componentId, row, col, rotations, weld);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> moveComponent(String username, int componentId, int row, int col, int rotations) {
        EventContext.clear();

        try {
            model.moveComponent(username, componentId, row, col, rotations);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> rotateComponent(String username, int componentId, int num) {
        EventContext.clear();

        try {
            model.rotateComponent(username, componentId, num);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }
        return EventContext.getAndClear();
    }

    public synchronized List<Event> lookCardPile(String username, int deckIndex) {
        EventContext.clear();

        try {
            model.lookCardPile(username, deckIndex);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }
        return EventContext.getAndClear();
    }

    public synchronized List<Event> releaseCardPile(String username) {
        EventContext.clear();

        try {
            model.releaseCardPile(username);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> moveHourglass(String username, Consumer<List<Event>> callback) {
        EventContext.clear();

        try {
            model.moveHourglass(username, callback);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> setReady(String username) {
        EventContext.clear();

        try {
            model.setReady(username);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> checkShip(String username, List<Integer> toRemove) {
        EventContext.clear();

        try {
            model.checkShip(username, toRemove);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> chooseAlien(String username, Map<Integer, AlienType> aliensIds) {
        EventContext.clear();

        try {
            model.chooseAlien(username, aliensIds);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> chooseShipPart(String username, int partIndex) {
        EventContext.clear();

        try {
            model.chooseShipPart(username, partIndex);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> drawCard(String username) {
        EventContext.clear();

        try {
            model.drawCard(username);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> activateCannons(String username, List<Integer> batteriesIds, List<Integer> cannonComponentsIds) {
        EventContext.clear();

        try {
            model.activateCannons(username, batteriesIds, cannonComponentsIds);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> activateEngines(String username, List<Integer> batteriesIds, List<Integer> engineComponentsIds) {
        EventContext.clear();

        try {
            model.activateEngines(username, batteriesIds, engineComponentsIds);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> activateShield(String username, Integer batteryId) {
        EventContext.clear();

        try {
            model.activateShield(username, batteryId);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> updateGoods(String username, Map<Integer, List<ColorType>> cargoHoldsIds, List<Integer> batteriesIds) {
        EventContext.clear();

        try {
            model.updateGoods(username, cargoHoldsIds, batteriesIds);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> removeCrew(String username, List<Integer> cabinsIds) {
        EventContext.clear();

        try {
            model.removeCrew(username, cabinsIds);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> rollDices(String username) {
        EventContext.clear();

        try {
            model.rollDices(username);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> getBoolean(String username, boolean value) {
        EventContext.clear();

        try {
            model.getBoolean(username, value);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> getIndex(String username, Integer value) {
        EventContext.clear();

        try {
            model.getIndex(username, value);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized List<Event> endFlight(String username) {
        EventContext.clear();

        try {
            model.endFlight(username);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }

        return EventContext.getAndClear();
    }

    public synchronized void leaveGame(String username) {
        try {
            model.leaveGame(username);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }
    }

    public synchronized void rejoinGame(String username) {
        try {
            model.rejoinGame(username);
        } catch (RuntimeException e) {
            EventContext.emit(new ErrorEvent(e.getMessage()));
        }
    }

    public synchronized ModelDTO toDTO() {
        return GameStateDTOFactory.createFromModel(model);
    }

    // TEST only
    public ModelFacade getModel() {
        return model;
    }

    public List<Event> startTest(int testNumber) {
        EventContext.clear();
        List<String> usernames = model.getBoard().getStartingDeck().stream().map(PlayerData::getUsername).toList();
        switch (testNumber) {
            case 1 -> {
                model.pickComponent(usernames.getFirst(), 102);
                model.insertComponent(usernames.getFirst(), 102, 0, 2, 0, true);

                model.pickComponent(usernames.getFirst(), 16);
                model.rotateComponent(usernames.getFirst(), 16, 3);
                model.insertComponent(usernames.getFirst(), 16, 0, 4, 0, true);

                model.pickComponent(usernames.getFirst(), 113);
                model.rotateComponent(usernames.getFirst(), 113, 3);
                model.insertComponent(usernames.getFirst(), 113, 1, 1, 0, true);

                model.pickComponent(usernames.getFirst(), 60);
                model.rotateComponent(usernames.getFirst(), 60, 3);
                model.insertComponent(usernames.getFirst(), 60, 1, 2, 0, true);

                model.pickComponent(usernames.getFirst(), 131);
                model.insertComponent(usernames.getFirst(), 131, 1, 3, 0, true);

                model.pickComponent(usernames.getFirst(), 116);
                model.rotateComponent(usernames.getFirst(), 116, 1);
                model.insertComponent(usernames.getFirst(), 116, 1, 4, 0, true);

                model.pickComponent(usernames.getFirst(), 134);
                model.rotateComponent(usernames.getFirst(), 134, 3);
                model.insertComponent(usernames.getFirst(), 134, 2, 0, 0, true);

                model.pickComponent(usernames.getFirst(), 55);
                model.insertComponent(usernames.getFirst(), 55, 2, 1, 0, true);

                model.pickComponent(usernames.getFirst(), 38);
                model.rotateComponent(usernames.getFirst(), 38, 1);
                model.insertComponent(usernames.getFirst(), 38, 2, 2, 0, true);

                model.pickComponent(usernames.getFirst(), 63);
                model.insertComponent(usernames.getFirst(), 63, 2, 4, 0, true);

                model.pickComponent(usernames.getFirst(), 24);
                model.insertComponent(usernames.getFirst(), 24, 2, 5, 0, true);

                model.pickComponent(usernames.getFirst(), 25);
                model.insertComponent(usernames.getFirst(), 25, 2, 6, 0, true);

                model.pickComponent(usernames.getFirst(), 9);
                model.rotateComponent(usernames.getFirst(), 9, 1);
                model.insertComponent(usernames.getFirst(), 9, 3, 0, 0, true);

                model.pickComponent(usernames.getFirst(), 152);
                model.rotateComponent(usernames.getFirst(), 152, 2);
                model.insertComponent(usernames.getFirst(), 152, 3, 1, 0, true);

                model.pickComponent(usernames.getFirst(), 5);
                model.insertComponent(usernames.getFirst(), 5, 3, 2, 0, true);

                model.pickComponent(usernames.getFirst(), 92);
                model.insertComponent(usernames.getFirst(), 92, 3, 3, 0, true);

                model.pickComponent(usernames.getFirst(), 146);
                model.rotateComponent(usernames.getFirst(), 146, 1);
                model.insertComponent(usernames.getFirst(), 146, 3, 4, 0, true);

                model.pickComponent(usernames.getFirst(), 62);
                model.rotateComponent(usernames.getFirst(), 62, 1);
                model.insertComponent(usernames.getFirst(), 62, 3, 5, 0, true);

                model.pickComponent(usernames.getFirst(), 77);
                model.insertComponent(usernames.getFirst(), 77, 4, 0, 0, true);

                model.pickComponent(usernames.getFirst(), 46);
                model.rotateComponent(usernames.getFirst(), 46, 2);
                model.insertComponent(usernames.getFirst(), 46, 4, 1, 0, true);

                model.pickComponent(usernames.getFirst(), 137);
                model.insertComponent(usernames.getFirst(), 137, 4, 2, 0, true);

                model.pickComponent(usernames.getFirst(), 104);
                model.rotateComponent(usernames.getFirst(), 104, 3);
                model.insertComponent(usernames.getFirst(), 104, 4, 4, 0, true);

                model.pickComponent(usernames.getFirst(), 36);
                model.rotateComponent(usernames.getFirst(), 36, 2);
                model.insertComponent(usernames.getFirst(), 36, 4, 5, 0, true);

                model.pickComponent(usernames.getFirst(), 100);
                model.rotateComponent(usernames.getFirst(), 100, 1);
                model.insertComponent(usernames.getFirst(), 100, 4, 6, 0, true);


                model.pickComponent(usernames.get(1), 127);
                model.rotateComponent(usernames.get(1), 127, 1);
                model.insertComponent(usernames.get(1), 127, 0, 2, 0, true);

                model.pickComponent(usernames.get(1), 108);
                model.insertComponent(usernames.get(1), 108, 0, 4, 0, true);

                model.pickComponent(usernames.get(1), 143);
                model.rotateComponent(usernames.get(1), 143, 3);
                model.insertComponent(usernames.get(1), 143, 1, 1, 0, true);

                model.pickComponent(usernames.get(1), 64);
                model.rotateComponent(usernames.get(1), 64, 1);
                model.insertComponent(usernames.get(1), 64, 1, 2, 0, true);

                model.pickComponent(usernames.get(1), 3);
                model.rotateComponent(usernames.get(1), 3, 2);
                model.insertComponent(usernames.get(1), 3, 1, 4, 0, true);

                model.pickComponent(usernames.get(1), 28);
                model.rotateComponent(usernames.get(1), 28, 3);
                model.insertComponent(usernames.get(1), 28, 1, 5, 0, true);

                model.pickComponent(usernames.get(1), 149);
                model.rotateComponent(usernames.get(1), 149, 3);
                model.insertComponent(usernames.get(1), 149, 2, 0, 0, true);

                model.pickComponent(usernames.get(1), 51);
                model.rotateComponent(usernames.get(1), 51, 1);
                model.insertComponent(usernames.get(1), 51, 2, 1, 0, true);

                model.pickComponent(usernames.get(1), 56);
                model.insertComponent(usernames.get(1), 56, 2, 2, 0, true);

                model.pickComponent(usernames.get(1), 58);
                model.rotateComponent(usernames.get(1), 58, 2);
                model.insertComponent(usernames.get(1), 58, 2, 4, 0, true);

                model.pickComponent(usernames.get(1), 150);
                model.rotateComponent(usernames.get(1), 150, 1);
                model.insertComponent(usernames.get(1), 150, 2, 5, 0, true);

                model.pickComponent(usernames.get(1), 103);
                model.insertComponent(usernames.get(1), 103, 2, 6, 0, true);

                model.pickComponent(usernames.get(1), 14);
                model.insertComponent(usernames.get(1), 14, 3, 0, 0, true);

                model.pickComponent(usernames.get(1), 79);
                model.insertComponent(usernames.get(1), 79, 3, 1, 0, true);

                model.pickComponent(usernames.get(1), 85);
                model.insertComponent(usernames.get(1), 85, 3, 3, 0, true);

                model.pickComponent(usernames.get(1), 43);
                model.rotateComponent(usernames.get(1), 43, 1);
                model.insertComponent(usernames.get(1), 43, 3, 4, 0, true);

                model.pickComponent(usernames.get(1), 53);
                model.insertComponent(usernames.get(1), 53, 3, 5, 0, true);

                model.pickComponent(usernames.get(1), 97);
                model.insertComponent(usernames.get(1), 97, 3, 6, 0, true);

                model.pickComponent(usernames.get(1), 45);
                model.rotateComponent(usernames.get(1), 45, 1);
                model.insertComponent(usernames.get(1), 45, 4, 4, 0, true);

                model.pickComponent(usernames.get(1), 67);
                model.insertComponent(usernames.get(1), 67, 4, 5, 0, true);

                model.pickComponent(usernames.get(1), 90);
                model.reserveComponent(usernames.get(1), 90);


                model.pickComponent(usernames.get(2), 118);
                model.insertComponent(usernames.get(2), 118, 0, 2, 0, true);

                model.pickComponent(usernames.get(2), 126);
                model.insertComponent(usernames.get(2), 126, 0, 4, 0, true);

                model.pickComponent(usernames.get(2), 136);
                model.rotateComponent(usernames.get(2), 136, 1);
                model.insertComponent(usernames.get(2), 136, 1, 1, 0, true);

                model.pickComponent(usernames.get(2), 44);
                model.rotateComponent(usernames.get(2), 44, 3);
                model.insertComponent(usernames.get(2), 44, 1, 2, 0, true);

                model.pickComponent(usernames.get(2), 61);
                model.insertComponent(usernames.get(2), 61, 1, 3, 0, true);

                model.pickComponent(usernames.get(2), 1);
                model.rotateComponent(usernames.get(2), 1, 3);
                model.insertComponent(usernames.get(2), 1, 1, 4, 0, true);

                model.pickComponent(usernames.get(2), 133);
                model.insertComponent(usernames.get(2), 133, 1, 5, 0, true);

                model.pickComponent(usernames.get(2), 114);
                model.insertComponent(usernames.get(2), 114, 2, 0, 0, true);

                model.pickComponent(usernames.get(2), 37);
                model.rotateComponent(usernames.get(2), 37, 1);
                model.insertComponent(usernames.get(2), 37, 2, 1, 0, true);

                model.pickComponent(usernames.get(2), 148);
                model.rotateComponent(usernames.get(2), 148, 2);
                model.insertComponent(usernames.get(2), 148, 2, 2, 0, true);

                model.pickComponent(usernames.get(2), 142);
                model.rotateComponent(usernames.get(2), 142, 2);
                model.insertComponent(usernames.get(2), 142, 2, 4, 0, true);

                model.pickComponent(usernames.get(2), 39);
                model.rotateComponent(usernames.get(2), 39, 3);
                model.insertComponent(usernames.get(2), 39, 2, 5, 0, true);

                model.pickComponent(usernames.get(2), 12);
                model.insertComponent(usernames.get(2), 12, 3, 0, 0, true);

                model.pickComponent(usernames.get(2), 41);
                model.insertComponent(usernames.get(2), 41, 3, 1, 0, true);

                model.pickComponent(usernames.get(2), 18);
                model.rotateComponent(usernames.get(2), 18, 2);
                model.insertComponent(usernames.get(2), 18, 3, 2, 0, true);

                model.pickComponent(usernames.get(2), 95);
                model.insertComponent(usernames.get(2), 95, 3, 3, 0, true);

                model.pickComponent(usernames.get(2), 151);
                model.rotateComponent(usernames.get(2), 151, 3);
                model.insertComponent(usernames.get(2), 151, 3, 4, 0, true);

                model.pickComponent(usernames.get(2), 30);
                model.insertComponent(usernames.get(2), 30, 3, 5, 0, true);

                model.pickComponent(usernames.get(2), 75);
                model.insertComponent(usernames.get(2), 75, 4, 0, 0, true);

                model.pickComponent(usernames.get(2), 94);
                model.insertComponent(usernames.get(2), 94, 4, 1, 0, true);

                model.pickComponent(usernames.get(2), 81);
                model.insertComponent(usernames.get(2), 81, 4, 2, 0, true);

                model.pickComponent(usernames.get(2), 10);
                model.rotateComponent(usernames.get(2), 10, 1);
                model.insertComponent(usernames.get(2), 10, 4, 4, 0, true);

                model.pickComponent(usernames.get(2), 96);
                model.insertComponent(usernames.get(2), 96, 4, 5, 0, true);

                model.pickComponent(usernames.get(2), 87);
                model.insertComponent(usernames.get(2), 87, 4, 6, 0, true);
            }
            case 2 -> {}
        }
        return EventContext.getAndClear();
    }

}
