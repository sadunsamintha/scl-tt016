package com.sicpa.standard.sasscl.business.coding.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sicpa.standard.client.common.storage.StorageException;
import com.sicpa.standard.printer.xcode.ExtendedCode;
import com.sicpa.standard.sasscl.business.coding.CodeReceivedFailedException;
import com.sicpa.standard.sasscl.business.coding.ICodeReceiver;
import com.sicpa.standard.sasscl.business.coding.RequestCodesEvent;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.common.storage.QuarantineReason;
import com.sicpa.standard.sasscl.config.GlobalConfig;
import com.sicpa.standard.sasscl.devices.printer.AbstractPrinterAdaptor;
import com.sicpa.standard.sasscl.devices.printer.PrinterAdaptorException;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.EncoderInfo;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.generator.IEncoder;
import com.sicpa.standard.sasscl.sicpadata.generator.impl.EncoderNoEncryptionSimulator;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;

public class CodingTest {

	private TestStorage storage;
	private TestPrinter printer;
	private Coding coding;

	@Before
	public void setup() {

		printer = new TestPrinter();
		coding = new Coding(new GlobalConfig(), storage = new TestStorage());
		ProductionParameters productionParameters = new ProductionParameters();
		SKU sku = new SKU(1);
		sku.setCodeType(new CodeType(1));
		productionParameters.setSku(sku);
		productionParameters.setProductionMode(ProductionMode.STANDARD);
		coding.setProductionParameters(productionParameters);
	}

	@Test
	public void normalTest() {

		coding.requestCodes(new RequestCodesEvent(50, printer));
		for (int i = 1; i <= 50; i++) {
			Assert.assertThat(printer.codes.get(i - 1), IsEqual.equalTo(String.valueOf(i)));
		}
	}

	@Test
	public void encoderException() {

		storage.encoders.add(new TestEncoder(20000, 25000, 2030));
		// when using the TestEncoder, there will be an exception, so test that no code is sent to the printer
		coding.requestCodes(new RequestCodesEvent(500, Mockito.mock(ICodeReceiver.class)));
		Assert.assertThat(printer.codes, IsNull.nullValue());
	}

	@Test
	public void runOutOfCodesException() {
		// ask for way to many codes, the printers should not get codes
		coding.requestCodes(new RequestCodesEvent(5000000, Mockito.mock(ICodeReceiver.class)));
		Assert.assertThat(printer.codes, IsNull.nullValue());
	}

	@Test
	public void noEncoderInStorageTest() {
		// ---no encoders in storage
		storage.currentEncoder = null;
		storage.encoders.clear();
		coding.requestCodes(new RequestCodesEvent(50, Mockito.mock(ICodeReceiver.class)));
		Assert.assertThat(printer.codes, IsNull.nullValue());
	}

	public static int getCurrentYear() {

		return new GregorianCalendar().get(Calendar.YEAR);
	}

	private class TestStorage implements IStorage {

		ArrayList<EncoderNoEncryptionSimulator> encoders = new ArrayList<EncoderNoEncryptionSimulator>();
		IEncoder currentEncoder = null;

		public TestStorage() {

			for (int i = 0; i < 6; i++) {
				this.encoders.add(new EncoderNoEncryptionSimulator(0, encoderCount++, i * 20 + 1, (i + 1) * 20,
						getCurrentYear(), 1234));
			}
		}

		@Override
		public IEncoder useNextEncoder(final CodeType c) {

			if (this.encoders.size() > 0) {
				this.currentEncoder = this.encoders.get(0);
				this.encoders.remove(0);
				System.out.println("OriginalCodingTest$TestStorage.useNextEncoder " + encoders + ": " + currentEncoder);
				return this.currentEncoder;
			} else {
				this.currentEncoder = null;
				System.out.println("OriginalCodingTest$TestStorage.useNextEncoder " + currentEncoder);
				return null;
			}

		}

		@Override
		public void saveEncoders(final int year, final IEncoder... encoders) {
			for (IEncoder enc : encoders) {
				this.encoders.add((EncoderNoEncryptionSimulator) enc);
			}
		}

		@Override
		public void saveCurrentEncoder(IEncoder encoder) {
			currentEncoder = encoder;
		}

		@Override
		public IEncoder getCurrentEncoder(final CodeType c) {

			return this.currentEncoder;
		}

		@Override
		public void remove(final String id) throws StorageException {

		}

