package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.properties.DirectionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OpenSpaceCardTest {

    private List<String> usernames;
    private PlayerData player1;
    private PlayerData player2;
    private PlayerData player3;
    private PlayerData player4;
    private Board board;
    private OpenSpaceCard openSpaceCard;
    private ConnectorType[] connectors;
    private EngineComponent singleEngine1;
    private EngineComponent singleEngine2;
    private EngineComponent singleEngine3;
    private EngineComponent singleEngine4;
    private EngineComponent doubleEngine1;
    private BatteryComponent doubleBattery;
    private BatteryComponent tripleBattery;

    private ModelFacade modelFacade;

    @BeforeEach
    void setUp() {

        connectors = new ConnectorType[]{ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL};
        usernames = new ArrayList<>();
        usernames.add("Simone");
        usernames.add("Davide");
        usernames.add("Tommaso");
        usernames.add("Giovanni");

        player1 = new PlayerData(usernames.get(0));
        player2 = new PlayerData(usernames.get(1));
        player3 = new PlayerData(usernames.get(2));
        player4 = new PlayerData(usernames.get(3));

        modelFacade = new ModelFacade(usernames);
        board = modelFacade.getBoard();

        board.moveToBoard(player4);
        board.movePlayer(player4, 14);
        board.moveToBoard(player3);
        board.movePlayer(player3, 15);
        board.moveToBoard(player1);
        board.movePlayer(player1, 15);
        board.moveToBoard(player2);
        board.movePlayer(player2, 14);

        singleEngine1 = new EngineComponent(connectors, DirectionType.SOUTH, false);
        board.getCommonComponents().add(singleEngine1);
        singleEngine2 = new EngineComponent(connectors, DirectionType.SOUTH, false);
        board.getCommonComponents().add(singleEngine2);
        singleEngine3 = new EngineComponent(connectors, DirectionType.SOUTH, false);
        board.getCommonComponents().add(singleEngine3);
        singleEngine4 = new EngineComponent(connectors, DirectionType.SOUTH, false);
        board.getCommonComponents().add(singleEngine4);
        doubleEngine1 = new EngineComponent(connectors, DirectionType.SOUTH, true);
        board.getCommonComponents().add(doubleEngine1);
        doubleBattery = new BatteryComponent(connectors, false);
        board.getCommonComponents().add(doubleBattery);
        tripleBattery = new BatteryComponent(connectors, true);
        board.getCommonComponents().add(tripleBattery);


        openSpaceCard = new OpenSpaceCard(2, false);
        board.getCardPile().clear();
        board.getCardPile().add(openSpaceCard);

    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void check_players_final_positions_after_resolve() throws Exception {

        // player1
        singleEngine1.showComponent();
        singleEngine1.pickComponent(board, player1.getShip());
        singleEngine1.insertComponent(player1.getShip(), 3, 2);
        singleEngine1.weldComponent();
        CabinComponent cabin1 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin1);
        cabin1.showComponent();
        cabin1.pickComponent(board, player1.getShip());
        cabin1.insertComponent(player1.getShip(), 3, 3);
        cabin1.weldComponent();

        // player 2
        doubleEngine1.showComponent();
        doubleEngine1.pickComponent(board, player2.getShip());
        doubleEngine1.insertComponent(player2.getShip(), 2, 2);
        doubleEngine1.weldComponent();

        singleEngine2.showComponent();
        singleEngine2.pickComponent(board, player2.getShip());
        singleEngine2.insertComponent(player2.getShip(), 3, 2);
        singleEngine2.weldComponent();

        singleEngine3.showComponent();
        singleEngine3.pickComponent(board, player2.getShip());
        singleEngine3.insertComponent(player2.getShip(), 3, 5);
        singleEngine3.weldComponent();

        tripleBattery.showComponent();
        tripleBattery.pickComponent(board, player2.getShip());
        tripleBattery.insertComponent(player2.getShip(), 3, 4);
        tripleBattery.weldComponent();

        CabinComponent cabin2 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin2);
        cabin2.showComponent();
        cabin2.pickComponent(board, player2.getShip());
        cabin2.insertComponent(player2.getShip(), 3, 3);
        cabin2.weldComponent();

        // player 3
        singleEngine4.showComponent();
        singleEngine4.pickComponent(board, player3.getShip());
        singleEngine4.insertComponent(player3.getShip(), 3, 5);
        singleEngine4.weldComponent();

        CabinComponent cabin3 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin3);
        cabin3.showComponent();
        cabin3.pickComponent(board, player3.getShip());
        cabin3.insertComponent(player3.getShip(), 3, 3);
        cabin3.weldComponent();

        // player 4
        doubleBattery.showComponent();
        doubleBattery.pickComponent(board, player4.getShip());
        doubleBattery.insertComponent(player4.getShip(), 3, 4);
        doubleBattery.weldComponent();

        CabinComponent cabin4 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin4);
        cabin4.showComponent();
        cabin4.pickComponent(board, player4.getShip());
        cabin4.insertComponent(player4.getShip(), 3, 3);
        cabin4.weldComponent();

        modelFacade.nextCard(player4.getUsername());

        //player 2 response
        List<BatteryComponent> batteries = new ArrayList<>(List.of(tripleBattery));
        List<EngineComponent> engines = new ArrayList<>(List.of(doubleEngine1));

        modelFacade.activateEngines(player2.getUsername(), batteries, engines);



        assertEquals(19,
                board.getPlayers().stream()
                        .filter(entry -> entry.getKey().equals(player3))
                        .findFirst()
                        .get()
                        .getValue());
        assertEquals(21,
                board.getPlayers().stream()
                        .filter(entry -> entry.getKey().equals(player2))
                        .findFirst()
                        .get()
                        .getValue());
        assertEquals(17,
                board.getPlayers().stream()
                        .filter(entry -> entry.getKey().equals(player1))
                        .findFirst()
                        .get()
                        .getValue());
        assertTrue(board.getStartingDeck().contains(player4));
    }
}
