package it.polimi.ingsw.client.model;

import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.client.model.events.CardPileLookedEvent;
import it.polimi.ingsw.client.model.game.ClientBoardAdvancedMode;
import it.polimi.ingsw.common.model.enums.PlayerState;

import java.util.List;

public class ClientGameModelAdvancedMode extends ClientGameModel {

    public ClientGameModelAdvancedMode(List<String> usernames) {
        super(usernames);
        this.board = new ClientBoardAdvancedMode(usernames);
    }

    @Override
    public void cardPileLooked(String username, int deckIndex) {
        PlayerState.LOOK_CARD_PILE.getDeckIndex().put(username, deckIndex);

        ClientEventBus.getInstance().publish(new CardPileLookedEvent(username, deckIndex, null));
    }

    @Override
    public void cardPileLooked(String username, int deckIndex, List<ClientCard> cards) {
        board.getLookedCards().addAll(cards);
        PlayerState.LOOK_CARD_PILE.getDeckIndex().put(username, deckIndex);

        ClientEventBus.getInstance().publish(new CardPileLookedEvent(username, deckIndex, board.getLookedCards()));
    }

}