package it.polimi.ingsw.model.factory;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.enums.DirectionType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ComponentFactory {

    private final List<Component> components;
    private final Map<Integer, Component> componentsMap;
    private final Map<ColorType, Component> startingCabins;

    public ComponentFactory() {
        this.components = new ArrayList<>();
        this.componentsMap = new HashMap<>();
        this.startingCabins = new HashMap<>();

        JsonNode componentsJson = loadJsonConfig();
        JsonNode componentsArray = componentsJson.get("components");
        JsonNode startingCabinsArray = componentsJson.get("startingCabins");

        List<ColorType> colors = List.of(ColorType.values());
        for (int i = 0; i < startingCabinsArray.size(); i++) {
            JsonNode componentJson = startingCabinsArray.get(i);
            Component component = createComponent(componentJson);
            startingCabins.put(colors.get(i), component);
            componentsMap.put(componentJson.get("id").asInt(), component);
        }

        for (int i = 0; i < componentsArray.size(); i++) {
            JsonNode componentJson = componentsArray.get(i);
            Component component = createComponent(componentJson);
            components.add(component);
            componentsMap.put(componentJson.get("id").asInt(), component);
        }
    }

    public List<Component> getComponents(){
        return components;
    }

    public Map<ColorType, Component> getStartingCabins() {
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

    public Map<Integer, Component> getComponentsMap() {
        return new HashMap<>(componentsMap);
    }

    @SuppressWarnings("Duplicates")
    private Component createComponent(JsonNode componentJson) {
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
                return new BatteryComponent(id, connectors, isTriple);

            case "CargoHoldsComponent":
                int numberCargo = componentJson.get("number").asInt();
                return new CargoHoldsComponent(id, connectors, numberCargo);

            case "CabinComponent":
                boolean isStartingCabin = componentJson.get("isStarting").booleanValue();;
                return new CabinComponent(id, connectors, isStartingCabin);

            case "Component":
                return new Component(id, connectors);

            case "SpecialCargoHoldsComponent":
                int numberSpecialCargo = componentJson.get("number").asInt();
                return new SpecialCargoHoldsComponent(id, connectors, numberSpecialCargo);

            case "EngineComponent":
                DirectionType engineDirection = DirectionType.valueOf(componentJson.get("direction").asText());
                boolean engineIsDouble = componentJson.get("isDouble").booleanValue();;
                return new EngineComponent(id, connectors, engineDirection, engineIsDouble);

            case "CannonComponent":
                DirectionType cannonDirection = DirectionType.valueOf(componentJson.get("direction").asText());
                boolean cannonIsDouble = componentJson.get("isDouble").booleanValue();
                return new CannonComponent(id, connectors, cannonDirection, cannonIsDouble);

            case "ShieldComponent":
                JsonNode directionArray = componentJson.get("directionsProtected");
                DirectionType[] direction = new DirectionType[directionArray.size()];
                for (int i = 0; i < directionArray.size(); i++) {
                    direction[i] = (DirectionType.valueOf(directionArray.get(i).asText()));
                }
                return new ShieldComponent(id, connectors, direction);

            case "OddComponent":
                AlienType alienType = AlienType.valueOf(componentJson.get("typeAlien").asText());
                return new OddComponent(id, connectors, alienType);

            default:
                throw new IllegalArgumentException("Unknown component type: " + type);
        }
    }
}
