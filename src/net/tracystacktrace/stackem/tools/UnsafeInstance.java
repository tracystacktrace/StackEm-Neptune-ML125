package net.tracystacktrace.stackem.tools;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public final class UnsafeInstance {
    private static Unsafe instance;

    public static Unsafe getUnsafe() {
        if (instance == null) {
            try {
                final Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                instance = (Unsafe) field.get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                System.out.println("Failed to access the sun.misc.Unsafe instance: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public static <T, R> R getObject(Class<T> targetClass, T targetObject, String fieldName) {
        try {
            final Field targetField = targetClass.getDeclaredField(fieldName);
            final long fieldOffset = targetObject != null ? getUnsafe().objectFieldOffset(targetField) : getUnsafe().staticFieldOffset(targetField);

            return (R) getUnsafe().getObject(targetObject != null ? targetObject : getUnsafe().staticFieldBase(targetField), fieldOffset);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
