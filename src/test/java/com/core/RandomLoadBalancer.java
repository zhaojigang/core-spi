package com.core;

@Extension("random")
public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public String selectProvider() {
        return "random: 10.211.55.10:8080";
    }
}
