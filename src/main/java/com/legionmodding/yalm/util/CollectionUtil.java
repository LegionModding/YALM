package com.legionmodding.yalm.util;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

import com.legionmodding.yalm.reflection.TypeUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

public class CollectionUtil
{
    private static Class<?> findTypeFromGenericInterface(Class<?> cls)
    {
        final TypeToken<?> token = TypeToken.of(cls);
        final TypeToken<?> typeB = token.resolveType(TypeUtil.FUNCTION_B_PARAM);
        if (typeB.getType() instanceof Class<?>)
        {
            return typeB.getRawType();
        }

        return null;
    }

    private static Class<?> findTypeFromMethod(Class<?> cls)
    {
        for (Method m : cls.getDeclaredMethods())
        {
            if (m.getName().equals("apply"))
            {
                final Class<?>[] parameterTypes = m.getParameterTypes();

                if (parameterTypes.length == 1)
                {
                    final Class<?> parameterType = parameterTypes[0];
                    if (parameterType != Object.class)
                    {
                        return parameterType;
                    }
                }
            }
        }

        return null;
    }

    private static <A, B> Object allocateArray(Function<A, B> transformer, final int length)
    {
        final Class<?> transformerCls = transformer.getClass();
        Class<?> componentType = findTypeFromGenericInterface(transformerCls);

        if (componentType == null)
        {
            componentType = findTypeFromMethod(transformerCls);
        }

        Preconditions.checkState(componentType != null, "Failed to find type for class %s", transformer);
        return Array.newInstance(componentType, length);
    }

    private static <B, A> void transform(A[] input, Function<A, B> transformer, final Object result)
    {
        for (int i = 0; i < input.length; i++)
        {
            final B o = transformer.apply(input[i]);
            Array.set(result, i, o);
        }
    }

    @SuppressWarnings("unchecked")
    public static <A, B> B[] transform(A[] input, Function<A, B> transformer)
    {
        final Object result = allocateArray(transformer, input.length);
        transform(input, transformer, result);
        return (B[])result;
    }

    @SuppressWarnings("unchecked")
    public static <A, B> B[] transform(Class<? extends B> cls, A[] input, Function<A, B> transformer)
    {
        final Object result = Array.newInstance(cls, input.length);
        transform(input, transformer, result);
        return (B[])result;
    }

    @SuppressWarnings("unchecked")
    public static <A, B> B[] transform(Collection<A> input, Function<A, B> transformer)
    {
        final Object result = allocateArray(transformer, input.size());

        int i = 0;

        for (A a : input)
        {
            final B o = transformer.apply(a);
            Array.set(result, i++, o);
        }

        return (B[])result;
    }

    public static <K, V> void putOnce(Map<K, V> map, K key, V value)
    {
        final V prev = map.put(key, value);
        Preconditions.checkState(prev == null, "Duplicate value on key %s: %s -> %s", key, prev, value);
    }

    public static <T> Set<T> asSet(Optional<T> value)
    {
        return value.map(Collections::singleton).orElseGet(Collections::emptySet);
    }
}
