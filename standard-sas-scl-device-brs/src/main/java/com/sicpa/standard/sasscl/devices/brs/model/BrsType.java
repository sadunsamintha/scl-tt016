package com.sicpa.standard.sasscl.devices.brs.model;

import org.apache.commons.lang.StringUtils;

public enum BrsType {

    SICK("SICK"), DATAMAN("DATAMAN");

    private String value;

    private BrsType(String value) {
        this.value = value;

    }

    public static BrsType fromString(String value) {
        if (!StringUtils.isBlank(value)) {
            for (BrsType brsType : BrsType.values()) {
                if (value.trim().equalsIgnoreCase(brsType.value)) {
                    return brsType;
                }
            }
        }
        throw new IllegalArgumentException("No constant with value " + value + " found");
    }

}
