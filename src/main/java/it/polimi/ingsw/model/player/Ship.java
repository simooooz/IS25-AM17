package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.properties.DirectionType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Ship {
    private final Optional<Component>[][] dashboard;
    private final List<Component> discards;
    private final Component[] reserves;
    private int crew;
    private int batteries;
    private boolean engineAlien;
    private boolean cannonAlien;
    private final Map<ColorType, Integer> goods;
    private final List<DirectionType> protectedSides;

    public Ship() {
        this.dashboard = new Optional[4][6];
        this.discards = new ArrayList<>();
        this.reserves = new Component[2];
        this.crew = 0;
        this.batteries = 0;
        this.engineAlien = false;
        this.cannonAlien = false;
        this.goods = new HashMap<>();
        this.protectedSides = new ArrayList<>();

        for(int row = 0; row < 4; row++) {
            for(int col = 0; col < 6; col++) {
                this.dashboard[row][col] = Optional.empty();
            }
        }
    }

    public Optional<Component>[][] getDashboard() {
        return dashboard;
    }

    public Optional<Component> getDashboard(int row, int col) {
        if (row < 0 || col < 0 || row >= dashboard.length || col >= dashboard[0].length) return Optional.empty();
        return dashboard[row][col];
    }

    public List<Component> getDiscards() {
        return discards;
    }

    public Component[] getReserves() {
        return reserves;
    }

    public int countExposedConnectors() {
        AtomicInteger exposedConnectors = new AtomicInteger();
        for (Optional<Component>[] row : dashboard) {
            for (Optional<Component> componentOpt : row) {
                componentOpt.ifPresent((Component component) -> {
                    if (component.getConnectors()[0] != ConnectorType.EMPTY && getDashboard(component.getY()-1, component.getX()).isEmpty())
                        exposedConnectors.getAndIncrement();
                    if (component.getConnectors()[1] != ConnectorType.EMPTY && getDashboard(component.getY(), component.getX()+1).isEmpty())
                        exposedConnectors.getAndIncrement();
                    if (component.getConnectors()[2] != ConnectorType.EMPTY && getDashboard(component.getY()+1, component.getX()).isEmpty())
                        exposedConnectors.getAndIncrement();
                    if (component.getConnectors()[3] != ConnectorType.EMPTY && getDashboard(component.getY(), component.getX()-1).isEmpty())
                        exposedConnectors.getAndIncrement();
                });
            }
        }
        return exposedConnectors.get();
    }

    public int getCrew() {
        return crew;
    }

    public void setCrew(int crew) {
        this.crew = crew;
    }

    public int getBatteries() {
        return batteries;
    }

    public void setBatteries(int batteries) {
        this.batteries = batteries;
    }

    public boolean getEngineAlien() {
        return engineAlien;
    }

    public boolean getCannonAlien() {
        return cannonAlien;
    }

    public void setEngineAlien(boolean engineAlien) {
        this.engineAlien = engineAlien;
    }

    public void setCannonAlien(boolean cannonAlien) {
        this.cannonAlien = cannonAlien;
    }

    public Map<ColorType, Integer> getGoods() {
        return goods;
    }

    public List<DirectionType> getProtectedSides() {
        return protectedSides;
    }

    public <T extends Component> List<T> getComponentByType(Class<T> componentType) {
        List<T> list = new ArrayList<>();
        for (Optional<Component>[] row : dashboard) {
            for (Optional<Component> component : row) {
                if (component.isPresent() && componentType.isInstance(component.get())) {
                    list.add(componentType.cast(component.get()));
                }
            }
        }
        return list;
    }

    public int calcEnginePower(List<EngineComponent> l) {
        int pwr = l.stream()
                    .mapToInt(e -> {
                            if (e.getIsDouble()) {
                                if (getBatteries() == 0) return 0;      // the player has no batteries => cannot take advantage of the dual motor(s)
                                else {
    //                                the player decides whether to use a battery to activate the dual motor
    //                                probably this response will be given by a user gesture (battery removal, click on a removal button...)

    //                                if (awaitForResponse()) {
    //                                    if so, the component where to decrement the number of batteries is received.

    //                                    BatteryComponent res = awaitForBatteryComponent();
    //                                    res.useBattery(this);
                                        setBatteries(getBatteries() - 1);
                                        return 2;
    //                                } else {
    //                                    return 0;
    //                                }
                                }
                            }
    //                        the engine is single, it does not need to be activated
                            return 1;
                    })
                    .sum();
        return engineAlien ? pwr+2 : pwr;
    }

    public double calcFirePower(List<CannonComponent> l) {
        double pwr = l.stream()
                        .mapToDouble(c -> {
                            if (c.getIsDouble()) {
                                if (getBatteries() == 0) return 0;      // the player has no batteries => cannot take advantage of the dual cannon(s)
                                else {
    //                                the player decides whether to use a battery to activate the dual cannon
    //                                probably this response will be given by a user gesture (battery removal, click on a removal button...)

    //                                if (awaitForResponse()) {
    //                                    if so, the component where to decrement the number of batteries is received.

    //                                    BatteryComponent res = awaitForBatteryComponent();
    //                                    res.useBattery(this);
                                        setBatteries(getBatteries() - 1);
                                        return c.getDirection() == DirectionType.NORTH ? 2 : 1;
    //                                } else {
    //                                    return 0;
    //                                }
                                }
                            }
                            return c.getDirection() == DirectionType.NORTH ? 1 : 0.5;
                        })
                        .sum();
        return cannonAlien ? pwr+2 : pwr;
    }

    public void updateComponents(List<Component> components) {
        for(Component component : components) {

        }
    }

    public void checkShip(int row, int col) {
        if (getDashboard(row, col).isEmpty()) return;

        if (!getDashboard(row, col).get().checkComponent(this)) getDashboard(row, col).get().affectDestroy(this);

        checkShip(row-1, col);
        checkShip(row+1, col);
        checkShip(row, col-1);
        checkShip(row, col+1);
    }

}
