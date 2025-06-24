package it.polimi.ingsw.client.model.cards.utils;

public enum ClientMalusType {

    DAYS {
        @Override
        public String toString() {
            return "📅";
        }
    },

    GOODS {
        @Override
        public String toString() {
            return  "🔲";
        }
    },

    CREW {
        @Override
        public String toString() {
            return "👨";
        }
    }

}