package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.Meteor;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.properties.DirectionType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MeteorSwarmCard extends Card{

    private final List<Meteor> meteors;
    private int meteorIndex;
    private int coord;

    public MeteorSwarmCard(int id, int level, boolean isLearner, List<Meteor> meteors) {
        super(id, level, isLearner);
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
                if (!model.isLearnerMode())
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

    @Override
    public String toString() {
        String hBorder = "─";
        String vBorder = "│";
        String[] angles = {"┌", "┐", "└", "┘"};
        String hDivider = "┼";
        String leftDivider = "├";
        String rightDivider = "┤";

        List<String> cardLines = new ArrayList<>();

        // Title box
        String topBorder = " " + angles[0] + Constants.repeat(hBorder, 21) + angles[1] + " ";
        cardLines.add(topBorder);

        String title = " " + vBorder + Constants.inTheMiddle("MeteorSwarm" + (getIsLearner() ? "(L)" : ""), 21) + vBorder + " ";
        cardLines.add(title);

        // First row divider
        String divider = " " + leftDivider + Constants.repeat(hBorder, 21) + rightDivider + " ";
        cardLines.add(divider);

        for (Meteor m : meteors) {
            String meteorRow = " " + vBorder + "     " + m.toString() +"\u200A" + "\u2005" + "     " + vBorder + " ";
            cardLines.add(meteorRow);
        }


        // Bottom border
        String bottomBorder = " " + angles[2] + Constants.repeat(hBorder, 21) + angles[3] + " ";
        cardLines.add(bottomBorder);

        return String.join("\n", cardLines);
    }

    @Override
    public void printCardInfo(ModelFacade model, Board board) {
        for (PlayerData player : board.getPlayersByPos()) {
            PlayerState state = model.getPlayerState(player.getUsername());

            switch (state) {
                case DONE -> Chroma.println("- " + player.getUsername() + " has done", Chroma.YELLOW_BOLD);
                case WAIT -> Chroma.println("- " + player.getUsername() + " is waiting", Chroma.YELLOW_BOLD);
                case WAIT_SHIP_PART -> Chroma.println("- " + player.getUsername() + " is choosing which part of ship to keep", Chroma.YELLOW_BOLD);
                case WAIT_SHIELD -> Chroma.println("- " + player.getUsername() + " is choosing if activate a shield or not", Chroma.YELLOW_BOLD);
                case WAIT_CANNONS -> Chroma.println("- " + player.getUsername() + " is choosing if activate a double cannon or not", Chroma.YELLOW_BOLD);
                case WAIT_ROLL_DICES -> Chroma.println("- " + player.getUsername() + " is rolling dices", Chroma.YELLOW_BOLD);
            }
        }

        if (board.getPlayersByPos().stream().noneMatch(p -> model.getPlayerState(p.getUsername()) == PlayerState.WAIT_ROLL_DICES))
            Chroma.println("Meteor n." + (meteorIndex+1) + " is hitting at coord: " + coord, Chroma.YELLOW_BOLD);
        else if (meteorIndex > 0)
            Chroma.println("Previous meteor n." + (meteorIndex) + " has come at coord: " + coord, Chroma.YELLOW_BOLD);
    }

}