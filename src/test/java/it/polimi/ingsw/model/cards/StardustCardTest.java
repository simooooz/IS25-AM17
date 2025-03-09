package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.properties.DirectionType;
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

    private Optional<Component>[][] dashboard1;
    private Optional<Component>[][] dashboard2;
    private Optional<Component>[][] dashboard3;
    private List<Optional<Component>> discarded;
    private Optional<Component> component;
    private int battery;
    private Map<ColorType, Integer> goods;
    private List<DirectionType> directions;

    @BeforeEach
    void setUp() {
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
        dashboard3 = new Optional[5][7];
        for (int i = 0; i < dashboard3.length; i++) {
            for (int j = 0; j < dashboard3[i].length; j++) {
                dashboard3[i][j] = Optional.empty();
            }
        }
        discarded = new ArrayList<>();
        component = Optional.empty();
        battery = 1;
        goods = new HashMap<>();
        directions = new ArrayList<>();

        ship1 = new Ship(dashboard1, discarded, component, 1, battery, goods, directions);
        ship2 = new Ship(dashboard2, discarded, component, 1, battery, goods, directions);
        ship3 = new Ship(dashboard3, discarded, component, 1, battery, goods, directions);

        player1 = new PlayerData(ColorType.BLUE, "Simone", ship1, 0);
        player2 = new PlayerData(ColorType.BLUE, "Davide", ship2, 0);
        player3 = new PlayerData(ColorType.BLUE, "Tommaso", ship3, 0);

        players = new ArrayList<>();

        players.add(new AbstractMap.SimpleEntry<>(player1, 23));
        players.add(new AbstractMap.SimpleEntry<>(player2, 24));
        players.add(new AbstractMap.SimpleEntry<>(player3, 25));

        board = new Board(players);
    }

    @AfterEach
    void tearDown() {
        players.clear();
    }

    @Test
    void testShouldCheckThatPlayerMoves() {
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

        dashboard1[0][0] = Optional.of(component1);
        component1.setX(0);
        component1.setY(0);
        dashboard1[0][1] = Optional.of(component2);
        component2.setX(1);
        component2.setY(0);
        dashboard1[1][0] = Optional.of(component11);
        component11.setX(0);
        component11.setY(1);
        dashboard1[1][1] = Optional.of(component4);
        component4.setX(1);
        component4.setY(1);                             // #exposedConnectors = 3

        dashboard2[0][0] = Optional.of(component5);
        component5.setX(0);
        component5.setY(0);
        dashboard2[0][1] = Optional.of(component3);
        component3.setX(1);
        component3.setY(0);
        dashboard2[1][0] = Optional.of(component6);
        component6.setX(0);
        component6.setY(1);                             // #exposedConnectors = 4

        dashboard3[0][0] = Optional.of(component42);
        component42.setX(0);
        component42.setY(0);
        dashboard3[0][1] = Optional.of(component32);
        component32.setX(1);
        component32.setY(0);                            // #exposedConnectors = 3

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
