package it.polimi.ingsw.model.game;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.factory.CardFactoryAdvancedMode;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.game.objects.Time;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.player.ShipAdvancedMode;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.*;

public class BoardAdvancedMode extends Board {

    private final Time timeManagement;

    public BoardAdvancedMode(List<String> usernames) {
        super();
        this.timeManagement = new Time();

        List<ColorType> colors = Arrays.stream(ColorType.values()).toList();
        for (int i = 0; i < usernames.size(); i++) {
            Ship ship = new ShipAdvancedMode(componentFactory.getStartingCabins().get(colors.get(i)));
            this.startingDeck.add(new PlayerData(usernames.get(i), ship));
        }

        this.cardFactory = new CardFactoryAdvancedMode();
        cardPile.addAll(cardFactory.getCards());
    }

    public void moveHourglass(String username, ModelFacade model) {
        if (timeManagement.getHourglassPos() == 1)
            getPlayersByPos().stream()
                    .filter(player -> player.getUsername().equals(username))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("You can't rotate hourglass because you haven't finished to build your ship"));

        timeManagement.startTimer(model);
    }

    @Override
    public void shuffleCards() {
        do {
            Collections.shuffle(cardPile);
        } while (cardPile.getFirst().getLevel() != 2);
    }

    @Override
    public void startMatch(ModelFacade model) {
        timeManagement.startTimer(model);
    }

    @Override
    public void pickNewCard(ModelFacade model) {
        cardPile.get(cardPilePos).endCard(this);
        super.pickNewCard(model);
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

    @SuppressWarnings("Duplicates")
    @Override
    public String toString(String username, PlayerState state) {
        StringBuilder sb = new StringBuilder();

        switch (state) {
            case BUILD -> {
                sb.append(Constants.displayComponents(commonComponents, 10));

                sb.append("\nHourglass position: ").append(timeManagement.getHourglassPos());
                sb.append("\nTime left: ").append(timeManagement.getTimeLeft()).append("\n");

                for (PlayerData player : startingDeck)
                    sb.append("- ").append(player.getUsername()).append(Chroma.color(" not ready\n", Chroma.RED));
                for (AbstractMap.SimpleEntry<PlayerData, Integer> entry : players)
                    sb.append("- ").append(entry.getKey().getUsername()).append(Chroma.color(" READY\n", Chroma.GREEN));
            }

            case LOOK_CARD_PILE -> {
                int deckIndex = PlayerState.LOOK_CARD_PILE.getDeckIndex().get(username);
                int startingDeckIndex = deckIndex == 0 ? 0 : (deckIndex == 1 ? 3 : 6);
                int endingDeckIndex = startingDeckIndex + 3;
                cardPile.subList(startingDeckIndex, endingDeckIndex).forEach(card -> sb.append(card).append("\n") );
                // todo CHANGE with displayCards

                sb.append("\nHourglass position: ").append(timeManagement.getHourglassPos()).append(timeManagement.getHourglassPos() == 0 ? " (last!)" : "");
                sb.append("\nTime left: ").append(timeManagement.getTimeLeft()).append("\n");

                for (PlayerData player : startingDeck)
                    sb.append("- ").append(player.getUsername()).append(Chroma.color(" not ready\n", Chroma.RED));
                for (AbstractMap.SimpleEntry<PlayerData, Integer> entry : players)
                    sb.append("- ").append(entry.getKey().getUsername()).append(Chroma.color(" READY\n", Chroma.GREEN));
            }

            case DRAW_CARD, WAIT, WAIT_CANNONS, WAIT_ENGINES, WAIT_GOODS, WAIT_REMOVE_GOODS, WAIT_ROLL_DICES, WAIT_REMOVE_CREW, WAIT_SHIELD, WAIT_BOOLEAN, WAIT_INDEX, DONE -> {
                sb.append(Chroma.color("\nCards resolved so far " + getCardPilePos() + "/" + getCardPile().size(), Chroma.GREY_BOLD)).append("\n");

                sb.append("\nPlayers in game:\n");
                for (AbstractMap.SimpleEntry<PlayerData, Integer> entry : players)
                    sb.append("- ").append(entry.getKey().getUsername()).append(" | ").append("flight days: ").append(entry.getValue()).append(" | ").append("$").append(entry.getKey().getCredits()).append("\n");

                if (!startingDeck.isEmpty()) {
                    sb.append("Starting deck:\n");
                    for (PlayerData player : startingDeck)
                        sb.append("  ").append(player.getUsername()).append(" | ").append("$").append(player.getCredits()).append("\n");
                }
            }

            case END -> {
                sb.append("\nRanking:\n");
                for (PlayerData player : this.getRanking())
                    sb.append("-  ").append(player.getUsername()).append(" $").append(player.getCredits()).append("\n");
            }
        }
        return sb.toString();
    }

}
