package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static it.polimi.ingsw.model.components.utils.ConnectorType.*;
import static org.junit.jupiter.api.Assertions.*;

class StardustCardTest {

    private List<String> usernames;
    private PlayerData player1;
    private PlayerData player2;
    private PlayerData player3;

    private ModelFacade modelFacade;
    private Board board;

    private Component component1;
    private Component component2;
    private Component component3;
    private Component component4;
    private Component component5;
    private Component component6;
    private Component component7;
    private Component component8;
    private Component component9;

    private CabinComponent cabin1;
    private CabinComponent cabin2;
    private CabinComponent cabin3;
    private CabinComponent cabin4;

    @BeforeEach
    void setUp() {

        usernames = new ArrayList<>();
        usernames.add("Simone");
        usernames.add("Davide");
        usernames.add("Tommaso");

        player1 = new PlayerData(usernames.get(0));
        player2 = new PlayerData(usernames.get(1));
        player3 = new PlayerData(usernames.get(2));

        modelFacade = new ModelFacade(usernames);
        board = modelFacade.getBoard();

        board.moveToBoard(player3);
        board.movePlayer(player3, 19);
        board.moveToBoard(player2);
        board.movePlayer(player2, 21);
        board.moveToBoard(player1);
        board.movePlayer(player1, 22);

        ConnectorType[] connectors1 = new ConnectorType[]{SINGLE, DOUBLE, SINGLE, EMPTY};
        ConnectorType[] connectors2 = new ConnectorType[]{EMPTY, EMPTY, DOUBLE, UNIVERSAL};
        ConnectorType[] connectors3 = new ConnectorType[]{DOUBLE, EMPTY, EMPTY, UNIVERSAL};
        ConnectorType[] connectors4 = new ConnectorType[]{DOUBLE, SINGLE, EMPTY, UNIVERSAL};

        cabin1 = new CabinComponent(new ConnectorType[]{UNIVERSAL, UNIVERSAL, UNIVERSAL, UNIVERSAL}, false);
        board.getCommonComponents().add(cabin1);
        cabin2 = new CabinComponent(new ConnectorType[]{UNIVERSAL, UNIVERSAL, UNIVERSAL, UNIVERSAL}, false);
        board.getCommonComponents().add(cabin2);
        cabin3 = new CabinComponent(new ConnectorType[]{UNIVERSAL, UNIVERSAL, UNIVERSAL, UNIVERSAL}, false);
        board.getCommonComponents().add(cabin3);

        component1 = new Component(connectors1);
        board.getCommonComponents().add(component1);
        component2 = new Component(connectors2);
        board.getCommonComponents().add(component2);
        component3 = new Component(connectors1);
        board.getCommonComponents().add(component3);
        component4 = new Component(connectors4);
        board.getCommonComponents().add(component4);
        component5 = new Component(connectors1);
        board.getCommonComponents().add(component5);
        component6 = new Component(connectors3);
        board.getCommonComponents().add(component6);
        component7 = new Component(connectors1);
        board.getCommonComponents().add(component7);
        component8 = new Component(connectors4);
        board.getCommonComponents().add(component8);
        component9 = new Component(connectors3);
        board.getCommonComponents().add(component9);

        StardustCard stardustCard = new StardustCard(2, false);
        board.getCardPile().clear();
        board.getCardPile().add(stardustCard);

    }

    @AfterEach
    void tearDown() { usernames.clear(); }

    @Test
    void testShouldCheckPlayersPosAfterCardEffect() {

        // p1
        cabin1.showComponent();
        cabin1.pickComponent(board, player1.getShip());
        cabin1.insertComponent(player1.getShip(), 3, 4);
        cabin1.weldComponent();

        component1.showComponent();
        component1.pickComponent(board, player1.getShip());
        component1.insertComponent(player1.getShip(), 2, 2);
        component1.weldComponent();

        component2.showComponent();
        component2.pickComponent(board, player1.getShip());
        component2.insertComponent(player1.getShip(), 2, 3);
        component2.weldComponent();

        component3.showComponent();
        component3.pickComponent(board, player1.getShip());
        component3.insertComponent(player1.getShip(), 3, 2);
        component3.weldComponent();

        component4.showComponent();
        component4.pickComponent(board, player1.getShip());
        component4.insertComponent(player1.getShip(), 3, 3);
        component4.weldComponent();

        // p2
        cabin2.showComponent();
        cabin2.pickComponent(board, player2.getShip());
        cabin2.insertComponent(player2.getShip(), 4, 2);
        cabin2.weldComponent();

        component5.showComponent();
        component5.pickComponent(board, player2.getShip());
        component5.insertComponent(player2.getShip(), 2, 2);
        component5.weldComponent();

        component6.showComponent();
        component6.pickComponent(board, player2.getShip());
        component6.insertComponent(player2.getShip(), 2, 3);
        component6.weldComponent();

        component7.showComponent();
        component7.pickComponent(board, player2.getShip());
        component7.insertComponent(player2.getShip(), 3, 2);
        component7.weldComponent();

        // p3
        cabin3.showComponent();
        cabin3.pickComponent(board, player3.getShip());
        cabin3.insertComponent(player3.getShip(), 2, 1);
        cabin3.weldComponent();

        component8.showComponent();
        component8.pickComponent(board, player3.getShip());
        component8.insertComponent(player3.getShip(), 2, 2);
        component8.weldComponent();

        component9.showComponent();
        component9.pickComponent(board, player3.getShip());
        component9.insertComponent(player3.getShip(), 2, 3);
        component9.weldComponent();

        // trigger card effect
        modelFacade.nextCard(player3.getUsername());

        assertEquals(18,
                board.getPlayers().stream()
                        .filter(entry -> entry.getKey().equals(player1))
                        .findFirst()
                        .get()
                        .getValue());
        assertEquals(17,
                board.getPlayers().stream()
                        .filter(entry -> entry.getKey().equals(player2))
                        .findFirst()
                        .get()
                        .getValue());
        assertEquals(20,
                board.getPlayers().stream()
                        .filter(entry -> entry.getKey().equals(player3))
                        .findFirst()
                        .get()
                        .getValue());

    }

}
