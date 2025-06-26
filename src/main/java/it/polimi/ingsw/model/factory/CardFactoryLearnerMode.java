package it.polimi.ingsw.model.factory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Specialized card factory implementation for learner game mode.
 * This factory creates a simplified card deck specifically designed for
 * new players to learn the game mechanics without overwhelming complexity.
 * <p>
 * The learner mode factory implements a straightforward deck construction approach:
 * - Loads all available cards from the JSON configuration
 * - Filters cards to include only those marked as learner-appropriate
 * - Creates a deck containing exclusively educational/simplified cards
 * <p>
 * Learner mode cards are specifically designed with the following characteristics:
 * - Simplified mechanics that focus on core game concepts
 * - Reduced complexity to avoid overwhelming new players
 * - Clear, straightforward effects that demonstrate fundamental strategies
 * - Educational value to help players understand basic ship management
 * - Lower difficulty to allow learning without excessive punishment
 * <p>
 * This mode serves as an introduction to the game, allowing new players to:
 * - Familiarize themselves with component management and ship building
 * - Learn basic resource allocation strategies (crew, goods, batteries)
 * - Understand encounter types without complex decision trees
 * - Practice fundamental mechanics before advancing to standard play
 * - Build confidence through achievable challenges
 * <p>
 * The learner deck contains only cards flagged as "isLearner: true" in the
 * configuration, ensuring a curated experience optimized for education
 * rather than challenge. This creates a supportive learning environment
 * where new players can master the basics at their own pace.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public class CardFactoryLearnerMode extends CardFactory {

    /**
     * Constructs a new CardFactoryLearnerMode and generates the learner-appropriate card deck.
     * <p>
     * The construction process follows these steps:
     * 1. Loads the complete card configuration from the JSON file
     * 2. Iterates through all available cards in the configuration
     * 3. Filters cards to include only those marked with "isLearner: true"
     * 4. Creates and adds learner-appropriate cards to the deck
     * <p>
     * This filtering ensures that the resulting deck contains only cards
     * that have been specifically designed and balanced for new players.
     * The learner cards typically feature:
     * - Simplified encounter mechanics
     * - Clear cause-and-effect relationships
     * - Forgiving consequences for mistakes
     * - Opportunities to practice all major game systems
     * <p>
     * By excluding complex cards (multi-stage encounters, intricate penalties,
     * advanced strategic choices), the learner mode provides a controlled
     * environment where new players can focus on understanding the fundamental
     * game mechanics without being overwhelmed by advanced complexity.
     * <p>
     * The deck size will vary based on how many cards are marked as
     * learner-appropriate in the configuration, but is designed to provide
     * sufficient variety for multiple learning sessions while maintaining
     * educational focus.
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