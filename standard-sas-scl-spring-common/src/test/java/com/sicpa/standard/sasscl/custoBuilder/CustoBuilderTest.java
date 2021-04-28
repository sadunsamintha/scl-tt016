package com.sicpa.standard.sasscl.custoBuilder;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.sasscl.business.statistics.IStatisticsKeyToViewDescriptorMapping;
import com.sicpa.standard.sasscl.business.statistics.mapper.IProductStatusToStatisticKeyMapper;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;
import com.sicpa.standard.sasscl.devices.remote.datasender.IPackageSenderGlobal;
import com.sicpa.standard.sasscl.devices.remote.mapping.IRemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.sicpa.standard.sasscl.ioc.BeansName.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
@PrepareForTest(BeanProvider.class)
public class CustoBuilderTest {

	@Test
	public void testAddActionOnStarting() {
		AtomicBoolean actionDone = new AtomicBoolean(false);
		CustoBuilder.addActionOnStartingProduction(() -> actionDone.set(true));
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
		IPackageSenderGlobal sender = mock(IPackageSenderGlobal.class);
		IRemoteServer server = mock(IRemoteServer.class);

		given(BeanProvider.getBean(STATISTICS_PRODUCTS_STATUS_MAPPER)).willReturn(statsMapping);
		given(BeanProvider.getBean(REMOTE_SERVER_PRODUCT_STATUS_MAPPING)).willReturn(remoteMapping);
		given(BeanProvider.getBean(STATISTICS_VIEW_MAPPER)).willReturn(viewMapping);
		given(BeanProvider.getBean(REMOTE_SERVER)).willReturn(server);
		given(BeanProvider.getBean(PACKAGE_SENDER)).willReturn(sender);

		CustoBuilder.addProductStatus(status, statKey, idOnRemote, colorOnView, indexOnView, langKey, true, true);

		verify(statsMapping, Mockito.times(1)).add(status, statKey);
		verify(remoteMapping, Mockito.times(1)).add(status, idOnRemote);
		verify(viewMapping, Mockito.times(1)).add(statKey, colorOnView, indexOnView, langKey, true);
		verify(sender, Mockito.times(1)).addToActivatedPackager(status);

	}
}
