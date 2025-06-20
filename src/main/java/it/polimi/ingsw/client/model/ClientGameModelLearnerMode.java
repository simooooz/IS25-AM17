package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.client.model.game.ClientBoardLearnerMode;

import java.util.List;

public class ClientGameModelLearnerMode extends ClientGameModel {

    public ClientGameModelLearnerMode(List<String> usernames) {
        super();
        this.board = new ClientBoardLearnerMode(usernames);
    }

    @Override
    public void cardPileLooked(String username, int deckIndex) {
        throw new RuntimeException("Card piles aren't learner mode flight");
    }

    @Override
    public void cardPileLooked(String username, int deckIndex, List<ClientCard> cards) {
        throw new RuntimeException("Card piles aren't learner mode flight");
    }

}