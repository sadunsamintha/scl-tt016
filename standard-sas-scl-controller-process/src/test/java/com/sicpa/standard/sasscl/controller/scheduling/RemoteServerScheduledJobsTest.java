package com.sicpa.standard.sasscl.controller.scheduling;

import static org.junit.Assert.assertEquals;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.common.storage.FileStorage;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.MaxDownTimeReachedEvent;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorProvider;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;

public class RemoteServerScheduledJobsTest {

	static final int REMOTE_SERVER_MAX_DOWN_TIME = 100;
	static final String ENGLISH_LANG = "en";
	static final int REQUEST_NUMBER_ENCODERS = 44;
	static final int ID_CODE_TYPE = 22;
	static final CodeType CODE_TYPE = new CodeType(ID_CODE_TYPE);
	static final int ID_SKU = 1;

	RemoteServerScheduledJobsSCL remoteServerScheduledJobs;
	IStorage storage;
	IRemoteServer remoteServer;
	SkuListProvider productionParametersProvider;
	AuthenticatorProvider authenticatorProvider;
	ProductionParameterRootNode productionParameterRootNode;
	SKUNode skuNode;
	SKU sku;
	IAuthenticator auth;
	Map<String, ? super ResourceBundle> mapBundles;
	MyResourceBundle resourceBundle;
	boolean maxdowntimeReceived = false;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setUp() throws Exception {

		this.storage = Mockito.mock(IStorage.class);
		this.remoteServer = Mockito.mock(IRemoteServer.class);
		this.auth = Mockito.mock(IAuthenticator.class);
		this.productionParametersProvider = new SkuListProvider();
		this.authenticatorProvider = new AuthenticatorProvider();
		this.mapBundles = new HashMap();
		this.resourceBundle = new MyResourceBundle();
		this.mapBundles.put(ENGLISH_LANG, resourceBundle);

		Object maxDownTimeListener = new Object() {
			@Subscribe
			public void catchMaxDownTime(MaxDownTimeReachedEvent evt) {
				maxdowntimeReceived = true;
			}
		};
		EventBusService.register(maxDownTimeListener);

		createRemoteServerScheduledJobs(storage);

		this.productionParameterRootNode = createProductionParameterRootNode();
		productionParametersProvider.set(productionParameterRootNode);
	}

	void createRemoteServerScheduledJobs(IStorage storage) {
		remoteServerScheduledJobs = new RemoteServerScheduledJobsSCL();
		remoteServerScheduledJobs.setSkuListProvider(productionParametersProvider);
		remoteServerScheduledJobs.setStorage(storage);
		remoteServerScheduledJobs.setRemoteServer(remoteServer);
		remoteServerScheduledJobs.setAuthenticatorProvider(authenticatorProvider);
		remoteServerScheduledJobs.setRemoteServerMaxDownTime_day(REMOTE_SERVER_MAX_DOWN_TIME);
		remoteServerScheduledJobs.setRequestNumberEncoders(REQUEST_NUMBER_ENCODERS);
	}

	@Test
	public void testGetEncodersFromRemoteServerRemoteServerNotConnected() {
		fixtureServerConnected(false);
		remoteServerScheduledJobs.getEncodersFromRemoteServer();
	}

	@Test
	public void testGetAuthenticatorFromRemoteServerNoConnection() {
		fixtureServerConnected(false);
		this.remoteServerScheduledJobs.getAuthenticatorFromRemoteServer();
	}

	@Test
	public void testGetAuthenticatorFromRemoteServerConnection() throws Exception {
		fixtureServerConnected(true);

		Mockito.when(remoteServer.getAuthenticator()).thenReturn(auth);

		this.remoteServerScheduledJobs.getAuthenticatorFromRemoteServer();

		Mockito.verify(storage).saveAuthenticator(auth);
		assertEquals(auth, authenticatorProvider.get());

	}

	@Test
	public void testGetProductionParametersFromRemoteServerNoConnected() {
		fixtureServerConnected(false);
		this.remoteServerScheduledJobs.getProductionParametersFromRemoteServer();
	}

	@Test
	public void testGetProductionParametersFromRemoteServerConnected() throws Exception {
		fixtureServerConnected(true);

		Mockito.when(remoteServer.getTreeProductionParameters()).thenReturn(productionParameterRootNode);

		this.remoteServerScheduledJobs.getProductionParametersFromRemoteServer();

		Mockito.verify(storage).saveProductionParameters(productionParameterRootNode);
		assertEquals(productionParameterRootNode, productionParametersProvider.get());

	}

	@Test
	public void testGetLanguageFileFromRemoteServerNoConnected() {
		fixtureServerConnected(false);

		this.remoteServerScheduledJobs.getLanguageFileFromRemoteServer();
	}

	@Test
	public void testAddMaxDownTimeListener() {
		storage = Mockito.mock(FileStorage.class);

		createRemoteServerScheduledJobs(this.storage);

		fixtureServerConnected(false);

		remoteServerScheduledJobs.checkRemoteServerMaxDownTime();

		Assert.assertTrue(maxdowntimeReceived);
	}

	void fixtureServerConnected(boolean connected) {
		Mockito.when(this.remoteServer.isConnected()).thenReturn(connected);
	}

	ProductionParameterRootNode createProductionParameterRootNode() {
		ProductionParameterRootNode productionParameterRootNode = new ProductionParameterRootNode();
		skuNode = new SKUNode();
		sku = new SKU(ID_SKU, "COD_TYPE");
		sku.setCodeType(CODE_TYPE);
		skuNode.setValue(sku);
		productionParameterRootNode.addChildren(skuNode);
		return productionParameterRootNode;
	}

	static class MyResourceBundle extends ResourceBundle {

		@Override
		public Enumeration<String> getKeys() {
			return null;
		}

		@Override
		protected Object handleGetObject(String s) {
			return null;
		}
	}

}
