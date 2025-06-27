package it.polimi.ingsw.client.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.polimi.ingsw.Constants;

import java.util.List;

public class ClientCannonFirePenaltyCombatZone extends ClientPenaltyCombatZone {

    @JsonProperty private List<ClientCannonFire> cannonFires;
    @JsonProperty private List<Integer> coords;
    @JsonProperty private int cannonIndex;

    public ClientCannonFirePenaltyCombatZone() {}

    @Override
    public String toString() {
        StringBuilder fires = new StringBuilder();
        for (int i = 0; i <= cannonFires.size(); i++) {
            if (i == 0)
                fires.append("\t   │\n");
            else if (i == cannonFires.size())
                fires.append("│").append(Constants.inTheMiddle(i-1<coords.size()? String.valueOf(coords.get(i-1)): "       ", 7)).append(cannonFires.get(i - 1).toString());
            else {
                fires.append("│").append(Constants.inTheMiddle(i-1<coords.size()? String.valueOf(coords.get(i-1)): "       ", 7)).append(cannonFires.get(i - 1).toString()).append("\t   │\n");
            }
        }

        return fires.toString();
    }

}
