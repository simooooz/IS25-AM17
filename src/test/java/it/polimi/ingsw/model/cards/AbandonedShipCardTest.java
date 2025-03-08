package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.properties.DirectionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AbandonedShipCardTest {
    private Optional<Component>[][] dashboard;
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
    void testShouldNotUpdateCardIfCrewNotEnough() {
        // Initialization
        Ship testShip = new Ship(dashboard, discarded, component, 5, battery, goods, directions);
        PlayerData playerTester = new PlayerData(ColorType.BLUE, "Pippo", testShip, 50);
        players.add(new AbstractMap.SimpleEntry<>(playerTester, 16));
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(2, false, 10, 0, 0);
        // call to the card
        abandonedShipCard.resolve(board);
        // check
        assertEquals(5, testShip.getCrew());
        assertEquals(50, playerTester.getCredits());
        assertEquals(16, players.stream().filter(entry -> entry.getKey().equals(playerTester)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckIfTheParameterAreUpdateWithNoPlayersInDaysPositionBehind() {
        // Initialization
        Ship testShip = new Ship(dashboard, discarded, component, 10, battery, goods, directions);
        PlayerData playerTester = new PlayerData(ColorType.BLUE, "Pippo", testShip, 50);
        players.add(new AbstractMap.SimpleEntry<>(playerTester, 30));
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(2, false, 5, 6, 4);
        // call to the card
        abandonedShipCard.resolve(board);
        // check
        assertEquals(5, testShip.getCrew());
        assertEquals(56, playerTester.getCredits());
        assertEquals(26, players.stream().filter(entry -> entry.getKey().equals(playerTester)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckIfTheParameterAreUpdateWithPlayersInDaysPositionBehind() {
        // initialization
        Ship testShip = new Ship(dashboard, discarded, component, 10, battery, goods, directions);
        PlayerData playerTester = new PlayerData(ColorType.BLUE, "Pippo", testShip, 50);
        players.add(new AbstractMap.SimpleEntry<>(playerTester, 16));
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(2, false, 5, 6, 7);
        // call to the card
        abandonedShipCard.resolve(board);
        // check
        assertEquals(5, testShip.getCrew());
        assertEquals(56, playerTester.getCredits());
        assertEquals(6, players.stream().filter(entry -> entry.getKey().equals(playerTester)).findFirst().get().getValue());
    }

}

