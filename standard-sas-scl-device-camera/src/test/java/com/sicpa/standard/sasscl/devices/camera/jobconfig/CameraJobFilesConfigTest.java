/**
 * Author	: YYang
 * Date		: Jan 27, 2011
 *
 * Copyright (c) 2011 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.camera.jobconfig;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.sicpa.standard.client.common.utils.ConfigUtils;

/**
 * test cases for CameraJobFilesConfig
 * 
 */
public class CameraJobFilesConfigTest {

	@Test
	public void testLoadingConfigFile() {
		CameraJobFilesConfig jobFileConfig = new CameraJobFilesConfig("CameraJobConfig.xml");
		Assert.assertTrue(jobFileConfig.isSetCameraJob());
		Assert.assertFalse(jobFileConfig.isUseDefaultCameraJobFile());
		Assert.assertEquals("defaultJob.job", jobFileConfig.getDefaultCameraJobFile().getCameraJobFileName());
		CameraJobFileDescriptor jobFileDescriptor = jobFileConfig
				.retrieveCameraJobConfiguration(new Integer[] { 0, 2 });
		Assert.assertEquals("cameraJobForSKU0_2.job", jobFileDescriptor.getCameraJobFileName());
	}

	@Test
	public void testCameraJobConfigNode() {

		ProductCameraJob productCameraJob = new ProductCameraJob(0, new CameraJobFileDescriptor("camerajobFile0.job"));

		CameraJobConfigNode cameraJobConfigNode = new CameraJobConfigNode(productCameraJob);

		ProductCameraJob skuCameraJob = new ProductCameraJob(3, new CameraJobFileDescriptor("camerajobFile3.job"));
		CameraJobConfigNode skuCameraJobConfigNode = new CameraJobConfigNode(skuCameraJob);

		cameraJobConfigNode.addChild(new ProductCameraJob(1, new CameraJobFileDescriptor("camerajobFile1.job")));
		cameraJobConfigNode.addChild(new ProductCameraJob(2, new CameraJobFileDescriptor("camerajobFile2.job")));
		cameraJobConfigNode.addChild(skuCameraJobConfigNode);

		Assert.assertEquals(3, cameraJobConfigNode.getChildren().size());
	}

