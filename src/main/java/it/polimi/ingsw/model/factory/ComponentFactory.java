package it.polimi.ingsw.model.factory;

import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.properties.DirectionType;
import org.json.JSONArray;
import org.json.JSONObject;

public class ComponentFactory {
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
                boolean isStartingCabin = componentJson.optBoolean("isStartingCabin", false);
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
                JSONArray directionArray = componentJson.getJSONArray("directionProtected");
                DirectionType[] direction = new DirectionType[directionArray.length()];
                for (int i = 0; i < directionArray.length(); i++) {
                    direction[i] = (DirectionType.valueOf(directionArray.getString(i)));
                }
                return new ShieldComponent(connectors, direction);

            case "OddsComponent":
                AlienType alienType = AlienType.valueOf(componentJson.getString("typeAlien"));
                return new OddComponent(connectors, alienType);

            default:
                throw new IllegalArgumentException("Unknown component type: " + type);
        }
    }
}
