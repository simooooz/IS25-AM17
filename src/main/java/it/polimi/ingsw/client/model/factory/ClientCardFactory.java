package it.polimi.ingsw.client.model.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
// RIMOSSO: import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import it.polimi.ingsw.client.model.cards.ClientCard;

import java.util.List;

public class ClientCardFactory {
    // MODIFICATO: Rimosso .registerModule(new Jdk8Module())
    private static final ObjectMapper mapper = createStaticObjectMapper();

    // AGGIUNTO: Metodo statico per creare ObjectMapper
    private static ObjectMapper createStaticObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Prova a registrare JDK8 module se disponibile
        try {
            Class.forName("com.fasterxml.jackson.datatype.jdk8.Jdk8Module");
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jdk8.Jdk8Module());
        } catch (ClassNotFoundException e) {
            // JDK8 module non disponibile, continua senza
        }

        return objectMapper;
    }

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