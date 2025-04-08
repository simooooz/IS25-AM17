package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.Meteor;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.properties.DirectionType;

import java.io.Console;
import java.util.List;
import java.util.Objects;
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
    public boolean startCard(ModelFacade model, Board board) {
        this.meteorIndex = 0;

        for (PlayerData player : board.getPlayersByPos())
            model.setPlayerState(player.getUsername(), PlayerState.WAIT);
        model.setPlayerState(board.getPlayersByPos().getFirst().getUsername(), PlayerState.WAIT_ROLL_DICES);
        return false;
    }

    private boolean autoCheckPlayers(ModelFacade model, Board board) {
        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : board.getPlayersByPos())
            if (model.getPlayerState(player.getUsername()) != PlayerState.DONE)
                hasDone = false;

        if (hasDone) {
            meteorIndex++;
            if (meteorIndex >= meteors.size()) {
                endCard(board);
                return true;
            }
            else {
                for (PlayerData player : board.getPlayersByPos())
                    model.setPlayerState(player.getUsername(), PlayerState.WAIT);
                model.setPlayerState(board.getPlayersByPos().getFirst().getUsername(), PlayerState.WAIT_ROLL_DICES);
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
    public boolean doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_ROLL_DICES) {
            this.coord = value;
            for (PlayerData player : board.getPlayersByPos()) {
                PlayerState newState = meteors.get(meteorIndex).hit(player.getShip(), coord);
                model.setPlayerState(player.getUsername(), newState);
            }
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Double value, ModelFacade model, Board board, String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_CANNONS) {
            if (value == 0) {
                Optional<Component> target = meteors.get(meteorIndex).getTargets(player.getShip(), coord).stream().findFirst();
                target.ifPresent(component -> {
                    PlayerState newState = component.destroyComponent(player.getShip()); // DONE or WAIT_SHIP_PART
                    model.setPlayerState(username, newState);
                });
            }
            else
                model.setPlayerState(username, PlayerState.DONE);
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        PlayerData player = board.getPlayerEntityByUsername(username);
        if (commandType == PlayerState.WAIT_SHIELD) {
            if (!value) {
                Optional<Component> target = meteors.get(meteorIndex).getTargets(player.getShip(), coord).stream().findFirst();
                target.ifPresent(component -> {
                    PlayerState newState = component.destroyComponent(player.getShip()); // DONE or WAIT_SHIP_PART
                    model.setPlayerState(username, newState);
                });
            }
            else
                model.setPlayerState(username, PlayerState.DONE);
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

    @Override
    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        if (commandType == PlayerState.WAIT_SHIP_PART) {
            model.setPlayerState(username, PlayerState.DONE);
            return autoCheckPlayers(model, board);
        }
        throw new RuntimeException("Command type not valid in doCommandEffects");
    }

}