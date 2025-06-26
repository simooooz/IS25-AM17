package it.polimi.ingsw.model.cards.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Utility class representing a single war line within a combat zone encounter.
 * A war line defines a specific evaluation criterion and its associated penalty,
 * creating a complete assessment and consequence system for player performance.
 * <p>
 * War lines are the building blocks of combat zone encounters, where players
 * are evaluated on different aspects of their ship capabilities (crew, cannons, engines)
 * and the worst performer in each category faces the corresponding penalty.
 * Multiple war lines can be chained together to create complex, multi-stage
 * combat evaluations that test various aspects of player preparedness.
 * <p>
 * Each war line operates independently, allowing combat zones to evaluate
 * different ship capabilities sequentially and apply appropriate consequences
 * for each area of weakness. This system creates strategic depth where players
 * must maintain balanced ship capabilities to avoid multiple penalties.
 *
 * @author Generated Javadoc
 * @version 1.0
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
     * The war line combines an evaluation criterion (such as crew count, cannon power,
     * or engine capability) with a corresponding penalty that will be applied to
     * the player who performs worst in that category. This pairing creates a
     * complete assessment and consequence system for one aspect of combat readiness.
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
     * The criteria type determines how players are assessed and compared within
     * this war line. Different criteria types evaluate different aspects of
     * ship capability, such as crew resources, offensive firepower, or
     * propulsion systems.
     * <p>
     * This information is used to:
     * - Determine the appropriate evaluation method for each player
     * - Calculate player rankings within the war line
     * - Identify which player performs worst and faces the penalty
     *
     * @return the CriteriaType defining the evaluation method for this war line
     */
    public CriteriaType getCriteriaType() {
        return criteriaType;
    }

    /**
     * Retrieves the penalty applied to the worst-performing player in this war line.
     * <p>
     * The penalty defines the specific consequences that the worst performer
     * will face after the evaluation is complete. Penalties can range from
     * simple resource deductions to complex multi-stage punishment sequences,
     * depending on the severity and nature of the combat zone.
     * <p>
     * This penalty system allows war lines to apply thematically appropriate
     * consequences that match the evaluation criteria, such as crew losses
     * for poor crew management or equipment damage for insufficient defensive capability.
     *
     * @return the PenaltyCombatZone defining the consequences for poor performance
     */
    public PenaltyCombatZone getPenalty() {
        return penalty;
    }

}