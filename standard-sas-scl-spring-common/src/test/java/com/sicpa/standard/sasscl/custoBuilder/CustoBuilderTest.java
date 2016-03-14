package com.sicpa.standard.sasscl.custoBuilder;

import static com.sicpa.standard.sasscl.ioc.BeansName.REMOTE_SERVER_PRODUCT_STATUS_MAPPING;
import static com.sicpa.standard.sasscl.ioc.BeansName.STATISTICS_PRODUCTS_STATUS_MAPPER;
import static com.sicpa.standard.sasscl.ioc.BeansName.STATISTICS_VIEW_MAPPER;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.sasscl.business.statistics.IStatisticsKeyToViewDescriptorMapping;
import com.sicpa.standard.sasscl.business.statistics.mapper.IProductStatusToStatisticKeyMapper;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.remote.mapping.IRemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.ioc.BeansName;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyFactory;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.thoughtworks.xstream.XStream;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BeanProvider.class)
public class CustoBuilderTest {

	SKU sku;
	CustomProperty<Boolean> isCompliant = new CustomProperty<>("compliant", Boolean.class, Boolean.TRUE);

	@Before
	public void before() {
		sku = new SKU();
		CustomizablePropertyFactory.setCustomizablePropertyDefinition(null);
	}

	@Test
	public void testModelAddCustomPropertySuccess() {
		CustoBuilder.Model.addPropertyToClass(sku.getClass(), isCompliant);
		sku.setProperty(isCompliant, true);

		Assert.assertEquals(true, sku.getProperty(isCompliant).booleanValue());
	}

	@Test(expected = RuntimeException.class)
	public void testModelAddCustomPropertyFail() {
		sku.setProperty(isCompliant, true);
	}

	@Test
	public void testModelAddCustomPropertySerializationXStream() {
		CustoBuilder.Model.addPropertyToClass(sku.getClass(), isCompliant);
		sku.setProperty(isCompliant, true);

		XStream xStream = new XStream();
		String skuStr = xStream.toXML(sku);
		SKU skuFromXML = (SKU) xStream.fromXML(skuStr);

		Assert.assertEquals(true, skuFromXML.getProperty(isCompliant).booleanValue());
	}

	@Test
	public void testModelGetDefaultValue() {
		CustoBuilder.Model.addPropertyToClass(sku.getClass(), isCompliant);

		Assert.assertEquals(true, sku.getProperty(isCompliant).booleanValue());
	}

	@Test
	public void testModelOverwriteDefaultValue() {
		CustoBuilder.Model.addPropertyToClass(sku.getClass(), isCompliant);
		sku.setProperty(isCompliant, false);

		Assert.assertEquals(false, sku.getProperty(isCompliant).booleanValue());
	}

	@Test
	public void testAddActionOnStarting() {
		AtomicBoolean actionDone = new AtomicBoolean(false);
		CustoBuilder.ProcessFlow.addActionOnStartingProduction(() -> actionDone.set(true));
		EventBusService.post(new ApplicationFlowStateChangedEvent(ApplicationFlowState.STT_STARTING));
		Assert.assertTrue(actionDone.get());
	}

	@Test
	public void testAddStatistics() {
		int idOnRemote = 1234;
		ProductStatus status = ProductStatus.AUTHENTICATED;
		StatisticsKey statKey = StatisticsKey.GOOD;
		String langKey = "bla.bla";
		int indexOnView = 5;
		Color colorOnView = Color.BLUE;
		PowerMockito.mockStatic(BeanProvider.class);
		IProductStatusToStatisticKeyMapper statsMapping = mock(IProductStatusToStatisticKeyMapper.class);
		IRemoteServerProductStatusMapping remoteMapping = mock(IRemoteServerProductStatusMapping.class);
		IStatisticsKeyToViewDescriptorMapping viewMapping = mock(IStatisticsKeyToViewDescriptorMapping.class);

		BDDMockito.given(BeanProvider.getBean(STATISTICS_PRODUCTS_STATUS_MAPPER)).willReturn(statsMapping);
		BDDMockito.given(BeanProvider.getBean(REMOTE_SERVER_PRODUCT_STATUS_MAPPING)).willReturn(remoteMapping);
		BDDMockito.given(BeanProvider.getBean(STATISTICS_VIEW_MAPPER)).willReturn(viewMapping);

		CustoBuilder.Statistics.addStatistics(status, statKey, idOnRemote, colorOnView, indexOnView, langKey);

		verify(statsMapping, Mockito.times(1)).add(status, statKey);
		verify(remoteMapping, Mockito.times(1)).add(status, idOnRemote);
		verify(viewMapping, Mockito.times(1)).add(statKey, colorOnView, indexOnView, langKey);

	}
}
