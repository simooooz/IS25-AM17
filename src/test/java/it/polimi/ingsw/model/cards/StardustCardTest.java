package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.components.utils.ConnectorType.*;
import static it.polimi.ingsw.model.properties.DirectionType.NORTH;
import static it.polimi.ingsw.model.properties.DirectionType.SOUTH;
import static org.junit.jupiter.api.Assertions.*;

class StardustCardTest {
    private PlayerData player1;
    private PlayerData player2;
    private PlayerData player3;

    private List<AbstractMap.SimpleEntry<PlayerData, Integer>> players;

    private Ship ship1;
    private Ship ship2;
    private Ship ship3;

    private Board board;

    @BeforeEach
    void setUp() throws Exception {
        Optional<Component>[][] dashboard1 = new Optional[5][7];
        for (int i = 0; i < dashboard1.length; i++) {
            for (int j = 0; j < dashboard1[i].length; j++) {
                dashboard1[i][j] = Optional.empty();
            }
        }
        Optional<Component>[][] dashboard2 = new Optional[5][7];
        for (int i = 0; i < dashboard2.length; i++) {
            for (int j = 0; j < dashboard2[i].length; j++) {
                dashboard2[i][j] = Optional.empty();
            }
        }
        Optional<Component>[][] dashboard3 = new Optional[5][7];
        for (int i = 0; i < dashboard3.length; i++) {
            for (int j = 0; j < dashboard3[i].length; j++) {
                dashboard3[i][j] = Optional.empty();
            }
        }
        List<Component> discarded = new ArrayList<>();
        Component[] reserves = new Component[2];
        ship1 = new Ship(dashboard1, discarded, reserves);
        ship2 = new Ship(dashboard2, discarded, reserves);
        ship3 = new Ship(dashboard3, discarded, reserves);

        players = new ArrayList<>();
        player1 = new PlayerData(ColorType.BLUE, "simo", ship1, 0);
        player2 = new PlayerData(ColorType.RED, "davide", ship2, 0);
        player3 = new PlayerData(ColorType.GREEN, "tommy", ship3, 0);

        board = new Board(players);

        board.moveToStartingDeck(player1);
        board.moveToStartingDeck(player2);
        board.moveToStartingDeck(player3);

        board.moveToBoard(player3);
        board.movePlayer(player3, 25);
        board.moveToBoard(player2);
        board.movePlayer(player2, 24);
        board.moveToBoard(player1);
        board.movePlayer(player1, 23);
    }

    @AfterEach
    void tearDown() {
        players.clear();
    }

    @Test
    void check_players_final_positions_after_resolve() throws Exception {
        ConnectorType[] connectors1 = {SINGLE, DOUBLE, SINGLE, EMPTY};
        ConnectorType[] connectors2 = {EMPTY, EMPTY, DOUBLE, UNIVERSAL};
        ConnectorType[] connectors3 = {DOUBLE, EMPTY, EMPTY, UNIVERSAL};
        ConnectorType[] connectors4 = {DOUBLE, SINGLE, EMPTY, UNIVERSAL};
        Component component1 = new Component(connectors1);
        Component component5 = new Component(connectors1);
        Component component6 = new Component(connectors1);
        Component component11 = new Component(connectors1);
        Component component2 = new Component(connectors2);
        Component component3 = new Component(connectors3);
        Component component32 = new Component(connectors3);
        Component component4 = new Component(connectors4);
        Component component42 = new Component(connectors4);

        component1.insertComponent(ship1, 2, 2);
        component2.insertComponent(ship1, 2, 3);
        component11.insertComponent(ship1, 3, 2);
        component4.insertComponent(ship1, 3, 3);    // #exposedConnectors = 3

        component5.insertComponent(ship2, 2, 2);
        component3.insertComponent(ship2, 2, 3);
        component6.insertComponent(ship2, 3, 2);    // #exposedConnectors = 4

        component42.insertComponent(ship3, 2, 2);
        component32.insertComponent(ship3, 2, 3);   // #exposedConnectors = 3

        StardustCard card = new StardustCard(1, false);
        card.resolve(board);

        assertEquals(20,
                players.stream()
                        .filter(entry -> entry.getKey().equals(player1))
                        .findFirst()
                        .get()
                        .getValue());
        assertEquals(19,
                players.stream()
                        .filter(entry -> entry.getKey().equals(player2))
                        .findFirst()
                        .get()
                        .getValue());
        assertEquals(22,
                players.stream()
                        .filter(entry -> entry.getKey().equals(player3))
                        .findFirst()
                        .get()
                        .getValue());
    }
}
