package it.polimi.ingsw.model.factory;


import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.properties.DirectionType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class ComponentFactory {

    private final List<Component> components;
    private final Map<Integer, Component> componentsMap;
    private final Map<ColorType, Component> startingCabins;

    public ComponentFactory() {
        this.components = new ArrayList<>();
        this.componentsMap = new HashMap<>();
        this.startingCabins = new HashMap<>();

        JSONObject componentsJson = loadJsonConfig();
        JSONArray componentsArray = componentsJson.getJSONArray("components");
        JSONArray startingCabinsArray = componentsJson.getJSONArray("startingCabins");

        List<ColorType> colors = List.of(ColorType.values());
        for (int i = 0; i < startingCabinsArray.length(); i++) {
            JSONObject componentJson = startingCabinsArray.getJSONObject(i);
            Component component = createComponent(componentJson);
            startingCabins.put(colors.get(i), component);
            componentsMap.put(componentJson.getInt("id"), component);
        }

        for (int i = 0; i < componentsArray.length(); i++) {
            JSONObject componentJson = componentsArray.getJSONObject(i);
            Component component = createComponent(componentJson);
            components.add(component);
            componentsMap.put(componentJson.getInt("id"), component);
        }
    }

    public List<Component> getComponents(){
        return components;
    }

    public Map<ColorType, Component> getStartingCabins() {
        return startingCabins;
    }

    private JSONObject loadJsonConfig() {
        try {
            String jsonContent = new String(getClass().getResourceAsStream("/factory.json").readAllBytes());
            return new JSONObject(jsonContent);
        } catch (IOException e) {
            System.err.println("Error while loading JSON file: " + e.getMessage());
            return new JSONObject(); // Return an empty JSON
        }
    }

    public Map<Integer, Component> getComponentsMap() {
        return new HashMap<>(componentsMap);
    }

    private Component createComponent(JSONObject componentJson) {
        int id = componentJson.getInt("id");
        String type = componentJson.getString("type");
        JSONArray connectorsArray = componentJson.getJSONArray("connectors");
        ConnectorType[] connectors = new ConnectorType[connectorsArray.length()];
        for (int i = 0; i < connectorsArray.length(); i++) {
            connectors[i] = (ConnectorType.valueOf(connectorsArray.getString(i)));
        }

        switch(type) {
            case "BatteryComponent":
                boolean isTriple = componentJson.optBoolean("isTriple", false);
                return new BatteryComponent(id, connectors, isTriple);

            case "CargoHoldsComponent":
                int numberCargo = componentJson.optInt("number", 0);
                return new CargoHoldsComponent(id, connectors, numberCargo);

            case "CabinComponent":
                boolean isStartingCabin = componentJson.optBoolean("isStarting", false);
                return new CabinComponent(id, connectors, isStartingCabin);

            case "Component":
                return new Component(id, connectors);

            case "SpecialCargoHoldsComponent":
                int numberSpecialCargo = componentJson.optInt("number", 0);
                return new SpecialCargoHoldsComponent(id, connectors, numberSpecialCargo);

            case "EngineComponent":
                DirectionType engineDirection = DirectionType.valueOf(componentJson.getString("direction"));
                boolean engineIsDouble = componentJson.optBoolean("isDouble", false);
                return new EngineComponent(id, connectors, engineDirection, engineIsDouble);

            case "CannonComponent":
                DirectionType cannonDirection = DirectionType.valueOf(componentJson.getString("direction"));
                boolean cannonIsDouble = componentJson.optBoolean("isDouble", false);
                return new CannonComponent(id, connectors, cannonDirection, cannonIsDouble);

            case "ShieldComponent":
                JSONArray directionArray = componentJson.getJSONArray("directionsProtected");
                DirectionType[] direction = new DirectionType[directionArray.length()];
                for (int i = 0; i < directionArray.length(); i++) {
                    direction[i] = (DirectionType.valueOf(directionArray.getString(i)));
                }
                return new ShieldComponent(id, connectors, direction);

            case "OddComponent":
                AlienType alienType = AlienType.valueOf(componentJson.getString("typeAlien"));
                return new OddComponent(id, connectors, alienType);

            default:
                throw new IllegalArgumentException("Unknown component type: " + type);
        }
    }
}
