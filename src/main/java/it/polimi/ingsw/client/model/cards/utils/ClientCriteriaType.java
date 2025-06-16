package it.polimi.ingsw.client.model.cards.utils;

public enum ClientCriteriaType {
    CREW {
        @Override
        public String toString() {
            return "↓ 👨";
        }
    },
    CANNON {
        @Override
        public String toString() {
            return "↓ 💥";
        }
    },
    ENGINE {
        @Override
        public String toString() {
            return "↓ 🚀";
        }
    }
}
