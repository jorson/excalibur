package com.excalibur.core.util;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility methods for Objects
 *
 */
public final class ObjectUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private ObjectUtils() {
        // No public constructor
    }

    /**
     * Perform a safe equals between 2 objects.
     * <p/>
     * It manages the case where the first object is null and it would have resulted in a
     * {@link NullPointerException} if <code>o1.equals(o2)</code> was used.
     *
     * @param o1 First object to check.
     * @param o2 Second object to check.
     * @return <code>true</code> if both objects are equal. <code>false</code> otherwise
     * @see Object#equals(Object) uals()
     */
    public static boolean safeEquals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        } else {
            return o1.equals(o2);
        }
    }

    public static <T> T getNoNull(T a, T b) {
        return a != null ? a : b;
    }

    public static ObjectMapper getMapperInstance() {
        return OBJECT_MAPPER;
    }

    public static JavaType constructParametricType(Class<?> collectionClass, Class<?>... elementClasses) {
        return OBJECT_MAPPER.getTypeFactory()
                .constructParametricType(collectionClass, elementClasses);
    }
}
