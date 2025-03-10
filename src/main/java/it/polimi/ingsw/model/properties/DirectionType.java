package it.polimi.ingsw.model.properties;

import it.polimi.ingsw.model.components.Component;
import java.util.Optional;

public enum DirectionType {
    NORTH {
        @Override
        public Optional<Component> firstComponentFromThisDirection(Optional<Component>[][] dashboard, int direction) {
            if (direction < 4 || direction > 10) return Optional.empty();
            direction -= 4;
            for (Optional<Component>[] row : dashboard)
                if (row[direction].isPresent())
                    return row[direction];

            return Optional.empty();
        }
    },

    EAST {
        @Override
        public Optional<Component> firstComponentFromThisDirection(Optional<Component>[][] dashboard, int direction) {
            if (direction < 5 || direction > 9) return Optional.empty();
            direction -= 5;
            for (int col = dashboard[direction].length-1; col >= 0; col--)
                if (dashboard[direction][col].isPresent())
                    return dashboard[direction][col];

            return Optional.empty();
        }
    },

    SOUTH {
        @Override
        public Optional<Component> firstComponentFromThisDirection(Optional<Component>[][] dashboard, int direction) {
            if (direction < 4 || direction > 10) return Optional.empty();
            direction -= 4;
            for (int row = dashboard.length-1; row >= 0; row--)
                if (dashboard[row][direction].isPresent())
                    return dashboard[row][direction];

            return Optional.empty();
        }
    },

    WEST {
        @Override
        public Optional<Component> firstComponentFromThisDirection(Optional<Component>[][] dashboard, int direction) {
            if (direction < 5 || direction > 9) return Optional.empty();
            direction -= 5;
            for (int col=0; col < dashboard[direction].length; col++)
                if (dashboard[direction][col].isPresent())
                    return dashboard[direction][col];

            return Optional.empty();
        }
    };

    public abstract Optional<Component> firstComponentFromThisDirection(Optional<Component>[][] dashboard, int direction);

}
