package it.polimi.ingsw.model.factory;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.model.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardFactoryAdvancedMode extends CardFactory {

    public CardFactoryAdvancedMode() {
        super();

        List<Card> level1Cards = new ArrayList<>();
        List<Card> level2Cards = new ArrayList<>();

        JsonNode deckJson = loadJsonConfig();
        JsonNode cardsArray = deckJson.get("cards");

        for (int i = 0; i < cardsArray.size(); i++) {
            JsonNode cardJson = cardsArray.get(i);
            Card card = createCard(cardJson);
            if (card.getLevel() == 1)
                level1Cards.add(card);
            else if (card.getLevel() == 2)
                level2Cards.add(card);
        }

        Collections.shuffle(level1Cards);
        Collections.shuffle(level2Cards);

        for (int i = 0; i < 12; i++) {
            if ((i + 1) % 3 == 0)
                cardPile.add(level1Cards.get(i));
            else
                cardPile.add(level2Cards.get(i));
        }
    }

}
