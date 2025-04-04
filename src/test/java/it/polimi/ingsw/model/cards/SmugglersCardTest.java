package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.model.properties.DirectionType.*;
import static org.junit.jupiter.api.Assertions.*;

class SmugglersCardTest {
    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private PlayerData p3;
    private ModelFacade modelFacade;
    private Board board;
    private ConnectorType[] connectors;
    private CabinComponent cabin1;
    private CabinComponent cabin2;
    private OddComponent odd1;
    private CannonComponent cannon1;
    private CannonComponent cannon2;
    private CannonComponent cannon3;
    private BatteryComponent battery1;
    private SpecialCargoHoldsComponent cargo1;
    private SpecialCargoHoldsComponent cargo2;
    private SpecialCargoHoldsComponent cargo3;
    private Map<ColorType, Integer> rewards1;


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
        cabin1.insertComponent(p1.getShip(), 1, 2);
        cabin1.weldComponent();

        cabin2 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin2);

        cabin2.showComponent();
        cabin2.pickComponent(board, p2.getShip());
        cabin2.insertComponent(p2.getShip(), 1, 2);
        cabin2.weldComponent();

        odd1 = new OddComponent(connectors, AlienType.ENGINE);
        board.getCommonComponents().add(odd1);

        odd1.showComponent();
        odd1.pickComponent(board, p1.getShip());
        odd1.insertComponent(p1.getShip(), 2, 1);
        odd1.weldComponent();

        rewards1 = new HashMap<>();
        rewards1.put(ColorType.RED, 2);
        rewards1.put(ColorType.GREEN, 1);

        cargo1 = new SpecialCargoHoldsComponent(connectors, 2);
        board.getCommonComponents().add(cargo1);

        cargo1.showComponent();
        cargo1.pickComponent(board, p1.getShip());
        cargo1.insertComponent(p1.getShip(), 1, 3);
        cargo1.weldComponent();

        cargo2 = new SpecialCargoHoldsComponent(connectors, 2);
        board.getCommonComponents().add(cargo2);

        cargo2.showComponent();
        cargo2.pickComponent(board, p2.getShip());
        cargo2.insertComponent(p2.getShip(), 1, 3);
        cargo2.weldComponent();

        cargo3 = new SpecialCargoHoldsComponent(connectors, 2);
        board.getCommonComponents().add(cargo3);

        cargo3.showComponent();
        cargo3.pickComponent(board, p1.getShip());
        cargo3.insertComponent(p1.getShip(), 2, 3);
        cargo3.weldComponent();
    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldGetRewardsIfFirePowerEnoughAndMovePlayer() {
        SmugglersCard smugglersCard = new SmugglersCard(2, false, 1, 5, rewards1, 1);
        board.getCardPile().clear();
        board.getCardPile().add(smugglersCard);

        cannon1 = new CannonComponent(connectors, NORTH, false);
        board.getCommonComponents().add(cannon1);

        cannon2 = new CannonComponent(connectors, NORTH, true);
        board.getCommonComponents().add(cannon2);

        cannon1.showComponent();
        cannon1.pickComponent(board, p1.getShip());
        cannon1.insertComponent(p1.getShip(), 3, 2);
        cannon1.weldComponent();

        cannon2.showComponent();
        cannon2.pickComponent(board, p1.getShip());
        cannon2.insertComponent(p1.getShip(), 2, 2);
        cannon2.weldComponent();

        battery1 = new BatteryComponent(connectors, false);
        board.getCommonComponents().add(battery1);

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

        Map<SpecialCargoHoldsComponent, List<ColorType>> cargoMap = new HashMap<>();
        cargoMap.put(cargo1, new ArrayList<>(List.of(ColorType.RED, ColorType.GREEN)));

        modelFacade.updateGoods(p1.getUsername(), cargoMap, new ArrayList<>());

        assertEquals(1, p1.getShip().getGoods().get(ColorType.RED));
        assertEquals(1, p1.getShip().getGoods().get(ColorType.GREEN));
        assertEquals(14, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckThatCardIsUsedBySecondPLayer() {
        SmugglersCard smugglersCard = new SmugglersCard(2, false, 2, 2, rewards1, 1);
        board.getCardPile().clear();
        board.getCardPile().add(smugglersCard);

        cannon1 = new CannonComponent(connectors, NORTH, false);
        board.getCommonComponents().add(cannon1);

        cannon2 = new CannonComponent(connectors, SOUTH, true);
        board.getCommonComponents().add(cannon2);

        cannon3 = new CannonComponent(connectors, WEST, false);
        board.getCommonComponents().add(cannon3);

        battery1 = new BatteryComponent(connectors, false);
        board.getCommonComponents().add(battery1);

        cannon1.showComponent();
        cannon1.pickComponent(board, p2.getShip());
        cannon1.insertComponent(p2.getShip(), 3, 2);
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

        cargo1.loadGood(ColorType.BLUE, p1.getShip());
        cargo1.loadGood(ColorType.GREEN, p1.getShip());
        cargo3.loadGood(ColorType.YELLOW, p1.getShip());

        List<BatteryComponent> batteries = new ArrayList<>();
        batteries.add(battery1);
        List<CannonComponent> cannons = new ArrayList<>();
        cannons.add(cannon2);

        modelFacade.nextCard(p1.getUsername());

        Map<SpecialCargoHoldsComponent, List<ColorType>> cargoMap1 = new HashMap<>();
        cargoMap1.put(cargo1, new ArrayList<>(List.of(ColorType.BLUE)));
        cargoMap1.put(cargo3, new ArrayList<>());

        modelFacade.activateCannons(p2.getUsername(), batteries, cannons);
        modelFacade.getBoolean(p2.getUsername(), true);

        Map<SpecialCargoHoldsComponent, List<ColorType>> cargoMap2 = new HashMap<>();
        cargoMap2.put(cargo2, new ArrayList<>(List.of(ColorType.RED, ColorType.GREEN)));

        modelFacade.updateGoods(p1.getUsername(), cargoMap1, new ArrayList<>());
        modelFacade.updateGoods(p2.getUsername(), cargoMap2, new ArrayList<>());

        assertEquals(0, p1.getShip().getGoods().get(ColorType.YELLOW));
        assertEquals(0, p1.getShip().getGoods().get(ColorType.GREEN));
        assertEquals(1, p1.getShip().getGoods().get(ColorType.BLUE));
        assertEquals(15, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().get().getValue());
        assertEquals(1, p2.getShip().getGoods().get(ColorType.RED));
        assertEquals(1, p2.getShip().getGoods().get(ColorType.GREEN));
        assertEquals(10, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p2)).findFirst().get().getValue());
    }
}