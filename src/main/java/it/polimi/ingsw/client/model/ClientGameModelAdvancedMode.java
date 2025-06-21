package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.client.model.events.CardPileLookedEvent;
import it.polimi.ingsw.client.model.game.ClientBoardAdvancedMode;
import it.polimi.ingsw.common.dto.ModelDTO;

import java.util.List;

public class ClientGameModelAdvancedMode extends ClientGameModel {

    public ClientGameModelAdvancedMode(List<String> usernames) {
        super();
        this.board = new ClientBoardAdvancedMode(usernames);
    }

    public ClientGameModelAdvancedMode(ModelDTO dto) {
        super(dto);
        this.board = new ClientBoardAdvancedMode(dto.board);
    }

    @Override
    public void cardPileLooked(String username, int deckIndex) {
        ClientEventBus.getInstance().publish(new CardPileLookedEvent(username, deckIndex, null));
    }

    @Override
    public void cardPileLooked(String username, int deckIndex, List<ClientCard> cards) {
        board.getLookedCards().addAll(cards);
        ClientEventBus.getInstance().publish(new CardPileLookedEvent(username, deckIndex, board.getLookedCards()));
    }

}