package it.polimi.ingsw.model.cards;

import com.sun.jdi.connect.Connector;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CabinComponent;
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
    private List<Component> discarded;
    private Component[] component;
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
        dashboard = new Optional[5][7];
        for (int i = 0; i < dashboard.length; i++) {
            for (int j = 0; j < dashboard[i].length; j++) {
                dashboard[i][j] = Optional.empty();
            }
        }

        dashboard1 = new Optional[5][7];
        for (int i = 0; i < dashboard1.length; i++) {
            for (int j = 0; j < dashboard1[i].length; j++) {
                dashboard1[i][j] = Optional.empty();
            }
        }

        dashboard2 = new Optional[5][7];
        for (int i = 0; i < dashboard2.length; i++) {
            for (int j = 0; j < dashboard2[i].length; j++) {
                dashboard2[i][j] = Optional.empty();
            }
        }

        discarded = new ArrayList<>();
        battery = 0;
        goods = new HashMap<>();
        directions = new ArrayList<>();
        ship1 = new Ship(dashboard, discarded, component);
        ship2 = new Ship(dashboard, discarded, component);
        ship3 = new Ship(dashboard, discarded, component);
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
    void testShouldGetRewardsIfFirePowerEnoughAndMovePlayer() throws Exception {
        // Initialization
        Ship testShip = new Ship(dashboard, discarded, component);
        PlayerData playerTester = new PlayerData(ColorType.BLUE, "Pippo", testShip, 50);
        players.add(new AbstractMap.SimpleEntry<>(playerTester, 16));
        SlaversCard slaversCard = new SlaversCard(2, false, 2, 10, 2, 1);
        // Add cannons to the ship
        ConnectorType[] connectors = {SINGLE, DOUBLE, SINGLE, SINGLE};
        CannonComponent cannon1 = new CannonComponent(connectors, NORTH, false);
        CannonComponent cannon2 = new CannonComponent(connectors, NORTH, true);
        CabinComponent cabin1 = new CabinComponent(connectors, false);
        CabinComponent cabin2 = new CabinComponent(connectors, false);
        BatteryComponent battery1 = new BatteryComponent(connectors, true);
        battery1.insertComponent(testShip, 0, 2);
        cannon1.insertComponent(testShip, 1, 1);
        cannon2.insertComponent(testShip, 1, 2);
        cabin1.insertComponent(testShip, 2, 1);
        cabin2.insertComponent(testShip, 2, 2);
        // call to the card
        slaversCard.resolve(board);
        // check
        assertEquals(4, testShip.getCrew());
        assertEquals(60, playerTester.getCredits());
        assertEquals(13, players.stream().filter(entry -> entry.getKey().equals(playerTester)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckThatCardIsUsedBySecondPLayer() throws Exception {
        // Initialization
        Ship testShip1 = new Ship(dashboard1, discarded, component);
        Ship testShip2 = new Ship(dashboard2, discarded, component);
        PlayerData playerTester1 = new PlayerData(ColorType.BLUE, "PLayerTester1", testShip1, 51);
        PlayerData playerTester2 = new PlayerData(ColorType.BLUE, "PlayerTester2", testShip2, 37);
        players.add(new AbstractMap.SimpleEntry<>(playerTester1, 30));
        players.add(new AbstractMap.SimpleEntry<>(playerTester2, 17));
        SlaversCard slaversCard = new SlaversCard(2, false, 1, 10, 10, 1);
        // Creation of the cannon
        ConnectorType[] connectors = {SINGLE, DOUBLE, SINGLE, SINGLE};
        CannonComponent cannon1 = new CannonComponent(connectors, NORTH, false);
        CannonComponent cannon2 = new CannonComponent(connectors, NORTH, true);
        CabinComponent cabin1 = new CabinComponent(connectors, false);
        BatteryComponent battery1 = new BatteryComponent(connectors, true);
        cannon2.insertComponent(testShip1, 1, 2);
        cannon2.insertComponent(testShip1, 2, 2);
        cannon2.insertComponent(testShip2, 1, 1);
        cabin1.insertComponent(testShip1, 2, 1);
        battery1.insertComponent(testShip2, 3, 3);
        // call to the card
        slaversCard.resolve(board);
        // check
        assertEquals(1, playerTester1.getShip().getCrew());
        assertEquals(51, playerTester1.getCredits());
        assertEquals(0, playerTester2.getShip().getCrew());
        assertEquals(47, playerTester2.getCredits());
        assertEquals(2, playerTester2.getShip().getBatteries());
        assertEquals(30, players.stream().filter(entry -> entry.getKey().equals(playerTester1)).findFirst().get().getValue());
        assertEquals(4, players.stream().filter(entry -> entry.getKey().equals(playerTester2)).findFirst().get().getValue());
    }

    @Test

    void testShouldCheckThatCardIsUsedByOnlyTheFirsPLayer() throws Exception {
        // Initialization
        Ship testShip1 = new Ship(dashboard1, discarded, component);
        Ship testShip2 = new Ship(dashboard2, discarded, component);
        PlayerData playerTester1 = new PlayerData(ColorType.BLUE, "PLayerTester1", testShip1, 51);
        PlayerData playerTester2 = new PlayerData(ColorType.BLUE, "PlayerTester2", testShip2, 37);
        players.add(new AbstractMap.SimpleEntry<>(playerTester1, 30));
        players.add(new AbstractMap.SimpleEntry<>(playerTester2, 17));
        SlaversCard slaversCard = new SlaversCard(2, false, 1, 10, 10, 1);
        // Creation of the cannon
        ConnectorType[] connectors = {SINGLE, DOUBLE, SINGLE, SINGLE};
        CannonComponent cannon1 = new CannonComponent(connectors, NORTH, false);
        CannonComponent cannon2 = new CannonComponent(connectors, NORTH, true);
        CabinComponent cabin1 = new CabinComponent(connectors, false);
        BatteryComponent battery1 = new BatteryComponent(connectors, true);
        cannon2.insertComponent(testShip2, 1, 2);
        cannon2.insertComponent(testShip2, 2, 2);
        cannon2.insertComponent(testShip1, 1, 1);
        cabin1.insertComponent(testShip2, 2, 1);
        battery1.insertComponent(testShip1, 3, 3);
        // call to the card
        slaversCard.resolve(board);
        // check
        assertEquals(0, playerTester1.getShip().getCrew());
        assertEquals(61, playerTester1.getCredits());
        assertEquals(2, playerTester2.getShip().getCrew());
        assertEquals(37, playerTester2.getCredits());
        assertEquals(2, playerTester1.getShip().getBatteries());
        assertEquals(17, players.stream().filter(entry -> entry.getKey().equals(playerTester2)).findFirst().get().getValue());
        assertEquals(20, players.stream().filter(entry -> entry.getKey().equals(playerTester1)).findFirst().get().getValue());
    }

}

