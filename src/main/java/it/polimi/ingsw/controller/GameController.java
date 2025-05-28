package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.exceptions.IllegalStateException;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;
import java.util.Map;

/**
 * It creates via {@link ModelFacade} a game instance
 * and coordinates the interaction between view and model
 */
public class GameController {

    /**
     * {@link ModelFacade} ref: exposed methods for interacting with the model
     */
    private final ModelFacade model;
    private final boolean learnerMode;

    /**
     * Constructor
     *
     * @param usernames players' usernames in the game
     */
    public GameController(List<String> usernames, boolean learnerMode) {
        this.model = new ModelFacade(usernames, learnerMode);
        this.learnerMode = learnerMode;
    }

    public void startMatch() {
        model.startMatch();
    }

    public void setShuffledCardPile(List<Integer> ids) {
        model.setShuffledCardPile(ids);
    }

    public void pickComponent(String username, int componentId) {
        if (model.getPlayerState(username) == PlayerState.LOOK_CARD_PILE) model.releaseCardPile(username);
        else if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        else if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.pickComponent(username, componentId);
    }

    public void releaseComponent(String username, int componentId) {
        if (model.getPlayerState(username) == PlayerState.LOOK_CARD_PILE) model.releaseCardPile(username);
        else if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        else if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.releaseComponent(username, componentId);
    }

    public void reserveComponent(String username, int componentId) {
        if (model.getPlayerState(username) == PlayerState.LOOK_CARD_PILE) model.releaseCardPile(username);
        else if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        else if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");
        else if (learnerMode) throw new IllegalArgumentException("Match is in learner mode");

        model.reserveComponent(username, componentId);
    }

    public void insertComponent(String username, int componentId, int row, int col, int rotations, boolean weld) {
        if (model.getPlayerState(username) == PlayerState.LOOK_CARD_PILE) model.releaseCardPile(username);
        else if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        else if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.insertComponent(username, componentId, row, col, rotations, weld);
    }

    public void moveComponent(String username, int componentId, int row, int col, int rotations) {
        if (model.getPlayerState(username) == PlayerState.LOOK_CARD_PILE) model.releaseCardPile(username);
        else if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        else if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.moveComponent(username, componentId, row, col);
        model.rotateComponent(username, componentId, rotations);
    }

    public void rotateComponent(String username, int componentId, int num) {
        if (model.getPlayerState(username) == PlayerState.LOOK_CARD_PILE) model.releaseCardPile(username);
        else if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        else if (model.isPlayerReady(username)) throw new RuntimeException("Player is ready");

        model.rotateComponent(username, componentId, num);
    }

    public void lookCardPile(String username, int deckIndex) {
        if (model.getPlayerState(username) == PlayerState.LOOK_CARD_PILE) model.releaseCardPile(username);
        else if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        else if (model.isPlayerReady(username)) throw new IllegalStateException("Player is already ready");
        else if (learnerMode) throw new IllegalArgumentException("Match is in learner mode");

        model.lookCardPile(username, deckIndex);
    }

    public void moveHourglass(String username) {
        if (model.getPlayerState(username) == PlayerState.LOOK_CARD_PILE) model.releaseCardPile(username);
        else if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");
        else if (learnerMode) throw new IllegalArgumentException("Match is in learner mode");

        model.moveHourglass(username);
    }

    public void setReady(String username) {
        if (model.getPlayerState(username) == PlayerState.LOOK_CARD_PILE) model.releaseCardPile(username);
        else if (model.getPlayerState(username) != PlayerState.BUILD) throw new IllegalStateException("State is not BUILDING");

        model.setReady(username);
    }

    public void checkShip(String username, List<Integer> toRemove) {
        if (model.getPlayerState(username) != PlayerState.CHECK) throw new IllegalStateException("State is not CHECKING");
        model.checkShip(username, toRemove);
    }

