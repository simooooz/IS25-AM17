package it.polimi.ingsw.view.TUI.graphics;

import it.polimi.ingsw.model.components.utils.ConnectorType;
import it.polimi.ingsw.model.game.objects.AlienType;

import java.util.ArrayList;
import java.util.List;

public class ComponentsTUI {

    public static abstract class ComponentUI {
        protected String id;
        protected boolean isCovered = true;
        // todo: handle connectors print
//        protected ConnectorType[] connectors;

        public ComponentUI(String id/*, ConnectorType[] connectors */) {
            this.id = id;
//            this.connectors = connectors;
        }

        public String getId() {
            return id;
        }

        public void uncover() {
            isCovered = false;
        }

        public String print() {
            return isCovered ? printCovered() : printUncovered();
        }

        /**
         * Generates a representation of a covered card: a bordered
         * frame with the card's ID centered within it.
         *
         * @return rendering of the covered card
         */
        public String printCovered() {
            int width = 8;
            String hBorder = "─";
            String vBorder = "|";
            String[] angles = {"┌", "┐", "└", "┘"};

            List<String> component = new ArrayList<>();

            // top
            component.add(angles[0] + repeat(hBorder, width - 2) + angles[1]);

            // id
            String ID = inTheMiddle(this.id, width - 2);
            component.add(vBorder + ID + vBorder);

            // bottom
            component.add(angles[2] + repeat(hBorder, width - 2) + angles[3]);

            return String.join("\n", component);
        }

        /**
         * Generates a graphical representation of the uncovered card.
         * The symbol is provided by the subclass implementation
         * of the {@code getSimboloDisegno} method.
         *
         * @return a string containing the graphical representation of the uncovered card
         */
        public String printUncovered() {
            int width = 8;
            String hBorder = "─";
            String vBorder = "|";
            String[] angles = {"┌", "┐", "└", "┘"};

            List<String> component = new ArrayList<>();

            // top
            component.add(angles[0] + repeat(hBorder, width - 2) + angles[1]);

            // icon
            String icon = inTheMiddle(getIcon(), width - 2);
            component.add(vBorder + icon + vBorder);

            // bottom
            component.add(angles[2] + repeat(hBorder, width - 2) + angles[3]);

            return String.join("\n", component);
        }

        /**
         * Retrieves the icon associated with this component.
         *
         * @return a string representing the icon for the component
         */
        protected abstract String getIcon();

        @Override
        public String toString() {
            return printUncovered();
        }

        // utility to repeat a string n times
        private String repeat(String str, int n) {
            return String.valueOf(str).repeat(Math.max(0, n));
        }

        // display: flex, align-items: center, justify-content: center (lol)
        private String inTheMiddle(String text, int width) {
            if (text.length() >= width) {
                return text.substring(0, width);
            }

            int leftPadding = (width - text.length()) / 2;
            int rightPadding = width - text.length() - leftPadding;

            return repeat(" ", leftPadding) + text + repeat(" ", rightPadding);
        }
    }

    public static class Component extends ComponentUI {
        public Component(String id) { super(id); }

        @Override
        protected String getIcon() {
            return "connector";
        }
    }

    public static class BatteryComponent extends ComponentUI {
        private final boolean isTriple;
        public BatteryComponent(String id, boolean isTriple) {
            super(id);
            this.isTriple = isTriple;
        }

        @Override
        protected String getIcon() {
            return isTriple ? "🔋🔋🔋" : "🔋🔋";
        }
    }

    public static class CabinComponent extends ComponentUI {
        public CabinComponent(String id) {
            super(id);
        }

        @Override
        protected String getIcon() {
            return "cabin";
        }
    }

    public static class CannonComponent extends ComponentUI {
        private final boolean isDouble;
        public CannonComponent(String id, boolean isDouble) {
            super(id);
            this.isDouble = isDouble;
        }

        @Override
        protected String getIcon() {
            return isDouble ? "🔫🔫" : "🔫" ;
        }
    }

    public static class CargoHoldsComponent extends ComponentUI {
        public CargoHoldsComponent(String id) {
            super(id);
        }

        @Override
        protected String getIcon() {
            return "[]";
        }
    }

    public static class EngineComponent extends ComponentUI {
        private final boolean isDouble;
        public EngineComponent(String id, boolean isDouble) {
            super(id);
            this.isDouble = isDouble;
        }

        @Override
        protected String getIcon() {
            return isDouble ? "⚙️⚙️" : "⚙️";
        }
    }

    public static class OddComponent extends ComponentUI {
        private final AlienType alien;
        public OddComponent(String id, AlienType alien) {
            super(id);
            this.alien = alien;
        }

        @Override
        protected String getIcon() {
            return alien.equals(AlienType.ENGINE) ? "👽⚙️" : "👽🔫";
        }
    }

    public static class ShieldComponent extends ComponentUI {
        public ShieldComponent(String id) {
            super(id);
        }

        @Override
        protected String getIcon() {
            return "🛡";
        }
    }

    public static class SpecialCargoHoldsComponent extends ComponentUI {
        public SpecialCargoHoldsComponent(String id) {
            super(id);
        }

        @Override
        protected String getIcon() {
            return "🟥";
        }
    }

    // utility to print a grid of components
    public static String gridOfComponents(List<ComponentUI> components, int componentsPerRow) {
        StringBuilder output = new StringBuilder();

        List<List<ComponentUI>> componentsRows = new ArrayList<>();
        for (int i = 0; i < components.size(); i += componentsPerRow) {
            int end_row = Math.min(i + componentsPerRow, components.size());
            componentsRows.add(components.subList(i, end_row));
        }

        for (List<ComponentUI> row : componentsRows) {
            List<String[]> printed = new ArrayList<>();
            for (ComponentUI component : row) {
                printed.add(component.print().split("\n"));
            }

            int height = printed.getFirst().length;

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < printed.size(); j++) {
                    output.append(printed.get(j)[i]);
                    if (j < printed.size() - 1) {
                        output.append("  ");
                    }
                }
                output.append("\n");
            }
        }

        return output.toString();
    }

}
