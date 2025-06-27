package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.client.controller.ClientGameController;
import it.polimi.ingsw.client.model.ClientEventBus;
import it.polimi.ingsw.client.model.cards.ClientCard;
import it.polimi.ingsw.client.model.factory.ClientCardFactory;
import it.polimi.ingsw.client.model.game.ClientLobby;
import it.polimi.ingsw.common.dto.GameStateDTOFactory;
import it.polimi.ingsw.common.dto.ModelDTO;
import it.polimi.ingsw.common.model.enums.PlayerState;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.events.game.ErrorEvent;
import it.polimi.ingsw.network.UserState;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.network.socket.server.ClientHandler;
import it.polimi.ingsw.network.socket.server.SocketServer;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;

/**
 * Enumeration of all message types used in client-server communication.
 * Each enum constant implements the Command pattern by providing specific
 * execution logic for both client and server sides.
 */
@SuppressWarnings("unchecked")
public enum MessageType {

    /**
     * Error message display
     */
    ERROR {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            ClientEventBus.getInstance().publish(new ErrorEvent(castedMessage.getArg1()));
        }
    },

    /**
     * Start event batching
     */
    BATCH_START {
        @Override
        public void execute(ClientSocket client, Message message) {
            ClientEventBus.getInstance().startBatch();
        }
    },

    /**
     * End event batching
     */
    BATCH_END {
        @Override
        public void execute(ClientSocket client, Message message) {
            ClientEventBus.getInstance().endBatch();
        }
    },

    /**
     * Set username request
     */
    SET_USERNAME {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            SocketServer.setUsername(user, castedMessage.getArg1());
        }
    },

    /**
     * Username set confirmation
     */
    USERNAME_OK_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            client.setState(UserState.LOBBY_SELECTION);
            client.setUsername(castedMessage.getArg1());
        }
    },

    /**
     * Create lobby request
     */
    CREATE_LOBBY {
        @Override
        public void execute(ClientHandler user, Message message) {
            TripleArgMessage<String, Integer, Boolean> castedMessage = (TripleArgMessage<String, Integer, Boolean>) message;
            SocketServer.createLobby(user, castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3());
        }
    },

    /**
     * Lobby created event
     */
    CREATED_LOBBY_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            QuadrupleArgMessage<String, List<String>, Boolean, Integer> castedMessage = (QuadrupleArgMessage<String, List<String>, Boolean, Integer>) message;
            client.setState(UserState.IN_LOBBY);
            client.setLobby(new ClientLobby(castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3(), castedMessage.getArg4()));
        }
    },

    /**
     * Join random lobby request
     */
    JOIN_RANDOM_LOBBY {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Boolean> castedMessage = (SingleArgMessage<Boolean>) message;
            SocketServer.joinRandomLobby(user, castedMessage.getArg1());
        }
    },

    /**
     * Join specific lobby request
     */
    JOIN_LOBBY {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            SocketServer.joinLobby(user, castedMessage.getArg1());
        }
    },

    /**
     * Player joined lobby event
     */
    JOINED_LOBBY_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            client.getLobby().addPlayer(castedMessage.getArg1());
        }
    },

    /**
     * Leave game request
     */
    LEAVE_GAME {
        @Override
        public void execute(ClientHandler user, Message message) {
            SocketServer.leaveGame(user);
        }
    },

    /**
     * Player left lobby event
     */
    LEFT_LOBBY_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            if (!castedMessage.getArg1().equals(client.getUsername()))
                client.getLobby().removePlayer(castedMessage.getArg1());
            else {
                client.setState(UserState.LOBBY_SELECTION);
                client.setLobby(null);
            }
        }
    },

    /**
     * Synchronize all game state
     */
    SYNC_ALL_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            ModelDTO dto = GameStateDTOFactory.deserializeDTO(castedMessage.getArg1());

            if (dto != null) {
                client.setState(UserState.IN_GAME);
                ClientLobby lobby = client.getLobby();
                lobby.setGame(new ClientGameController(lobby.isLearnerMode(), dto));
            }
        }
    },

    /**
     * Match started event
     */
    MATCH_STARTED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            client.setState(UserState.IN_GAME);
            client.getLobby().initGame();
        }
    },

    /**
     * Players state updated
     */
    PLAYERS_STATE_UPDATED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<Map<String, PlayerState>> castedMessage = (SingleArgMessage<Map<String, PlayerState>>) message;
            client.getGameController().playersStateUpdated(castedMessage.getArg1());
        }
    },

    /**
     * Pick component request
     */
    PICK_COMPONENT {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            SocketServer.pickComponent(user, castedMessage.getArg1());
        }
    },

    /**
     * Component picked event
     */
    COMPONENT_PICKED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Integer> castedMessage = (DoubleArgMessage<String, Integer>) message;
            client.getGameController().componentPicked(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    /**
     * Release component request
     */
    RELEASE_COMPONENT {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            SocketServer.releaseComponent(user, castedMessage.getArg1());
        }
    },

    /**
     * Component released event
     */
    COMPONENT_RELEASED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Integer> castedMessage = (DoubleArgMessage<String, Integer>) message;
            client.getGameController().componentReleased(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    /**
     * Reserve component request
     */
    RESERVE_COMPONENT {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            SocketServer.reserveComponent(user, castedMessage.getArg1());
        }
    },

    /**
     * Component reserved event
     */
    COMPONENT_RESERVED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Integer> castedMessage = (DoubleArgMessage<String, Integer>) message;
            client.getGameController().componentReserved(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    /**
     * Insert component request
     */
    INSERT_COMPONENT {
        @Override
        public void execute(ClientHandler user, Message message) {
            QuadrupleArgMessage<Integer, Integer, Integer, Integer> castedMessage = (QuadrupleArgMessage<Integer, Integer, Integer, Integer>) message;
            SocketServer.insertComponent(user, castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3(), castedMessage.getArg4());
        }
    },

    /**
     * Component inserted event
     */
    COMPONENT_INSERTED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            QuadrupleArgMessage<String, Integer, Integer, Integer> castedMessage = (QuadrupleArgMessage<String, Integer, Integer, Integer>) message;
            client.getGameController().componentInserted(castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3(), castedMessage.getArg4());
        }
    },

    /**
     * Move component request
     */
    MOVE_COMPONENT {
        @Override
        public void execute(ClientHandler user, Message message) {
            QuadrupleArgMessage<Integer, Integer, Integer, Integer> castedMessage = (QuadrupleArgMessage<Integer, Integer, Integer, Integer>) message;
            SocketServer.moveComponent(user, castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3(), castedMessage.getArg4());
        }
    },

    /**
     * Component moved event
     */
    COMPONENT_MOVED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            QuadrupleArgMessage<String, Integer, Integer, Integer> castedMessage = (QuadrupleArgMessage<String, Integer, Integer, Integer>) message;
            client.getGameController().componentMoved(castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3(), castedMessage.getArg4());
        }
    },

    /**
     * Rotate component request
     */
    ROTATE_COMPONENT {
        @Override
        public void execute(ClientHandler user, Message message) {
            DoubleArgMessage<Integer, Integer> castedMessage = (DoubleArgMessage<Integer, Integer>) message;
            SocketServer.rotateComponent(user, castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    /**
     * Component rotated event
     */
    COMPONENT_ROTATED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<Integer, Integer> castedMessage = (DoubleArgMessage<Integer, Integer>) message;
            client.getGameController().componentRotated(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    /**
     * Component destroyed event
     */
    COMPONENT_DESTROYED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Integer> castedMessage = (DoubleArgMessage<String, Integer>) message;
            client.getGameController().componentDestroyed(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    /**
     * Look at card pile request
     */
    LOOK_CARD_PILE {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            SocketServer.lookCardPile(user, castedMessage.getArg1());
        }
    },

    /**
     * Release card pile request
     */
    RELEASE_CARD_PILE {
        @Override
        public void execute(ClientHandler user, Message message) {
            SocketServer.releaseCardPile(user);
        }
    },

    /**
     * Card pile looked event
     */
    CARD_PILE_LOOKED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            TripleArgMessage<String, Integer, String> castedMessage = (TripleArgMessage<String, Integer, String>) message;
            List<ClientCard> cards = ClientCardFactory.deserializeCardList(castedMessage.getArg3());
            client.getGameController().cardPileLooked(castedMessage.getArg1(), castedMessage.getArg2(), cards);
        }
    },

    /**
     * Card pile released event
     */
    CARD_PILE_RELEASED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            client.getGameController().cardPileReleased(castedMessage.getArg1());
        }
    },

    /**
     * Move hourglass request
     */
    MOVE_HOURGLASS {
        @Override
        public void execute(ClientHandler user, Message message) {
            SocketServer.moveHourglass(user);
        }
    },

    /**
     * Hourglass moved event
     */
    HOURGLASS_MOVED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            client.getGameController().hourglassMoved();
        }
    },

    /**
     * Set ready request
     */
    SET_READY {
        @Override
        public void execute(ClientHandler user, Message message) {
            SocketServer.setReady(user);
        }
    },

    /**
     * Check ship request
     */
    CHECK_SHIP {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<List<Integer>> castedMessage = (SingleArgMessage<List<Integer>>) message;
            SocketServer.checkShip(user, castedMessage.getArg1());
        }
    },

    /**
     * Choose alien request
     */
    CHOOSE_ALIEN {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Map<Integer, AlienType>> castedMessage = (SingleArgMessage<Map<Integer, AlienType>>) message;
            SocketServer.chooseAlien(user, castedMessage.getArg1());
        }
    },

    /**
     * Choose ship part request
     */
    CHOOSE_SHIP_PART {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            SocketServer.chooseShipPart(user, castedMessage.getArg1());
        }
    },

    /**
     * Ship broken event
     */
    SHIP_BROKEN_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, List<List<Integer>>> castedMessage = (DoubleArgMessage<String, List<List<Integer>>>) message;
            client.getGameController().shipBroken(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    /**
     * Players position updated
     */
    PLAYERS_POSITION_UPDATED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<List<String>, List<SimpleEntry<String, Integer>>> castedMessage = (DoubleArgMessage<List<String>, List<SimpleEntry<String, Integer>>>) message;
            client.getGameController().playersPositionUpdated(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    /**
     * Draw card request
     */
    DRAW_CARD {
        @Override
        public void execute(ClientHandler user, Message message) {
            SocketServer.drawCard(user);
        }
    },

    /**
     * Card revealed event
     */
    CARD_REVEALED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            ClientCard card = ClientCardFactory.deserializeCard(castedMessage.getArg1());
            client.getGameController().cardRevealed(card);
        }
    },

    /**
     * Card updated event
     */
    CARD_UPDATED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            ClientCard card = ClientCardFactory.deserializeCard(castedMessage.getArg1());
            client.getGameController().cardUpdated(card);
        }
    },

    /**
     * Activate cannons request
     */
    ACTIVATE_CANNONS {
        @Override
        public void execute(ClientHandler user, Message message) {
            DoubleArgMessage<List<Integer>, List<Integer>> castedMessage = (DoubleArgMessage<List<Integer>, List<Integer>>) message;
            SocketServer.activateCannons(user, castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    /**
     * Activate engines request
     */
    ACTIVATE_ENGINES {
        @Override
        public void execute(ClientHandler user, Message message) {
            DoubleArgMessage<List<Integer>, List<Integer>> castedMessage = (DoubleArgMessage<List<Integer>, List<Integer>>) message;
            SocketServer.activateEngines(user, castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    /**
     * Activate shield request
     */
    ACTIVATE_SHIELD {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            SocketServer.activateShield(user, castedMessage.getArg1());
        }
    },

    /**
     * Batteries updated event
     */
    BATTERIES_UPDATED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<Integer, Integer> castedMessage = (DoubleArgMessage<Integer, Integer>) message;
            client.getGameController().batteriesUpdated(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    /**
     * Update goods request
     */
    UPDATE_GOODS {
        @Override
        public void execute(ClientHandler user, Message message) {
            DoubleArgMessage<Map<Integer, List<ColorType>>, List<Integer>> castedMessage = (DoubleArgMessage<Map<Integer, List<ColorType>>, List<Integer>>) message;
            SocketServer.updateGoods(user, castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    /**
     * Goods updated event
     */
    GOODS_UPDATED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<Integer, List<ColorType>> castedMessage = (DoubleArgMessage<Integer, List<ColorType>>) message;
            client.getGameController().goodsUpdated(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    /**
     * Remove crew request
     */
    REMOVE_CREW {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<List<Integer>> castedMessage = (SingleArgMessage<List<Integer>>) message;
            SocketServer.removeCrew(user, castedMessage.getArg1());
        }
    },

    /**
     * Crew updated event
     */
    CREW_UPDATED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            TripleArgMessage<Integer, Integer, AlienType> castedMessage = (TripleArgMessage<Integer, Integer, AlienType>) message;
            client.getGameController().crewUpdated(castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3());
        }
    },

    /**
     * Credits updated event
     */
    CREDITS_UPDATED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Integer> castedMessage = (DoubleArgMessage<String, Integer>) message;
            client.getGameController().creditsUpdated(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    /**
     * Roll dices request
     */
    ROLL_DICES {
        @Override
        public void execute(ClientHandler user, Message message) {
            SocketServer.rollDices(user);
        }
    },

    /**
     * Get boolean request
     */
    GET_BOOLEAN {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Boolean> castedMessage = (SingleArgMessage<Boolean>) message;
            SocketServer.getBoolean(user, castedMessage.getArg1());
        }
    },

    /**
     * Get index request
     */
    GET_INDEX {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            SocketServer.getIndex(user, castedMessage.getArg1());
        }
    },

    /**
     * End flight request
     */
    END_FLIGHT {
        @Override
        public void execute(ClientHandler user, Message message) {
            SocketServer.endFlight(user);
        }
    },

    /**
     * Flight ended event
     */
    FLIGHT_ENDED_EVENT {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            client.getGameController().flightEnded(castedMessage.getArg1());
        }
    };

    /**
     * Default server-side execution method.
     * Override in specific enum constants to provide implementation.
     *
     * @param user    the client handler
     * @param message the message to execute
     */
    public void execute(ClientHandler user, Message message) {
        // This code should not be executed
        // Otherwise, do nothing
    }

    /**
     * Default client-side execution method.
     * Override in specific enum constants to provide implementation.
     *
     * @param client  the client socket
     * @param message the message to execute
     */
    public void execute(ClientSocket client, Message message) {
        // This code should not be executed
        // Otherwise, do nothing
    }

}
