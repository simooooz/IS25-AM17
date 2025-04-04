package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.OddComponent;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EpidemicCardTest {

    private List<String> usernames;
    private PlayerData p1;
    private PlayerData p2;
    private PlayerData p3;
    private Board board;
    private EpidemicCard epidemicCard;
    private ConnectorType[] connectors;
    private CabinComponent cabin1;
    private CabinComponent cabin2;
    private CabinComponent cabin3;
    private OddComponent odd1;
    private OddComponent odd2;
    private ModelFacade modelFacade;

    @BeforeEach
    void setUp() {
        // inizializzazione delle variabili comuni ai test

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
        cabin1 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin1);
        cabin2 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin2);
        cabin3 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin3);
        odd1 = new OddComponent(connectors, AlienType.CANNON);
        board.getCommonComponents().add(odd1);
        odd2 = new OddComponent(connectors, AlienType.ENGINE);
        board.getCommonComponents().add(odd2);
        epidemicCard = new EpidemicCard(2, false);
        board.getCardPile().clear();
        board.getCardPile().add(epidemicCard);


    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldReduceAliensInTwoAdjacentCabins() {

        odd1.showComponent();
        odd1.pickComponent(board, p1.getShip());
        odd1.insertComponent(p1.getShip(), 2, 1);
        odd1.weldComponent();

        odd2.showComponent();
        odd2.pickComponent(board, p1.getShip());
        odd2.insertComponent(p1.getShip(), 1, 3);
        odd2.weldComponent();

        cabin1.showComponent();
        cabin1.pickComponent(board, p1.getShip());
        cabin1.insertComponent(p1.getShip(), 1, 1);
        cabin1.weldComponent();
        cabin1.setAlien(AlienType.CANNON, p1.getShip());

        cabin2.showComponent();
        cabin2.pickComponent(board, p1.getShip());
        cabin2.insertComponent(p1.getShip(), 1, 2);
        cabin2.weldComponent();
        cabin2.setAlien(AlienType.ENGINE, p1.getShip());

        cabin3.showComponent();
        cabin3.pickComponent(board, p1.getShip());
        cabin3.insertComponent(p1.getShip(), 2, 3);
        cabin3.weldComponent();

        modelFacade.nextCard(p1.getUsername());

        //verifico che sia stato aggiornato correttamente il valore dell'equipaggio
        assertEquals(2, p1.getShip().getCrew());
        //verifico che l'equipaggio sia stato rimosso nella plancia della nave
        assertFalse(p1.getShip().getComponentByType(CabinComponent.class).getFirst().getAlien().isPresent());
        assertEquals(1, p1.getShip().getComponentByType(CabinComponent.class).getFirst().getX());
        assertFalse(((CabinComponent)p1.getShip().getDashboard()[1][1].get()).getAlien().isPresent());

    }

    @Test
    void testShouldReduceHumansInThreeAdjacentCabins() {

        cabin1.showComponent();
        cabin1.pickComponent(board, p1.getShip());
        cabin1.insertComponent(p1.getShip(), 1, 1);
        cabin1.weldComponent();

        cabin2.showComponent();
        cabin2.pickComponent(board, p1.getShip());
        cabin2.insertComponent(p1.getShip(), 1, 2);
        cabin2.weldComponent();
        cabin2.setHumans(1, p1.getShip());

        cabin3.showComponent();
        cabin3.pickComponent(board, p1.getShip());
        cabin3.insertComponent(p1.getShip(), 2, 2);
        cabin3.weldComponent();

        modelFacade.nextCard(p1.getUsername());
        //verifico che sia stato aggiornato correttamente il valore dell'equipaggio
        assertEquals(2, p1.getShip().getCrew());

        assertEquals(1, p1.getShip().getComponentByType(CabinComponent.class).getFirst().getHumans());
        assertEquals(1, ((CabinComponent)p1.getShip().getDashboard()[1][1].get()).getHumans());
        assertEquals(0, ((CabinComponent)p1.getShip().getDashboard()[1][2].get()).getHumans());

    }

    @Test
    void testShouldNotReduceHumansInNonAdjacentCabins() {

        cabin1.showComponent();
        cabin1.pickComponent(board, p1.getShip());
        cabin1.insertComponent(p1.getShip(), 1, 1);
        cabin1.weldComponent();

        cabin3.showComponent();
        cabin3.pickComponent(board, p1.getShip());
        cabin3.insertComponent(p1.getShip(), 2, 2);
        cabin3.weldComponent();

        modelFacade.nextCard(p1.getUsername());

        assertEquals(4, p1.getShip().getCrew());
        assertEquals(2, p1.getShip().getComponentByType(CabinComponent.class).getFirst().getHumans());
        assertEquals(1, p1.getShip().getComponentByType(CabinComponent.class).getFirst().getX());
        assertEquals(2, ((CabinComponent)p1.getShip().getDashboard()[1][1].get()).getHumans());

    }

    @Test
    void testShouldNotReduceHumanAndAlienInNonAdjacentCabins() {
        cabin1.showComponent();
        cabin1.pickComponent(board, p1.getShip());
        cabin1.insertComponent(p1.getShip(), 1, 1);
        cabin1.weldComponent();

        odd1.showComponent();
        odd1.pickComponent(board, p1.getShip());
        odd1.insertComponent(p1.getShip(), 2, 1);
        odd1.weldComponent();

        cabin3.showComponent();
        cabin3.pickComponent(board, p1.getShip());
        cabin3.insertComponent(p1.getShip(), 2, 2);
        cabin3.weldComponent();
        cabin3.setAlien(AlienType.CANNON, p1.getShip());

        modelFacade.nextCard(p1.getUsername());

        assertEquals(3, p1.getShip().getCrew());
        assertEquals(2, p1.getShip().getComponentByType(CabinComponent.class).getFirst().getHumans());
        assertEquals(2, ((CabinComponent)p1.getShip().getDashboard()[1][1].get()).getHumans());
        assertTrue(p1.getShip().getComponentByType(CabinComponent.class).get(1).getAlien().isPresent());
        assertTrue(((CabinComponent)p1.getShip().getDashboard()[2][2].get()).getAlien().isPresent());

    }

}