    public void chooseAlien(String username, Map<Integer, AlienType> aliensIds) {
        if (model.getPlayerState(username) != PlayerState.WAIT_ALIEN) throw new IllegalStateException("State is not WAIT_ALIEN");
        model.chooseAlien(username, aliensIds);
    }

    public void chooseShipPart(String username, int partIndex) {
        if (model.getPlayerState(username) != PlayerState.WAIT_SHIP_PART) throw new IllegalStateException("State is not WAIT_SHIP_PART");
        model.chooseShipPart(username, partIndex);
    }

    public void drawCard(String username) {
        if (model.getPlayerState(username) != PlayerState.DRAW_CARD) throw new IllegalStateException("State is not DRAW_CARD");
        model.nextCard();
    }

    public void activateCannons(String username, List<Integer> batteriesIds, List<Integer> cannonComponentsIds) {
        if (model.getPlayerState(username) != PlayerState.WAIT_CANNONS) throw new IllegalStateException("State is not WAIT_CANNONS");
        model.activateCannons(username, batteriesIds, cannonComponentsIds);
    }

    public void activateEngines(String username, List<Integer> batteriesIds, List<Integer> engineComponentsIds) {
        if (model.getPlayerState(username) != PlayerState.WAIT_ENGINES) throw new IllegalStateException("State is not WAIT_ENGINES");
        model.activateEngines(username, batteriesIds, engineComponentsIds);
    }

    public void activateShield(String username, Integer batteryId) {
        if (model.getPlayerState(username) != PlayerState.WAIT_SHIELD) throw new IllegalStateException("State is not WAIT_SHIELD");
        model.activateShield(username, batteryId);
    }

    public void updateGoods(String username, Map<Integer, List<ColorType>> cargoHoldsIds, List<Integer> batteriesIds) {
        if (model.getPlayerState(username) != PlayerState.WAIT_GOODS && model.getPlayerState(username) != PlayerState.WAIT_REMOVE_GOODS) throw new IllegalStateException("State is not WAIT_GOODS or WAIT_REMOVE_GOODS");
        model.updateGoods(username, cargoHoldsIds, batteriesIds);
    }

    public void removeCrew(String username, List<Integer> cabinsIds) {
        if (model.getPlayerState(username) != PlayerState.WAIT_REMOVE_CREW) throw new IllegalStateException("State is not WAIT_REMOVE_CREW");
        model.removeCrew(username, cabinsIds);
    }

    public void rollDices(String username, Integer value) {
        if (model.getPlayerState(username) != PlayerState.WAIT_ROLL_DICES) throw new IllegalStateException("State is not WAIT_ROLL_DICES");
        model.rollDices(username, value);
    }

    public void getBoolean(String username, boolean value) {
        if (model.getPlayerState(username) != PlayerState.WAIT_BOOLEAN) throw new IllegalStateException("State is not WAIT_BOOLEAN");
        model.getBoolean(username, value);
    }

    public void getIndex(String username, Integer value) {
        if (model.getPlayerState(username) != PlayerState.WAIT_INDEX) throw new IllegalStateException("State is not WAIT_INDEX");
        model.getIndex(username, value);
    }

    public void endFlight(String username) {
        if (learnerMode) throw new IllegalArgumentException("Match is in learner mode");
        model.endFlight(username);
    }

    public void leaveGame(String username) {
        model.setPlayerState(username, PlayerState.END);
        model.endFlight(username);
    }

    public void endGame() {
        model.endGame();
    }

    // TEST only
    public PlayerState getState(String username) {
        return model.getPlayerState(username);
    }

    public ModelFacade getModel() {
        return model;
    }

