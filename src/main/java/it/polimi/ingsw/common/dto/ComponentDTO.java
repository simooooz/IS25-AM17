package it.polimi.ingsw.common.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.polimi.ingsw.common.model.enums.ConnectorType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "componentType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BatteryComponentDTO.class, name = "BATTERY"),
        @JsonSubTypes.Type(value = CabinComponentDTO.class, name = "CABIN"),
        @JsonSubTypes.Type(value = CannonComponentDTO.class, name = "CANNON"),
        @JsonSubTypes.Type(value = CargoHoldsComponentDTO.class, name = "CARGO_HOLDS"),
        @JsonSubTypes.Type(value = EngineComponentDTO.class, name = "ENGINE"),
        @JsonSubTypes.Type(value = OddComponentDTO.class, name = "ODD"),
        @JsonSubTypes.Type(value = ShieldComponentDTO.class, name = "SHIELD")
})
public class ComponentDTO {

    public int id;
    public ConnectorType[] connectors;
    public int x;
    public int y;
    public boolean inserted;
    public boolean shown;
    public int rotationCounter;

    public ComponentDTO(int id, ConnectorType[] connectors, int x, int y, boolean inserted, boolean shown, int rotationCounter) {
        this.id = id;
        this.connectors = connectors;
        this.x = x;
        this.y = y;
        this.inserted = inserted;
        this.shown = shown;
        this.rotationCounter = rotationCounter;
    }

    public ComponentDTO() {}

}