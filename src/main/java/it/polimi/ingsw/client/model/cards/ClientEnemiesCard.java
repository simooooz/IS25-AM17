package it.polimi.ingsw.client.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a specific type of game card on the client side that involves an encounter with enemies.
 * This is an abstract base class for cards that feature gameplay mechanics such as fighting or defeating enemies.
 * It extends the {@code ClientCard} class, inheriting common properties and methods related to game cards.
 * <p>
 * Subclasses of {@code ClientEnemiesCard} may define gameplay-related characteristics specific to different
 * types of enemy cards, such as pirates, smugglers, or slavers.
 */
public abstract class ClientEnemiesCard extends ClientCard {

    @JsonProperty
    protected int days;
    @JsonProperty
    protected int enemyFirePower;
    @JsonProperty
    protected boolean enemiesDefeated;

    public ClientEnemiesCard() {
    }

}
