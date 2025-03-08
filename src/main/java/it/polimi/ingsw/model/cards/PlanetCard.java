package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.utils.Planet;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.AbstractMap.SimpleEntry;
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
    public void resolve(Board board){
        for (SimpleEntry<PlayerData, Integer> entry : board.getPlayers()) {
            PlayerData player = entry.getKey();
            Optional<Planet> landingPlanet = Optional.of(pianetadaricevere); //chiedo l'eventuale pianeta all'utente

            landingPlanet.isPresent((Planet planet) -> {
                planet.getRewards().keySet().forEach(goodType -> {
                    int rewardTypeG = planet.getRewards().get(goodType);
                    int availableG = board.getAvailableGoods().get(goodType);

                    int toDecrease = availableG < rewardTypeG ? availableG : rewardTypeG;
                    while (toDecrease > 0) {

                        toDecrease--;
                    }
                    board.getAvailableGoods().put(g, availableG - toDecrease);
                    player.getShip().getGoods().put(g, rewardTypeG);
                });
            });
        };

        //dopo che tutti hanno scelto se atterrare decremento i giorni di volo in ordine inverso di rotta
        for(int i=board.getPlayers().size()-1;i>=0;i--){
            PlayerData player = board.getPlayers().get(i).getKey();
            if(planets.values().stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .anyMatch(playerData -> playerData.equals(player))){
                        board.getPlayers().get(i).setValue(board.getPlayers().get(i).getValue()-days);
            }
        }
    }

}
