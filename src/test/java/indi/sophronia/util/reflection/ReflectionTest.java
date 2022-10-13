package indi.sophronia.util.reflection;

import indi.sophronia.util.reflection.types.Base;
import indi.sophronia.util.reflection.types.Drived;
import indi.sophronia.util.reflection.types.Drived2;

import java.util.Arrays;
import java.util.Map;

public class ReflectionTest {
    public static void main(String[] args) throws NoSuchMethodException {
        Class<?> i = ReflectionUtils.findRequiredInterface(Drived.class, clazz -> true);
        System.out.println(i);

        System.out.println(ReflectionUtils.overrides(Base.class.getDeclaredMethod("getA"),
                Drived.class.getDeclaredMethod("getK")));
        System.out.println(ReflectionUtils.overrides(Base.class.getDeclaredMethod("getA"),
                Drived.class.getDeclaredMethod("getA")));
        System.out.println(ReflectionUtils.overrides(Map.class.getDeclaredMethod("put", Object.class, Object.class),
                Drived.class.getDeclaredMethod("put", Object.class, Object.class)));
        System.out.println(ReflectionUtils.overrides(Base.class.getDeclaredMethod("getV", Object.class, Number.class),
                Drived.class.getDeclaredMethod("getV", Object.class, Number.class)));
        System.out.println(ReflectionUtils.overrides(Base.class.getDeclaredMethod("getV", Object.class, Number.class),
                Drived.class.getDeclaredMethod("getV", int.class, Object.class)));
        System.out.println(ReflectionUtils.overrides(Map.class.getDeclaredMethod("put", Object.class, Object.class),
                Base.class.getDeclaredMethod("put", Number.class, Integer.class)));
        System.out.println(ReflectionUtils.overrides(Base.class.getDeclaredMethod("put", Number.class, Integer.class),
                Drived.class.getDeclaredMethod("put", Object.class, Object.class)));
        System.out.println(ReflectionUtils.overrides(Base.class.getDeclaredMethod("put", Number.class, Integer.class),
                Drived.class.getDeclaredMethod("put", Number.class, Integer.class)));
        System.out.println(ReflectionUtils.overrides(Map.class.getDeclaredMethod("put", Object.class, Object.class),
                Drived.class.getDeclaredMethod("put", Number.class, Integer.class)));
        System.out.println(ReflectionUtils.overrides(Map.class.getDeclaredMethod("put", Object.class, Object.class),
                Drived2.class.getDeclaredMethod("put", Double.class, Integer.class)));

        new Drived<Short, Character, Byte, Float>() {
            {
                ReflectionMetaData data = ReflectionMetaData.register(getClass());
                System.out.println(data.actualTypeParameter(Map.class, 0).instanceType());
                System.out.println(data.actualTypeParameter(Base.class, 1).instanceType());
                System.out.println(data.actualTypeParameter(Base.class, 2).instanceType());
            }
        };

        System.out.println(Arrays.toString(ReflectionUtils.
                findBaseMethods(Drived2.class.getDeclaredMethod("put", Double.class, Integer.class))));
    }
}
