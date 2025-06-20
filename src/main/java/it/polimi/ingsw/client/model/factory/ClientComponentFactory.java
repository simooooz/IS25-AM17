package it.polimi.ingsw.client.model.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import it.polimi.ingsw.client.model.components.*;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.DirectionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientComponentFactory {

    private final List<ClientComponent> components;
    private final Map<Integer, ClientComponent> componentsMap;
    private final Map<ColorType, ClientComponent> startingCabins;

    public ClientComponentFactory() {
        this.components = new ArrayList<>();
        this.componentsMap = new HashMap<>();
        this.startingCabins = new HashMap<>();

        JsonNode componentsJson = loadJsonConfig();
        JsonNode componentsArray = componentsJson.get("components");
        JsonNode startingCabinsArray = componentsJson.get("startingCabins");

        List<ColorType> colors = List.of(ColorType.values());
        for (int i = 0; i < startingCabinsArray.size(); i++) {
            JsonNode componentJson = startingCabinsArray.get(i);
            ClientComponent component = createComponent(componentJson);
            startingCabins.put(colors.get(i), component);
            componentsMap.put(componentJson.get("id").asInt(), component);
        }

        for (int i = 0; i < componentsArray.size(); i++) {
            JsonNode componentJson = componentsArray.get(i);
            ClientComponent component = createComponent(componentJson);
            components.add(component);
            componentsMap.put(componentJson.get("id").asInt(), component);
        }
    }

    public List<ClientComponent> getComponents(){
        return components;
    }

    public Map<ColorType, ClientComponent> getStartingCabins() {
        return startingCabins;
    }

    private JsonNode loadJsonConfig() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        try {
            return objectMapper.readTree(new File("src/main/resources/factory.json"));
        } catch (IOException e) {
            throw new RuntimeException("Unable to load config file");
        }
    }

    public Map<Integer, ClientComponent> getComponentsMap() {
        return new HashMap<>(componentsMap);
    }

    @SuppressWarnings("Duplicates")
    private ClientComponent createComponent(JsonNode componentJson) {
        int id = componentJson.get("id").asInt();
        String type = componentJson.get("type").asText();
        JsonNode connectorsArray = componentJson.get("connectors");
        ConnectorType[] connectors = new ConnectorType[connectorsArray.size()];
        for (int i = 0; i < connectorsArray.size(); i++) {
            connectors[i] = (ConnectorType.valueOf(connectorsArray.get(i).asText()));
        }

        switch(type) {
            case "BatteryComponent":
                boolean isTriple = componentJson.get("isTriple").booleanValue();;
                return new ClientBatteryComponent(id, connectors, isTriple);

            case "CargoHoldsComponent", "SpecialCargoHoldsComponent":
                int numberCargo = componentJson.get("number").asInt();
                return new ClientCargoHoldsComponent(id, connectors, numberCargo);

            case "CabinComponent":
                boolean isStartingCabin = componentJson.get("isStarting").booleanValue();;
                return new ClientCabinComponent(id, connectors, isStartingCabin);

            case "Component":
                return new ClientComponent(id, connectors);

            case "EngineComponent":
                DirectionType engineDirection = DirectionType.valueOf(componentJson.get("direction").asText());
                boolean engineIsDouble = componentJson.get("isDouble").booleanValue();;
                return new ClientEngineComponent(id, connectors, engineDirection, engineIsDouble);

            case "CannonComponent":
                DirectionType cannonDirection = DirectionType.valueOf(componentJson.get("direction").asText());
                boolean cannonIsDouble = componentJson.get("isDouble").booleanValue();
                return new ClientCannonComponent(id, connectors, cannonDirection, cannonIsDouble);

            case "ShieldComponent":
                JsonNode directionArray = componentJson.get("directionsProtected");
                DirectionType[] direction = new DirectionType[directionArray.size()];
                for (int i = 0; i < directionArray.size(); i++) {
                    direction[i] = (DirectionType.valueOf(directionArray.get(i).asText()));
                }
                return new ClientShieldComponent(id, connectors, direction);

            case "OddComponent":
                AlienType alienType = AlienType.valueOf(componentJson.get("typeAlien").asText());
                return new ClientOddComponent(id, connectors, alienType);

            default:
                throw new IllegalArgumentException("Unknown component type: " + type);
        }
    }

}
