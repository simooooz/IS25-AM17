package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.components.utils.ConnectorType.SINGLE;
import static it.polimi.ingsw.model.game.objects.AlienType.CANNON;
import static org.junit.jupiter.api.Assertions.*;

class AbandonedShipCardTest {
    private Optional<Component>[][] dashboard;
    private List<Component> discarded;
    private ConnectorType[] connectors;
    private Component[] reserves;

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
        reserves = new Component[2];
        ship1 = new Ship(dashboard, discarded, reserves);
        ship2 = new Ship(dashboard, discarded, reserves);
        ship3 = new Ship(dashboard, discarded, reserves);
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
    void testShouldNotUpdateCardIfCrewNotEnough() throws Exception{
        // Initialization
        Ship testShip = new Ship(dashboard, discarded, reserves);
        PlayerData playerTester = new PlayerData(ColorType.BLUE, "Pippo", testShip, 50);
        players.add(new AbstractMap.SimpleEntry<>(playerTester, 16));
        CabinComponent cabin1 = new CabinComponent(connectors, false);
        CabinComponent cabin2 = new CabinComponent(connectors, false);
        cabin1.insertComponent(testShip, 2, 2);
        cabin2.insertComponent(testShip, 1, 2);
        cabin2.setAlien(CANNON, testShip);
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(2, false, 10, 0, 0);
        // call to the card
        abandonedShipCard.resolve(board);
        // check
        assertEquals(4, testShip.getCrew());
        assertEquals(50, playerTester.getCredits());
        assertEquals(16, players.stream().filter(entry -> entry.getKey().equals(playerTester)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckIfTheParameterAreUpdateWithNoPlayersInDaysPositionBehind() throws Exception{
        // Initialization
        Ship testShip = new Ship(dashboard, discarded, reserves);
        PlayerData playerTester = new PlayerData(ColorType.BLUE, "Pippo", testShip, 50);
        players.add(new AbstractMap.SimpleEntry<>(playerTester, 30));
        CabinComponent cabin1 = new CabinComponent(connectors, false);
        CabinComponent cabin2 = new CabinComponent(connectors, false);
        cabin1.insertComponent(testShip, 2, 2);
        cabin2.insertComponent(testShip, 1, 2);
        cabin2.setAlien(CANNON, testShip);
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(2, false, 3, 6, 4);
        // call to the card
        abandonedShipCard.resolve(board);
        // check
        assertEquals(5, testShip.getCrew());
        assertEquals(56, playerTester.getCredits());
        assertEquals(26, players.stream().filter(entry -> entry.getKey().equals(playerTester)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckIfTheParameterAreUpdateWithPlayersInDaysPositionBehind() throws Exception{
        // initialization
        Ship testShip = new Ship(dashboard, discarded, reserves);
        PlayerData playerTester = new PlayerData(ColorType.BLUE, "Pippo", testShip, 50);
        players.add(new AbstractMap.SimpleEntry<>(playerTester, 16));
        CabinComponent cabin1 = new CabinComponent(connectors, false);
        CabinComponent cabin2 = new CabinComponent(connectors, false);
        cabin1.insertComponent(testShip, 2, 2);
        cabin2.insertComponent(testShip, 1, 2);
        cabin2.setAlien(CANNON, testShip);
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(2, false, 3, 6, 7);
        // call to the card
        abandonedShipCard.resolve(board);
        // check
        assertEquals(5, testShip.getCrew());
        assertEquals(56, playerTester.getCredits());
        assertEquals(6, players.stream().filter(entry -> entry.getKey().equals(playerTester)).findFirst().get().getValue());
    }

}

