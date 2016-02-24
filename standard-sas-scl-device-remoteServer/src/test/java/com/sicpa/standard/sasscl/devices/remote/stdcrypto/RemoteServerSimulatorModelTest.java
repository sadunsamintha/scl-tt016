/**
 * Author	: YYang
 * Date		: Sep 29, 2010
 *
 * Copyright (c) 2010 SICPA Security Solutions, all rights reserved.
 *
 */
package com.sicpa.standard.sasscl.devices.remote.stdcrypto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorModel;
import com.sicpa.standard.sasscl.devices.remote.stdCrypto.CryptoFieldsConfig;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.CryptoServiceProviderManager;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.standard.sicpadata.api.exception.SicpadataException;
import com.sicpa.standard.sicpadata.api.exception.UnknownModeException;
import com.sicpa.standard.sicpadata.api.exception.UnknownSystemTypeException;
import com.sicpa.standard.sicpadata.api.exception.UnknownVersionException;
import com.sicpa.standard.sicpadata.spi.manager.ServiceProviderException;
import com.sicpa.standard.sicpadata.spi.manager.StaticServiceProviderManager;
import com.sicpa.standard.sicpadata.spi.password.UniquePasswordProvider;
import com.sicpa.standard.sicpadata.spi.sequencestorage.FileSequenceStorageProvider;

/**
 * Test related to RemoteServerSimulatorModel
 * 
 */
public class RemoteServerSimulatorModelTest {

	@Before
	public void setupActivatedProdct() {

	}

	@Before
	public void prepareCodeGenerator() {
		StaticServiceProviderManager.register(new CryptoServiceProviderManager(new UniquePasswordProvider("dummy"),
				new FileSequenceStorageProvider("./simulation/storage/encoder-sequence"), ""));
	}

	@After
	public void removeEncoderSequenceEntry() throws IOException {
		File seqFile = new File("./test/encoder-sequence");
		FileUtils.deleteDirectory(seqFile);
	}

	/**
	 * Test calling get encoder without setting the model
	 * 
	 */
	@Test(expected = RemoteServerException.class)
	public void remoteServerSimulatorExceptionTest() throws RemoteServerException {
		CodeType codeType = new CodeType(1);
		RemoteServerSimulator remoteServerSimulator = null;
		RemoteServerSimulatorModel model = null;

		remoteServerSimulator = new RemoteServerSimulator(model);

		// exception will be thrown
		remoteServerSimulator.downloadEncoder(3, codeType, 2010);

		// should never reach this line
		Assert.fail();
	}

	/**
	 * Test calling the get authenticator without setting the model
	 * 
	 */
	@Test(expected = RemoteServerException.class)
	public void remoteServerSimulatorExceptionTest2() throws RemoteServerException {

		RemoteServerSimulator remoteServerSimulator = null;
		RemoteServerSimulatorModel model = null;

		remoteServerSimulator = new RemoteServerSimulator(model);

		// exception will be thrown
		remoteServerSimulator.getAuthenticator();

		// should never reach this line
		Assert.fail();
	}

	/**
	 * 
	 * Test the authenticator is able to decode the code generated by encoder get from remote server simulator
	 * 
	 */
	@Test
	public void remoteServerSimulatorEncodersAuthenticatorTest() throws RemoteServerException, CryptographyException,
			ServiceProviderException, UnknownModeException, UnknownVersionException, UnknownSystemTypeException,
			SicpadataException {

		CodeType codeType = new CodeType(1);

		RemoteServerSimulatorModel model = new RemoteServerSimulatorModel();
		model.setUseCrypto(true);
		model.setRequestNumberOfCodes(99000);

		RemoteServerSimulator simulator = new RemoteServerSimulator(model);
		simulator.setCryptoFieldsConfig(new CryptoFieldsConfig());
		simulator.setServiceProviderManager(StaticServiceProviderManager.getInstance());

		simulator.setupBusinessCrypto();

		IAuthenticator authenticator = simulator.getAuthenticator();

		IEncoder firstEncoder = simulator.createOneEncoder(2010, (int) codeType.getId());

		String encryptedCode1 = firstEncoder.getEncryptedCodes(1).get(0);
		String encryptedCode2 = firstEncoder.getEncryptedCodes(1).get(0);

		/**
		 * sequence = 0, batch = 0, code type = 1
		 */
		DecodedCameraCode result1 = (DecodedCameraCode) authenticator.decode(null, encryptedCode1);

		Assert.assertEquals(0, result1.getBatchId());
		Assert.assertEquals(1, result1.getCodeType().getId());

		/**
		 * sequence = 1, batch = 0, code type = 1
		 */
		DecodedCameraCode result2 = (DecodedCameraCode) authenticator.decode(null, encryptedCode2);
		Assert.assertEquals(0, result2.getBatchId());
		Assert.assertEquals(1, result2.getCodeType().getId());

	}

