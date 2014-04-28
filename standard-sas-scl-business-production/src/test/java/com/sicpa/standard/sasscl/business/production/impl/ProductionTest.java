package com.sicpa.standard.sasscl.business.production.impl;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.fest.reflect.core.Reflection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.sicpa.standard.client.common.storage.StorageException;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.common.storage.productPackager.DefaultProductsPackager;
import com.sicpa.standard.sasscl.config.GlobalConfig;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.model.PackagedProducts;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;

@SuppressWarnings("unchecked")
public class ProductionTest {

	private Production production;
	IStorage storage;
	IRemoteServer remoteServer;

	@Before
	public void setUp() throws Exception {
		GlobalConfig config = new GlobalConfig();
		config.setProductionDataSerializationErrorThreshold(2);
		storage = mock(IStorage.class);
		remoteServer = mock(IRemoteServer.class);
		when(remoteServer.isConnected()).thenReturn(true);
		production = new Production(config, storage, remoteServer);
		when(storage.getBatchOfProductsCount()).thenReturn(10);

		DefaultProductsPackager packager = new DefaultProductsPackager();
		production.setProductsPackager(packager);
	}

	@Test
	public void receiveCodeTest() {
		List<Product> queue = Reflection.field("products").ofType(List.class).in(production).get();
		Assert.assertEquals(0, queue.size());
		Product product = new Product();
		production.notifyNewProduct(new NewProductEvent(product));

		Assert.assertEquals(1, queue.size());
		Assert.assertTrue(queue.get(0) == product);
	}

	/**
	 * Notify about five products to the module and verify that they are saved in the storage.
	 * 
	 */
	@Test
	public void notify5ProductsAndSavedtoStorage() {

		List<Product> queue = Reflection.field("products").ofType(List.class).in(production).get();
		// queue.clear();
		Assert.assertEquals(0, queue.size());

		// Simulate that production is being notified about 5 new products.
		Product[] products = new Product[5];
		for (int i = 0; i < 5; ++i) {
			Product product = new Product();
			products[i] = product;
			production.notifyNewProduct(new NewProductEvent(product));
		}

		Assert.assertEquals(5, queue.size());

		// Simulate production is called by the scheduler to serialize data.
		production.saveProductionData();

		ArgumentCaptor<Product[]> productCaptor = ArgumentCaptor.forClass(Product[].class);
		try {
			// check that the storage module is called to save production once
			verify(storage, atLeast(1)).saveProduction(productCaptor.capture());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// verify that 5 products where received in the storage to be saved
		// and match the notified to the production module
		Assert.assertEquals(5, productCaptor.getValue().length);

		for (int j = 0; j < products.length; j++) {
			Assert.assertEquals(products[j], productCaptor.getValue()[j]);
		}

		// verify that the queue in Production is now empty
		Assert.assertEquals(0, queue.size());

	}

	/**
	 * Test that the production module is notified about 5 new products. Then simulate a scheduler call to serialize
	 * this products locally, and later another call to send unsent products to remote server.
	 * 
	 * @throws Exception
	 */
	@Test
	public void notify5ProductsSerializeFirstAndSendToRemoteServer() throws Exception {

		List<Product> c = mock(List.class);

		// assume 3 is the batch size
		// first call returns 3, second 2, third and consecutive return 0
		when(c.size()).thenReturn(3).thenReturn(2).thenReturn(0);

		when(storage.getABatchOfProducts()).thenReturn(
				new PackagedProducts(c, 1l, ProductStatus.AUTHENTICATED, "147", 123l,false)).thenReturn(null);

		// first serialize 5 products
		for (int i = 0; i < 5; ++i) {
			production.notifyNewProduct(mock(NewProductEvent.class));
		}

		// simulate scheduler is calling for serialize on disk.
		production.saveProductionData();

		// create batch of products
		production.packageProduction();

		// simulate scheduler calls to send production data.
		production.sendAllProductionData();

		verify(remoteServer, times(1)).sendProductionData((PackagedProducts) anyObject());
	}

	/**
	 * Check that remote server is never called when there were no that in the storage.
	 * 
	 * @throws Exception
	 */
	@Test
	public void noProductsFromStorageThenNoRemoteServerCalled() throws Exception {

		when(storage.getABatchOfProducts()).thenReturn(null);

		production.notifyNewProduct(mock(NewProductEvent.class));

		production.sendAllProductionData();

		verify(remoteServer, never()).sendProductionData((PackagedProducts) anyObject());
		verify(storage, never()).notifyDataSentToRemoteServer();
	}

	/**
	 * Verify that save production is never called in storage when there are not production in memory.
	 * 
	 * @throws Exception
	 */
	@Test
	public void noProductNotificationNoStorageCalledToSerialize() throws Exception {

		// There is no data;
		// Simulate scheduler calls to send production data.
		production.saveProductionData();

		verify(storage, never()).saveProduction((Product[]) anyObject());
	}

	/**
	 * Try to send to remote server first, as there are still not saved in storage, they can't be retrieved and sent to
	 * remote server.
	 * 
	 * @throws Exception
	 */
	@Test
	public void noRemoteDataAndNotify5ProductsSendToRemoteServerFirstAndSerialize() throws Exception {

		when(storage.getABatchOfProducts()).thenReturn(null);

		// create 5 products
		for (int i = 0; i < 5; ++i) {
			production.notifyNewProduct(mock(NewProductEvent.class));
		}

		// simulate scheduler calls to send production data to remote server.
		production.sendAllProductionData();

		// check it is only called once, so that the production returned
		// is of size 0, so it does nothing
		// and sendProduction that should never be called even if
		// we were notified about 5 products
		verify(storage, times(1)).getABatchOfProducts();

		verify(remoteServer, never()).sendProductionData((PackagedProducts) anyObject());
		verify(storage, never()).notifyDataSentToRemoteServer();
	}

	/**
	 * Verify tha storage is never notified about sent data if remote server communication has failed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void sendProductionDataFails() throws Exception {
		List<Product> c = mock(List.class);

		when(c.size()).thenReturn(3);
		when(storage.getABatchOfProducts()).thenReturn(
				new PackagedProducts(c, 1l, ProductStatus.AUTHENTICATED, "147", 123l,false));
		doThrow(new RemoteServerException()).when(remoteServer).sendProductionData((PackagedProducts) anyObject());

		production.sendAllProductionData();

		verify(remoteServer, times(1)).sendProductionData((PackagedProducts) anyObject());
		verify(storage, never()).notifyDataSentToRemoteServer();
	}

	/**
	 * Verify that we try to send to server after tree tries trying to save in storage and failed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void trySendProductionDataAfterSavingFailed3Times() throws Exception {

		doThrow(new StorageException("", new Exception())).when(storage).saveProduction((Product[]) anyObject());

		Product[] products = new Product[5];

		for (int i = 0; i < 5; ++i) {
			Product product = new Product();
			product.setPrinted(false);
			product.setStatus(ProductStatus.AUTHENTICATED);
			products[i] = product;
			production.notifyNewProduct(new NewProductEvent(product));
		}

		production.saveProductionData();
		production.saveProductionData();
		production.saveProductionData();

		verify(remoteServer, atLeast(1)).sendProductionData((PackagedProducts) anyObject());
	}
}
