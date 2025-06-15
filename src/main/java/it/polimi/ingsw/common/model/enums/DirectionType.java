package it.polimi.ingsw.common.model.enums;

import it.polimi.ingsw.model.components.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public enum DirectionType {
    NORTH {
        @Override
        public List<Component> getComponentsFromThisDirection(Optional<Component>[][] dashboard, int direction) {
            List<Component> components = new ArrayList<>();
            if (direction < 4 || direction > 10) return components;
            direction -= 4;

            for (Optional<Component>[] row : dashboard)
                row[direction].ifPresent(components::add);

            return components;
        }
    },

    EAST {
        @Override
        public List<Component> getComponentsFromThisDirection(Optional<Component>[][] dashboard, int direction) {
            List<Component> components = new ArrayList<>();
            if (direction < 5 || direction > 9) return components;
            direction -= 5;

            for (int col = dashboard[direction].length-1; col >= 0; col--)
                dashboard[direction][col].ifPresent(components::add);

            return components;
        }
    },

    SOUTH {
        @Override
        public List<Component> getComponentsFromThisDirection(Optional<Component>[][] dashboard, int direction) {
            List<Component> components = new ArrayList<>();
            if (direction < 4 || direction > 10) return components;
            direction -= 4;

            for (int row = dashboard.length-1; row >= 0; row--)
                dashboard[row][direction].ifPresent(components::add);

            return components;
        }
    },

    WEST {
        @Override
        public List<Component> getComponentsFromThisDirection(Optional<Component>[][] dashboard, int direction) {
            List<Component> components = new ArrayList<>();
            if (direction < 5 || direction > 9) return components;
            direction -= 5;

            for (int col=0; col < dashboard[direction].length; col++)
                dashboard[direction][col].ifPresent(components::add);

            return components;
        }
    };

    public abstract List<Component> getComponentsFromThisDirection(Optional<Component>[][] dashboard, int direction);

}
