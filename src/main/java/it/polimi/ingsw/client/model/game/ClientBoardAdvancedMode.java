package it.polimi.ingsw.client.model.game;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.player.ClientShip;
import it.polimi.ingsw.client.model.player.ClientShipAdvancedMode;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class ClientBoardAdvancedMode extends ClientBoard {

    private int timeLeft;
    private int hourglassPos;
    private final Timer timer;
    private final List<ClientCard> lookedCards;

    public ClientBoardAdvancedMode(List<String> usernames) {
        super();
        this.timer = new Timer();
        this.lookedCards = new ArrayList<>();

        List<ColorType> colors = Arrays.stream(ColorType.values()).toList();
        for (int i = 0; i < usernames.size(); i++) {
            ClientPlayer player = new ClientPlayer(usernames.get(i));

            ClientShip ship = new ClientShipAdvancedMode();
            player.setShip(ship);
            componentFactory.getStartingCabins().get(colors.get(i)).insertComponent(player, 2, 3, 0, true);

            this.startingDeck.add(player);
        }
    }

    @Override
    public List<ClientCard> getLookedCards() {
        return lookedCards;
    }

    private void rotateHourglass() {
        if (hourglassPos > 0) {
            timeLeft = 60;
            hourglassPos--;
        }
    }

    private void startTimer() {
        rotateHourglass();

        TimerTask currentTask = new TimerTask() {
            public void run() {
                if (timeLeft == 1)
                    this.cancel();
                timeLeft -= 1;
            }
        };
        this.timer.scheduleAtFixedRate(currentTask, 1000, 1000);
    }

    @Override
    public void moveHourglass() {
        startTimer();
    }

    @Override
    public void startMatch(ClientGameModel model) {
        this.hourglassPos = 3;
        startTimer();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public String toString(String username, PlayerState state) {
        StringBuilder sb = new StringBuilder();

        switch (state) {
            case BUILD, WAIT_ALIEN, LOOK_CARD_PILE -> {
                sb.append(Constants.displayComponents(commonComponents, 8));

                sb.append("\nHourglass position: ").append(hourglassPos).append(hourglassPos == 0 ? " (last!)" : "");
                sb.append("\nTime left: ").append(timeLeft).append("\n");

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
                    sb.append(Chroma.color("\nCards resolved so far " + (cardPile.size()-(state == PlayerState.DRAW_CARD ? 0 : 1)) + "/12", Chroma.GREY_BOLD)).append("\n");

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
