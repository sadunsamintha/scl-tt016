package com.sicpa.tt016.model;


import java.util.Map;
import java.util.Properties;

/**
 * Model to list configuration values which cannot be used together
 * Intended usage is to parse these against context configuration and
 * throw a <code>DisallowedConfiguratonException</code> with the reason
 * the configuration is not valid
 */
public class DisallowedConfiguration {
    private final Map<String,String> configurationValues;
    private final String reason;

    private static final String EXCEPTION_KEY = "disallowed.configuration.error";

    public DisallowedConfiguration(Map<String, String> configurationValues,String reason) {
        this.configurationValues = configurationValues;
        this.reason = reason;

    }
    public void validate(Properties p,DisallowedConfigurationConsumer d){
        long numberOfEqualProperties = configurationValues.entrySet().stream().filter( i -> p.get(i.getKey()).equals(i.getValue())).count();
        if (numberOfEqualProperties - configurationValues.size() == 0){
            d.apply(getMessageKey(),getMessageParams());
        }
    }
    private String getMessageKey(){
        return EXCEPTION_KEY;
    }
    private Object[] getMessageParams(){
        return new Object[]{configurationValues.toString(),reason};
    }

}
