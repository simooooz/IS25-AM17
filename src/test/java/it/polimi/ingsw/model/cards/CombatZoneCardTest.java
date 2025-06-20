package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.GameController;

import it.polimi.ingsw.model.cards.utils.*;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;


class CombatZoneCardTest {
    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private PlayerData p3;
    private GameController controller;
    private Board board;
    private List<WarLine> damages;


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
    void testShouldCheckThatP1HasLessCannonPowerP2HasLessEnginePowerAndP3HasLessCrew() {

        controller.pickComponent("Simone", 45);
        controller.rotateComponent("Simone", 45, 2);
        controller.insertComponent("Simone", 45, 2, 2, 0, true);

        controller.pickComponent("Simone", 114);
        controller.insertComponent("Simone", 114, 2, 4, 0, true);

        controller.pickComponent("Simone", 108);
        controller.rotateComponent("Simone", 108, 1);
        controller.insertComponent("Simone", 108, 2, 5, 0, true);

        controller.pickComponent("Simone", 73);
        controller.insertComponent("Simone", 73, 3, 2, 0, true);

        controller.pickComponent("Simone", 93);
        controller.insertComponent("Simone", 93, 3, 3, 0, true);

        controller.pickComponent("Simone", 11);
        controller.insertComponent("Simone", 11, 3, 4, 0, true);

        controller.pickComponent("Simone", 69);
        controller.rotateComponent("Simone", 69, 1);
        controller.insertComponent("Simone", 69, 3, 5, 0, true);


        controller.pickComponent("Davide", 143);
        controller.rotateComponent("Davide", 143, 3);
        controller.insertComponent("Davide", 143, 1, 2, 0, true);

        controller.pickComponent("Davide", 43);
        controller.rotateComponent("Davide", 43, 1);
        controller.insertComponent("Davide", 43, 2, 2, 0, true);

        controller.pickComponent("Davide", 121);
        controller.insertComponent("Davide", 121, 2, 4, 0, true);

        controller.pickComponent("Davide", 134);
        controller.insertComponent("Davide", 134, 2, 5, 0, true);

        controller.pickComponent("Davide", 75);
        controller.rotateComponent("Davide", 75, 0);
        controller.insertComponent("Davide", 75, 3, 2, 0, true);

        controller.pickComponent("Davide", 3);
        controller.rotateComponent("Davide", 3, 0);
        controller.insertComponent("Davide", 3, 3, 3, 0, true);

        controller.pickComponent("Davide", 18);
        controller.rotateComponent("Davide", 18, 3);
        controller.insertComponent("Davide", 18, 3, 4, 0, true);

        controller.pickComponent("Davide", 60);
        controller.rotateComponent("Davide", 60, 0);
        controller.insertComponent("Davide", 60, 3, 5, 0, true);


        controller.pickComponent("Tommaso", 135);
        controller.rotateComponent("Tommaso", 135, 1);
        controller.insertComponent("Tommaso", 135, 2, 2, 0, true);

        controller.pickComponent("Tommaso", 115);
        controller.rotateComponent("Tommaso", 115, 0);
        controller.insertComponent("Tommaso", 115, 2, 4, 0, true);

        controller.pickComponent("Tommaso", 126);
        controller.rotateComponent("Tommaso", 126, 1);
        controller.insertComponent("Tommaso", 126, 2, 5, 0, true);

        controller.pickComponent("Tommaso", 96);
        controller.rotateComponent("Tommaso", 96, 0);
        controller.insertComponent("Tommaso", 96, 3, 3, 0, true);

        controller.pickComponent("Tommaso", 15);
        controller.rotateComponent("Tommaso", 15, 3);
        controller.insertComponent("Tommaso", 15, 3, 4, 0, true);

        controller.pickComponent("Tommaso", 30);
        controller.rotateComponent("Tommaso", 30, 0);
        controller.insertComponent("Tommaso", 30, 3, 5, 0, true);


        controller.setReady("Simone");
        controller.setReady("Davide");
        controller.setReady("Tommaso");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);
        board.movePlayer(p3, 10);


        PenaltyCombatZone penalty1 = new CountablePenaltyZone(2, MalusType.GOODS);
        PenaltyCombatZone penalty2 = new CountablePenaltyZone(3, MalusType.DAYS);
        PenaltyCombatZone penalty3 = new CountablePenaltyZone(2, MalusType.CREW);
        damages = new ArrayList<>();
        damages.add(new WarLine(CriteriaType.CREW, penalty1));
        damages.add(new WarLine(CriteriaType.ENGINE, penalty2));
        damages.add(new WarLine(CriteriaType.CANNON, penalty3));

        CombatZoneCard combatZoneCard = new CombatZoneCard(0, 2, false, damages);
        board.getCardPile().clear();
        board.getCardPile().add(combatZoneCard);
        board.getCardPile().add(combatZoneCard);

        ((CargoHoldsComponent) p3.getShip().getDashboard(3,5).orElseThrow()).loadGood(ColorType.BLUE, p3.getShip());
        ((CargoHoldsComponent) p3.getShip().getDashboard(3,5).orElseThrow()).loadGood(ColorType.GREEN, p3.getShip());
        ((CargoHoldsComponent) p3.getShip().getDashboard(3,5).orElseThrow()).loadGood(ColorType.YELLOW, p3.getShip());


        controller.chooseAlien("Davide", new HashMap<>(Map.of(43, AlienType.CANNON)));

        controller.drawCard("Simone");

        controller.updateGoods("Tommaso", new HashMap<>(Map.of(30, List.of(ColorType.BLUE))), new ArrayList<>());

        //Player 1 activates double engine
        controller.activateEngines(p1.getUsername(), new ArrayList<>(List.of(11)), new ArrayList<>(List.of(93)));

        //Player 3 activates double engine
        controller.activateEngines("Tommaso", new ArrayList<>(List.of(15)), new ArrayList<>(List.of(96)));

        //Player 3 activates double cannon
        controller.activateCannons("Tommaso", new ArrayList<>(), new ArrayList<>());

        // Player 1 indicates cabins where remove crew
        controller.removeCrew("Tommaso", new ArrayList<>(List.of(34, 34)));


    }


}