package it.polimi.ingsw.controller;

import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.utils.*;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.common.model.enums.DirectionType.*;
import static org.junit.jupiter.api.Assertions.*;

public class LearnerModeGameSimulationTest {
    private GameController controller;

    @BeforeEach
    void setUp() {
        List<String> usernames = new ArrayList<>(List.of("Tommaso", "Simone", "Davide", "Demetrio"));
        controller = new GameController(usernames, true);
        controller.startMatch();
    }


    @Test
    public void testCompleteLearnerModeGame() {

        // Building Ships
        controller.pickComponent("Tommaso", 130);
        controller.rotateComponent("Tommaso", 130, 3);
        controller.insertComponent("Tommaso", 130, 0, 3, 0, true);

        controller.pickComponent("Tommaso", 68);
        controller.rotateComponent("Tommaso", 68, 2);
        controller.insertComponent("Tommaso", 68, 1, 4, 0, true);

        controller.pickComponent("Tommaso", 14);
        controller.rotateComponent("Tommaso", 14, 1);
        controller.insertComponent("Tommaso", 14, 1, 2, 0, true);

        controller.pickComponent("Tommaso", 58);
        controller.rotateComponent("Tommaso", 58, 1);
        controller.insertComponent("Tommaso", 58, 1, 3, 0, true);

        controller.pickComponent("Tommaso", 25);
        controller.rotateComponent("Tommaso", 25, 2);
        controller.insertComponent("Tommaso", 25, 2, 1, 0, true);

        controller.pickComponent("Tommaso", 44);
        controller.rotateComponent("Tommaso", 44, 3);
        controller.insertComponent("Tommaso", 44, 2, 2, 0, true);

        controller.pickComponent("Tommaso", 115);
        controller.rotateComponent("Tommaso", 115, 1);
        controller.insertComponent("Tommaso", 115, 2, 5, 0, true);

        controller.pickComponent("Tommaso", 29);
        controller.rotateComponent("Tommaso", 29, 1);
        controller.insertComponent("Tommaso", 29, 2, 4, 0, true);

        controller.pickComponent("Tommaso", 96);
        controller.rotateComponent("Tommaso", 96, 0);
        controller.insertComponent("Tommaso", 96, 3, 1, 0, true);

        controller.pickComponent("Tommaso", 31);
        controller.rotateComponent("Tommaso", 31, 0);
        controller.insertComponent("Tommaso", 31, 3, 2, 0, true);

        controller.pickComponent("Tommaso", 51);
        controller.rotateComponent("Tommaso", 51, 1);
        controller.insertComponent("Tommaso", 51, 4, 2, 0, true);

        controller.pickComponent("Tommaso", 77);
        controller.rotateComponent("Tommaso", 77, 0);
        controller.insertComponent("Tommaso", 77, 3, 3, 0, true);

        controller.pickComponent("Tommaso", 5);
        controller.rotateComponent("Tommaso", 5, 3);
        controller.insertComponent("Tommaso", 5, 3, 4, 0, true);

        controller.pickComponent("Tommaso", 147);
        controller.rotateComponent("Tommaso", 147, 1);
        controller.insertComponent("Tommaso", 147, 4, 4, 0, true);

        controller.pickComponent("Tommaso", 89);
        controller.rotateComponent("Tommaso", 89, 0);
        controller.insertComponent("Tommaso", 89, 4, 5, 0, true);


        controller.pickComponent("Simone", 118);
        controller.rotateComponent("Simone", 118, 3);
        controller.insertComponent("Simone", 118, 0, 3, 0, true);

        controller.pickComponent("Simone", 13);
        controller.rotateComponent("Simone", 13, 1);
        controller.insertComponent("Simone", 13, 1, 2, 0, true);

        controller.pickComponent("Simone", 150);
        controller.rotateComponent("Simone", 150, 1);
        controller.insertComponent("Simone", 150, 1, 3, 0, true);

        controller.pickComponent("Simone", 116);
        controller.rotateComponent("Simone", 116, 1);
        controller.insertComponent("Simone", 116, 1, 4, 0, true);

        controller.pickComponent("Simone", 125);
        controller.rotateComponent("Simone", 125, 0);
        controller.insertComponent("Simone", 125, 2, 1, 0, true);

        controller.pickComponent("Simone", 149);
        controller.rotateComponent("Simone", 149, 3);
        controller.insertComponent("Simone", 149, 2, 2, 0, true);

        controller.pickComponent("Simone", 63);
        controller.rotateComponent("Simone", 63, 3);
        controller.insertComponent("Simone", 63, 2, 4, 0, true);

        controller.pickComponent("Simone", 62);
        controller.rotateComponent("Simone", 62, 2);
        controller.insertComponent("Simone", 62, 2, 5, 0, true);

        controller.pickComponent("Simone", 30);
        controller.rotateComponent("Simone", 30, 2);
        controller.insertComponent("Simone", 30, 3, 1, 0, true);

        controller.pickComponent("Simone", 56);
        controller.rotateComponent("Simone", 56, 3);
        controller.insertComponent("Simone", 56, 3, 2, 0, true);

        controller.pickComponent("Simone", 18);
        controller.rotateComponent("Simone", 18, 1);
        controller.insertComponent("Simone", 18, 3, 3, 0, true);

        controller.pickComponent("Simone", 60);
        controller.rotateComponent("Simone", 60, 2);
        controller.insertComponent("Simone", 60, 3, 4, 0, true);

        controller.pickComponent("Simone", 124);
        controller.rotateComponent("Simone", 124, 1);
        controller.insertComponent("Simone", 124, 3, 5, 0, true);

        controller.pickComponent("Simone", 84);
        controller.rotateComponent("Simone", 84, 0);
        controller.insertComponent("Simone", 84, 4, 1, 0, true);

        controller.pickComponent("Simone", 99);
        controller.rotateComponent("Simone", 99, 0);
        controller.insertComponent("Simone", 99, 4, 2, 0, true);

        controller.pickComponent("Simone", 94);
        controller.rotateComponent("Simone", 94, 0);
        controller.insertComponent("Simone", 94, 4, 4, 0, true);

        controller.pickComponent("Simone", 16);
        controller.rotateComponent("Simone", 16, 0);
        controller.insertComponent("Simone", 16, 4, 5, 0, true);


        controller.pickComponent("Demetrio", 103);
        controller.rotateComponent("Demetrio", 103, 0);
        controller.insertComponent("Demetrio", 103, 0, 3, 0, true);

        controller.pickComponent("Demetrio", 152);
        controller.rotateComponent("Demetrio", 152, 3);
        controller.insertComponent("Demetrio", 152, 1, 2, 0, true);

        controller.pickComponent("Demetrio", 4);
        controller.rotateComponent("Demetrio", 4, 2);
        controller.insertComponent("Demetrio", 4, 1, 3, 0, true);

        controller.pickComponent("Demetrio", 129);
        controller.rotateComponent("Demetrio", 129, 0);
        controller.insertComponent("Demetrio", 129, 1, 4, 0, true);

        controller.pickComponent("Demetrio", 12);
        controller.rotateComponent("Demetrio", 12, 0);
        controller.insertComponent("Demetrio", 12, 2, 1, 0, true);

        controller.pickComponent("Demetrio", 65);
        controller.rotateComponent("Demetrio", 65, 0);
        controller.insertComponent("Demetrio", 65, 2, 2, 0, true);

        controller.pickComponent("Demetrio", 24);
        controller.rotateComponent("Demetrio", 24, 2);
        controller.insertComponent("Demetrio", 24, 2, 4, 0, true);

        controller.pickComponent("Demetrio", 153);
        controller.rotateComponent("Demetrio", 153, 1);
        controller.insertComponent("Demetrio", 153, 2, 5, 0, true);

        controller.pickComponent("Demetrio", 1);
        controller.rotateComponent("Demetrio", 1, 0);
        controller.insertComponent("Demetrio", 1, 3, 1, 0, true);

        controller.pickComponent("Demetrio", 55);
        controller.rotateComponent("Demetrio", 55, 1);
        controller.insertComponent("Demetrio", 55, 3, 2, 0, true);

        controller.pickComponent("Demetrio", 146);
        controller.rotateComponent("Demetrio", 146, 2);
        controller.insertComponent("Demetrio", 146, 3, 3, 0, true);

        controller.pickComponent("Demetrio", 6);
        controller.rotateComponent("Demetrio", 6, 0);
        controller.insertComponent("Demetrio", 6, 3, 4, 0, true);

        controller.pickComponent("Demetrio", 47);
        controller.rotateComponent("Demetrio", 47, 1);
        controller.insertComponent("Demetrio", 47, 3, 5, 0, true);

        controller.pickComponent("Demetrio", 66);
        controller.rotateComponent("Demetrio", 66, 1);
        controller.insertComponent("Demetrio", 66, 4, 1, 0, true);

        controller.pickComponent("Demetrio", 95);
        controller.rotateComponent("Demetrio", 95, 0);
        controller.insertComponent("Demetrio", 95, 4, 2, 0, true);

        controller.pickComponent("Demetrio", 61);
        controller.rotateComponent("Demetrio", 61, 2);
        controller.insertComponent("Demetrio", 61, 4, 4, 0, true);

        controller.pickComponent("Demetrio", 97);
        controller.rotateComponent("Demetrio", 97, 0);
        controller.insertComponent("Demetrio", 97, 4, 5, 0, true);


        controller.pickComponent("Davide", 102);
        controller.rotateComponent("Davide", 102, 0);
        controller.insertComponent("Davide", 102, 0, 3, 0, true);

        controller.pickComponent("Davide", 132);
        controller.rotateComponent("Davide", 132, 3);
        controller.insertComponent("Davide", 132, 1, 2, 0, true);

        controller.pickComponent("Davide", 38);
        controller.rotateComponent("Davide", 38, 3);
        controller.insertComponent("Davide", 38, 1, 3, 0, true);

        controller.pickComponent("Davide", 109);
        controller.rotateComponent("Davide", 109, 1);
        controller.insertComponent("Davide", 109, 1, 4, 0, true);

        controller.pickComponent("Davide", 151);
        controller.rotateComponent("Davide", 151, 2);
        controller.insertComponent("Davide", 151, 2, 1, 0, true);

        controller.pickComponent("Davide", 15);
        controller.rotateComponent("Davide", 15, 3);
        controller.insertComponent("Davide", 15, 2, 2, 0, true);

        controller.pickComponent("Davide", 148);
        controller.rotateComponent("Davide", 148, 0);
        controller.insertComponent("Davide", 148, 2, 4, 0, true);

        controller.pickComponent("Davide", 17);
        controller.rotateComponent("Davide", 17, 2);
        controller.insertComponent("Davide", 17, 2, 5, 0, true);

        controller.pickComponent("Davide", 144);
        controller.rotateComponent("Davide", 144, 3);
        controller.insertComponent("Davide", 144, 3, 1, 0, true);

        controller.pickComponent("Davide", 67);
        controller.rotateComponent("Davide", 67, 3);
        controller.insertComponent("Davide", 67, 3, 2, 0, true);

        controller.pickComponent("Davide", 92);
        controller.rotateComponent("Davide", 92, 0);
        controller.insertComponent("Davide", 92, 3, 3, 0, true);

        controller.pickComponent("Davide", 136);
        controller.rotateComponent("Davide", 136, 0);
        controller.insertComponent("Davide", 136, 3, 4, 0, true);

        controller.pickComponent("Davide", 41);
        controller.rotateComponent("Davide", 41, 0);
        controller.insertComponent("Davide", 41, 3, 5, 0, true);

        controller.pickComponent("Davide", 37);
        controller.rotateComponent("Davide", 37, 2);
        controller.insertComponent("Davide", 37, 4, 1, 0, true);

        controller.pickComponent("Davide", 82);
        controller.rotateComponent("Davide", 82, 0);
        controller.insertComponent("Davide", 82, 4, 2, 0, true);

        controller.pickComponent("Davide", 3);
        controller.rotateComponent("Davide", 3, 3);
        controller.insertComponent("Davide", 3, 4, 4, 0, true);

        controller.pickComponent("Davide", 19);
        controller.rotateComponent("Davide", 19, 0);
        controller.insertComponent("Davide", 19, 4, 5, 0, true);

        controller.setReady("Tommaso");
        controller.setReady("Simone");
        controller.setReady("Davide");
        controller.setReady("Demetrio");

        controller.checkShip("Davide", new ArrayList<>(List.of(136)));

        assertEquals(9, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().countExposedConnectors());
        assertEquals(5, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().countExposedConnectors());
        assertEquals(14, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().countExposedConnectors());
        assertEquals(5, controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio").getShip().countExposedConnectors());

        assertEquals(4, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso")))
                .findFirst().orElseThrow().getValue());
        assertEquals(2, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Simone")))
                .findFirst().orElseThrow().getValue());
        assertEquals(1, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio")))
                .findFirst().orElseThrow().getValue());
        assertEquals(0, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Davide")))
                .findFirst().orElseThrow().getValue());

        // Card Deck Initialization

        List<Card> cardDeck = controller.getModel().getBoard().getCardPile();
        cardDeck.clear();

        MeteorSwarmCard card1 = new MeteorSwarmCard(0, 1, true, new ArrayList<>(List.of(new Meteor(true, NORTH), new Meteor(false, WEST), new Meteor(false, EAST))));
        cardDeck.add(card1);

        AbandonedStationCard card2 = new AbandonedStationCard(0, 1, true, 5, 1, new HashMap<>(Map.of(ColorType.YELLOW, 1, ColorType.GREEN, 1)));
        cardDeck.add(card2);

        SmugglersCard card3 = new SmugglersCard(0, 1, true, 4, 2, new HashMap<>(Map.of(ColorType.YELLOW, 1, ColorType.GREEN, 1, ColorType.BLUE, 1)), 1);
        cardDeck.add(card3);

        OpenSpaceCard card4 = new OpenSpaceCard(0, 1, true);
        cardDeck.add(card4);

        AbandonedShipCard card5 = new AbandonedShipCard(0, 1, true, 3, 4, 1);
        cardDeck.add(card5);

        PenaltyCombatZone penalty1 = new CountablePenaltyZone(3, MalusType.DAYS);
        PenaltyCombatZone penalty2 = new CountablePenaltyZone(2, MalusType.CREW);
        PenaltyCombatZone penalty3 = new CannonFirePenaltyCombatZone(new ArrayList<>(List.of(new CannonFire(false, SOUTH), new CannonFire(true, SOUTH))));
        List<AbstractMap.SimpleEntry<CriteriaType, PenaltyCombatZone>> damages = new ArrayList<>();
        damages.add(new AbstractMap.SimpleEntry<>(CriteriaType.CREW, penalty1));
        damages.add(new AbstractMap.SimpleEntry<>(CriteriaType.ENGINE, penalty2));
        damages.add(new AbstractMap.SimpleEntry<>(CriteriaType.CANNON, penalty3));
        CombatZoneCard card6 = new CombatZoneCard(0, 1, true, damages);
        cardDeck.add(card6);

        Planet p1 = new Planet(new HashMap<>(Map.of(ColorType.RED, 2)));
        Planet p2 = new Planet(new HashMap<>(Map.of(ColorType.RED, 1, ColorType.BLUE, 2)));
        Planet p3 = new Planet(new HashMap<>(Map.of(ColorType.YELLOW, 1)));
        PlanetCard card7 = new PlanetCard(0, 1, true, new ArrayList<>(List.of(p1, p2, p3)), 2);
        cardDeck.add(card7);

        StardustCard card8 = new StardustCard(0, 1, true);
        cardDeck.add(card8);


        // Card 1

        controller.drawCard("Tommaso");

        // First Meteor
        boolean finish = card1.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 8, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.activateCannons("Demetrio", new ArrayList<>(List.of(4)), new ArrayList<>(List.of(129)));

        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(1, 4).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getDashboard(1, 4).isEmpty());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getDashboard(1, 4).isEmpty());
        assertFalse(controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio").getShip().getDashboard(1, 4).isEmpty());

        // Second Meteor
        finish = card1.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 5, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        // Third Meteor
        finish = card1.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        controller.activateShield("Tommaso", 5);
        controller.activateShield("Davide", 3);
        controller.activateShield("Demetrio", 4);

        assertEquals(4, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getBatteries());
        assertEquals(7, controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio").getShip().getBatteries());
        assertEquals(4, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getBatteries());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getDashboard(1, 3).isPresent());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getDashboard(1, 3).isPresent());
        assertTrue(controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getDashboard(1, 3).isPresent());

