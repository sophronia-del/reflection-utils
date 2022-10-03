package indi.sophronia.util.reflection.types;

import java.util.HashMap;

public abstract class Base<K, V, A extends Number> extends HashMap<A, Integer> {
    @Override
    public Integer put(A key, Integer value) {
        return super.put(key, value);
    }

    public A getA() {
        return null;
    }

    public K getK() {
        return null;
    }

    public V getV(K k, A a) {
        return null;
    }
}
