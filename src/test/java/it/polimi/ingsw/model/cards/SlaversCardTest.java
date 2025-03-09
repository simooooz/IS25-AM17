package it.polimi.ingsw.model.cards;

import com.sun.jdi.connect.Connector;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.properties.DirectionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.*;

import static it.polimi.ingsw.model.components.utils.ConnectorType.*;
import static it.polimi.ingsw.model.components.utils.ConnectorType.SINGLE;
import static it.polimi.ingsw.model.properties.DirectionType.NORTH;
import static org.junit.jupiter.api.Assertions.*;

class SlaversCardTest {
    private Optional<Component>[][] dashboard;
    private Optional<Component>[][] dashboard1;
    private Optional<Component>[][] dashboard2;
    private List<Optional<Component>> discarded;
    private Optional<Component> component;
    private int battery;
    private Map<ColorType, Integer> goods;
    private List<DirectionType> directions;
    private Ship ship1;
    private Ship ship2;
    private Ship ship3;
    private List<AbstractMap.SimpleEntry<PlayerData, Integer>> players;
    private PlayerData player1;
    private PlayerData player2;
    private PlayerData player3;
    private Board board;

    @BeforeEach
    void setUp() {
        dashboard = new Optional[3][3];
        for (int i = 0; i < dashboard.length; i++) {
            for (int j = 0; j < dashboard[i].length; j++) {
                dashboard[i][j] = Optional.empty();
            }
        }

        dashboard1 = new Optional[3][3];
        for (int i = 0; i < dashboard1.length; i++) {
            for (int j = 0; j < dashboard1[i].length; j++) {
                dashboard1[i][j] = Optional.empty();
            }
        }

        dashboard2 = new Optional[3][3];
        for (int i = 0; i < dashboard2.length; i++) {
            for (int j = 0; j < dashboard2[i].length; j++) {
                dashboard2[i][j] = Optional.empty();
            }
        }

        discarded = new ArrayList<>();
        component = Optional.empty();
        battery = 0;
        goods = new HashMap<>();
        directions = new ArrayList<>();
        ship1 = new Ship(dashboard, discarded, component, 1, battery, goods, directions);
        ship2 = new Ship(dashboard, discarded, component, 1, battery, goods, directions);
        ship3 = new Ship(dashboard, discarded, component, 1, battery, goods, directions);
        players = new ArrayList<>();
        player1 = new PlayerData(ColorType.BLUE, "Simone", ship1, 0);
        player2 = new PlayerData(ColorType.BLUE, "Davide", ship2, 0);
        player3 = new PlayerData(ColorType.BLUE, "Tommaso", ship3, 0);
        players.add(new AbstractMap.SimpleEntry<>(player1, 11));
        players.add(new AbstractMap.SimpleEntry<>(player2, 12));
        players.add(new AbstractMap.SimpleEntry<>(player3, 15));
        board = new Board(players);
    }

    @AfterEach
    void tearDown() {
        players.clear();
    }

    @Test
    void testShouldLoseCrewIfFirePowerNotEnough() {
        // Initialization
        Ship testShip = new Ship(dashboard, discarded, component, 10, battery, goods, directions);
        PlayerData playerTester = new PlayerData(ColorType.BLUE, "Pippo", testShip, 50);
        players.add(new AbstractMap.SimpleEntry<>(playerTester, 16));
        SlaversCard slaversCard = new SlaversCard(2, false, 5, 10, 2, 3);
        // call to the card
        slaversCard.resolve(board);
        // check
        assertEquals(5, testShip.getCrew());
        assertEquals(50, playerTester.getCredits());
        assertEquals(16, players.stream().filter(entry -> entry.getKey().equals(playerTester)).findFirst().get().getValue());
    }

