package com.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 创建 ExtensionLoader 的工厂
 */
public class ExtensionLoaderFactory {
    /**
     * key: 扩展接口 Class
     * value: 扩展接口对应的 ExtensionLoader（单例，每一个扩展接口有一个 ExtensionLoader）
     */
    private static final Map<Class, ExtensionLoader> LOADER_MAP = new ConcurrentHashMap<>();

    /**
     * 获取或创建 clazz 扩展接口的 ExtensionLoader
     *
     * @param clazz 扩展接口
     * @param <T>
     * @return clazz 扩展接口的 ExtensionLoader
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> clazz) {
        ExtensionLoader<T> loader = LOADER_MAP.get(clazz);
        if (loader == null) {
            synchronized (ExtensionLoaderFactory.class) {
                if (loader == null) {
                    loader = new ExtensionLoader<>(clazz);
                    LOADER_MAP.put(clazz, loader);
                }
            }
        }
        return loader;
    }
}
