package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.network.UserState;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.network.socket.server.ClientHandler;
import it.polimi.ingsw.network.socket.server.Server;
import it.polimi.ingsw.view.TUI.Chroma;


@SuppressWarnings("unchecked")
public enum MessageType {

    ERROR,

    SET_USERNAME {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            boolean success = Server.setUsername(user, castedMessage.getArg1());
            if (success)
                user.send(new SingleArgMessage<>(MessageType.USERNAME_OK, castedMessage.getArg1()));
            else
                user.send(new ZeroArgMessage(MessageType.USERNAME_ALREADY_TAKEN));
        }
    },
    USERNAME_OK {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            client.setUsername(castedMessage.getArg1());
            client.getViewTui().handleUIState();
        }
    },
    USERNAME_ALREADY_TAKEN {
        @Override
        public void execute(ClientSocket client, Message message) {
            Chroma.println("username already taken", Chroma.RED);
            client.getViewTui().handleUIState();
        }
    },

    CREATE_LOBBY {
        @Override
        public void execute(ClientHandler user, Message message) {
            TripleArgMessage<String, Integer, Boolean> castedMessage = (TripleArgMessage<String, Integer, Boolean>) message;
            Server.createLobby(user, castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3());
        }
    },
    CREATE_LOBBY_OK {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<Lobby> castedMessage = (SingleArgMessage<Lobby>) message;
            client.setLobby(castedMessage.getArg1());
            client.setState(UserState.IN_LOBBY);
            client.getViewTui().handleUIState();
        }
    },

    JOIN_RANDOM_LOBBY {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Boolean> castedMessage = (SingleArgMessage<Boolean>) message;
            Server.joinRandomLobby(user, castedMessage.getArg1());
        }
    },
    JOIN_RANDOM_LOBBY_OK {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<Lobby> castedMessage = (SingleArgMessage<Lobby>) message;
            client.setLobby(castedMessage.getArg1());
            client.setState(UserState.IN_LOBBY);
            client.getViewTui().handleUIState();
        }
    },

    JOIN_LOBBY {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            Server.joinLobby(user, castedMessage.getArg1());
        }
    },
    JOIN_LOBBY_OK {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<Lobby> castedMessage = (SingleArgMessage<Lobby>) message;
            client.setLobby(castedMessage.getArg1());
            client.setState(UserState.IN_LOBBY);
            client.getViewTui().handleUIState();
        }
    },

    GAME_STARTED_OK {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<Lobby> castedMessage = (SingleArgMessage<Lobby>) message;
            client.setLobby(castedMessage.getArg1());
            client.setState(UserState.IN_GAME);
            client.setGameController(new GameController(castedMessage.getArg1().getPlayers(), castedMessage.getArg1().isLearnerMode()));
            client.getViewTui().handleUIState();
        }
    },

    LEAVE_GAME {
        @Override
        public void execute(ClientHandler user, Message message) {
            Server.leaveGame(user);
        }
    },
    LEAVE_GAME_OK {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<Lobby> castedMessage = (SingleArgMessage<Lobby>) message;
            client.setLobby(castedMessage.getArg1());
            client.setState(UserState.LOBBY_SELECTION);
            client.setGameController(null);
            client.getViewTui().handleUIState();
        }
    },

    SHOW_COMPONENT,
    SHOW_COMPONENT_RES,

    PICK_COMPONENT {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            Server.pickComponent(user, castedMessage.getArg1());
            user.notifyGameEvent(this, castedMessage.getArg1());
        }
    },
    PICK_COMPONENT_RES {
        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Integer> castedMessage = (DoubleArgMessage<String, Integer>) message;
            client.getGameController().pickComponent(castedMessage.getArg1(), castedMessage.getArg2());
            client.getViewTui().handleUIState();
        }
    },

    RELEASE_COMPONENT {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            Server.releaseComponent(user, castedMessage.getArg1());
            user.notifyGameEvent(this, castedMessage.getArg1());
        }
    },
    RELEASE_COMPONENT_RES {
        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Integer> castedMessage = (DoubleArgMessage<String, Integer>) message;
            client.getGameController().pickComponent(castedMessage.getArg1(), castedMessage.getArg2());
            client.getViewTui().handleUIState();
        }
    },

    RESERVE_COMPONENT,
    RESERVE_COMPONENT_RES,

    INSERT_COMPONENT,
    INSERT_COMPONENT_RES,

    MOVE_COMPONENT,
    MOVE_COMPONENT_RES,

    ROTATE_COMPONENT,
    ROTATE_COMPONENT_RES,

    LOOK_CARD_PILE,
    LOOK_CARD_PILE_RES,

    MOVE_HOURGLASS,
    MOVE_HOURGLASS_RES,

    SET_READY,
    SET_READY_RES,

    CHECK_SHIP,
    CHECK_SHIP_RES;

    public void execute(ClientHandler user, Message message) {
        // TODO unknown command
    }

    public void execute(ClientSocket client, Message message) {
        // TODO unknown command
    }

}
