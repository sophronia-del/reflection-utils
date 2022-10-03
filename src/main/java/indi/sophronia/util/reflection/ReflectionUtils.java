package indi.sophronia.util.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.function.Predicate;

public class ReflectionUtils {
    public static Class<?> findRequiredInterface(Class<?> base, Predicate<Class<?>> requirement) {
        ReflectionMetaData data = ReflectionMetaData.register(base);
        if (base.isInterface() && requirement.test(base)) {
            return base;
        }
        for (Class<?> i : data.getInterfaces()) {
            if (requirement.test(i)) {
                return i;
            }
        }
        return null;
    }

    /**
     * @return true if base method is overridden by drived
     */
    public static boolean overrides(Method base, Method drived) {
        ReflectionMetaData baseData = ReflectionMetaData.register(base.getDeclaringClass());
        ReflectionMetaData drivedData = ReflectionMetaData.register(drived.getDeclaringClass());

        if (base.getParameterCount() != drived.getParameterCount()) {
            return false;
        }
        if (!base.getDeclaringClass().isAssignableFrom(drived.getDeclaringClass())) {
            return false;
        }
        if (!base.getName().equals(drived.getName())) {
            return false;
        }

        for (int i = 0; i < base.getParameterCount(); i++) {
            Type baseGenType = base.getGenericParameterTypes()[i];
            Type drivedGenType = drived.getGenericParameterTypes()[i];

            // unmatched non-generic parameter type
            if (baseGenType instanceof Class && !baseGenType.equals(drivedGenType)) {
                return false;
            }

            if (baseGenType instanceof TypeVariable) {
                int indexInBase = baseData.indexOfTypeVariable((TypeVariable<?>) baseGenType);
                GenericTypeMapping inDrived = drivedData.actualTypeParameter(base.getDeclaringClass(), indexInBase);

                // check if implemented type parameter matches
                if (drivedGenType instanceof Class &&
                        !drivedGenType.equals(inDrived.staticType)) {
                    return false;
                }

                // check if type parameter matches
                if (drivedGenType instanceof TypeVariable) {

                    int indexInDrived2 = drivedData.indexOfTypeVariable((TypeVariable<?>) drivedGenType);
                    if (inDrived.drivedIndex != indexInDrived2) {
                        return false;
                    }
                }
            }
        }

        // other cases will cause compiling error
        return true;
    }

    public static Method findRequiredMethod(Class<?> clazz, Predicate<Method> requirement) {
        Method m = findDeclaredMethod(clazz, requirement);
        if (m != null) {
            return m;
        }
        ReflectionMetaData data = ReflectionMetaData.register(clazz);
        for (Class<?> superClass : data.getSuperClasses()) {
            m = findDeclaredMethod(superClass, requirement);
            if (m != null) {
                return m;
            }
        }
        for (Class<?> anInterface : data.getInterfaces()) {
            m = findDeclaredMethod(anInterface, requirement);
            if (m != null) {
                return m;
            }
        }
        return null;
    }

    public static Method findDeclaredMethod(Class<?> clazz, Predicate<Method> requirement) {
        for (Method declaredMethod : clazz.getDeclaredMethods()) {
            if (requirement.test(declaredMethod)) {
                return declaredMethod;
            }
        }
        return null;
    }

    public static Field findRequiredField(Class<?> clazz, Predicate<Field> requirement) {
        Field f = findDeclaredField(clazz, requirement);
        if (f != null) {
            return f;
        }
        ReflectionMetaData data = ReflectionMetaData.register(clazz);
        for (Class<?> superClass : data.getSuperClasses()) {
            f = findDeclaredField(superClass, requirement);
            if (f != null) {
                return f;
            }
        }
        for (Class<?> anInterface : data.getInterfaces()) {
            f = findDeclaredField(anInterface, requirement);
            if (f != null) {
                return f;
            }
        }
        return null;
    }

    public static Field findDeclaredField(Class<?> clazz, Predicate<Field> requirement) {
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (requirement.test(declaredField)) {
                return declaredField;
            }
        }
        return null;
    }
}
