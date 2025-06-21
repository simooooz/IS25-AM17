package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.utils.CannonFire;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.common.model.enums.DirectionType.*;
import static org.junit.jupiter.api.Assertions.*;

class PiratesCardTest {
    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private PlayerData p3;
    private GameController controller;
    private Board board;
    private List<CannonFire> cannonFires;


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

        p1.setCredits(50);
        p2.setCredits(40);
        p3.setCredits(30);

    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldCheckThatP1DrawAutomaticallyP2DrawAndP3WinButNotRedeemReward() {

        controller.pickComponent("Simone", 155);
        controller.insertComponent("Simone", 155, 2, 4, 0, true);


        controller.pickComponent("Davide", 132);
        controller.insertComponent("Davide", 132, 1, 3, 0, true);

        controller.pickComponent("Davide", 121);
        controller.insertComponent("Davide", 121, 1, 4, 0, true);

        controller.pickComponent("Davide", 9);
        controller.insertComponent("Davide", 9, 2, 2, 0, true);


        controller.pickComponent("Tommaso", 107);
        controller.insertComponent("Tommaso", 107, 2, 2, 0, true);

        controller.pickComponent("Tommaso", 119);
        controller.insertComponent("Tommaso", 119, 2, 4, 0, true);


        controller.setReady("Simone");
        controller.setReady("Davide");
        controller.setReady("Tommaso");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);
        board.movePlayer(p3, 10);


        PiratesCard piratesCard = new PiratesCard(0, 2, false, 1, 4, 3, cannonFires);
        board.getCardPile().clear();
        board.getCardPile().add(piratesCard);
        board.getCardPile().add(piratesCard);


        controller.drawCard("Simone");

        controller.activateCannons("Davide", new ArrayList<>(), new ArrayList<>());

        controller.getBoolean("Tommaso", false);

        assertEquals(11, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p3)).findFirst().orElseThrow().getValue());
        assertEquals(2, p2.getShip().getComponentByType(BatteryComponent.class).getFirst().getBatteries());
    }

    @Test
    void testShouldCheckThatP1WinsAndRedeemRewards() {

        controller.pickComponent("Simone", 121);
        controller.insertComponent("Simone", 121, 1, 4, 0, true);

        controller.pickComponent("Simone", 15);
        controller.insertComponent("Simone", 15, 1, 3, 0, true);

        controller.pickComponent("Simone", 132);
        controller.rotateComponent("Simone", 132, 1);
        controller.insertComponent("Simone", 132, 1, 5, 0, true);

        controller.pickComponent("Simone", 126);
        controller.rotateComponent("Simone", 126, 3);
        controller.insertComponent("Simone", 126, 2, 2, 0, true);

        controller.pickComponent("Simone", 122);
        controller.rotateComponent("Simone", 122, 2);
        controller.insertComponent("Simone", 122, 2, 4, 0, true);


        controller.pickComponent("Davide", 144);
        controller.insertComponent("Davide", 144, 2, 4, 0, true);


        controller.pickComponent("Tommaso", 74);
        controller.insertComponent("Tommaso", 74, 3, 3, 0, true);

        controller.setReady("Simone");
        controller.setReady("Davide");
        controller.setReady("Tommaso");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);
        board.movePlayer(p3, 10);


        PiratesCard piratesCard = new PiratesCard(0, 2, false, 3, 5, 3, cannonFires);
        board.getCardPile().clear();
        board.getCardPile().add(piratesCard);
        board.getCardPile().add(piratesCard);
        StardustCard stardustCard = new StardustCard(0, 2, false);
        board.getCardPile().add(stardustCard);

        controller.drawCard("Simone");

        controller.activateCannons("Simone", new ArrayList<>(List.of(15, 15)), new ArrayList<>(List.of(126, 132)));

        controller.getBoolean("Simone", true);

        assertEquals(1, p1.getShip().getComponentByType(BatteryComponent.class).getFirst().getBatteries());
        assertEquals(10, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().orElseThrow().getValue());
        assertEquals(55, p1.getCredits());
        assertEquals(12, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p2)).findFirst().orElseThrow().getValue());
    }

    @Test
    void testShouldCheckThatP1andP2LoseAndP3Wins() {

        controller.pickComponent("Simone", 15);
        controller.rotateComponent("Simone", 15, 2);
        controller.insertComponent("Simone", 15, 1, 4, 0, true);

        controller.pickComponent("Simone", 132);
        controller.insertComponent("Simone", 132, 1, 3, 0, true);

        controller.pickComponent("Simone", 155);
        controller.rotateComponent("Simone", 155, 3);
        controller.insertComponent("Simone", 155, 2, 2, 0, true);

        controller.pickComponent("Simone", 150);
        controller.rotateComponent("Simone", 150, 3);
        controller.insertComponent("Simone", 150, 3, 2, 0, true);


        controller.pickComponent("Davide", 74);
        controller.insertComponent("Davide", 74, 3, 3, 0, true);


        controller.pickComponent("Tommaso", 9);
        controller.insertComponent("Tommaso", 9, 2, 2, 0, true);

        controller.pickComponent("Tommaso", 121);
        controller.insertComponent("Tommaso", 121, 1, 3, 0, true);

        controller.pickComponent("Tommaso", 126);
        controller.rotateComponent("Tommaso", 126, 1);
        controller.insertComponent("Tommaso", 126, 1, 4, 0, true);

        controller.setReady("Simone");
        controller.setReady("Davide");
        controller.setReady("Tommaso");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);
        board.movePlayer(p3, 10);


        CannonFire c1 = new CannonFire(false, NORTH);
        CannonFire c2 = new CannonFire(true, EAST);
        cannonFires = new ArrayList<>(List.of(c1, c2));
        PiratesCard piratesCard = new PiratesCard(0, 2, false, 1, 5, 3, cannonFires);
        board.getCardPile().clear();
        board.getCardPile().add(piratesCard);
        board.getCardPile().add(piratesCard);


        controller.drawCard("Simone");

        controller.activateCannons("Simone", new ArrayList<>(), new ArrayList<>());

        controller.activateCannons("Tommaso", new ArrayList<>(List.of(9)), new ArrayList<>(List.of(126)));

        controller.getBoolean("Tommaso", false);



        boolean finish = piratesCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.activateShield("Simone", 15);

        finish = piratesCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 8, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        assertEquals(Optional.empty(), p1.getShip().getDashboard(3, 4));
        assertEquals(1, p3.getShip().getComponentByType(BatteryComponent.class).getFirst().getBatteries());
        assertEquals(30, p3.getCredits());
        assertEquals(11, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p3)).findFirst().orElseThrow().getValue());
    }

}