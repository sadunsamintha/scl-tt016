/**
 * Author	: CWong
 * Date		: Jul 28, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */

package com.sicpa.standard.sasscl.devices.camera.d900.simulator;

import java.io.Serializable;
import com.sicpa.standard.camera.d900.controller.model.ID900CameraModel;

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
public class D900CameraSimulatorConfig implements ID900CameraModel, Serializable {

	private static final long serialVersionUID = 1L;

	

	protected D900CodeGetMethod codeGetMethod = D900CodeGetMethod.generated;

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

	private int percentageBlobCode = 0;


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

	public void setCodeGetMethod(final D900CodeGetMethod codeGetMethod) {
		this.codeGetMethod = codeGetMethod;
	}

	public int getPercentageBadCode() {
		return percentageBadCode;
	}

	public D900CodeGetMethod getCodeGetMethod() {
		return codeGetMethod;
	}

	public void setPercentageBadCode(final int percentageBadCode) {
		this.percentageBadCode = percentageBadCode;
	}

	public int getPercentageBlobCode() {
		return percentageBlobCode;
	}

	public void setPercentageBlobCode(int percentageBlobCode) {
		this.percentageBlobCode = percentageBlobCode;
	}
}
