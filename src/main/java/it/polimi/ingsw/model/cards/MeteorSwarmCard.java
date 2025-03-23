package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.utils.Meteor;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;
import java.util.Optional;

public class MeteorSwarmCard extends Card{

    private final List<Meteor> meteors;
    private int meteorIndex;
    private int coord;

    public MeteorSwarmCard(int level, boolean isLearner, List<Meteor> meteors) {
        super(level, isLearner);
        this.meteors = meteors;
    }

    @Override
    public void resolve(Board board) throws Exception {
    }

    public void startCard(Board board) {
        this.meteorIndex = 0;

        for (PlayerData player : board.getPlayersByPos())
            playersState.put(player.getUsername(), CardState.WAIT);
        playersState.put(board.getPlayersByPos().getFirst().getUsername(), CardState.WAIT_ROLL_DICE);
    }

    public void changeState(Board board, String username) throws Exception {

        CardState actState = playersState.get(username);

        switch (actState) {
            case WAIT_ROLL_DICE -> {
                for(PlayerData player : board.getPlayersByPos()) {
                    CardState newState = meteors.get(meteorIndex).hit(player.getShip(), coord);
                    playersState.put(player.getUsername(), newState);
                }
                meteorIndex++;
            }
            case WAIT_SHIELD, WAIT_CANNON -> playersState.put(username, CardState.DONE);
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : board.getPlayersByPos())
            if (playersState.get(player.getUsername()) != CardState.DONE)
                hasDone = false;

        if (hasDone) {
            if (meteorIndex >= meteors.size()) {
                endCard();
            }
            else {
                for (PlayerData player : board.getPlayersByPos())
                    playersState.put(player.getUsername(), CardState.WAIT);
                playersState.put(board.getPlayersByPos().getFirst().getUsername(), CardState.WAIT_ROLL_DICE);
            }
        }

    }

    public void endCard() {

    }

    public void doCommandEffects(CardState commandType, Integer value) {
        if (commandType == CardState.WAIT_ROLL_DICE)
            this.coord = value;
    }

    public void doCommandEffects(CardState commandType, Double value, String username, Board board) {
        // TODO pre check that cannon is the right one and is effectly activated
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == CardState.WAIT_CANNON && value > 0) {
            Optional<Component> target = meteors.get(meteorIndex).getTarget(player.getShip(), coord);
            if (target.isPresent())
                target.get().destroyComponent(player.getShip());
        }
    }

    public void doCommandEffects(CardState commandType, Boolean value, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == CardState.WAIT_SHIELD && !value) {
            Optional<Component> target = meteors.get(meteorIndex).getTarget(player.getShip(), coord);
            if (target.isPresent())
                target.get().destroyComponent(player.getShip());
        }
    }

}