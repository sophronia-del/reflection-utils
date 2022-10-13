package indi.sophronia.util.reflection;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * Reflection metadata records all super classes, all interfaces
 * and mapping of type parameters to each generic super class / interface
 * for provided class
 */
public class ReflectionMetaData {
    private static final ConcurrentMap<Class<?>, ReflectionMetaData> cache = new ConcurrentHashMap<>();

    private final Class<?> type;
    private final Class<?>[] superClasses;
    private final Class<?>[] interfaces;

    /**
     * The map to save mapping relations of generic type parameters between current {@link #type} and
     * super classes and interfaces
     */
    private final Map<Class<?>, GenericTypeMapping[]> typeParameterMap = new LinkedHashMap<>();

    private final TypeVariable<?>[] typeVariables;

    private ReflectionMetaData(Class<?> clazz) {
        this.type = clazz;

        int genericTypeParameterCount = clazz.getTypeParameters().length;
        typeVariables = new TypeVariable[genericTypeParameterCount];
        GenericTypeMapping[] baseMapping = new GenericTypeMapping[genericTypeParameterCount];
        for (int i = 0; i < genericTypeParameterCount; i++) {
            baseMapping[i] = new GenericTypeMapping(i);
            typeVariables[i] = clazz.getTypeParameters()[i];
        }
        typeParameterMap.put(clazz, baseMapping);

        List<Class<?>> superClasses = new ArrayList<>();
        Set<Class<?>> interfaces = new LinkedHashSet<>();
        for (Type i : clazz.getGenericInterfaces()) {
            ParameterizedType genericInterface = i instanceof ParameterizedType ? (ParameterizedType) i : null;
            Class<?> ii = (Class<?>) (genericInterface != null ? genericInterface.getRawType() : i);
            ReflectionMetaData data = register(ii);
            if (genericInterface != null) {
                handleGenericSuper(data, genericInterface.getActualTypeArguments());
            }
            interfaces.add(ii);
            interfaces.addAll(Arrays.asList(data.interfaces));
        }

        Type superType = clazz.getGenericSuperclass();
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            superClasses.add(superClass);
            ReflectionMetaData superData = register(superClass);
            if (superType instanceof ParameterizedType) {
                handleGenericSuper(superData, ((ParameterizedType) superType).getActualTypeArguments());
            } else if (superClass.getTypeParameters().length > 0) {
                handleRawGenericSuper(superData);
            }
            superClasses.addAll(Arrays.asList(superData.superClasses));
            interfaces.addAll(Arrays.asList(superData.interfaces));
        }

        this.superClasses = superClasses.toArray(new Class[0]);
        this.interfaces = interfaces.toArray(new Class[0]);
    }

    private void handleGenericSuper(ReflectionMetaData superData, Type[] actualTypeArguments) {
        Type[] currentTypeParameters = type.getTypeParameters();
        GenericTypeMapping[] superMapping = new GenericTypeMapping[actualTypeArguments.length];
        for (int i = 0; i < actualTypeArguments.length; i++) {
            boolean asSuperParameter = false;
            for (int j = 0; j < currentTypeParameters.length; j++) {
                if (currentTypeParameters[j].equals(actualTypeArguments[i])) {
                    asSuperParameter = true;
                    superMapping[i] = new GenericTypeMapping(j);
                    break;
                }
            }
            if (!asSuperParameter) {
                superMapping[i] = new GenericTypeMapping((Class<?>) actualTypeArguments[i]);
            }
        }
        typeParameterMap.put(superData.type, superMapping);

        // success from super class
        for (Map.Entry<Class<?>, GenericTypeMapping[]> entry : superData.typeParameterMap.entrySet()) {
            GenericTypeMapping[] genericTypeMappings = entry.getValue();
            GenericTypeMapping[] mappings = new GenericTypeMapping[genericTypeMappings.length];
            for (int i = 0; i < genericTypeMappings.length; i++) {
                // static type parameters depends on nothing
                if (genericTypeMappings[i].instantiated()) {
                    mappings[i] = new GenericTypeMapping(genericTypeMappings[i]);
                    continue;
                }
                // otherwise, replace them by mapping of super class
                int indexInSuperClass = genericTypeMappings[i].index();
                mappings[i] = new GenericTypeMapping(superMapping[indexInSuperClass]);
            }
            typeParameterMap.put(entry.getKey(), mappings);
        }
    }

    /**
     * If base class is raw of generic type, decay all type parameters
     * to their bound
     */
    private void handleRawGenericSuper(ReflectionMetaData superData) {
        for (Map.Entry<Class<?>, GenericTypeMapping[]> entry : superData.typeParameterMap.entrySet()) {
            GenericTypeMapping[] decayedMappings = Arrays.copyOf(entry.getValue(), entry.getValue().length);
            for (int i = 0; i < decayedMappings.length; i++) {
                decayedMappings[i] = new GenericTypeMapping((Class<?>)
                        entry.getKey().getTypeParameters()[i].getBounds()[0]);
            }
            this.typeParameterMap.put(entry.getKey(), decayedMappings);
        }
    }

    public static ReflectionMetaData register(Class<?> clazz) {
        return register(clazz, ReflectionMetaData::new);
    }

    public static ReflectionMetaData register(Class<?> clazz,
                                              Function<Class<?>, ReflectionMetaData> supplier) {
        ReflectionMetaData data = cache.get(clazz);
        if (data != null) {
            return data;
        }
        synchronized (ReflectionMetaData.class) {
            data = cache.get(clazz);
            if (data != null) {
                return data;
            }
            data = supplier.apply(clazz);
            cache.put(clazz, data);
            return data;
        }
    }

    public Class<?>[] getSuperClasses() {
        return superClasses;
    }

    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    public int indexOfTypeVariable(TypeVariable<?> typeVariable) {
        for (int i = 0; i < typeVariables.length; i++) {
            if (typeVariables[i].equals(typeVariable)) {
                return i;
            }
        }
        throw new IllegalArgumentException("incorrect typeVariable: " + typeVariable);
    }

    public GenericTypeMapping actualTypeParameter(Class<?> parameterizedBase, int baseIndex) {
        GenericTypeMapping[] mappings = typeParameterMap.get(parameterizedBase);
        if (mappings == null) {
            throw new IllegalArgumentException(parameterizedBase.getName() + " is not a generic type");
        }
        return mappings[baseIndex];
    }
}
