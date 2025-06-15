package it.polimi.ingsw.model.factory;

import com.fasterxml.jackson.databind.JsonNode;


public class CardFactoryLearnerMode extends CardFactory {

    public CardFactoryLearnerMode() {
        super();

        JsonNode deckJson = loadJsonConfig();
        JsonNode cardsArray = deckJson.get("cards");

        for (int i = 0; i < cardsArray.size(); i++) {
            JsonNode cardJson = cardsArray.get(i);
            if (cardJson.get("isLearner").asBoolean())
                cardPile.add(createCard(cardJson));
        }
    }

}
