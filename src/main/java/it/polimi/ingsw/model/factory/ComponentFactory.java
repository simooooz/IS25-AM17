package it.polimi.ingsw.model.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.common.model.enums.ConnectorType;
import it.polimi.ingsw.common.model.enums.AlienType;
import it.polimi.ingsw.common.model.enums.ColorType;
import it.polimi.ingsw.common.model.enums.DirectionType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;

/**
 * Factory class responsible for creating and managing ship component instances from JSON configuration.
 * This factory implements the Factory Method pattern to provide a flexible system for component creation,
 * supporting all component types used in ship construction and customization.
 * <p>
 */

public class ComponentFactory {

    /**
     * List of regular components available for acquisition during gameplay
     */
    private final List<Component> components;

    /**
     * Map providing ID-based lookup for all components (regular and starting cabins)
     */
    private final Map<Integer, Component> componentsMap;

    /**
     * Map of starting cabin components organized by color for player ship initialization
     */
    private final Map<ColorType, Component> startingCabins;

    /**
     * Constructs a new ComponentFactory and initializes all components from JSON configuration.
     * <p>
     */
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

    /**
     * Retrieves the list of regular components available for acquisition during gameplay.
     * <p>
     * @return the list of regular components available for gameplay
     */
    public List<Component> getComponents() {
        return components;
    }

    /**
     * Retrieves the map of starting cabin components organized by color type.
     * <p>
     * @return the map of starting cabins keyed by color type
     */
    public Map<ColorType, Component> getStartingCabins() {
        return startingCabins;
    }

    /**
     * Loads and parses the JSON configuration file containing component definitions.
     * <p>
     * @return the root JsonNode containing all component configuration data
     * @throws RuntimeException if the configuration file cannot be found or parsed
     */
    protected JsonNode loadJsonConfig() {
        ObjectMapper objectMapper = createObjectMapper();

        try {
            InputStream configStream = getClass().getResourceAsStream("/factory.json");

            if (configStream == null) {
                throw new RuntimeException("Config file 'factory.json' not found in resources");
            }

            return objectMapper.readTree(configStream);

        } catch (IOException e) {
            throw new RuntimeException("Unable to parse config file", e);
        }
    }

    /**
     * Creates a new ObjectMapper instance with JDK8 module support for enhanced serialization.
     * <p>
     * @return a configured ObjectMapper instance for JSON processing
     */
    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Prova a registrare JDK8 module se disponibile
        try {
            Class.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module");
            // Se arriviamo qui, la classe esiste
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jdk8.Jdk8Module());
        } catch (ClassNotFoundException e) {
            // JDK8 module non disponibile, continua senza
        }

        return objectMapper;
    }

    /**
     * Retrieves a copy of the component registry map for ID-based component lookup.
     * <p>
     * @return a copy of the component registry map keyed by component ID
     */
    public Map<Integer, Component> getComponentsMap() {
        return new HashMap<>(componentsMap);
    }

    /**
     * Creates a specific component instance from JSON configuration data.
     * <p>
     * @param componentJson the JsonNode containing the component's configuration data
     * @return a fully configured Component instance of the appropriate type
     * @throws IllegalArgumentException if the component type is unknown or configuration is invalid
     */
    @SuppressWarnings("Duplicates")
    private Component createComponent(JsonNode componentJson) {
        int id = componentJson.get("id").asInt();
        String type = componentJson.get("type").asText();
        JsonNode connectorsArray = componentJson.get("connectors");
        ConnectorType[] connectors = new ConnectorType[connectorsArray.size()];
        for (int i = 0; i < connectorsArray.size(); i++) {
            connectors[i] = (ConnectorType.valueOf(connectorsArray.get(i).asText()));
        }

        switch (type) {
            case "BatteryComponent":
                boolean isTriple = componentJson.get("isTriple").booleanValue();
                ;
                return new BatteryComponent(id, connectors, isTriple);

            case "CargoHoldsComponent":
                int numberCargo = componentJson.get("number").asInt();
                return new CargoHoldsComponent(id, connectors, numberCargo);

            case "CabinComponent":
                boolean isStartingCabin = componentJson.get("isStarting").booleanValue();
                ;
                return new CabinComponent(id, connectors, isStartingCabin);

            case "Component":
                return new Component(id, connectors);

            case "SpecialCargoHoldsComponent":
                int numberSpecialCargo = componentJson.get("number").asInt();
                return new SpecialCargoHoldsComponent(id, connectors, numberSpecialCargo);

            case "EngineComponent":
                DirectionType engineDirection = DirectionType.valueOf(componentJson.get("direction").asText());
                boolean engineIsDouble = componentJson.get("isDouble").booleanValue();
                ;
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