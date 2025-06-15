package it.polimi.ingsw.common.model.enums;

import it.polimi.ingsw.view.TUI.Chroma;

public enum ColorType {
    RED {
        @Override
        public String toString() {
            return Chroma.color("  " , Chroma.RED_BACKGROUND);
        }
    },
    YELLOW {
        @Override
        public String toString() {
            return Chroma.color("  " , Chroma.YELLOW_BACKGROUND);
        }
    },
    GREEN {
        @Override
        public String toString() {
            return Chroma.color("  " , Chroma.GREEN_BACKGROUND);
        }
    },
    BLUE {
        @Override
        public String toString() {
            return Chroma.color("  " , Chroma.BLUE_BACKGROUND);
        }
    }
}