package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.OddComponent;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.components.utils.ConnectorType.SINGLE;
import static it.polimi.ingsw.model.game.objects.AlienType.CANNON;
import static org.junit.jupiter.api.Assertions.*;

class AbandonedShipCardTest {

    private Optional<Component>[][] dashboard1;
    private Optional<Component>[][] dashboard2;
    private Optional<Component>[][] dashboard3;
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
    private CabinComponent cabin6;
    private OddComponent odd1;
    private OddComponent odd2;

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
        connectors = new ConnectorType[]{ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL, ConnectorType.UNIVERSAL};

        p1.setCredits(50);
        p2.setCredits(40);
        p3.setCredits(30);

        cabin1 = new CabinComponent(connectors, true);
        board.getCommonComponents().add(cabin1);
        cabin2 = new CabinComponent(connectors, true);
        board.getCommonComponents().add(cabin2);
        cabin3 = new CabinComponent(connectors, true);
        board.getCommonComponents().add(cabin3);
        cabin4 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin4);
        cabin5 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin5);
        cabin6 = new CabinComponent(connectors, false);
        board.getCommonComponents().add(cabin6);

        odd1 = new OddComponent(connectors, AlienType.CANNON);
        board.getCommonComponents().add(odd1);
        odd2 = new OddComponent(connectors, AlienType.ENGINE);
        board.getCommonComponents().add(odd2);

        odd1.showComponent();
        odd1.pickComponent(board, p1.getShip());
        odd1.insertComponent(p1.getShip(), 2, 1);
        odd1.weldComponent();

        odd2.showComponent();
        odd2.pickComponent(board, p2.getShip());
        odd2.insertComponent(p2.getShip(), 2, 1);
        odd2.weldComponent();

        cabin1.showComponent();
        cabin1.pickComponent(board, p1.getShip());
        cabin1.insertComponent(p1.getShip(), 1, 2);
        cabin1.weldComponent();
        cabin1.setHumans(2, p1.getShip());

        cabin2.showComponent();
        cabin2.pickComponent(board, p2.getShip());
        cabin2.insertComponent(p2.getShip(), 1, 2);
        cabin2.weldComponent();
        cabin2.setHumans(2, p2.getShip());

        cabin3.showComponent();
        cabin3.pickComponent(board, p3.getShip());
        cabin3.insertComponent(p3.getShip(), 1, 2);
        cabin3.weldComponent();
        cabin3.setHumans(2, p3.getShip());

        cabin4.showComponent();
        cabin4.pickComponent(board, p1.getShip());
        cabin4.insertComponent(p1.getShip(), 1, 1);
        cabin4.weldComponent();
        cabin4.setAlien(AlienType.CANNON, p1.getShip());

        cabin5.showComponent();
        cabin5.pickComponent(board, p2.getShip());
        cabin5.insertComponent(p2.getShip(), 1, 1);
        cabin5.weldComponent();
        cabin5.setAlien(AlienType.ENGINE, p2.getShip());

        cabin6.showComponent();
        cabin6.pickComponent(board, p2.getShip());
        cabin6.insertComponent(p2.getShip(), 1, 3);
        cabin6.weldComponent();
    }

    @AfterEach
    void tearDown() {
        usernames.clear();
    }

    @Test
    void testShouldNotUpdateCardIfCrewNotEnough() throws Exception{
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(2, false, 10, 0, 0);
        board.getCardPile().add(abandonedShipCard);

        modelFacade.nextCard(p1.getUsername());

        assertEquals(4, p1.getShip().getCrew());
        assertEquals(50, p1.getCredits());
        assertEquals(15, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckIfTheParameterAreUpdateWithHuman() throws Exception{
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(2, false, 2, 6, 7);
        board.getCardPile().add(abandonedShipCard);

        modelFacade.nextCard(p1.getUsername());

        modelFacade.getBoolean(p1.getUsername(), true);

        List<CabinComponent> updated = new ArrayList<>();
        updated.add(cabin1);
        updated.add(cabin1);

        modelFacade.removeCrew(p1.getUsername(), updated);

        assertEquals(2, p1.getShip().getCrew());
        assertEquals(56, p1.getCredits());
        assertEquals(6, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckIfTheParameterAreUpdateWithHumanAndAlien() throws Exception{
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(2, false, 3, 6, 7);
        board.getCardPile().add(abandonedShipCard);

        modelFacade.nextCard(p1.getUsername());

        modelFacade.getBoolean(p1.getUsername(), true);

        List<CabinComponent> updated = new ArrayList<>();
        updated.add(cabin1);
        updated.add(cabin4);

        modelFacade.removeCrew(p1.getUsername(), updated);

        assertEquals(1, p1.getShip().getCrew());
        assertEquals(56, p1.getCredits());
        assertEquals(6, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckIfTheParameterAreUpdateWithAlien() throws Exception{
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(2, false, 2, 6, 7);
        board.getCardPile().add(abandonedShipCard);

        modelFacade.nextCard(p1.getUsername());

        modelFacade.getBoolean(p1.getUsername(), true);

        List<CabinComponent> updated = new ArrayList<>();
        updated.add(cabin4);

        modelFacade.removeCrew(p1.getUsername(), updated);

        assertEquals(2, p1.getShip().getCrew());
        assertEquals(56, p1.getCredits());
        assertEquals(6, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckIfTheCardIsUsedBySecondPlayer() throws Exception{
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(2, false, 5, 6, 2);
        board.getCardPile().add(abandonedShipCard);

        modelFacade.nextCard(p1.getUsername());
        modelFacade.getBoolean(p2.getUsername(), true);

        List<CabinComponent> updated = new ArrayList<>();
        updated.add(cabin2);
        updated.add(cabin6);
        updated.add(cabin6);
        updated.add(cabin5);

        modelFacade.removeCrew(p2.getUsername(), updated);

        assertEquals(4, p1.getShip().getCrew());
        assertEquals(1, p2.getShip().getCrew());
        assertEquals(50, p1.getCredits());
        assertEquals(46, p2.getCredits());
        assertEquals(15, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().get().getValue());
        assertEquals(9, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p2)).findFirst().get().getValue());
    }

    @Test
    void testShouldCheckIfTheCardIsUsedByThirdPlayer() throws Exception{
        AbandonedShipCard abandonedShipCard = new AbandonedShipCard(2, false, 1, 6, 2);
        board.getCardPile().add(abandonedShipCard);

        modelFacade.nextCard(p1.getUsername());
        modelFacade.getBoolean(p1.getUsername(), false);
        modelFacade.getBoolean(p2.getUsername(), false);
        modelFacade.getBoolean(p3.getUsername(), true);

        List<CabinComponent> updated = new ArrayList<>();
        updated.add(cabin3);

        modelFacade.removeCrew(p3.getUsername(), updated);

        assertEquals(4, p1.getShip().getCrew());
        assertEquals(6, p2.getShip().getCrew());
        assertEquals(1, p3.getShip().getCrew());
        assertEquals(50, p1.getCredits());
        assertEquals(40, p2.getCredits());
        assertEquals(36, p3.getCredits());
        assertEquals(15, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p1)).findFirst().get().getValue());
        assertEquals(12, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p2)).findFirst().get().getValue());
        assertEquals(9, board.getPlayers().stream().filter(entry -> entry.getKey().equals(p3)).findFirst().get().getValue());
    }

}

