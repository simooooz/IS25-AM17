package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EpidemicCardTest {

    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private GameController controller;


    @BeforeEach
    void setUp() {

        usernames = new ArrayList<>();
        usernames.add("Simone");
        usernames.add("Davide");

        controller = new GameController(usernames, false);
        controller.startMatch();

        Board board = controller.getModel().getBoard();
        p1 = board.getPlayerEntityByUsername("Simone");
        p2 = board.getPlayerEntityByUsername("Davide");


        EpidemicCard epidemicCard = new EpidemicCard(0, 2, false);
        board.getCardPile().clear();
        board.getCardPile().add(epidemicCard);

    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldReduceAliensInTwoAdjacentCabinsAndHumansInThreeAdjacentCabins() {

        controller.pickComponent("Simone", 38);
        controller.rotateComponent("Simone", 38, 2);
        controller.insertComponent("Simone", 38, 1, 1, 0, true);

        controller.pickComponent("Simone", 50);
        controller.rotateComponent("Simone", 50, 3);
        controller.insertComponent("Simone", 50, 1, 2, 0, true);

        controller.pickComponent("Simone", 142);
        controller.rotateComponent("Simone", 142, 2);
        controller.insertComponent("Simone", 142, 1, 3, 0, true);

        controller.pickComponent("Simone", 139);
        controller.rotateComponent("Simone", 139, 1);
        controller.insertComponent("Simone", 139, 2, 1, 0, true);


        controller.pickComponent("Davide", 52);
        controller.rotateComponent("Davide", 52, 2);
        controller.insertComponent("Davide", 52, 1, 1, 0, true);

        controller.pickComponent("Davide", 37);
        controller.rotateComponent("Davide", 37, 1);
        controller.insertComponent("Davide", 37, 1, 2, 0, true);
        ((CabinComponent) p2.getShip().getDashboard(1, 2).orElseThrow()).setHumans(1, p2.getShip());

        controller.pickComponent("Davide", 36);
        controller.rotateComponent("Davide", 36, 2);
        controller.insertComponent("Davide", 36, 2, 1, 0, true);

        controller.pickComponent("Davide", 57);
        controller.insertComponent("Davide", 57, 2, 2, 0, true);


        controller.setReady("Simone");
        controller.setReady("Davide");

        controller.chooseAlien("Simone", new HashMap<>(Map.of(38, AlienType.ENGINE, 50, AlienType.CANNON)));

        controller.drawCard("Simone");

        assertEquals(2, p1.getShip().getCrew());
        assertFalse(p1.getShip().getEngineAlien());
        assertFalse(p1.getShip().getCannonAlien());
        assertFalse(((CabinComponent)p1.getShip().getDashboard(1, 1).orElseThrow()).getAlien().isPresent());
        assertEquals(4, p2.getShip().getCrew());
        assertEquals(1, ((CabinComponent)p2.getShip().getDashboard(1, 1).orElseThrow()).getHumans());
        assertEquals(0, ((CabinComponent)p2.getShip().getDashboard(1, 2).orElseThrow()).getHumans());

    }


    @Test
    void testShouldNotReduceHumansAndAliensInNonAdjacentCabins() {

        controller.pickComponent("Simone", 46);
        controller.rotateComponent("Simone", 46, 2);
        controller.insertComponent("Simone", 46, 2, 1, 0, true);

        controller.pickComponent("Simone", 48);
        controller.rotateComponent("Simone", 48, 3);
        controller.insertComponent("Simone", 48, 1, 2, 0, true);

        controller.pickComponent("Simone", 63);
        controller.rotateComponent("Simone", 63, 1);
        controller.insertComponent("Simone", 63, 2, 2, 0, true);


        controller.pickComponent("Davide", 44);
        controller.rotateComponent("Davide", 44, 1);
        controller.insertComponent("Davide", 44, 1, 2, 0, true);

        controller.pickComponent("Davide", 40);
        controller.rotateComponent("Davide", 40, 1);
        controller.insertComponent("Davide", 40, 2, 1, 0, true);

        controller.pickComponent("Davide", 141);
        controller.insertComponent("Davide", 141, 2, 2, 0, true);


        controller.setReady("Simone");
        controller.setReady("Davide");

        controller.chooseAlien("Davide", new HashMap<>(Map.of(44, AlienType.CANNON)));

        controller.drawCard("Simone");

        assertEquals(6, p1.getShip().getCrew());
        assertEquals(2, ((CabinComponent)p1.getShip().getDashboard(1, 2).orElseThrow()).getHumans());
        assertEquals(2, ((CabinComponent)p1.getShip().getDashboard(2, 1).orElseThrow()).getHumans());
        assertEquals(5, p2.getShip().getCrew());
        assertEquals(2, ((CabinComponent)p2.getShip().getDashboard(2,1).orElseThrow()).getHumans());
        assertTrue(p2.getShip().getCannonAlien());
        assertFalse(((CabinComponent)p1.getShip().getDashboard(1, 2).orElseThrow()).getAlien().isPresent());


    }


}