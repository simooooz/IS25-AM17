package it.polimi.ingsw.client.model.game;

import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.ClientGameModel;
import it.polimi.ingsw.client.model.components.ClientComponent;
import it.polimi.ingsw.client.model.factory.ClientComponentFactory;
import it.polimi.ingsw.client.model.player.ClientPlayer;
import it.polimi.ingsw.client.model.player.ClientShip;
import it.polimi.ingsw.client.model.player.ClientShipLearnerMode;
import it.polimi.ingsw.common.dto.BoardDTO;
import it.polimi.ingsw.common.dto.GameStateDTOFactory;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

public class ClientBoardLearnerMode extends ClientBoard {

    public ClientBoardLearnerMode(List<String> usernames) {
        super();

        ClientComponentFactory componentFactory = new ClientComponentFactory();
        this.commonComponents = new ArrayList<>(componentFactory.getComponents());
        this.mapIdComponents = new HashMap<>(componentFactory.getComponentsMap());

        List<ColorType> colors = Arrays.stream(ColorType.values()).toList();
        for (int i = 0; i < usernames.size(); i++) {
            ClientPlayer player = new ClientPlayer(usernames.get(i));

            ClientShip ship = new ClientShipLearnerMode();
            player.setShip(ship);

            componentFactory.getStartingCabins().get(colors.get(i)).insertComponent(player, 2, 3, 0, true);

            this.startingDeck.add(player);
        }
    }

    public ClientBoardLearnerMode(BoardDTO dto) {
        super(dto);

        this.mapIdComponents = new HashMap<>();
        for (Integer id : dto.mapIdComponents.keySet()) {
            ClientComponent component = GameStateDTOFactory.componentFromDTO(dto.mapIdComponents.get(id));
            this.mapIdComponents.put(id, component);
        }

        this.commonComponents = dto.commonComponents.stream().map(id -> this.mapIdComponents.get(id)).toList();

        this.startingDeck = dto.startingDeck.stream().map(e -> {
            ClientShip ship = new ClientShipLearnerMode();
            if (e.ship.componentInHand != null)
                ship.setComponentInHand(mapIdComponents.get(e.ship.componentInHand));
            for (Integer discard : e.ship.discards)
                ship.getDiscards().add(mapIdComponents.get(discard));
            for (Integer reserve : e.ship.reserves)
                ship.getReserves().add(mapIdComponents.get(reserve));
            for (int i = 0; i < e.ship.dashboard.length; i++)
                for (int j = 0; j < e.ship.dashboard[i].length; j++)
                    ship.getDashboard()[i][j] = e.ship.dashboard[i][j] == null ? Optional.empty() : Optional.of(mapIdComponents.get(e.ship.dashboard[i][j]));

            return new ClientPlayer(e, ship);
        }).toList();

        this.players = dto.players.stream().map(e -> {
            ClientShip ship = new ClientShipLearnerMode();
            if (e.player.ship.componentInHand != null)
                ship.setComponentInHand(mapIdComponents.get(e.player.ship.componentInHand));
            for (Integer discard : e.player.ship.discards)
                ship.getDiscards().add(mapIdComponents.get(discard));
            for (Integer reserve : e.player.ship.reserves)
                ship.getReserves().add(mapIdComponents.get(reserve));
            for (int i = 0; i < e.player.ship.dashboard.length; i++)
                for (int j = 0; j < e.player.ship.dashboard[i].length; j++)
                    ship.getDashboard()[i][j] = e.player.ship.dashboard[i][j] == null ? Optional.empty() : Optional.of(mapIdComponents.get(e.player.ship.dashboard[i][j]));

            return new SimpleEntry<>(new ClientPlayer(e.player, ship), e.position);
        }).toList();

    }

    @Override
    public void startMatch(ClientGameModel model) {
        // Do nothing, there aren't specific things to do in learner mode
    }

    @Override
    public void moveHourglass() {
        throw new RuntimeException("Hourglass is not in learner mode flight");
    }

    @Override
    public int getHourglassPos() {
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
                for (ClientPlayer player : startingDeck.stream().filter(ClientPlayer::hasEndedInAdvance).toList())
                    sb.append("- ").append(player).append("\n");

            }

            case DRAW_CARD, WAIT, WAIT_CANNONS, WAIT_ENGINES, WAIT_GOODS, WAIT_REMOVE_GOODS, WAIT_ROLL_DICES, WAIT_REMOVE_CREW, WAIT_SHIELD, WAIT_BOOLEAN, WAIT_INDEX, WAIT_SHIP_PART, DONE -> {
                if (!cardPile.isEmpty())
                    sb.append(Chroma.color("\nCards resolved so far " + (cardPile.size()-(state == PlayerState.DRAW_CARD ? 0 : 1)) + "/8", Chroma.GREY_BOLD)).append("\n");

                sb.append("\nPlayers in game:\n");
                for (SimpleEntry<ClientPlayer, Integer> entry : players)
                    sb.append("- ").append(entry.getKey()).append(" | ").append("flight days: ").append(entry.getValue()).append(" | ").append("$").append(entry.getKey().getCredits()).append("\n");

                if (!startingDeck.isEmpty()) {
                    sb.append("\bStarting deck:\n");
                    for (ClientPlayer player : startingDeck)
                        sb.append("- ").append(player).append(" | ").append("$").append(player.getCredits()).append("\n");
                }

                sb.append(ColorType.RED).append("  ").append(4).append("\t").append(ColorType.YELLOW).append("  ").append(3).append("\t").append(ColorType.GREEN).append("  ").append(2).append("\t").append(ColorType.BLUE).append("  ").append(   1);
            }

            case END -> {
                sb.append("\nRanking:\n");
                for (ClientPlayer player : getAllPlayers())
                    sb.append("- ").append(player).append(" $").append(player.getCredits()).append("\n");
            }
        }
        return sb.toString();
    }

}
