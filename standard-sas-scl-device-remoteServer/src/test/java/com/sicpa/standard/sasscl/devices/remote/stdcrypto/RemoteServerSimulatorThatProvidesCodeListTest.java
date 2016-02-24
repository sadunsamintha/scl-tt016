package com.sicpa.standard.sasscl.devices.remote.stdcrypto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorModel;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorThatProvidesCodeList;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.CryptoFieldsConfig;
import com.sicpa.standard.sasscl.devices.simulator.gui.SimulatorControlView;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.sicpadata.CryptoServiceProviderManager;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sicpadata.spi.manager.StaticServiceProviderManager;
import com.sicpa.standard.sicpadata.spi.password.UniquePasswordProvider;
import com.sicpa.standard.sicpadata.spi.sequencestorage.FileSequenceStorageProvider;

public class RemoteServerSimulatorThatProvidesCodeListTest {

	@Before
	public void prepareCodeGenerator() {
		StaticServiceProviderManager.register(new CryptoServiceProviderManager(new UniquePasswordProvider("dummy"),
				new FileSequenceStorageProvider("./simulation/storage/encoder-sequence"), ""));
	}

	/**
	 * Run the RemoteServerSimulatorThatProvidesCodeList(RemoteServerSimulatorModel) constructor test.
	 */
	@Test
	public void testRemoteServerSimulatorThatProvidesCodeList() throws RemoteServerException {
		RemoteServerSimulatorModel model = new RemoteServerSimulatorModel();
		RemoteServerSimulatorThatProvidesCodeList server = new RemoteServerSimulatorThatProvidesCodeList(model);

		assertNotNull(server);
		assertEquals(null, server.getLanguageBundles());
		assertEquals("remoteServer", server.getName());
		assertEquals(false, server.isConnected());
	}

	/**
	 * Run the void doConnect() method test.
	 */
	@Test
	public void testDoConnect() throws RemoteServerException {
		RemoteServerSimulatorThatProvidesCodeList server = new RemoteServerSimulatorThatProvidesCodeList(
				new RemoteServerSimulatorModel());
		server.setSimulatorGui(new SimulatorControlView());
		server.doConnect();
	}

	/**
	 * Run the void doDisconnect() method test.
	 */
	@Test
	public void testDoDisconnect() throws RemoteServerException {
		RemoteServerSimulatorThatProvidesCodeList server = new RemoteServerSimulatorThatProvidesCodeList(
				new RemoteServerSimulatorModel());
		server.setSimulatorGui(new SimulatorControlView());
		server.doDisconnect();
	}

	/**
	 * Run the void testGetListOfCodes() method test.
	 */
	@Test
	public void testGetListOfCodes() throws Exception {
		int numberOfCodes = 100;

		RemoteServerSimulatorModel model = new RemoteServerSimulatorModel();
		model.setRequestNumberOfCodes(numberOfCodes);
		model.setUseCrypto(true);

		RemoteServerSimulatorThatProvidesCodeList server = new RemoteServerSimulatorThatProvidesCodeList(model);
		server.setCryptoFieldsConfig(new CryptoFieldsConfig());
		server.setSimulatorGui(new SimulatorControlView());
		server.setServiceProviderManager(StaticServiceProviderManager.getInstance());
		server.setupBusinessCrypto();

		int batchesQuantity = 10;
		CodeType codeType = new CodeType(1);
		int year = 1;

		List<IEncoder> result = server.createEncoders(batchesQuantity, codeType, year);

		// return the required batches quantity
		Assert.assertEquals(10, result.size());

		IEncoder encoder = result.get(0);
		List<String> codes = new ArrayList<String>();
		while (!encoder.isEncoderEmpty()) {
			codes.add(encoder.getEncryptedCodes(1).get(0));
		}

		// make sure the encoder contains the required number of codes
		Assert.assertEquals(numberOfCodes, codes.size());

	}

}
