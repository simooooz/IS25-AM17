package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.utils.*;

import it.polimi.ingsw.model.exceptions.CabinComponentNotValidException;
import it.polimi.ingsw.model.exceptions.IllegalStateException;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;

import static it.polimi.ingsw.model.properties.DirectionType.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameSimulationTest {
    private GameController controller;

    @BeforeEach
    void setUp() {
        List<String> usernames = new ArrayList<>(List.of("Tommaso", "Simone", "Davide"));
        controller = new GameController(usernames, false);
        controller.startMatch();
    }

    @Test
    public void testFirstCompleteGame(){

        // Building Ships

        controller.showComponent("Tommaso", 102);
        controller.pickComponent("Tommaso", 102);
        controller.insertComponent("Tommaso", 102, 0, 2);

        controller.showComponent("Tommaso", 16);
        controller.pickComponent("Tommaso", 16);
        controller.rotateComponent("Tommaso", 16, 3);
        controller.insertComponent("Tommaso", 16, 0, 4);

        controller.showComponent("Tommaso", 113);
        controller.pickComponent("Tommaso", 113);
        controller.rotateComponent("Tommaso", 113, 3);
        controller.insertComponent("Tommaso", 113, 1, 1);

        controller.showComponent("Tommaso", 60);
        controller.pickComponent("Tommaso", 60);
        controller.rotateComponent("Tommaso", 60, 3);
        controller.insertComponent("Tommaso", 60, 1, 2);

        controller.showComponent("Tommaso", 131);
        controller.pickComponent("Tommaso", 131);
        controller.insertComponent("Tommaso", 131, 1, 3);

        controller.showComponent("Tommaso", 116);
        controller.pickComponent("Tommaso", 116);
        controller.rotateComponent("Tommaso", 116, 1);
        controller.insertComponent("Tommaso", 116, 1, 4);

        controller.showComponent("Tommaso", 125);
        controller.pickComponent("Tommaso", 125);
        controller.insertComponent("Tommaso", 125, 1, 5);

        controller.showComponent("Tommaso", 134);
        controller.pickComponent("Tommaso", 134);
        controller.rotateComponent("Tommaso", 134, 3);
        controller.insertComponent("Tommaso", 134, 2, 0);

        controller.showComponent("Tommaso", 55);
        controller.pickComponent("Tommaso", 55);
        controller.insertComponent("Tommaso", 55, 2, 1);

        controller.showComponent("Tommaso", 38);
        controller.pickComponent("Tommaso", 38);
        controller.rotateComponent("Tommaso", 38, 1);
        controller.insertComponent("Tommaso", 38, 2, 2);

        controller.showComponent("Tommaso", 32);
        controller.pickComponent("Tommaso", 32);
        controller.insertComponent("Tommaso", 32, 2, 3);

        controller.showComponent("Tommaso", 63);
        controller.pickComponent("Tommaso", 63);
        controller.insertComponent("Tommaso", 63, 2, 4);

        controller.showComponent("Tommaso", 24);
        controller.pickComponent("Tommaso", 24);
        controller.insertComponent("Tommaso", 24, 2, 5);

        controller.showComponent("Tommaso", 25);
        controller.pickComponent("Tommaso", 25);
        controller.insertComponent("Tommaso", 25, 2, 6);

        controller.showComponent("Tommaso", 9);
        controller.pickComponent("Tommaso", 9);
        controller.rotateComponent("Tommaso", 9, 1);
        controller.insertComponent("Tommaso", 9, 3, 0);

        controller.showComponent("Tommaso", 152);
        controller.pickComponent("Tommaso", 152);
        controller.rotateComponent("Tommaso", 152, 2);
        controller.insertComponent("Tommaso", 152, 3, 1);

        controller.showComponent("Tommaso", 5);
        controller.pickComponent("Tommaso", 5);
        controller.insertComponent("Tommaso", 5, 3, 2);

        controller.showComponent("Tommaso", 92);
        controller.pickComponent("Tommaso", 92);
        controller.insertComponent("Tommaso", 92, 3, 3);

        controller.showComponent("Tommaso", 146);
        controller.pickComponent("Tommaso", 146);
        controller.rotateComponent("Tommaso", 146, 1);
        controller.insertComponent("Tommaso", 146, 3, 4);

        controller.showComponent("Tommaso", 62);
        controller.pickComponent("Tommaso", 62);
        controller.rotateComponent("Tommaso", 62, 1);
        controller.insertComponent("Tommaso", 62, 3, 5);

        controller.showComponent("Tommaso", 77);
        controller.pickComponent("Tommaso", 77);
        controller.insertComponent("Tommaso", 77, 4, 0);

        controller.showComponent("Tommaso", 46);
        controller.pickComponent("Tommaso", 46);
        controller.rotateComponent("Tommaso", 46, 2);
        controller.insertComponent("Tommaso", 46, 4, 1);

        controller.showComponent("Tommaso", 137);
        controller.pickComponent("Tommaso", 137);
        controller.insertComponent("Tommaso", 137, 4, 2);

        controller.showComponent("Tommaso", 104);
        controller.pickComponent("Tommaso", 104);
        controller.rotateComponent("Tommaso", 104, 3);
        controller.insertComponent("Tommaso", 104, 4, 4);

        controller.showComponent("Tommaso", 36);
        controller.pickComponent("Tommaso", 36);
        controller.rotateComponent("Tommaso", 36, 2);
        controller.insertComponent("Tommaso", 36, 4, 5);

        controller.showComponent("Tommaso", 100);
        controller.pickComponent("Tommaso", 100);
        controller.rotateComponent("Tommaso", 100, 1);
        controller.insertComponent("Tommaso", 100, 4, 6);


        controller.showComponent("Simone", 127);
        controller.pickComponent("Simone", 127);
        controller.rotateComponent("Simone", 127, 1);
        controller.insertComponent("Simone", 127, 0, 2);

        controller.showComponent("Simone", 108);
        controller.pickComponent("Simone", 108);
        controller.insertComponent("Simone", 108, 0, 4);

        controller.showComponent("Simone", 143);
        controller.pickComponent("Simone", 143);
        controller.rotateComponent("Simone", 143, 3);
        controller.insertComponent("Simone", 143, 1, 1);

        controller.showComponent("Simone", 64);
        controller.pickComponent("Simone", 64);
        controller.rotateComponent("Simone", 64, 1);
        controller.insertComponent("Simone", 64, 1, 2);

        controller.showComponent("Simone", 84);
        controller.pickComponent("Simone", 84);
        controller.rotateComponent("Simone", 84, 2);
        controller.insertComponent("Simone", 84, 1, 3);

        controller.showComponent("Simone", 3);
        controller.pickComponent("Simone", 3);
        controller.rotateComponent("Simone", 3, 2);
        controller.insertComponent("Simone", 3, 1, 4);

        controller.showComponent("Simone", 28);
        controller.pickComponent("Simone", 28);
        controller.rotateComponent("Simone", 28, 3);
        controller.insertComponent("Simone", 28, 1, 5);

        controller.showComponent("Simone", 149);
        controller.pickComponent("Simone", 149);
        controller.rotateComponent("Simone", 149, 3);
        controller.insertComponent("Simone", 149, 2, 0);

        controller.showComponent("Simone", 51);
        controller.pickComponent("Simone", 51);
        controller.rotateComponent("Simone", 51, 1);
        controller.insertComponent("Simone", 51, 2, 1);

        controller.showComponent("Simone", 56);
        controller.pickComponent("Simone", 56);
        controller.insertComponent("Simone", 56, 2, 2);

        controller.showComponent("Simone", 33);
        controller.pickComponent("Simone", 33);
        controller.insertComponent("Simone", 33, 2, 3);

        controller.showComponent("Simone", 58);
        controller.pickComponent("Simone", 58);
        controller.rotateComponent("Simone", 58, 2);
        controller.insertComponent("Simone", 58, 2, 4);

        controller.showComponent("Simone", 150);
        controller.pickComponent("Simone", 150);
        controller.rotateComponent("Simone", 150, 1);
        controller.insertComponent("Simone", 150, 2, 5);

        controller.showComponent("Simone", 103);
        controller.pickComponent("Simone", 103);
        controller.insertComponent("Simone", 103, 2, 6);

        controller.showComponent("Simone", 14);
        controller.pickComponent("Simone", 14);
        controller.insertComponent("Simone", 14, 3, 0);

        controller.showComponent("Simone", 79);
        controller.pickComponent("Simone", 79);
        controller.insertComponent("Simone", 79, 3, 1);

        controller.showComponent("Simone", 17);
        controller.pickComponent("Simone", 17);
        controller.rotateComponent("Simone", 17, 3);
        controller.insertComponent("Simone", 17, 3, 2);

        controller.showComponent("Simone", 85);
        controller.pickComponent("Simone", 85);
        controller.insertComponent("Simone", 85, 3, 3);

        controller.showComponent("Simone", 43);
        controller.pickComponent("Simone", 43);
        controller.rotateComponent("Simone", 43, 1);
        controller.insertComponent("Simone", 43, 3, 4);

        controller.showComponent("Simone", 53);
        controller.pickComponent("Simone", 53);
        controller.insertComponent("Simone", 53, 3, 5);

        controller.showComponent("Simone", 97);
        controller.pickComponent("Simone", 97);
        controller.insertComponent("Simone", 97, 3, 6);

        controller.showComponent("Simone", 45);
        controller.pickComponent("Simone", 45);
        controller.rotateComponent("Simone", 45, 1);
        controller.insertComponent("Simone", 45, 4, 4);

        controller.showComponent("Simone", 67);
        controller.pickComponent("Simone", 67);
        controller.insertComponent("Simone", 67, 4, 5);

        controller.showComponent("Simone", 90);
        controller.reserveComponent("Simone", 90);


        controller.showComponent("Davide", 118);
        controller.pickComponent("Davide", 118);
        controller.insertComponent("Davide", 118, 0, 2);

        controller.showComponent("Davide", 126);
        controller.pickComponent("Davide", 126);
        controller.insertComponent("Davide", 126, 0, 4);

        controller.showComponent("Davide", 136);
        controller.pickComponent("Davide", 136);
        controller.rotateComponent("Davide", 136, 1);
        controller.insertComponent("Davide", 136, 1, 1);

        controller.showComponent("Davide", 44);
        controller.pickComponent("Davide", 44);
        controller.rotateComponent("Davide", 44, 3);
        controller.insertComponent("Davide", 44, 1, 2);

        controller.showComponent("Davide", 61);
        controller.pickComponent("Davide", 61);
        controller.insertComponent("Davide", 61, 1, 3);

        controller.showComponent("Davide", 1);
        controller.pickComponent("Davide", 1);
        controller.rotateComponent("Davide", 1, 3);
        controller.insertComponent("Davide", 1, 1, 4);

        controller.showComponent("Davide", 133);
        controller.pickComponent("Davide", 133);
        controller.insertComponent("Davide", 133, 1, 5);

        controller.showComponent("Davide", 114);
        controller.pickComponent("Davide", 114);
        controller.insertComponent("Davide", 114, 2, 0);

        controller.showComponent("Davide", 37);
        controller.pickComponent("Davide", 37);
        controller.rotateComponent("Davide", 37, 1);
        controller.insertComponent("Davide", 37, 2, 1);

        controller.showComponent("Davide", 148);
        controller.pickComponent("Davide", 148);
        controller.rotateComponent("Davide", 148, 2);
        controller.insertComponent("Davide", 148, 2, 2);

        controller.showComponent("Davide", 34);
        controller.pickComponent("Davide", 34);
        controller.insertComponent("Davide", 34, 2, 3);

        controller.showComponent("Davide", 142);
        controller.pickComponent("Davide", 142);
        controller.rotateComponent("Davide", 142, 2);
        controller.insertComponent("Davide", 142, 2, 4);

        controller.showComponent("Davide", 39);
        controller.pickComponent("Davide", 39);
        controller.rotateComponent("Davide", 39, 3);
        controller.insertComponent("Davide", 39, 2, 5);

        controller.showComponent("Davide", 12);
        controller.pickComponent("Davide", 12);
        controller.insertComponent("Davide", 12, 3, 0);

        controller.showComponent("Davide", 41);
        controller.pickComponent("Davide", 41);
        controller.insertComponent("Davide", 41, 3, 1);

        controller.showComponent("Davide", 18);
        controller.pickComponent("Davide", 18);
        controller.rotateComponent("Davide", 18, 2);
        controller.insertComponent("Davide", 18, 3, 2);

        controller.showComponent("Davide", 95);
        controller.pickComponent("Davide", 95);
        controller.insertComponent("Davide", 95, 3, 3);

        controller.showComponent("Davide", 151);
        controller.pickComponent("Davide", 151);
        controller.rotateComponent("Davide", 151, 3);
        controller.insertComponent("Davide", 151, 3, 4);

        controller.showComponent("Davide", 30);
        controller.pickComponent("Davide", 30);
        controller.insertComponent("Davide", 30, 3, 5);

        controller.showComponent("Davide", 75);
        controller.pickComponent("Davide", 75);
        controller.insertComponent("Davide", 75, 4, 0);

        controller.showComponent("Davide", 94);
        controller.pickComponent("Davide", 94);
        controller.insertComponent("Davide", 94, 4, 1);

        controller.showComponent("Davide", 81);
        controller.pickComponent("Davide", 81);
        controller.insertComponent("Davide", 81, 4, 2);

        controller.showComponent("Davide", 10);
        controller.pickComponent("Davide", 10);
        controller.rotateComponent("Davide", 10, 1);
        controller.insertComponent("Davide", 10, 4, 4);

        controller.showComponent("Davide", 96);
        controller.pickComponent("Davide", 96);
        controller.insertComponent("Davide", 96, 4, 5);

        controller.showComponent("Davide", 87);
        controller.pickComponent("Davide", 87);
        controller.insertComponent("Davide", 87, 4, 6);

        controller.showComponent("Davide", 72);
        controller.reserveComponent("Davide", 72);

        controller.showComponent("Davide", 83);
        controller.reserveComponent("Davide", 83);


        controller.setReady("Tommaso");
        controller.setReady("Simone");
        controller.setReady("Davide");


        // Player 1 Ship Check

        List<Integer> toRemoveTilesIDs = new ArrayList<>();
        toRemoveTilesIDs.add(125);

        controller.checkShip("Tommaso", toRemoveTilesIDs);

        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(1,5).isEmpty());
        assertEquals(2, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().countExposedConnectors());


        // Player 2 Ship Check

        toRemoveTilesIDs.clear();
        toRemoveTilesIDs.add(84);
        toRemoveTilesIDs.add(17);

        controller.checkShip("Simone", toRemoveTilesIDs);

        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getDashboard(1,3).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getDashboard(3,2).isEmpty());
        assertEquals(7, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().countExposedConnectors());

        assertEquals(4, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().countExposedConnectors());


        // Player 3 Ship Check

        toRemoveTilesIDs.clear();
        toRemoveTilesIDs.add(87);

        Exception exception1 = assertThrows(IllegalStateException.class, () -> controller.checkShip("Davide", toRemoveTilesIDs));
        assertEquals("State is not CHECKING", exception1.getMessage());


        //Alien management

        Exception exception2 = assertThrows(IllegalStateException.class, () -> controller.chooseAlien("Simone", new HashMap<>()));
        assertEquals("State is not WAIT_ALIEN", exception2.getMessage());

        controller.chooseAlien("Tommaso", new HashMap<>(Map.of(46, AlienType.ENGINE)));

        controller.chooseAlien("Simone", new HashMap<>(Map.of(51, AlienType.CANNON)));

        Exception exception3 = assertThrows(CabinComponentNotValidException.class, () -> controller.chooseAlien("Davide", new HashMap<>(Map.of(41, AlienType.CANNON))));
        assertEquals("Alien CANNON is not compatible with this cabin", exception3.getMessage());

        controller.chooseAlien("Davide", new HashMap<>(Map.of(39, AlienType.CANNON)));

        // Card Deck Initialization

        List<Card> cardDeck = controller.getModel().getBoard().getCardPile();
        cardDeck.clear();
        SlaversCard card1 = new SlaversCard(2, false, 4, 8, 2, 7);
        cardDeck.add(card1);
        AbandonedShipCard card2 = new AbandonedShipCard(1, true, 3, 4, 1);
        cardDeck.add(card2);
        AbandonedStationCard card3 = new AbandonedStationCard(1, true, 5, 1, new HashMap<>(Map.of(ColorType.YELLOW, 1, ColorType.GREEN, 1)));
        cardDeck.add(card3);
        PenaltyCombatZone penalty1 = new CountablePenaltyZone(4, MalusType.DAYS);
        PenaltyCombatZone penalty2 = new CountablePenaltyZone(3, MalusType.GOODS);
        PenaltyCombatZone penalty3 = new CannonFirePenaltyCombatZone(new ArrayList<>(List.of(new CannonFire(false, NORTH), new CannonFire(false, WEST), new CannonFire(false, EAST), new CannonFire(true, SOUTH))));
        List<SimpleEntry<CriteriaType, PenaltyCombatZone>> damages = new ArrayList<>();
        damages.add(new AbstractMap.SimpleEntry<>(CriteriaType.CANNON, penalty1));
        damages.add(new AbstractMap.SimpleEntry<>(CriteriaType.ENGINE, penalty2));
        damages.add(new AbstractMap.SimpleEntry<>(CriteriaType.CREW, penalty3));
        CombatZoneCard card4 = new CombatZoneCard(2, false, damages);
        cardDeck.add(card4);
        MeteorSwarmCard card5 = new MeteorSwarmCard(2, false, new ArrayList<>(List.of(new Meteor(false, NORTH), new Meteor(false, NORTH), new Meteor(true, WEST), new Meteor(false, WEST), new Meteor(false, WEST))));
        cardDeck.add(card5);
        Planet p1 = new Planet(new HashMap<>(Map.of(ColorType.GREEN, 2)));
        Planet p2 = new Planet(new HashMap<>(Map.of(ColorType.YELLOW, 1)));
        Planet p3 = new Planet(new HashMap<>(Map.of(ColorType.BLUE, 3)));
        PlanetCard card6 = new PlanetCard(1, false, new ArrayList<>(List.of(p1, p2, p3)), 1);
        cardDeck.add(card6);
        SmugglersCard card7 = new SmugglersCard(2, false, 8, 3, new HashMap<>(Map.of(ColorType.RED, 1, ColorType.YELLOW, 2)),1);
        cardDeck.add(card7);
        MeteorSwarmCard card8 = new MeteorSwarmCard(2, false, new ArrayList<>(List.of(new Meteor(true, NORTH), new Meteor(true, NORTH), new Meteor(false, SOUTH), new Meteor(false, SOUTH))));
        cardDeck.add(card8);
        OpenSpaceCard card9 = new OpenSpaceCard(2, false);
        cardDeck.add(card9);
        p1 = new Planet(new HashMap<>(Map.of(ColorType.GREEN, 4)));
        p2 = new Planet(new HashMap<>(Map.of(ColorType.YELLOW, 2)));
        p3 = new Planet(new HashMap<>(Map.of(ColorType.BLUE, 4)));
        PlanetCard card10 = new PlanetCard(1, false, new ArrayList<>(List.of(p1, p2, p3)), 3);
        cardDeck.add(card10);
        StardustCard card11 = new StardustCard(1, true);
        cardDeck.add(card11);
        PiratesCard card12 = new PiratesCard(2, false, 6, 7, 2, new ArrayList<>(List.of(new CannonFire(true, NORTH), new CannonFire(false, NORTH), new CannonFire(true, NORTH))));
        cardDeck.add(card12);

        // Card 1
        controller.drawCard("Tommaso");

        controller.removeCrew("Tommaso", new ArrayList<>(List.of(36, 36, 38, 32)));
        assertEquals(3, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getCrew());

        controller.removeCrew("Simone", new ArrayList<>(List.of(33, 43, 45, 45)));
        assertEquals(3, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getCrew());

        controller.activateCannons("Davide", new ArrayList<>(List.of(12, 12)), new ArrayList<>(List.of(126, 133)));
        assertEquals(5, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getBatteries());

        controller.getBoolean("Davide", true);
        assertEquals(8, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getCredits());
        assertEquals(-1, controller.getModel().getBoard().getPlayers().get(2).getValue());


        // Card 2
        controller.drawCard("Tommaso");

        controller.getBoolean("Tommaso", false);

        controller.getBoolean("Simone", false);

        controller.getBoolean("Davide", true);
        controller.removeCrew("Davide", new ArrayList<>(List.of(37, 44, 34)));
        assertEquals(6, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getCrew());
        assertEquals(12, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getCredits());

        // Card 3
        controller.drawCard("Tommaso");

        controller.getBoolean("Davide", false);

        // Card 4
        controller.drawCard("Tommaso");

        // First War Line
        controller.activateCannons("Tommaso", new ArrayList<>(List.of(16, 16)), new ArrayList<>(List.of(134, 131)));
        assertEquals(5, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getBatteries());

        controller.activateCannons("Simone", new ArrayList<>(), new ArrayList<>());
        assertEquals(5, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getBatteries());

        assertEquals(-2, controller.getModel().getBoard().getPlayers().get(2).getValue());

        // Second War Line
        controller.activateEngines("Tommaso", new ArrayList<>(List.of(16)), new ArrayList<>(List.of(77)));
        assertEquals(4, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getBatteries());

        controller.activateEngines("Simone", new ArrayList<>(), new ArrayList<>());
        assertEquals(5, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getBatteries());

        controller.updateGoods("Simone", new HashMap<>(), new ArrayList<>(List.of(14, 14, 3)));
        assertEquals(2, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getBatteries());

        // Third War Line
        // First Cannon Fire
        boolean finish = card4.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 9, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.chooseShipPart("Tommaso", 0);

        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(2, 5).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(2, 6).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(3, 5).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(3, 6).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(3, 7).isEmpty());

        //Second Cannon Fire
        finish = card4.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 7, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.activateShield("Tommaso", 5);

        assertEquals(3, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getBatteries());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(2, 0).isPresent());

        //Third Cannon Fire
        finish = card4.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.activateShield("Tommaso", null);

        controller.chooseShipPart("Tommaso", 0);

        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(0, 4).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(1, 4).isEmpty());

        //Fourth Cannon Fire
        finish = card4.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 10, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        // Card 5
        controller.drawCard("Tommaso");

        //First Meteor
        finish = card5.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 7, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.activateShield("Simone", 14);

        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getDashboard(2, 3).isPresent());

        //Second Meteor
        finish = card5.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 5, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.activateShield("Davide", 10);

        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getDashboard(1, 1).isPresent());

        //Third Meteor
        finish = card5.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 9, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.chooseShipPart("Simone", 0);

        assertFalse(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(4, 0).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getDashboard(4, 4).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getDashboard(4, 5).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getDashboard(4, 0).isEmpty());


        //Fourth Meteor
        finish = card5.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 7, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.activateShield("Davide", 1);

        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getDashboard(2, 0).isPresent());

        //Fifth Meteor
        finish = card5.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 3, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        // Card 6
        controller.drawCard("Tommaso");

        controller.getIndex("Tommaso", 0);
        controller.getIndex("Simone", 2);
        controller.getIndex("Davide", 1);

        List<ColorType> good = new ArrayList<>(List.of(ColorType.GREEN));
        controller.updateGoods("Tommaso", new HashMap<>(Map.of(63, good)), new ArrayList<>());

        assertEquals(1, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getGoods().get(ColorType.GREEN));

        good = new ArrayList<>(List.of(ColorType.BLUE, ColorType.BLUE));
        List<ColorType> good2 = new ArrayList<>(List.of(ColorType.BLUE));
        controller.updateGoods("Simone", new HashMap<>(Map.of(28, good, 64, good2)), new ArrayList<>());

        assertEquals(3, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getGoods().get(ColorType.BLUE));

        good = new ArrayList<>(List.of(ColorType.YELLOW));
        controller.updateGoods("Davide", new HashMap<>(Map.of(18, good)), new ArrayList<>());

        assertEquals(1, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getGoods().get(ColorType.YELLOW));

        assertEquals(5, controller.getModel().getBoard().getPlayers().get(0).getValue());
        assertEquals(-2, controller.getModel().getBoard().getPlayers().get(1).getValue());
        assertEquals(-3, controller.getModel().getBoard().getPlayers().get(2).getValue());


        // Card 7
        controller.drawCard("Tommaso");

        good = new ArrayList<>();
        controller.updateGoods("Tommaso", new HashMap<>(Map.of(63, good)), new ArrayList<>(List.of(9, 5)));
        controller.updateGoods("Simone", new HashMap<>(Map.of(28, good, 64, good)), new ArrayList<>());


        controller.activateCannons("Davide", new ArrayList<>(List.of(1, 10)), new ArrayList<>(List.of(126, 133)));
        assertEquals(-2, controller.getModel().getBoard().getPlayers().get(1).getValue());


        assertEquals(1, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getBatteries());
        assertEquals(0, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getGoods().get(ColorType.GREEN));
        assertEquals(0, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getGoods().get(ColorType.BLUE));

        // Card 8

        controller.drawCard("Tommaso");

        finish = card8.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 5, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(1, 1).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getDashboard(1, 1).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getDashboard(1, 1).isEmpty());
        assertFalse(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getCannonAlien());

        finish = card8.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 10, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getDashboard(2, 6).isPresent());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getDashboard(4, 6).isEmpty());

        finish = card8.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 4, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.activateShield("Davide", 12);

        finish = card8.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 10, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        // Card 9
        controller.drawCard("Tommaso");

        controller.activateEngines("Tommaso", new ArrayList<>(), new ArrayList<>());
        controller.activateEngines("Simone", new ArrayList<>(List.of(3)), new ArrayList<>(List.of(97)));

        assertEquals(8, controller.getModel().getBoard().getPlayers().get(0).getValue());
        assertEquals(2, controller.getModel().getBoard().getPlayers().get(1).getValue());
        assertEquals(-2, controller.getModel().getBoard().getPlayers().get(2).getValue());


        // Card 10
        controller.drawCard("Tommaso");

        controller.getIndex("Tommaso", 1);
        controller.getIndex("Simone", 0);
        controller.getIndex("Davide", 2);

        good = new ArrayList<>(List.of(ColorType.YELLOW));
        controller.updateGoods("Tommaso", new HashMap<>(Map.of(63, good)), new ArrayList<>());
        good = new ArrayList<>(List.of(ColorType.GREEN));
        good2 = new ArrayList<>(List.of(ColorType.GREEN, ColorType.GREEN, ColorType.GREEN));
        controller.updateGoods("Simone", new HashMap<>(Map.of(28, good2, 64, good)), new ArrayList<>());
        good = new ArrayList<>(List.of(ColorType.YELLOW, ColorType.BLUE));
        good2 = new ArrayList<>(List.of(ColorType.BLUE, ColorType.BLUE, ColorType.BLUE));
        controller.updateGoods("Davide", new HashMap<>(Map.of(18, good, 30, good2)), new ArrayList<>());

        assertEquals(5, controller.getModel().getBoard().getPlayers().get(0).getValue());
        assertEquals(-1, controller.getModel().getBoard().getPlayers().get(1).getValue());
        assertEquals(-5, controller.getModel().getBoard().getPlayers().get(2).getValue());


        // Card 11
        controller.drawCard("Tommaso");

        assertEquals(1, controller.getModel().getBoard().getPlayers().get(0).getValue());
        assertEquals(-10, controller.getModel().getBoard().getPlayers().get(1).getValue());
        assertEquals(-12, controller.getModel().getBoard().getPlayers().get(2).getValue());

        // Card 12
        controller.drawCard("Tommaso");

        finish = card12.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 10, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getDashboard(2, 6).isEmpty());

        finish = card12.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 5, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.chooseShipPart("Tommaso", 0);

        controller.chooseShipPart("Simone", 0);

        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(2, 0).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(2, 1).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(3, 0).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(4, 0).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getDashboard(2, 0).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getDashboard(2, 1).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getDashboard(3, 0).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getDashboard(2, 1).isEmpty());

        // Final Ranking

        finish = card12.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 11, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        List<PlayerData> finalRank = controller.getModel().getBoard().getRanking();

        assertEquals(5, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().countExposedConnectors());
        assertEquals(9, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().countExposedConnectors());
        assertEquals(9, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().countExposedConnectors());

        assertEquals(17, finalRank.get(0).getCredits());
        assertEquals(3, finalRank.get(1).getCredits());
        assertEquals(0, finalRank.get(2).getCredits());


    }

    @Test
    public void testSecondCompleteGame() {

        // Building Ships

        // Player 1 Ship Check

        // Player 2 Ship Check

        // Player 3 Ship Check

        // Alien Management

        // Card Deck Initialization

        // Card 1

        // Card 2

        // Card 3

        // Card 4

        // Card 5

        // Card 6

        // Card 7

        // Card 8

        // Card 9

        // Card 10

        // Card 11

        // Card 12

        // Final Ranking

    }

    @Test
    public void testThirdCompleteGame() {

        // Building Ships

        // Player 1 Ship Check

        // Player 2 Ship Check

        // Player 3 Ship Check

        // Alien Management

        // Card Deck Initialization

        // Card 1

        // Card 2

        // Card 3

        // Card 4

        // Card 5

        // Card 6

        // Card 7

        // Card 8

        // Card 9

        // Card 10

        // Card 11

        // Card 12

        // Final Ranking

    }


}