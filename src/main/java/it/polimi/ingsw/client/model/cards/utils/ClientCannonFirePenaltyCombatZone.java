package it.polimi.ingsw.client.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ClientCannonFirePenaltyCombatZone extends ClientPenaltyCombatZone {

    @JsonProperty private List<ClientCannonFire> cannonFires;
    // TODO mi servono??
    @JsonProperty private int coord;
    @JsonProperty private int cannonIndex;

    public ClientCannonFirePenaltyCombatZone() {}

    @Override
    public String toString() {
        String fires = "";
        for (int i = 0; i <= cannonFires.size(); i++) {
            if (i == 0)
                fires = fires + "\t   │\n";
            else if (i == cannonFires.size())
                fires = fires + "│       " + cannonFires.get(i-1).toString();
            else {
                fires = fires + "│       " + cannonFires.get(i-1).toString() + "\t   │\n";
            }
        }

        return fires;
    }

}
