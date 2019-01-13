package com.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 扩展加载器
 *
 * @param <T>
 */
public class ExtensionLoader<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionLoader.class);
    /**
     * 当前扩展加载器处理的扩展接口名
     */
    private String interfaceName;
    /**
     * interfaceName 扩展接口下的所有实现
     */
    private Map<String, ExtensionClass<T>> alias2ExtensionClass;

    public ExtensionLoader(Class<T> interfaceClass) {
        this.interfaceName = interfaceClass.getName();
        this.alias2ExtensionClass = new ConcurrentHashMap<>();
        // 此处只指定了一个 spi 文件存储的路径
        loadFromFile("META-INF/services/corespi/");
    }

    private void loadFromFile(String spiConfigPath) {
        String spiFile = spiConfigPath + interfaceName;
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            loadFromClassLoader(classLoader, spiFile);
        } catch (Exception e) {
            LOGGER.error("load file {} error, ", spiFile, e);
        }
    }

    private void loadFromClassLoader(ClassLoader classLoader, String spiFile) throws IOException {
        // 读取多个spi文件
        Enumeration<URL> urls = classLoader != null ? classLoader.getResources(spiFile) : ClassLoader.getSystemResources(spiFile);
        if (urls == null) {
            return;
        }
        while (urls.hasMoreElements()) {
            // 每一个 url 是一个文件
            URL url = urls.nextElement();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // 读取文件中的每一行
                    readLine(line);
                }
            } catch (Exception e) { // 文件需要整体失败，不能单行失败
                LOGGER.error("load {} fail，", spiFile, e);
            }
        }
    }

    private void readLine(String line) throws ClassNotFoundException {
        // spi 文件需要严格按照 alias=className 格式编写
        String[] aliasAndClassName = line.split("=");
        // 任何不是 alias=className 格式的行都直接过滤掉
        if (aliasAndClassName == null || aliasAndClassName.length != 2) {
            return;
        }
        String alias = aliasAndClassName[0].trim();
        String className = aliasAndClassName[1].trim();
        Class<?> clazz = Class.forName(className, false, this.getClass().getClassLoader());

        // 必须具有扩展注解
        Extension extension = clazz.getAnnotation(Extension.class);
        if (extension == null) {
            LOGGER.error("{} need @Extension", className);
            return;
        }

        // 创建 ExtensionClass
        ExtensionClass<T> extensionClass = new ExtensionClass<>((Class<? extends T>) clazz);
        alias2ExtensionClass.putIfAbsent(alias, extensionClass);
    }

    public T getExtension(String alias) {
        ExtensionClass<T> extensionClass = alias2ExtensionClass.get(alias);
        if (extensionClass == null) {
            throw new RuntimeException("Not found extension of " + interfaceName + " named: \"" + alias + "\"!");
        }
        return extensionClass.getExtInstance();
    }

    public T getExtension(String alias, Class[] argTypes, Object[] args) {
        ExtensionClass<T> extensionClass = alias2ExtensionClass.get(alias);
        if (extensionClass == null) {
            throw new RuntimeException("Not found extension of " + interfaceName + " named: \"" + alias + "\"!");
        }
        return extensionClass.getExtInstance(argTypes, args);
    }
}
