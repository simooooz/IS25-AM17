package it.polimi.ingsw.network.messages;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.game.Lobby;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.network.UserState;
import it.polimi.ingsw.network.socket.client.ClientSocket;
import it.polimi.ingsw.network.socket.server.ClientHandler;
import it.polimi.ingsw.network.socket.server.Server;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.List;
import java.util.Map;


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
        }
    },
    USERNAME_ALREADY_TAKEN {
        @Override
        public void execute(ClientSocket client, Message message) {
            Chroma.println("Username already taken", Chroma.RED);
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
        }
    },

    GAME_STARTED_OK {
        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<Lobby> castedMessage = (SingleArgMessage<Lobby>) message;
            client.setLobby(castedMessage.getArg1());
            client.setState(UserState.IN_GAME);
            client.setGameController(new GameController(castedMessage.getArg1().getPlayers(), castedMessage.getArg1().isLearnerMode()));
            client.getGameController().startMatch();
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
            if (castedMessage.getArg1().hasPlayer(client.getUsername()))
                client.setLobby(castedMessage.getArg1());
            else {
                client.setLobby(null);
                client.setGameController(null);
                client.setState(UserState.LOBBY_SELECTION);
            }
        }
    },

    PICK_COMPONENT {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            Server.pickComponent(user, castedMessage.getArg1());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Integer> castedMessage = (DoubleArgMessage<String, Integer>) message;
            client.getGameController().pickComponent(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    RELEASE_COMPONENT {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            Server.releaseComponent(user, castedMessage.getArg1());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Integer> castedMessage = (DoubleArgMessage<String, Integer>) message;
            client.getGameController().releaseComponent(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },


    RESERVE_COMPONENT {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            Server.reserveComponent(user, castedMessage.getArg1());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Integer> castedMessage = (DoubleArgMessage<String, Integer>) message;
            client.getGameController().reserveComponent(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    INSERT_COMPONENT {
        @Override
        public void execute(ClientHandler user, Message message) {
            QuadrupleArgMessage<Integer, Integer, Integer, Integer> castedMessage = (QuadrupleArgMessage<Integer, Integer, Integer, Integer>) message;
            Server.insertComponent(user, castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3(), castedMessage.getArg4());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            QuintupleArgMessage<String, Integer, Integer, Integer, Integer> castedMessage = (QuintupleArgMessage<String, Integer, Integer, Integer, Integer>) message;
            client.getGameController().insertComponent(castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3(), castedMessage.getArg4(), castedMessage.getArg5(), true);
        }
    },

    MOVE_COMPONENT {
        @Override
        public void execute(ClientHandler user, Message message) {
            QuadrupleArgMessage<Integer, Integer, Integer, Integer> castedMessage = (QuadrupleArgMessage<Integer, Integer, Integer, Integer>) message;
            Server.moveComponent(user, castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3(), castedMessage.getArg4());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            QuintupleArgMessage<String, Integer, Integer, Integer, Integer> castedMessage = (QuintupleArgMessage<String, Integer, Integer, Integer, Integer>) message;
            client.getGameController().moveComponent(castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3(), castedMessage.getArg4(), castedMessage.getArg5());
        }
    },

    ROTATE_COMPONENT {
        @Override
        public void execute(ClientHandler user, Message message) {
            DoubleArgMessage<Integer, Integer> castedMessage = (DoubleArgMessage<Integer, Integer>) message;
            Server.rotateComponent(user, castedMessage.getArg1(), castedMessage.getArg2());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            TripleArgMessage<String, Integer, Integer> castedMessage = (TripleArgMessage<String, Integer, Integer>) message;
            client.getGameController().rotateComponent(castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3());
        }
    },

    LOOK_CARD_PILE {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            Server.lookCardPile(user, castedMessage.getArg1());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Integer> castedMessage = (DoubleArgMessage<String, Integer>) message;
            client.getGameController().lookCardPile(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    MOVE_HOURGLASS {
        @Override
        public void execute(ClientHandler user, Message message) {
            Server.moveHourglass(user);
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            client.getGameController().moveHourglass(castedMessage.getArg1());
        }
    },

    SET_READY {
        @Override
        public void execute(ClientHandler user, Message message) {
            Server.setReady(user);
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            client.getGameController().setReady(castedMessage.getArg1());
        }
    },

    CHECK_SHIP {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<List<Integer>> castedMessage = (SingleArgMessage<List<Integer>>) message;
            Server.checkShip(user, castedMessage.getArg1());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, List<Integer>> castedMessage = (DoubleArgMessage<String, List<Integer>>) message;
            client.getGameController().checkShip(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    CHOOSE_ALIEN {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Map<Integer, AlienType>> castedMessage = (SingleArgMessage<Map<Integer, AlienType>>) message;
            Server.chooseAlien(user, castedMessage.getArg1());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Map<Integer, AlienType>> castedMessage = (DoubleArgMessage<String, Map<Integer, AlienType>>) message;
            client.getGameController().chooseAlien(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    CHOOSE_SHIP_PART {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            Server.chooseShipPart(user, castedMessage.getArg1());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Integer> castedMessage = (DoubleArgMessage<String, Integer>) message;
            client.getGameController().chooseShipPart(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    DRAW_CARD {
        @Override
        public void execute(ClientHandler user, Message message) {
            Server.drawCard(user);
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            client.getGameController().drawCard(castedMessage.getArg1());
        }
    },

    ACTIVATE_CANNONS {
        @Override
        public void execute(ClientHandler user, Message message) {
            DoubleArgMessage<List<Integer>, List<Integer>> castedMessage = (DoubleArgMessage<List<Integer>, List<Integer>>) message;
            Server.activateCannons(user, castedMessage.getArg1(), castedMessage.getArg2());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            TripleArgMessage<String, List<Integer>, List<Integer>> castedMessage = (TripleArgMessage<String, List<Integer>, List<Integer>>) message;
            client.getGameController().activateCannons(castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3());
        }
    },

    ACTIVATE_ENGINES {
        @Override
        public void execute(ClientHandler user, Message message) {
            DoubleArgMessage<List<Integer>, List<Integer>> castedMessage = (DoubleArgMessage<List<Integer>, List<Integer>>) message;
            Server.activateEngines(user, castedMessage.getArg1(), castedMessage.getArg2());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            TripleArgMessage<String, List<Integer>, List<Integer>> castedMessage = (TripleArgMessage<String, List<Integer>, List<Integer>>) message;
            client.getGameController().activateEngines(castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3());
        }
    },

    ACTIVATE_SHIELD {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            Server.activateShield(user, castedMessage.getArg1());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Integer> castedMessage = (DoubleArgMessage<String, Integer>) message;
            client.getGameController().activateShield(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    UPDATE_GOODS {
        @Override
        public void execute(ClientHandler user, Message message) {
            DoubleArgMessage<Map<Integer, List<ColorType>>, List<Integer>> castedMessage = (DoubleArgMessage<Map<Integer, List<ColorType>>, List<Integer>>) message;
            Server.updateGoods(user, castedMessage.getArg1(), castedMessage.getArg2());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            TripleArgMessage<String, Map<Integer, List<ColorType>>, List<Integer>> castedMessage = (TripleArgMessage<String, Map<Integer, List<ColorType>>, List<Integer>>) message;
            client.getGameController().updateGoods(castedMessage.getArg1(), castedMessage.getArg2(), castedMessage.getArg3());
        }
    },

    REMOVE_CREW {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<List<Integer>> castedMessage = (SingleArgMessage<List<Integer>>) message;
            Server.removeCrew(user, castedMessage.getArg1());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, List<Integer>> castedMessage = (DoubleArgMessage<String, List<Integer>>) message;
            client.getGameController().removeCrew(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    ROLL_DICES {
        @Override
        public void execute(ClientHandler user, Message message) {
            Server.rollDices(user);
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            client.getGameController().rollDices(castedMessage.getArg1());
        }
    },

    GET_BOOLEAN {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Boolean> castedMessage = (SingleArgMessage<Boolean>) message;
            Server.getBoolean(user, castedMessage.getArg1());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Boolean> castedMessage = (DoubleArgMessage<String, Boolean>) message;
            client.getGameController().getBoolean(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    GET_INDEX {
        @Override
        public void execute(ClientHandler user, Message message) {
            SingleArgMessage<Integer> castedMessage = (SingleArgMessage<Integer>) message;
            Server.getIndex(user, castedMessage.getArg1());
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            DoubleArgMessage<String, Integer> castedMessage = (DoubleArgMessage<String, Integer>) message;
            client.getGameController().getIndex(castedMessage.getArg1(), castedMessage.getArg2());
        }
    },

    END_FLIGHT {
        @Override
        public void execute(ClientHandler user, Message message) {
            Server.endFlight(user);
        }

        @Override
        public void execute(ClientSocket client, Message message) {
            SingleArgMessage<String> castedMessage = (SingleArgMessage<String>) message;
            client.getGameController().endFlight(castedMessage.getArg1());
        }
    };

    public void execute(ClientHandler user, Message message) {
        // This code should not be executed
        // Otherwise, do nothing
    }

    public void execute(ClientSocket client, Message message) {
        // This code should not be executed
        // Otherwise, do nothing
    }

}
