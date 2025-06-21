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

class SlaversCardTest {
    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private GameController controller;
    private Board board;


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


        controller.pickComponent("Simone", 136);
        controller.rotateComponent("Simone", 136, 1);
        controller.insertComponent("Simone", 136, 2, 1, 0, true);

        controller.pickComponent("Simone", 52);
        controller.insertComponent("Simone", 52, 2, 2, 0, true);

        controller.pickComponent("Davide", 53);
        controller.rotateComponent("Davide", 53, 1);
        controller.insertComponent("Davide", 53, 2, 2, 0, true);

    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }


    @Test
    void testShouldGetRewardsIfFirePowerEnoughAndMovePlayer() {

        controller.pickComponent("Simone", 115);
        controller.insertComponent("Simone", 115, 1, 3, 0, true);

        controller.pickComponent("Simone", 132);
        controller.insertComponent("Simone", 132, 1, 4, 0, true);

        controller.pickComponent("Simone", 15);
        controller.rotateComponent("Simone", 15, 2);
        controller.insertComponent("Simone", 15, 2, 4, 0, true);

        controller.setReady("Simone");
        controller.setReady("Davide");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);

        SlaversCard slaversCard = new SlaversCard(0, 2, false, 5, 5, 1, 1);
        board.getCardPile().clear();
        board.getCardPile().add(slaversCard);

        controller.chooseAlien("Simone", new HashMap<>(Map.of()));

        controller.drawCard("Simone");
        controller.activateCannons("Simone", new ArrayList<>(List.of(15)), new ArrayList<>(List.of(132)));
        controller.getBoolean("Simone", true);

        assertEquals(14, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().orElseThrow().getValue());
    }

    @Test
    void testShouldCheckThatCardIsUsedBySecondPLayer() {

        controller.pickComponent("Simone", 115);
        controller.rotateComponent("Simone", 115, 1);
        controller.insertComponent("Simone", 115, 1, 3, 0, true);


        controller.pickComponent("Davide", 114);
        controller.insertComponent("Davide", 114, 1, 3, 0, true);

        controller.pickComponent("Davide", 134);
        controller.insertComponent("Davide", 134, 1, 4, 0, true);

        controller.pickComponent("Davide", 5);
        controller.insertComponent("Davide", 5, 2, 4, 0, true);

        controller.setReady("Simone");
        controller.setReady("Davide");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);

        SlaversCard slaversCard = new SlaversCard(0, 2, false, 2, 5, 1, 1);
        board.getCardPile().clear();
        board.getCardPile().add(slaversCard);
        board.getCardPile().add(slaversCard);

        controller.chooseAlien("Simone", new HashMap<>(Map.of(52, AlienType.ENGINE)));

        controller.drawCard("Simone");

        controller.removeCrew("Simone", new ArrayList<>(List.of(32, 52)));

        controller.activateCannons("Davide", new ArrayList<>(List.of(5)), new ArrayList<>(List.of(134)));

        controller.getBoolean("Davide", true);

        assertEquals(1, p1.getShip().getCrew());
        assertEquals(50, p1.getCredits());
        assertEquals(2, p2.getShip().getCrew());
        assertEquals(45, p2.getCredits());
        assertEquals(15, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().orElseThrow().getValue());
        assertEquals(11, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p2)).findFirst().orElseThrow().getValue());
    }
}

