module it.polimi.ingsw {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires jdk.xml.dom;

    exports it.polimi.ingsw.model;
    exports it.polimi.ingsw.model.cards;
    exports it.polimi.ingsw.model.cards.utils;
    exports it.polimi.ingsw.model.game;
    exports it.polimi.ingsw.model.player;
    exports it.polimi.ingsw.model.components;
    exports it.polimi.ingsw.controller;

    exports it.polimi.ingsw.network.exceptions;

    exports it.polimi.ingsw.common.model.enums;
    exports it.polimi.ingsw.common.model.events;
    exports it.polimi.ingsw.common.model;

    exports it.polimi.ingsw.client.model;
    exports it.polimi.ingsw.client.model.cards;
    exports it.polimi.ingsw.client.model.cards.utils;
    exports it.polimi.ingsw.client.model.game;
    exports it.polimi.ingsw.client.model.player;
    exports it.polimi.ingsw.client.model.components;
    exports it.polimi.ingsw.client.controller;

    requires com.fasterxml.jackson.datatype.jdk8;
    requires com.fasterxml.jackson.databind;

    opens it.polimi.ingsw.model.cards to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.model.cards.utils to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.client.model.cards to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.client.model.cards.utils to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.common.model.enums to com.fasterxml.jackson.databind;

    exports it.polimi.ingsw.network.rmi to java.rmi;

    exports it.polimi.ingsw.view.GUI to javafx.graphics, javafx.fxml;
    opens it.polimi.ingsw.view.GUI to javafx.fxml;
}