    @Test
    void testShouldGetRewardsIfFirePowerEnoughAndMovePlayer() {
        // Initialization
        Ship testShip = new Ship(dashboard, discarded, component, 10, 2, goods, directions);
        PlayerData playerTester = new PlayerData(ColorType.BLUE, "Pippo", testShip, 50);
        players.add(new AbstractMap.SimpleEntry<>(playerTester, 16));
        SlaversCard slaversCard = new SlaversCard(2, false, 5, 10, 2, 1);
        // Add cannons to the ship
        ConnectorType[] connectors = {SINGLE, DOUBLE, SINGLE, SINGLE};
        CannonComponent cannon1 = new CannonComponent(connectors, NORTH, false);
        CannonComponent cannon2 = new CannonComponent(connectors, NORTH, true);
        dashboard[1][1] = Optional.of(cannon1);
        dashboard[1][2] = Optional.of(cannon2);
        // call to the card
        slaversCard.resolve(board);
        // check
        assertEquals(10, testShip.getCrew());
        assertEquals(60, playerTester.getCredits());
        assertEquals(13, players.stream().filter(entry -> entry.getKey().equals(playerTester)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckThatCardIsUsedBySecondPLayer() {
        // Initialization
        Ship testShip1 = new Ship(dashboard1, discarded, component, 100, 1, goods, directions);
        Ship testShip2 = new Ship(dashboard2, discarded, component, 50, 10, goods, directions);
        PlayerData playerTester1 = new PlayerData(ColorType.BLUE, "PLayerTester1", testShip1, 51);
        PlayerData playerTester2 = new PlayerData(ColorType.BLUE, "PlayerTester2", testShip2, 37);
        players.add(new AbstractMap.SimpleEntry<>(playerTester1, 17));
        players.add(new AbstractMap.SimpleEntry<>(playerTester2, 30));
        SlaversCard slaversCard = new SlaversCard(2, false, 5, 10, 10, 3);
        // Creation of the cannon
        ConnectorType[] connectors = {SINGLE, DOUBLE, SINGLE, SINGLE};
        CannonComponent cannon1 = new CannonComponent(connectors, NORTH, false);
        CannonComponent cannon2 = new CannonComponent(connectors, NORTH, true);
        dashboard1[1][1] = Optional.of(cannon1);
        dashboard1[1][2] = Optional.of(cannon2);
        dashboard1[2][2] = Optional.of(cannon1);
        dashboard1[2][1] = Optional.of(cannon2);
        dashboard2[1][1] = Optional.of(cannon2);
        // call to the card
        slaversCard.resolve(board);
        // check
        assertEquals(45, playerTester2.getShip().getCrew());
        assertEquals(37, playerTester2.getCredits());
        assertEquals(100, playerTester1.getShip().getCrew());
        assertEquals(61, playerTester1.getCredits());
        assertEquals(0, playerTester1.getShip().getBatteries());
        assertEquals(30, players.stream().filter(entry -> entry.getKey().equals(playerTester2)).findFirst().get().getValue());
        assertEquals(4, players.stream().filter(entry -> entry.getKey().equals(playerTester1)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckThatCardIsUsedByOnlyTheFirsPLayer() {
        // Initialization
        Ship testShip1 = new Ship(dashboard1, discarded, component, 100, 1, goods, directions);
        Ship testShip2 = new Ship(dashboard2, discarded, component, 50, 10, goods, directions);
        PlayerData playerTester1 = new PlayerData(ColorType.BLUE, "PLayerTester1", testShip1, 51);
        PlayerData playerTester2 = new PlayerData(ColorType.BLUE, "PlayerTester2", testShip2, 37);
        players.add(new AbstractMap.SimpleEntry<>(playerTester1, 30));
        players.add(new AbstractMap.SimpleEntry<>(playerTester2, 17));
        SlaversCard slaversCard = new SlaversCard(2, false, 5, 10, 10, 3);
        // creation of the cannon and assignation to the right ship
        ConnectorType[] connectors = {SINGLE, DOUBLE, SINGLE, SINGLE};
        CannonComponent cannon1 = new CannonComponent(connectors, NORTH, false);
        CannonComponent cannon2 = new CannonComponent(connectors, NORTH, true);
        dashboard1[1][1] = Optional.of(cannon1);
        dashboard1[1][2] = Optional.of(cannon2);
        dashboard1[2][2] = Optional.of(cannon1);
        dashboard1[2][1] = Optional.of(cannon2);
        dashboard2[1][1] = Optional.of(cannon2);
        // call to the card
        slaversCard.resolve(board);
        // check
        assertEquals(50, playerTester2.getShip().getCrew());
        assertEquals(37, playerTester2.getCredits());
        assertEquals(100, playerTester1.getShip().getCrew());
        assertEquals(61, playerTester1.getCredits());
        assertEquals(0, playerTester1.getShip().getBatteries());
        assertEquals(17, players.stream().filter(entry -> entry.getKey().equals(playerTester2)).findFirst().get().getValue());
        assertEquals(20, players.stream().filter(entry -> entry.getKey().equals(playerTester1)).findFirst().get().getValue());
    }

}