package it.polimi.ingsw.client.model.cards;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ClientEnemiesCard extends ClientCard {

    @JsonProperty protected int days;
    @JsonProperty protected int enemyFirePower;
    @JsonProperty protected boolean enemiesDefeated;

    public ClientEnemiesCard(int id, int level, boolean isLearner, int days, int enemyFirePower) {
        super(id, level, isLearner);
        this.days = days;
        this.enemyFirePower = enemyFirePower;
    }

    public ClientEnemiesCard() {}

}
