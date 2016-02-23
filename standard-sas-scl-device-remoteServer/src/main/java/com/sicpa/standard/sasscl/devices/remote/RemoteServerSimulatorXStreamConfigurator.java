package com.sicpa.standard.sasscl.devices.remote;

import com.sicpa.standard.client.common.xstream.IXStreamConfigurator;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorModel;
import com.thoughtworks.xstream.XStream;

public class RemoteServerSimulatorXStreamConfigurator implements IXStreamConfigurator {

	@Override
	public void configure(XStream xstream) {
		xstream.alias("RemoteServerSimulatorModel", RemoteServerSimulatorModel.class);
		xstream.useAttributeFor(RemoteServerSimulatorModel.class, "useCrypto");
		xstream.useAttributeFor(RemoteServerSimulatorModel.class, "requestNumberOfCodes");
	}

	// public static void main(final String[] args) throws IOException, URISyntaxException {
	// Object o = ConfigUtils
	// .load("C:/workspace/sasscl-parent/standard-scl-app/src/main/resources/config/remoteServerSimulator.xml");
	//
	// XStream x = ConfigUtils.getXStream();
	// x.setMode(XStream.NO_REFERENCES);
	// new CommonModelXStreamConfigurator().configure(x);
	// new RemoteServerSimulatorXStreamConfigurator().configure(x);
	//
	// System.out.println(x.toXML(o));
	// }
}
