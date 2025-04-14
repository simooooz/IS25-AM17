package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.network.socket.client.UserOfClient;
import it.polimi.ingsw.network.socket.server.User;

import java.util.List;

@SuppressWarnings("unchecked")
public enum MessageType {

    // network
    ERROR,

    SET_USERNAME {
        @Override
        public void execute(Message message, User user) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            if (!User.isUsernameTaken(castedMessage.getArg1())) {
                boolean done = user.setUsername(castedMessage.getArg1());
                if (done)
                    user.send(new ZeroArgMessage(MessageType.USERNAME_OK));
                else
                    user.send(new ZeroArgMessage(MessageType.USERNAME_ALREADY_TAKEN));
            }
            else
                user.send(new ZeroArgMessage(MessageType.USERNAME_ALREADY_TAKEN));
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
        public void execute(Message message, User client) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            client.getGameController().showComponent(client.getUsername(), castedMessage.getArg1());
            client.send(new ZeroArgMessage(SHOW_COMPONENT_RES));
        }
    },

    SHOW_COMPONENT_RES,

    PICK_COMPONENT {
        @Override
        public void execute(Message message, User client) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            client.getGameController().pickComponent(client.getUsername(), castedMessage.getArg1());
            client.send(new ZeroArgMessage(PICK_COMPONENT_RES));
        }
    },

    PICK_COMPONENT_RES,

    RELEASE_COMPONENT {
        @Override
        public void execute(Message message, User client) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            client.getGameController().releaseComponent(client.getUsername(), castedMessage.getArg1());
            client.send(new ZeroArgMessage(RELEASE_COMPONENT_RES));
        }
    },

    RELEASE_COMPONENT_RES,

    RESERVE_COMPONENT {
        @Override
        public void execute(Message message, User client) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            client.getGameController().reserveComponent(client.getUsername(), castedMessage.getArg1());
            client.send(new ZeroArgMessage(RESERVE_COMPONENT_RES));
        }
    },

    RESERVE_COMPONENT_RES,

    INSERT_COMPONENT {
        @Override
        public void execute(Message message, User client) {
            TripleArgMessage<Integer, Integer, Integer> castedMessage = (TripleArgMessage<Integer, Integer, Integer>) message;
            client.getGameController().insertComponent(client.getUsername(), castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3());
            client.send(new ZeroArgMessage(INSERT_COMPONENT_RES));
        }
    },

    INSERT_COMPONENT_RES,

    MOVE_COMPONENT {
        @Override
        public void execute(Message message, User client) {
            TripleArgMessage<Integer, Integer, Integer> castedMessage = (TripleArgMessage<Integer, Integer, Integer>) message;
            client.getGameController().moveComponent(client.getUsername(), castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3());
            client.send(new ZeroArgMessage(MOVE_COMPONENT_RES));
        }
    },

    MOVE_COMPONENT_RES,

    ROTATE_COMPONENT {
        @Override
        public void execute(Message message, User client) {
            DoubleArgMessage<Integer, Integer> castedMessage = (DoubleArgMessage<Integer, Integer>) message;
            client.getGameController().rotateComponent(client.getUsername(), castedMessage.getArg1(), castedMessage.getArg2());
            client.send(new ZeroArgMessage(ROTATE_COMPONENT_RES));
        }
    },

    ROTATE_COMPONENT_RES,

    LOOK_CARD_PILE {
        @Override
        public void execute(Message message, User client) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            List<Card> pile = client.getGameController().lookCardPile(client.getUsername(), castedMessage.getArg1());
            client.send(new SingleArgMessage<>(LOOK_CARD_PILE_RES, pile));
        }
    },

    LOOK_CARD_PILE_RES,

    MOVE_HOURGLASS {
        @Override
        public void execute(Message message, User client) {
            client.getGameController().moveHourglass(client.getUsername());
        }
    },

    MOVE_HOURGLASS_RES,

    SET_READY {
        @Override
        public void execute(Message message, User client) {
            client.getGameController().setReady(client.getUsername());
        }
    },

    SET_READY_RES,

    CHECK_SHIP {
        @Override
        public void execute(Message message, UserOfClient client) {
            SingleArgMessage<List<Integer>> castedMessage = (SingleArgMessage<List<Integer>>) message;
            client.getGameController().checkShip(client.getUsername(), castedMessage.getArg1());
        }
    },

    CHECK_SHIP_RES;

    public void execute(Message message, User user) {
        // TODO unknown command
    }

    public void execute(Message message, UserOfClient user) {
        // TODO unknown command
    }

}
