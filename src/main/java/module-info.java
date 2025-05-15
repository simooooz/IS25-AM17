module it.polimi.ingsw {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.json;
    requires java.rmi;

    exports it.polimi.ingsw.network.rmi to java.rmi;
    opens it.polimi.ingsw to javafx.fxml;

    exports it.polimi.ingsw;
}
