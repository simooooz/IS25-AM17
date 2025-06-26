package it.polimi.ingsw.common.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.Constants;
import it.polimi.ingsw.client.model.components.*;
import it.polimi.ingsw.model.ModelFacade;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.player.Ship;

import java.util.AbstractMap.SimpleEntry;

public class GameStateDTOFactory {

    private static final ObjectMapper mapper = createStaticObjectMapper();

    private static ObjectMapper createStaticObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Class.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module");
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jdk8.Jdk8Module());
        } catch (ClassNotFoundException e) {
            // JDK8 module not available, go ahead without
        }

        return objectMapper;
    }

    public static ModelDTO createFromModel(ModelFacade serverModel) {
        ModelDTO dto = new ModelDTO();
        dto.playersState = serverModel.getPlayersState();
        dto.board = serverModel.getBoard().toDto();
        return dto;
    }

    public static PlayerPositionDTO createPlayerPositionDTO(SimpleEntry<PlayerData, Integer> entry) {
        return new PlayerPositionDTO(createPlayerDTO(entry.getKey()), entry.getValue());
    }

    public static PlayerDTO createPlayerDTO(PlayerData player) {
        return new PlayerDTO(player.getUsername(), createShipDTO(player.getShip()), player.getCredits(), player.hasEndedInAdvance(), player.isLeftGame());
    }

    public static ShipDTO createShipDTO(Ship serverShip) {
        ShipDTO dto = new ShipDTO();
        dto.discards = serverShip.getDiscards().stream().map(Component::getId).toList();
        dto.reserves = serverShip.getReserves().stream().map(Component::getId).toList();
        dto.componentInHand = serverShip.getHandComponent().map(Component::getId).orElse(null);

        Integer[][] dashboard = new Integer[Constants.SHIP_ROWS][Constants.SHIP_COLUMNS];
        for (int i = 0; i < serverShip.getDashboard().length; i++)
            for (int j = 0; j < serverShip.getDashboard()[i].length; j++)
                dashboard[i][j] = serverShip.getDashboard(i, j).map(Component::getId).orElse(null);

        dto.dashboard = dashboard;
        return dto;
    }

    public static ClientComponent componentFromDTO(ComponentDTO dto) {
        return switch (dto) {
            case BatteryComponentDTO batteryDTO -> new ClientBatteryComponent(batteryDTO);
            case CabinComponentDTO cabinDTO -> new ClientCabinComponent(cabinDTO);
            case CannonComponentDTO cannonDTO -> new ClientCannonComponent(cannonDTO);
            case CargoHoldsComponentDTO cargoDTO -> new ClientCargoHoldsComponent(cargoDTO);
            case SpecialCargoHoldsComponentDTO specialCargoDTO -> new ClientSpecialCargoHoldsComponent(specialCargoDTO);
            case EngineComponentDTO engineDTO -> new ClientEngineComponent(engineDTO);
            case OddComponentDTO oddDTO -> new ClientOddComponent(oddDTO);
            case ShieldComponentDTO shieldDTO -> new ClientShieldComponent(shieldDTO);
            default -> new ClientComponent(dto);
        };
    }

    public static String serializeDTO(ModelDTO dto) {
        try {
            return mapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static ModelDTO deserializeDTO(String jsonString) {
        try {
            return mapper.readValue(jsonString, ModelDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

} 