package com.core;

import org.testng.annotations.Test;

public class TestSPI {
    @Test
    public void testMainFunc() {
        // 1. 获取 LoadBalancer 的 ExtensionLoader
        ExtensionLoader<LoadBalancer> loader = ExtensionLoaderFactory.getExtensionLoader(LoadBalancer.class);
        // 2. 根据 alias 获取具体的 Extension
        LoadBalancer loadBalancer = loader.getExtension("random");
        // 3. 使用具体的 loadBalancer
        System.out.println(loadBalancer.selectProvider());

        // 4. 根据 alias 获取具体的 Extension
        LoadBalancer hasArgsLoadBalancer = loader.getExtension("hasArgs", new Class[]{String.class}, new Object[]{"haha"});
        // 5. 使用具体的 loadBalancer
        System.out.println(hasArgsLoadBalancer.selectProvider());
    }
}
