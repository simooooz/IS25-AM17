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

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbandonedStationCardTest {
//
//    // Declare these as class fields instead of local variables
//    private Optional<Component>[][] dashboard;
//    private List<Optional<Component>> discarded;
//    private Optional<Component> component;
//    private int battery;
//    private Map<ColorType, Integer> goods;
//    private Map<ColorType, Integer> cardGoods;
//    private List<DirectionType> directions;
//    private Ship ship1;
//    private Ship ship2;
//    private Ship ship3;
//    private List<AbstractMap.SimpleEntry<PlayerData, Integer>> players;
//    private PlayerData player1;
//    private PlayerData player2;
//    private PlayerData player3;
//    private Board board;
//
//    @BeforeEach
//    void setUp() {
//        // Initialize the fields here
//        dashboard = new Optional[3][3]; // Specify actual dimensions
//        // Initialize each cell with Optional.empty()
//        for (int i = 0; i < dashboard.length; i++) {
//            for (int j = 0; j < dashboard[i].length; j++) {
//                dashboard[i][j] = Optional.empty();
//            }
//        }
//
//        discarded = new ArrayList<>();
//        component = Optional.empty();
//        battery = 0;
//        directions = new ArrayList<>();
//
//        goods = Map.ofEntries(
//                Map.entry(ColorType.RED, 10),
//                Map.entry(ColorType.GREEN, 15),
//                Map.entry(ColorType.BLUE, 20),
//                Map.entry(ColorType.YELLOW, 25)
//        );
//        cardGoods = Map.ofEntries(
//                Map.entry(ColorType.GREEN, 10),
//                Map.entry(ColorType.BLUE, 15),
//                Map.entry(ColorType.RED, 20),
//                Map.entry(ColorType.YELLOW, 25)
//        );
//
//        // initialization of the ships
//        ship1 = new Ship(dashboard, discarded, component, 1, battery, goods, directions);
//        ship2 = new Ship(dashboard, discarded, component, 1, battery, goods, directions);
//        ship3 = new Ship(dashboard, discarded, component, 1, battery, goods, directions);
//        // initilization of the players
//        players = new ArrayList<>();
//        player1 = new PlayerData(ColorType.BLUE, "Simone", ship1, 0);
//        player2 = new PlayerData(ColorType.BLUE, "Davide", ship2, 0);
//        player3 = new PlayerData(ColorType.BLUE, "Tommaso", ship3, 0);
//        // add player with their initial value of goods
//        players.add(new AbstractMap.SimpleEntry<>(player1, 11));
//        players.add(new AbstractMap.SimpleEntry<>(player2, 12));
//        players.add(new AbstractMap.SimpleEntry<>(player3, 15));
//        // create the board Game
//        board = new Board(players);
//    }
//
//    @AfterEach
//    void tearDown() {
//        players.clear();
//    }
//
//    @Test
//    void testShouldNotUpdateCardIfCrewNotEnough() {
//        // Initialization
//        Ship testShip = new Ship(dashboard, discarded, component, 5, battery, goods, directions);
//        PlayerData playerTester = new PlayerData(ColorType.BLUE, "Pippo", testShip, 50);
//        players.add(new AbstractMap.SimpleEntry<>(playerTester, 16));
//        AbandonedStationCard abandonedStationCard = new AbandonedStationCard(2, false, 10, 0, cardGoods);
//        // call to the card
//        abandonedStationCard.resolve(board);
//        // check
//        assertEquals(3, testShip.getGoods().size() - 1);
//        assertEquals(16, players.stream()
//                .filter(entry -> entry.getKey().equals(playerTester))
//                .findFirst()
//                .get()
//                .getValue());
//    }
//
//    @Test
//    void testShouldCheckIfTheParameterAreUpdateWithNoPlayersInDaysPositionBehind() {
//        // Initialization
//        Ship testShip = new Ship(dashboard, discarded, component, 5, battery, goods, directions);
//        PlayerData playerTester = new PlayerData(ColorType.BLUE, "Pippo", testShip, 50);
//        players.add(new AbstractMap.SimpleEntry<>(playerTester, 16));
//        AbandonedStationCard abandonedStationCard = new AbandonedStationCard(2, false, 10, 0, cardGoods);
//        // call to the card
//        abandonedStationCard.resolve(board);
//        // check
//        assertEquals(8, testShip.getGoods().size() - 1);
//        assertEquals(26, players.stream()
//                .filter(entry -> entry.getKey().equals(playerTester))
//                .findFirst()
//                .get()
//                .getValue());
//    }
//
//    @Test
//    void testShouldCheckIfTheParameterAreUpdateWithPlayersInDaysPositionBehind() {
//        // Initialization
//        Ship testShip = new Ship(dashboard, discarded, component, 5, battery, goods, directions);
//        PlayerData playerTester = new PlayerData(ColorType.BLUE, "Pippo", testShip, 50);
//        players.add(new AbstractMap.SimpleEntry<>(playerTester, 16));
//        AbandonedStationCard abandonedStationCard = new AbandonedStationCard(2, false, 10, 0, cardGoods);
//        // call to the card
//        abandonedStationCard.resolve(board);
//        // check
//        assertEquals(8, testShip.getGoods().size() - 1);
//        assertEquals(6, players.stream()
//                .filter(entry -> entry.getKey().equals(playerTester))
//                .findFirst()
//                .get()
//                .getValue());
//    }

}