package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.components.SpecialCargoHoldsComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbandonedStationCardTest {

    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private GameController controller;
    private Board board;
    private Map<ColorType, Integer> cardGoods;


    @BeforeEach
    void setUp() {

        usernames = new ArrayList<>();
        usernames.add("Simone");
        usernames.add("Davide");

        controller = new GameController(usernames, false);
        controller.startMatch();

        board = controller.getModel().getBoard();
        p1 = board.getPlayerEntityByUsername("Simone");
        p2 = board.getPlayerEntityByUsername("Davide");


        controller.pickComponent("Simone", 69);
        controller.rotateComponent("Simone", 69, 3);
        controller.insertComponent("Simone", 69, 1, 4, 0, true);

        ((SpecialCargoHoldsComponent) p1.getShip().getDashboard(1,4).orElseThrow()).loadGood(ColorType.BLUE, p1.getShip());
        ((SpecialCargoHoldsComponent) p1.getShip().getDashboard(1,4).orElseThrow()).loadGood(ColorType.RED, p1.getShip());

        controller.pickComponent("Simone", 44);
        controller.rotateComponent("Simone", 44, 1);
        controller.insertComponent("Simone", 44, 2, 4, 0, true);

        controller.pickComponent("Simone", 67);
        controller.rotateComponent("Simone", 67, 1);
        controller.insertComponent("Simone", 67, 3, 4, 0, true);

        controller.pickComponent("Davide", 39);
        controller.rotateComponent("Davide", 39, 3);
        controller.insertComponent("Davide", 39, 2, 4, 0, true);

        controller.pickComponent("Davide", 68);
        controller.rotateComponent("Davide", 68, 1);
        controller.insertComponent("Davide", 68, 3, 4, 0, true);

        controller.setReady("Simone");
        controller.setReady("Davide");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);


        cardGoods = new HashMap<>();
        cardGoods.put(ColorType.RED, 2);
        cardGoods.put(ColorType.GREEN, 1);

    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldNotUpdateIfCrewNotEnough() {
        AbandonedStationCard abandonedStationCard = new AbandonedStationCard(0, 2, false, 10, 5, cardGoods);
        board.getCardPile().clear();
        board.getCardPile().add(abandonedStationCard);

        controller.drawCard("Simone");

        assertEquals(2, p1.getShip().getGoods().values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(15, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().orElseThrow().getValue());
    }

    @Test
    void testShouldCheckIfTheParameterAreUpdate() {
        AbandonedStationCard abandonedStationCard = new AbandonedStationCard(0, 2, false, 2, 5, cardGoods);
        board.getCardPile().clear();
        board.getCardPile().add(abandonedStationCard);

        controller.drawCard("Simone");

        controller.getBoolean("Simone", true);

        Map<Integer, List<ColorType>> cargoMap = new HashMap<>();
        cargoMap.put(67, new ArrayList<>(List.of(ColorType.RED, ColorType.GREEN)));
        cargoMap.put(69, new ArrayList<>(List.of(ColorType.BLUE, ColorType.RED)));

        controller.updateGoods("Simone", cargoMap, new ArrayList<>());

        assertEquals(4, p1.getShip().getGoods().values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(9, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().orElseThrow().getValue());
    }

    @Test
    void testShouldCheckIfTheCardIsUsedBySecondPlayer() {
        AbandonedStationCard abandonedStationCard = new AbandonedStationCard(0, 2, false, 2, 2, cardGoods);
        board.getCardPile().clear();
        board.getCardPile().add(abandonedStationCard);

        controller.drawCard("Simone");
        controller.getBoolean("Simone", false);
        controller.getBoolean("Davide", true);

        Map<Integer, List<ColorType>> cargoMap = new HashMap<>();
        cargoMap.put(68, new ArrayList<>(List.of(ColorType.RED, ColorType.GREEN)));

        controller.updateGoods(p2.getUsername(), cargoMap, new ArrayList<>());

        assertEquals(2, p1.getShip().getGoods().values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(2, p2.getShip().getGoods().values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(15, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().orElseThrow().getValue());
        assertEquals(10, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p2)).findFirst().orElseThrow().getValue());
    }
}