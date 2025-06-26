package it.polimi.ingsw.model.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.cards.SlaversCard;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.utils.*;
import it.polimi.ingsw.common.model.enums.ColorType;

import it.polimi.ingsw.common.model.enums.DirectionType;
import it.polimi.ingsw.model.cards.AbandonedShipCard;
import it.polimi.ingsw.model.cards.AbandonedStationCard;
import it.polimi.ingsw.model.cards.CombatZoneCard;
import it.polimi.ingsw.model.cards.EpidemicCard;
import it.polimi.ingsw.model.cards.MeteorSwarmCard;
import it.polimi.ingsw.model.cards.OpenSpaceCard;
import it.polimi.ingsw.model.cards.PiratesCard;
import it.polimi.ingsw.model.cards.PlanetCard;
import it.polimi.ingsw.model.cards.SmugglersCard;
import it.polimi.ingsw.model.cards.StardustCard;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Abstract factory class responsible for creating and managing card instances from JSON configuration.
 * This factory implements the Factory Method pattern to provide a flexible system for card creation
 * and serialization, supporting various card types with complex nested structures.
 * <p>
 * The CardFactory serves multiple critical functions:
 * - JSON-based card creation from configuration files
 * - Polymorphic card instantiation supporting all card types in the game
 * - Card serialization for network transmission and persistence
 * - Centralized card management and type safety
 * <p>
 * The factory supports a comprehensive set of card types including:
 * - Enemy encounters (Slavers, Pirates, Smugglers)
 * - Environmental hazards (Stardust, Epidemic, Meteor Swarms)
 * - Exploration opportunities (Abandoned Ships/Stations, Planets)
 * - Strategic challenges (Combat Zones, Open Space)
 * <p>
 * Card creation involves parsing complex JSON structures that may include:
 * - Nested utility objects (WarLines, Meteors, CannonFires, Planets)
 * - Enumerated values (ColorTypes, DirectionTypes, CriteriaTypes)
 * - Collections of complex objects
 * - Conditional logic based on card-specific properties
 * <p>
 * The serialization system provides network-ready JSON representations of cards
 * for client-server communication and game state persistence.
 *
 * @author Generated Javadoc
 * @version 1.0
 */
public abstract class CardFactory {

    /**
     * The collection of cards managed by this factory instance
     */
    protected final List<Card> cardPile;

    /**
     * Static ObjectMapper configured for card serialization operations
     */
    private static final ObjectMapper mapper = createStaticObjectMapper();

    /**
     * Constructs a new CardFactory with an empty card collection.
     * Subclasses are responsible for populating the card pile through
     * their specific creation logic (e.g., loading from configuration files).
     */
    public CardFactory() {
        this.cardPile = new ArrayList<>();
    }

    /**
     * Retrieves the collection of cards managed by this factory.
     * <p>
     * This method provides access to all cards that have been created
     * and registered with this factory instance. The returned list
     * reflects the current state of the factory's card collection.
     *
     * @return the list of cards managed by this factory
     */
    public List<Card> getCards() {
        return cardPile;
    }

