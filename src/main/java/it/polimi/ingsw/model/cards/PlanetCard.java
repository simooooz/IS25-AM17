package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.utils.Planet;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.Map;
import java.util.Optional;

public class PlanetCard extends Card{

    private final Map<Planet, Optional<PlayerData>> planets;
    private final int days;

    public PlanetCard(int level, boolean isLearner, Map<Planet, Optional<PlayerData>> planets, int days) {
        super(level, isLearner);
        this.planets = planets;
        this.days = days;
    }

    public Map<Planet, Optional<PlayerData>> getPlanets() {
        return planets;
    }

    public int getDays() {
        return days;
    }

    @Override
    public void resolve(Board board){}
}
