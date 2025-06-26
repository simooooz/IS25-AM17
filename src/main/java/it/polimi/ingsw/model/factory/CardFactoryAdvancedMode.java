package it.polimi.ingsw.model.factory;

import com.fasterxml.jackson.databind.JsonNode;
import it.polimi.ingsw.model.cards.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Specialized card factory implementation for advanced game mode.
 * This factory creates a structured 12-card adventure deck with a specific
 * difficulty progression pattern designed for experienced players.
 * <p>
 * The advanced mode factory implements a sophisticated deck construction algorithm:
 * - Loads all available cards from the JSON configuration
 * - Separates cards by difficulty level (Level 1 and Level 2)
 * - Shuffles each level independently to ensure variability
 * - Creates a 12-card deck with a strategic difficulty pattern
 * <p>
 * The deck structure follows a specific pattern where every third position
 * contains a Level 1 (easier) card, while all other positions contain
 * Level 2 (harder) cards. This creates the following progression:
 * <p>
 * Position Pattern: L2, L2, L1, L2, L2, L1, L2, L2, L1, L2, L2, L1
 * <p>
 * This pattern ensures that:
 * - Players face primarily challenging encounters (Level 2 cards)
 * - Strategic relief is provided at regular intervals (Level 1 cards)
 * - The overall difficulty remains high for experienced players
 * - Pacing allows for resource recovery and strategic planning
 * <p>
 * The advanced mode is designed for players who have mastered the basic
 * game mechanics and seek a more challenging and strategically demanding
 * experience with higher stakes and more complex decision-making.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public class CardFactoryAdvancedMode extends CardFactory {

    /**
     * Constructs a new CardFactoryAdvancedMode and generates the advanced mode card deck.
     * <p>
     * The construction process follows these steps:
     * 1. Loads the complete card configuration from the JSON file
     * 2. Parses all available cards and separates them by difficulty level
     * 3. Shuffles Level 1 and Level 2 cards independently for randomization
     * 4. Constructs a 12-card deck following the advanced mode pattern
     * 5. Places Level 1 cards at positions 3, 6, 9, and 12 (every third position)
     * 6. Fills all other positions with Level 2 cards
     * <p>
     * This creates a challenging yet balanced adventure progression where
     * experienced players face primarily difficult encounters with strategic
     * easier moments for recovery and planning. The shuffling ensures that
     * each game session presents different challenges while maintaining
     * the intended difficulty curve.
     * <p>
     * The resulting deck provides approximately 67% Level 2 encounters and
     * 33% Level 1 encounters, creating a significantly more challenging
     * experience compared to other game modes.
     */
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