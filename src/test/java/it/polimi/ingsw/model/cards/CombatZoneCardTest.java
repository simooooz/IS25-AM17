package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;

import it.polimi.ingsw.model.cards.utils.*;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.properties.DirectionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.properties.DirectionType.*;
import static org.junit.jupiter.api.Assertions.*;

class CombatZoneCardTest {
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
    private CabinComponent cabin4;
    private CabinComponent cabin5;
    private OddComponent odd1;
    private OddComponent odd2;
    private EngineComponent engine1;
    private EngineComponent engine2;
    private EngineComponent engine3;
    private EngineComponent engine4;
    private CannonComponent cannon1;
    private CannonComponent cannon2;
    private CannonComponent cannon3;
    private CannonComponent cannon4;
    private CannonComponent cannon5;
    private CannonComponent cannon6;
    private ShieldComponent shield1;
    private ShieldComponent shield2;
    private ShieldComponent shield3;
    private BatteryComponent battery1;
    private BatteryComponent battery2;
    private BatteryComponent battery3;
    private SpecialCargoHoldsComponent cargo1;
    private SpecialCargoHoldsComponent cargo2;
    private SpecialCargoHoldsComponent cargo3;
    private List<CannonFire> cannonFires;
    private List<AbstractMap.SimpleEntry<CriteriaType, PenaltyCombatZone>> damages;


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

