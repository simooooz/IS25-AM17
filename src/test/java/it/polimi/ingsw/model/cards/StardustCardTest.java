package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StardustCardTest {

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


        controller.showComponent("Simone", 3);
        controller.pickComponent("Simone", 3);
        controller.insertComponent("Simone", 3, 1, 1);

        controller.showComponent("Simone", 40);
        controller.pickComponent("Simone", 40);
        controller.rotateComponent("Simone", 40, 1);
        controller.insertComponent("Simone", 40, 2, 1);

        controller.showComponent("Simone", 66);
        controller.pickComponent("Simone", 66);
        controller.rotateComponent("Simone", 66, 3);
        controller.insertComponent("Simone", 66, 1, 2);

        controller.showComponent("Simone", 52);
        controller.pickComponent("Simone", 52);
        controller.insertComponent("Simone", 52, 2, 2);

        controller.showComponent("Simone", 32);
        controller.pickComponent("Simone", 32);
        controller.insertComponent("Simone", 32, 2, 3);


        controller.showComponent("Davide", 5);
        controller.pickComponent("Davide", 5);
        controller.rotateComponent("Davide", 5, 3);
        controller.insertComponent("Davide", 5, 1, 1);

        controller.showComponent("Davide", 59);
        controller.pickComponent("Davide", 59);
        controller.insertComponent("Davide", 59, 2, 1);

        controller.showComponent("Davide", 132);
        controller.pickComponent("Davide", 132);
        controller.insertComponent("Davide", 132, 2, 2);

        controller.showComponent("Davide", 33);
        controller.pickComponent("Davide", 33);
        controller.insertComponent("Davide", 33, 2, 3);


        controller.showComponent("Tommaso", 34);
        controller.pickComponent("Tommaso", 34);
        controller.insertComponent("Tommaso", 34, 2, 3);

        controller.showComponent("Tommaso", 136);
        controller.pickComponent("Tommaso", 136);
        controller.insertComponent("Tommaso", 136, 2, 4);

        controller.showComponent("Tommaso", 156);
        controller.pickComponent("Tommaso", 156);
        controller.insertComponent("Tommaso", 156, 2, 5);

        controller.setReady("Tommaso");
        controller.setReady("Davide");
        controller.setReady("Simone");

        board.movePlayer(p3, 19);
        board.movePlayer(p2, 21);
        board.movePlayer(p1, 22);

    }

    @AfterEach
    void tearDown() { usernames.clear(); }

    @Test
    void testShouldCheckPlayersPosAfterCardEffect() {

        StardustCard stardustCard = new StardustCard(2, false);
        board.getCardPile().clear();
        board.getCardPile().add(stardustCard);

        // trigger card effect
        controller.drawCard("Tommaso");

        assertEquals(18,
                board.getPlayers().stream()
                        .filter(entry -> entry.getKey().equals(p1))
                        .findFirst()
                        .orElseThrow()
                        .getValue());
        assertEquals(17,
                board.getPlayers().stream()
                        .filter(entry -> entry.getKey().equals(p2))
                        .findFirst()
                        .orElseThrow()
                        .getValue());
        assertEquals(20,
                board.getPlayers().stream()
                        .filter(entry -> entry.getKey().equals(p3))
                        .findFirst()
                        .orElseThrow()
                        .getValue());

    }

}
