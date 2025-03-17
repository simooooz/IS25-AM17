package it.polimi.ingsw.model.cards.factory;

import it.polimi.ingsw.model.cards.utils.*;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.properties.DirectionType;
import org.json.JSONArray;
import org.json.JSONObject;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.AbandonedShipCard;
import it.polimi.ingsw.model.cards.AbandonedStationCard;
import it.polimi.ingsw.model.cards.CombactZoneCard;
import it.polimi.ingsw.model.cards.EpidemicCard;
import it.polimi.ingsw.model.cards.MeteorSwarmCard;
import it.polimi.ingsw.model.cards.OpenSpaceCard;
import it.polimi.ingsw.model.cards.PiratesCard;
import it.polimi.ingsw.model.cards.PlanetCard;
import it.polimi.ingsw.model.cards.SlaversCard;
import it.polimi.ingsw.model.cards.SmugglersCard;
import it.polimi.ingsw.model.cards.StardustCard;
import it.polimi.ingsw.model.cards.StrayBigMeteorsCard;

import java.util.*;

public interface CardFactory {
    public default Card createCard(JSONObject cardJson) {
        String type = cardJson.getString("type");
        int level = cardJson.getInt("level");
        boolean isLearner = cardJson.getBoolean("isLearner");

        // Factory pattern
        switch(type) {
            case "SlaversCard":
                int slaversCrew = cardJson.optInt("crew", 0);
                int slaversCredits = cardJson.optInt("credits", 0);
                int slaversDays = cardJson.optInt("days", 0);
                int slaverFirePower = cardJson.optInt("firePower", 0);
                return new SlaversCard(level, isLearner, slaversCrew, slaversCredits, slaversDays, slaverFirePower);

            case "SmugglersCard":
                int smugFirePower = cardJson.optInt("firePower", 0);
                int smugLostGoods = cardJson.optInt("lostGoods", 0);
                JSONArray smugGoodsArray = cardJson.getJSONArray("goods");
                List<ColorType> smugGoods = new ArrayList<>();
                for (int i = 0; i < smugGoodsArray.length(); i++) {
                    smugGoods.add(ColorType.valueOf(smugGoodsArray.getString(i)));
                }
                int smugDays = cardJson.optInt("days", 0);
                return new SmugglersCard(level, isLearner, smugFirePower, smugLostGoods, smugGoods, smugDays);

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
                JSONArray meteorsArray = cardJson.optJSONArray("cannonFires");
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
                JSONObject planetsJson = cardJson.getJSONObject("planets");
                Map<Planet, Optional<PlayerData>> planets = new HashMap<>();
                for (String planetName : planetsJson.keySet()) {
                    JSONObject planetData = planetsJson.getJSONObject(planetName);
                    JSONObject rewardsJson = planetData.getJSONObject("rewards");
                    Map<ColorType, Integer> rewards = new HashMap<>();

                    for (String color : rewardsJson.keySet()) {
                        rewards.put(ColorType.valueOf(color.toUpperCase()), rewardsJson.getInt(color));
                    }

                    Planet planet = new Planet(rewards);
                    planets.put(planet, null);
                }
                int days = cardJson.optInt("days", 0);
                return new PlanetCard(level, isLearner, planets, days);

            case "CombactZoneCard":
                JSONArray combactArray = cardJson.optJSONArray("warLines");
                List<AbstractMap.SimpleEntry<CriteriaType, PenaltyCombatZone>> combact = new ArrayList<>();

                for (int i = 0; i < combactArray.length(); i++) {
                    JSONObject combactJson = combactArray.getJSONObject(i);
                    CriteriaType criteria = CriteriaType.valueOf(combactJson.getString("criteria"));
                    PenaltyCombatZone penalty = PenaltyCombatZone.valueOf(combactJson.getString("penalty"));
                    combact.add(new AbstractMap.SimpleEntry<>(criteria, penalty));
                }
                return new CombactZoneCard(level, isLearner, combact);

            default:
                throw new IllegalArgumentException("Unknown card type: " + type);
        }
    }
}