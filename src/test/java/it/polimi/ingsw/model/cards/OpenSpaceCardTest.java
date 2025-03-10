package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.BatteryComponent;
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

    @BeforeEach
    void setUp() {

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

        // adding instances for testing
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
    void check_players_final_positions_after_resolve() throws Exception {
        ConnectorType[] connectors = { ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.EMPTY };

        EngineComponent singleEngine = new EngineComponent(connectors, DirectionType.SOUTH, false);
        EngineComponent doubleEngine = new EngineComponent(connectors, DirectionType.SOUTH, true);

//        BatteryComponent doubleHoldBatteries = new BatteryComponent(connectors, false);
        BatteryComponent tripleHoldBatteries = new BatteryComponent(connectors, true);

        // player1
        singleEngine.insertComponent(ship1, 3, 2);

        // player 2
        doubleEngine.insertComponent(ship2, 2, 2);
        singleEngine.insertComponent(ship2, 3, 2);
        singleEngine.insertComponent(ship2, 3, 5);
        tripleHoldBatteries.insertComponent(ship2, 4, 4);

        // player 3
        singleEngine.insertComponent(ship3, 3, 5);

        OpenSpaceCard card = new OpenSpaceCard(2, false);
        card.resolve(board);

        assertEquals(19,
                players.stream()
                        .filter(entry -> entry.getKey().equals(player3))
                        .findFirst()
                        .get()
                        .getValue());
        assertEquals(20,
                players.stream()
                        .filter(entry -> entry.getKey().equals(player2))
                        .findFirst()
                        .get()
                        .getValue());
        assertEquals(17,
                players.stream()
                        .filter(entry -> entry.getKey().equals(player1))
                        .findFirst()
                        .get()
                        .getValue());
    }
}
