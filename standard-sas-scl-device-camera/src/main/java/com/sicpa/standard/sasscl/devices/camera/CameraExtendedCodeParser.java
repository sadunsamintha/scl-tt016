package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.camera.parser.CameraCodeType;
import com.sicpa.standard.camera.parser.ICameraCodeParser;
import com.sicpa.standard.camera.parser.ICameraCodeParserListener;
import com.sicpa.standard.camera.parser.ParsedResult;
import com.sicpa.standard.camera.parser.event.CodeEventArgs;
import com.sicpa.standard.camera.parser.event.ErrorCodeEventArgs;
import com.sicpa.standard.camera.parser.event.MetricsEventArgs;
import com.sicpa.standard.camera.parser.event.UnknownCodeEventArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Properties;

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
 

public class CameraExtendedCodeParser implements ICameraCodeParser {
    public static final Logger logger = LoggerFactory.getLogger(CameraExtendedCodeParser.class);

    /**
     *
     * to keep the code type definition
     *
     */
    private ExtendedCodeTypeDefinition codeTypeDefinition = new ExtendedCodeTypeDefinition();

    /**
     * delimiter for metrics
     *
     */
    private String mMetricsDelimiter = ";";

    /**
     * delimiter for extended code
     *
     */
    private String mExtendedCodeDelimiter = ";";

    private String VALID_TEXT_CODE = "T100";
    private String INVALID_TEXT_CODE = "E100";

    /**
     * code parser listener
     */
    private ICameraCodeParserListener mCameraCodeParserListener;

    /**
     * constructor
     */
    public CameraExtendedCodeParser() {
    }

