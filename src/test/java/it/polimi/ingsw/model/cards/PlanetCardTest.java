package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.utils.Planet;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PlanetCardTest {

    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private PlayerData p3;
    private GameController controller;


    @BeforeEach
    void setUp() {

        usernames = new ArrayList<>();
        usernames.add("Simone");
        usernames.add("Davide");
        usernames.add("Tommaso");

        controller = new GameController(usernames, false);
        controller.startMatch();

        Board board = controller.getModel().getBoard();
        p1 = board.getPlayerEntityByUsername("Simone");
        p2 = board.getPlayerEntityByUsername("Davide");
        p3 = board.getPlayerEntityByUsername("Tommaso");

        Map<ColorType, Integer> rewards1 = new HashMap<>();
        rewards1.put(ColorType.RED, 2);
        rewards1.put(ColorType.GREEN, 1);
        Map<ColorType, Integer> rewards2 = new HashMap<>();
        rewards2.put(ColorType.RED, 2);
        rewards2.put(ColorType.YELLOW, 1);
        List<Planet> planetList = new ArrayList<>(List.of(new Planet(rewards1), new Planet(rewards2), new Planet(rewards1)));

        PlanetCard planetCard = new PlanetCard(0, 2, true, planetList, 3);
        board.getCardPile().clear();
        board.getCardPile().add(planetCard);

    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldLandonPlanetP1() {

        controller.pickComponent("Simone", 31);
        controller.insertComponent("Simone", 31, 1, 3, 0, true);

        controller.pickComponent("Simone", 68);
        controller.insertComponent("Simone", 68, 2, 2, 0, true);


        controller.pickComponent("Davide", 51);
        controller.insertComponent("Davide", 51, 2, 4, 0, true);


        controller.pickComponent("Tommaso", 69);
        controller.insertComponent("Tommaso", 69, 2, 4, 0, true);

        controller.setReady("Simone");
        controller.setReady("Davide");
        controller.setReady("Tommaso");

        controller.drawCard("Simone");

        // player1 lands on first planet
        controller.getIndex("Simone", 0);

        controller.getIndex("Davide", null);

        controller.getIndex("Tommaso", null);

        //P1 response
        Map<Integer, List<ColorType>> cargoMap = new HashMap<>();
        cargoMap.put(68, new ArrayList<>(List.of(ColorType.RED, ColorType.GREEN)));
        //cargoMap.put(31, new ArrayList<>());

        controller.updateGoods("Simone", cargoMap, new ArrayList<>());

        assertEquals(1, p1.getShip().getGoods().get(ColorType.RED));
        assertEquals(1, p1.getShip().getGoods().get(ColorType.GREEN));
        assertEquals(0, p2.getShip().getGoods().get(ColorType.RED));

    }

    @Test
    void testShouldLandOnPlanetAllThePlayers() {

        controller.pickComponent("Simone", 68);
        controller.insertComponent("Simone", 68, 2, 2, 0, true);


        controller.pickComponent("Davide", 31);
        controller.insertComponent("Davide", 31, 2, 4, 0, true);


        controller.pickComponent("Tommaso", 69);
        controller.insertComponent("Tommaso", 69, 2, 4, 0, true);

        controller.setReady("Simone");
        controller.setReady("Davide");
        controller.setReady("Tommaso");

        controller.drawCard("Simone");

        controller.getIndex("Simone", 0);

        controller.getIndex("Davide", 1);

        controller.getIndex("Tommaso", 2);


        //P1 response
        Map<Integer, List<ColorType>> cargoMap1 = new HashMap<>();
        cargoMap1.put(68, new ArrayList<>(List.of(ColorType.RED, ColorType.RED)));

        controller.updateGoods(p1.getUsername(), cargoMap1, new ArrayList<>());

        //P2 response
        Map<Integer, List<ColorType>> cargoMap2 = new HashMap<>();
        cargoMap2.put(31, new ArrayList<>(List.of(ColorType.YELLOW)));

        controller.updateGoods(p2.getUsername(), cargoMap2, new ArrayList<>());

        //P3 response
        Map<Integer, List<ColorType>> cargoMap3 = new HashMap<>();
        cargoMap3.put(69, new ArrayList<>(List.of(ColorType.RED, ColorType.GREEN)));

        controller.updateGoods(p3.getUsername(), cargoMap3, new ArrayList<>());

        assertEquals(2, p1.getShip().getGoods().get(ColorType.RED));
        assertEquals(0, p1.getShip().getGoods().get(ColorType.GREEN));
        assertEquals(0, p2.getShip().getGoods().get(ColorType.RED));
        assertEquals(1, p2.getShip().getGoods().get(ColorType.YELLOW));
        assertEquals(1, p3.getShip().getGoods().get(ColorType.RED));
        assertEquals(0, p3.getShip().getGoods().get(ColorType.YELLOW));
        assertEquals(2, ((SpecialCargoHoldsComponent)p1.getShip().getDashboard(2, 2).orElseThrow()).getGoods().stream().filter(color -> color == ColorType.RED).count());

    }




}