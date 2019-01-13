package com.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 扩展实现类类 Class 包装类
 *
 * @param <T>
 */
public class ExtensionClass<T> {
    /**
     * 真实的扩展实现类类 Class
     */
    private Class<? extends T> clazz;

    public ExtensionClass(Class<? extends T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 调用无参构造器创建扩展实现类实例
     *
     * @return 扩展实现类实例
     */
    public T getExtInstance() {
        if (clazz == null) {
            throw new RuntimeException("Class of ExtensionClass is null");
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("create " + clazz.getCanonicalName() + " instance error", e);
        }
    }

    /**
     * 调用有参构造器创建扩展实现类实例
     *
     * @return 扩展实现类实例
     */
    public T getExtInstance(Class[] argTypes, Object[] args) {
        if (clazz == null) {
            throw new RuntimeException("Class of ExtensionClass is null");
        }
        try {
            Constructor<? extends T> constructor = clazz.getDeclaredConstructor(argTypes);
            return constructor.newInstance(args);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("create " + clazz.getCanonicalName() + " instance error", e);
        }
    }
}
