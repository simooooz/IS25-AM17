package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedShipCardTest {

    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private PlayerData p3;
    private GameController controller;
    private Board board;

    @BeforeEach
    void setUp() {

        usernames = new ArrayList<>();
        usernames.add("Simone");
        usernames.add("Davide");
        usernames.add("Tommaso");

        controller = new GameController(usernames, false);
        controller.startMatch();

        board = controller.getModel().getBoard();
        p1 = board.getPlayerEntityByUsername("Simone");
        p2 = board.getPlayerEntityByUsername("Davide");
        p3 = board.getPlayerEntityByUsername("Tommaso");


        controller.pickComponent("Simone", 141);
        controller.insertComponent("Simone", 141, 2, 1, 0, true);

        controller.pickComponent("Simone", 37);
        controller.rotateComponent("Simone", 37, 3);
        controller.insertComponent("Simone", 37, 1, 1, 0, true);

        controller.pickComponent("Simone", 59);
        controller.insertComponent("Simone", 59, 2, 2, 0, true);

        controller.pickComponent("Davide", 136);
        controller.insertComponent("Davide", 136, 3, 1, 0, true);

        controller.pickComponent("Davide", 46);
        controller.rotateComponent("Davide",46, 2);
        controller.insertComponent("Davide", 46, 2, 2, 0, true);

        controller.pickComponent("Davide", 44);
        controller.insertComponent("Davide", 44, 2, 1, 0, true);

        controller.pickComponent("Tommaso", 117);
        controller.rotateComponent("Tommaso", 117, 3);
        controller.insertComponent("Tommaso", 117, 1, 3, 0, true);

        controller.setReady("Simone");
        controller.setReady("Davide");
        controller.setReady("Tommaso");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);
        board.movePlayer(p3, 10);

        p1.setCredits(50);
        p2.setCredits(40);
        p3.setCredits(30);

        controller.chooseAlien("Simone", new HashMap<>(Map.of(37, AlienType.CANNON)));

        controller.chooseAlien("Davide", new HashMap<>(Map.of(44, AlienType.ENGINE)));
    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldNotUpdateCardIfCrewNotEnough() {
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(0, 2, false, 10, 6, 0);
        board.getCardPile().clear();
        board.getCardPile().add(abandonedShipCard);
        board.getCardPile().add(abandonedShipCard);


        controller.drawCard("Simone");

        assertEquals(3, p1.getShip().getCrew());
        assertEquals(50, p1.getCredits());
        assertEquals(15, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().orElseThrow().getValue());
    }

    @Test
    void testShouldCheckIfTheParameterAreUpdateWithHuman() {
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(0, 2, false, 2, 6, 7);
        board.getCardPile().clear();
        board.getCardPile().add(abandonedShipCard);
        board.getCardPile().add(abandonedShipCard);

        controller.drawCard("Simone");

        controller.getBoolean("Simone", true);

        controller.removeCrew("Simone", new ArrayList<>(List.of(32, 32)));

        assertTrue(board.getStartingDeck().contains(p1));
        assertEquals(1, p1.getShip().getCrew());
        assertEquals(56, p1.getCredits());
    }

    @Test
    void testShouldCheckIfTheParameterAreUpdateWithHumanAndAlien() {
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(0, 2, false, 2, 6, 7);
        board.getCardPile().clear();
        board.getCardPile().add(abandonedShipCard);
        board.getCardPile().add(abandonedShipCard);

        controller.drawCard("Simone");

        controller.getBoolean("Simone", true);

        controller.removeCrew("Simone", new ArrayList<>(List.of(32, 37)));

        assertEquals(1, p1.getShip().getCrew());
        assertEquals(56, p1.getCredits());
        assertEquals(6, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().orElseThrow().getValue());
    }

    @Test
    void testShouldCheckIfTheCardIsUsedBySecondPlayer() {
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(0, 2, false, 4, 6, 2);
        board.getCardPile().clear();
        board.getCardPile().add(abandonedShipCard);
        board.getCardPile().add(abandonedShipCard);

        controller.drawCard("Simone");

        controller.getBoolean("Davide", true);

        controller.removeCrew("Davide", new ArrayList<>(List.of(33, 33, 44, 46)));

        assertEquals(3, p1.getShip().getCrew());
        assertEquals(1, p2.getShip().getCrew());
        assertEquals(50, p1.getCredits());
        assertEquals(46, p2.getCredits());
        assertEquals(15, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().orElseThrow().getValue());
        assertEquals(9, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p2)).findFirst().orElseThrow().getValue());
    }

    @Test
    void testShouldCheckIfTheCardIsUsedByThirdPlayer() {
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(0, 2, false, 1, 6, 2);
        board.getCardPile().clear();
        board.getCardPile().add(abandonedShipCard);
        board.getCardPile().add(abandonedShipCard);

        controller.drawCard("Simone");

        controller.getBoolean("Simone", false);
        controller.getBoolean("Davide", false);
        controller.getBoolean("Tommaso", true);

        controller.removeCrew("Tommaso", new ArrayList<>(List.of(34)));

        assertEquals(3, p1.getShip().getCrew());
        assertEquals(5, p2.getShip().getCrew());
        assertEquals(1, p3.getShip().getCrew());
        assertEquals(50, p1.getCredits());
        assertEquals(40, p2.getCredits());
        assertEquals(36, p3.getCredits());
        assertEquals(15, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().orElseThrow().getValue());
        assertEquals(12, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p2)).findFirst().orElseThrow().getValue());
        assertEquals(9, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p3)).findFirst().orElseThrow().getValue());
    }

}