    /**
     * Creates and configures a static ObjectMapper for card serialization operations.
     * <p>
     * The ObjectMapper is configured with JDK8 module support when available,
     * enabling proper serialization of Optional types and other JDK8 features
     * commonly used in card implementations.
     *
     * @return a configured ObjectMapper instance for serialization operations
     */
    private static ObjectMapper createStaticObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Class.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module");
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jdk8.Jdk8Module());
        } catch (ClassNotFoundException e) {
        }

        return objectMapper;
    }

    /**
     * Loads and parses the JSON configuration file containing card definitions.
     * <p>
     * This method reads the 'factory.json' resource file and parses it into
     * a JsonNode structure that can be used for card creation. The configuration
     * file contains the complete specifications for all card types and their
     * associated properties.
     *
     * @return the root JsonNode containing all card configuration data
     * @throws RuntimeException if the configuration file cannot be found or parsed
     */
    protected JsonNode loadJsonConfig() {
        ObjectMapper objectMapper = createObjectMapper();

        try {
            InputStream configStream = getClass().getResourceAsStream("/factory.json");

            if (configStream == null) {
                throw new RuntimeException("Config file 'factory.json' not found in resources");
            }

            return objectMapper.readTree(configStream);

        } catch (IOException e) {
            throw new RuntimeException("Unable to parse config file", e);
        }
    }

    /**
     * Creates a new ObjectMapper instance with JDK8 module support.
     * <p>
     * This method provides a fresh ObjectMapper configured similarly to the
     * static instance but for use in specific operations that require a
     * separate mapper instance.
     *
     * @return a new configured ObjectMapper instance
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Class.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module");
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jdk8.Jdk8Module());
        } catch (ClassNotFoundException e) {
        }

        return objectMapper;
    }

    /**
     * Creates a specific card instance from JSON configuration data.
     * <p>
     * This method implements the core card creation logic, parsing JSON data
     * and instantiating the appropriate card type based on the configuration.
     * The method handles complex card creation including:
     * <p>
     * - Basic card properties (id, level, learner mode)
     * - Card-specific parameters (crew requirements, credits, days)
     * - Complex nested structures (war lines, meteors, cannon fires)
     * - Collections of utility objects (planets, penalties)
     * - Enumerated value parsing (colors, directions, criteria types)
     * <p>
     * Supported card types include:
     * - SlaversCard: Enemy encounter requiring crew sacrifice
     * - SmugglersCard: Enemy encounter with goods-based penalties and rewards
     * - PiratesCard: Enemy encounter with cannon fire retaliation
     * - StardustCard: Environmental hazard affecting ship movement
     * - OpenSpaceCard: Navigation challenge using engine power
     * - EpidemicCard: Crew-affecting environmental hazard
     * - MeteorSwarmCard: Defensive challenge against meteor impacts
     * - AbandonedShipCard: Opportunity encounter requiring crew investment
     * - AbandonedStationCard: Opportunity encounter offering goods rewards
     * - PlanetCard: Trading encounter with multiple planet options
     * - CombatZoneCard: Complex multi-criteria competitive encounter
     *
     * @param cardJson the JsonNode containing the card's configuration data
     * @return a fully configured Card instance of the appropriate type
     * @throws IllegalArgumentException if the card type is unknown or configuration is invalid
     */
    protected Card createCard(JsonNode cardJson) {
        String type = cardJson.get("type").asText();
        int id = cardJson.get("id").asInt();
        int level = cardJson.get("level").asInt();
        boolean isLearner = cardJson.get("isLearner").asBoolean();

        switch (type) {
            case "SlaversCard":
                int slaversCrew = cardJson.get("crew").asInt();
                int slaversCredits = cardJson.get("credits").asInt();
                int slaversDays = cardJson.get("days").asInt();
                int slaverFirePower = cardJson.get("firePower").asInt();
                return new SlaversCard(id, level, isLearner, slaversCrew, slaversCredits, slaversDays, slaverFirePower);

            case "SmugglersCard":
                int smugFirePower = cardJson.get("firePower").asInt();
                int smugLostGoods = cardJson.get("lostGoods").asInt();
                JsonNode rewardsJson = cardJson.get("rewards");
                Map<ColorType, Integer> rewards = new HashMap<>();
                for (Iterator<String> it = rewardsJson.fieldNames(); it.hasNext(); ) {
                    String color = it.next();
                    rewards.put(ColorType.valueOf(color.toUpperCase()), rewardsJson.get(color).asInt());
                }
                int smugDays = cardJson.get("days").asInt();
                return new SmugglersCard(id, level, isLearner, smugFirePower, smugLostGoods, rewards, smugDays);

            case "PiratesCard":
                int pirateFirePower = cardJson.get("piratesFirePower").asInt();
                int pirateCredits = cardJson.get("credits").asInt();
                int pirateDays = cardJson.get("days").asInt();
                JsonNode piratesCannonFiresArray = cardJson.get("cannonFires");
                List<CannonFire> piratesCannonFires = new ArrayList<>();
                for (int i = 0; i < piratesCannonFiresArray.size(); i++) {
                    JsonNode cannonFireJson = piratesCannonFiresArray.get(i);
                    boolean isBig = cannonFireJson.get("isBig").asBoolean();
                    DirectionType directionFrom = DirectionType.valueOf(cannonFireJson.get("directionFrom").asText());
                    piratesCannonFires.add(new CannonFire(isBig, directionFrom));
                }
                return new PiratesCard(id, level, isLearner, pirateFirePower, pirateCredits, pirateDays, piratesCannonFires);

            case "StardustCard":
                return new StardustCard(id, level, isLearner);

            case "OpenSpaceCard":
                return new OpenSpaceCard(id, level, isLearner);

            case "EpidemicCard":
                return new EpidemicCard(id, level, isLearner);

            case "MeteorSwarmCard":
                JsonNode meteorsArray = cardJson.get("meteors");
                List<Meteor> meteors = new ArrayList<>();
                for (int i = 0; i < meteorsArray.size(); i++) {
                    JsonNode cannonFireJson = meteorsArray.get(i);
                    boolean isBig = cannonFireJson.get("isBig").asBoolean();
                    DirectionType directionFrom = DirectionType.valueOf(cannonFireJson.get("directionFrom").asText());
                    meteors.add(new Meteor(isBig, directionFrom));
                }
                return new MeteorSwarmCard(id, level, isLearner, meteors);

            case "StrayBigMeteorsCard":
                JsonNode meteorsBigArray = cardJson.get("cannonFires");
                List<Meteor> meteorsBig = new ArrayList<>();
                for (int i = 0; i < meteorsBigArray.size(); i++) {
                    JsonNode meteorsJson = meteorsBigArray.get(i);
                    boolean isBig = meteorsJson.get("isBig").asBoolean();
                    DirectionType directionFrom = DirectionType.valueOf(meteorsJson.get("directionFrom").asText());
                    meteorsBig.add(new Meteor(isBig, directionFrom));
                }
                return new MeteorSwarmCard(id, level, isLearner, meteorsBig);

            case "AbandonedShipCard":
                int abandonedShipCrew = cardJson.get("crew").asInt();
                int abandonedShipCredits = cardJson.get("credits").asInt();
                int abandonedShipDays = cardJson.get("days").asInt();
                return new AbandonedShipCard(id, level, isLearner, abandonedShipCrew, abandonedShipCredits, abandonedShipDays);

            case "AbandonedStationCard":
                int crew = cardJson.get("crew").asInt();
                int stationDays = cardJson.get("days").asInt();
                JsonNode goodsJson = cardJson.get("goods");
                Map<ColorType, Integer> goods = new HashMap<>();
                for (Iterator<String> it = goodsJson.fieldNames(); it.hasNext(); ) {
                    String color = it.next();
                    goods.put(ColorType.valueOf(color.toUpperCase()), goodsJson.get(color).asInt());
                }
                return new AbandonedStationCard(id, level, isLearner, crew, stationDays, goods);

            case "PlanetCard":
                JsonNode planetsJsonArray = cardJson.get("planets");
                List<Planet> planets = new ArrayList<>();
                for (int i = 0; i < planetsJsonArray.size(); i++) {
                    JsonNode planetJson = planetsJsonArray.get(i);
                    JsonNode rewardsJsonPlanet = planetJson.get("rewards");
                    Map<ColorType, Integer> rewardsPlanet = new HashMap<>();
                    for (Iterator<String> it = rewardsJsonPlanet.fieldNames(); it.hasNext(); ) {
                        String color = it.next();
                        rewardsPlanet.put(ColorType.valueOf(color.toUpperCase()), rewardsJsonPlanet.get(color).asInt());
                    }
                    Planet planet = new Planet(rewardsPlanet);
                    planets.add(planet);
                }
                int days = cardJson.get("days").asInt();
                return new PlanetCard(id, level, isLearner, planets, days);

            case "CombactZoneCard":
                JsonNode combactArray = cardJson.get("warLines");
                List<WarLine> combact = new ArrayList<>();
                for (int i = 0; i < combactArray.size(); i++) {
                    JsonNode combactJson = combactArray.get(i);
                    CriteriaType criteria = CriteriaType.valueOf(combactJson.get("CriteriaType").asText());
                    JsonNode penaltyJson = combactJson.get("PenaltyCombatZone");
                    String penaltyType = penaltyJson.get("type").asText();
                    PenaltyCombatZone penalty;
                    if (penaltyType.equals("CountablePenaltyZone")) {
                        int penaltyNumber = penaltyJson.get("penaltyNumber").asInt();
                        MalusType malusType = MalusType.valueOf(penaltyJson.get("MalusType").asText());
                        penalty = new CountablePenaltyZone(penaltyNumber, malusType);
                    } else if (penaltyType.equals("CannonFirePenaltyCombatZone")) {
                        JsonNode cannonFiresArray = penaltyJson.get("cannonFires");
                        List<CannonFire> cannonFires = new ArrayList<>();
                        for (int j = 0; j < cannonFiresArray.size(); j++) {
                            JsonNode cannonFireJson = cannonFiresArray.get(j);
                            boolean isBig = cannonFireJson.get("isBig").asBoolean();
                            DirectionType directionFrom = DirectionType.valueOf(cannonFireJson.get("directionFrom").asText());
                            cannonFires.add(new CannonFire(isBig, directionFrom));
                        }
                        penalty = new CannonFirePenaltyCombatZone(cannonFires);
                    } else {
                        throw new IllegalArgumentException("Unknown penalty type: " + penaltyType);
                    }
                    combact.add(new WarLine(criteria, penalty));
                }
                return new CombatZoneCard(id, level, isLearner, combact);

            default:
                throw new IllegalArgumentException("Unknown card type: " + type);
        }
    }

    /**
     * Serializes a single card instance to JSON string format.
     * <p>
     * This method converts a Card object into a JSON string representation
     * suitable for network transmission, persistence, or client-server
     * communication. The serialization preserves all card state including
     * dynamic properties and current conditions.
     *
     * @param card the card instance to serialize
     * @return JSON string representation of the card
     * @throws RuntimeException if serialization fails due to JSON processing errors
     */
    public static String serializeCard(Card card) {
        try {
            return mapper.writeValueAsString(card);
        } catch (JsonProcessingException e) {
            // TODO che faccio?
            e.printStackTrace();
            throw new RuntimeException("Errore serializzazione carta: " + e.getMessage(), e);
        }
    }

    /**
     * Serializes a list of card instances to JSON string format.
     * <p>
     * This method converts a collection of Card objects into a JSON array
     * string representation suitable for bulk operations, event transmission,
     * or batch card data transfer. Each card in the list is fully serialized
     * with its complete state information.
     *
     * @param cards the list of card instances to serialize
     * @return JSON array string representation of the card list
     * @throws RuntimeException if serialization fails due to JSON processing errors
     */
    public static String serializeCardList(List<Card> cards) {
        try {
            JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, Card.class);
            return mapper.writerFor(listType).writeValueAsString(cards);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore serializzazione carta: " + e.getMessage(), e);
        }
    }
}
