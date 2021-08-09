package com.sicpa.tt016.model;


@FunctionalInterface
public interface DisallowedConfigurationConsumer {

    void apply(String key, Object[] params);
}