    public void startTest(int testNumber) {
        List<String> usernames = model.getBoard().getStartingDeck().stream().map(PlayerData::getUsername).toList();
        switch (testNumber) {
            case 1 -> {
                this.pickComponent(usernames.getFirst(), 102);
                this.insertComponent(usernames.getFirst(), 102, 0, 2, 0, true);

                this.pickComponent(usernames.getFirst(), 16);
                this.rotateComponent(usernames.getFirst(), 16, 3);
                this.insertComponent(usernames.getFirst(), 16, 0, 4, 0, true);

                this.pickComponent(usernames.getFirst(), 113);
                this.rotateComponent(usernames.getFirst(), 113, 3);
                this.insertComponent(usernames.getFirst(), 113, 1, 1, 0, true);

                this.pickComponent(usernames.getFirst(), 60);
                this.rotateComponent(usernames.getFirst(), 60, 3);
                this.insertComponent(usernames.getFirst(), 60, 1, 2, 0, true);

                this.pickComponent(usernames.getFirst(), 131);
                this.insertComponent(usernames.getFirst(), 131, 1, 3, 0, true);

                this.pickComponent(usernames.getFirst(), 116);
                this.rotateComponent(usernames.getFirst(), 116, 1);
                this.insertComponent(usernames.getFirst(), 116, 1, 4, 0, true);

                this.pickComponent(usernames.getFirst(), 134);
                this.rotateComponent(usernames.getFirst(), 134, 3);
                this.insertComponent(usernames.getFirst(), 134, 2, 0, 0, true);

                this.pickComponent(usernames.getFirst(), 55);
                this.insertComponent(usernames.getFirst(), 55, 2, 1, 0, true);

                this.pickComponent(usernames.getFirst(), 38);
                this.rotateComponent(usernames.getFirst(), 38, 1);
                this.insertComponent(usernames.getFirst(), 38, 2, 2, 0, true);

                this.pickComponent(usernames.getFirst(), 63);
                this.insertComponent(usernames.getFirst(), 63, 2, 4, 0, true);

                this.pickComponent(usernames.getFirst(), 24);
                this.insertComponent(usernames.getFirst(), 24, 2, 5, 0, true);

                this.pickComponent(usernames.getFirst(), 25);
                this.insertComponent(usernames.getFirst(), 25, 2, 6, 0, true);

                this.pickComponent(usernames.getFirst(), 9);
                this.rotateComponent(usernames.getFirst(), 9, 1);
                this.insertComponent(usernames.getFirst(), 9, 3, 0, 0, true);

                this.pickComponent(usernames.getFirst(), 152);
                this.rotateComponent(usernames.getFirst(), 152, 2);
                this.insertComponent(usernames.getFirst(), 152, 3, 1, 0, true);

                this.pickComponent(usernames.getFirst(), 5);
                this.insertComponent(usernames.getFirst(), 5, 3, 2, 0, true);

                this.pickComponent(usernames.getFirst(), 92);
                this.insertComponent(usernames.getFirst(), 92, 3, 3, 0, true);

                this.pickComponent(usernames.getFirst(), 146);
                this.rotateComponent(usernames.getFirst(), 146, 1);
                this.insertComponent(usernames.getFirst(), 146, 3, 4, 0, true);

                this.pickComponent(usernames.getFirst(), 62);
                this.rotateComponent(usernames.getFirst(), 62, 1);
                this.insertComponent(usernames.getFirst(), 62, 3, 5, 0, true);

                this.pickComponent(usernames.getFirst(), 77);
                this.insertComponent(usernames.getFirst(), 77, 4, 0, 0, true);

                this.pickComponent(usernames.getFirst(), 46);
                this.rotateComponent(usernames.getFirst(), 46, 2);
                this.insertComponent(usernames.getFirst(), 46, 4, 1, 0, true);

                this.pickComponent(usernames.getFirst(), 137);
                this.insertComponent(usernames.getFirst(), 137, 4, 2, 0, true);

                this.pickComponent(usernames.getFirst(), 104);
                this.rotateComponent(usernames.getFirst(), 104, 3);
                this.insertComponent(usernames.getFirst(), 104, 4, 4, 0, true);

                this.pickComponent(usernames.getFirst(), 36);
                this.rotateComponent(usernames.getFirst(), 36, 2);
                this.insertComponent(usernames.getFirst(), 36, 4, 5, 0, true);

                this.pickComponent(usernames.getFirst(), 100);
                this.rotateComponent(usernames.getFirst(), 100, 1);
                this.insertComponent(usernames.getFirst(), 100, 4, 6, 0, true);


                this.pickComponent(usernames.get(1), 127);
                this.rotateComponent(usernames.get(1), 127, 1);
                this.insertComponent(usernames.get(1), 127, 0, 2, 0, true);

                this.pickComponent(usernames.get(1), 108);
                this.insertComponent(usernames.get(1), 108, 0, 4, 0, true);

                this.pickComponent(usernames.get(1), 143);
                this.rotateComponent(usernames.get(1), 143, 3);
                this.insertComponent(usernames.get(1), 143, 1, 1, 0, true);

                this.pickComponent(usernames.get(1), 64);
                this.rotateComponent(usernames.get(1), 64, 1);
                this.insertComponent(usernames.get(1), 64, 1, 2, 0, true);

                this.pickComponent(usernames.get(1), 3);
                this.rotateComponent(usernames.get(1), 3, 2);
                this.insertComponent(usernames.get(1), 3, 1, 4, 0, true);

                this.pickComponent(usernames.get(1), 28);
                this.rotateComponent(usernames.get(1), 28, 3);
                this.insertComponent(usernames.get(1), 28, 1, 5, 0, true);

                this.pickComponent(usernames.get(1), 149);
                this.rotateComponent(usernames.get(1), 149, 3);
                this.insertComponent(usernames.get(1), 149, 2, 0, 0, true);

                this.pickComponent(usernames.get(1), 51);
                this.rotateComponent(usernames.get(1), 51, 1);
                this.insertComponent(usernames.get(1), 51, 2, 1, 0, true);

                this.pickComponent(usernames.get(1), 56);
                this.insertComponent(usernames.get(1), 56, 2, 2, 0, true);

                this.pickComponent(usernames.get(1), 58);
                this.rotateComponent(usernames.get(1), 58, 2);
                this.insertComponent(usernames.get(1), 58, 2, 4, 0, true);

                this.pickComponent(usernames.get(1), 150);
                this.rotateComponent(usernames.get(1), 150, 1);
                this.insertComponent(usernames.get(1), 150, 2, 5, 0, true);

                this.pickComponent(usernames.get(1), 103);
                this.insertComponent(usernames.get(1), 103, 2, 6, 0, true);

                this.pickComponent(usernames.get(1), 14);
                this.insertComponent(usernames.get(1), 14, 3, 0, 0, true);

                this.pickComponent(usernames.get(1), 79);
                this.insertComponent(usernames.get(1), 79, 3, 1, 0, true);

                this.pickComponent(usernames.get(1), 85);
                this.insertComponent(usernames.get(1), 85, 3, 3, 0, true);

                this.pickComponent(usernames.get(1), 43);
                this.rotateComponent(usernames.get(1), 43, 1);
                this.insertComponent(usernames.get(1), 43, 3, 4, 0, true);

                this.pickComponent(usernames.get(1), 53);
                this.insertComponent(usernames.get(1), 53, 3, 5, 0, true);

                this.pickComponent(usernames.get(1), 97);
                this.insertComponent(usernames.get(1), 97, 3, 6, 0, true);

                this.pickComponent(usernames.get(1), 45);
                this.rotateComponent(usernames.get(1), 45, 1);
                this.insertComponent(usernames.get(1), 45, 4, 4, 0, true);

                this.pickComponent(usernames.get(1), 67);
                this.insertComponent(usernames.get(1), 67, 4, 5, 0, true);

                this.pickComponent(usernames.get(1), 90);
                this.reserveComponent(usernames.get(1), 90);


                this.pickComponent(usernames.get(2), 118);
                this.insertComponent(usernames.get(2), 118, 0, 2, 0, true);

                this.pickComponent(usernames.get(2), 126);
                this.insertComponent(usernames.get(2), 126, 0, 4, 0, true);

                this.pickComponent(usernames.get(2), 136);
                this.rotateComponent(usernames.get(2), 136, 1);
                this.insertComponent(usernames.get(2), 136, 1, 1, 0, true);

                this.pickComponent(usernames.get(2), 44);
                this.rotateComponent(usernames.get(2), 44, 3);
                this.insertComponent(usernames.get(2), 44, 1, 2, 0, true);

                this.pickComponent(usernames.get(2), 61);
                this.insertComponent(usernames.get(2), 61, 1, 3, 0, true);

                this.pickComponent(usernames.get(2), 1);
                this.rotateComponent(usernames.get(2), 1, 3);
                this.insertComponent(usernames.get(2), 1, 1, 4, 0, true);

                this.pickComponent(usernames.get(2), 133);
                this.insertComponent(usernames.get(2), 133, 1, 5, 0, true);

                this.pickComponent(usernames.get(2), 114);
                this.insertComponent(usernames.get(2), 114, 2, 0, 0, true);

                this.pickComponent(usernames.get(2), 37);
                this.rotateComponent(usernames.get(2), 37, 1);
                this.insertComponent(usernames.get(2), 37, 2, 1, 0, true);

                this.pickComponent(usernames.get(2), 148);
                this.rotateComponent(usernames.get(2), 148, 2);
                this.insertComponent(usernames.get(2), 148, 2, 2, 0, true);

                this.pickComponent(usernames.get(2), 142);
                this.rotateComponent(usernames.get(2), 142, 2);
                this.insertComponent(usernames.get(2), 142, 2, 4, 0, true);

                this.pickComponent(usernames.get(2), 39);
                this.rotateComponent(usernames.get(2), 39, 3);
                this.insertComponent(usernames.get(2), 39, 2, 5, 0, true);

                this.pickComponent(usernames.get(2), 12);
                this.insertComponent(usernames.get(2), 12, 3, 0, 0, true);

                this.pickComponent(usernames.get(2), 41);
                this.insertComponent(usernames.get(2), 41, 3, 1, 0, true);

                this.pickComponent(usernames.get(2), 18);
                this.rotateComponent(usernames.get(2), 18, 2);
                this.insertComponent(usernames.get(2), 18, 3, 2, 0, true);

                this.pickComponent(usernames.get(2), 95);
                this.insertComponent(usernames.get(2), 95, 3, 3, 0, true);

                this.pickComponent(usernames.get(2), 151);
                this.rotateComponent(usernames.get(2), 151, 3);
                this.insertComponent(usernames.get(2), 151, 3, 4, 0, true);

                this.pickComponent(usernames.get(2), 30);
                this.insertComponent(usernames.get(2), 30, 3, 5, 0, true);

                this.pickComponent(usernames.get(2), 75);
                this.insertComponent(usernames.get(2), 75, 4, 0, 0, true);

                this.pickComponent(usernames.get(2), 94);
                this.insertComponent(usernames.get(2), 94, 4, 1, 0, true);

                this.pickComponent(usernames.get(2), 81);
                this.insertComponent(usernames.get(2), 81, 4, 2, 0, true);

                this.pickComponent(usernames.get(2), 10);
                this.rotateComponent(usernames.get(2), 10, 1);
                this.insertComponent(usernames.get(2), 10, 4, 4, 0, true);

                this.pickComponent(usernames.get(2), 96);
                this.insertComponent(usernames.get(2), 96, 4, 5, 0, true);

                this.pickComponent(usernames.get(2), 87);
                this.insertComponent(usernames.get(2), 87, 4, 6, 0, true);
            }
        }
    }

}