	@Test
	public void testCameraJobFilesConfigGetJobMethod() {

		// define default camera job
		CameraJobFileDescriptor defaultCameraJob = new CameraJobFileDescriptor("defaultCameraJob.job");

		// define some job descriptors for production mode
		CameraJobFileDescriptor desc0 = new CameraJobFileDescriptor("cameraJobForMode0.job");
		CameraJobFileDescriptor desc1 = new CameraJobFileDescriptor("cameraJobForMode1.job");
		CameraJobFileDescriptor desc2 = new CameraJobFileDescriptor("cameraJobForMode2.job");

		// define some job descriptors for SKUs in production mode 0
		CameraJobFileDescriptor desc0_1 = new CameraJobFileDescriptor("cameraJobForSKU0_1.job");
		CameraJobFileDescriptor desc0_2 = new CameraJobFileDescriptor("cameraJobForSKU0_2.job");
		CameraJobFileDescriptor desc0_3 = new CameraJobFileDescriptor("cameraJobForSKU0_3.job");
		CameraJobFileDescriptor desc0_4 = new CameraJobFileDescriptor("cameraJobForSKU0_4.job");

		// if there is any levels further down to the SKU
		CameraJobFileDescriptor desc0_1_1 = new CameraJobFileDescriptor("cameraJob0_1_1.job");
		CameraJobFileDescriptor desc0_1_3 = new CameraJobFileDescriptor("cameraJob0_1_3.job");

		// define some job descriptors for SKUs in production mode 1
		CameraJobFileDescriptor desc1_1 = new CameraJobFileDescriptor("cameraJobForSKU0_1.job");
		CameraJobFileDescriptor desc1_2 = new CameraJobFileDescriptor("cameraJobForSKU0_2.job");
		CameraJobFileDescriptor desc1_3 = new CameraJobFileDescriptor("cameraJobForSKU0_3.job");

		// Level 1 ProductCameraJob
		ProductCameraJob productionMode1 = new ProductCameraJob(0, desc0);
		ProductCameraJob productionMode2 = new ProductCameraJob(1, desc1);
		ProductCameraJob productionMode3 = new ProductCameraJob(2, desc2);

		// Level 2 ProductCameraJob
		ProductCameraJob sku0_1 = new ProductCameraJob(1, desc0_1);
		ProductCameraJob sku0_2 = new ProductCameraJob(2, desc0_2);
		ProductCameraJob sku0_3 = new ProductCameraJob(3, desc0_3);
		ProductCameraJob sku0_4 = new ProductCameraJob(4, desc0_4);

		ProductCameraJob sku1_1 = new ProductCameraJob(1, desc1_1);
		ProductCameraJob sku1_2 = new ProductCameraJob(2, desc1_2);
		ProductCameraJob sku1_3 = new ProductCameraJob(3, desc1_3);

		// Level 3 ProductCameraJob
		ProductCameraJob pc_0_1_1 = new ProductCameraJob(1, desc0_1_1);
		ProductCameraJob pc_0_1_3 = new ProductCameraJob(3, desc0_1_3);

		// Form tree structure

		// level 1
		CameraJobConfigNode productMode1ConfigNode = new CameraJobConfigNode(productionMode1);
		CameraJobConfigNode productMode2ConfigNode = new CameraJobConfigNode(productionMode2);
		CameraJobConfigNode productMode3ConfigNode = new CameraJobConfigNode(productionMode3);

		// level 2
		CameraJobConfigNode sku0_1ConfigNode = new CameraJobConfigNode(sku0_1);
		productMode1ConfigNode.addChild(sku0_1ConfigNode);
		productMode1ConfigNode.addChild(sku0_2);
		productMode1ConfigNode.addChild(sku0_3);
		productMode1ConfigNode.addChild(sku0_4);

		productMode2ConfigNode.addChild(sku1_1);
		productMode2ConfigNode.addChild(sku1_2);
		productMode2ConfigNode.addChild(sku1_3);

		// level 3
		sku0_1ConfigNode.addChild(pc_0_1_1);
		sku0_1ConfigNode.addChild(pc_0_1_3);

		List<CameraJobConfigNode> mainConfigNodeList = new ArrayList<CameraJobConfigNode>();
		mainConfigNodeList.add(productMode1ConfigNode);
		mainConfigNodeList.add(productMode2ConfigNode);
		mainConfigNodeList.add(productMode3ConfigNode);

		// create CameraJobFilesConfig
		CameraJobFilesConfig cameraJobFilesConfiguration = new CameraJobFilesConfig(mainConfigNodeList);

		// set default camera job
		cameraJobFilesConfiguration.setDefaultCameraJobFile(defaultCameraJob);

		// search camera job config for production mode = 0 , sku = 4
		CameraJobFileDescriptor cameraJob = cameraJobFilesConfiguration.retrieveCameraJobConfiguration(new Integer[] {
				0, 4 });

		Assert.assertEquals("cameraJobForSKU0_4.job", cameraJob.getCameraJobFileName());

		// search camera job config for production mode = 0, sku=1, level after sku = 3
		cameraJob = cameraJobFilesConfiguration.retrieveCameraJobConfiguration(new Integer[] { 0, 1, 3 });

		Assert.assertEquals("cameraJob0_1_3.job", cameraJob.getCameraJobFileName());

		// search for camera job config for production mode = 0, sku=5 (invalid sku, should use the parent -
		// cameraJobForMode0.job)
		cameraJob = cameraJobFilesConfiguration.retrieveCameraJobConfiguration(new Integer[] { 0, 5 });

		Assert.assertEquals("cameraJobForMode0.job", cameraJob.getCameraJobFileName());

		// search for camera job config for invalid production mode, production mode = 6, sku=4 (no camera job found
		// from the tree config, default camera job will be used)
		cameraJob = cameraJobFilesConfiguration.retrieveCameraJobConfiguration(new Integer[] { 6, 4 });

		Assert.assertEquals("defaultCameraJob.job", cameraJob.getCameraJobFileName());

		// empty integer test
		cameraJob = cameraJobFilesConfiguration.retrieveCameraJobConfiguration(new Integer[] {});

		Assert.assertEquals("defaultCameraJob.job", cameraJob.getCameraJobFileName());

	}

	@Test
	public void testLoadCameraJobConfigFile() {

		try {

			CameraJobFilesConfig jobFileConfig = ConfigUtils.load(CameraJobFilesConfig.class, "CameraJobConfig.xml");
			Assert.assertEquals("defaultJob.job", jobFileConfig.getDefaultCameraJobFile().getCameraJobFileName());
			Assert.assertEquals(false, jobFileConfig.isUseDefaultCameraJobFile());

			CameraJobFileDescriptor jobFileDescriptor = jobFileConfig
					.retrieveCameraJobConfiguration(new Integer[] { 0 });

			Assert.assertEquals("cameraJobForMode0.job", jobFileDescriptor.getCameraJobFileName());

			jobFileDescriptor = jobFileConfig.retrieveCameraJobConfiguration(new Integer[] { 1 });

			Assert.assertEquals("cameraJobForMode1.job", jobFileDescriptor.getCameraJobFileName());

			jobFileDescriptor = jobFileConfig.retrieveCameraJobConfiguration(new Integer[] { 0, 2 });

			Assert.assertEquals("cameraJobForSKU0_2.job", jobFileDescriptor.getCameraJobFileName());

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (URISyntaxException e) {
			Assert.fail(e.getMessage());
		}

	}

}
