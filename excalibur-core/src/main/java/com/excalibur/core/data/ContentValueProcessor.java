package com.excalibur.core.data;

import android.content.ContentValues;
import android.database.Cursor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nd.hy.android.core.util.Ln;
import com.nd.hy.android.core.util.ObjectUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * ContentValueProcessor
 * Date: 14-2-18
 *
 * @author Yangz
 */
public class ContentValueProcessor {

    private static ObjectMapper om = ObjectUtils.getMapperInstance();

    public static ContentValues toContentValues(Object data) {
        ContentValues values = new ContentValues();
        appendValues(data.getClass(), values, data);
        return values;
    }

    public static void appendValues(Class<?> clazz, ContentValues values, Object data) {
        if (clazz == Object.class) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    try {
                        field.setAccessible(true);
                        appendValue(values, column.value(), field.getType(), field.get(data), column.isJsonText());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        appendValues(clazz.getSuperclass(), values, data);
    }

    private static long getSecondMillis(Date date) {
        long time = date.getTime();
        if (time < 9999999999l) {
            return time * 1000;
        }
        return time;
    }

    private static void appendValue(ContentValues values, String name, Class<?> type, Object obj, boolean isJson) {
        if (isJson) {
            try {
                values.put(name, om.writeValueAsString(obj));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } else if (type.isAssignableFrom(int.class)) {
            values.put(name, (Integer) obj);
        } else if (type.isAssignableFrom(String.class)) {
            values.put(name, (String) obj);
        } else if (type.isAssignableFrom(Date.class)) {
            long time = getSecondMillis((Date) obj);
            values.put(name, String.valueOf(time));
        } else if (type.isAssignableFrom(byte.class)) {
            values.put(name, (Byte) obj);
        } else if (type.isAssignableFrom(float.class)) {
            values.put(name, (Float) obj);
        } else if (type.isAssignableFrom(short.class)) {
            values.put(name, (Short) obj);
        } else if (type.isAssignableFrom(double.class)) {
            values.put(name, (Double) obj);
        } else if (type.isAssignableFrom(long.class)) {
            values.put(name, (Long) obj);
        } else if (type.isEnum()) {
            values.put(name, ((Enum) obj).ordinal());
        }
    }

    /**
     * clazz的类必须有一个公共的无参构造函数
     *
     * @param cursor
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> fromCursorToList(Cursor cursor, Class<T> clazz) {
        List<T> array = new ArrayList<T>();
        if (cursor != null && cursor.moveToFirst()) {
            try {
                HashMap<String, JavaType> typeMap = new HashMap<String, JavaType>();
                do {
                    T target = clazz.newInstance();
                    fromCursor(cursor, target, typeMap);
                    array.add(target);
                } while (cursor.moveToNext());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    public static <T> void fromCursor(Cursor cursor, T target) {
        fromCursor(target.getClass(), cursor, target, null);
    }

    public static <T> void fromCursor(Cursor cursor, T target, HashMap<String, JavaType> typeMap) {
        fromCursor(target.getClass(), cursor, target, typeMap);
    }

    public static <T> void fromCursor(Class clazz, Cursor cursor, T target, HashMap<String, JavaType> typeMap) {
        if (clazz == Object.class) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    String name = column.value();
                    Object value = null;
                    if (column.isJsonText()) {
                        if (!column.collection().equals(Object.class)) {
                            JavaType javaType = getJavaType(typeMap, column, name);
                            value = readValue(javaType, cursor, column);
                        }
                    }
                    if (value == null) {
                        value = readValue(field.getType(), cursor, column);
                    }
                    safeSetField(target, field, value);
                }
            }
        }
        fromCursor(clazz.getSuperclass(), cursor, target, typeMap);
    }

    private static JavaType getJavaType(HashMap<String, JavaType> typeMap, Column column, String name) {
        JavaType javaType;
        if (typeMap == null) {
            javaType = ObjectUtils.constructParametricType(column.collection(), column.element());
        } else {
            javaType = typeMap.get(name);
            if (javaType == null) {
                javaType = ObjectUtils.constructParametricType(column.collection(), column.element());
                typeMap.put(name, javaType);
            }
        }
        return javaType;
    }

    public static Object readValue(JavaType type, Cursor cursor, Column column) {
        int index = cursor.getColumnIndex(column.value());
        String content = cursor.getString(index);
        try {
            Ln.d("%s:%s", type.toString(), content);
            return om.readValue(content, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object readValue(Class<?> type, Cursor cursor, Column column) {
        int index = cursor.getColumnIndex(column.value());
        if (column.isJsonText()) {
            try {
                return om.readValue(cursor.getString(index), type);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        } else if (type.isAssignableFrom(int.class)) {
            return cursor.getInt(index);
        } else if (type.isAssignableFrom(String.class)) {
            return cursor.getString(index);
        } else if (type.isAssignableFrom(Date.class)) {
            return readDateFromString(cursor.getString(index));
        } else if (type.isAssignableFrom(byte.class)) {
            return cursor.getInt(index);
        } else if (type.isAssignableFrom(float.class)) {
            return cursor.getFloat(index);
        } else if (type.isAssignableFrom(short.class)) {
            return cursor.getShort(index);
        } else if (type.isAssignableFrom(double.class)) {
            return cursor.getDouble(index);
        } else if (type.isAssignableFrom(long.class)) {
            return cursor.getLong(index);
        }
        return null;
    }

    private static Date readDateFromString(String time) {
        try {
            long t = Long.parseLong(time);
            return new Date(t);
        } catch (NumberFormatException e) {
            Ln.e(e, e.getMessage());
        }
        return null;
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
