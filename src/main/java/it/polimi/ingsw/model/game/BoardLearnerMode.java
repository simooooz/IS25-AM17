package it.polimi.ingsw.model.game;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.PlayerState;
import it.polimi.ingsw.model.factory.CardFactoryLearnerMode;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.model.player.ShipLearnerMode;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.*;

public class BoardLearnerMode extends Board {

    public BoardLearnerMode(List<String> usernames) {
        super();

        List<ColorType> colors = Arrays.stream(ColorType.values()).toList();
        for (int i = 0; i < usernames.size(); i++) {
            Ship ship = new ShipLearnerMode(componentFactory.getStartingCabins().get(colors.get(i)));
            this.startingDeck.add(new PlayerData(usernames.get(i), ship));
        }

        this.cardFactory = new CardFactoryLearnerMode();
        cardPile.addAll(cardFactory.getCards());
    }

    @Override
    public void shuffleCards() {
        Collections.shuffle(cardPile);
    }

    @Override
    public void startMatch(ModelFacade model) {
        // Do nothing, there aren't specific things to do in learner mode
    }

    @Override
    public int[] getBoardOrderPos() {
        return new int[]{4, 2, 1, 0};
    }

    @Override
    public void moveHourglass(String username, ModelFacade model) {
        throw new RuntimeException("Hourglass is not in learner mode flight");
    }

    @Override
    protected int[] getRankingCreditsValues() {
        return new int[]{4, 3, 2, 1};
    }

    @Override
    protected int getRankingMostBeautifulShipReward() {
        return 2;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public String toString(String username, PlayerState state) {
        StringBuilder sb = new StringBuilder();

        switch (state) {
            case BUILD -> {
                sb.append(Constants.displayComponents(commonComponents, 8));

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
