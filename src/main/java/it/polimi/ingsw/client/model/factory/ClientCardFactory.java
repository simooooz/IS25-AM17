package it.polimi.ingsw.client.model.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.client.model.cards.ClientCard;

import java.util.List;

public class ClientCardFactory {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static ClientCard deserializeCard(String jsonString) {
        try {
            return mapper.readValue(jsonString, ClientCard.class);
        } catch (JsonProcessingException e) {
            // TODO che faccio?
            e.printStackTrace();
            throw new RuntimeException("Errore deserializzazione carta: " + e.getMessage(), e);
        }
    }

    public static List<ClientCard> deserializeCardList(String jsonString) {
        try {
            TypeReference<List<ClientCard>> typeRef = new TypeReference<>() {};
            return mapper.readValue(jsonString, typeRef);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore deserializzazione lista carte: " + e.getMessage(), e);
        }
    }

}
