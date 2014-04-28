package com.sicpa.standard.sasscl.devices.camera;

import com.sicpa.standard.sasscl.config.xstream.IXStreamConfigurator;
import com.sicpa.standard.sasscl.devices.camera.jobconfig.CameraJobConfigNode;
import com.sicpa.standard.sasscl.devices.camera.jobconfig.CameraJobFileDescriptor;
import com.sicpa.standard.sasscl.devices.camera.jobconfig.CameraJobFilesConfig;
import com.sicpa.standard.sasscl.devices.camera.jobconfig.ProductCameraJob;
import com.sicpa.standard.sasscl.devices.camera.simulator.CameraSimulatorConfig;
import com.thoughtworks.xstream.XStream;

public class CameraXStreamConfigurator implements IXStreamConfigurator {

	@Override
	public void configure(XStream xstream) {
		xstream.alias("CameraJobFilesConfig", CameraJobFilesConfig.class);
		xstream.useAttributeFor(CameraJobFilesConfig.class, "setCameraJob");
		xstream.useAttributeFor(CameraJobFilesConfig.class, "useDefaultCameraJobFile");
		xstream.addImplicitCollection(CameraJobFilesConfig.class, "cameraJobConfigNodes");

		xstream.alias("CameraJobFileDescriptor", CameraJobFileDescriptor.class);
		xstream.useAttributeFor(CameraJobFileDescriptor.class, "cameraJobFileName");
		xstream.useAttributeFor(CameraJobFileDescriptor.class, "cameraJobFilePath");

		xstream.alias("CameraJobConfigNode", CameraJobConfigNode.class);

		xstream.useAttributeFor(ProductCameraJob.class, "productId");

		xstream.alias("CameraSimulatorConfig", CameraSimulatorConfig.class);
	}

	// public static void main(final String[] args) throws IOException, URISyntaxException {
	//
	// XStream x = ConfigUtils.getXStream();
	// x.setMode(XStream.NO_REFERENCES);
	// new CommonModelXStreamConfigurator().configure(x);
	// new CameraXStreamConfigurator().configure(x);
	//
	// Object o = ConfigUtils
	// .load("C:/workspace/sasscl-parent/standard-scl-app/src/main/resources/config/cameraJobConfig.xml");
	//
	// System.out.println(x.toXML(o));
	// }
}
