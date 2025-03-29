package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.SpecialCargoHoldsComponent;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbandonedStationCardTest {

    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private PlayerData p3;
    private ModelFacade modelFacade;
    private Board board;
    private ConnectorType[] connectors;

    private SpecialCargoHoldsComponent cargo1;
    private SpecialCargoHoldsComponent cargo2;
    private SpecialCargoHoldsComponent cargo3;
    private Map<ColorType, Integer> cardGoods;

    private CabinComponent cabin1;
    private CabinComponent cabin2;

    private int battery;

    @BeforeEach
    void setUp() {
        connectors = new ConnectorType[]{ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL};

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

        cardGoods = new HashMap<>();
        cardGoods.put(ColorType.RED, 2);
        cardGoods.put(ColorType.GREEN, 1);

        cargo1 = new SpecialCargoHoldsComponent(connectors, 3);
        board.getCommonComponents().add(cargo1);

        cargo2 = new SpecialCargoHoldsComponent(connectors, 3);
        board.getCommonComponents().add(cargo2);

        cargo3 = new SpecialCargoHoldsComponent(connectors, 3);
        board.getCommonComponents().add(cargo3);

        cargo1.showComponent();
        cargo1.pickComponent(board, p1.getShip());
        cargo1.insertComponent(p1.getShip(), 1, 1);
        cargo1.weldComponent();

        cargo1.loadGood(ColorType.BLUE, p1.getShip());
        cargo1.loadGood(ColorType.RED, p1.getShip());

        cargo2.showComponent();
        cargo2.pickComponent(board, p1.getShip());
        cargo2.insertComponent(p1.getShip(), 2, 1);
        cargo2.weldComponent();

        cargo3.showComponent();
        cargo3.pickComponent(board, p2.getShip());
        cargo3.insertComponent(p2.getShip(), 2, 1);
        cargo3.weldComponent();

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
    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldNotUpdateIfCrewNotEnough() {
        AbandonedStationCard abandonedStationCard = new AbandonedStationCard(2, false, 10, 5, cardGoods);
        board.getCardPile().add(abandonedStationCard);

        modelFacade.nextCard(p1.getUsername());

        assertEquals(2, p1.getShip().getGoods().values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(15, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckIfTheParameterAreUpdate() {
        AbandonedStationCard abandonedStationCard = new AbandonedStationCard(2, false, 2, 5, cardGoods);
        board.getCardPile().add(abandonedStationCard);

        modelFacade.nextCard(p1.getUsername());

        modelFacade.getBoolean(p1.getUsername(), true);

        Map<SpecialCargoHoldsComponent, List<ColorType>> cargoMap = new HashMap<>();
        cargoMap.put(cargo2, new ArrayList<>(List.of(ColorType.RED, ColorType.GREEN)));

        modelFacade.updateGoods(p1.getUsername(), cargoMap, new ArrayList<>());

        assertEquals(4, p1.getShip().getGoods().values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(8, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckIfTheCardIsUsedBySecondPlayer() throws Exception{
        AbandonedStationCard abandonedStationCard = new AbandonedStationCard(2, false, 2, 2, cardGoods);
        board.getCardPile().add(abandonedStationCard);

        modelFacade.nextCard(p1.getUsername());
        modelFacade.getBoolean(p1.getUsername(), false);
        modelFacade.getBoolean(p2.getUsername(), true);

        Map<SpecialCargoHoldsComponent, List<ColorType>> cargoMap = new HashMap<>();
        cargoMap.put(cargo3, new ArrayList<>(List.of(ColorType.RED, ColorType.GREEN)));

        modelFacade.updateGoods(p2.getUsername(), cargoMap, new ArrayList<>());

        assertEquals(2, p1.getShip().getGoods().values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(2, p2.getShip().getGoods().values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(15, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().get().getValue());
        assertEquals(9, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p2)).findFirst().get().getValue());
    }
}