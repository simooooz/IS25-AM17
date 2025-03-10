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
    private final Map<ColorType, Integer> goods;
    private final List<DirectionType> protectedSides;

    public Ship(Optional<Component>[][] dashboard, List<Component> discards, Component[] reserves) {
        this.dashboard = dashboard;
        this.discards = discards;
        this.reserves = reserves;
        this.crew = 0;
        this.batteries = 0;
        this.goods = new HashMap<>();
        this.protectedSides = new ArrayList<>();
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
        return l.stream()
                .mapToInt(e -> {
                        if (e.getIsDouble()) {
//                            response = ?    ask if player wants to activate the double engine
//                            return (response) ? 2 : 0;
                            if (getBatteries()-1 >= 0) {
                                setBatteries(getBatteries()-1);
                                return 2;
                            }
                            return 0;
                        }
                        return 1;
                })   // missing the interaction of player
                .sum();
    }

    public double calcFirePower(List<CannonComponent> l) {
        return l.stream()
                .mapToDouble(c -> {
//                    if (c.getIsDouble()) {
//                        response = ?     ask if player wants to activate the double cannon
//                        if (response) return c.getDirection() == DirectionType.NORTH ? 2 : 1;
//                    }
                    if (c.getIsDouble()) {
                        if (getBatteries()-1 >= 0) {
                            setBatteries(getBatteries()-1);
                            return c.getDirection() == DirectionType.NORTH ? 2 : 1;
                        }
                        return 0;
                    }
                    return c.getDirection() == DirectionType.NORTH ? 1 : 0.5;
                })
                .sum();
    }

}
