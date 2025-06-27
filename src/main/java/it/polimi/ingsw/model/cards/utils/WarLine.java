package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Utility class representing a single war line within a combat zone encounter.
 * A war line defines a specific evaluation criterion and its associated penalty,
 * creating a complete assessment and consequence system for player performance.
 */
public class WarLine {

    /**
     * The criteria type that defines how players are evaluated in this war line
     */
    @JsonProperty
    private CriteriaType criteriaType;

    /**
     * The penalty applied to the worst-performing player in this war line
     */
    @JsonProperty
    private PenaltyCombatZone penalty;

    /**
     * Constructs a new WarLine with the specified evaluation criteria and penalty.
     * <p>
     *
     * @param criteriaType the criteria type that defines how players are evaluated
     * @param penalty      the penalty to be applied to the worst-performing player
     */
    public WarLine(CriteriaType criteriaType, PenaltyCombatZone penalty) {
        this.criteriaType = criteriaType;
        this.penalty = penalty;
    }

    /**
     * Retrieves the criteria type used for player evaluation in this war line.
     * <p>
     * @return the CriteriaType defining the evaluation method for this war line
     */
    public CriteriaType getCriteriaType() {
        return criteriaType;
    }

    /**
     * Retrieves the penalty applied to the worst-performing player in this war line.
     * <p>
     *
     * @return the PenaltyCombatZone defining the consequences for poor performance
     */
    public PenaltyCombatZone getPenalty() {
        return penalty;
    }

}