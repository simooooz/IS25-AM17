module it.polimi.ingsw {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.json;


    opens it.polimi.ingsw to javafx.fxml;
    exports it.polimi.ingsw;
}
