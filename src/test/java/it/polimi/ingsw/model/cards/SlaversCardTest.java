package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.properties.DirectionType.EAST;
import static it.polimi.ingsw.model.properties.DirectionType.NORTH;
import static org.junit.jupiter.api.Assertions.*;

class SlaversCardTest {
    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private PlayerData p3;
    private ModelFacade modelFacade;
    private Board board;
    private ConnectorType[] connectors;
    private OddComponent odd1;
    private CabinComponent cabin1;
    private CabinComponent cabin2;
    private CabinComponent cabin3;
    private CannonComponent cannon1;
    private CannonComponent cannon2;
    private CannonComponent cannon3;
    private BatteryComponent battery1;

    @BeforeEach
    void setUp() {;
        connectors = new ConnectorType[]{ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL};

        usernames = new ArrayList<>();
        usernames.add("Simone");
        usernames.add("Davide");
        usernames.add("Tommaso");

        p1 = new PlayerData(usernames.get(0));
        p2 = new PlayerData(usernames.get(1));
        p3 = new PlayerData(usernames.get(2));

        p1.setCredits(50);
        p2.setCredits(40);
        p3.setCredits(30);

        modelFacade = new ModelFacade(usernames);
        board = modelFacade.getBoard();

        board.moveToBoard(p1);
        board.movePlayer(p1, 9);
        board.moveToBoard(p2);
        board.movePlayer(p2, 9);
        board.moveToBoard(p3);
        board.movePlayer(p3, 10);

        odd1 = new OddComponent(connectors, AlienType.ENGINE);
        board.getCommonComponents().add(odd1);

        odd1.showComponent();
        odd1.pickComponent(board, p1.getShip());
        odd1.insertComponent(p1.getShip(), 2, 1);
        odd1.weldComponent();

        cabin1 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin1);

        cabin1.showComponent();
        cabin1.pickComponent(board, p1.getShip());
        cabin1.insertComponent(p1.getShip(), 1, 2);
        cabin1.weldComponent();

        cabin2 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin2);

        cabin2.showComponent();
        cabin2.pickComponent(board, p2.getShip());
        cabin2.insertComponent(p2.getShip(), 1, 2);
        cabin2.weldComponent();

        cabin3 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin3);

        cabin3.showComponent();
        cabin3.pickComponent(board, p1.getShip());
        cabin3.insertComponent(p1.getShip(), 2, 2);
        cabin3.weldComponent();
        cabin3.setAlien(AlienType.ENGINE, p1.getShip());

    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }


    @Test
    void testShouldGetRewardsIfFirePowerEnoughAndMovePlayer() {
        SlaversCard slaversCard = new SlaversCard(2, false, 5, 5, 1, 1);
        board.getCardPile().clear();
        board.getCardPile().add(slaversCard);

        cannon1 = new CannonComponent(connectors, NORTH, false);
        board.getCommonComponents().add(cannon1);

        cannon2 = new CannonComponent(connectors, NORTH, true);
        board.getCommonComponents().add(cannon2);

        battery1 = new BatteryComponent(connectors, false);
        board.getCommonComponents().add(battery1);

        cannon1.showComponent();
        cannon1.pickComponent(board, p1.getShip());
        cannon1.insertComponent(p1.getShip(), 3, 2);
        cannon1.weldComponent();

        cannon2.showComponent();
        cannon2.pickComponent(board, p1.getShip());
        cannon2.insertComponent(p1.getShip(), 2, 3);
        cannon2.weldComponent();

        battery1.showComponent();
        battery1.pickComponent(board, p1.getShip());
        battery1.insertComponent(p1.getShip(), 3, 4);
        battery1.weldComponent();

        List<BatteryComponent> batteries = new ArrayList<>();
        batteries.add(battery1);
        List<CannonComponent> cannons = new ArrayList<>();
        cannons.add(cannon2);

        modelFacade.nextCard(p1.getUsername());
        modelFacade.activateCannons(p1.getUsername(), batteries, cannons);
        modelFacade.getBoolean(p1.getUsername(), true);

        assertEquals(14, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckThatCardIsUsedBySecondPLayer() throws Exception {

        SlaversCard slaversCard = new SlaversCard(2, false, 2, 5, 1, 1);
        board.getCardPile().clear();
        board.getCardPile().add(slaversCard);

        cannon1 = new CannonComponent(connectors, EAST, false);
        board.getCommonComponents().add(cannon1);

        cannon2 = new CannonComponent(connectors, NORTH, true);
        board.getCommonComponents().add(cannon2);

        cannon3 = new CannonComponent(connectors, NORTH, false);
        board.getCommonComponents().add(cannon3);

        battery1 = new BatteryComponent(connectors, false);
        board.getCommonComponents().add(battery1);

        cannon1.showComponent();
        cannon1.pickComponent(board, p1.getShip());
        cannon1.insertComponent(p1.getShip(), 3, 2);
        cannon1.weldComponent();

        cannon2.showComponent();
        cannon2.pickComponent(board, p2.getShip());
        cannon2.insertComponent(p2.getShip(), 2, 2);
        cannon2.weldComponent();

        cannon3.showComponent();
        cannon3.pickComponent(board, p2.getShip());
        cannon3.insertComponent(p2.getShip(), 2, 3);
        cannon3.weldComponent();

        battery1.showComponent();
        battery1.pickComponent(board, p2.getShip());
        battery1.insertComponent(p2.getShip(), 3, 4);
        battery1.weldComponent();

        List<BatteryComponent> batteries = new ArrayList<>();
        batteries.add(battery1);
        List<CannonComponent> cannons = new ArrayList<>();
        cannons.add(cannon2);

        modelFacade.nextCard(p1.getUsername());
        modelFacade.activateCannons(p2.getUsername(), batteries, cannons);
        modelFacade.getBoolean(p2.getUsername(), true);

        List<CabinComponent> updated = new ArrayList<>();
        updated.add(cabin1);
        updated.add(cabin3);

        modelFacade.removeCrew(p1.getUsername(), updated);

        assertEquals(1, p1.getShip().getCrew());
        assertEquals(50, p1.getCredits());
        assertEquals(2, p2.getShip().getCrew());
        assertEquals(45, p2.getCredits());
        assertEquals(15, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().get().getValue());
        assertEquals(10, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p2)).findFirst().get().getValue());
    }
}

