package it.polimi.ingsw.model.factory;


import it.polimi.ingsw.model.cards.SlaversCard;
import org.json.JSONArray;
import org.json.JSONObject;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.utils.*;
import it.polimi.ingsw.model.game.objects.ColorType;

import it.polimi.ingsw.model.properties.DirectionType;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;


public class CardFactory {

    private final List<Card> cards;


    public CardFactory(boolean learnerMode) {
        // Costruttore che genera il mazzo dalle carte nel JSON
        this.cards = new ArrayList<>();
        List<Card> level1Cards = new ArrayList<>();
        List<Card> level2Cards = new ArrayList<>();

        JSONObject deckJson = loadJsonConfig();
        JSONArray cardsArray = deckJson.getJSONArray("cards");

        for (int i = 0; i < cardsArray.length(); i++) {
            JSONObject cardJson = cardsArray.getJSONObject(i);
            Card card = createCard(cardJson);
            if (card.getLevel() == 1)
                level1Cards.add(card);
            else if (card.getLevel() == 2)
                level2Cards.add(card);
        }
        Collections.shuffle(level1Cards);
        Collections.shuffle(level2Cards);
        if(!learnerMode) {
            for (int i = 0; i < 12; i++) {
                if ((i + 1) % 3 == 0)
                    cards.add(level1Cards.get(i));
                else
                    cards.add(level2Cards.get(i));
            }
        }
        else {
            cards.addAll(level1Cards.stream().filter(Card::getIsLearner).toList());
        }
    }

    // Metodo per ottenere la lista di carte
    public List<Card> getCards() {
        return cards;
    }

    private JSONObject loadJsonConfig() {
        try {
            String jsonContent = new String(Files.readAllBytes(new File("src/main/java/it/polimi/ingsw/model/resources/factory.json").toPath()));
            return new JSONObject(jsonContent);
        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file JSON: " + e.getMessage());
            return new JSONObject(); // Restituisce un JSON vuoto
        }
    }

