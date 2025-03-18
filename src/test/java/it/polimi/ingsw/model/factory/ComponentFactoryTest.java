package it.polimi.ingsw.model.factory;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.components.Component;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.properties.DirectionType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class ComponentFactoryTest {

    private ComponentFactory componentFactory;
    private List<JSONObject> componentNodes;

    @BeforeEach
    public void setUp() throws IOException {
        // Initialize your factory
        componentFactory = new ComponentFactory();

        // Load the JSON data
        String jsonContent = new String(Files.readAllBytes(new File("C:\\Users\\tommy\\IdeaProjects\\IS25-AM17\\src\\main\\java\\it\\polimi\\ingsw\\model\\resources\\factory.json").toPath()));
        JSONObject rootNode = new JSONObject(jsonContent);

        // Extract the cards array
        JSONArray componentArray = rootNode.getJSONArray("components");

        // Convert to a list for easier testing
        componentNodes = new ArrayList<>();
        for (int i = 0; i < componentArray.length(); i++) {
            componentNodes.add(componentArray.getJSONObject(i));
        }
    }

    @Test
    public void testAllComponentFromJson() {
        for (JSONObject c : componentNodes) {
            String type = c.getString("type");

            Component component = componentFactory.createComponent(c);
            assertNotNull(component, "Card should not be null");

            if (component instanceof BatteryComponent) {
                BatteryComponent batteryComponent = (BatteryComponent) component;
                assertEquals(c.getJSONArray("connectors").toList(), Arrays.stream(batteryComponent.getConnectors()).map(Enum::name).toList());
                assertEquals(c.getBoolean("isTriple"), batteryComponent.getIsTriple() );
            }
            else if (component instanceof CabinComponent) {
                CabinComponent cabinComponent = (CabinComponent) component;
                assertEquals(c.getJSONArray("connectors").toList(), Arrays.stream(cabinComponent.getConnectors()).map(Enum::name).toList());
                assertEquals(c.getBoolean("isStarting"), cabinComponent.getIsStarting());
            }
            else if (component instanceof CannonComponent) {
                CannonComponent cannonComponent = (CannonComponent) component;
                assertEquals(c.getJSONArray("connectors").toList(), Arrays.stream(cannonComponent.getConnectors()).map(Enum::name).toList());
                assertEquals(c.getEnum(DirectionType.class, "direction"), cannonComponent.getDirection());
                assertEquals(c.getBoolean("isDouble"), cannonComponent.getIsDouble());
            }
            else if (component instanceof CargoHoldsComponent) {
                CargoHoldsComponent cargoHoldsComponent = (CargoHoldsComponent) component;
                assertEquals(c.getJSONArray("connectors").toList(), Arrays.stream(cargoHoldsComponent.getConnectors()).map(Enum::name).toList());
                assertEquals(c.getInt("number"), cargoHoldsComponent.getNumber());
            }
            else if (component instanceof Component) {
                Component component2 = (Component) component;
                assertEquals(c.getJSONArray("connectors").toList(), Arrays.stream(component2.getConnectors()).map(Enum::name).toList());
            }
            else if (component instanceof EngineComponent) {
                EngineComponent engineComponent = (EngineComponent) component;
                assertEquals(c.getJSONArray("connectors").toList(), Arrays.stream(engineComponent.getConnectors()).map(Enum::name).toList());
                assertEquals(c.getEnum(DirectionType.class, "direction"), engineComponent.getDirection());
                assertEquals(c.getBoolean("isDouble"), engineComponent.getIsDouble());
            }
            else if (component instanceof OddComponent) {
                OddComponent oddComponent = (OddComponent) component;
                assertEquals(c.getJSONArray("connectors").toList(), Arrays.stream(oddComponent.getConnectors()).map(Enum::name).toList());
                assertEquals(c.getEnum(AlienType.class, "typeAlien"), oddComponent.getType());
            }
            else if (component instanceof SpecialCargoHoldsComponent) {
                SpecialCargoHoldsComponent specialCargoHoldsComponent = (SpecialCargoHoldsComponent) component;
                assertEquals(c.getJSONArray("connectors").toList(), Arrays.stream(specialCargoHoldsComponent.getConnectors()).map(Enum::name).toList());
                assertEquals(c.getInt("number"), specialCargoHoldsComponent.getNumber());
            } else if (component instanceof ShieldComponent) {
                ShieldComponent shieldComponent = (ShieldComponent) component;
                assertEquals(c.getJSONArray("connectors").toList(), Arrays.stream(shieldComponent.getConnectors()).map(Enum::name).toList());
                assertEquals(c.getJSONArray("directionsProtected").toList(), Arrays.stream(shieldComponent.getDirectionsProtected()).map(Enum::name).toList());
            }

        }    
    }


}