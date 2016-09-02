package com.sicpa.tt016.model;


@FunctionalInterface
public interface DisallowedConfigurationConsumer {
    public void apply(String key,Object[] params);
}
