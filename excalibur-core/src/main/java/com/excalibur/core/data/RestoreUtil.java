package com.excalibur.core.data;

import android.os.Bundle;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * @version 14-5-21
 */
public final class RestoreUtil {

    public static void saveState(Bundle outState, Object data) {
        saveValues(data.getClass(), outState, data);
    }

    public static void loadState(Bundle saveState, Object data) {
        if (saveState == null) {
            return;
        }
        readValues(data.getClass(), saveState, data);
    }

    public static void saveValues(Class<?> clazz, Bundle outState, Object data) {
        if (clazz == Object.class) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                Restore restore = field.getAnnotation(Restore.class);
                if (restore != null) {
                    try {
                        field.setAccessible(true);
                        saveValue(outState, getRestoreValue(restore, field.getName()),
                                field.getType(), field.get(data));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        saveValues(clazz.getSuperclass(), outState, data);
    }

    public static void saveValue(Bundle bundle, String name, Class<?> type, Object obj) {
        if (obj == null) {
            return;
        }
        if (type.isAssignableFrom(int.class)) {
            bundle.putInt(name, (Integer) obj);
        } else if (type.isAssignableFrom(String.class)) {
            bundle.putString(name, (String) obj);
        } else if (type.isEnum()) {
            bundle.putSerializable(name, (Enum) obj);
        } else if (Serializable.class.isAssignableFrom(obj.getClass())) {
            bundle.putSerializable(name, (Serializable) obj);
        } else if (type.isAssignableFrom(byte.class)) {
            bundle.putByte(name, (Byte) obj);
        } else if (type.isAssignableFrom(float.class)) {
            bundle.putFloat(name, (Float) obj);
        } else if (type.isAssignableFrom(short.class)) {
            bundle.putShort(name, (Short) obj);
        } else if (type.isAssignableFrom(double.class)) {
            bundle.putDouble(name, (Double) obj);
        } else if (type.isAssignableFrom(long.class)) {
            bundle.putLong(name, (Long) obj);
        }
    }

    public static void readValues(Class<?> clazz, Bundle saveState, Object data) {
        if (clazz == Object.class) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                Restore restore = field.getAnnotation(Restore.class);
                if (restore != null) {
                    Object value = loadValue(saveState, getRestoreValue(restore, field.getName()));
                    safeSetField(data, field, value);
                }
            }
        }
        readValues(clazz.getSuperclass(), saveState, data);
    }

    private static String getRestoreValue(Restore restore, String fieldName) {
        if (Restore.USE_DEFAULT_NAME.equals(restore.value())) {
            return fieldName;
        }
        return restore.value();
    }

    public static Object loadValue(Bundle bundle, String name) {
        if (!bundle.containsKey(name)) {
            return null;
        }
        return bundle.get(name);
    }

    private static void safeSetField(Object target, Field field, Object value) {
        try {
            if (value != null) {
                field.setAccessible(true);
                field.set(target, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
