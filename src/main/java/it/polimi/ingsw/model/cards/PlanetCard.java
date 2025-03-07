package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.utils.Planet;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.AbstractMap;
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
        for (AbstractMap.SimpleEntry<PlayerData, Integer> entry : board.getPlayers()){
            PlayerData player=entry.getKey();
            Planet landingPlanet = (pianetadaricevere); //chiedo l'eventuale pianeta all'utente
            if(landingPlanet !=null){
                // se il pianeta che ha scelto è nella carta e non c'è nessun altro
                if((planets.containsKey(landingPlanet))&& planets.get(landingPlanet).isEmpty()){
                    // decremento le merci dalla plancia e le aggiungo al giocatore
                    for(ColorType g: landingPlanet.getRewards()){
                        int rewardTypeG= landingPlanet.getNumberOfRewards(g);
                        int availableG= board.getAvailableGoods().get(g);
                        // se le merci sulla plancia sono minori di quelle della carta
                        // le do tutte al giocatore e metto quelle disponibili a 0
                        if(availableG < rewardTypeG){
                            while(rewardTypeG > 0){
                                //chiedo il componente sul quale caricare la/le merci
                                //componente=askforlocation()
                                //loadGoods(componente);
                                rewardTypeG--;
                                player.getShip().getGoods().put(g, availableG);
                                board.getAvailableGoods().put(g,0);
                            }
                        }
                        else{
                            //chiedo il componente sul quale caricare le merci
                            //componente=askforlocation()
                            //loadGoods(componente);

                            //aggiungo al player quelle previste dalla carta
                            //decremento quelle disponibili
                            player.getShip().getGoods().put(g, rewardTypeG);
                            board.getAvailableGoods().put(g,availableG-rewardTypeG);
                        }
                    }
                }

            }
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