    /**
     * setup the camera code type prefix before parsing
     *
     * Available key for properties
     *
     * CODE, ERROR, METRICS, UNKNOWN
     *
     * @See CameraCodeType
     *
     *
     * @param prop
     *            - Properties that define the code type prefixes
     *
     */
    public void setupCameraCodeTypeDefinition(final Properties prop) {

        if (prop == null) {
            return;
        }

        this.codeTypeDefinition.setCodeTypeDefinitions(new HashMap<CameraCodeType, String[]>());

        for (CameraCodeType codeType : CameraCodeType.values()) {
            String prefixes = prop.getProperty(codeType.name());
            if (prefixes != null) {
                this.codeTypeDefinition.addDefinition(codeType, prefixes);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @seecom.sicpa.standard.camera.parser.ICameraCodeParser# notifyOnCameraCodeRetrieved
     * (com.sicpa.standard.camera.parser.event.CodeEventArgs, long[])
     */
    public void notifyOnCameraCodeRetrieved(final CodeEventArgs eventArgs) {

        if (this.mCameraCodeParserListener == null) {
            return;
        }

        this.mCameraCodeParserListener.receivedCameraCode(this, eventArgs);
    }

    /*
     * (non-Javadoc)
     *
     * @seecom.sicpa.standard.camera.parser.ICameraCodeParser# notifyOnCameraErrorCodeRetrieved
     * (com.sicpa.standard.camera.parser.event.ErrorCodeEventArgs, long[])
     */
    public void notifyOnCameraErrorCodeRetrieved(final ErrorCodeEventArgs eventArgs) {

        if (this.mCameraCodeParserListener == null) {
            return;
        }

        this.mCameraCodeParserListener.receivedCameraErrorCode(this, eventArgs);

    }

    /*
     * (non-Javadoc)
     *
     * @seecom.sicpa.standard.camera.parser.ICameraCodeParser# notifyOnCameraMetricsRetrieved
     * (com.sicpa.standard.camera.parser.event.MetricsEventArgs, long[])
     */
    public void notifyOnCameraMetricsRetrieved(final MetricsEventArgs eventArgs) {

        if (this.mCameraCodeParserListener == null) {
            return;
        }

        this.mCameraCodeParserListener.receivedCameraMetrics(this, eventArgs);

    }

    /*
     * (non-Javadoc)
     *
     * @seecom.sicpa.standard.camera.parser.ICameraCodeParser# notifyOnCameraUnknownCodeRetrieved
     * (com.sicpa.standard.camera.parser.event.UnknownCodeEventArgs, long[])
     */
    public void notifyOnCameraUnknownCodeRetrieved(final UnknownCodeEventArgs eventArgs) {

        if (this.mCameraCodeParserListener == null) {
            return;
        }

        this.mCameraCodeParserListener.receivedCameraUnknownCode(this, eventArgs);

    }

    /**
     *
     * method to handle valid code
     *
     * @param result
     */
    public void handleValidCode(final ParsedResult result) {

        CodeEventArgs eventArgs = new CodeEventArgs(result.getCodeWithoutPrefix(), result.getPrefix(), result.getCode());

        notifyOnCameraCodeRetrieved(eventArgs);
    }

    /**
     *
     * method to handle error code
     *
     * @param result
     */
    public void handleErrorCode(final ParsedResult result) {

        ErrorCodeEventArgs eventArgs = null;

        if (result.getCodeWithoutPrefix().length() != 0) {

            long errorCode = -1;

            try {
                errorCode = Long.parseLong(result.getCodeWithoutPrefix());
            } catch (Exception e) {
                // ignore exception
            }

            eventArgs = new ErrorCodeEventArgs(errorCode, result.getCodeWithoutPrefix(), result.getPrefix(),
                    result.getCode());

            notifyOnCameraErrorCodeRetrieved(eventArgs);

        } else {

            eventArgs = new ErrorCodeEventArgs(0L, "", result.getPrefix(), result.getCode());
            notifyOnCameraErrorCodeRetrieved(eventArgs);

        }

    }

    /**
     * this is the default method to handle metrics based on default code type definitions method to handle metrics
     *
     * override this if different behavior is required
     *
     * @param result
     */
    public void handleMetrics(final ParsedResult result) {

        String[] tokens = result.getCode().split(this.getMetricsDelimiter());

        if (tokens.length > 0) {

            CodeEventArgs eventArgs = null;

            // case special for 1
            if (result.getPrefix().equals("1")) {
                eventArgs = new CodeEventArgs(tokens[0], result.getPrefix(), result.getCode());
            } else {
                eventArgs = new CodeEventArgs(tokens[0].substring(1), result.getPrefix(), result.getCode());
            }

            notifyOnCameraCodeRetrieved(eventArgs);

            float[] metrics = null;

            if (tokens.length > 2) {
                metrics = new float[tokens.length - 1];
                for (int i = 0; i < tokens.length - 1; i++) {
                    metrics[i] = Float.parseFloat(tokens[i + 1]);
                }
            }

            MetricsEventArgs metricsEvent = new MetricsEventArgs(metrics, result.getCode(), result.getPrefix(),
                    result.getCode());

            this.notifyOnCameraMetricsRetrieved(metricsEvent);
        }

    }

    /**
     *
     * method to handle unknown code
     *
     * @param result
     */
    public void handleUnknownCode(final ParsedResult result) {

        UnknownCodeEventArgs eventArgs = new UnknownCodeEventArgs(result.getCode(), result.getPrefix());

        this.notifyOnCameraUnknownCodeRetrieved(eventArgs);
    }

    /**
     *
     * method to handle camera code
     *
     * @param result
     */
    public void handleCameraCode(final ParsedResult result) {

        switch (result.getCodeType()) {

            case CODE:

                this.handleValidCode(result);

                break;

            case ERROR:

                this.handleErrorCode(result);

                break;

            case METRICS:

                this.handleMetrics(result);

                break;

            case UNKNOWN:

                this.handleUnknownCode(result);

                break;
        }

    }

    /**
     * method to parse the code, result will be sent by event
     */
    public void parse(String code) {

        logger.debug("Camera Code Parser - Code received: " + code);

        if (code == null || code.length() == 0) {
            return;
        }

        code = code.trim();

        // Parse DMX Code And Text Code
        String dmxCode = code.substring(0, code.indexOf(mExtendedCodeDelimiter));
        String txtCode = code.substring(code.indexOf(mExtendedCodeDelimiter) + 1);
        logger.debug("Camera Code Parser - DMX Code: " + dmxCode);
        logger.debug("Camera Code Parser - Txt Code: " + txtCode);

        if(txtCode != null) {
            if (txtCode.equals(VALID_TEXT_CODE)) {
                parseDmxCode(dmxCode);
                return;
            } else if (txtCode.equals(INVALID_TEXT_CODE)) {
                ParsedResult result = new ParsedResult(CameraCodeType.ERROR, String.valueOf(INVALID_TEXT_CODE.charAt(0))
                        , txtCode, txtCode.substring(1));

                this.handleCameraCode(result);
            }
        }
    }

    public void parseDmxCode(String dmxCode) {

        if (dmxCode == null || dmxCode.length() == 0) {
            return;
        }

        dmxCode = dmxCode.trim();

        for (CameraCodeType codeType : CameraCodeType.values()) {

            String[] prefixes = this.codeTypeDefinition.getCodeTypeDefinitions().get(codeType);

            for (String prefix : prefixes) {

                if (dmxCode.startsWith(prefix)) {

                    ParsedResult result = new ParsedResult(codeType, prefix, dmxCode, dmxCode.substring(1));

                    this.handleCameraCode(result);

                    return;

                }
            }
        }

        UnknownCodeEventArgs eventArgs = new UnknownCodeEventArgs(dmxCode, "");

        // handle unknown code
        this.notifyOnCameraUnknownCodeRetrieved(eventArgs);

    }

    /**
     * return code type definition
     *
     * @return
     */
    public ExtendedCodeTypeDefinition getCodeTypeDefinition() {
        return this.codeTypeDefinition;
    }

    /**
     * add listener
     *
     * @param listener
     */
    public void setListener(final ICameraCodeParserListener listener) {
        this.mCameraCodeParserListener = listener;
    }

    /**
     * return delimiter for metrics
     *
     * @return - delimiter in String
     */
    public String getMetricsDelimiter() {
        return this.mMetricsDelimiter;
    }

    /**
     *
     * set the delimiter for metrics
     *
     * @param metricsDelimiter
     */
    public void setMetricsDelimiter(final String metricsDelimiter) {
        this.mMetricsDelimiter = metricsDelimiter;
    }
}