        cabin2 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin2);

        cabin3 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin3);

        cabin4 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin4);

        cabin5 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin5);

        odd1 = new OddComponent(connectors, AlienType.CANNON);
        board.getCommonComponents().add(odd1);

        odd2 = new OddComponent(connectors, AlienType.ENGINE);
        board.getCommonComponents().add(odd2);

        engine1 = new EngineComponent(connectors, EAST, false);
        board.getCommonComponents().add(engine1);

        engine2 = new EngineComponent(connectors, SOUTH, true);
        board.getCommonComponents().add(engine2);

        engine3 = new EngineComponent(connectors, SOUTH, false);
        board.getCommonComponents().add(engine3);

        engine4 = new EngineComponent(connectors, WEST, true);
        board.getCommonComponents().add(engine4);

        cannon1 = new CannonComponent(connectors, NORTH, false);
        board.getCommonComponents().add(cannon1);

        cannon2 = new CannonComponent(connectors, EAST, false);
        board.getCommonComponents().add(cannon2);

        cannon3 = new CannonComponent(connectors, EAST, false);
        board.getCommonComponents().add(cannon3);

        cannon4 = new CannonComponent(connectors, NORTH, true);
        board.getCommonComponents().add(cannon4);

        cannon5 = new CannonComponent(connectors, NORTH, false);
        board.getCommonComponents().add(cannon5);

        cannon6 = new CannonComponent(connectors, EAST, true);
        board.getCommonComponents().add(cannon6);

        shield1 = new ShieldComponent(connectors, new DirectionType[]{NORTH, SOUTH});
        board.getCommonComponents().add(shield1);

        shield2 = new ShieldComponent(connectors, new DirectionType[]{EAST, WEST});
        board.getCommonComponents().add(shield2);

        shield3 = new ShieldComponent(connectors, new DirectionType[]{SOUTH, WEST});
        board.getCommonComponents().add(shield3);

        battery1 = new BatteryComponent(connectors, true);
        board.getCommonComponents().add(battery1);

        battery2 = new BatteryComponent(connectors, false);
        board.getCommonComponents().add(battery2);

        battery3 = new BatteryComponent(connectors, true);
        board.getCommonComponents().add(battery3);

        cargo1 = new SpecialCargoHoldsComponent(connectors, 3);
        board.getCommonComponents().add(cargo1);

        cargo2 = new SpecialCargoHoldsComponent(connectors, 3);
        board.getCommonComponents().add(cargo2);

        cargo3 = new SpecialCargoHoldsComponent(connectors, 3);
        board.getCommonComponents().add(cargo3);


    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldCheckThatP1HasLessCannonPowerP2HasLessEnginePowerandP3HasLessCrew() {

        //Card Setting

        PenaltyCombatZone penalty1 = new CountablePenaltyZone(2, MalusType.GOODS);
        PenaltyCombatZone penalty2 = new CountablePenaltyZone(3, MalusType.DAYS);
        PenaltyCombatZone penalty3 = new CountablePenaltyZone(2, MalusType.CREW);
        damages = new ArrayList<>();
        damages.add(new AbstractMap.SimpleEntry<>(CriteriaType.CREW, penalty1));
        damages.add(new AbstractMap.SimpleEntry<>(CriteriaType.ENGINE, penalty2));
        damages.add(new AbstractMap.SimpleEntry<>(CriteriaType.CANNON, penalty3));

        CombatZoneCard combatZoneCard = new CombatZoneCard(2, false, damages);
        board.getCardPile().clear();
        board.getCardPile().add(combatZoneCard);

        //Player 1 Ship configuration

        cabin1.showComponent();
        cabin1.pickComponent(board, p1.getShip());
        cabin1.insertComponent(p1.getShip(),2, 2);
        cabin1.weldComponent();

        cabin2.showComponent();
        cabin2.pickComponent(board, p1.getShip());
        cabin2.insertComponent(p1.getShip(),2, 3);
        cabin2.weldComponent();

        cannon1.showComponent();
        cannon1.pickComponent(board, p1.getShip());
        cannon1.insertComponent(p1.getShip(), 2, 4);
        cannon1.weldComponent();

        cannon2.showComponent();
        cannon2.pickComponent(board, p1.getShip());
        cannon2.insertComponent(p1.getShip(), 2, 5);
        cannon2.weldComponent();

        engine1.showComponent();
        engine1.pickComponent(board, p1.getShip());
        engine1.insertComponent(p1.getShip(), 3, 2);
        engine1.rotateComponent(p1.getShip(), true);
        engine1.weldComponent();

        engine2.showComponent();
        engine2.pickComponent(board, p1.getShip());
        engine2.insertComponent(p1.getShip(), 3, 3);
        engine2.weldComponent();

        battery1.showComponent();
        battery1.pickComponent(board, p1.getShip());
        battery1.insertComponent(p1.getShip(), 3, 4);
        battery1.weldComponent();

        cargo1.showComponent();
        cargo1.pickComponent(board, p1.getShip());
        cargo1.insertComponent(p1.getShip(), 3, 5);
        cargo1.weldComponent();

        //Player 2 Ship Configuration

        odd1.showComponent();
        odd1.pickComponent(board, p2.getShip());
        odd1.insertComponent(p2.getShip(), 1, 2);
        odd1.weldComponent();

        cabin3.showComponent();
        cabin3.pickComponent(board, p2.getShip());
        cabin3.insertComponent(p2.getShip(), 2, 2);
        cabin3.weldComponent();
        cabin3.setAlien(AlienType.CANNON, p2.getShip());

        cabin4.showComponent();
        cabin4.pickComponent(board, p2.getShip());
        cabin4.insertComponent(p2.getShip(), 2, 3);
        cabin4.weldComponent();

        cannon3.showComponent();
        cannon3.pickComponent(board, p2.getShip());
        cannon3.insertComponent(p2.getShip(), 2, 4);
        cannon3.rotateComponent(p2.getShip(), false);
        cannon3.weldComponent();

        cannon4.showComponent();
        cannon4.pickComponent(board, p2.getShip());
        cannon4.insertComponent(p2.getShip(), 2, 5);
        cannon4.weldComponent();

        engine3.showComponent();
        engine3.pickComponent(board, p2.getShip());
        engine3.insertComponent(p2.getShip(), 3, 2);
        engine3.weldComponent();

        battery2.showComponent();
        battery2.pickComponent(board, p2.getShip());
        battery2.insertComponent(p2.getShip(), 3, 3);
        battery2.weldComponent();

        cargo2.showComponent();
        cargo2.pickComponent(board, p2.getShip());
        cargo2.insertComponent(p2.getShip(), 3, 4);
        cargo2.weldComponent();

        shield2.showComponent();
        shield2.pickComponent(board, p2.getShip());
        shield2.insertComponent(p2.getShip(), 3, 5);
        shield2.weldComponent();

        //Player 3 Ship Configuration

        odd2.showComponent();
        odd2.pickComponent(board, p3.getShip());
        odd2.insertComponent(p3.getShip(), 2, 1);
        odd2.weldComponent();

        cabin5.showComponent();
        cabin5.pickComponent(board, p3.getShip());
        cabin5.insertComponent(p3.getShip(), 2, 2);
        cabin5.weldComponent();

        cannon5.showComponent();
        cannon5.pickComponent(board, p3.getShip());
        cannon5.insertComponent(p3.getShip(), 2, 3);
        cannon5.weldComponent();

        cannon6.showComponent();
        cannon6.pickComponent(board, p3.getShip());
        cannon6.insertComponent(p3.getShip(), 2, 4);
        cannon6.weldComponent();

        engine4.showComponent();
        engine4.pickComponent(board, p3.getShip());
        engine4.insertComponent(p3.getShip(), 3, 2);
        engine4.weldComponent();

        battery3.showComponent();
        battery3.pickComponent(board, p3.getShip());
        battery3.insertComponent(p3.getShip(), 3, 3);
        battery3.weldComponent();

        cargo3.showComponent();
        cargo3.pickComponent(board, p3.getShip());
        cargo3.insertComponent(p3.getShip(), 3, 4);
        cargo3.weldComponent();
        cargo3.loadGood(ColorType.RED, p3.getShip());
        cargo3.loadGood(ColorType.YELLOW, p3.getShip());
        cargo3.loadGood(ColorType.BLUE, p3.getShip());

        modelFacade.nextCard(p1.getUsername());


        Map<SpecialCargoHoldsComponent, List<ColorType>> cargoMap2 = new HashMap<>();
        cargoMap2.put(cargo3, new ArrayList<>(List.of(ColorType.BLUE)));
        List<BatteryComponent> batteryComponents = new ArrayList<>();
        modelFacade.updateGoods(p3.getUsername(), cargoMap2, batteryComponents);

        //Player 1 activates double engine
        List<BatteryComponent> batteries1 = new ArrayList<>(List.of(battery1));
        List<EngineComponent> engines1 = new ArrayList<>(List.of(engine2));
        modelFacade.activateEngines(p1.getUsername(), batteries1, engines1);

        //Player 3 activates double engine
        List<BatteryComponent> batteries3 = new ArrayList<>(List.of(battery3));
        List<EngineComponent> engines3 = new ArrayList<>(List.of(engine4));
        modelFacade.activateEngines(p3.getUsername(), batteries3, engines3);

        //Player 3 activates double cannon
        modelFacade.activateCannons(p3.getUsername(), new ArrayList<>(), new ArrayList<>());

        // Player 1 indicates cabins where remove crew
        List<CabinComponent> cabins3 = new ArrayList<>(List.of(cabin5, cabin5));
        modelFacade.removeCrew(p3.getUsername(), cabins3);


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

        List<BatteryComponent> batteries = new ArrayList<>();
        batteries.add(battery1);
        batteries.add(battery1);
        List<CannonComponent> cannons = new ArrayList<>();
        cannons.add(cannon2);
        cannons.add(cannon3);

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
        assertEquals(2, p3.getShip().getComponentByType(BatteryComponent.class).getFirst().getBatteries());
        assertEquals(30, p3.getCredits());
        assertEquals(11, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p3)).findFirst().get().getValue());
    }

}