		@Override
		public void saveStatistics(final StatisticsValues stats) {

		}

		@Override
		public void saveSelectedProductionParamters(final ProductionParameters param) {

		}

		@Override
		public void saveProduction(final Product[] products) throws StorageException {

		}

		@Override
		public void save(final Serializable object, final String id) throws StorageException {

		}

		@Override
		public void notifyDataSentToRemoteServer() {

		}

		@Override
		public void notifyDataErrorSendingToRemoteServer() {

		}

		@Override
		public Object load(final String id) throws StorageException {

			return null;
		}

		@Override
		public StatisticsValues getStatistics() {

			return null;
		}

		@Override
		public ProductionParameters getSelectedProductionParameters() {

			return null;
		}

		@Override
		public ProductionParameterRootNode getProductionParameters() {

			return null;
		}

		@Override
		public void saveProductionParameters(final ProductionParameterRootNode node) {

		}

		@Override
		public PackagedProducts getABatchOfProducts() {

			return null;
		}

		@Override
		public void packageProduction(final int batchSize) {

		}

		@Override
		public void cleanUpOldSentProduction() {

		}

		@Override
		public IAuthenticator getAuthenticator() {

			return null;
		}

		@Override
		public void saveAuthenticator(final IAuthenticator auth) {

		}

		@Override
		public int getAvailableNumberOfEncoders(final CodeType c, final int year) {

			return 0;
		}

		@Override
		public String getStorageInfo() {

			return null;
		}

		@Override
		public int getBatchOfProductsCount() {

			return 0;
		}

		@Override
		public void saveToQuarantine(Serializable object, String id, QuarantineReason reason) {

		}

		@Override
		public List<EncoderInfo> getAllEndodersInfo() {
			return null;
		}

		@Override
		public List<IEncoder> getPendingEncoders() {
			return null;
		}

		@Override
		public void notifyEncodersInfoSent(List<EncoderInfo> encoderInfos) {

		}

		@Override
		public void confirmEncoder(long id) {

		}

		@Override
		public void removePendingEncoder(long id) {

		}

		@Override
		public void quarantineEncoder(long id) {

		}
	}

	private static class TestPrinter extends AbstractPrinterAdaptor {

		List<String> codes;
		List<ExtendedCode> xcodes;

		boolean crash = false;

		@Override
		public void sendCodesToPrint(final List<String> codes) {

			this.codes = codes;
		}

		@Override
		protected void doDisconnect() throws PrinterAdaptorException {

		}

		@Override
		protected void doConnect() throws PrinterAdaptorException {

		}

		@Override
		public void doStart() throws PrinterAdaptorException {

		}

		@Override
		public void doStop() throws PrinterAdaptorException {

		}

		@Override
		public void provideCode(final List<String> codes, Object requestor) throws CodeReceivedFailedException {

			if (crash) {
				crash = false;
				throw new CodeReceivedFailedException();
			}
			sendCodesToPrint(codes);
		}

		@Override
		public void resetCodes() throws PrinterAdaptorException {

		}

		@Override
		public void switchOff() throws PrinterAdaptorException {

		}

		@Override
		public void switchOn() throws PrinterAdaptorException {

		}

		@Override
		public boolean isBlockProductionStart() {
			return true;
		}

		@Override
		public void sendExtendedCodesToPrint(List<ExtendedCode> codes) {
			this.xcodes = codes;
		}

		@Override
		public void provideExtendedCode(List<ExtendedCode> codes,
				Object requestor) throws CodeReceivedFailedException {
			if (crash) {
				crash = false;
				throw new CodeReceivedFailedException();
			}
			sendExtendedCodesToPrint(codes);
			
		}
	}

	private static int encoderCount = 0;

	private class TestEncoder extends EncoderNoEncryptionSimulator {

		private static final long serialVersionUID = 3869359854482960152L;

		public TestEncoder(final int min, final int max, final int year) {

			super(0, encoderCount++, min, max, year, 0);
		}

		@Override
		public String getEncryptedCode() throws CryptographyException {

			if (getSequence() > 20020) {
				throw new CryptographyException();
			}
			return super.getEncryptedCode();
		}
		@Override
		public ExtendedCode getExtendedCode() throws CryptographyException {

			if (getSequence() > 20020) {
				throw new CryptographyException();
			}
			return super.getExtendedCode();
		}
	}
}
