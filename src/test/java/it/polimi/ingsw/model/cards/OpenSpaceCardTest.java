package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.EngineComponent;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.properties.DirectionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.components.utils.ConnectorType.*;
import static it.polimi.ingsw.model.properties.DirectionType.SOUTH;
import static org.junit.jupiter.api.Assertions.*;

class OpenSpaceCardTest {
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
    private Optional<Component> component;

    private List<Optional<Component>> discarded;
    private int battery;
    private Map<ColorType, Integer> goods;
    private List<DirectionType> directions;

    @BeforeEach
    void setUp() {
        dashboard1 = new Optional[4][4];
        for (int i = 0; i < dashboard1.length; i++) {
            for (int j = 0; j < dashboard1[i].length; j++) {
                dashboard1[i][j] = Optional.empty();
            }
        }
        dashboard2 = new Optional[4][4];
        for (int i = 0; i < dashboard2.length; i++) {
            for (int j = 0; j < dashboard2[i].length; j++) {
                dashboard2[i][j] = Optional.empty();
            }
        }
        dashboard3 = new Optional[4][4];
        for (int i = 0; i < dashboard3.length; i++) {
            for (int j = 0; j < dashboard3[i].length; j++) {
                dashboard3[i][j] = Optional.empty();
            }
        }

        discarded = new ArrayList<>();
        component = Optional.empty();
        battery = 0;
        goods = new HashMap<>();
        directions = new ArrayList<>();
        ship1 = new Ship(dashboard1, discarded, component, 1, battery, goods, directions);
        ship2 = new Ship(dashboard2, discarded, component, 1, battery, goods, directions);
        ship3 = new Ship(dashboard3, discarded, component, 1, battery, goods, directions);
        players = new ArrayList<>();
        player1 = new PlayerData(ColorType.BLUE, "Simone", ship1, 0);
        player2 = new PlayerData(ColorType.BLUE, "Davide", ship2, 0);
        player3 = new PlayerData(ColorType.BLUE, "Tommaso", ship3, 0);
        players.add(new AbstractMap.SimpleEntry<>(player1, 16));
        players.add(new AbstractMap.SimpleEntry<>(player2, 14));
        players.add(new AbstractMap.SimpleEntry<>(player3, 18));
        board = new Board(players);
    }

    @AfterEach
    void tearDown() {
        players.clear();
    }

    @Test
    void testShouldCheckThatPlayerMoves() {
        ConnectorType[] connectors = {SINGLE, DOUBLE, SINGLE, EMPTY};
        EngineComponent engine1 = new EngineComponent(connectors, SOUTH, false);
        EngineComponent engine2 = new EngineComponent(connectors, SOUTH, true);

//        dashboard1[1][1] = Optional.of(engine1);

        dashboard2[1][2] = Optional.of(engine2);
        dashboard2[2][1] = Optional.of(engine1);

//        dashboard3[1][3] = Optional.of(engine1);

        OpenSpaceCard card = new OpenSpaceCard(2, false);
        card.resolve(board);

        assertEquals(18,
                players.stream()
                        .filter(entry -> entry.getKey().equals(player3))
                        .findFirst()
                        .get()
                        .getValue());
        assertEquals(15,
                players.stream()
                        .filter(entry -> entry.getKey().equals(player2))
                        .findFirst()
                        .get()
                        .getValue());
        assertEquals(16,
                players.stream()
                        .filter(entry -> entry.getKey().equals(player1))
                        .findFirst()
                        .get()
                        .getValue());
    }
}
