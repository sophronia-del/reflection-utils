package indi.sophronia.util.reflection;

import java.lang.reflect.Type;

/**
 * Provides mapping of type parameter between
 * drived class with its generic super class or interface
 */
public class GenericTypeMapping {
    /**
     * If a type parameter of <strong>base class</strong> is not static defined,
     * index value of the type parameter means its index in <strong>drived class</strong>
     */
    private final int drivedIndex;

    /**
     * Static defined type parameter
     */
    private final Type staticType;

    public GenericTypeMapping(GenericTypeMapping genericTypeMapping) {
        this.drivedIndex = genericTypeMapping.drivedIndex;
        this.staticType = genericTypeMapping.staticType;
    }

    public GenericTypeMapping(int drivedIndex) {
        this.drivedIndex = drivedIndex;
        this.staticType = null;
    }

    public GenericTypeMapping(Type staticType, int drivedIndex) {
        this.staticType = staticType;
        this.drivedIndex = drivedIndex;
    }

    public boolean generic() {
        return staticType != null || drivedIndex >= 0;
    }

    public boolean instantiated() {
        return staticType != null;
    }

    /**
     * @return
     * This method returns a {@link java.lang.reflect.Type Type} object, with specific type when requirement meets:
     * <p>{@link java.lang.Class Class} if type parameter is a normal class, like Integer
     * <p>{@link java.lang.reflect.ParameterizedType ParameterizedType}
     * if type parameter is a <strong>parameterizable type with provided parameters</strong>, like Class&lt;Integer&gt;.
     * In this case, actual parameters can be generic variables, when their declarer is generic
     * <p>{@link java.lang.reflect.TypeVariable TypeVariable} /
     * {@link java.lang.reflect.GenericArrayType GenericArrayType}
     * if type parameter is a variable declared by generic methods or other type parameter providers
     */
    public Type instanceType() {
        return staticType;
    }

    public int index() {
        return drivedIndex;
    }

    @Override
    public String toString() {
        return staticType != null ? staticType.toString() : String.valueOf(drivedIndex);
    }
}
