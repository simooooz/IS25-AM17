package it.polimi.ingsw.model.components;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.exceptions.CabinComponentNotValidException;
import it.polimi.ingsw.model.exceptions.ComponentNotValidException;
import it.polimi.ingsw.model.game.objects.AlienType;
import it.polimi.ingsw.model.player.Ship;
import it.polimi.ingsw.view.TUI.Chroma;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CabinComponent extends Component {

    private int humans;
    private Optional<AlienType> alien;
    private final boolean isStarting;

    public CabinComponent(ConnectorType[] connectors, boolean isStarting) {
        super(connectors);
        this.humans = 0;
        this.alien = Optional.empty();
        this.isStarting = isStarting;
    }

    public boolean getIsStarting() {
        return isStarting;
    }

    public int getHumans() {
        return humans;
    }

    public void setHumans(int humans, Ship ship) {
        if (ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this))
            throw new ComponentNotValidException("Tile not valid");
        if (humans < 0) humans = 0;
        if (alien.isPresent()) setAlien(null, ship);
        int delta = humans - this.humans;
        ship.setCrew(ship.getCrew() + delta);
        this.humans = humans;
    }

    public Optional<AlienType> getAlien() {
        return alien;
    }

    public void setAlien(AlienType newAlien, Ship ship) {
        if (ship.getDashboard(y, x).isEmpty() || !ship.getDashboard(y, x).get().equals(this))
            throw new ComponentNotValidException("Tile not valid");
        if (isStarting && newAlien != null) throw new ComponentNotValidException("Alien isn't compatible with staring cabin tile");
        else if (this.alien.isEmpty() && newAlien != null) { // Should set new alien

            // Check if exists an odd component
            this.getLinkedNeighbors(ship).stream()
                    .filter(c -> c instanceof OddComponent)
                    .map(c -> (OddComponent) c)
                    .filter(c -> c.getType() == newAlien)
                    .findFirst()
                    .orElseThrow(() -> new CabinComponentNotValidException("Alien " + newAlien + " is not compatible with this cabin"));

            if (newAlien == AlienType.CANNON && !ship.getCannonAlien()) { ship.setCannonAlien(true); }
            else if (newAlien == AlienType.CANNON && ship.getCannonAlien()) throw new CabinComponentNotValidException("Alien " + newAlien + " is already present");
            else if (newAlien == AlienType.ENGINE && !ship.getEngineAlien()) { ship.setEngineAlien(true); }
            else if (newAlien == AlienType.ENGINE && ship.getEngineAlien()) throw new CabinComponentNotValidException("Alien " + newAlien + " is already present");

            setHumans(0, ship);
            ship.setCrew(ship.getCrew() + 1);
        }
        else if (this.alien.isPresent() && newAlien == null) { // Should remove alien
            ship.setCrew(ship.getCrew() - 1);
            if (this.alien.get() == AlienType.CANNON) { ship.setCannonAlien(false); }
            else { ship.setEngineAlien(false); }
        }
        this.alien = Optional.ofNullable(newAlien);
    }

    @Override
    public void insertComponent(Ship ship, int row, int col, boolean learnerMode) {
        super.insertComponent(ship, row, col, learnerMode);
        setHumans(2, ship);
    }

    @Override
    public void affectDestroy(Ship ship) {
        setHumans(0, ship);
        setAlien(null, ship);
        super.affectDestroy(ship);
    }


    public List<String> icon() {
        if (getHumans() == 0 && alien.isEmpty()) {
            return new ArrayList<>(List.of(
                    " " + Chroma.color("┌─────┐", Chroma.GREY_BOLD) + " ",
                    " " + Chroma.color("│     │", Chroma.GREY_BOLD) + " ",
                    " " + Chroma.color("└─────┘", Chroma.GREY_BOLD) + " "));

        }
        else if (getHumans() > 0) {
            if(getHumans() == 2)
                return new ArrayList<>(List.of(
                        " " + Chroma.color("┌─────┐", Chroma.GREY_BOLD) + " ",
                        " " + Chroma.color("│" + "👨🏻‍🚀" + "\u200A" + "\u200A"+ "👨🏻‍🚀" + "│", Chroma.GREY_BOLD) + " ",
                        " " + Chroma.color("└─────┘", Chroma.GREY_BOLD) + " "));
            else
                return new ArrayList<>(List.of(
                    " " + Chroma.color("┌─────┐", Chroma.GREY_BOLD) + " ",
                    " " + Chroma.color("│ " + "\u2009" + "👨🏻‍🚀" + "\u2009" + " │", Chroma.GREY_BOLD) + " ",
                    " " + Chroma.color("└─────┘", Chroma.GREY_BOLD) + " "));
        }
        else {
            String color = getAlien().orElseThrow().equals(AlienType.CANNON) ? Chroma.PURPLE_BOLD : Chroma.ORANGE_BOLD;
            return new ArrayList<>(List.of(
                    " " + Chroma.color("┌─────┐", color) + " ",
                    " " + Chroma.color("│ " + "\u2009" + "👽" + "\u2009" + " │", color) + " ",
                    " " + Chroma.color("└─────┘", color) + " "));

        }
    }

}
