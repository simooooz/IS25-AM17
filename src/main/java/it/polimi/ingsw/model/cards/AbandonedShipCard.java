package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.player.PlayerData;

import java.util.List;

public class AbandonedShipCard extends Card {
    private final int crew;
    private final int credits;
    private final int days;
    private int playerIndex;
    private int conquerorPlayerIndex;
    private List<PlayerData> players;
    private boolean shipConquered;

    public AbandonedShipCard(int level, boolean isLearner, int crew, int credits, int days) {
        super(level, isLearner);
        this.crew = crew;
        this.credits = credits;
        this.days = days;
    }

    public void startCard(Board board) {
        this.playerIndex = 0;
        this.conquerorPlayerIndex = -1;
        this.players = board.getPlayersByPos();
        this.shipConquered = false;

        for (PlayerData player : board.getPlayersByPos()) {
            playersState.put(player.getUsername(), CardState.WAIT);
        }
        autoCheckPlayers();
    }

    public void changeState(Board board, String username) {

        CardState actState = playersState.get(username);

        switch (actState) {
            case WAIT_CREW -> playersState.put(username, CardState.DONE);
            case WAIT_BOOLEAN -> {
                if (conquerorPlayerIndex == players.indexOf(getPlayerbyEntiy(username))) { // basta !=-1 ??
                    playersState.put(username, CardState.WAIT_CREW);
                    shipConquered = true;
                }
                else
                    playersState.put(username, CardState.DONE);

            }
        }

        playerIndex++;
        autoCheckPlayers();

    }

    private void autoCheckPlayers() {
        for (; playerIndex < players.size(); playerIndex++) {
            PlayerData player = players.get(playerIndex);

            if (shipConquered)
                playersState.put(player.getUsername(), CardState.DONE);
            else if (player.getShip().getCrew() < crew) // User loses automatically
                playersState.put(player.getUsername(), CardState.DONE);
            else { // User could win
                playersState.put(player.getUsername(), CardState.WAIT_BOOLEAN);
                return;
            }
        }

        // Check if everyone has finished
        boolean hasDone = true;
        for (PlayerData player : players)
            if (playersState.get(player.getUsername()) != CardState.DONE)
                hasDone = false;

        if (hasDone) {
            endCard();
        }
    }

    public void endCard() {

    }

    public void doCommandEffects(CardState commandType, Boolean value, String username, Board board) {
        PlayerData player = board.getPlayerEntitybyUsername(username);
        if (commandType == CardState.WAIT_BOOLEAN && value) {
            conquerorPlayerIndex = players.indexOf(getPlayerbyEntity(username));
        }
        else if (commandType == CardState.WAIT_CREW) {
            board.movePlayer(player, days * -1);
            player.setCredits(credits + player.getCredits());
        }
    }

}
