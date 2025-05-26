package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.utils.Meteor;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.game.Board;

import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.properties.DirectionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeteorSwarmCardTest {

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

        controller.pickComponent("Simone", 32);
        controller.insertComponent("Simone", 32, 2, 3, 0, true);

        controller.pickComponent("Simone", 132);
        controller.insertComponent("Simone", 132, 1, 1, 0, true);

        controller.pickComponent("Simone", 15);
        controller.rotateComponent("Simone", 15, 2);
        controller.insertComponent("Simone", 15, 1, 2, 0, true);

        controller.pickComponent("Simone", 36);
        controller.rotateComponent("Simone", 36, 2);
        controller.insertComponent("Simone", 36, 2, 1, 0, true);

        controller.pickComponent("Simone", 148);
        controller.insertComponent("Simone", 148, 2, 2, 0, true);


        controller.pickComponent("Davide", 33);
        controller.insertComponent("Davide", 33, 2, 3, 0, true);

        controller.pickComponent("Davide", 28);
        controller.insertComponent("Davide", 28, 2, 4, 0, true);


        controller.pickComponent("Tommaso", 34);
        controller.insertComponent("Tommaso", 34, 2, 3, 0, true);

        controller.pickComponent("Tommaso", 22);
        controller.insertComponent("Tommaso", 22, 2, 4, 0, true);


    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldCheckSmallMeteorsDoesNothingIfGoodAssembledShip() {

        controller.setReady("Simone");
        controller.setReady("Davide");
        controller.setReady("Tommaso");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);
        board.movePlayer(p3, 10);

        List<Meteor> meteors = new ArrayList<>();
        Meteor meteor1 = new Meteor(false, DirectionType.EAST);
        meteors.add(meteor1);

        MeteorSwarmCard meteorSwarmCard = new MeteorSwarmCard(2, false, meteors);
        board.getCardPile().clear();
        board.getCardPile().add(meteorSwarmCard);

        controller.drawCard("Simone");
        // set dice manually
        boolean finish = meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        assertEquals(2, p1.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(CannonComponent.class).size());
    }

    @Test
    void testShouldCheckSmallMeteorsBreakShipIfExposedConnectors() {

        controller.setReady("Simone");
        controller.setReady("Davide");
        controller.setReady("Tommaso");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);
        board.movePlayer(p3, 10);


        List<Meteor> meteors = new ArrayList<>();
        Meteor meteor1 = new Meteor(false, DirectionType.WEST);
        meteors.add(meteor1);

        MeteorSwarmCard meteorSwarmCard = new MeteorSwarmCard(2, false, meteors);
        board.getCardPile().clear();
        board.getCardPile().add(meteorSwarmCard);

        controller.drawCard("Simone");
        // set dice manually
        boolean finish = meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        assertEquals(2, p1.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(0, p1.getShip().getComponentByType(CannonComponent.class).size());
    }

    @Test
    void testShouldCheckSmallMeteorsDoesNothingIfShieldIsUsed() {

        controller.setReady("Simone");
        controller.setReady("Davide");
        controller.setReady("Tommaso");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);
        board.movePlayer(p3, 10);


        List<Meteor> meteors = new ArrayList<>();
        Meteor meteor1 = new Meteor(false, DirectionType.NORTH);
        meteors.add(meteor1);

        MeteorSwarmCard meteorSwarmCard = new MeteorSwarmCard(2, false, meteors);
        board.getCardPile().clear();
        board.getCardPile().add(meteorSwarmCard);

        controller.drawCard("Simone");
        // set dice manually
        boolean finish = meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.activateShield("Simone", 15);

        assertEquals(2, p1.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(CannonComponent.class).size());
    }

    @Test
    void testShouldCheckBigMeteorsBreakShipIfCannonIsNotUsed() {

        controller.setReady("Simone");
        controller.setReady("Davide");
        controller.setReady("Tommaso");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);
        board.movePlayer(p3, 10);


        List<Meteor> meteors = new ArrayList<>();
        Meteor meteor1 = new Meteor(true, DirectionType.EAST);
        meteors.add(meteor1);

        MeteorSwarmCard meteorSwarmCard = new MeteorSwarmCard(2, false, meteors);
        board.getCardPile().clear();
        board.getCardPile().add(meteorSwarmCard);

        controller.drawCard("Simone");

        boolean finish = meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        assertEquals(2, p1.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(0, p1.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(CannonComponent.class).size());
    }

    @Test
    void testShouldCheckBigMeteorsDoesNothingIfCannonIsUsed() {

        controller.setReady("Simone");
        controller.setReady("Davide");
        controller.setReady("Tommaso");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);
        board.movePlayer(p3, 10);


        List<Meteor> meteors = new ArrayList<>();
        Meteor meteor1 = new Meteor(true, DirectionType.NORTH);
        meteors.add(meteor1);

        MeteorSwarmCard meteorSwarmCard = new MeteorSwarmCard(2, false, meteors);
        board.getCardPile().clear();
        board.getCardPile().add(meteorSwarmCard);

        controller.drawCard("Simone");
        // set dice manually
        boolean finish = meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 5, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.activateCannons(p1.getUsername(), new ArrayList<>(List.of(15)), new ArrayList<>(List.of(132)));

        assertEquals(2, p1.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(CannonComponent.class).size());
    }

    @Test
    void testShouldCheckMeteorHitEveryPlayer() {

        controller.pickComponent("Davide", 43);
        controller.insertComponent("Davide", 43, 2, 1, 0,true);

        controller.pickComponent("Davide", 126);
        controller.insertComponent("Davide", 126, 1, 1, 0, true);

        controller.pickComponent("Davide", 12);
        controller.rotateComponent("Davide", 12, 2);
        controller.insertComponent("Davide", 12, 1, 2, 0, true);

        controller.pickComponent("Davide", 151);
        controller.insertComponent("Davide", 151, 2, 2, 0, true);


        controller.pickComponent("Tommaso", 128);
        controller.insertComponent("Tommaso", 128, 1, 1, 0 , true);

        controller.pickComponent("Tommaso", 14);
        controller.rotateComponent("Tommaso", 14, 3);
        controller.insertComponent("Tommaso", 14, 1, 2, 0,true);

        controller.pickComponent("Tommaso", 38);
        controller.rotateComponent("Tommaso", 38, 3);
        controller.insertComponent("Tommaso", 38, 2, 1, 0, true);

        controller.pickComponent("Tommaso", 150);
        controller.insertComponent("Tommaso", 150, 2, 2, 0, true);


        controller.setReady("Simone");
        controller.setReady("Davide");
        controller.setReady("Tommaso");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);
        board.movePlayer(p3, 10);

        List<Meteor> meteors = new ArrayList<>();
        Meteor meteor1 = new Meteor(false, DirectionType.EAST);
        Meteor meteor2 = new Meteor(false, DirectionType.NORTH);
        Meteor meteor3 = new Meteor(true, DirectionType.NORTH);

        meteors.add(meteor1);
        meteors.add(meteor2);
        meteors.add(meteor3);

        MeteorSwarmCard meteorSwarmCard = new MeteorSwarmCard(2, false, meteors);
        board.getCardPile().clear();
        board.getCardPile().add(meteorSwarmCard);


        controller.drawCard("Simone");
        // set dice manually
        boolean finish = meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        finish = meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.activateShield("Simone", 15);

        controller.activateShield("Davide", 12);

        controller.activateShield("Tommaso", 14);

        finish = meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 5, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.activateCannons("Simone", new ArrayList<>(List.of(15)), new ArrayList<>(List.of(132)));

        controller.activateCannons("Davide", new ArrayList<>(List.of(12)), new ArrayList<>(List.of(126)));

        controller.activateCannons("Tommaso", new ArrayList<>(List.of(14)), new ArrayList<>(List.of(128)));


        assertEquals(2, p1.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(CannonComponent.class).size());
        assertEquals(2, p2.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(1, p2.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p2.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(1, p2.getShip().getComponentByType(CannonComponent.class).size());
        assertEquals(1, p2.getShip().getComponentByType(CargoHoldsComponent.class).size());
        assertEquals(2, p3.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(1, p3.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p3.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(1, p3.getShip().getComponentByType(CannonComponent.class).size());
        assertEquals(1, p3.getShip().getComponentByType(CargoHoldsComponent.class).size());

    }

    @Test
    void testShouldCheckBigMeteorsDestroysShipAndUserChooseShipPart() {

        controller.setReady("Simone");
        controller.setReady("Davide");
        controller.setReady("Tommaso");

        board.movePlayer(p1, 9);
        board.movePlayer(p2, 9);
        board.movePlayer(p3, 10);


        List<Meteor> meteors = new ArrayList<>();
        Meteor meteor1 = new Meteor(true, DirectionType.NORTH);
        meteors.add(meteor1);
        meteors.add(meteor1);

        MeteorSwarmCard meteorSwarmCard = new MeteorSwarmCard(2, false, meteors);
        board.getCardPile().clear();
        board.getCardPile().add(meteorSwarmCard);

        controller.drawCard("Simone");
        // set dice manually
        boolean finish = meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        finish = meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.chooseShipPart("Simone", 0);


        assertEquals(1, p1.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(0, p1.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(0, p1.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(CannonComponent.class).size());
    }

}