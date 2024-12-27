package cn.jgzhan.lrpc.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/18
 */
public class FieldUtils {
    public static void setField(Object bean, Field field, Object value) {
        try {
            setAccessible(field);
            field.set(bean, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getField(Object bean, String fieldName) {
        try {
            var field = bean.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(bean);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("deprecation") // on JDK 9
    public static void setAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()))
                && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

}
