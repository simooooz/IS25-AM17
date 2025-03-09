package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.properties.DirectionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EpidemicCardTest {

    private Optional<Component>[][] dashboard1;
    private List<Optional<Component>> discarded;
    private Optional<Component> component;
    private int battery;
    private Map<ColorType, Integer> goods;
    private List<DirectionType> directions;
    private List<AbstractMap.SimpleEntry<PlayerData, Integer>> players;
    private Board board;
    private ConnectorType[] connectors;
    private CabinComponent cabin1;
    private CabinComponent cabin2;
    private CabinComponent cabin3;

    @BeforeEach
    void setUp() {
        // inizializzazione delle variabili comuni ai test
        dashboard1 = new Optional[3][3];
        for (int i = 0; i < dashboard1.length; i++) {
            for (int j = 0; j < dashboard1[i].length; j++) {
                dashboard1[i][j] = Optional.empty();
            }
        }
        players = new ArrayList<>();
        board = new Board(players);
        cabin1 = new CabinComponent(connectors, false);
        cabin2 = new CabinComponent(connectors, false);
        cabin3 = new CabinComponent(connectors, false);
    }

    @AfterEach
    void tearDown() {
        players.clear();
    }

    @Test
    void testShouldReduceAliensInTwoAdjacentCabins() {
        //setto i due alieni in due cabine adiacenti (setto manualmente gli umani a 0 per definizione)
        cabin1.setAlien(AlienType.CANNON);
        cabin2.setAlien(AlienType.ENGINE);
        cabin1.setHumans(0);
        cabin2.setHumans(0);
        dashboard1[1][1] = Optional.of(cabin1);
        cabin1.setX(1);
        cabin1.setY(1);
        dashboard1[1][2] = Optional.of(cabin2);
        cabin2.setX(1);
        cabin2.setY(2);
        // setto manualmente l'equipaggio iniziale a 4 (2 per ogni alieno)
        Ship ship1 = new Ship(dashboard1, discarded, component, 4, battery, goods, directions);
        PlayerData player1 = new PlayerData(ColorType.BLUE, "Simone", ship1, 0);
        players.add(new AbstractMap.SimpleEntry<>(player1, 2));
        EpidemicCard epidemicCard = new EpidemicCard(2, false);
        epidemicCard.resolve(board);

        //verifico che sia stato aggiornato correttamente il valore dell'equipaggio
        assertEquals(0, player1.getShip().getCrew());
        //verifico che l'equipaggio sia stato rimosso nella plancia della nave
        assertFalse(player1.getShip().getCabines().getFirst().getAlien().isPresent());
        assertEquals(1, player1.getShip().getCabines().getFirst().getX());
        assertFalse(((CabinComponent)player1.getShip().getDashboard()[1][1].get()).getAlien().isPresent());

    }


    @Test
    void testShouldReduceHumansInThreeAdjacentCabins() {
        cabin2.setHumans(1);
        dashboard1[1][1] = Optional.of(cabin1);
        cabin1.setX(1);
        cabin1.setY(1);
        dashboard1[1][2] = Optional.of(cabin2);
        cabin2.setX(1);
        cabin2.setY(2);
        dashboard1[2][2] = Optional.of(cabin3);
        cabin3.setX(2);
        cabin3.setY(2);
        Ship ship1 = new Ship(dashboard1, discarded, component, 5, battery, goods, directions);
        PlayerData player1 = new PlayerData(ColorType.BLUE, "Simone", ship1, 0);
        players.add(new AbstractMap.SimpleEntry<>(player1, 2));
        EpidemicCard epidemicCard = new EpidemicCard(2, false);
        epidemicCard.resolve(board);

        //verifico che sia stato aggiornato correttamente il valore dell'equipaggio
        assertEquals(2, player1.getShip().getCrew());

        assertEquals(1, player1.getShip().getCabines().getFirst().getHumans());
        assertEquals(1, ((CabinComponent)player1.getShip().getDashboard()[1][1].get()).getHumans());
        assertEquals(0, ((CabinComponent)player1.getShip().getDashboard()[1][2].get()).getHumans());

    }

    @Test
    void testShouldNotReduceHumansInNonAdjacentCabins() {
        CabinComponent cabin1 = new CabinComponent(connectors, false);
        CabinComponent cabin3 = new CabinComponent(connectors, false);
        dashboard1[1][1] = Optional.of(cabin1);
        cabin1.setX(1);
        cabin1.setY(1);
        dashboard1[2][2] = Optional.of(cabin3);
        cabin3.setX(2);
        cabin3.setY(2);
        Ship ship1 = new Ship(dashboard1, discarded, component, 4, battery, goods, directions);
        PlayerData player1 = new PlayerData(ColorType.BLUE, "Simone", ship1, 0);
        players.add(new AbstractMap.SimpleEntry<>(player1, 2));
        EpidemicCard epidemicCard = new EpidemicCard(2, false);
        epidemicCard.resolve(board);

        assertEquals(4, player1.getShip().getCrew());
        assertEquals(2, player1.getShip().getCabines().getFirst().getHumans());
        assertEquals(1, player1.getShip().getCabines().getFirst().getX());
        assertEquals(2, ((CabinComponent)player1.getShip().getDashboard()[1][1].get()).getHumans());

    }

    @Test
    void testShouldNotReduceHumanAndAlienInNonAdjacentCabins() {
        cabin1.setAlien(AlienType.CANNON);
        cabin1.setHumans(0);
        cabin2.setHumans(1);
        dashboard1[1][1] = Optional.of(cabin1);
        cabin1.setX(1);
        cabin1.setY(1);
        dashboard1[2][2] = Optional.of(cabin2);
        cabin2.setX(2);
        cabin2.setY(2);
        Ship ship1 = new Ship(dashboard1, discarded, component, 3, battery, goods, directions);
        PlayerData player1 = new PlayerData(ColorType.BLUE, "Simone", ship1, 0);
        players.add(new AbstractMap.SimpleEntry<>(player1, 2));
        EpidemicCard epidemicCard = new EpidemicCard(2, false);
        epidemicCard.resolve(board);

        assertEquals(3, player1.getShip().getCrew());
        assertEquals(0, player1.getShip().getCabines().getFirst().getHumans());
        assertEquals(0, ((CabinComponent)player1.getShip().getDashboard()[1][1].get()).getHumans());
        assertTrue(player1.getShip().getCabines().getFirst().getAlien().isPresent());
        assertTrue(((CabinComponent)player1.getShip().getDashboard()[1][1].get()).getAlien().isPresent());

    }

}