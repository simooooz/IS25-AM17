package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OpenSpaceCardTest {

    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private PlayerData p3;
    private PlayerData p4;
    private GameController controller;
    private Board board;


    @BeforeEach
    void setUp() {

        usernames = new ArrayList<>();
        usernames.add("Simone");
        usernames.add("Davide");
        usernames.add("Tommaso");
        usernames.add("Giovanni");

        controller = new GameController(usernames, false);
        controller.startMatch();

        board = controller.getModel().getBoard();
        p1 = board.getPlayerEntityByUsername("Simone");
        p2 = board.getPlayerEntityByUsername("Davide");
        p3 = board.getPlayerEntityByUsername("Tommaso");
        p4 = board.getPlayerEntityByUsername("Giovanni");


        controller.pickComponent("Simone", 43);
        controller.rotateComponent("Simone",43, 1);
        controller.insertComponent("Simone", 43, 2, 2, 0, true);

        controller.pickComponent("Simone", 75);
        controller.insertComponent("Simone", 75, 3, 2, 0, true);

        controller.pickComponent("Simone", 32);
        controller.insertComponent("Simone", 32, 2, 3, 0, true);


        controller.pickComponent("Davide", 15);
        controller.rotateComponent("Davide",15, 1);
        controller.insertComponent("Davide", 15, 2, 2, 0, true);

        controller.pickComponent("Davide", 79);
        controller.insertComponent("Davide", 79, 3, 1, 0, true);

        controller.pickComponent("Davide", 44);
        controller.rotateComponent("Davide",44, 1);
        controller.insertComponent("Davide", 44, 3, 2, 0, true);

        controller.pickComponent("Davide", 91);
        controller.insertComponent("Davide", 91, 4, 2, 0, true);

        controller.pickComponent("Davide", 85);
        controller.insertComponent("Davide", 85, 3, 3, 0, true);

        controller.pickComponent("Davide", 33);
        controller.insertComponent("Davide", 33, 2, 3, 0, true);


        controller.pickComponent("Tommaso", 41);
        controller.insertComponent("Tommaso", 41, 2, 2, 0, true);

        controller.pickComponent("Tommaso", 77);
        controller.insertComponent("Tommaso", 77, 3, 2, 0, true);

        controller.pickComponent("Tommaso", 34);
        controller.insertComponent("Tommaso", 34, 2, 3, 0, true);


        controller.pickComponent("Giovanni", 6);
        controller.insertComponent("Giovanni", 6, 2, 2, 0, true);

        controller.pickComponent("Giovanni", 38);
        controller.insertComponent("Giovanni", 38, 3, 2, 0, true);

        controller.pickComponent("Giovanni", 35);
        controller.insertComponent("Giovanni", 35, 2, 3, 0, true);


        controller.setReady("Giovanni");
        controller.setReady("Tommaso");
        controller.setReady("Simone");
        controller.setReady("Davide");

        board.movePlayer(p4, 14);
        board.movePlayer(p3, 15);
        board.movePlayer(p1, 15);
        board.movePlayer(p2, 14);


        OpenSpaceCard openSpaceCard = new OpenSpaceCard(2, false);
        board.getCardPile().clear();
        board.getCardPile().add(openSpaceCard);

    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldMovePlayersCorrectly() {

        controller.drawCard("Giovanni");

        controller.activateEngines("Davide", new ArrayList<>(List.of(15)), new ArrayList<>(List.of(91)));

        assertEquals(19,
                board.getPlayers().stream()
                        .filter(entry -> entry.getKey().equals(p3))
                        .findFirst()
                        .orElseThrow()
                        .getValue());
        assertEquals(21,
                board.getPlayers().stream()
                        .filter(entry -> entry.getKey().equals(p2))
                        .findFirst()
                        .orElseThrow()
                        .getValue());
        assertEquals(17,
                board.getPlayers().stream()
                        .filter(entry -> entry.getKey().equals(p1))
                        .findFirst()
                        .orElseThrow()
                        .getValue());
        assertTrue(board.getStartingDeck().contains(p4));
    }
}
