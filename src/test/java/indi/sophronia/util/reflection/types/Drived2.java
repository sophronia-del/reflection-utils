package indi.sophronia.util.reflection.types;

public class Drived2 <K, V, A, B> extends Base<V, K, Double> {
    @Override
    public Integer put(Double key, Integer value) {
        return super.put(key, value);
    }

    @Override
    public K getV(V v, Double o) {
        return super.getV(v, o);
    }

    public V getK() {
        return null;
    }

    public Double getA() {
        return null;
    }
}
