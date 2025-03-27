package it.polimi.ingsw.model.cards.commands;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.exceptions.CabinComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.Ship;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RemoveCrewCommand implements Command {

    private final String username;
    private final Board board;
    private final List<CabinComponent> cabinComponents;

    public RemoveCrewCommand(String username, Board board, List<CabinComponent> cabinComponents) {
        this.username = username;
        this.board = board;
        this.cabinComponents = cabinComponents;
    }

    @Override
    public void execute(Card card) {
        Ship ship = board.getPlayerEntityByUsername(username).getShip();
        checkInput(ship);
        card.doSpecificCheck(PlayerState.WAIT_REMOVE_CREW, cabinComponents, 0, username, board);

        for (CabinComponent cabin : cabinComponents) {
            if (cabin.getAlien().isPresent())
                cabin.setAlien(null, ship);
            else
                cabin.setHumans(cabin.getHumans() - 1, ship);
        }

    }

    private void checkInput(Ship ship) {
        for (CabinComponent component : cabinComponents)
            if (ship.getDashboard(component.getY(), component.getX()).isEmpty() || !ship.getDashboard(component.getY(), component.getX()).get().equals(component))
                throw new ComponentNotValidException("Cabin component not valid");

        boolean enoughCrew = cabinComponents.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .allMatch(entry -> entry.getValue() <= (entry.getKey().getAlien().isPresent() ? 2 : entry.getKey().getHumans()));
        if (!enoughCrew)
            throw new CabinComponentNotValidException("Not enough crew");
    }

}
