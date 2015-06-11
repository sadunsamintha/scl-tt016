package com.sicpa.standard.sasscl.common.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.storage.impl.XStreamFileStorage;
import com.sicpa.standard.client.common.utils.LogUtils;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.common.storage.productPackager.DefaultProductsPackager;
import com.sicpa.standard.sasscl.common.storage.productPackager.IProductsPackager;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKeyBad;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKeyGood;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionModeNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.SKUNode;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.FileSequenceStorageProvider;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.impl.EncoderNoEncryptionSimulator;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.standard.sasscl.sicpadata.reader.IDecodedResult;

public class FileStorageTest {

	private final String dataFolder = "test/data";
	private final String internalFolder = "test/internal";

	private IProductsPackager productsPackager;

	private final FileStorage storage = new FileStorage(dataFolder, internalFolder, "quarantine",
			new XStreamFileStorage());

	private FileSequenceStorageProvider fileSequenceStorageProvider = Mockito.mock(FileSequenceStorageProvider.class);

	@Before
	public void setUp() {
		LogUtils.initLogger();
		productsPackager = new DefaultProductsPackager();
		storage.setProductsPackager(productsPackager);
		storage.setFileSequenceProvider(fileSequenceStorageProvider);
	}

	public void cleanStorage() {
		try {
			FileUtils.forceDelete(new File(internalFolder));
			FileUtils.forceDelete(new File(dataFolder));
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	@Test
	public void testSaveCurrentEncoder() {

		cleanStorage();

		int year = Calendar.getInstance().get(Calendar.YEAR);
		EncoderNoEncryptionSimulator encoder = new EncoderNoEncryptionSimulator(0, 1, 0, 99999, year, 1);

		CodeType type = new CodeType(1);

		storage.saveCurrentEncoder(encoder);

		Assert.assertEquals(1, storage.getCurrentEncoder(type).getId());

	}

	@Test
	public void testSaveEncoder() {

		cleanStorage();

		int year = Calendar.getInstance().get(Calendar.YEAR);
		int encoderId = 1;
		EncoderNoEncryptionSimulator encoder = new EncoderNoEncryptionSimulator(0, encoderId, 0, 99999, year, 1);

		CodeType type = new CodeType(1);
		storage.saveEncoders(year, encoder);

		Assert.assertEquals(1, storage.getPendingEncoders().get(0).getId());

		storage.confirmEncoder(encoderId);

		Assert.assertEquals(1, storage.useNextEncoder(type).getId());
	}

	@Test
	public void testSaveAuthenticator() {
		cleanStorage();
		final DummyAuthenticator authenticator = new DummyAuthenticator();

		storage.saveAuthenticator(authenticator);
		final DummyAuthenticator auth = (DummyAuthenticator) storage.getAuthenticator();

		Assert.assertNotNull(auth);
	}

	@Ignore
	@Test
	public void testSaveProduction() throws IOException, Exception {

		// clean the production data first
		cleanStorage();

		Product[] products1 = new Product[10];
		for (int i = 0; i < products1.length; i++) {
			Product p = new Product();
			p.setCode(new Code("" + i));
			p.setStatus(ProductStatus.AUTHENTICATED);
			p.setSku(new SKU(i));
			products1[i] = p;
		}
		Product[] products2 = new Product[15];
		for (int i = 0; i < products2.length; i++) {
			Product p = new Product();
			p.setCode(new Code("" + i));
			p.setStatus(ProductStatus.TYPE_MISMATCH);
			p.setSku(new SKU(i));
			products2[i] = p;
		}

		storage.saveProduction(products1);
		// wait a bit a have a new timepstamp for the file
		ThreadUtils.waitForNextTimeStamp();
		storage.saveProduction(products2);
		// wait a bit a have a new timepstamp for the file
		ThreadUtils.waitForNextTimeStamp();
		// storage.saveProduction(products3);

		storage.packageProduction(10000);

		PackagedProducts pakProducts = storage.getABatchOfProducts();

		// save 10 and then save 15, if then we asked for a batch
		// we get the content of both file so 10, as it will return the 1st file
		// Assert.assertTrue(pakProducts.getProduct().size() == 10);

		int i = 0;
		// test if what we saved is what we loaded
		for (Product aProduct : pakProducts.getProducts()) {
			if (i < products1.length) {
				Assert.assertEquals(products1[i].getStatus(), aProduct.getStatus());
			} else {
				Assert.assertEquals(products2[i - 10].getStatus(), aProduct.getStatus());
			}
			i++;
		}
		storage.notifyDataSentToRemoteServer();

		pakProducts = storage.getABatchOfProducts();

	}

	static class DummyAuthenticator implements IAuthenticator {

		private static final long serialVersionUID = 1L;

		@Override
		public IDecodedResult decode(String mode, String encryptedCode) throws CryptographyException {
			IDecodedResult result = new DummyResult();
			result.setAuthenticated(true);
			return result;
		}

		private class DummyResult implements IDecodedResult {

			boolean authenticated = true;

			@Override
			public boolean isAuthenticated() {
				return authenticated;
			}

			@Override
			public void setAuthenticated(boolean authenticated) {
				this.authenticated = authenticated;
			}

		}
	}

	@Test
	public void testSaveSelectedProductionParamters() {

		cleanStorage();
		// create production parameters
		ProductionParameters productionParam = new ProductionParameters(ProductionMode.EXPORT, new SKU(1, "SKU002"),
				"12345");

		storage.saveSelectedProductionParamters(productionParam);
		ProductionParameters paramFromFile = storage.getSelectedProductionParameters();

		Assert.assertEquals(ProductionMode.EXPORT.getDescription(), paramFromFile.getProductionMode().getDescription());
		Assert.assertEquals("12345", paramFromFile.getBarcode());
		Assert.assertEquals("SKU002", paramFromFile.getSku().getDescription());
	}

	protected static Logger logger = LoggerFactory.getLogger(FileStorageTest.class);

	@Test
	public void testSaveStatistics2() {
		cleanStorage();
		StatisticsValues statValue = new StatisticsValues();
		statValue.set(new StatisticsKeyGood(), 25);
		statValue.set(new StatisticsKeyBad(), 10);

		storage.saveStatistics(statValue);

		StatisticsValues restoredValue = storage.getStatistics();
		Assert.assertEquals(25, restoredValue.get(new StatisticsKeyGood()));
		Assert.assertEquals(10, restoredValue.get(new StatisticsKeyBad()));
	}

	@Test
	public void testSaveGetProductionParameters() {
		cleanStorage();
		// create production parameters root node
		ProductionParameterRootNode rootNode = new ProductionParameterRootNode();
		ProductionModeNode standardModeNode = new ProductionModeNode(ProductionMode.STANDARD);
		SKUNode skuNode1 = new SKUNode(new SKU(1, "SKU001"));
		standardModeNode.addChildren(skuNode1);
		rootNode.addChildren(standardModeNode);

		storage.saveProductionParameters(rootNode);

		ProductionParameterRootNode nodeFromFile = storage.getProductionParameters();

		Assert.assertEquals(1, nodeFromFile.getChildren().size());
		Assert.assertEquals(Messages.get(ProductionMode.STANDARD.getDescription()), nodeFromFile.getChildren().get(0)
				.getText());
		Assert.assertEquals("SKU001", nodeFromFile.getChildren().get(0).getChildren().get(0).getText());
	}

	@Test
	public void testEncoderInfo() throws CryptographyException {

		cleanStorage();

		List<IEncoder> encoders = new ArrayList<IEncoder>();

		Date onClientDate = new Date();
		// buffer
		IEncoder e1 = createEncoder(0, 1, onClientDate);
		IEncoder e2 = createEncoder(1, 2, onClientDate);
		storage.saveEncoders(2012, e1, e2);

		// current
		IEncoder currentEncoder = createEncoder(2, 1, onClientDate);
		currentEncoder.getEncryptedCodes(1).get(0);
		Assert.assertNotNull(currentEncoder.getFirstCodeDate());
		Assert.assertNotNull(currentEncoder.getLastCodeDate());
		storage.saveCurrentEncoder(currentEncoder);

		// finished
		IEncoder ef = createEncoder(3, 2, onClientDate);
		storage.saveFinishedEncoder(ef);

		encoders.addAll(Arrays.asList(e1, e2, currentEncoder, ef));

		List<EncoderInfo> infos = storage.getAllEndodersInfo();
		for (EncoderInfo info : infos) {
			compareEncoderInfoToEncoder(encoders.get((int) info.getEncoderId()), info);
		}
	}

	public void compareEncoderInfoToEncoder(IEncoder encoder, EncoderInfo info) {
		Assert.assertEquals(encoder.getId(), info.getEncoderId());
		Assert.assertEquals(encoder.getCodeTypeId(), info.getCodeTypeId());
		Assert.assertEquals(encoder.getFirstCodeDate(), info.getFirstCodeDate());
		Assert.assertEquals(encoder.getLastCodeDate(), info.getLastCodeDate());
	}

	private IEncoder createEncoder(int id, int codeTypeId, Date onClientDate) {
		EncoderNoEncryptionSimulator encoder = new EncoderNoEncryptionSimulator(0, id, 0, 99999, 2012, codeTypeId);
		encoder.setOnClientDate(onClientDate);
		return encoder;
	}

}
