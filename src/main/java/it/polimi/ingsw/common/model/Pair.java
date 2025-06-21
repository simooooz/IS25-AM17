package it.polimi.ingsw.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Pair<K, V> {

    @JsonProperty private K key;
    @JsonProperty private V value;

    public Pair() {}

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pair<?, ?> that = (Pair<?, ?>) obj;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

}