	/**
	 * 
	 * Test using encoder and authenticator after it is deserialized
	 * 
	 * @throws ServiceProviderException
	 * 
	 */
	// @Test
	// public void remoteServerSimulatorSerializeEncoderTest() throws RemoteServerException, CryptographyException,
	// UnknownModeException, UnknownVersionException, UnknownSystemTypeException, SicpadataException,
	// ServiceProviderException {
	//
	// SkuProvider skuProvider = new SkuProvider();
	// skuProvider.set(new SKU(1));
	//
	// RemoteServerSimulatorModel model = new RemoteServerSimulatorModel();
	// RemoteServerSimulator simulator = new RemoteServerSimulator(model);
	// simulator.setSkuProvider(skuProvider);
	// simulator.setConfig(new GlobalBean());
	// simulator.setServiceProviderManager(StaticServiceProviderManager.getInstance());
	//
	// CodeType codeType = new CodeType(1);
	//
	// // retrieve encoders
	// IEncoder encoder = simulator.createOneEncoder(2010, (int) codeType.getId());
	//
	// // retrieve authenticator
	// IAuthenticator authenticator = simulator.getAuthenticator();
	//
	// Assert.assertNotNull(encoder);
	//
	// String firstEncryptedCode = encoder.getEncryptedCodes(1).get(0);
	//
	// Assert.assertNotNull(firstEncryptedCode);
	//
	// /*
	// * try to decode, should not throw any exception
	// */
	// authenticator.decode(null, firstEncryptedCode);
	//
	// }

	@Test
	public void remoteServerSimulatorSendProductionDataTest() throws RemoteServerException {

		RemoteServerSimulatorModel model = new RemoteServerSimulatorModel();
		RemoteServerSimulator simulator = new RemoteServerSimulator(model);

		File outputDir = new File(simulator.getRemoteServerSimulatorOutputFolder());

		// clear output folder
		if (outputDir.exists()) {
			for (File prodFile : outputDir.listFiles()) {
				prodFile.delete();
			}
			outputDir.delete();
		}

		long currentTime = System.currentTimeMillis();

		List<Product> products = new ArrayList<Product>();

		SKU sku = new SKU(1, "sku");

		products.add(new Product(new Code("1"), ProductStatus.AUTHENTICATED, sku, "00001", new Date(), 1234));

		// create packaged products
		PackagedProducts packageProducts = new PackagedProducts(currentTime, ProductStatus.AUTHENTICATED, "147", 123l,
				false);
		packageProducts.getProducts().addAll(products);

		simulator.sendProductionData(packageProducts);

		Assert.assertTrue(outputDir.exists());

		Assert.assertEquals(1, outputDir.listFiles().length);
	}

	/**
	 * Run the ProductionParameterRootNode getProductionParameters() method test.
	 * 
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testGetProductionParameters() {
		RemoteServerSimulatorModel fixture = new RemoteServerSimulatorModel();
		ProductionParameterRootNode productionParameters = new ProductionParameterRootNode();
		productionParameters.setFileImage("sku1.png");
		fixture.setProductionParameters(productionParameters);

		ProductionParameterRootNode result = fixture.getProductionParameters();

		assertNotNull(result);
		assertEquals(false, result.isShownOnSummary());
		assertEquals(null, result.getFormatedTextForSummary());
		assertEquals(null, result.getValue());
		assertEquals(0, result.getId());
		assertEquals(null, result.getText());
		assertEquals(null, result.getImage());
		assertEquals(true, result.isLeaf());
		assertEquals(0, result.getChildren().size());
		assertEquals(null, result.getFileImage());
	}

	/**
	 * Run the void setProductionParameters(ProductionParameterRootNode) method test.
	 * 
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testSetProductionParameters() {
		RemoteServerSimulatorModel fixture = new RemoteServerSimulatorModel();

		fixture.setProductionParameters(new ProductionParameterRootNode());
		ProductionParameterRootNode productionParameters = new ProductionParameterRootNode();

		fixture.setProductionParameters(productionParameters);
		Assert.assertEquals(productionParameters, fixture.getProductionParameters());
	}
}
