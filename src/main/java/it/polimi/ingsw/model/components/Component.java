package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;

public class Component {

    private final ConnectorType[] connectors;

    public Component(ConnectorType[] connectors) {
        this.connectors = connectors;
    }

    public ConnectorType[] getConnectors() {
        return connectors;
    }

}
