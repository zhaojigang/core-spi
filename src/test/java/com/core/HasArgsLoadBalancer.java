package com.core;

@Extension("hasArgs")
public class HasArgsLoadBalancer implements LoadBalancer {
    private String tag;

    public HasArgsLoadBalancer(String tag){
        this.tag = tag;
    }

    @Override
    public String selectProvider() {
        return "hasArgs: 10.211.55.11:8080 - " + tag;
    }
}
