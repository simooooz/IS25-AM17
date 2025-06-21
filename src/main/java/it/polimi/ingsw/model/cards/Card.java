package it.polimi.ingsw.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CabinComponent;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.exceptions.BatteryComponentNotValidException;
import it.polimi.ingsw.model.exceptions.GoodNotValidException;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = AbandonedShipCard.class, name = "ABANDONED_SHIP"),
        @JsonSubTypes.Type(value = AbandonedStationCard.class, name = "ABANDONED_STATION"),
        @JsonSubTypes.Type(value = CombatZoneCard.class, name = "COMBAT_ZONE"),
        @JsonSubTypes.Type(value = EpidemicCard.class, name = "EPIDEMIC"),
        @JsonSubTypes.Type(value = MeteorSwarmCard.class, name = "METEOR_SWARM"),
        @JsonSubTypes.Type(value = OpenSpaceCard.class, name = "OPEN_SPACE"),
        @JsonSubTypes.Type(value = PiratesCard.class, name = "PIRATES"),
        @JsonSubTypes.Type(value = PlanetCard.class, name = "PLANET"),
        @JsonSubTypes.Type(value = SlaversCard.class, name = "SLAVERS"),
        @JsonSubTypes.Type(value = SmugglersCard.class, name = "SMUGGLERS"),
        @JsonSubTypes.Type(value = StardustCard.class, name = "STARDUST")
})
abstract public class Card {

    @JsonProperty private final int id;
    @JsonProperty private final int level;
    @JsonProperty private final boolean isLearner;

    public Card(int id, int level, boolean isLearner) {
        this.id = id;
        this.level = level;
        this.isLearner = isLearner;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public boolean getIsLearner() {
        return isLearner;
    }

    public abstract boolean startCard(ModelFacade model, Board board);

    public void endCard(Board board) {
        List<PlayerData> players = board.getPlayersByPos();

        for (PlayerData player : players)
            if (player.getShip().getCrew() - (player.getShip().getCannonAlien() ? 1 : 0) - (player.getShip().getEngineAlien() ? 1 : 0) == 0)
                player.endFlight();

        if (!board.getPlayers().isEmpty()) {
            int leaderPos = board.getPlayers().getFirst().getValue();
            for (SimpleEntry<PlayerData, Integer> entry : board.getPlayers())
                if (leaderPos >= entry.getValue() + 24)
                    entry.getKey().endFlight();
        }
    }

    public void doSpecificCheck(PlayerState commandType, Map<ColorType, Integer> rewards, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        if (commandType != PlayerState.WAIT_GOODS) return;

        for (ColorType good : ColorType.values())
            if ((deltaGood.get(good) > 0 && !rewards.containsKey(good)) || (rewards.containsKey(good) && deltaGood.get(good) > rewards.get(good)))
                throw new GoodNotValidException("Reward check not passed, insert only allowed goods");

        if (!batteries.isEmpty())
            throw new BatteryComponentNotValidException("Battery component list should be empty");
    }

    public void doSpecificCheck(PlayerState commandType, int number, Map<ColorType, Integer> deltaGood, List<BatteryComponent> batteries, String username, Board board) {
        if (commandType != PlayerState.WAIT_REMOVE_GOODS) return;
        Ship ship = board.getPlayerEntityByUsername(username).getShip();

        for (ColorType goodType : ColorType.values()) {
            number += deltaGood.get(goodType);
            if (number > 0 && (ship.getGoods().get(goodType) + deltaGood.get(goodType) != 0))
                throw new GoodNotValidException("There are more valuable goods in the ship");
        }

        if (number < 0) throw new IllegalArgumentException("Too many goods provided");
        if (number == 0 && batteries.isEmpty()) return;
        else if (number == 0) throw new IllegalArgumentException("Battery components list should be empty");

        if (batteries.size() > number)
            throw new BatteryComponentNotValidException("Too many battery components provided");
        else if (batteries.size() < number && (ship.getBatteries() >= number || ship.getBatteries() != batteries.size()))
            throw new BatteryComponentNotValidException("Too few battery components provided");
    }

    public void doSpecificCheck(PlayerState commandType, List<CabinComponent> cabins, int toRemove, String username, Board board) {
        int crew = cabins.size();
        int shipCrew = board.getPlayerEntityByUsername(username).getShip().getCrew();
        if (commandType == PlayerState.WAIT_REMOVE_CREW && crew != toRemove && (shipCrew >= toRemove || shipCrew != crew))
            throw new IllegalArgumentException("Too few cabin components provided");
    }

    public void doSpecificCheck(PlayerState commandType, List<CannonComponent> cannons, String username, Board board) {
    }

    public boolean doCommandEffects(PlayerState commandType, Integer value, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    public boolean doCommandEffects(PlayerState commandType, Double value, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    public boolean doCommandEffects(PlayerState commandType, Boolean value, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }

    public boolean doCommandEffects(PlayerState commandType, ModelFacade model, Board board, String username) {
        throw new RuntimeException("Method not valid");
    }
}
