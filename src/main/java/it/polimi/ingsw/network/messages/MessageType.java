package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.network.socket.client.User;
import it.polimi.ingsw.network.socket.server.RefToUser;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public enum MessageType {

    // network
    DISCONNECT,
    DISCONNECT_OK {
        @Override
        public void execute(Message message, User user) {
            user.getSocket().close();
        }
    },

    ERROR,

    SET_USERNAME {
        @Override
        public void execute(Message message, RefToUser user) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            if (!RefToUser.isUsernameTaken(castedMessage.getArg1())) {
                boolean done = user.setUsername(castedMessage.getArg1());
                if (done)
                    user.send(new ZeroArgMessage(MessageType.USERNAME_OK), new CompletableFuture<>());
                else
                    user.send(new ZeroArgMessage(MessageType.USERNAME_ALREADY_TAKEN), new CompletableFuture<>());
            } else
                user.send(new ZeroArgMessage(MessageType.USERNAME_ALREADY_TAKEN), new CompletableFuture<>());
        }
    },
    USERNAME_OK,
    USERNAME_ALREADY_TAKEN,

    CREATE_LOBBY,
    CREATE_LOBBY_OK,
    JOIN_RANDOM_LOBBY,
    JOIN_RANDOM_LOBBY_OK,
    JOIN_LOBBY,
    JOIN_LOBBY_OK,
    LEAVE_GAME,
    LEAVE_GAME_OK,

    SHOW_COMPONENT {
        @Override
        public void execute(Message message, RefToUser client) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            client.getGameController().showComponent(client.getUsername(), castedMessage.getArg1());
            client.send(new ZeroArgMessage(SHOW_COMPONENT_RES), new CompletableFuture<>());
        }
    },

    SHOW_COMPONENT_RES,

    PICK_COMPONENT {
        @Override
        public void execute(Message message, RefToUser client) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            client.getGameController().pickComponent(client.getUsername(), castedMessage.getArg1());
            client.send(new ZeroArgMessage(PICK_COMPONENT_RES), new CompletableFuture<>());
        }
    },

    PICK_COMPONENT_RES,

    RELEASE_COMPONENT {
        @Override
        public void execute(Message message, RefToUser client) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            client.getGameController().releaseComponent(client.getUsername(), castedMessage.getArg1());
            client.send(new ZeroArgMessage(RELEASE_COMPONENT_RES), new CompletableFuture<>());
        }
    },

    RELEASE_COMPONENT_RES,

    RESERVE_COMPONENT {
        @Override
        public void execute(Message message, RefToUser client) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            client.getGameController().reserveComponent(client.getUsername(), castedMessage.getArg1());
            client.send(new ZeroArgMessage(RESERVE_COMPONENT_RES), new CompletableFuture<>());
        }
    },

    RESERVE_COMPONENT_RES,

    INSERT_COMPONENT {
        @Override
        public void execute(Message message, RefToUser client) {
            TripleArgMessage<Integer, Integer, Integer> castedMessage = (TripleArgMessage<Integer, Integer, Integer>) message;
            client.getGameController().insertComponent(client.getUsername(), castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3());
            client.send(new ZeroArgMessage(INSERT_COMPONENT_RES), new CompletableFuture<>());
        }
    },

    INSERT_COMPONENT_RES,

    MOVE_COMPONENT {
        @Override
        public void execute(Message message, RefToUser client) {
            TripleArgMessage<Integer, Integer, Integer> castedMessage = (TripleArgMessage<Integer, Integer, Integer>) message;
            client.getGameController().moveComponent(client.getUsername(), castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3());
            client.send(new ZeroArgMessage(MOVE_COMPONENT_RES), new CompletableFuture<>());
        }
    },

    MOVE_COMPONENT_RES,

    ROTATE_COMPONENT {
        @Override
        public void execute(Message message, RefToUser client) {
            DoubleArgMessage<Integer, Integer> castedMessage = (DoubleArgMessage<Integer, Integer>) message;
            client.getGameController().rotateComponent(client.getUsername(), castedMessage.getArg1(), castedMessage.getArg2());
            client.send(new ZeroArgMessage(ROTATE_COMPONENT_RES), new CompletableFuture<>());
        }
    },

    ROTATE_COMPONENT_RES,

    LOOK_CARD_PILE {
        @Override
        public void execute(Message message, RefToUser client) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            List<Card> pile = client.getGameController().lookCardPile(client.getUsername(), castedMessage.getArg1());
            client.send(new SingleArgMessage<>(LOOK_CARD_PILE_RES, pile), new CompletableFuture<>());
        }
    },

    LOOK_CARD_PILE_RES,

    MOVE_HOURGLASS {
        @Override
        public void execute(Message message, RefToUser client) {
            client.getGameController().moveHourglass(client.getUsername());
        }
    },

    MOVE_HOURGLASS_RES,

    SET_READY {
        @Override
        public void execute(Message message, RefToUser client) {
            client.getGameController().setReady(client.getUsername());
        }
    },

    SET_READY_RES,
    CHECK_SHIP,
    CHECK_SHIP_RES;

    public void execute(Message message, RefToUser user) {
        // TODO unknown command
    }

    public void execute(Message message, User user) {
        // TODO unknown command
    }

}
