package it.polimi.ingsw.model.factory;

import it.polimi.ingsw.model.cards.Card;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class CardFactoryLearnerMode extends CardFactory {

    public CardFactoryLearnerMode() {
        super();

        JSONObject deckJson = loadJsonConfig();
        JSONArray cardsArray = deckJson.getJSONArray("cards");

        for (int i = 0; i < cardsArray.length(); i++) {
            JSONObject cardJson = cardsArray.getJSONObject(i);
            if (cardJson.getBoolean("isLearner"))
                cardPile.add(createCard(cardJson));
        }
    }

    public List<Card> getAllCards() {
        return cardPile;
    }

}
