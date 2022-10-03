package indi.sophronia.util.reflection;

/**
 * Provides mapping of type parameter between
 * drived class with its generic super class or interface
 */
public class GenericTypeMapping {
    /**
     * If a type parameter of <strong>base class</strong> is not static defined,
     * index value of the type parameter means its index in <strong>drived class</strong>
     */
    public final int drivedIndex;

    /**
     * Static defined type parameter
     */
    public final Class<?> staticType;

    public GenericTypeMapping(GenericTypeMapping genericTypeMapping) {
        this.drivedIndex = genericTypeMapping.drivedIndex;
        this.staticType = genericTypeMapping.staticType;
    }

    public GenericTypeMapping(int drivedIndex) {
        this.drivedIndex = drivedIndex;
        this.staticType = null;
    }

    public GenericTypeMapping(Class<?> staticType) {
        this.drivedIndex = 0;
        this.staticType = staticType;
    }

    @Override
    public String toString() {
        return staticType != null ? staticType.toString() : String.valueOf(drivedIndex);
    }
}
