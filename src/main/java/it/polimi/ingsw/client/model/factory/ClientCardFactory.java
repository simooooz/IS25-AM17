package it.polimi.ingsw.client.model.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.ingsw.client.model.cards.ClientCard;

import java.util.List;

public class ClientCardFactory {

    private static final ObjectMapper mapper = createStaticObjectMapper();

    private static ObjectMapper createStaticObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Class.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module");
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jdk8.Jdk8Module());
        } catch (ClassNotFoundException e) {
            // JDK8 module not available, go ahead without
        }

        return objectMapper;
    }

    public static ClientCard deserializeCard(String jsonString) {
        try {
            return mapper.readValue(jsonString, ClientCard.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static List<ClientCard> deserializeCardList(String jsonString) {
        try {
            TypeReference<List<ClientCard>> typeRef = new TypeReference<>() {};
            return mapper.readValue(jsonString, typeRef);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}