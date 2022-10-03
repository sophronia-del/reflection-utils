package indi.sophronia.util.reflection.types;

public abstract class Drived<K, V, A, B> extends Base {
    @Override
    public Object put(Object key, Object value) {
        return super.put((Number) key, (Integer) value);
    }

    @Override
    public Integer put(Number key, Integer value) {
        return super.put(key, value);
    }

    @Override
    public Object getV(Object v, Number o) {
        return super.getV(v, o);
    }

    public K getV(int v, Object o) {
        return null;
    }

    public V getK() {
        return null;
    }

    public Number getA() {
        return null;
    }
}
