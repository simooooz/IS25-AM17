package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;

public class Component {

    private final ConnectorType[] connectors;
    private int x;
    private int y;

    public Component(ConnectorType[] connectors) {
        this.connectors = connectors;
    }

    public ConnectorType[] getConnectors() {
        return connectors;
    }

    public boolean isNearTo(Component c){
        int rowDiff = this.x-c.x;
        int colDiff = this.y-c.y;
        if((rowDiff==0) && (colDiff==1)||(rowDiff==1)&&(colDiff==0)){
            return true;
        }
        else {
            return false;
        }
    }

}
