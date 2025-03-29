package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.controller.GameState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.commands.GoodCommand;
import it.polimi.ingsw.model.cards.utils.Planet;
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

import static org.junit.jupiter.api.Assertions.*;

class PlanetCardTest {

    private Optional<Component>[][] dashboard1;
    private Optional<Component>[][] dashboard2;
    private Optional<Component>[][] dashboard3;
    private Map<ColorType, Integer> goods;
    private List<DirectionType> directions;
    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private PlayerData p3;
    private Board board;
    private PlanetCard planetCard;
    private ConnectorType[] connectors;
    private SpecialCargoHoldsComponent cargo1;
    private SpecialCargoHoldsComponent cargo2;
    private SpecialCargoHoldsComponent cargo3;
    private SpecialCargoHoldsComponent cargo4;
    private SpecialCargoHoldsComponent cargo5;
    private List<Planet> planetList;
    private Planet planet1;
    private Planet planet2;
    private Planet planet3;
    private Map<ColorType, Integer> rewards1;
    private Map<ColorType, Integer> rewards2;
    private Map<ColorType, Integer> rewards3;
    private ModelFacade modelFacade;




    @BeforeEach
    void setUp() {
        // inizializzazione delle variabili comuni ai test
        dashboard1 = new Optional[4][6];
        for (int i = 0; i < dashboard1.length; i++) {
            for (int j = 0; j < dashboard1[i].length; j++) {
                dashboard1[i][j] = Optional.empty();
            }
        }

        dashboard2 = new Optional[4][6];
        for (int i = 0; i < dashboard2.length; i++) {
            for (int j = 0; j < dashboard2[i].length; j++) {
                dashboard2[i][j] = Optional.empty();
            }
        }

        dashboard3 = new Optional[4][6];
        for (int i = 0; i < dashboard3.length; i++) {
            for (int j = 0; j < dashboard3[i].length; j++) {
                dashboard3[i][j] = Optional.empty();
            }
        }
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
        board.getPlayers().add(new AbstractMap.SimpleEntry<>(p1, 15));
        board.getPlayers().add(new AbstractMap.SimpleEntry<>(p2, 12));
        board.getPlayers().add(new AbstractMap.SimpleEntry<>(p3, 11));
        cargo1 = new SpecialCargoHoldsComponent(connectors, 2);
        board.getCommonComponents().add(cargo1);
        cargo2 = new CargoHoldsComponent(connectors, 3);
        board.getCommonComponents().add(cargo2);
        cargo3 = new SpecialCargoHoldsComponent(connectors, 2);
        board.getCommonComponents().add(cargo3);
        cargo4 = new CargoHoldsComponent(connectors, 2);
        board.getCommonComponents().add(cargo4);
        cargo5 = new CargoHoldsComponent(connectors, 3);
        board.getCommonComponents().add(cargo5);
        rewards1 = new HashMap<>();
        rewards1.put(ColorType.RED, 2);
        rewards1.put(ColorType.GREEN, 1);
        rewards2 = new HashMap<>();
        rewards2.put(ColorType.RED, 2);
        rewards2.put(ColorType.YELLOW, 1);
        rewards3 = new HashMap<>();
        rewards3.put(ColorType.BLUE, 1);
        rewards3.put(ColorType.YELLOW, 2);
        planet1 = new Planet(rewards1);
        planet2 = new Planet(rewards2);
        planet3 = new Planet(rewards1);
        planetList = new ArrayList<>();
        planetList.add(planet1);
        planetList.add(planet2);
        planetList.add(planet3);
        planetCard = new PlanetCard(2, true, planetList, 3);
        board.getCardPile().add(planetCard);

    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldLandonPlanetP1() throws Exception {

        cargo1.showComponent();
        cargo1.pickComponent(board, p1.getShip());
        cargo1.insertComponent(p1.getShip(), 1, 2);
        cargo1.weldComponent();

        cargo2.showComponent();
        cargo2.pickComponent(board, p1.getShip());
        cargo2.insertComponent(p1.getShip(), 2, 2);
        cargo2.weldComponent();

        cargo3.showComponent();
        cargo3.pickComponent(board, p3.getShip());
        cargo3.insertComponent(p3.getShip(), 1, 2);
        cargo3.weldComponent();

        modelFacade.nextCard(p1.getUsername());
        // player1 lands on first planet
        modelFacade.getIndex(p1.getUsername(), 0);

        modelFacade.getIndex(p2.getUsername(), -1);

        modelFacade.getIndex(p3.getUsername(), -1);

        //P1 response
        Map<SpecialCargoHoldsComponent, List<ColorType>> cargoMap = new HashMap<>();
        cargoMap.put(cargo1, new ArrayList<>(List.of(ColorType.RED, ColorType.GREEN)));

        modelFacade.updateGoods(p1.getUsername(), cargoMap, new ArrayList<>());


        assertEquals(1, p1.getShip().getGoods().get(ColorType.RED));
        assertEquals(1, p1.getShip().getGoods().get(ColorType.GREEN));
        assertEquals(0, p2.getShip().getGoods().get(ColorType.RED));
        assertEquals(GameState.END, modelFacade.getState());

    }

    @Test
    void testShouldLandOnPlanetAllthePlayers() throws Exception {

        cargo1.showComponent();
        cargo1.pickComponent(board, p1.getShip());
        cargo1.insertComponent(p1.getShip(), 1, 2);
        cargo1.weldComponent();

        cargo2.showComponent();
        cargo2.pickComponent(board, p2.getShip());
        cargo2.insertComponent(p2.getShip(), 2, 2);
        cargo2.weldComponent();

        cargo3.showComponent();
        cargo3.pickComponent(board, p3.getShip());
        cargo3.insertComponent(p3.getShip(), 1, 2);
        cargo3.weldComponent();

        modelFacade.nextCard(p1.getUsername());
        // player1 lands on first planet
        modelFacade.getIndex(p1.getUsername(), 0);

        modelFacade.getIndex(p2.getUsername(), 1);

        modelFacade.getIndex(p3.getUsername(), 2);


        //P1 response
        Map<SpecialCargoHoldsComponent, List<ColorType>> cargoMap1 = new HashMap<>();
        cargoMap1.put(cargo1, new ArrayList<>(List.of(ColorType.RED, ColorType.RED)));

        modelFacade.updateGoods(p1.getUsername(), cargoMap1, new ArrayList<>());

        //P2 response
        Map<SpecialCargoHoldsComponent, List<ColorType>> cargoMap2 = new HashMap<>();
        cargoMap2.put(cargo2, new ArrayList<>(List.of(ColorType.YELLOW)));

        modelFacade.updateGoods(p2.getUsername(), cargoMap2, new ArrayList<>());

        //P3 response
        Map<SpecialCargoHoldsComponent, List<ColorType>> cargoMap3 = new HashMap<>();
        cargoMap3.put(cargo3, new ArrayList<>(List.of(ColorType.RED, ColorType.GREEN)));

        modelFacade.updateGoods(p3.getUsername(), cargoMap3, new ArrayList<>());

        assertEquals(2, p1.getShip().getGoods().get(ColorType.RED));
        assertEquals(0, p1.getShip().getGoods().get(ColorType.GREEN));
        assertEquals(0, p2.getShip().getGoods().get(ColorType.RED));
        assertEquals(1, p2.getShip().getGoods().get(ColorType.YELLOW));
        assertEquals(1, p3.getShip().getGoods().get(ColorType.RED));
        assertEquals(0, p3.getShip().getGoods().get(ColorType.YELLOW));
        assertEquals(2, cargo1.getGoods().stream().filter(color -> color == ColorType.RED).count());

        assertEquals(GameState.END, modelFacade.getState());

    }




}