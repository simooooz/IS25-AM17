package it.polimi.ingsw.client.model.components;

import it.polimi.ingsw.common.dto.CargoHoldsComponentDTO;
import it.polimi.ingsw.common.model.enums.ConnectorType;

public final class ClientCargoHoldsComponent extends ClientSpecialCargoHoldsComponent {

    public ClientCargoHoldsComponent(int id, ConnectorType[] connectors, int number) {
        super(id, connectors, number);
    }

    public ClientCargoHoldsComponent(CargoHoldsComponentDTO dto) {
        super(dto);
    }

}