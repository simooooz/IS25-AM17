package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.client.model.game.ClientBoardLearnerMode;
import it.polimi.ingsw.common.dto.ModelDTO;

import java.util.List;

public class ClientGameModelLearnerMode extends ClientGameModel {

    public ClientGameModelLearnerMode(List<String> usernames) {
        super();
        this.board = new ClientBoardLearnerMode(usernames);
    }

    public ClientGameModelLearnerMode(ModelDTO dto) {
        super(dto);
        this.board = new ClientBoardLearnerMode(dto.board);
    }

    @Override
    public void cardPileLooked(String username, int deckIndex, List<ClientCard> cards) {
        throw new RuntimeException("Card piles aren't learner mode flight");
    }

}