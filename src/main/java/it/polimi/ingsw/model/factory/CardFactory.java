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


public abstract class CardFactory {

    protected final List<Card> cardPile;

    public CardFactory() {
        this.cardPile = new ArrayList<>();
    }

    public List<Card> getCards() {
        return cardPile;
    }

    protected JSONObject loadJsonConfig() {
        try {
            String jsonContent = new String(Files.readAllBytes(new File("src/main/java/it/polimi/ingsw/model/resources/factory.json").toPath()));
            return new JSONObject(jsonContent);
        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file JSON: " + e.getMessage());
            return new JSONObject();
        }
    }

    protected Card createCard(JSONObject cardJson) {
        String type = cardJson.getString("type");
        int id = cardJson.getInt("id");
        int level = cardJson.getInt("level");
        boolean isLearner = cardJson.getBoolean("isLearner");

        switch (type) {
            case "SlaversCard":
                int slaversCrew = cardJson.optInt("crew", 0);
                int slaversCredits = cardJson.optInt("credits", 0);
                int slaversDays = cardJson.optInt("days", 0);
                int slaverFirePower = cardJson.optInt("firePower", 0);
                return new SlaversCard(id, level, isLearner, slaversCrew, slaversCredits, slaversDays, slaverFirePower);

            case "SmugglersCard":
                int smugFirePower = cardJson.optInt("firePower", 0);
                int smugLostGoods = cardJson.optInt("lostGoods", 0);
                JSONObject rewardsJson = cardJson.getJSONObject("rewards");
                Map<ColorType, Integer> rewards = new HashMap<>();
                for (String color : rewardsJson.keySet()) {
                    rewards.put(ColorType.valueOf(color.toUpperCase()), rewardsJson.getInt(color));
                }
                int smugDays = cardJson.optInt("days", 0);
                return new SmugglersCard(id, level, isLearner, smugFirePower, smugLostGoods, rewards, smugDays);

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
                return new PiratesCard(id, level, isLearner, pirateFirePower, pirateCredits, pirateDays, piratesCannonFires);

            case "StardustCard":
                return new StardustCard(id, level, isLearner);

            case "OpenSpaceCard":
                return new OpenSpaceCard(id, level, isLearner);

            case "EpidemicCard":
                return new EpidemicCard(id, level, isLearner);

            case "MeteorSwarmCard":
                JSONArray meteorsArray = cardJson.optJSONArray("meteors");
                List<Meteor> meteors = new ArrayList<>();
                for (int i = 0; i < meteorsArray.length(); i++) {
                    JSONObject cannonFireJson = meteorsArray.getJSONObject(i);
                    boolean isBig = cannonFireJson.getBoolean("isBig");
                    DirectionType directionFrom = DirectionType.valueOf(cannonFireJson.getString("directionFrom"));
                    meteors.add(new Meteor(isBig, directionFrom));
                }
                return new MeteorSwarmCard(id, level, isLearner, meteors);

            case "StrayBigMeteorsCard":
                JSONArray meteorsBigArray = cardJson.optJSONArray("cannonFires");
                List<Meteor> meteorsBig = new ArrayList<>();
                for (int i = 0; i < meteorsBigArray.length(); i++) {
                    JSONObject meteorsJson = meteorsBigArray.getJSONObject(i);
                    boolean isBig = meteorsJson.getBoolean("isBig");
                    DirectionType directionFrom = DirectionType.valueOf(meteorsJson.getString("directionFrom"));
                    meteorsBig.add(new Meteor(isBig, directionFrom));
                }
                return new MeteorSwarmCard(id, level, isLearner, meteorsBig);

            case "AbandonedShipCard":
                int abandonedShipCrew = cardJson.optInt("crew", 0);
                int abandonedShipCredits = cardJson.optInt("credits", 0);
                int abandonedShipDays = cardJson.optInt("days", 0);
                return new AbandonedShipCard(id, level, isLearner, abandonedShipCrew, abandonedShipCredits, abandonedShipDays);

            case "AbandonedStationCard":
                int crew = cardJson.getInt("crew");
                int stationDays = cardJson.getInt("days");
                JSONObject goodsJson = cardJson.getJSONObject("goods");
                Map<ColorType, Integer> goods = new HashMap<>();
                for (String color : goodsJson.keySet()) {
                    goods.put(ColorType.valueOf(color.toUpperCase()), goodsJson.getInt(color));
                }
                return new AbandonedStationCard(id, level, isLearner, crew, stationDays, goods);

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
                return new PlanetCard(id, level, isLearner, planets, days);

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
                return new CombatZoneCard(id, level, isLearner, combact);

            default:
                throw new IllegalArgumentException("Unknown card type: " + type);
        }
    }

    public abstract List<Card> getAllCards();

}