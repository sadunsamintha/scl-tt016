/**
 * Author	: YYang
 * Date		: Jan 26, 2011
 *
 * Copyright (c) 2011 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.camera.jobconfig;

/**
 * 
 * To encapsulate the camera job details
 * 
 */
public class CameraJobFileDescriptor {

	/**
	 * name of the camera job
	 * 
	 */
	protected String cameraJobFileName;

	/**
	 * attribute to indicate the local path of the camera job. It is used to upload the camera job to the camera if
	 * necessary
	 * 
	 * TODO: future enhancement - upload camera job file
	 * 
	 */
	protected String cameraJobFilePath;

	/**
	 * attribute to indicate if the job needs to be upload to the camera during application startup
	 * 
	 * TODO: future enhancement - upload camera job file
	 * 
	 */
	protected transient boolean uploadJobDuringProduction = false;

	/**
	 * 
	 * @param fileName
	 */
	public CameraJobFileDescriptor(final String fileName) {
		this.cameraJobFileName = fileName;
	}

	/**
	 * 
	 * @param filePath
	 * @param fileName
	 * @param uploadJobDuringProduction
	 */
	public CameraJobFileDescriptor(final String filePath, final String fileName, final boolean uploadJobDuringProduction) {
		this.cameraJobFileName = fileName;
		this.cameraJobFilePath = filePath;
		this.uploadJobDuringProduction = uploadJobDuringProduction;
	}

	// setter and getter
	public String getCameraJobFilePath() {
		return this.cameraJobFilePath;
	}

	public void setCameraJobFilePath(final String cameraJobFilePath) {
		this.cameraJobFilePath = cameraJobFilePath;
	}

	public String getCameraJobFileName() {
		return this.cameraJobFileName;
	}

	public void setCameraJobFileName(final String cameraJobFileName) {
		this.cameraJobFileName = cameraJobFileName;
	}

	public boolean isUploadJobDuringProduction() {
		return this.uploadJobDuringProduction;
	}

	public void setUploadJobDuringProduction(final boolean uploadJobDuringProduction) {
		this.uploadJobDuringProduction = uploadJobDuringProduction;
	}

}
