package cn.jgzhan.lrpc.example.common.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/18
 */
public interface SingletonUtils {

    Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();


    /**
     * 获取单例对象
     * 注：以第一次调用时的参数为准
     *
     * @param clazz 类
     * @param args  构造函数参数
     * @return 单例对象
     */
    static <T> T getSingleton(Class<T> clazz, Object... args) {
        final String key = clazz.toString();
        if (OBJECT_MAP.containsKey(key)) {
            return clazz.cast(OBJECT_MAP.get(key));
        }
        synchronized (key) {
            if (OBJECT_MAP.containsKey(key)) {
                return clazz.cast(OBJECT_MAP.get(key));
            }
            try {
                Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                for (Constructor<?> constructor : constructors) {
                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    if (parameterTypes.length == args.length && matchParameters(parameterTypes, args)) {
                        final var instance = constructor.newInstance(args);
                        OBJECT_MAP.put(key, instance);
                        return clazz.cast(instance);
                    }
                }
                throw new NoSuchMethodException("No matching constructor found for arguments: " + Arrays.toString(args));
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static boolean matchParameters(Class<?>[] parameterTypes, Object[] args) {
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!parameterTypes[i].isInstance(args[i])) {
                return false;
            }
        }
        return true;
    }


}