        // Card 2

        controller.drawCard("Tommaso");

        controller.getBoolean("Tommaso", true);

        controller.updateGoods("Tommaso", new HashMap<>(Map.of(29, new ArrayList<>(List.of(ColorType.GREEN, ColorType.YELLOW)))), new ArrayList<>());

        assertEquals(1, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getGoods().get(ColorType.GREEN));
        assertEquals(1, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getGoods().get(ColorType.YELLOW));

        assertEquals(3, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso")))
                .findFirst().orElseThrow().getValue());

        // Card 3

        controller.drawCard("Tommaso");

        controller.updateGoods("Tommaso", new HashMap<>(Map.of(29, new ArrayList<>())), new ArrayList<>());
        controller.updateGoods("Simone", new HashMap<>(), new ArrayList<>(List.of(16, 16)));
        controller.updateGoods("Demetrio", new HashMap<>(), new ArrayList<>(List.of(6, 6)));
        controller.updateGoods("Davide", new HashMap<>(), new ArrayList<>(List.of(3, 15)));

        assertEquals(0, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getGoods().get(ColorType.GREEN));
        assertEquals(0, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getGoods().get(ColorType.YELLOW));
        assertEquals(4, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getBatteries());
        assertEquals(5, controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio").getShip().getBatteries());
        assertEquals(2, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getBatteries());


        // Card 4

        controller.drawCard("Tommaso");

        controller.activateEngines("Tommaso", new ArrayList<>(List.of(5)), new ArrayList<>(List.of(96)));

        controller.activateEngines("Simone", new ArrayList<>(List.of(16)), new ArrayList<>(List.of(99)));

        controller.activateEngines("Demetrio", new ArrayList<>(), new ArrayList<>());

        controller.activateEngines("Davide", new ArrayList<>(), new ArrayList<>());

        assertEquals(7, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso")))
                .findFirst().orElseThrow().getValue());
        assertEquals(5, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Simone")))
                .findFirst().orElseThrow().getValue());
        assertEquals(2, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Davide")))
                .findFirst().orElseThrow().getValue());
        assertEquals(1, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio")))
                .findFirst().orElseThrow().getValue());


        // Card 5

        controller.drawCard("Tommaso");

        controller.getBoolean("Tommaso", true);

        controller.removeCrew("Tommaso", new ArrayList<>(List.of(51, 51, 44)));

        assertEquals(3, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getCrew());
        assertEquals(4, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getCredits());
        assertEquals(6, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso")))
                .findFirst().orElseThrow().getValue());

        // Card 6

        controller.drawCard("Tommaso");

        // First War Line
        assertEquals(0, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Simone")))
                .findFirst().orElseThrow().getValue());

        // Second War Line
        controller.activateEngines("Tommaso", new ArrayList<>(), new ArrayList<>());

        controller.activateEngines("Davide", new ArrayList<>(), new ArrayList<>());

        controller.activateEngines("Demetrio", new ArrayList<>(List.of(1)), new ArrayList<>(List.of(97)));

        controller.removeCrew("Davide", new ArrayList<>(List.of(37, 37)));

        assertEquals(6, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getCrew());

        // Third War Line
        controller.activateCannons("Tommaso", new ArrayList<>(), new ArrayList<>());

        finish = card6.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 12, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }

