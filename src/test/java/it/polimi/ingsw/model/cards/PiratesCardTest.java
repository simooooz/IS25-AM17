package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.CannonFire;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.properties.DirectionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.properties.DirectionType.*;
import static org.junit.jupiter.api.Assertions.*;

class PiratesCardTest {
    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private PlayerData p3;
    private ModelFacade modelFacade;
    private Board board;
    private ConnectorType[] connectors;
    private CabinComponent cabin1;
    private CabinComponent cabin2;
    private CabinComponent cabin3;
    private CannonComponent cannon1;
    private CannonComponent cannon2;
    private CannonComponent cannon3;
    private CannonComponent cannon4;
    private CannonComponent cannon5;
    private ShieldComponent shield1;
    private BatteryComponent battery1;
    private BatteryComponent battery2;
    private List<CannonFire> cannonFires;


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

        cabin1 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin1);

        cabin1.showComponent();
        cabin1.pickComponent(board, p1.getShip());
        cabin1.insertComponent(p1.getShip(), 2, 1);
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
        cabin3.pickComponent(board, p3.getShip());
        cabin3.insertComponent(p3.getShip(), 1, 2);
        cabin3.weldComponent();

        cannonFires = new ArrayList<>();

    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldCheckThatP1DrawAutomaticallyP2DrawandP3WinButNotReedemReward() {
        PiratesCard piratesCard = new PiratesCard(2, false, 1, 4, 3, cannonFires);
        board.getCardPile().clear();
        board.getCardPile().add(piratesCard);

        cannon1 = new CannonComponent(connectors, NORTH, false);
        board.getCommonComponents().add(cannon1);

        cannon2 = new CannonComponent(connectors, NORTH, false);
        board.getCommonComponents().add(cannon2);

        cannon3 = new CannonComponent(connectors, NORTH, true);
        board.getCommonComponents().add(cannon3);

        cannon4 = new CannonComponent(connectors, NORTH, false);
        board.getCommonComponents().add(cannon4);

        cannon5 = new CannonComponent(connectors, NORTH, false);
        board.getCommonComponents().add(cannon5);

        cannon1.showComponent();
        cannon1.pickComponent(board, p1.getShip());
        cannon1.insertComponent(p1.getShip(), 1, 3);
        cannon1.weldComponent();

        cannon2.showComponent();
        cannon2.pickComponent(board, p2.getShip());
        cannon2.insertComponent(p2.getShip(), 1, 3);
        cannon2.weldComponent();

        cannon3.showComponent();
        cannon3.pickComponent(board, p2.getShip());
        cannon3.insertComponent(p2.getShip(), 1, 4);
        cannon3.weldComponent();

        cannon4.showComponent();
        cannon4.pickComponent(board, p3.getShip());
        cannon4.insertComponent(p3.getShip(), 1, 3);
        cannon4.weldComponent();

        cannon5.showComponent();
        cannon5.pickComponent(board, p3.getShip());
        cannon5.insertComponent(p3.getShip(), 1, 4);
        cannon5.weldComponent();

        battery1 = new BatteryComponent(connectors, false);
        board.getCommonComponents().add(battery1);

        battery1.showComponent();
        battery1.pickComponent(board, p2.getShip());
        battery1.insertComponent(p2.getShip(), 2, 4);
        battery1.weldComponent();

        List<BatteryComponent> batteries = new ArrayList<>();
        List<CannonComponent> cannons = new ArrayList<>();

        modelFacade.nextCard(p1.getUsername());

        modelFacade.activateCannons(p2.getUsername(), batteries, cannons);

        modelFacade.getBoolean(p3.getUsername(), false);

        assertEquals(11, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p3)).findFirst().get().getValue());
        assertEquals(2, p2.getShip().getComponentByType(BatteryComponent.class).getFirst().getBatteries());
    }

    @Test
    void testShouldCheckThatP1WinsandReedemRewards() {
        PiratesCard piratesCard = new PiratesCard(2, false, 3, 5, 3, cannonFires);
        board.getCardPile().clear();
        board.getCardPile().add(piratesCard);

        cannon1 = new CannonComponent(connectors, NORTH, false);
        board.getCommonComponents().add(cannon1);

        cannon2 = new CannonComponent(connectors, WEST, true);
        board.getCommonComponents().add(cannon2);

        cannon3 = new CannonComponent(connectors, EAST, true);
        board.getCommonComponents().add(cannon3);

        cannon4 = new CannonComponent(connectors, SOUTH, false);
        board.getCommonComponents().add(cannon4);

        cannon1.showComponent();
        cannon1.pickComponent(board, p1.getShip());
        cannon1.insertComponent(p1.getShip(), 3, 2);
        cannon1.weldComponent();

        cannon2.showComponent();
        cannon2.pickComponent(board, p1.getShip());
        cannon2.insertComponent(p1.getShip(), 2, 2);
        cannon2.weldComponent();

        cannon3.showComponent();
        cannon3.pickComponent(board, p1.getShip());
        cannon3.insertComponent(p1.getShip(), 2, 3);
        cannon3.weldComponent();

        cannon4.showComponent();
        cannon4.pickComponent(board, p1.getShip());
        cannon4.insertComponent(p1.getShip(), 2, 4);
        cannon4.weldComponent();

        battery1 = new BatteryComponent(connectors, true);
        board.getCommonComponents().add(battery1);

        battery1.showComponent();
        battery1.pickComponent(board, p1.getShip());
        battery1.insertComponent(p1.getShip(), 3, 4);
        battery1.weldComponent();

        List<BatteryComponent> batteries = new ArrayList<>(List.of(battery1, battery1));
        List<CannonComponent> cannons = new ArrayList<>(List.of(cannon2, cannon3));

        modelFacade.nextCard(p1.getUsername());

        modelFacade.activateCannons(p1.getUsername(), batteries, cannons);

        modelFacade.getBoolean(p1.getUsername(), true);

        assertEquals(1, p1.getShip().getComponentByType(BatteryComponent.class).getFirst().getBatteries());
        assertEquals(10, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().get().getValue());
        assertEquals(55, p1.getCredits());
        assertEquals(12, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p2)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckThatP1andP2LoseandP3Wins() {
        CannonFire c1 = new CannonFire(false, NORTH);
        CannonFire c2 = new CannonFire(true, EAST);
        cannonFires = new ArrayList<>(List.of(c1, c2));
        PiratesCard piratesCard = new PiratesCard(2, false, 1, 5, 3, cannonFires);
        board.getCardPile().clear();
        board.getCardPile().add(piratesCard);

        cannon1 = new CannonComponent(connectors, WEST, false);
        board.getCommonComponents().add(cannon1);

        cannon2 = new CannonComponent(connectors, NORTH, true);
        board.getCommonComponents().add(cannon2);

        cannon3 = new CannonComponent(connectors, EAST, true);
        board.getCommonComponents().add(cannon3);

        cannon4 = new CannonComponent(connectors, NORTH, false);
        board.getCommonComponents().add(cannon4);

        cannon1.showComponent();
        cannon1.pickComponent(board, p1.getShip());
        cannon1.insertComponent(p1.getShip(), 3, 2);
        cannon1.weldComponent();

        cannon2.showComponent();
        cannon2.pickComponent(board, p1.getShip());
        cannon2.insertComponent(p1.getShip(), 2, 2);
        cannon2.weldComponent();

        cannon3.showComponent();
        cannon3.pickComponent(board, p3.getShip());
        cannon3.insertComponent(p3.getShip(), 2, 3);
        cannon3.weldComponent();

        cannon4.showComponent();
        cannon4.pickComponent(board, p3.getShip());
        cannon4.insertComponent(p3.getShip(), 2, 4);
        cannon4.weldComponent();

        battery1 = new BatteryComponent(connectors, true);
        board.getCommonComponents().add(battery1);

        battery1.showComponent();
        battery1.pickComponent(board, p3.getShip());
        battery1.insertComponent(p3.getShip(), 3, 4);
        battery1.weldComponent();

        battery2 = new BatteryComponent(connectors, true);
        board.getCommonComponents().add(battery2);

        battery2.showComponent();
        battery2.pickComponent(board, p1.getShip());
        battery2.insertComponent(p1.getShip(), 3, 3);
        battery2.weldComponent();

        shield1 = new ShieldComponent(connectors, new DirectionType[]{NORTH, WEST});
        board.getCommonComponents().add(shield1);

        shield1.showComponent();
        shield1.pickComponent(board, p1.getShip());
        shield1.insertComponent(p1.getShip(), 3, 4);
        shield1.weldComponent();

        List<BatteryComponent> batteriesp1 = new ArrayList<>();
        List<CannonComponent> cannonsp1 = new ArrayList<>();

        List<BatteryComponent> batteriesp3 = new ArrayList<>();
        batteriesp3.add(battery1);
        List<CannonComponent> cannonsp3 = new ArrayList<>();
        cannonsp3.add(cannon3);

        modelFacade.nextCard(p1.getUsername());

        modelFacade.activateCannons(p1.getUsername(), batteriesp1, cannonsp1);

        modelFacade.activateCannons(p3.getUsername(), batteriesp3, cannonsp3);

        modelFacade.getBoolean(p3.getUsername(), false);

        // set dices value manually
        piratesCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, p1.getUsername(), board);
        modelFacade.setState(piratesCard.changeCardState(board, p1.getUsername()));

        modelFacade.activateShield(p1.getUsername(), battery2);

        piratesCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 8, p1.getUsername(), board);
        modelFacade.setState(piratesCard.changeCardState(board, p1.getUsername()));

        assertEquals(Optional.of(cannon2), p1.getShip().getDashboard(2, 2));
        assertEquals(Optional.empty(), p1.getShip().getDashboard(3, 4));
        assertTrue(p1.getShip().getDiscards().contains(shield1));
        assertEquals(2, p3.getShip().getComponentByType(BatteryComponent.class).getFirst().getBatteries());
        assertEquals(30, p3.getCredits());
        assertEquals(11, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p3)).findFirst().get().getValue());
    }

}