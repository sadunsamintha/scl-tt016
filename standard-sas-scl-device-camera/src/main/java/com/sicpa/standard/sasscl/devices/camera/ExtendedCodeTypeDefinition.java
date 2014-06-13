package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.camera.parser.CameraCodeType;

import java.util.HashMap;
import java.util.Map;

/**
 * NOTICE:  All information and intellectual property contained herein is the confidential property of SICPA Security Solutions SA,
 * and may be subject to patent, copyright, trade secret, and other intellectual property and contractual protections.
 * Reproduction or dissemination of the information or intellectual property contained herein is strictly forbidden,
 * unless separate written permission has been obtained from SICPA Security Solutions SA.
 *
 * Copyright © 2014 SICPA Security Solutions SA. All rights reserved.
 *
 * Created by lclaudon on 12.06.14.
 */
 

public class ExtendedCodeTypeDefinition {
    /**
     * delimiter of the prefix
     */
    public static final String sPrefixDelimiter = ",";

    /**
     * map to keep the prefixes of every camera code type
     */
    private Map<CameraCodeType, String[]> codeTypeDefinitions;

    /**
     * constructor
     */
    public ExtendedCodeTypeDefinition() {

        // populate default value
        this.codeTypeDefinitions = new HashMap<CameraCodeType, String[]>();
        this.codeTypeDefinitions.put(CameraCodeType.CODE, new String[] { "C", "T" });
        this.codeTypeDefinitions.put(CameraCodeType.ERROR, new String[] { "E",
                "0" });
        this.codeTypeDefinitions.put(CameraCodeType.METRICS, new String[] {
                "V", "1" });
        this.codeTypeDefinitions.put(CameraCodeType.UNKNOWN,
                new String[] { "" });
    }

    /**
     * add new definition in the definitions map
     *
     * @param cameraCodeType
     * @param prefixes
     */
    public void addDefinition(CameraCodeType cameraCodeType, String prefixes) {

        if (cameraCodeType == null || prefixes == null)
            return;

        this.codeTypeDefinitions.put(cameraCodeType, prefixes
                .split(sPrefixDelimiter));
    }

    /**
     *
     * return the code type definitions map
     *
     * @return - instance of Map<CameraCodeType,String[]> that keeps the code
     *         type as key and an array of String as prefixes value
     */
    public Map<CameraCodeType, String[]> getCodeTypeDefinitions() {
        return codeTypeDefinitions;
    }

    /**
     *
     * set the code type definitions map
     *
     * @param codeTypeDefinitions
     */
    public void setCodeTypeDefinitions(
            Map<CameraCodeType, String[]> codeTypeDefinitions) {
        this.codeTypeDefinitions = codeTypeDefinitions;
    }

}
