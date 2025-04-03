package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.utils.Meteor;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.properties.DirectionType;

import java.io.Console;
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
    public boolean startCard(Board board) {
        this.meteorIndex = 0;

        for (PlayerData player : board.getPlayersByPos())
            playersState.put(player.getUsername(), PlayerState.WAIT);
        playersState.put(board.getPlayersByPos().getFirst().getUsername(), PlayerState.WAIT_ROLL_DICES);
        return false;
    }

    @Override
    protected boolean changeState(Board board, String username) {

        PlayerState actState = playersState.get(username);

        switch (actState) {
            case WAIT_ROLL_DICES -> {
                for(PlayerData player : board.getPlayersByPos()) {
                    PlayerState newState = meteors.get(meteorIndex).hit(player.getShip(), coord);
                    playersState.put(player.getUsername(), newState);
                }
            }
            case WAIT_SHIELD, WAIT_CANNONS -> playersState.put(username, PlayerState.DONE);
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : board.getPlayersByPos())
            if (playersState.get(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone) {
            meteorIndex ++;
            if (meteorIndex >= meteors.size()) {
                endCard(board);
                return true;
            }
            else {

                for (PlayerData player : board.getPlayersByPos())
                    playersState.put(player.getUsername(), PlayerState.WAIT);
                playersState.put(board.getPlayersByPos().getFirst().getUsername(), PlayerState.WAIT_ROLL_DICES);
            }
        }
        return false;
    }

    @Override
    public void doSpecificCheck(PlayerState commandType, List<CannonComponent> cannons, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_CANNONS) {
            if (cannons.size() != 1) throw new IllegalArgumentException("Too many cannon components provided");
            CannonComponent chosenCannon = cannons.getFirst();

            List<Component> targets = meteors.get(meteorIndex).getTargets(player.getShip(), coord);
            if (meteors.get(meteorIndex).getDirectionFrom() != DirectionType.NORTH) {
                targets.addAll(meteors.get(meteorIndex).getDirectionFrom().getComponentsFromThisDirection(player.getShip().getDashboard(), coord-1));
                targets.addAll(meteors.get(meteorIndex).getDirectionFrom().getComponentsFromThisDirection(player.getShip().getDashboard(), coord+1));
            }

            targets.stream()
                .filter(c -> c instanceof CannonComponent)
                .map(c -> (CannonComponent) c)
                .filter(c -> c.getDirection() == meteors.get(meteorIndex).getDirectionFrom())
                .filter(cannonComponent -> cannonComponent.equals(chosenCannon))
                .findFirst()
                .orElseThrow(() -> new  IllegalArgumentException("Cannon component not found in target coordinates"));
        }
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Integer value, String username, Board board) {
        if (commandType == PlayerState.WAIT_ROLL_DICES)
            this.coord = value;
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Double value, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_CANNONS && value == 0) {
            Optional<Component> target = meteors.get(meteorIndex).getTargets(player.getShip(), coord).stream().findFirst();
            target.ifPresent(component -> component.destroyComponent(player.getShip()));
        }
    }

    @Override
    public void doCommandEffects(PlayerState commandType, Boolean value, String username, Board board) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_SHIELD && !value) {
            Optional<Component> target = meteors.get(meteorIndex).getTargets(player.getShip(), coord).stream().findFirst();
            target.ifPresent(component -> component.destroyComponent(player.getShip()));
        }
    }

}