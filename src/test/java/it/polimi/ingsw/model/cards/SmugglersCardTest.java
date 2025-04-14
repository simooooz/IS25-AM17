package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SmugglersCardTest {
    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private GameController controller;
    private Board board;
    private Map<ColorType, Integer> rewards;


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

        p1.setCredits(50);
        p2.setCredits(40);


        controller.showComponent("Simone", 52);
        controller.pickComponent("Simone", 52);
        controller.rotateComponent("Simone", 52, 1);
        controller.insertComponent("Simone", 52, 2, 2);

        controller.showComponent("Simone", 69);
        controller.pickComponent("Simone", 69);
        controller.rotateComponent("Simone", 69, 1);
        controller.insertComponent("Simone", 69, 3, 2);

        controller.showComponent("Simone", 17);
        controller.pickComponent("Simone", 17);
        controller.insertComponent("Simone", 17, 1, 2);

        controller.showComponent("Simone", 32);
        controller.pickComponent("Simone", 32);
        controller.insertComponent("Simone", 32, 2, 3);


        controller.showComponent("Davide", 38);
        controller.pickComponent("Davide", 38);
        controller.rotateComponent("Davide", 38, 2);
        controller.insertComponent("Davide", 38, 1, 2);

        controller.showComponent("Davide", 68);
        controller.pickComponent("Davide", 68);
        controller.rotateComponent("Davide", 68, 1);
        controller.insertComponent("Davide", 68, 0, 2);

        controller.showComponent("Davide", 53);
        controller.pickComponent("Davide", 53);
        controller.rotateComponent("Davide", 53, 1);
        controller.insertComponent("Davide", 53, 2, 2);

        controller.showComponent("Davide", 33);
        controller.pickComponent("Davide", 33);
        controller.insertComponent("Davide", 33, 2, 3);

        rewards = new HashMap<>();
        rewards.put(ColorType.RED, 2);
        rewards.put(ColorType.GREEN, 1);
    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldGetRewardsIfFirePowerEnoughAndMovePlayer() {

        controller.showComponent("Simone", 129);
        controller.pickComponent("Simone", 129);
        controller.insertComponent("Simone", 129, 0, 2);

        controller.showComponent("Simone", 115);
        controller.pickComponent("Simone", 115);
        controller.insertComponent("Simone", 115, 1, 3);

        controller.showComponent("Simone", 15);
        controller.pickComponent("Simone", 15);
        controller.rotateComponent("Simone", 15, 2);
        controller.insertComponent("Simone", 15, 1, 4);

        controller.setReady("Simone");
        controller.setReady("Davide");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);

        SmugglersCard smugglersCard = new SmugglersCard(2, false, 1, 5, rewards, 1);
        board.getCardPile().clear();
        board.getCardPile().add(smugglersCard);

        controller.drawCard("Simone");

        controller.activateCannons("Simone", new ArrayList<>(List.of(15)), new ArrayList<>(List.of(129)));

        controller.getBoolean("Simone", true);

        controller.updateGoods(p1.getUsername(), new HashMap<>(Map.of(69, new ArrayList<>(List.of(ColorType.RED, ColorType.GREEN)))), new ArrayList<>());

        assertEquals(1, p1.getShip().getGoods().get(ColorType.RED));
        assertEquals(1, p1.getShip().getGoods().get(ColorType.GREEN));
        assertEquals(14, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().orElseThrow().getValue());
    }

    @Test
    void testShouldCheckThatCardIsUsedBySecondPLayer() {

        controller.showComponent("Davide", 102);
        controller.pickComponent("Davide", 102);
        controller.rotateComponent("Davide", 102, 3);
        controller.insertComponent("Davide", 102, 1, 1);

        controller.showComponent("Davide", 114);
        controller.pickComponent("Davide", 114);
        controller.insertComponent("Davide", 114, 1, 3);

        controller.showComponent("Davide", 134);
        controller.pickComponent("Davide", 134);
        controller.rotateComponent("Davide", 134, 2);
        controller.insertComponent("Davide", 134, 3, 2);

        controller.showComponent("Davide", 5);
        controller.pickComponent("Davide", 5);
        controller.insertComponent("Davide", 5, 3, 3);

        controller.setReady("Simone");
        controller.setReady("Davide");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);

        SmugglersCard smugglersCard = new SmugglersCard(2, false, 2, 2, rewards, 1);
        board.getCardPile().clear();
        board.getCardPile().add(smugglersCard);

        ((SpecialCargoHoldsComponent) p1.getShip().getDashboard(3,2).orElseThrow()).loadGood(ColorType.BLUE, p1.getShip());
        ((SpecialCargoHoldsComponent) p1.getShip().getDashboard(3,2).orElseThrow()).loadGood(ColorType.GREEN, p1.getShip());
        ((SpecialCargoHoldsComponent) p1.getShip().getDashboard(1,2).orElseThrow()).loadGood(ColorType.YELLOW, p1.getShip());

        controller.drawCard("Simone");

        Map<Integer, List<ColorType>> cargoMap1 = new HashMap<>();
        cargoMap1.put(69, new ArrayList<>(List.of(ColorType.BLUE)));
        cargoMap1.put(17, new ArrayList<>());

        controller.updateGoods("Simone", cargoMap1, new ArrayList<>());

        controller.activateCannons("Davide", new ArrayList<>(List.of(5)), new ArrayList<>(List.of(134)));
        controller.getBoolean(p2.getUsername(), true);

        Map<Integer, List<ColorType>> cargoMap2 = new HashMap<>();
        cargoMap2.put(68, new ArrayList<>(List.of(ColorType.RED, ColorType.GREEN)));

        controller.updateGoods("Davide", cargoMap2, new ArrayList<>());

        assertEquals(0, p1.getShip().getGoods().get(ColorType.YELLOW));
        assertEquals(0, p1.getShip().getGoods().get(ColorType.GREEN));
        assertEquals(1, p1.getShip().getGoods().get(ColorType.BLUE));
        assertEquals(15, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().orElseThrow().getValue());
        assertEquals(1, p2.getShip().getGoods().get(ColorType.RED));
        assertEquals(1, p2.getShip().getGoods().get(ColorType.GREEN));
        assertEquals(11, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p2)).findFirst().orElseThrow().getValue());
    }
}