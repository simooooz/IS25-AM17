package it.polimi.ingsw.model.game;

import it.polimi.ingsw.common.dto.BoardDTO;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.factory.CardFactory;
import it.polimi.ingsw.model.factory.CardFactoryAdvancedMode;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.factory.ComponentFactory;
import it.polimi.ingsw.model.game.objects.Time;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.player.ShipAdvancedMode;

import java.util.*;
import java.util.function.Consumer;

public class BoardAdvancedMode extends Board {

    private final Time timeManagement;
    private final Map<String, Integer> cardPilesWatchMap;

    public BoardAdvancedMode(List<String> usernames) {
        super();
        this.cardPilesWatchMap = new HashMap<>();
        this.timeManagement = new Time();
        ComponentFactory componentFactory = new ComponentFactory();
        this.commonComponents = new ArrayList<>(componentFactory.getComponents());
        this.mapIdComponents = new HashMap<>(componentFactory.getComponentsMap());
        List<ColorType> colors = Arrays.stream(ColorType.values()).toList();
        for (int i = 0; i < usernames.size(); i++) {
            PlayerData player = new PlayerData(usernames.get(i));

            Ship ship = new ShipAdvancedMode();
            player.setShip(ship);

            componentFactory.getStartingCabins().get(colors.get(i)).insertComponent(player, 2, 3, 0, true);

            this.startingDeck.add(player);
        }

        CardFactory cardFactory = new CardFactoryAdvancedMode();
        cardPile.addAll(cardFactory.getCards());
    }

    @Override
    public void moveHourglass(String username, ModelFacade model, Consumer<List<GameEvent>> callback) {
        if (timeManagement.getHourglassPos() == 1)
            getPlayersByPos().stream()
                    .filter(player -> player.getUsername().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("You can't rotate hourglass because you haven't finished to build your ship"));

        timeManagement.startTimer(model, callback);
    }

    @Override
    public void shuffleCards() {
        do {
            Collections.shuffle(cardPile);
        } while (cardPile.getFirst().getLevel() != 2);
    }

    @Override
    public void startMatch(ModelFacade model) {
        timeManagement.startTimer(model, (_) -> {});
    }

    @Override
    public void pickNewCard(ModelFacade model) {
        cardPile.get(cardPilePos).endCard(this);
        super.pickNewCard(model);
    }

    @Override
    public Map<String, Integer> getCardPilesWatchMap() {
        return cardPilesWatchMap;
    }

    @Override
    public int[] getBoardOrderPos() {
        return new int[]{6, 3, 1, 0};
    }

    @Override
    protected int[] getRankingCreditsValues() {
        return new int[]{8, 6, 4, 2};
    }

    @Override
    protected int getRankingMostBeautifulShipReward() {
        return 4;
    }

    @Override
    public BoardDTO toDto() {
        BoardDTO dto = super.toDto();
        dto.timeLeft = timeManagement.getTimeLeft();
        dto.hourglassPos = timeManagement.getHourglassPos();
        return dto;
    }

}
