package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.utils.CannonFire;
import it.polimi.ingsw.model.components.BatteryComponent;
import it.polimi.ingsw.model.components.CannonComponent;
import it.polimi.ingsw.model.game.Board;
import it.polimi.ingsw.model.game.objects.Dice;
import it.polimi.ingsw.model.player.PlayerData;
import it.polimi.ingsw.model.properties.DirectionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PiratesCard extends Card{
    private final int piratesFirePower;
    private final int credits;
    private final int days;
    private final List<CannonFire> cannonFires;

    public PiratesCard(int level, boolean isLearner, int piratesFirePower, int credits, int days, List<CannonFire> cannonFires) {
        super(level, isLearner);
        this.piratesFirePower = piratesFirePower;
        this.credits = credits;
        this.days = days;
        this.cannonFires = cannonFires;
    }

    @Override
    public void resolve(Board board) throws Exception {
        List<PlayerData> defeatedPlayers = new ArrayList<>();
        boolean piratesDefeated = false;

        for (PlayerData player : board.getPlayersByPos()) {

            boolean win = false;
            double singleCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                .filter(cannon -> !cannon.getIsDouble())
                .mapToDouble(cannon -> cannon.getDirection() == DirectionType.NORTH ? 1 : 0.5).sum();
            double doubleCannonsPower = player.getShip().getComponentByType(CannonComponent.class).stream()
                    .filter(CannonComponent::getIsDouble)
                    .mapToDouble(cannon -> cannon.getDirection() == DirectionType.NORTH ? 2 : 1).sum();

            if (singleCannonsPower >= piratesFirePower) { // User win automatically
                win = true;
            }
            else if (singleCannonsPower + doubleCannonsPower >= piratesFirePower) { // User could win
                List<CannonComponent> cannonsToActivate = new ArrayList<>(); // View => user select which cannons wants to activate. If he hasn't enough batteries he'll press skip
                if (player.getShip().getBatteries() < cannonsToActivate.size()) throw new Exception(); // Not enough batteries

                double userFirePower = singleCannonsPower;
                for (CannonComponent doubleCannon : cannonsToActivate) // Calculate firepower
                    userFirePower += doubleCannon.getDirection() == DirectionType.NORTH ? 2 : 1;

                if (userFirePower >= piratesFirePower) { // Set win to true and use batteries
                    win = true;
                    for (int i = 0; i < cannonsToActivate.size(); i++) { // Remove batteries
                        Optional<BatteryComponent> chosenComponentOpt = Optional.empty(); // View => Ask the user
                        BatteryComponent chosenComponent = chosenComponentOpt.orElseThrow();
                        chosenComponent.useBattery(player.getShip());
                    }
                }
            }

            if (!win) { // Player is defeated
                defeatedPlayers.add(player);
            }
            else if (!piratesDefeated) {
                boolean wantRedeem = false; // View => Ask user if he wants to redeem the reward
                if (wantRedeem) {
                    board.movePlayer(player, -1*days);
                    player.setCredits(credits + player.getCredits());
                    piratesDefeated = true;
                }
            }
        }

        for(CannonFire cannonFire : cannonFires) {
            int coord = Dice.roll() + Dice.roll(); // View => The first defeated player rolls dices
            for (PlayerData player : defeatedPlayers)
                cannonFire.hit(player.getShip(), coord);
        }

    }

}
