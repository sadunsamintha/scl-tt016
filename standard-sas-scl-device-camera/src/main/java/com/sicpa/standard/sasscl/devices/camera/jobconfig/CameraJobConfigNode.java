/**
 * Author	: YYang
 * Date		: Jan 26, 2011
 *
 * Copyright (c) 2011 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.camera.jobconfig;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Tree node of a camera job configuration - ProductCameraJob
 * 
 * @see ProductCameraJob
 * 
 */
public class CameraJobConfigNode {

	/**
	 * current node configuration
	 */
	protected ProductCameraJob productCameraJob;

	/**
	 * a list of child node held by this current node
	 */
	protected final List<CameraJobConfigNode> children = new ArrayList<CameraJobConfigNode>();

	/**
	 * to form a node with passed in configuration
	 * 
	 * @param productCameraJob
	 */
	public CameraJobConfigNode(final ProductCameraJob productCameraJob) {
		this.productCameraJob = productCameraJob;
	}

	/**
	 * 
	 * add an instance of CameraJobConfigNode as a child in the current node
	 * 
	 * @param node
	 */
	public void addChild(final CameraJobConfigNode node) {
		if (children != null && !children.contains(node)) {
			children.add(node);
		}
	}

	/**
	 * 
	 * add an instance of ProductCameraJob as a child in the current node
	 * 
	 * @param productCameraJob
	 */
	public void addChild(final ProductCameraJob productCameraJob) {
		CameraJobConfigNode childNode = new CameraJobConfigNode(productCameraJob);
		addChild(childNode);
	}

	// getter and setter

	public ProductCameraJob getProductCameraJob() {
		return productCameraJob;
	}

	public List<CameraJobConfigNode> getChildren() {
		return children;
	}

	public void setChildren(final List<CameraJobConfigNode> children) {
		this.children.clear();
		this.children.addAll(children);
	}

	public void setProductCameraJob(final ProductCameraJob productCameraJob) {
		this.productCameraJob = productCameraJob;
	}

}