    // Generate single card
    private Card createCard(JSONObject cardJson) {
        String type = cardJson.getString("type");
        int level = cardJson.getInt("level");
        boolean isLearner = cardJson.getBoolean("isLearner");

        switch (type) {
            case "SlaversCard":
                int slaversCrew = cardJson.optInt("crew", 0);
                int slaversCredits = cardJson.optInt("credits", 0);
                int slaversDays = cardJson.optInt("days", 0);
                int slaverFirePower = cardJson.optInt("firePower", 0);
                return new SlaversCard(level, isLearner, slaversCrew, slaversCredits, slaversDays, slaverFirePower);

            case "SmugglersCard":
                int smugFirePower = cardJson.optInt("firePower", 0);
                int smugLostGoods = cardJson.optInt("lostGoods", 0);
                JSONObject rewardsJson = cardJson.getJSONObject("rewards");
                Map<ColorType, Integer> rewards = new HashMap<>();
                for (String color : rewardsJson.keySet()) {
                    rewards.put(ColorType.valueOf(color.toUpperCase()), rewardsJson.getInt(color));
                }
                int smugDays = cardJson.optInt("days", 0);
                return new SmugglersCard(level, isLearner, smugFirePower, smugLostGoods, rewards, smugDays);

            case "PiratesCard":
                int pirateFirePower = cardJson.optInt("piratesFirePower", 0);
                int pirateCredits = cardJson.optInt("credits", 0);
                int pirateDays = cardJson.optInt("days", 0);
                JSONArray piratesCannonFiresArray = cardJson.optJSONArray("cannonFires");
                List<CannonFire> piratesCannonFires = new ArrayList<>();
                for (int i = 0; i < piratesCannonFiresArray.length(); i++) {
                    JSONObject cannonFireJson = piratesCannonFiresArray.getJSONObject(i);
                    boolean isBig = cannonFireJson.getBoolean("isBig");
                    DirectionType directionFrom = DirectionType.valueOf(cannonFireJson.getString("directionFrom"));
                    piratesCannonFires.add(new CannonFire(isBig, directionFrom));
                }
                return new PiratesCard(level, isLearner, pirateFirePower, pirateCredits, pirateDays, piratesCannonFires);

            case "StardustCard":
                return new StardustCard(level, isLearner);

            case "OpenSpaceCard":
                return new OpenSpaceCard(level, isLearner);

            case "EpidemicCard":
                return new EpidemicCard(level, isLearner);

            case "MeteorSwarmCard":
                JSONArray meteorsArray = cardJson.optJSONArray("meteors");
                List<Meteor> meteors = new ArrayList<>();
                for (int i = 0; i < meteorsArray.length(); i++) {
                    JSONObject cannonFireJson = meteorsArray.getJSONObject(i);
                    boolean isBig = cannonFireJson.getBoolean("isBig");
                    DirectionType directionFrom = DirectionType.valueOf(cannonFireJson.getString("directionFrom"));
                    meteors.add(new Meteor(isBig, directionFrom));
                }
                return new MeteorSwarmCard(level, isLearner, meteors);

            case "StrayBigMeteorsCard":
                JSONArray meteorsBigArray = cardJson.optJSONArray("cannonFires");
                List<Meteor> meteorsBig = new ArrayList<>();
                for (int i = 0; i < meteorsBigArray.length(); i++) {
                    JSONObject meteorsJson = meteorsBigArray.getJSONObject(i);
                    boolean isBig = meteorsJson.getBoolean("isBig");
                    DirectionType directionFrom = DirectionType.valueOf(meteorsJson.getString("directionFrom"));
                    meteorsBig.add(new Meteor(isBig, directionFrom));
                }
                return new MeteorSwarmCard(level, isLearner, meteorsBig);

            case "AbandonedShipCard":
                int abandonedShipCrew = cardJson.optInt("crew", 0);
                int abandonedShipCredits = cardJson.optInt("credits", 0);
                int abandonedShipDays = cardJson.optInt("days", 0);
                return new AbandonedShipCard(level, isLearner, abandonedShipCrew, abandonedShipCredits, abandonedShipDays);

            case "AbandonedStationCard":
                int crew = cardJson.getInt("crew");
                int stationDays = cardJson.getInt("days");
                JSONObject goodsJson = cardJson.getJSONObject("goods");
                Map<ColorType, Integer> goods = new HashMap<>();
                for (String color : goodsJson.keySet()) {
                    goods.put(ColorType.valueOf(color.toUpperCase()), goodsJson.getInt(color));
                }
                return new AbandonedStationCard(level, isLearner, crew, stationDays, goods);

            case "PlanetCard":
                JSONArray planetsJsonArray = cardJson.optJSONArray("planets");
                List<Planet> planets = new ArrayList<>();
                for (int i = 0; i < planetsJsonArray.length(); i++) {
                    JSONObject planetJson = planetsJsonArray.getJSONObject(i);
                    JSONObject rewardsJsonPlanet = planetJson.getJSONObject("rewards");
                    Map<ColorType, Integer> rewardsPlanet = new HashMap<>();
                    for (String color : rewardsJsonPlanet.keySet()) {
                        rewardsPlanet.put(ColorType.valueOf(color.toUpperCase()), rewardsJsonPlanet.getInt(color));
                    }
                    Planet planet = new Planet(rewardsPlanet);
                    planets.add(planet);
                }
                int days = cardJson.optInt("days", 0);
                return new PlanetCard(level, isLearner, planets, days);

            case "CombactZoneCard":
                JSONArray combactArray = cardJson.optJSONArray("warLines");
                List<AbstractMap.SimpleEntry<CriteriaType, PenaltyCombatZone>> combact = new ArrayList<>();
                for (int i = 0; i < combactArray.length(); i++) {
                    JSONObject combactJson = combactArray.getJSONObject(i);
                    CriteriaType criteria = CriteriaType.valueOf(combactJson.getString("CriteriaType"));
                    JSONObject penaltyJson = combactJson.getJSONObject("PenaltyCombatZone");
                    String penaltyType = penaltyJson.getString("type");
                    PenaltyCombatZone penalty;
                    if (penaltyType.equals("CountablePenaltyZone")) {
                        int penaltyNumber = penaltyJson.getInt("penaltyNumber");
                        MalusType malusType = MalusType.valueOf(penaltyJson.getString("MalusType"));
                        penalty = new CountablePenaltyZone(penaltyNumber, malusType);
                    } else if (penaltyType.equals("CannonFirePenaltyCombatZone")) {
                        JSONArray cannonFiresArray = penaltyJson.getJSONArray("cannonFires");
                        List<CannonFire> cannonFires = new ArrayList<>();
                        for (int j = 0; j < cannonFiresArray.length(); j++) {
                            JSONObject cannonFireJson = cannonFiresArray.getJSONObject(j);
                            boolean isBig = cannonFireJson.getBoolean("isBig");
                            DirectionType directionFrom = DirectionType.valueOf(cannonFireJson.getString("directionFrom"));
                            cannonFires.add(new CannonFire(isBig, directionFrom));
                        }
                        penalty = new CannonFirePenaltyCombatZone(cannonFires);
                    } else {
                        throw new IllegalArgumentException("Unknown penalty type: " + penaltyType);
                    }
                    combact.add(new AbstractMap.SimpleEntry<>(criteria, penalty));
                }
                return new CombatZoneCard(level, isLearner, combact);

            default:
                throw new IllegalArgumentException("Unknown card type: " + type);
        }
    }
}

