package it.polimi.ingsw.model.factory;

import it.polimi.ingsw.model.cards.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class CardFactoryTest {

    private CardFactory cardFactory;
    private List<JSONObject> cardNodes;

    @BeforeEach
    public void setUp() throws IOException {
        // Initialize your factory
        cardFactory = new CardFactory();

        // Load the JSON data
        String jsonContent = new String(Files.readAllBytes(new File("src/main/java/it/polimi/ingsw/model/resources/factory.json").toPath()));
        JSONObject rootNode = new JSONObject(jsonContent);

        // Extract the cards array
        JSONArray cardsArray = rootNode.getJSONArray("cards");

        // Convert to a list for easier testing
        cardNodes = new ArrayList<>();
        for (int i = 0; i < cardsArray.length(); i++) {
            cardNodes.add(cardsArray.getJSONObject(i));
        }
    }

    @Test
    public void testAllCardsFromJson() {
        for (JSONObject cardNode : cardNodes) {
            String type = cardNode.getString("type");
            
            Card card = cardFactory.createCard(cardNode);
            assertNotNull(card, "Card should not be null");
            assertEquals(type, card.getClass().getSimpleName(), "Card type should match");

            if (card instanceof SlaversCard) {
                SlaversCard slaversCard = (SlaversCard) card;
                assertEquals(cardNode.getInt("level"), slaversCard.getLevel());
                assertEquals(cardNode.getBoolean("isLearner"), slaversCard.getIsLearner());
                assertEquals(cardNode.getInt("crew"), slaversCard.getCrew());
                assertEquals(cardNode.getInt("credits"), slaversCard.getCredits());
                assertEquals(cardNode.getInt("days"), slaversCard.getDays());
                assertEquals(cardNode.getInt("firePower"), slaversCard.getFirePower());
            } else if (card instanceof SmugglersCard) {
                SmugglersCard smugglersCard = (SmugglersCard) card;
                assertEquals(cardNode.getInt("level"), smugglersCard.getLevel());
                assertEquals(cardNode.getBoolean("isLearner"), smugglersCard.getIsLearner());
                assertEquals(cardNode.getInt("firePower"), smugglersCard.getFirePower());
                assertEquals(cardNode.getInt("lostGoods"), smugglersCard.getLostGoods());
                assertEquals(cardNode.getJSONArray("goods").toList(), smugglersCard.getGoods().stream().map(Enum::name).toList());
                assertEquals(cardNode.getInt("days"), smugglersCard.getDays());
            } else if (card instanceof PiratesCard) {
                PiratesCard piratesCard = (PiratesCard) card;
                assertEquals(cardNode.getInt("level"), piratesCard.getLevel());
                assertEquals(cardNode.getBoolean("isLearner"), piratesCard.getIsLearner());
                assertEquals(cardNode.getInt("piratesFirePower"), piratesCard.getPiratesFirePower());
                assertEquals(cardNode.getInt("credits"), piratesCard.getCredits());
                assertEquals(cardNode.getInt("days"), piratesCard.getDays());
            } else if (card instanceof StardustCard) {
                StardustCard stardustCard = (StardustCard) card;
                assertEquals(cardNode.getInt("level"), stardustCard.getLevel());
                assertEquals(cardNode.getBoolean("isLearner"), stardustCard.getIsLearner());
            } else if (card instanceof OpenSpaceCard) {
                OpenSpaceCard openSpaceCard = (OpenSpaceCard) card;
                assertEquals(cardNode.getInt("level"), openSpaceCard.getLevel());
                assertEquals(cardNode.getBoolean("isLearner"), openSpaceCard.getIsLearner());
            } else if (card instanceof EpidemicCard) {
                EpidemicCard epidemicCard = (EpidemicCard) card;
                assertEquals(cardNode.getInt("level"), epidemicCard.getLevel());
                assertEquals(cardNode.getBoolean("isLearner"), epidemicCard.getIsLearner());
            } else if (card instanceof MeteorSwarmCard) {
                MeteorSwarmCard meteorSwarmCard = (MeteorSwarmCard) card;
                assertEquals(cardNode.getInt("level"), meteorSwarmCard.getLevel());
                assertEquals(cardNode.getBoolean("isLearner"), meteorSwarmCard.getIsLearner());
            } else if (card instanceof AbandonedShipCard) {
                AbandonedShipCard abandonedShipCard = (AbandonedShipCard) card;
                assertEquals(cardNode.getInt("level"), abandonedShipCard.getLevel());
                assertEquals(cardNode.getBoolean("isLearner"), abandonedShipCard.getIsLearner());
                assertEquals(cardNode.getInt("crew"), abandonedShipCard.getCrew());
                assertEquals(cardNode.getInt("credits"), abandonedShipCard.getCredits());
                assertEquals(cardNode.getInt("days"), abandonedShipCard.getDays());
            } else if (card instanceof AbandonedStationCard) {
                AbandonedStationCard abandonedStationCard = (AbandonedStationCard) card;
                assertEquals(cardNode.getInt("level"), abandonedStationCard.getLevel());
                assertEquals(cardNode.getBoolean("isLearner"), abandonedStationCard.getIsLearner());
                assertEquals(cardNode.getInt("crew"), abandonedStationCard.getCrew());
                assertEquals(cardNode.getInt("days"), abandonedStationCard.getDays());
            } else if (card instanceof PlanetCard) {
                PlanetCard planetCard = (PlanetCard) card;
                assertEquals(cardNode.getInt("level"), planetCard.getLevel());
                assertEquals(cardNode.getBoolean("isLearner"), planetCard.getIsLearner());
                assertEquals(cardNode.getInt("days"), planetCard.getDays());
            } else if (card instanceof CombactZoneCard) {
                    CombactZoneCard combactZoneCard = (CombactZoneCard) card;
                    assertEquals(cardNode.getInt("level"), combactZoneCard.getLevel());
                    assertEquals(cardNode.getBoolean("isLearner"), combactZoneCard.getIsLearner());
                    JSONArray warLinesArray = cardNode.getJSONArray("warLines");
                    assertEquals(warLinesArray.length(), combactZoneCard.getWarLines().size());
                }
        }
    }

    
}