/**
 * Author	: YYang
 * Date		: Jan 26, 2011
 *
 * Copyright (c) 2011 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.camera.jobconfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sicpa.standard.client.common.utils.ConfigUtils;

/**
 * Configuration bean for camera controller
 * 
 */
public class CameraJobFilesConfig implements ICameraJobFilesConfig{

	/**
	 * indicate if the camera job need to be set based on product parameter.
	 * 
	 * Set to "false" to disable the camera job setting action. This is used if the camera job is the same for all
	 * product descriptors
	 * 
	 * Set to "true" to enable this feature
	 * 
	 * Default value = false
	 * 
	 */
	protected boolean setCameraJob = false;

	/**
	 * customization camera job file for every level of product parameters
	 * 
	 * for example: Production Mode, SKU, etc
	 */
	protected List<CameraJobConfigNode> cameraJobConfigNodes;

	/**
	 * default camera job file to be used
	 * 
	 * falls back to this if there is no camera job found for the first level product parameters (i.e. product mode)
	 * 
	 */
	protected CameraJobFileDescriptor defaultCameraJobFile;

	/**
	 * indicate that the default camera job file will be used. If the value is set to "true", the camera job defined in
	 * defaultCameraJobFile attribute will be used. cameraJobConfigNodes will be ignored (no tree traversal)
	 * 
	 */
	protected boolean useDefaultCameraJobFile = false;

	public CameraJobFilesConfig(final List<CameraJobConfigNode> cameraJobConfigNodes) {
		this.cameraJobConfigNodes = cameraJobConfigNodes;
	}

	public CameraJobFilesConfig(final CameraJobFileDescriptor defaultCameraJobFile,
			final List<CameraJobConfigNode> cameraJobConfigNodes) {
		this.defaultCameraJobFile = defaultCameraJobFile;
		this.cameraJobConfigNodes = cameraJobConfigNodes;
	}

	public CameraJobFilesConfig(final CameraJobFileDescriptor defaultCameraJobFile,
			final List<CameraJobConfigNode> cameraJobConfigNodes, final boolean useDefaultCameraJobFile) {
		this.defaultCameraJobFile = defaultCameraJobFile;
		this.cameraJobConfigNodes = cameraJobConfigNodes;
		this.useDefaultCameraJobFile = useDefaultCameraJobFile;
	}

	public CameraJobFilesConfig(final String file) {
		this.setDefaultValues();
		ConfigUtils.loadCopySave(this, file);
	}

	public void setDefaultValues() {
		setSetCameraJob(false);
		cameraJobConfigNodes = new ArrayList<CameraJobConfigNode>();
		defaultCameraJobFile = new CameraJobFileDescriptor("defaultCameraJob.job");
		setUseDefaultCameraJobFile(false);
	}

	/**
	 * 
	 * return the camera job descriptor by passed in IDs
	 * 
	 * This method tries to retrieve the camera job descriptor with the passed in IDs. It returns the job descriptor of
	 * the deepest level found. If there is no job descriptor found, the default camera job defined in
	 * defaultCameraJobFile will be used.
	 * 
	 * @see CameraJobFilesConfig#defaultCameraJobFile
	 * 
	 * @param ids
	 *            a list of IDs to locate the camera job file descriptor of a particular product parameter.
	 *            <p>
	 *            ID should be put in the order according to the levels. For example id for production mode = 0, and the
	 *            id for SKU = 1. Passed in IDs should be 0 follow by 1 --> new Integer[] {0,1}
	 *            </p>
	 * 
	 * @return
	 */
	public CameraJobFileDescriptor retrieveCameraJobConfiguration(final Integer[] ids) {
		if (ids == null || ids.length == 0) {
			return defaultCameraJobFile;
		}
		// convert array to list for easier handling
		List<Integer> idsArray = new ArrayList<Integer>();
		idsArray.addAll(Arrays.asList(ids));
		return retrieveCameraJobConfiguration(idsArray);
	}

	/**
	 * 
	 * return the camera job descriptor by passed in IDs (using list)
	 * 
	 * @See {@link CameraJobFilesConfig#retrieveCameraJobConfiguration(Integer[])}
	 * 
	 * @param ids
	 * @return
	 */
	public CameraJobFileDescriptor retrieveCameraJobConfiguration(final List<Integer> ids) {

		if (ids == null || ids.size() == 0) {
			return defaultCameraJobFile;
		}

		// no configurations defined in the tree nodes
		if (cameraJobConfigNodes == null || cameraJobConfigNodes.size() == 0) {
			return defaultCameraJobFile;
		}

		CameraJobFileDescriptor cameraJobDescriptor = null;
		CameraJobConfigNode node = getCameraJobConfigByIds(ids, cameraJobConfigNodes);

		if (node != null) {
			cameraJobDescriptor = node.getProductCameraJob().getCameraJobFile();
		} else {
			cameraJobDescriptor = defaultCameraJobFile;
		}

		return cameraJobDescriptor;
	}

	protected CameraJobConfigNode getCameraJobConfigByIds(final List<Integer> ids,
			final List<CameraJobConfigNode> cameraJobConfigNodes) {

		// return if there is no more id to search
		if (ids.size() == 0) {
			return null;
		}

		CameraJobConfigNode nodeToReturn = null;
		Integer searchId = ids.remove(0);

		for (CameraJobConfigNode node : cameraJobConfigNodes) {
			if (node.getProductCameraJob().getProductId().equals(searchId)) {
				nodeToReturn = node;
				if (node.getChildren() == null || node.getChildren().size() == 0) {
					break;
				} else {
					CameraJobConfigNode configNode = getCameraJobConfigByIds(ids, node.getChildren());
					if (configNode != null) {
						nodeToReturn = configNode;
					}
				}
			}
		}
		return nodeToReturn;
	}

	// getter and setter
	public List<CameraJobConfigNode> getCameraJobConfigNodes() {
		return cameraJobConfigNodes;
	}

	public void setCameraJobConfigNodes(final List<CameraJobConfigNode> cameraJobConfigNodes) {
		this.cameraJobConfigNodes = cameraJobConfigNodes;
	}

	public CameraJobFileDescriptor getDefaultCameraJobFile() {
		return this.defaultCameraJobFile;
	}

	public void setDefaultCameraJobFile(final CameraJobFileDescriptor defaultCameraJobFile) {
		this.defaultCameraJobFile = defaultCameraJobFile;
	}

	public boolean isUseDefaultCameraJobFile() {
		return useDefaultCameraJobFile;
	}

	public void setUseDefaultCameraJobFile(final boolean useDefaultCameraJobFile) {
		this.useDefaultCameraJobFile = useDefaultCameraJobFile;
	}

	public boolean isSetCameraJob() {
		return setCameraJob;
	}

	public void setSetCameraJob(boolean setCameraJob) {
		this.setCameraJob = setCameraJob;
	}

}
