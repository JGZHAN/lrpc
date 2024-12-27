package cn.jgzhan.lrpc.example.common.annotation;

import cn.jgzhan.lrpc.example.common.dto.Pair;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/9
 */
public class AnnotationScanner {

    private static final Logger log = LoggerFactory.getLogger(AnnotationScanner.class);

    public static Set<Class<?>> getClasses(String packageName, ClassLoader loader) {
        Set<Class<?>> classes = new HashSet<>();
        String path = packageName.replace('.', '/');
        try {
            URL packageURL = loader.getResource(path);
            assert packageURL != null;
            File directory = new File(packageURL.getFile());
            if (directory.exists()) {
                findClasses(directory, packageName, classes);
            }
        } catch (NullPointerException e) {
            log.error("Package not found: {}, e", packageName, e);
        }
        return classes;
    }


    private static void findClasses(File directory, String packageName, Set<Class<?>> classes) {
        if (!directory.exists()) {
            return;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                // 递归处理子包
                findClasses(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                // 加载类并添加到列表中
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    log.error("Class not found: {}", className);
                }
            }
        }
    }

    @NonNull
    public static Map<Class<?>, Set<String>> getFieldsForLrpcReference(Set<Class<?>> classes) {
        final Map<Class<?>, Set<String>> result = new HashMap<>();
        for (Class<?> clazz : classes) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(LrpcReference.class)) {
                    continue;
                }
                // 获取注解的addressArr属性
                final LrpcReference annotation = field.getAnnotation(LrpcReference.class);
                final String[] addressArr = annotation.addressArr();
                // 保存类该属性的类
                result.computeIfAbsent(clazz, k -> new HashSet<>()).addAll(Arrays.asList(addressArr));
            }
        }
        return result;
    }

    public static Set<Pair<Class<?>, Object>> groupByClassAndName(Set<Class<?>> classes) {
        final Set<Pair<Class<?>, Object>> result = new HashSet<>();
        for (Class<?> clz : classes) {
            try {
                findService(clz, result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private static void findService(Class<?> clz, Set<Pair<Class<?>, Object>> result) throws Exception {
        if (!clz.isAnnotationPresent(LrpcService.class)) {
            return;
        }

        final var genericInterfaces = clz.getGenericInterfaces();
        if (genericInterfaces.length == 0) {
            final Object implInstance = clz.cast(clz.getDeclaredConstructor().newInstance());
            result.add(Pair.of(clz, implInstance));
            return;
        }
        final Class<?> interFace = Class.forName(genericInterfaces[0].getTypeName());
        final Object implInstance = clz.cast(clz.getDeclaredConstructor().newInstance());
        result.add(Pair.of(interFace, implInstance));
    }

    @SuppressWarnings("unchecked")
    private static <T> Pair<Class<T>, T> findService(T instance) {
        final var genericInterfaces = instance.getClass().getGenericInterfaces();
        if (genericInterfaces.length == 0) {
            return Pair.of((Class<T>) instance.getClass(), instance);
        }
        final Class<T> interFace = (Class<T>) genericInterfaces[0];
        return Pair.of(interFace, instance);
    }

}
