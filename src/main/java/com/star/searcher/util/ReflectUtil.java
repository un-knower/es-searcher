package com.star.searcher.util;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/**
 * @author <a href="mailto:wangchao.star@gmail.com">wangchao</a>
 * @version 1.0.0
 * @since 2018-06-06 21:31:00
 */
public class ReflectUtil {
    public static List<Field> getAllDeclaredFields(Class<?> clz) {
        List<Field> fields = new ArrayList<>();
        Queue<Class<?>> queue = new ArrayDeque<>();
        queue.add(clz);
        while (!queue.isEmpty()) {
            Class<?> c = queue.poll();
            if (c != null) {
                fields.addAll(Arrays.asList(c.getDeclaredFields()));
                Class superClass = c.getSuperclass();
                if (superClass != null) {
                    queue.add(superClass);
                }
            }
        }
        return fields;
    }
}
