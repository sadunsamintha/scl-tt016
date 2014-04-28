/**
 * Author	: CWong
 * Date		: Jul 28, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */

package com.sicpa.standard.sasscl.devices.camera.simulator;

import java.io.Serializable;

import com.sicpa.standard.camera.controller.model.ICameraModel;

/**
 * 
 * Camera simulator's configurations.
 * 
 * <p>
 * It contains settings that can be configured to simulate the camera in SAS/SCL mode.
 * </p>
 * 
 * @author CWong
 * 
 */
public class CameraSimulatorConfig implements ICameraModel, Serializable {

	private static final long serialVersionUID = 1L;

	

	protected CodeGetMethod codeGetMethod = CodeGetMethod.generated;

	protected int percentageBadCode = 0;

	/**
	 * Frequency to read code from application
	 */
	protected int readCodeInterval = 100;

	/**
	 * File which contains list of code
	 */
	protected String dataFilePath;

	// getter and setter

	public String getDataFilePath() {
		return dataFilePath;
	}

	public void setDataFilePath(final String dataFilePath) {
		this.dataFilePath = dataFilePath;
	}

	public int getReadCodeInterval() {
		return readCodeInterval;
	}

	public void setReadCodeInterval(final int readCodeInterval) {
		this.readCodeInterval = readCodeInterval;
	}

	public void setCodeGetMethod(final CodeGetMethod codeGetMethod) {
		this.codeGetMethod = codeGetMethod;
	}

	public int getPercentageBadCode() {
		return percentageBadCode;
	}

	public CodeGetMethod getCodeGetMethod() {
		return codeGetMethod;
	}

	public void setPercentageBadCode(final int percentageBadCode) {
		this.percentageBadCode = percentageBadCode;
	}
}
