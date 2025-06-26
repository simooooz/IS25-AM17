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
 * The ComponentFactory serves multiple critical functions:
 * - JSON-based component creation from configuration files
 * - Polymorphic component instantiation supporting all component types in the game
 * - Management of both regular components and special starting cabin components
 * - Centralized component registry with ID-based lookup capabilities
 * - Color-coded starting cabin allocation for player ship initialization
 * <p>
 * The factory supports a comprehensive set of component types including:
 * - Power systems (BatteryComponent)
 * - Crew quarters (CabinComponent, including starting cabins)
 * - Storage systems (CargoHoldsComponent, SpecialCargoHoldsComponent)
 * - Propulsion systems (EngineComponent with directional and power variants)
 * - Weapon systems (CannonComponent with directional and power variants)
 * - Defensive systems (ShieldComponent with multi-directional protection)
 * - Alien support infrastructure (OddComponent for specialized crew)
 * <p>
 * Component creation involves parsing JSON configurations that specify:
 * - Basic component properties (ID, type, connector configurations)
 * - Type-specific parameters (capacity, direction, power level, protection)
 * - Special flags (starting cabin status, triple battery capacity, double power)
 * - Enumerated values (directions, alien types, connector types)
 * <p>
 * The factory maintains separate collections for regular components and starting cabins,
 * enabling efficient component distribution and player initialization processes.
 *
 * @author Generated Javadoc
 * @version 1.0
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
     * The construction process follows these steps:
     * 1. Loads the JSON configuration containing all component definitions
     * 2. Parses starting cabin components and associates them with color types
     * 3. Parses regular components and adds them to the available component pool
     * 4. Builds the component registry map for ID-based component lookup
     * <p>
     * Starting cabins are processed first and associated with color types in the order
     * they appear in the configuration, enabling color-coded player ship initialization.
     * Regular components are then processed and made available for acquisition during gameplay.
     * <p>
     * All components are registered in the components map regardless of type, providing
     * a unified lookup system for component identification and management.
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
     * This method provides access to all non-starting components that players can
     * acquire, pick up, and add to their ships during the game. Starting cabins
     * are not included in this list as they are handled separately during ship initialization.
     *
     * @return the list of regular components available for gameplay
     */
    public List<Component> getComponents() {
        return components;
    }

    /**
     * Retrieves the map of starting cabin components organized by color type.
     * <p>
     * Starting cabins are special cabin components that form the initial foundation
     * of each player's ship. They are associated with color types to enable
     * color-coded ship initialization and player differentiation. Each color
     * corresponds to a specific starting cabin configuration.
     *
     * @return the map of starting cabins keyed by color type
     */
    public Map<ColorType, Component> getStartingCabins() {
        return startingCabins;
    }

    /**
     * Loads and parses the JSON configuration file containing component definitions.
     * <p>
     * This method reads the 'factory.json' resource file and parses it into
     * a JsonNode structure that can be used for component creation. The configuration
     * file contains the complete specifications for all component types including
     * both regular components and starting cabins.
     *
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
     * The ObjectMapper is configured with JDK8 module support when available,
     * enabling proper handling of Optional types and other JDK8 features commonly
     * used in component implementations. The module registration is done conditionally
     * to avoid dependencies on JDK8-specific libraries when not available.
     *
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
     * This method provides access to the complete component registry that includes
     * both regular components and starting cabins. The map enables efficient
     * component lookup by ID for various game operations such as component
     * identification, event processing, and state synchronization.
     * <p>
     * A defensive copy is returned to prevent external modification of the
     * internal component registry while still providing lookup capabilities.
     *
     * @return a copy of the component registry map keyed by component ID
     */
    public Map<Integer, Component> getComponentsMap() {
        return new HashMap<>(componentsMap);
    }

    /**
     * Creates a specific component instance from JSON configuration data.
     * <p>
     * This method implements the core component creation logic, parsing JSON data
     * and instantiating the appropriate component type based on the configuration.
     * The method handles complex component creation including:
     * <p>
     * - Basic component properties (ID, connector configuration)
     * - Type-specific parameters (capacity, direction, power settings)
     * - Special component flags (starting status, triple capacity, double power)
     * - Enumerated value parsing (directions, alien types, connector types)
     * - Array processing (connector arrays, direction arrays)
     * <p>
     * Supported component types include:
     * - BatteryComponent: Power storage with optional triple capacity
     * - CargoHoldsComponent: Standard goods storage with red goods restriction
     * - CabinComponent: Crew quarters with optional starting cabin designation
     * - SpecialCargoHoldsComponent: Enhanced goods storage supporting all good types
     * - EngineComponent: Propulsion system with direction and power configuration
     * - CannonComponent: Weapon system with direction and power configuration
     * - ShieldComponent: Defensive system with multi-directional protection
     * - OddComponent: Alien support infrastructure for specialized crew
     * <p>
     * The method ensures type safety by validating component configurations
     * and provides meaningful error messages for unknown component types.
     *
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