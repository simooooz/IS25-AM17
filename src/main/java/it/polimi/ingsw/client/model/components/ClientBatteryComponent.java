package it.polimi.ingsw.client.model.components;

import it.polimi.ingsw.common.model.enums.ConnectorType;

import java.util.ArrayList;
import java.util.List;

public class ClientBatteryComponent extends ClientComponent {

    private final boolean isTriple;
    private int batteries;

    public ClientBatteryComponent(int id, ConnectorType[] connectors, boolean isTriple) {
        super(id, connectors);
        this.isTriple = isTriple;
        this.batteries = isTriple ? 3 : 2;
    }

    public void setBatteries(int batteries) {
        this.batteries = batteries;
    }

    @Override
    public List<String> icon() {
        String battery = "\uD83D\uDD0B";
        return new ArrayList<>(List.of(
                "    " + battery + "   \t ",
                "    " + batteries + "/" + (isTriple ? 3 : 2) + "    "));
    }

}