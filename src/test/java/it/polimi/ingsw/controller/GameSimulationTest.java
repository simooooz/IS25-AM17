package it.polimi.ingsw.controller;

import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.utils.*;

import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.exceptions.CabinComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.exceptions.IllegalStateException;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.function.Consumer;

import static it.polimi.ingsw.common.model.enums.DirectionType.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameSimulationTest {
    private GameController controller;

    @BeforeEach
    void setUp() {

    }

    @Test
    public void testFirstCompleteGame(){
        List<String> usernames = new ArrayList<>(List.of("Tommaso", "Simone", "Davide"));
        controller = new GameController(usernames, false);
        controller.startMatch();

        // Building Ships


        controller.pickComponent("Tommaso", 102);
        controller.insertComponent("Tommaso", 102, 0, 2, 0, true);

        controller.lookCardPile("Tommaso", 0);

        controller.pickComponent("Tommaso", 16);
        controller.rotateComponent("Tommaso", 16, 3);
        controller.insertComponent("Tommaso", 16, 1, 1, 0, false);
        controller.moveComponent("Tommaso", 16, 0, 4, 0);

        controller.pickComponent("Tommaso", 60);
        controller.releaseComponent("Tommaso", 60);

        controller.pickComponent("Tommaso", 113);
        controller.rotateComponent("Tommaso", 113, 3);
        controller.insertComponent("Tommaso", 113, 1, 1, 0, true);

        controller.pickComponent("Tommaso", 60);
        controller.rotateComponent("Tommaso", 60, 3);
        controller.insertComponent("Tommaso", 60, 1, 2, 0, true);

        controller.pickComponent("Tommaso", 131);
        controller.insertComponent("Tommaso", 131, 1, 3, 0, true);

        controller.pickComponent("Tommaso", 116);
        controller.rotateComponent("Tommaso", 116, 1);
        controller.insertComponent("Tommaso", 116, 1, 4, 0, true);

        controller.pickComponent("Tommaso", 125);
        controller.insertComponent("Tommaso", 125, 1, 5, 0, true);

        controller.pickComponent("Tommaso", 134);
        controller.rotateComponent("Tommaso", 134, 3);
        controller.insertComponent("Tommaso", 134, 2, 0, 0, true);

        controller.pickComponent("Tommaso", 55);
        controller.insertComponent("Tommaso", 55, 2, 1, 0, true);

        controller.pickComponent("Tommaso", 38);
        controller.rotateComponent("Tommaso", 38, 1);
        controller.insertComponent("Tommaso", 38, 2, 2, 0, true);

        controller.pickComponent("Tommaso", 63);
        controller.insertComponent("Tommaso", 63, 2, 4, 0, true);

        controller.pickComponent("Tommaso", 24);
        controller.insertComponent("Tommaso", 24, 2, 5, 0, true);

        controller.pickComponent("Tommaso", 25);
        controller.insertComponent("Tommaso", 25, 2, 6, 0, true);

        controller.pickComponent("Tommaso", 9);
        controller.rotateComponent("Tommaso", 9, 1);
        controller.insertComponent("Tommaso", 9, 3, 0, 0, true);

        controller.pickComponent("Tommaso", 152);
        controller.rotateComponent("Tommaso", 152, 2);
        controller.insertComponent("Tommaso", 152, 3, 1, 0, true);

        controller.pickComponent("Tommaso", 5);
        controller.insertComponent("Tommaso", 5, 3, 2, 0, true);

        controller.pickComponent("Tommaso", 92);
        controller.insertComponent("Tommaso", 92, 3, 3, 0, true);

        controller.pickComponent("Tommaso", 146);
        controller.rotateComponent("Tommaso", 146, 1);
        controller.insertComponent("Tommaso", 146, 3, 4, 0, true);

        controller.pickComponent("Tommaso", 62);
        controller.rotateComponent("Tommaso", 62, 1);
        controller.insertComponent("Tommaso", 62, 3, 5, 0, true);

        controller.pickComponent("Tommaso", 77);
        controller.insertComponent("Tommaso", 77, 4, 0, 0, true);

        controller.pickComponent("Tommaso", 46);
        controller.rotateComponent("Tommaso", 46, 2);
        controller.insertComponent("Tommaso", 46, 4, 1, 0, true);

        controller.pickComponent("Tommaso", 137);
        controller.insertComponent("Tommaso", 137, 4, 2, 0, true);

        controller.pickComponent("Tommaso", 104);
        controller.rotateComponent("Tommaso", 104, 3);
        controller.insertComponent("Tommaso", 104, 4, 4, 0, true);

        controller.pickComponent("Tommaso", 36);
        controller.rotateComponent("Tommaso", 36, 2);
        controller.insertComponent("Tommaso", 36, 4, 5, 0, true);

        controller.pickComponent("Tommaso", 100);
        controller.rotateComponent("Tommaso", 100, 1);
        controller.insertComponent("Tommaso", 100, 4, 6, 0, true);


        controller.pickComponent("Simone", 127);
        controller.rotateComponent("Simone", 127, 1);
        controller.insertComponent("Simone", 127, 0, 2, 0, true);

        controller.pickComponent("Simone", 108);
        controller.insertComponent("Simone", 108, 0, 4, 0, true);

        controller.pickComponent("Simone", 143);
        controller.rotateComponent("Simone", 143, 3);
        controller.insertComponent("Simone", 143, 1, 1, 0, true);

        controller.pickComponent("Simone", 64);
        controller.rotateComponent("Simone", 64, 1);
        controller.insertComponent("Simone", 64, 1, 2, 0, true);

        controller.pickComponent("Simone", 84);
        controller.rotateComponent("Simone", 84, 2);
        controller.insertComponent("Simone", 84, 1, 3, 0, true);

        controller.pickComponent("Simone", 3);
        controller.rotateComponent("Simone", 3, 2);
        controller.insertComponent("Simone", 3, 1, 4, 0, true);

        controller.pickComponent("Simone", 28);
        controller.rotateComponent("Simone", 28, 3);
        controller.insertComponent("Simone", 28, 1, 5, 0, true);

        controller.pickComponent("Simone", 149);
        controller.rotateComponent("Simone", 149, 3);
        controller.insertComponent("Simone", 149, 2, 0, 0, true);

        controller.pickComponent("Simone", 51);
        controller.rotateComponent("Simone", 51, 1);
        controller.insertComponent("Simone", 51, 2, 1, 0, true);

        controller.pickComponent("Simone", 56);
        controller.insertComponent("Simone", 56, 2, 2, 0, true);

        controller.pickComponent("Simone", 58);
        controller.rotateComponent("Simone", 58, 2);
        controller.insertComponent("Simone", 58, 2, 4, 0, true);

        controller.pickComponent("Simone", 150);
        controller.rotateComponent("Simone", 150, 1);
        controller.insertComponent("Simone", 150, 2, 5, 0, true);

        controller.pickComponent("Simone", 103);
        controller.insertComponent("Simone", 103, 2, 6, 0, true);

        controller.pickComponent("Simone", 14);
        controller.insertComponent("Simone", 14, 3, 0, 0, true);

        controller.pickComponent("Simone", 79);
        controller.insertComponent("Simone", 79, 3, 1, 0, true);

        controller.pickComponent("Simone", 17);
        controller.rotateComponent("Simone", 17, 3);
        controller.insertComponent("Simone", 17, 3, 2, 0, true);

        controller.pickComponent("Simone", 85);
        controller.insertComponent("Simone", 85, 3, 3, 0, true);

        controller.pickComponent("Simone", 43);
        controller.rotateComponent("Simone", 43, 1);
        controller.insertComponent("Simone", 43, 3, 4, 0, true);

        controller.pickComponent("Simone", 53);
        controller.insertComponent("Simone", 53, 3, 5, 0, true);

        controller.pickComponent("Simone", 97);
        controller.insertComponent("Simone", 97, 3, 6, 0, true);

        controller.pickComponent("Simone", 45);
        controller.rotateComponent("Simone", 45, 1);
        controller.insertComponent("Simone", 45, 4, 4, 0, true);

        controller.pickComponent("Simone", 67);
        controller.insertComponent("Simone", 67, 4, 5, 0, true);

        controller.pickComponent("Simone", 90);
        controller.reserveComponent("Simone", 90);


        controller.pickComponent("Davide", 118);
        controller.insertComponent("Davide", 118, 0, 2, 0, true);

        controller.pickComponent("Davide", 126);
        controller.insertComponent("Davide", 126, 0, 4, 0, true);

        controller.pickComponent("Davide", 136);
        controller.rotateComponent("Davide", 136, 1);
        controller.insertComponent("Davide", 136, 1, 1, 0, true);

        controller.pickComponent("Davide", 44);
        controller.rotateComponent("Davide", 44, 3);
        controller.insertComponent("Davide", 44, 1, 2, 0, true);

        controller.pickComponent("Davide", 61);
        controller.insertComponent("Davide", 61, 1, 3, 0, true);

        controller.pickComponent("Davide", 1);
        controller.rotateComponent("Davide", 1, 3);
        controller.insertComponent("Davide", 1, 1, 4, 0, true);

        controller.pickComponent("Davide", 133);
        controller.insertComponent("Davide", 133, 1, 5, 0, true);

        controller.pickComponent("Davide", 114);
        controller.insertComponent("Davide", 114, 2, 0, 0, true);

        controller.pickComponent("Davide", 37);
        controller.rotateComponent("Davide", 37, 1);
        controller.insertComponent("Davide", 37, 2, 1, 0, true);

        controller.pickComponent("Davide", 148);
        controller.rotateComponent("Davide", 148, 2);
        controller.insertComponent("Davide", 148, 2, 2, 0, true);

        controller.pickComponent("Davide", 142);
        controller.rotateComponent("Davide", 142, 2);
        controller.insertComponent("Davide", 142, 2, 4, 0, true);

        controller.pickComponent("Davide", 39);
        controller.rotateComponent("Davide", 39, 3);
        controller.insertComponent("Davide", 39, 2, 5, 0, true);

        controller.pickComponent("Davide", 12);
        controller.insertComponent("Davide", 12, 3, 0, 0, true);

        controller.pickComponent("Davide", 41);
        controller.insertComponent("Davide", 41, 3, 1, 0, true);

        controller.pickComponent("Davide", 18);
        controller.rotateComponent("Davide", 18, 2);
        controller.insertComponent("Davide", 18, 3, 2, 0, true);

        controller.pickComponent("Davide", 95);
        controller.insertComponent("Davide", 95, 3, 3, 0, true);

        controller.pickComponent("Davide", 151);
        controller.rotateComponent("Davide", 151, 3);
        controller.insertComponent("Davide", 151, 3, 4, 0, true);

        controller.pickComponent("Davide", 30);
        controller.insertComponent("Davide", 30, 3, 5, 0, true);

        controller.pickComponent("Davide", 75);
        controller.insertComponent("Davide", 75, 4, 0, 0, true);

        controller.pickComponent("Davide", 94);
        controller.insertComponent("Davide", 94, 4, 1, 0, true);

        controller.pickComponent("Davide", 81);
        controller.insertComponent("Davide", 81, 4, 2, 0, true);

        controller.pickComponent("Davide", 10);
        controller.rotateComponent("Davide", 10, 1);
        controller.insertComponent("Davide", 10, 4, 4, 0, true);

        controller.pickComponent("Davide", 96);
        controller.insertComponent("Davide", 96, 4, 5, 0, true);

        controller.pickComponent("Davide", 87);
        controller.insertComponent("Davide", 87, 4, 6, 0, true);

        controller.pickComponent("Davide", 156);
        controller.insertComponent("Davide", 156, 3, 6, 0, true);

        controller.pickComponent("Davide", 72);
        controller.reserveComponent("Davide", 72);

        controller.pickComponent("Davide", 83);
        controller.reserveComponent("Davide", 83);

        controller.moveHourglass("Tommaso", new Consumer<List<GameEvent>>() {
            @Override
            public void accept(List<GameEvent> gameEvents) {

            }
        });

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


        // Player 3 Ship Check

        toRemoveTilesIDs.clear();
        toRemoveTilesIDs.add(156);

        controller.checkShip("Davide", toRemoveTilesIDs);

        assertEquals(4, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().countExposedConnectors());


        //Alien management

        Exception exception = assertThrows(IllegalStateException.class, () -> controller.chooseAlien("Simone", new HashMap<>()));
        assertEquals("State is not WAIT_ALIEN", exception.getMessage());

        controller.chooseAlien("Tommaso", new HashMap<>(Map.of(46, AlienType.ENGINE)));

        controller.chooseAlien("Simone", new HashMap<>(Map.of(51, AlienType.CANNON)));

        exception = assertThrows(CabinComponentNotValidException.class, () -> controller.chooseAlien("Davide", new HashMap<>(Map.of(41, AlienType.CANNON))));
        assertEquals("Alien CANNON is not compatible with this cabin", exception.getMessage());

        controller.chooseAlien("Davide", new HashMap<>(Map.of(39, AlienType.CANNON)));

        // Card Deck Initialization

        List<Card> cardDeck = controller.getModel().getBoard().getCardPile();
        cardDeck.clear();
        SlaversCard card1 = new SlaversCard(0, 2, false, 4, 8, 2, 7);
        cardDeck.add(card1);
        AbandonedShipCard card2 = new AbandonedShipCard(0, 1, true, 3, 4, 1);
        cardDeck.add(card2);
        AbandonedStationCard card3 = new AbandonedStationCard(0, 1, true, 5, 1, new HashMap<>(Map.of(ColorType.YELLOW, 1, ColorType.GREEN, 1)));
        cardDeck.add(card3);
        PenaltyCombatZone penalty1 = new CountablePenaltyZone(4, MalusType.DAYS);
        PenaltyCombatZone penalty2 = new CountablePenaltyZone(3, MalusType.GOODS);
        PenaltyCombatZone penalty3 = new CannonFirePenaltyCombatZone(new ArrayList<>(List.of(new CannonFire(false, NORTH), new CannonFire(false, WEST), new CannonFire(false, EAST), new CannonFire(true, SOUTH))));
        List<WarLine> damages = new ArrayList<>();
        damages.add(new WarLine(CriteriaType.CANNON, penalty1));
        damages.add(new WarLine(CriteriaType.ENGINE, penalty2));
        damages.add(new WarLine(CriteriaType.CREW, penalty3));
        CombatZoneCard card4 = new CombatZoneCard(0, 2, false, damages);
        cardDeck.add(card4);
        MeteorSwarmCard card5 = new MeteorSwarmCard(0, 2, false, new ArrayList<>(List.of(new Meteor(false, NORTH), new Meteor(false, NORTH), new Meteor(true, WEST), new Meteor(false, WEST), new Meteor(false, WEST))));
        cardDeck.add(card5);
        Planet p1 = new Planet(new HashMap<>(Map.of(ColorType.GREEN, 2)));
        Planet p2 = new Planet(new HashMap<>(Map.of(ColorType.YELLOW, 1)));
        Planet p3 = new Planet(new HashMap<>(Map.of(ColorType.BLUE, 3)));
        PlanetCard card6 = new PlanetCard(0, 1, false, new ArrayList<>(List.of(p1, p2, p3)), 1);
        cardDeck.add(card6);
        SmugglersCard card7 = new SmugglersCard(0, 2, false, 8, 3, new HashMap<>(Map.of(ColorType.RED, 1, ColorType.YELLOW, 2)),1);
        cardDeck.add(card7);
        MeteorSwarmCard card8 = new MeteorSwarmCard(0, 2, false, new ArrayList<>(List.of(new Meteor(true, NORTH), new Meteor(true, NORTH), new Meteor(false, SOUTH), new Meteor(false, SOUTH))));
        cardDeck.add(card8);
        OpenSpaceCard card9 = new OpenSpaceCard(0, 2, false);
        cardDeck.add(card9);
        p1 = new Planet(new HashMap<>(Map.of(ColorType.GREEN, 4)));
        p2 = new Planet(new HashMap<>(Map.of(ColorType.YELLOW, 2)));
        p3 = new Planet(new HashMap<>(Map.of(ColorType.BLUE, 4)));
        PlanetCard card10 = new PlanetCard(0, 1, false, new ArrayList<>(List.of(p1, p2, p3)), 3);
        cardDeck.add(card10);
        StardustCard card11 = new StardustCard(0, 1, true);
        cardDeck.add(card11);
        PiratesCard card12 = new PiratesCard(0, 2, false, 6, 7, 2, new ArrayList<>(List.of(new CannonFire(true, NORTH), new CannonFire(false, NORTH), new CannonFire(true, NORTH))));
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

        exception = assertThrows(ComponentNotValidException.class, () -> {
            controller.activateCannons("Tommaso", new ArrayList<>(List.of(14)), new ArrayList<>(List.of(96)));
        });
        assertEquals("Battery component not valid", exception.getMessage());

        exception = assertThrows(ComponentNotValidException.class, () -> {
            controller.activateCannons("Tommaso", new ArrayList<>(List.of(16)), new ArrayList<>(List.of(118)));
        });
        assertEquals("Cannon component not valid", exception.getMessage());

        exception = assertThrows(ComponentNotValidException.class, () -> {
            controller.activateCannons("Tommaso", new ArrayList<>(List.of(16)), new ArrayList<>(List.of(102)));
        });
        assertEquals("Cannon component 102 is not double", exception.getMessage());

        exception = assertThrows(RuntimeException.class, () -> {
            controller.activateCannons("Tommaso", new ArrayList<>(List.of(16)), new ArrayList<>(List.of(134, 131)));
        });
        assertEquals("Inconsistent number of batteries", exception.getMessage());

        exception = assertThrows(ComponentNotValidException.class, () -> {
            controller.activateCannons("Tommaso", new ArrayList<>(List.of(16, 16)), new ArrayList<>(List.of(134, 134)));
        });
        assertEquals("Duplicate cannons", exception.getMessage());

        controller.activateCannons("Tommaso", new ArrayList<>(List.of(16, 16)), new ArrayList<>(List.of(134, 131)));
        assertEquals(5, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getBatteries());

        controller.activateCannons("Simone", new ArrayList<>(), new ArrayList<>());
        assertEquals(5, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getBatteries());

        assertEquals(-2, controller.getModel().getBoard().getPlayers().get(2).getValue());

        // Second War Line
        controller.activateEngines("Tommaso", new ArrayList<>(List.of(16)), new ArrayList<>(List.of(92)));
        assertEquals(4, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getBatteries());

        controller.activateEngines("Simone", new ArrayList<>(), new ArrayList<>());
        assertEquals(5, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getBatteries());

        exception = assertThrows(BatteryComponentNotValidException.class, () -> {
            controller.updateGoods("Simone", new HashMap<>(), new ArrayList<>(List.of(14, 14)));
        });
        assertEquals("Too few battery components provided", exception.getMessage());

        exception = assertThrows(BatteryComponentNotValidException.class, () -> {
            controller.updateGoods("Simone", new HashMap<>(), new ArrayList<>(List.of(14, 14, 3, 3)));
        });
        assertEquals("Too many battery components provided", exception.getMessage());

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

        exception = assertThrows(IllegalArgumentException.class, () -> {
            controller.getIndex("Davide", 0);
        });
        assertEquals("Planet not valid or already occupied", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            controller.getIndex("Davide", 6);
        });
        assertEquals("Planet not valid or already occupied", exception.getMessage());

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

        exception = assertThrows(IndexOutOfBoundsException.class, () -> {
            controller.chooseShipPart("Simone", 3);
        });
        assertEquals("Part index not valid", exception.getMessage());

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


        assertEquals(0, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getCredits());
        assertEquals(3, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getCredits());
        assertEquals(16, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getCredits());


    }

    @Test
    public void testSecondCompleteGame() {
        List<String> usernames = new ArrayList<>(List.of("Tommaso", "Simone", "Davide", "Demetrio"));
        controller = new GameController(usernames, false);
        controller.startMatch();

        // Building Ships
        controller.pickComponent("Tommaso", 69);
        controller.rotateComponent("Tommaso", 69, 3);
        controller.insertComponent("Tommaso", 69, 0, 2, 0, true);

        controller.pickComponent("Tommaso", 108);
        controller.rotateComponent("Tommaso", 108, 0);
        controller.insertComponent("Tommaso", 108, 0, 4, 0, true);

        controller.pickComponent("Tommaso", 106);
        controller.rotateComponent("Tommaso", 106, 0);
        controller.insertComponent("Tommaso", 106, 1, 1, 0, true);

        controller.pickComponent("Tommaso", 57);
        controller.rotateComponent("Tommaso", 57, 0);
        controller.insertComponent("Tommaso", 57, 1, 2, 0, true);

        controller.pickComponent("Tommaso", 122);
        controller.rotateComponent("Tommaso", 122, 0);
        controller.insertComponent("Tommaso", 122, 1, 3, 0, true);

        controller.pickComponent("Tommaso", 6);
        controller.rotateComponent("Tommaso", 6, 3);
        controller.insertComponent("Tommaso", 6, 1, 4, 0, true);

        controller.pickComponent("Tommaso", 113);
        controller.rotateComponent("Tommaso", 113, 0);
        controller.insertComponent("Tommaso", 113, 1, 5, 0, true);

        controller.pickComponent("Tommaso", 127);
        controller.rotateComponent("Tommaso", 127, 0);
        controller.insertComponent("Tommaso", 127, 2, 0, 0, true);

        controller.pickComponent("Tommaso", 37);
        controller.rotateComponent("Tommaso", 37, 0);
        controller.insertComponent("Tommaso", 37, 2, 1, 0, true);

        controller.pickComponent("Tommaso", 31);
        controller.rotateComponent("Tommaso", 31, 3);
        controller.insertComponent("Tommaso", 31, 2, 2, 0, true);

        controller.pickComponent("Tommaso", 18);
        controller.rotateComponent("Tommaso", 18, 3);
        controller.insertComponent("Tommaso", 18, 2, 4, 0, true);

        controller.pickComponent("Tommaso", 44);
        controller.rotateComponent("Tommaso", 44, 2);
        controller.insertComponent("Tommaso", 44, 2, 5, 0, true);

        controller.pickComponent("Tommaso", 2);
        controller.rotateComponent("Tommaso", 2, 2);
        controller.insertComponent("Tommaso", 2, 2, 6, 0, true);

        controller.pickComponent("Tommaso", 149);
        controller.rotateComponent("Tommaso", 149, 3);
        controller.insertComponent("Tommaso", 149, 3, 0, 0, true);

        controller.pickComponent("Tommaso", 66);
        controller.rotateComponent("Tommaso", 66, 0);
        controller.insertComponent("Tommaso", 66, 3, 1, 0, true);

        controller.pickComponent("Tommaso", 29);
        controller.rotateComponent("Tommaso", 29, 2);
        controller.insertComponent("Tommaso", 29, 3, 2, 0, true);

        controller.pickComponent("Tommaso", 14);
        controller.rotateComponent("Tommaso", 14, 0);
        controller.insertComponent("Tommaso", 14, 3, 3, 0, true);

        controller.pickComponent("Tommaso", 135);
        controller.rotateComponent("Tommaso", 135, 0);
        controller.insertComponent("Tommaso", 135, 3, 4, 0, true);

        controller.pickComponent("Tommaso", 24);
        controller.rotateComponent("Tommaso", 24, 0);
        controller.insertComponent("Tommaso", 24, 3, 5, 0, true);

        controller.pickComponent("Tommaso", 152);
        controller.rotateComponent("Tommaso", 152, 1);
        controller.insertComponent("Tommaso", 152, 3, 6, 0, true);

        controller.pickComponent("Tommaso", 77);
        controller.rotateComponent("Tommaso", 77, 0);
        controller.insertComponent("Tommaso", 77, 4, 0, 0, true);

        controller.pickComponent("Tommaso", 9);
        controller.rotateComponent("Tommaso", 9, 0);
        controller.insertComponent("Tommaso", 9, 4, 1, 0, true);

        controller.pickComponent("Tommaso", 81);
        controller.rotateComponent("Tommaso", 81, 0);
        controller.insertComponent("Tommaso", 81, 4, 2, 0, true);

        controller.pickComponent("Tommaso", 143);
        controller.rotateComponent("Tommaso", 143, 2);
        controller.insertComponent("Tommaso", 143, 4, 4, 0, true);

        controller.pickComponent("Tommaso", 99);
        controller.rotateComponent("Tommaso", 99, 0);
        controller.insertComponent("Tommaso", 99, 4, 5, 0, true);


        controller.pickComponent("Simone", 104);
        controller.rotateComponent("Simone", 104, 1);
        controller.insertComponent("Simone", 104, 0, 2, 0, true);

        controller.pickComponent("Simone", 125);
        controller.rotateComponent("Simone", 125, 0);
        controller.insertComponent("Simone", 125, 0, 4, 0, true);

        controller.pickComponent("Simone", 20);
        controller.rotateComponent("Simone", 20, 2);
        controller.insertComponent("Simone", 20, 1, 1, 0, true);

        controller.pickComponent("Simone", 55);
        controller.rotateComponent("Simone", 55, 2);
        controller.insertComponent("Simone", 55, 1, 2, 0, true);

        controller.pickComponent("Simone", 119);
        controller.rotateComponent("Simone", 119, 0);
        controller.insertComponent("Simone", 119, 1, 3, 0, true);

        controller.pickComponent("Simone", 59);
        controller.rotateComponent("Simone", 59, 1);
        controller.insertComponent("Simone", 59, 1, 4, 0, true);

        controller.pickComponent("Simone", 118);
        controller.rotateComponent("Simone", 118, 0);
        controller.insertComponent("Simone", 118, 1, 5, 0, true);

        controller.pickComponent("Simone", 13);
        controller.rotateComponent("Simone", 13, 1);
        controller.insertComponent("Simone", 13, 2, 0, 0, true);

        controller.pickComponent("Simone", 68);
        controller.rotateComponent("Simone", 68, 2);
        controller.insertComponent("Simone", 68, 2, 1, 0, true);

        controller.pickComponent("Simone", 12);
        controller.rotateComponent("Simone", 12, 3);
        controller.insertComponent("Simone", 12, 2, 2, 0, true);

        controller.pickComponent("Simone", 65);
        controller.rotateComponent("Simone", 65, 2);
        controller.insertComponent("Simone", 65, 2, 4, 0, true);

        controller.pickComponent("Simone", 54);
        controller.rotateComponent("Simone", 54, 0);
        controller.insertComponent("Simone", 54, 2, 5, 0, true);

        controller.pickComponent("Simone", 36);
        controller.rotateComponent("Simone", 36, 0);
        controller.insertComponent("Simone", 36, 2, 6, 0, true);

        controller.pickComponent("Simone", 16);
        controller.rotateComponent("Simone", 16, 3);
        controller.insertComponent("Simone", 16, 3, 0, 0, true);

        controller.pickComponent("Simone", 138);
        controller.rotateComponent("Simone", 138, 3);
        controller.insertComponent("Simone", 138, 3, 1, 0, true);

        controller.pickComponent("Simone", 147);
        controller.rotateComponent("Simone", 147, 3);
        controller.insertComponent("Simone", 147, 3, 2, 0, true);

        controller.pickComponent("Simone", 30);
        controller.rotateComponent("Simone", 30, 0);
        controller.insertComponent("Simone", 30, 3, 3, 0, true);

        controller.pickComponent("Simone", 11);
        controller.rotateComponent("Simone", 11, 2);
        controller.insertComponent("Simone", 11, 3, 4, 0, true);

        controller.pickComponent("Simone", 39);
        controller.rotateComponent("Simone", 39, 2);
        controller.insertComponent("Simone", 39, 3, 5, 0, true);

        controller.pickComponent("Simone", 146);
        controller.rotateComponent("Simone", 146, 1);
        controller.insertComponent("Simone", 146, 3, 6, 0, true);

        controller.pickComponent("Simone", 140);
        controller.rotateComponent("Simone", 140, 1);
        controller.insertComponent("Simone", 140, 4, 0, 0, true);

        controller.pickComponent("Simone", 95);
        controller.rotateComponent("Simone", 95, 0);
        controller.insertComponent("Simone", 95, 4, 1, 0, true);

        controller.pickComponent("Simone", 126);
        controller.rotateComponent("Simone", 126, 2);
        controller.insertComponent("Simone", 126, 4, 2, 0, true);

        controller.pickComponent("Simone", 92);
        controller.rotateComponent("Simone", 92, 0);
        controller.insertComponent("Simone", 92, 4, 4, 0, true);

        controller.pickComponent("Simone", 156);
        controller.rotateComponent("Simone", 156, 1);
        controller.insertComponent("Simone", 156, 4, 5, 0, true);

        controller.pickComponent("Simone", 73);
        controller.rotateComponent("Simone", 73, 0);
        controller.insertComponent("Simone", 73, 4, 6, 0, true);


        controller.pickComponent("Demetrio", 101);
        controller.rotateComponent("Demetrio", 101, 0);
        controller.insertComponent("Demetrio", 101, 0, 2, 0, true);

        controller.pickComponent("Demetrio", 103);
        controller.rotateComponent("Demetrio", 103, 0);
        controller.insertComponent("Demetrio", 103, 0, 4, 0, true);

        controller.pickComponent("Demetrio", 128);
        controller.rotateComponent("Demetrio", 128, 0);
        controller.insertComponent("Demetrio", 128, 1, 1, 0, true);

        controller.pickComponent("Demetrio", 61);
        controller.rotateComponent("Demetrio", 61, 2);
        controller.insertComponent("Demetrio", 61, 1, 2, 0, true);

        controller.pickComponent("Demetrio", 3);
        controller.rotateComponent("Demetrio", 3, 1);
        controller.insertComponent("Demetrio", 3, 1, 3, 0, true);

        controller.pickComponent("Demetrio", 60);
        controller.rotateComponent("Demetrio", 60, 2);
        controller.insertComponent("Demetrio", 60, 1, 4, 0, true);

        controller.pickComponent("Demetrio", 117);
        controller.rotateComponent("Demetrio", 117, 0);
        controller.insertComponent("Demetrio", 117, 1, 5, 0, true);

        controller.pickComponent("Demetrio", 153);
        controller.rotateComponent("Demetrio", 153, 3);
        controller.insertComponent("Demetrio", 153, 2, 0, 0, true);

        controller.pickComponent("Demetrio", 56);
        controller.rotateComponent("Demetrio", 56, 2);
        controller.insertComponent("Demetrio", 56, 2, 1, 0, true);

        controller.pickComponent("Demetrio", 27);
        controller.rotateComponent("Demetrio", 27, 1);
        controller.insertComponent("Demetrio", 27, 2, 2, 0, true);

        controller.pickComponent("Demetrio", 151);
        controller.rotateComponent("Demetrio", 151, 1);
        controller.insertComponent("Demetrio", 151, 2, 4, 0, true);

        controller.pickComponent("Demetrio", 25);
        controller.rotateComponent("Demetrio", 25, 3);
        controller.insertComponent("Demetrio", 25, 2, 5, 0, true);

        controller.pickComponent("Demetrio", 137);
        controller.rotateComponent("Demetrio", 137, 3);
        controller.insertComponent("Demetrio", 137, 2, 6, 0, true);

        controller.pickComponent("Demetrio", 40);
        controller.rotateComponent("Demetrio", 40, 1);
        controller.insertComponent("Demetrio", 40, 3, 0, 0, true);

        controller.pickComponent("Demetrio", 17);
        controller.rotateComponent("Demetrio", 17, 3);
        controller.insertComponent("Demetrio", 17, 3, 1, 0, true);

        controller.pickComponent("Demetrio", 145);
        controller.rotateComponent("Demetrio", 145, 3);
        controller.insertComponent("Demetrio", 145, 3, 2, 0, true);

        controller.pickComponent("Demetrio", 26);
        controller.rotateComponent("Demetrio", 26, 1);
        controller.insertComponent("Demetrio", 26, 3, 3, 0, true);

        controller.pickComponent("Demetrio", 48);
        controller.rotateComponent("Demetrio", 48, 1);
        controller.insertComponent("Demetrio", 48, 3, 4, 0, true);

        controller.pickComponent("Demetrio", 5);
        controller.rotateComponent("Demetrio", 5, 1);
        controller.insertComponent("Demetrio", 5, 3, 5, 0, true);

        controller.pickComponent("Demetrio", 1);
        controller.rotateComponent("Demetrio", 1, 3);
        controller.insertComponent("Demetrio", 1, 3, 6, 0, true);

        controller.pickComponent("Demetrio", 94);
        controller.rotateComponent("Demetrio", 94, 0);
        controller.insertComponent("Demetrio", 94, 4, 0, 0, true);

        controller.pickComponent("Demetrio", 79);
        controller.rotateComponent("Demetrio", 79, 0);
        controller.insertComponent("Demetrio", 79, 4, 1, 0, true);

        controller.pickComponent("Demetrio", 90);
        controller.rotateComponent("Demetrio", 90, 0);
        controller.insertComponent("Demetrio", 90, 4, 2, 0, true);

        controller.pickComponent("Demetrio", 78);
        controller.rotateComponent("Demetrio", 78, 0);
        controller.insertComponent("Demetrio", 78, 4, 4, 0, true);

        controller.pickComponent("Demetrio", 139);
        controller.rotateComponent("Demetrio", 139, 0);
        controller.insertComponent("Demetrio", 139, 4, 5, 0, true);

        controller.pickComponent("Demetrio", 88);
        controller.rotateComponent("Demetrio", 88, 0);
        controller.insertComponent("Demetrio", 88, 4, 6, 0, true);


        controller.pickComponent("Davide", 124);
        controller.rotateComponent("Davide", 124, 0);
        controller.insertComponent("Davide", 124, 0, 2, 0, true);

        controller.pickComponent("Davide", 100);
        controller.rotateComponent("Davide", 100, 0);
        controller.insertComponent("Davide", 100, 0, 4, 0, true);

        controller.pickComponent("Davide", 67);
        controller.rotateComponent("Davide", 67, 2);
        controller.insertComponent("Davide", 67, 1, 1, 0, true);

        controller.pickComponent("Davide", 63);
        controller.rotateComponent("Davide", 63, 2);
        controller.insertComponent("Davide", 63, 1, 2, 0, true);

        controller.pickComponent("Davide", 141);
        controller.rotateComponent("Davide", 141, 2);
        controller.insertComponent("Davide", 141, 1, 3, 0, true);

        controller.pickComponent("Davide", 41);
        controller.rotateComponent("Davide", 41, 1);
        controller.insertComponent("Davide", 41, 1, 4, 0, true);

        controller.pickComponent("Davide", 133);
        controller.rotateComponent("Davide", 133, 0);
        controller.insertComponent("Davide", 133, 1, 5, 0, true);

        controller.pickComponent("Davide", 105);
        controller.rotateComponent("Davide", 105, 0);
        controller.insertComponent("Davide", 105, 2, 0, 0, true);

        controller.pickComponent("Davide", 136);
        controller.rotateComponent("Davide", 136, 2);
        controller.insertComponent("Davide", 136, 2, 1, 0, true);

        controller.pickComponent("Davide", 58);
        controller.rotateComponent("Davide", 58, 0);
        controller.insertComponent("Davide", 58, 2, 2, 0, true);

        controller.pickComponent("Davide", 4);
        controller.rotateComponent("Davide", 4, 3);
        controller.insertComponent("Davide", 4, 2, 4, 0, true);

        controller.pickComponent("Davide", 150);
        controller.rotateComponent("Davide", 150, 0);
        controller.insertComponent("Davide", 150, 2, 5, 0, true);

        controller.pickComponent("Davide", 155);
        controller.rotateComponent("Davide", 155, 0);
        controller.insertComponent("Davide", 155, 2, 6, 0, true);

        controller.pickComponent("Davide", 154);
        controller.rotateComponent("Davide", 154, 1);
        controller.insertComponent("Davide", 154, 3, 0, 0, true);

        controller.pickComponent("Davide", 51);
        controller.rotateComponent("Davide", 51, 2);
        controller.insertComponent("Davide", 51, 3, 1, 0, true);

        controller.pickComponent("Davide", 7);
        controller.rotateComponent("Davide", 7, 2);
        controller.insertComponent("Davide", 7, 3, 2, 0, true);

        controller.pickComponent("Davide", 96);
        controller.rotateComponent("Davide", 96, 0);
        controller.insertComponent("Davide", 96, 3, 3, 0, true);

        controller.pickComponent("Davide", 148);
        controller.rotateComponent("Davide", 148, 3);
        controller.insertComponent("Davide", 148, 3, 4, 0, true);

        controller.pickComponent("Davide", 19);
        controller.rotateComponent("Davide", 19, 3);
        controller.insertComponent("Davide", 19, 3, 5, 0, true);

        controller.pickComponent("Davide", 23);
        controller.rotateComponent("Davide", 23, 3);
        controller.insertComponent("Davide", 23, 3, 6, 0, true);

        controller.pickComponent("Davide", 71);
        controller.rotateComponent("Davide", 71, 0);
        controller.insertComponent("Davide", 71, 4, 0, 0, true);

        controller.pickComponent("Davide", 142);
        controller.rotateComponent("Davide", 142, 0);
        controller.insertComponent("Davide", 142, 4, 1, 0, true);

        controller.pickComponent("Davide", 85);
        controller.rotateComponent("Davide", 85, 0);
        controller.insertComponent("Davide", 85, 4, 2, 0, true);

        controller.pickComponent("Davide", 10);
        controller.rotateComponent("Davide", 10, 1);
        controller.insertComponent("Davide", 10, 4, 4, 0, true);

        controller.pickComponent("Davide", 84);
        controller.rotateComponent("Davide", 84, 0);
        controller.insertComponent("Davide", 84, 4, 5, 0, true);

        controller.pickComponent("Davide", 45);
        controller.rotateComponent("Davide", 45, 0);
        controller.insertComponent("Davide", 45, 4, 6, 0, true);

        controller.setReady("Davide");
        controller.setReady("Tommaso");
        controller.setReady("Demetrio");
        controller.setReady("Simone");

        // Player 1 Ship Check

        // Player 2 Ship Check

        // Player 3 Ship Check

        // Player 4 Ship Check

        // Alien Management
        Exception exception = assertThrows(ComponentNotValidException.class, () -> {
            controller.chooseAlien("Davide", new HashMap<>(Map.of(39, AlienType.ENGINE, 41, AlienType.CANNON)));
        });
        assertEquals("Cabin component not valid", exception.getMessage());

        exception = assertThrows(ComponentNotValidException.class, () -> {
            controller.chooseAlien("Davide", new HashMap<>(Map.of(34, AlienType.ENGINE, 41, AlienType.CANNON)));
        });
        assertEquals("Alien isn't compatible with staring cabin tile", exception.getMessage());

        exception = assertThrows(ComponentNotValidException.class, () -> {
            controller.chooseAlien("Davide", new HashMap<>(Map.of(45, AlienType.ENGINE)));
        });
        assertEquals("Alien ENGINE is not compatible with this cabin", exception.getMessage());

        exception = assertThrows(ComponentNotValidException.class, () -> {
            controller.chooseAlien("Davide", new HashMap<>(Map.of(51, AlienType.CANNON, 41, AlienType.CANNON)));
        });
        assertEquals("Alien CANNON is already present in the ship", exception.getMessage());

        controller.chooseAlien("Davide", new HashMap<>(Map.of(51, AlienType.ENGINE, 41, AlienType.CANNON)));
        controller.chooseAlien("Simone", new HashMap<>(Map.of(39, AlienType.CANNON)));

        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getCannonAlien());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getEngineAlien());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getCannonAlien());


        // Card Deck Initialization

        List<Card> cardDeck = controller.getModel().getBoard().getCardPile();
        cardDeck.clear();
        EpidemicCard card1 = new EpidemicCard(0, 2, false);
        cardDeck.add(card1);
        PiratesCard card2 = new PiratesCard(0, 2, false, 6, 7, 2, new ArrayList<>(List.of(new CannonFire(true, NORTH), new CannonFire(false, NORTH), new CannonFire(true, NORTH))));
        cardDeck.add(card2);
        Planet p1 = new Planet(new HashMap<>(Map.of(ColorType.RED, 2)));
        Planet p2 = new Planet(new HashMap<>(Map.of(ColorType.GREEN, 4)));
        PlanetCard card3 = new PlanetCard(0, 2, false, new ArrayList<>(List.of(p1, p2)), 3);
        cardDeck.add(card3);
        AbandonedStationCard card4 = new AbandonedStationCard(0, 1, true, 5, 1, new HashMap<>(Map.of(ColorType.YELLOW, 1, ColorType.GREEN, 1)));
        cardDeck.add(card4);
        StardustCard card5 = new StardustCard(0, 2, false);
        cardDeck.add(card5);
        MeteorSwarmCard card6 = new MeteorSwarmCard(0, 2, false, new ArrayList<>(List.of(new Meteor(false, NORTH), new Meteor(false, NORTH), new Meteor(true, WEST), new Meteor(false, WEST), new Meteor(false, WEST))));
        cardDeck.add(card6);
        SmugglersCard card7 = new SmugglersCard(0, 1, true, 4, 2, new HashMap<>(Map.of(ColorType.YELLOW, 1, ColorType.GREEN, 1, ColorType.BLUE, 2)),1);
        cardDeck.add(card7);
        PenaltyCombatZone penalty1 = new CountablePenaltyZone(4, MalusType.DAYS);
        PenaltyCombatZone penalty2 = new CountablePenaltyZone(3, MalusType.GOODS);
        PenaltyCombatZone penalty3 = new CannonFirePenaltyCombatZone(new ArrayList<>(List.of(new CannonFire(false, NORTH), new CannonFire(false, WEST), new CannonFire(false, EAST), new CannonFire(true, SOUTH))));
        List<WarLine> damages = new ArrayList<>();
        damages.add(new WarLine(CriteriaType.CANNON, penalty1));
        damages.add(new WarLine(CriteriaType.ENGINE, penalty2));
        damages.add(new WarLine(CriteriaType.CREW, penalty3));
        CombatZoneCard card8 = new CombatZoneCard(0, 2, false, damages);
        cardDeck.add(card8);
        Planet p3 = new Planet(new HashMap<>(Map.of(ColorType.RED, 1, ColorType.YELLOW, 1)));
        Planet p4 = new Planet(new HashMap<>(Map.of(ColorType.YELLOW, 1, ColorType.GREEN, 1, ColorType.BLUE, 1)));
        Planet p5 = new Planet(new HashMap<>(Map.of(ColorType.GREEN, 2)));
        Planet p6 = new Planet(new HashMap<>(Map.of(ColorType.YELLOW, 1)));
        PlanetCard card9 = new PlanetCard(0, 2, false, new ArrayList<>(List.of(p3, p4, p5, p6)), 2);
        cardDeck.add(card9);
        OpenSpaceCard card10 = new OpenSpaceCard(0, 1, false);
        cardDeck.add(card10);
        Planet p7 = new Planet(new HashMap<>(Map.of(ColorType.RED, 2)));
        Planet p8 = new Planet(new HashMap<>(Map.of(ColorType.RED, 1, ColorType.BLUE, 2)));
        Planet p9 = new Planet(new HashMap<>(Map.of(ColorType.YELLOW, 1)));
        PlanetCard card11 = new PlanetCard(0, 2, false, new ArrayList<>(List.of(p7, p8, p9)), 2);
        cardDeck.add(card11);
        AbandonedShipCard card12 = new AbandonedShipCard(0, 2, false, 5, 8, 2);
        cardDeck.add(card12);


        assertEquals(1, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().countExposedConnectors());
        assertEquals(1, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().countExposedConnectors());
        assertEquals(0, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().countExposedConnectors());
        assertEquals(2, controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio").getShip().countExposedConnectors());

        // Card 1

        controller.drawCard("Davide");

        assertEquals(6, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getCrew());
        assertEquals(5, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getCrew());
        assertEquals(6, controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio").getShip().getCrew());
        assertEquals(6, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getCrew());


        // Card 2

        controller.drawCard("Davide");

        controller.activateCannons("Davide", new ArrayList<>(List.of(10)), new ArrayList<>(List.of(124)));

        controller.getBoolean("Davide", true);

        assertEquals(7, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getCredits());
        assertEquals(7, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getBatteries());
        assertEquals(4, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Davide")))
                .findFirst().orElseThrow().getValue());


        // Card 3

        controller.drawCard("Davide");

        controller.getIndex("Davide", 0);
        controller.getIndex("Tommaso", 1);

        controller.updateGoods("Davide", new HashMap<>(Map.of(67, new ArrayList<>(List.of(ColorType.RED)), 63, new ArrayList<>(List.of(ColorType.RED)))), new ArrayList<>());

        controller.updateGoods("Tommaso", new HashMap<>(Map.of(29, new ArrayList<>(List.of(ColorType.GREEN, ColorType.GREEN, ColorType.GREEN)), 24, new ArrayList<>(List.of(ColorType.GREEN)))), new ArrayList<>());

        // Card 4

        controller.drawCard("Demetrio");

        controller.getBoolean("Demetrio", false);

        controller.getBoolean("Simone", false);

        controller.getBoolean("Davide", true);

        controller.updateGoods("Davide", new HashMap<>(Map.of(67, new ArrayList<>(List.of(ColorType.RED)), 63, new ArrayList<>(List.of(ColorType.RED)),19, new ArrayList<>(List.of(ColorType.YELLOW, ColorType.GREEN)))), new ArrayList<>());





        // Card 5

        controller.drawCard("Demetrio");

        assertEquals(-4, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso")))
                .findFirst().orElseThrow().getValue());
        assertEquals(-1, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Simone")))
                .findFirst().orElseThrow().getValue());

        assertEquals(-2, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio")))
                .findFirst().orElseThrow().getValue());

        assertEquals(-3, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Davide")))
                .findFirst().orElseThrow().getValue());



        // Card 6

        controller.drawCard("Simone");

        boolean finish = card6.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 9, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        finish = card6.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 3, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        finish = card6.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 9, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.chooseShipPart("Simone", 0);


        finish = card6.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 7, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        finish = card6.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 7, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        // Card 7

        controller.drawCard("Simone");

        controller.getBoolean("Simone", false);

        // Card 8

        controller.drawCard("Simone");

        controller.activateCannons("Simone", new ArrayList<>(), new ArrayList<>());

        controller.activateCannons("Demetrio", new ArrayList<>(List.of(1)), new ArrayList<>(List.of(128)));

        controller.activateCannons("Tommaso", new ArrayList<>(List.of(9)), new ArrayList<>(List.of(127)));

        assertEquals(-8, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Simone")))
                .findFirst().orElseThrow().getValue());

        controller.activateEngines("Tommaso", new ArrayList<>(), new ArrayList<>());

        controller.updateGoods("Tommaso", new HashMap<>(Map.of( 24, new ArrayList<>(List.of(ColorType.GREEN)))), new ArrayList<>());

        finish = card8.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 5, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.activateShield("Simone", 13);

        finish = card8.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 7, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.activateShield("Simone", 13);

        finish = card8.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 10, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        finish = card8.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 5, controller.getModel(), controller.getModel().getBoard(), "Simone");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        assertEquals(7, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getBatteries());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getDashboard(4, 1).isEmpty());



        // Card 9

        controller.drawCard("Demetrio");

        controller.getIndex("Demetrio", 1);

        controller.getIndex("Davide", 0);

        controller.getIndex("Tommaso", 2);

        controller.getIndex("Simone", 3);

        controller.updateGoods("Davide", new HashMap<>(Map.of(67, new ArrayList<>(List.of(ColorType.RED, ColorType.RED)), 63, new ArrayList<>(List.of(ColorType.RED)),19, new ArrayList<>(List.of(ColorType.YELLOW, ColorType.GREEN)), 23, new ArrayList<>(List.of(ColorType.YELLOW)))), new ArrayList<>());

        controller.updateGoods("Demetrio", new HashMap<>(Map.of( 27, new ArrayList<>(List.of(ColorType.GREEN, ColorType.YELLOW, ColorType.BLUE)))), new ArrayList<>());

        controller.updateGoods("Tommaso", new HashMap<>(Map.of( 31, new ArrayList<>(List.of(ColorType.GREEN, ColorType.GREEN, ColorType.GREEN)))), new ArrayList<>());

        controller.updateGoods("Simone", new HashMap<>(Map.of( 65, new ArrayList<>(List.of(ColorType.YELLOW)))), new ArrayList<>());

        assertEquals(-4, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio")))
                .findFirst().orElseThrow().getValue());
        assertEquals(-5, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Davide")))
                .findFirst().orElseThrow().getValue());

        assertEquals(-6, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso")))
                .findFirst().orElseThrow().getValue());

        assertEquals(-10, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Simone")))
                .findFirst().orElseThrow().getValue());

        // Card 10

        controller.drawCard("Demetrio");

        controller.activateEngines("Davide", new ArrayList<>(List.of(154)), new ArrayList<>(List.of(96)));

        controller.activateEngines("Tommaso", new ArrayList<>(List.of(14)), new ArrayList<>(List.of(99)));

        controller.activateEngines("Simone", new ArrayList<>(List.of(13)), new ArrayList<>(List.of( 92)));

        assertEquals(-0, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio")))
                .findFirst().orElseThrow().getValue());
        assertEquals(2, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Davide")))
                .findFirst().orElseThrow().getValue());

        assertEquals(-3, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso")))
                .findFirst().orElseThrow().getValue());

        assertEquals(-7, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Simone")))
                .findFirst().orElseThrow().getValue());


        // Card 11

        controller.drawCard("Davide");

        controller.getIndex("Davide", 2);

        controller.getIndex("Demetrio", 1);

        controller.getIndex("Tommaso", 0);

        controller.updateGoods("Davide", new HashMap<>(Map.of(67, new ArrayList<>(List.of(ColorType.RED, ColorType.RED)), 63, new ArrayList<>(List.of(ColorType.RED)),19, new ArrayList<>(List.of(ColorType.YELLOW, ColorType.GREEN)), 23, new ArrayList<>(List.of(ColorType.YELLOW, ColorType.YELLOW)))), new ArrayList<>());

        controller.updateGoods("Demetrio", new HashMap<>(Map.of( 27, new ArrayList<>(List.of(ColorType.GREEN, ColorType.YELLOW, ColorType.BLUE)), 61, new ArrayList<>(List.of(ColorType.RED)), 17, new ArrayList<>(List.of(ColorType.BLUE, ColorType.BLUE)))), new ArrayList<>());

        controller.updateGoods("Tommaso", new HashMap<>(Map.of( 31, new ArrayList<>(List.of(ColorType.GREEN, ColorType.GREEN, ColorType.GREEN)), 69, new ArrayList<>(List.of(ColorType.RED, ColorType.RED)))), new ArrayList<>());

        // Card 12

        controller.drawCard("Davide");

        controller.getBoolean("Davide", true);

        exception = assertThrows(CabinComponentNotValidException.class, () -> {
            controller.removeCrew("Davide", new ArrayList<>(List.of(45, 45, 45, 51, 34)));
        });
        assertEquals("Not enough crew in a cabin", exception.getMessage());

        controller.removeCrew("Davide", new ArrayList<>(List.of(45, 45, 41, 51, 34)));

        assertEquals(-2, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio")))
                .findFirst().orElseThrow().getValue());
        assertEquals(-3, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Davide")))
                .findFirst().orElseThrow().getValue());

        assertEquals(-5, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso")))
                .findFirst().orElseThrow().getValue());

        assertEquals(-7, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Simone")))
                .findFirst().orElseThrow().getValue());

        // Final Ranking

        assertEquals(3, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().countExposedConnectors());
        assertEquals(3, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().countExposedConnectors());
        assertEquals(1, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().countExposedConnectors());
        assertEquals(4, controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio").getShip().countExposedConnectors());



        assertEquals(2, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getCredits());
        assertEquals(17, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getCredits());
        assertEquals(19, controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio").getCredits());
        assertEquals(47, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getCredits());



    }

    @Test
    public void testCheckingShip() {

        // Building Ships
        List<String> usernames = new ArrayList<>(List.of("Tommaso", "Simone"));
        controller = new GameController(usernames, false);
        controller.startMatch();

        controller.pickComponent("Tommaso", 38);
        controller.rotateComponent("Tommaso", 38, 1);
        controller.insertComponent("Tommaso", 38, 2, 2, 0, true);

        controller.pickComponent("Tommaso", 63);
        controller.insertComponent("Tommaso", 63, 2, 4, 0, true);

        controller.setReady("Tommaso");

        controller.pickComponent("Simone", 127);
        controller.insertComponent("Simone", 127, 1, 1, 0, true);

        controller.pickComponent("Simone", 156);
        controller.insertComponent("Simone", 156, 2, 0, 3, true);

        controller.pickComponent("Simone", 15);
        controller.insertComponent("Simone", 15, 3, 0, 2, true);

        controller.pickComponent("Simone", 102);
        controller.insertComponent("Simone", 102, 1, 3, 0, true);

        controller.pickComponent("Simone", 126);
        controller.insertComponent("Simone", 126, 1, 4, 0, true);

        controller.pickComponent("Simone", 41);
        controller.insertComponent("Simone", 41, 2, 2, 0, true);

        controller.pickComponent("Simone", 60);
        controller.insertComponent("Simone", 60, 2, 4, 1, true);

        controller.pickComponent("Simone", 17);
        controller.insertComponent("Simone", 17, 2, 5, 1, true);

        controller.pickComponent("Simone", 20);
        controller.insertComponent("Simone", 20, 2, 6, 0, true);

        controller.pickComponent("Simone", 46);
        controller.insertComponent("Simone", 46, 3, 1, 3, true);

        controller.pickComponent("Simone", 95);
        controller.insertComponent("Simone", 95, 3, 3, 0, true);

        controller.pickComponent("Simone", 146);
        controller.insertComponent("Simone", 146, 3, 4, 2, true);

        controller.pickComponent("Simone", 30);
        controller.insertComponent("Simone", 30, 3, 5, 0, true);

        controller.pickComponent("Simone", 14);
        controller.insertComponent("Simone", 14, 4, 0, 0, true);

        controller.pickComponent("Simone", 80);
        controller.insertComponent("Simone", 80, 4, 1, 0, true);

        controller.pickComponent("Simone", 88);
        controller.insertComponent("Simone", 88, 4, 2, 0, true);

        controller.pickComponent("Simone", 89);
        controller.insertComponent("Simone", 89, 4, 4, 0, true);

        controller.pickComponent("Simone", 44);
        controller.insertComponent("Simone", 44, 4, 5, 0, true);

        controller.setReady("Simone");

        List<Integer> toRemoveTilesIDs = new ArrayList<>();
        toRemoveTilesIDs.add(127);

        controller.checkShip("Simone", toRemoveTilesIDs);


        Exception exception = assertThrows(IllegalStateException.class, () -> {
            controller.drawCard("Tommaso");
        });
        assertEquals("State is not DRAW_CARD", exception.getMessage());

        toRemoveTilesIDs.clear();
        toRemoveTilesIDs.add(156);
        toRemoveTilesIDs.add(15);

        controller.checkShip("Simone", toRemoveTilesIDs);

        toRemoveTilesIDs.clear();
        toRemoveTilesIDs.add(46);
        toRemoveTilesIDs.add(14);
        toRemoveTilesIDs.add(80);
        toRemoveTilesIDs.add(88);

        controller.checkShip("Simone", toRemoveTilesIDs);

        exception = assertThrows(IllegalStateException.class, () -> {
            controller.activateEngines("Tommaso", new ArrayList<>(), new ArrayList<>());
        });
        assertEquals("State is not WAIT_ENGINES", exception.getMessage());

        controller.drawCard("Tommaso");

    }


}