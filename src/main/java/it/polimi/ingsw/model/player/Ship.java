package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.properties.DirectionType;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Ship {
    private final Optional<Component>[][] dashboard;
    private final List<Optional<Component>> discards;
    private final Optional<Component> reserves;
    private int crew;
    private int batteries;
    private final Map<ColorType, Integer> goods;
    private final List<DirectionType> protectedSides;

    public Ship(Optional<Component>[][] dashboard, List<Optional<Component>> discards, Optional<Component> reserves, int crew, int batteries, Map<ColorType, Integer> goods, List<DirectionType> protectedSides) {
        this.dashboard = dashboard;
        this.discards = discards;
        this.reserves = reserves;
        this.crew = crew;
        this.batteries = batteries;
        this.goods = goods;
        this.protectedSides = protectedSides;
    }



    public Optional<Component>[][] getDashboard() {
        return dashboard;
    }

    public void destroyComponent(Component component) {

    }

    public List<Optional<Component>> getDiscards() {
        return discards;
    }

    public Optional<Component> getReserves() {
        return reserves;
    }

    public int countExposedConnectors() {
        AtomicInteger exposedConnectors = new AtomicInteger();
        for (Optional<Component>[] row : dashboard) {
            for (Optional<Component> componentOpt : row) {
                componentOpt.ifPresent((Component component) -> {
                    if (component.getConnectors()[0] != ConnectorType.EMPTY && (component.getY() == 0 || dashboard[component.getY()-1][component.getX()].isEmpty()))
                        exposedConnectors.getAndIncrement();
                    if (component.getConnectors()[1] != ConnectorType.EMPTY && (component.getX() == 6 || dashboard[component.getY()][component.getX()+1].isEmpty()))
                        exposedConnectors.getAndIncrement();
                    if (component.getConnectors()[2] != ConnectorType.EMPTY && (component.getY() == 4 || dashboard[component.getY()+1][component.getX()].isEmpty()))
                        exposedConnectors.getAndIncrement();
                    if (component.getConnectors()[3] != ConnectorType.EMPTY && (component.getX() == 0 || dashboard[component.getY()][component.getX()-1].isEmpty()))
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

    public List<ShieldComponent> getShields(){
        List<ShieldComponent> shields = new ArrayList<>();
        for(Optional<Component>[] row : dashboard) {
            for(Optional<Component> component : row) {
                if(component.isPresent() && component.get() instanceof ShieldComponent c) {
                    shields.add(c);
                }
            }
        }
        return shields;
    }

    public List<CannonComponent> getCannons(){
        return new ArrayList<>();
    }

    public List<EngineComponent> getEngines(){
        return new ArrayList<>();
    }

    public List<CabinComponent> getCabines(){
        List<CabinComponent> cabines = new ArrayList<>();
        for (Optional<Component>[] row : dashboard) {
            for (Optional<Component> component : row) {
                if (component.isPresent() && component.get() instanceof CabinComponent c) {
                    cabines.add(c);
                }
            }
        }
        return cabines;
    }

}

