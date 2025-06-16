package it.polimi.ingsw.client.model.game;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.ClientEventBus;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.client.model.events.CardPileLookedEvent;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.player.ClientShip;
import it.polimi.ingsw.client.model.player.ClientShipLearnerMode;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.List;

public class ClientBoardLearnerMode extends ClientBoard {

    public ClientBoardLearnerMode(List<String> usernames) {
        super();
        List<ColorType> colors = Arrays.stream(ColorType.values()).toList();
        for (int i = 0; i < usernames.size(); i++) {
            ClientPlayer player = new ClientPlayer(usernames.get(i));

            ClientShip ship = new ClientShipLearnerMode();
            player.setShip(ship);
            componentFactory.getStartingCabins().get(colors.get(i)).insertComponent(player, 2, 3, 0, true);

            this.startingDeck.add(player);
        }
    }

    @Override
    public void startMatch(ClientGameModel model) {
        // Do nothing, there aren't specific things to do in learner mode
    }

    @Override
    public List<ClientCard> getLookedCards() {
        throw new RuntimeException("Card piles aren't learner mode flight");
    }

    @Override
    public void moveHourglass() {
        throw new RuntimeException("Hourglass is not in learner mode flight");
    }

    @SuppressWarnings("Duplicates")
    @Override
    public String toString(String username, PlayerState state) {
        StringBuilder sb = new StringBuilder();

        switch (state) {
            case BUILD, WAIT_ALIEN -> {
                sb.append(Constants.displayComponents(commonComponents, 8));

                for (ClientPlayer player : startingDeck.stream().filter(p -> !p.hasEndedInAdvance()).toList())
                    sb.append("- ").append(player.getUsername()).append(Chroma.color(" not ready\n", Chroma.RED));
                for (SimpleEntry<ClientPlayer, Integer> entry : players)
                    sb.append("- ").append(entry.getKey().getUsername()).append(Chroma.color(" READY\n", Chroma.GREEN));

                List<String> left = startingDeck.stream().filter(ClientPlayer::hasEndedInAdvance).map(ClientPlayer::getUsername).toList();
                if (!left.isEmpty()) {
                    sb.append("\nPlayers left:\n");
                    for (String player : left)
                        sb.append("- ").append(Chroma.color(player, Chroma.BLACK_BOLD));
                }

            }

            case DRAW_CARD, WAIT, WAIT_CANNONS, WAIT_ENGINES, WAIT_GOODS, WAIT_REMOVE_GOODS, WAIT_ROLL_DICES, WAIT_REMOVE_CREW, WAIT_SHIELD, WAIT_BOOLEAN, WAIT_INDEX, WAIT_SHIP_PART, DONE -> {
                if (!cardPile.isEmpty())
                    sb.append(Chroma.color("\nCards resolved so far " + (cardPile.size()-(state == PlayerState.DRAW_CARD ? 0 : 1)) + "/8", Chroma.GREY_BOLD)).append("\n");

                sb.append("\nPlayers in game:\n");
                for (SimpleEntry<ClientPlayer, Integer> entry : players)
                    sb.append("- ").append(entry.getKey().getUsername()).append(" | ").append("flight days: ").append(entry.getValue()).append(" | ").append("$").append(entry.getKey().getCredits()).append("\n");

                if (!startingDeck.isEmpty()) {
                    sb.append("Starting deck:\n");
                    for (ClientPlayer player : startingDeck)
                        sb.append("  ").append(player.getUsername()).append(" | ").append("$").append(player.getCredits()).append("\n");
                }
            }

            case END -> {
                sb.append("\nRanking:\n");
                for (ClientPlayer player : getAllPlayers())
                    sb.append("-  ").append(player.getUsername()).append(" $").append(player.getCredits()).append("\n");
            }
        }
        return sb.toString();
    }

}