        finish = card6.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 10, controller.getModel(), controller.getModel().getBoard(), "Tommaso");
        if (finish) { controller.getModel().getBoard().pickNewCard(controller.getModel()); }


        // Card 7

        controller.drawCard("Tommaso");

        controller.getIndex("Tommaso", 2);

        controller.getIndex("Davide", 1);

        controller.getIndex("Demetrio", null);

        controller.getIndex("Simone", 0);


        controller.updateGoods("Tommaso", new HashMap<>(Map.of(29, new ArrayList<>(List.of(ColorType.YELLOW)))), new ArrayList<>());

        controller.updateGoods("Simone", new HashMap<>(Map.of(62, new ArrayList<>(List.of(ColorType.RED)), 63, new ArrayList<>(List.of(ColorType.RED)))), new ArrayList<>());

        controller.updateGoods("Davide", new HashMap<>(Map.of(17, new ArrayList<>(List.of(ColorType.BLUE, ColorType.BLUE)), 67, new ArrayList<>(List.of(ColorType.RED)))), new ArrayList<>());

        assertEquals(1, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().getGoods().get(ColorType.YELLOW));
        assertEquals(2, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().getGoods().get(ColorType.RED));
        assertEquals(2, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getGoods().get(ColorType.BLUE));
        assertEquals(1, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().getGoods().get(ColorType.RED));
        assertEquals(4, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso")))
                .findFirst().orElseThrow().getValue());
        assertEquals(1, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio")))
                .findFirst().orElseThrow().getValue());
        assertEquals(-1, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Davide")))
                .findFirst().orElseThrow().getValue());
        assertEquals(-2, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Simone")))
                .findFirst().orElseThrow().getValue());



        // Card 8

        assertEquals(9, controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso").getShip().countExposedConnectors());
        assertEquals(5, controller.getModel().getBoard().getPlayerEntityByUsername("Simone").getShip().countExposedConnectors());
        assertEquals(16, controller.getModel().getBoard().getPlayerEntityByUsername("Davide").getShip().countExposedConnectors());
        assertEquals(5, controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio").getShip().countExposedConnectors());

        controller.drawCard("Tommaso");

        assertEquals(-4, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Demetrio")))
                .findFirst().orElseThrow().getValue());
        assertEquals(-6, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Tommaso")))
                .findFirst().orElseThrow().getValue());
        assertEquals(-7, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Simone")))
                .findFirst().orElseThrow().getValue());
        assertEquals(-18, controller.getModel().getBoard().getPlayers()
                .stream()
                .filter(entry -> entry.getKey().equals(controller.getModel().getBoard().getPlayerEntityByUsername("Davide")))
                .findFirst().orElseThrow().getValue());




        // Final Ranking

        List<PlayerData> finalRank = controller.getModel().getBoard().calcRanking();

        assertEquals(11, finalRank.stream().filter(p -> p.getUsername().equals("Simone")).findFirst().orElseThrow().getCredits());
        assertEquals(9, finalRank.stream().filter(p -> p.getUsername().equals("Tommaso")).findFirst().orElseThrow().getCredits());
        assertEquals(6, finalRank.stream().filter(p -> p.getUsername().equals("Demetrio")).findFirst().orElseThrow().getCredits());
        assertEquals(5, finalRank.stream().filter(p -> p.getUsername().equals("Davide")).findFirst().orElseThrow().getCredits());


    }
}
