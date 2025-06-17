module it.polimi.ingsw {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.json;
    requires java.rmi;
    requires jdk.xml.dom;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    exports it.polimi.ingsw.model.cards;
    exports it.polimi.ingsw.model.cards.utils;
    exports it.polimi.ingsw.client.model.cards;
    exports it.polimi.ingsw.client.model.cards.utils;
    exports it.polimi.ingsw.common.model.enums;

    opens it.polimi.ingsw.model.cards to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.model.cards.utils to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.client.model.cards to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.client.model.cards.utils to com.fasterxml.jackson.databind;
    opens it.polimi.ingsw.common.model.enums to com.fasterxml.jackson.databind;

    exports it.polimi.ingsw.network.rmi to java.rmi;

    exports it.polimi.ingsw.view.GUI to javafx.graphics, javafx.fxml;
    opens it.polimi.ingsw.view.GUI to javafx.fxml;
}