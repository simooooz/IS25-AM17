package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.components.*;
import it.polimi.ingsw.model.game.objects.ColorType;
import it.polimi.ingsw.model.game.objects.Good;
import it.polimi.ingsw.model.properties.DirectionType;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.List;

public class Ship {
    private final Optional<Component>[][] dashboard;
    private final List<Optional<Component>> descards;
    private final Optional<Component> reserves;
    private int crew;
    private int batteries;
    private final Map<ColorType, Integer> goods;
    private final List<DirectionType> protectedSides;

    public Ship(Optional<Component>[][] dashboard, List<Optional<Component>> descards, Optional<Component> reserves, int crew, int batteries, Map<ColorType, Integer> goods, List<DirectionType> protectedSides) {
        this.dashboard = dashboard;
        this.descards = descards;
        this.reserves = reserves;
        this.crew = crew;
        this.batteries = batteries;
        this.goods = goods;
        this.protectedSides = protectedSides;
    }



    public Optional<Component>[][] getDashboard() {
        return dashboard;
    }

    public void insertComponent(Component component) {

    }

    public void destroyComponent(Component component) {

    }

    public List<Optional<Component>> getDescards() {
        return descards;
    }

    public Optional<Component> getReserves() {
        return reserves;
    }

    public int countExposedConnectors(){
        return 0;
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
                if(component.isPresent() && component.get() instanceof ShieldComponent) {
                    ShieldComponent c = (ShieldComponent) component.get();
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
        for(Optional<Component>[] row : dashboard) {
            for(Optional<Component> component : row) {
                if(component.isPresent() && component.get() instanceof CabinComponent) {
                    CabinComponent c = (CabinComponent) component.get();
                    cabines.add(c);
                }
            }
        }
        return cabines;
    }

}

