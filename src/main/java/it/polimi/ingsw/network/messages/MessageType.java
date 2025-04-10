package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.network.socket.client.UserOfClient;
import it.polimi.ingsw.network.socket.server.User;

@SuppressWarnings("unchecked")
public enum MessageType {

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
          }
    },

    SHOW_COMPONENT_RES,

    PICK_COMPONENT {
        @Override
        public void execute(Message message, User client) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            client.getGameController().pickComponent(client.getUsername(), castedMessage.getArg1());
        }
    },

    PICK_COMPONENT_RES,

    RELEASE_COMPONENT {
        @Override
        public void execute(Message message, User client) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            client.getGameController().releaseComponent(client.getUsername(), castedMessage.getArg1());
        }
    },

    RELEASE_COMPONENT_RES,

    RESERVE_COMPONENT {
        @Override
        public void execute(Message message, User client) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            client.getGameController().reserveComponent(client.getUsername(), castedMessage.getArg1());
        }
    },

    RESERVE_COMPONENT_RES,

    INSERT_COMPONENT {
        @Override
        public void execute(Message message, User client) {
            TripleArgMessage<Integer, Integer, Integer> castedMessage = (TripleArgMessage<Integer, Integer, Integer>) message;
            client.getGameController().insertComponent(client.getUsername(), castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3());
        }
    },

    INSERT_COMPONENT_RES,

    MOVE_COMPONENT {
        @Override
        public void execute(Message message, User client) {
            TripleArgMessage<Integer, Integer, Integer> castedMessage = (TripleArgMessage<Integer, Integer, Integer>) message;
            client.getGameController().moveComponent(client.getUsername(), castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3());
        }
    },

    MOVE_COMPONENT_RES,

    ROTATE_COMPONENT {
        @Override
        public void execute(Message message, User client) {
            DoubleArgMessage<Integer, Integer> castedMessage = (DoubleArgMessage<Integer, Integer>) message;
            client.getGameController().rotateComponent(client.getUsername(), castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    ROTATE_COMPONENT_RES,

    LOOK_CARD_PILE {
        @Override
        public void execute(Message message, User client) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            client.getGameController().lookCardPile(client.getUsername(), castedMessage.getArg1());
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
    CHECK_SHIP,
    CHECK_SHIP_RES;

    public void execute(Message message, User user) {
        // TODO unkwnown command
    }

    public void execute(Message message, UserOfClient user) {
        // TODO unkwnown command
    }

}
