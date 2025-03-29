package it.polimi.ingsw.model.factory;


import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.properties.DirectionType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class ComponentFactory {

    private List<Component> components;
    private Map<Integer, Component> componentsMap;

    public ComponentFactory() {
        // Costruttore che genera il mazzo dalle carte nel JSON
        this.components = new ArrayList<>();
        this.componentsMap = new HashMap<>();

        JSONObject deckJson = loadJsonConfig();
        JSONArray componentsArray = deckJson.getJSONArray("components");

        for (int i = 0; i < componentsArray.length(); i++) {
            JSONObject cardJson = componentsArray.getJSONObject(i);
            Component component = createComponent(cardJson);
            components.add(component);
            componentsMap.put(i+1, component);
        }
    }

    public List<Component> getComponents(){
        return components;
    }

    private JSONObject loadJsonConfig() {
        try {
            String jsonContent = new String(Files.readAllBytes(new File("src/main/java/it/polimi/ingsw/model/resources/factory.json").toPath()));
            return new JSONObject(jsonContent);
        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file JSON: " + e.getMessage());
            return new JSONObject(); // Restituisce un JSON vuoto
        }
    }

    public Map<Integer, Component> getComponentsMap() {
        return new HashMap<>(componentsMap);
    }


    public static Component createComponent(JSONObject componentJson) {
        String type = componentJson.getString("type");
        JSONArray connectorsArray = componentJson.getJSONArray("connectors");
        ConnectorType[] connectors = new ConnectorType[connectorsArray.length()];
        for (int i = 0; i < connectorsArray.length(); i++) {
            connectors[i] = (ConnectorType.valueOf(connectorsArray.getString(i)));
        }

        switch(type) {
            case "BatteryComponent":
                boolean isTriple = componentJson.optBoolean("isTriple", false);
                return new BatteryComponent(connectors, isTriple);

            case "CargoHoldsComponent":
                int numberCargo = componentJson.optInt("number", 0);
                return new CargoHoldsComponent(connectors, numberCargo);

            case "CabinComponent":
                boolean isStartingCabin = componentJson.optBoolean("isStarting", false);
                return new CabinComponent(connectors, isStartingCabin);

            case "Component":
                return new Component(connectors);

            case "SpecialCargoHoldsComponent":
                int numberSpecialCargo = componentJson.optInt("number", 0);
                return new SpecialCargoHoldsComponent(connectors, numberSpecialCargo);

            case "EngineComponent":
                DirectionType engineDirection = DirectionType.valueOf(componentJson.getString("direction"));
                boolean engineIsDouble = componentJson.optBoolean("isDouble", false);
                return new EngineComponent(connectors, engineDirection, engineIsDouble);

            case "CannonComponent":
                DirectionType cannonDirection = DirectionType.valueOf(componentJson.getString("direction"));
                boolean cannonIsDouble = componentJson.optBoolean("isDouble", false);
                return new CannonComponent(connectors, cannonDirection, cannonIsDouble);

            case "ShieldComponent":
                JSONArray directionArray = componentJson.getJSONArray("directionsProtected");
                DirectionType[] direction = new DirectionType[directionArray.length()];
                for (int i = 0; i < directionArray.length(); i++) {
                    direction[i] = (DirectionType.valueOf(directionArray.getString(i)));
                }
                return new ShieldComponent(connectors, direction);

            case "OddComponent":
                AlienType alienType = AlienType.valueOf(componentJson.getString("typeAlien"));
                return new OddComponent(connectors, alienType);

            default:
                throw new IllegalArgumentException("Unknown component type: " + type);
        }
    }
}
