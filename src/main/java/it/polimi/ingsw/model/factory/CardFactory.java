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


public abstract class CardFactory {

    protected final List<Card> cardPile;
    private static final ObjectMapper mapper = createStaticObjectMapper();

    public CardFactory() {
        this.cardPile = new ArrayList<>();
    }

    public List<Card> getCards() {
        return cardPile;
    }

    private static ObjectMapper createStaticObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Class.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module");
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jdk8.Jdk8Module());
        } catch (ClassNotFoundException e) {
        }

        return objectMapper;
    }

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

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Class.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module");
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jdk8.Jdk8Module());
        } catch (ClassNotFoundException e) {
        }

        return objectMapper;
    }

    // ... resto del metodo createCard rimane uguale ...
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

    public static String serializeCard(Card card) {
        try {
            return mapper.writeValueAsString(card);
        } catch (JsonProcessingException e) {
            // TODO che faccio?
            e.printStackTrace();
            throw new RuntimeException("Errore serializzazione carta: " + e.getMessage(), e);
        }
    }

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