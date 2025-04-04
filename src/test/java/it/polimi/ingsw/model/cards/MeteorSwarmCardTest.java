package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.utils.Meteor;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;

import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.properties.DirectionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeteorSwarmCardTest {

    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private PlayerData p3;
    private ModelFacade modelFacade;
    private Board board;
    private ConnectorType[] connectors1;
    private ConnectorType[] connectors2;
    private ConnectorType[] connectors3;
    private ConnectorType[] connectors4;

    private CabinComponent cabin1;
    private CabinComponent cabin2;
    private CabinComponent cabin3;

    private DirectionType[] directions1;
    private ShieldComponent shield1;
    private ShieldComponent shield2;
    private ShieldComponent shield3;

    private BatteryComponent battery1;
    private BatteryComponent battery2;
    private BatteryComponent battery3;

    private CannonComponent cannon1;
    private CannonComponent cannon2;
    private CannonComponent cannon3;

    @BeforeEach
    void setUp() {
        connectors1 = new ConnectorType[]{ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.EMPTY, ConnectorType.EMPTY};
        connectors2 = new ConnectorType[]{ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL};
        connectors3 = new ConnectorType[]{ConnectorType.UNIVERSAL, ConnectorType.EMPTY, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL};
        connectors4 = new ConnectorType[]{ConnectorType.UNIVERSAL, ConnectorType.EMPTY, ConnectorType.EMPTY, ConnectorType.UNIVERSAL};

        usernames = new ArrayList<>();
        usernames.add("Simone");
        usernames.add("Davide");
        usernames.add("Tommaso");

        p1 = new PlayerData(usernames.get(0));
        p2 = new PlayerData(usernames.get(1));
        p3 = new PlayerData(usernames.get(2));

        modelFacade = new ModelFacade(usernames);
        board = modelFacade.getBoard();

        board.moveToBoard(p1);
        board.movePlayer(p1, 9);
        board.moveToBoard(p2);
        board.movePlayer(p2, 9);
        board.moveToBoard(p3);
        board.movePlayer(p3, 10);

        cabin1 = new CabinComponent(connectors1, true);
        board.getCommonComponents().add(cabin1);

        cabin1.showComponent();
        cabin1.pickComponent(board, p1.getShip());
        cabin1.insertComponent(p1.getShip(), 2, 1);
        cabin1.weldComponent();

        directions1 = new DirectionType[]{DirectionType.NORTH, DirectionType.EAST};
        shield1 = new ShieldComponent(connectors4, directions1);
        board.getCommonComponents().add(shield1);

        shield1.showComponent();
        shield1.pickComponent(board, p1.getShip());
        shield1.insertComponent(p1.getShip(), 2, 2);
        shield1.weldComponent();

        battery1 = new BatteryComponent(connectors3, true);
        board.getCommonComponents().add(battery1);

        battery1.showComponent();
        battery1.pickComponent(board, p1.getShip());
        battery1.insertComponent(p1.getShip(), 1, 2);
        battery1.weldComponent();

        cannon1 = new CannonComponent(connectors2, DirectionType.NORTH, true);
        board.getCommonComponents().add(cannon1);

        cannon1.showComponent();
        cannon1.pickComponent(board, p1.getShip());
        cannon1.insertComponent(p1.getShip(), 1, 1);
        cannon1.weldComponent();
    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldCheckSmallMeteorsDoesNothingIfGoodAssembledShip() {
        List<Meteor> meteors = new ArrayList<>();
        Meteor meteor1 = new Meteor(false, DirectionType.EAST);
        meteors.add(meteor1);

        MeteorSwarmCard meteorSwarmCard = new MeteorSwarmCard(2, false, meteors);
        board.getCardPile().add(meteorSwarmCard);

        modelFacade.nextCard(p1.getUsername());
        // set dice manually
        meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, p1.getUsername(), board);
        modelFacade.setState(meteorSwarmCard.changeCardState(board, p1.getUsername()));

        assertEquals(1, p1.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(CannonComponent.class).size());
    }

    @Test
    void testShouldCheckSmallMeteorsBreakShipIfExposedConnectors() {
        List<Meteor> meteors = new ArrayList<>();
        Meteor meteor1 = new Meteor(false, DirectionType.WEST);
        meteors.add(meteor1);

        MeteorSwarmCard meteorSwarmCard = new MeteorSwarmCard(2, false, meteors);
        board.getCardPile().add(meteorSwarmCard);

        modelFacade.nextCard(p1.getUsername());
        // set dice manually
        meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, p1.getUsername(), board);
        modelFacade.setState(meteorSwarmCard.changeCardState(board, p1.getUsername()));

        assertEquals(1, p1.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(0, p1.getShip().getComponentByType(CannonComponent.class).size());
    }

    @Test
    void testShouldCheckSmallMeteorsDoesNothingIfShielIsUsed() {
        List<Meteor> meteors = new ArrayList<>();
        Meteor meteor1 = new Meteor(false, DirectionType.NORTH);
        meteors.add(meteor1);

        MeteorSwarmCard meteorSwarmCard = new MeteorSwarmCard(2, false, meteors);
        board.getCardPile().add(meteorSwarmCard);

        modelFacade.nextCard(p1.getUsername());
        // set dice manually
        meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, p1.getUsername(), board);
        modelFacade.setState(meteorSwarmCard.changeCardState(board, p1.getUsername()));
        modelFacade.activateShield(p1.getUsername(), battery1);

        assertEquals(1, p1.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(CannonComponent.class).size());
    }

    @Test
    void testShouldCheckBigMeteorsBreackShifIfCannonIsNotUsed() {
        List<Meteor> meteors = new ArrayList<>();
        Meteor meteor1 = new Meteor(true, DirectionType.EAST);
        meteors.add(meteor1);

        MeteorSwarmCard meteorSwarmCard = new MeteorSwarmCard(2, false, meteors);
        board.getCardPile().add(meteorSwarmCard);

        modelFacade.nextCard(p1.getUsername());
        meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, p1.getUsername(), board);
        modelFacade.setState(meteorSwarmCard.changeCardState(board, p1.getUsername()));

        assertEquals(1, p1.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(0, p1.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(CannonComponent.class).size());
    }

    @Test
    void testShouldCheckBigMeteorsDoesNothingIfCannonIsUsed() {
        List<Meteor> meteors = new ArrayList<>();
        Meteor meteor1 = new Meteor(true, DirectionType.NORTH);
        meteors.add(meteor1);

        MeteorSwarmCard meteorSwarmCard = new MeteorSwarmCard(2, false, meteors);
        board.getCardPile().add(meteorSwarmCard);

        modelFacade.nextCard(p1.getUsername());
        // set dice manually
        meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 5, p1.getUsername(), board);
        modelFacade.setState(meteorSwarmCard.changeCardState(board, p1.getUsername()));
        List<BatteryComponent> battery = new ArrayList<>();
        battery.add(battery1);
        List<CannonComponent> cannon = new ArrayList<>();
        cannon.add(cannon1);
        modelFacade.activateCannons(p1.getUsername(), battery, cannon);

        assertEquals(1, p1.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(CannonComponent.class).size());
    }

    @Test
    void testShouldCheckMeteorHitEveryPlayer() {
        List<Meteor> meteors = new ArrayList<>();
        Meteor meteor1 = new Meteor(false, DirectionType.EAST);
        Meteor meteor2 = new Meteor(false, DirectionType.NORTH);
        Meteor meteor3 = new Meteor(true, DirectionType.NORTH);

        meteors.add(meteor1);
        meteors.add(meteor2);
        meteors.add(meteor3);

        MeteorSwarmCard meteorSwarmCard = new MeteorSwarmCard(2, false, meteors);
        board.getCardPile().add(meteorSwarmCard);

        cabin2 = new CabinComponent(connectors1, true);
        board.getCommonComponents().add(cabin2);

        cabin2.showComponent();
        cabin2.pickComponent(board, p2.getShip());
        cabin2.insertComponent(p2.getShip(), 2, 1);
        cabin2.weldComponent();

        shield2 = new ShieldComponent(connectors4, directions1);
        board.getCommonComponents().add(shield2);

        directions1 = new DirectionType[]{DirectionType.NORTH, DirectionType.EAST};
        shield2.showComponent();
        shield2.pickComponent(board, p2.getShip());
        shield2.insertComponent(p2.getShip(), 2, 2);
        shield2.weldComponent();

        battery2 = new BatteryComponent(connectors3, true);
        board.getCommonComponents().add(battery2);

        battery2.showComponent();
        battery2.pickComponent(board, p2.getShip());
        battery2.insertComponent(p2.getShip(), 1, 2);
        battery2.weldComponent();

        cannon2 = new CannonComponent(connectors2, DirectionType.NORTH, true);
        board.getCommonComponents().add(cannon2);

        cannon2.showComponent();
        cannon2.pickComponent(board, p2.getShip());
        cannon2.insertComponent(p2.getShip(), 1, 1);
        cannon2.weldComponent();

        cabin3 = new CabinComponent(connectors1, true);
        board.getCommonComponents().add(cabin3);

        cabin3.showComponent();
        cabin3.pickComponent(board, p3.getShip());
        cabin3.insertComponent(p3.getShip(), 2, 1);
        cabin3.weldComponent();

        directions1 = new DirectionType[]{DirectionType.NORTH, DirectionType.EAST};
        shield3 = new ShieldComponent(connectors4, directions1);
        board.getCommonComponents().add(shield3);

        shield3.showComponent();
        shield3.pickComponent(board, p3.getShip());
        shield3.insertComponent(p3.getShip(), 2, 2);
        shield3.weldComponent();

        battery3 = new BatteryComponent(connectors3, true);
        board.getCommonComponents().add(battery3);

        battery3.showComponent();
        battery3.pickComponent(board, p3.getShip());
        battery3.insertComponent(p3.getShip(), 1, 2);
        battery3.weldComponent();

        cannon3 = new CannonComponent(connectors2, DirectionType.NORTH, true);
        board.getCommonComponents().add(cannon3);

        cannon3.showComponent();
        cannon3.pickComponent(board, p3.getShip());
        cannon3.insertComponent(p3.getShip(), 1, 1);
        cannon3.weldComponent();

        modelFacade.nextCard(p1.getUsername());
        // set dice manually
        meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, p1.getUsername(), board);
        modelFacade.setState(meteorSwarmCard.changeCardState(board, p1.getUsername()));

        meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 6, p1.getUsername(), board);
        modelFacade.setState(meteorSwarmCard.changeCardState(board, p1.getUsername()));

        modelFacade.activateShield(p1.getUsername(), battery1);
        modelFacade.activateShield(p2.getUsername(), battery2);
        modelFacade.activateShield(p3.getUsername(), battery3);

        meteorSwarmCard.doCommandEffects(PlayerState.WAIT_ROLL_DICES, 5, p1.getUsername(), board);
        modelFacade.setState(meteorSwarmCard.changeCardState(board, p1.getUsername()));

        List<BatteryComponent> batteryp1 = new ArrayList<>();
        batteryp1.add(battery1);
        List<CannonComponent> cannonp1 = new ArrayList<>();
        cannonp1.add(cannon1);
        modelFacade.activateCannons(p1.getUsername(), batteryp1, cannonp1);
        List<BatteryComponent> batteryp2 = new ArrayList<>();
        batteryp2.add(battery2);
        List<CannonComponent> cannonp2 = new ArrayList<>();
        cannonp2.add(cannon2);
        modelFacade.activateCannons(p2.getUsername(), batteryp2, cannonp2);
        List<BatteryComponent> batteryp3 = new ArrayList<>();
        batteryp3.add(battery3);
        List<CannonComponent> cannonp3 = new ArrayList<>();
        cannonp3.add(cannon3);
        modelFacade.activateCannons(p3.getUsername(), batteryp3, cannonp3);

        assertEquals(1, p1.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(1, p1.getShip().getComponentByType(CannonComponent.class).size());
        assertEquals(1, p2.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(1, p2.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p2.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(1, p2.getShip().getComponentByType(CannonComponent.class).size());
        assertEquals(1, p3.getShip().getComponentByType(CabinComponent.class).size());
        assertEquals(1, p3.getShip().getComponentByType(BatteryComponent.class).size());
        assertEquals(1, p3.getShip().getComponentByType(ShieldComponent.class).size());
        assertEquals(1, p3.getShip().getComponentByType(CannonComponent.class).size());
    }

    // TODO impements test to check if the user can choose which part of the ship after destory

}