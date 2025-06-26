package it.polimi.ingsw.model.factory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Specialized card factory implementation for learner game mode.
 * This factory creates a simplified card deck that contains only cards flagged as
 * "isLearner: true" in the configuration.
 */
public class CardFactoryLearnerMode extends CardFactory {

    /**
     * Constructs a new CardFactoryLearnerMode and generates the learner-appropriate card deck.
     * <p>
     * The deck size will vary based on how many cards are marked as
     * learner-appropriate in the configuration.
     */
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