package com.sicpa.standard.sasscl.business.statistics;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.statistics.impl.Statistics;
import com.sicpa.standard.sasscl.business.statistics.mapper.DefaultProductStatusToStatisticsKeyMapper;
import com.sicpa.standard.sasscl.common.storage.IStorage;
import com.sicpa.standard.sasscl.controller.productionconfig.IProductionConfig;
import com.sicpa.standard.sasscl.controller.productionconfig.config.CameraConfig;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.provider.impl.ProductionConfigProvider;

public class StatisticsTest {

	@Test
	public void testStatistics() {

		ProductionConfigProvider productionConfigProvider = new ProductionConfigProvider();
		IProductionConfig productionConfig = Mockito.mock(IProductionConfig.class);
		Mockito.when(productionConfig.getCameraConfigs()).thenReturn(new ArrayList<CameraConfig>());
		productionConfigProvider.set(productionConfig);

		StatisticsValues values = new StatisticsValues();

		StatisticsKey goodKey = StatisticsKey.GOOD;
		goodKey.setLine("");
		StatisticsKey badKey = StatisticsKey.BAD;
		badKey.setLine("");

		values.set(badKey, 5);
		values.set(goodKey, 150);

		Statistics stats = new Statistics(Mockito.mock(IStorage.class));
		stats.setProductionConfigProvider(productionConfigProvider);
		stats.setStatusMapper(new DefaultProductStatusToStatisticsKeyMapper());

		stats.setValues(values);

		// increase good
		Product product = new Product();
		product.setQc("");
		product.setStatus(ProductStatus.AUTHENTICATED);
		NewProductEvent evt = new NewProductEvent(product);
		stats.notifyNewProduct(evt);
		Assert.assertEquals(5, stats.get(badKey));
		Assert.assertEquals(151, stats.get(goodKey));

		// increase bad
		product = new Product();
		product.setQc("");
		product.setStatus(ProductStatus.NOT_AUTHENTICATED);
		evt = new NewProductEvent(product);
		stats.notifyNewProduct(evt);
		Assert.assertEquals(6, stats.get(badKey));
		Assert.assertEquals(151, stats.get(goodKey));

		// test reset
		stats.reset();
		Assert.assertEquals(0, stats.get(badKey));
		Assert.assertEquals(0, stats.get(goodKey));

	}

	@Test
	public void testSaveStats() {
		IStorage storage = Mockito.mock(IStorage.class);
		StatisticsValues values = new StatisticsValues();
		StatisticsKey goodKey = StatisticsKey.GOOD;
		StatisticsKey badKey = StatisticsKey.BAD;
		values.set(badKey, 5);
		values.set(goodKey, 150);
		Mockito.when(storage.getStatistics()).thenReturn(values);
		Statistics stats = new Statistics(storage);
		stats.setValues(values);

		stats.saveStatistics();

		Mockito.verify(storage).saveStatistics(values);
	}

	@Test
	public void testRestoreStatsSave() {

		IStorage storage = Mockito.mock(IStorage.class);
		StatisticsValues values = new StatisticsValues();
		StatisticsKey goodKey = StatisticsKey.GOOD;
		StatisticsKey badKey = StatisticsKey.BAD;
		values.set(badKey, 5);
		values.set(goodKey, 150);
		Mockito.when(storage.getStatistics()).thenReturn(values);
		Statistics stats = new Statistics(storage);

		// restore from storage
		stats.setValues(storage.getStatistics());

		Assert.assertEquals(5, stats.get(badKey));
		Assert.assertEquals(150, stats.get(goodKey));

	}
}
