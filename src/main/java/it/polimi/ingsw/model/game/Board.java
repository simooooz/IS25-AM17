package it.polimi.ingsw.model.game;

import it.polimi.ingsw.common.dto.BoardDTO;
import it.polimi.ingsw.common.dto.GameStateDTOFactory;
import it.polimi.ingsw.common.model.events.GameEvent;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.common.model.events.game.PlayersPositionUpdatedEvent;
import it.polimi.ingsw.common.model.events.EventContext;
import it.polimi.ingsw.model.exceptions.PlayerNotFoundException;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.model.factory.CardFactory;
import it.polimi.ingsw.model.player.PlayerData;


import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class Board {

    protected Map<Integer, Component> mapIdComponents;
    protected List<Component> commonComponents;

    protected final List<SimpleEntry<PlayerData, Integer>> players;
    protected final List<PlayerData> startingDeck;

    protected final List<Card> cardPile;
    protected int cardPilePos;

    public Board() {
        this.startingDeck = new ArrayList<>();
        this.players = new ArrayList<>();

        this.cardPile = new ArrayList<>();
        this.cardPilePos = 0;
    }

    public List<SimpleEntry<PlayerData, Integer>> getPlayers() {
        return players;
    }

    public List<PlayerData> getPlayersByPos() {
        return players.stream().map(SimpleEntry::getKey).collect(Collectors.toList());
    }

    public PlayerData getPlayerEntityByUsername(String username) {
        return Stream.concat(players.stream().map(SimpleEntry::getKey), startingDeck.stream())
                .filter(p -> p.getUsername().equals(username))
                .findFirst()
                .orElseThrow(PlayerNotFoundException::new);
    }

    public List<PlayerData> getStartingDeck() {
        return startingDeck;
    }

    public Map<Integer, Component> getMapIdComponents() {
        return mapIdComponents;
    }

    public List<Component> getCommonComponents() {
        return commonComponents;
    }

    public List<Card> getCardPile() {
        return cardPile;
    }

    public int getCardPilePos() {
        return cardPilePos;
    }

    public void pickNewCard(ModelFacade model) {
        for (PlayerData player : getPlayersByPos())
            if (player.hasEndedInAdvance()) {
                model.setPlayerState(player.getUsername(), PlayerState.WAIT);
                moveToStartingDeck(player);
            }

        cardPilePos++;
        if (cardPilePos == cardPile.size() || players.isEmpty()) // All cards are resolved or there are no more players
            model.endGame();
        else { // Change card
            for (PlayerData p : getPlayersByPos())
                model.setPlayerState(p.getUsername(), PlayerState.WAIT);
            model.setPlayerState(getPlayersByPos().getFirst().getUsername(), PlayerState.DRAW_CARD);
        }
    }

    public void movePlayer(PlayerData playerData, int position) {
        if (position == 0) return;
        SimpleEntry<PlayerData, Integer> entry = players.stream()
                .filter(e -> e.getKey().equals(playerData))
                .findFirst()
                .orElseThrow(PlayerNotFoundException::new);

        for (int d = 0; d < Math.abs(position); d++) {
            int currentPosition = entry.getValue();
            int nextPosition = (position > 0) ? currentPosition + 1 : currentPosition - 1;
            boolean moved = false; // check if we've moved

            while (!moved) {
                boolean positionOccupied = false; // check if the position in occupied

                for (SimpleEntry<PlayerData, Integer> otherEntry : players) {
                    if (!otherEntry.equals(entry) && otherEntry.getValue() == nextPosition) {
                        positionOccupied = true;
                        break;
                    }
                }

                if (!positionOccupied) {
                    entry.setValue(nextPosition);
                    moved = true;
                }
                else
                    nextPosition = (position > 0) ? nextPosition + 1 : nextPosition - 1;
            }
        }

        players.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        EventContext.emit(new PlayersPositionUpdatedEvent(
                startingDeck.stream().map(PlayerData::getUsername).toList(),
                players.stream().map(e -> new SimpleEntry<>(e.getKey().getUsername(), e.getValue())).toList())
        );
    }

    public void moveToStartingDeck(PlayerData player) {
        players.stream()
                .filter(el -> el.getKey().equals(player))
                .findFirst()
                .ifPresent(e -> {
                    players.remove(e);
                    startingDeck.add(player);
                });

        EventContext.emit(new PlayersPositionUpdatedEvent(
                startingDeck.stream().map(PlayerData::getUsername).toList(),
                players.stream().map(e -> new SimpleEntry<>(e.getKey().getUsername(), e.getValue())).toList())
        );
    }

    public void moveToBoard(PlayerData player) {
        startingDeck.stream()
                .filter(p -> p.equals(player))
                .findFirst()
                .orElseThrow(PlayerNotFoundException::new);
        int pos = getBoardOrderPos()[players.size()];
        players.add(new SimpleEntry<>(player, pos));

        players.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        startingDeck.remove(player);

        EventContext.emit(new PlayersPositionUpdatedEvent(
                startingDeck.stream().map(PlayerData::getUsername).toList(),
                players.stream().map(e -> new SimpleEntry<>(e.getKey().getUsername(), e.getValue())).toList())
        );
    }

    // TODO check
    public List<PlayerData> calcRanking() {
        List<PlayerData> players = Stream.concat(
                this.getPlayersByPos().stream(),
                this.getStartingDeck().stream()
        ).toList();

        int[] credits = getRankingCreditsValues();

        Map<ColorType, Integer> CREDIT_MULTIPLIERS = Map.of(
                ColorType.RED, 4,
                ColorType.YELLOW, 3,
                ColorType.GREEN, 2,
                ColorType.BLUE, 1
        );

        IntStream.range(0, players.size())
                .forEach(i -> {
                    PlayerData player = players.get(i);
                    // reward for the order of arrival (only not dropped out players)
                    if (this.getPlayersByPos().contains(player))
                        player.setCredits(player.getCredits() + credits[i]);

                    // handling sale of goods - calculating total goods value first
                    int totalGoodsCredits = 0;
                    for (ColorType good : player.getShip().getGoods().keySet()) {
                        totalGoodsCredits += CREDIT_MULTIPLIERS.get(good) * player.getShip().getGoods().get(good);
                    }

                    // apply starting deck penalty if applicable (divide total by 2 and round up)
                    if (this.getStartingDeck().contains(player)) {
                        totalGoodsCredits = (int) Math.ceil(totalGoodsCredits / 2.0);
                    }

                    // add total goods credits to player's credits
                    player.setCredits(player.getCredits() + totalGoodsCredits);
                    // component leaks
                    player.setCredits(player.getCredits() - player.getShip().getDiscards().size() - player.getShip().getReserves().size());
                });

        // reward for the most beautiful ship
        int[] exposedConnectors = players.stream()
                .mapToInt(p -> p.getShip().countExposedConnectors())
                .toArray();
        players.stream()
                .filter(p -> p.getShip().countExposedConnectors() == Arrays.stream(exposedConnectors).min().orElseThrow())
                .forEach(p -> p.setCredits(p.getCredits() + getRankingMostBeautifulShipReward()));

        return players.stream()
                .sorted(Comparator.comparingInt(PlayerData::getCredits).reversed())
                .toList();
    }

    public BoardDTO toDto() {
        BoardDTO boardDTO = new BoardDTO();

        boardDTO.mapIdComponents = mapIdComponents.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toDTO()));
        boardDTO.commonComponents = commonComponents.stream()
                .map(Component::getId).toList();
        boardDTO.players = players.stream()
                .map(GameStateDTOFactory::createPlayerPositionDTO).toList();
        boardDTO.startingDeck = startingDeck.stream()
                .map(GameStateDTOFactory::createPlayerDTO).toList();
        boardDTO.cardPile = CardFactory.serializeCardList(cardPile.stream()
                .limit(cardPilePos).toList());

        return boardDTO;
    }

    public abstract Map<String, Integer> getCardPilesWatchMap();

    public abstract void shuffleCards();

    public abstract void startMatch(ModelFacade model);

    public abstract void moveHourglass(String username, ModelFacade model, Consumer<List<GameEvent>> callback);

    public abstract int[] getBoardOrderPos();

    protected abstract int[] getRankingCreditsValues();

    protected abstract int getRankingMostBeautifulShipReward();

}