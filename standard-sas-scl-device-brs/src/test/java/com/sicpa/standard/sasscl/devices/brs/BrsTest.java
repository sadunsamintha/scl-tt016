package com.sicpa.standard.sasscl.devices.brs;

import static com.sicpa.standard.client.common.eventbus.service.EventBusService.post;
import static com.sicpa.standard.sasscl.devices.brs.Brs.NTF_BRS_CAMERAS_CONNECTED;
import static com.sicpa.standard.sasscl.devices.brs.Brs.NTF_BRS_FRAUD1;
import static com.sicpa.standard.sasscl.devices.brs.Brs.NTF_BRS_FRAUD2;
import static com.sicpa.standard.sasscl.devices.brs.Brs.NTF_BRS_JAM_DETECTED1;
import static com.sicpa.standard.sasscl.devices.brs.Brs.NTF_BRS_JAM_DETECTED2;
import static com.sicpa.standard.sasscl.devices.brs.Brs.NTF_BRS_PRODUCT_COUNTER1;
import static com.sicpa.standard.sasscl.devices.brs.Brs.NTF_BRS_PRODUCT_COUNTER2;
import static com.sicpa.standard.sasscl.devices.brs.Brs.NTF_BRS_SKU_INFO;
import static com.sicpa.standard.sasscl.devices.brs.Brs.UNREAD;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.sasscl.business.statistics.IStatistics;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.devices.plc.PlcVariableMap;
import com.sicpa.standard.sasscl.devices.plc.event.PlcEvent;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcIntegerVariableDescriptor;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.model.SkuCode;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKeyBad;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKeyGood;
import com.sicpa.standard.sasscl.model.statistics.StatisticsValues;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.skucheck.SkuCheckFacade;
import com.sicpa.standard.sasscl.skucheck.acquisition.statistics.IAcquisitionStatistics;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EventBusService.class)
public class BrsTest {

	private Brs brs;
	private PlcProvider plcProvider;
	private SkuCheckFacade skuCheckFacade;
	private PlcIntegerVariableDescriptor brsPlcLineTypeVarDesc;
	private PlcIntegerVariableDescriptor brsPlcNotificationCntVarDesc;
	private IStatistics productionStatistics;
	private BrsStateListener brsStateListener;
	private BrsAggregateModel brsAggregateModel;

	@SuppressWarnings("serial")
	@Before
	public void setUp() throws Exception {

		plcProvider = Mockito.mock(PlcProvider.class);

		brsPlcLineTypeVarDesc = Mockito.mock(PlcIntegerVariableDescriptor.class);
		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brsPlcNotificationCntVarDesc = Mockito.mock(PlcIntegerVariableDescriptor.class);
		when(brsPlcNotificationCntVarDesc.getValue()).thenReturn(5);

		com.sicpa.standard.sasscl.model.statistics.StatisticsValues statisticsValues = new StatisticsValues();
		statisticsValues.set(new StatisticsKeyGood(), 97);
		statisticsValues.set(new StatisticsKeyBad(), 3);
		statisticsValues.set(StatisticsKey.TOTAL, 100);
		productionStatistics = Mockito.mock(IStatistics.class);
		when(productionStatistics.getValues()).thenReturn(statisticsValues);

		SkuCheckFacadeProvider skuCheckFacadeProvider = Mockito.mock(SkuCheckFacadeProvider.class);
		skuCheckFacade = Mockito.mock(SkuCheckFacade.class);
		when(skuCheckFacadeProvider.get()).thenReturn(skuCheckFacade);

		brsStateListener = Mockito.mock(BrsStateListener.class);
		brsStateListener.setPlcAdaptor(plcProvider.get());

		brsAggregateModel = new BrsAggregateModel();

		brs = new Brs(Mockito.mock(BrsConfigBean.class), skuCheckFacadeProvider, plcProvider, brsPlcLineTypeVarDesc,
				brsPlcNotificationCntVarDesc, Mockito.mock(IAcquisitionStatistics.class), productionStatistics,
				brsStateListener, brsAggregateModel, Mockito.mock(ITooManyUnreadHandler.class));

		// Inject the executor to be able to run the test in the same thread.
		ExecutorService executor = Mockito.mock(ExecutorService.class);
		Mockito.doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				((Runnable) invocation.getArguments()[0]).run();
				return null;
			}
		}).when(executor).execute(Mockito.<Runnable> any());
		brs.setExecutorService(executor);

		// noinspection serial,SerializableHasSerializationMethods
		new PlcVariableMap(new HashMap<String, String>() {

			{
				put(NTF_BRS_CAMERAS_CONNECTED, NTF_BRS_CAMERAS_CONNECTED);
				put(NTF_BRS_SKU_INFO, NTF_BRS_SKU_INFO);
				put(NTF_BRS_PRODUCT_COUNTER1, NTF_BRS_PRODUCT_COUNTER1);
				put(NTF_BRS_PRODUCT_COUNTER2, NTF_BRS_PRODUCT_COUNTER2);
				put(NTF_BRS_FRAUD1, NTF_BRS_FRAUD1);
				put(NTF_BRS_FRAUD2, NTF_BRS_FRAUD2);
				put(NTF_BRS_JAM_DETECTED1, NTF_BRS_JAM_DETECTED1);
				put(NTF_BRS_JAM_DETECTED2, NTF_BRS_JAM_DETECTED2);
			}
		});
	}

	@Test
	public void wrongConfigurationOfLineTypeVar() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(1);

		onCamerasConnected(11, EMPTY);
	}

	@Test
	public void onAllCamerasConnected() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		PowerMockito.mockStatic(EventBusService.class);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_CAMERAS_CONNECTED, (short) 15));

		PowerMockito.verifyStatic();

		post(new MessageEvent(plcProvider, MessageEventKey.BRS.BRS_PLC_ALL_CAMERAS_CONNECTED));
	}

	private void onCamerasConnected(int mask, String expected) {

		PowerMockito.mockStatic(EventBusService.class);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_CAMERAS_CONNECTED, (short) mask));

		PowerMockito.verifyStatic();

		post(new MessageEvent(plcProvider, MessageEventKey.BRS.BRS_PLC_CAMERAS_DISCONNECTION, expected));
	}

	@Test
	public void onOneCameraDisconnected() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		onCamerasConnected(14, "1");
	}

	@Test
	public void onAllCamerasDisconnected() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		PowerMockito.mockStatic(EventBusService.class);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_CAMERAS_CONNECTED, (short) 0));

		PowerMockito.verifyStatic();

		post(new MessageEvent(plcProvider, MessageEventKey.BRS.BRS_PLC_ALL_CAMERAS_DISCONNECTED));
	}

	@Test
	public void negativeMaskOnCamerasConnected() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		PowerMockito.mockStatic(EventBusService.class);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_CAMERAS_CONNECTED, (short) -1));

		PowerMockito.verifyStatic(Mockito.never());
	}

	@Test
	public void tooWideMaskOnCamerasConnected() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		onCamerasConnected(46, "1");
	}

	@Test
	public void onSkuInfoWithoutDelimiter() {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, "barcode1234" + (char) 0 + "some junk data"));

		verifyZeroInteractions(skuCheckFacade); // No info was processed.
	}

	@Test
	public void onSkuInfoWithoutSku() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, ":34:XXYYZZ;XXXXXXXXXXXX"));

		verifyZeroInteractions(skuCheckFacade); // No info was processed.
	}

	@Test
	public void onSkuInfoNoCount() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, "43843843::111;XXXXXXX"));

		verifyZeroInteractions(skuCheckFacade); // No info was processed.
	}

	@Test
	public void onSkuInfoInvalidCount() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, "354684:_bxm:0;XXXXXYYYZZUZUZSS"));

		verifyZeroInteractions(skuCheckFacade); // No info was processed.
	}

	@Test
	public void onSkuInfoDuringStoppedLine() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, "3854384:23"));

		verifyZeroInteractions(skuCheckFacade);
	}

	@Test
	public void onValidOfflineFieldOfflineNotification() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, "354684:25:0:987;XXXXXYYYZZUZUZSS"));

		verifyZeroInteractions(skuCheckFacade); // No info was processed.
	}

	@Test
	public void onValidOfflineFieldOnlineNotification() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, "354684:25:1:987;XXXXXYYYZZUZUZSS"));

		verify(skuCheckFacade, times(25)).addSku(new SkuCode("354684"));
		verify(skuCheckFacade, times(1)).querySkus();
	}

	@Test
	public void onInvalidOfflineField() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, "354684:25:90:987;XXXXXYYYZZUZUZSS"));

		verifyZeroInteractions(skuCheckFacade); // No info was processed.
	}

	@Test
	public void onCountZeroNotifications() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brs.processStateChanged(new ApplicationFlowStateChangedEvent(ApplicationFlowState.STT_STARTING,
				ApplicationFlowState.STT_STARTED, ""));
		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, UNREAD + ":0:1:156;XYYYZ"));

		verify(skuCheckFacade, never()).addUnread();
		verify(skuCheckFacade, never()).querySkus();
	}

	@Test
	public void onNegativeCountOfUnreadSkus() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brs.processStateChanged(new ApplicationFlowStateChangedEvent(ApplicationFlowState.STT_STARTING,
				ApplicationFlowState.STT_STARTED, ""));
		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, UNREAD + ":-5"));

		verify(skuCheckFacade, never()).addUnread();
		verify(skuCheckFacade, never()).querySkus();
	}

	@Test
	public void onUnreadSku() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brs.processStateChanged(new ApplicationFlowStateChangedEvent(ApplicationFlowState.STT_STARTING,
				ApplicationFlowState.STT_STARTED, ""));
		int count = 23;
		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, UNREAD + ":" + count + ":1:323;XZZZXYX"));

		verify(skuCheckFacade, times(count)).addUnread();
		verify(skuCheckFacade, times(1)).querySkus();
	}

	@Test
	public void onReadSku() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brs.processStateChanged(new ApplicationFlowStateChangedEvent(ApplicationFlowState.STT_STARTING,
				ApplicationFlowState.STT_STARTED, ""));
		int count = 23;
		String barCode = "38684168";
		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, barCode + ":" + count + ":1:876;XXXZZZZYY"));

		verify(skuCheckFacade, times(count)).addSku(new SkuCode(barCode));
		verify(skuCheckFacade, times(1)).querySkus();
	}

	/** Notification is no longer applicable **/
	@Ignore
	@Test(expected = NumberFormatException.class)
	public void onOfflineProductionEmptyCount() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_PRODUCT_COUNTER1, ""));
	}

	/** Notification is no longer applicable **/
	@Ignore
	@Test(expected = NumberFormatException.class)
	public void onOfflineProductionInvalidCount() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_PRODUCT_COUNTER1, "xml"));
	}

	@Test
	public void onOfflineProduction() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		int count = 43;
		brs.onPlcEvent(new PlcEvent(NTF_BRS_PRODUCT_COUNTER1, count));

		verify(skuCheckFacade, times(0)).addOfflineProduction(count);
	}

	@Test
	public void onUnknownEvent() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(0);

		brs.onPlcEvent(new PlcEvent("Some Unknown Event", Boolean.TRUE));

		// verifyZeroInteractions(plcProvider, skuCheckFacade);
		verifyZeroInteractions(skuCheckFacade);
	}

	@Test
	public void onSkuInfoWithAggregationWithoutUnreads() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(5);
		when(brsPlcNotificationCntVarDesc.getValue()).thenReturn(5);

		int threshold = LineType.masterCount(5) * (Integer) brsPlcNotificationCntVarDesc.getValue();

		String barCode = "38684168";
		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, barCode + ":" + 2 + ":1:876;XXXZZZZYY"));
		verifyZeroInteractions(skuCheckFacade);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, barCode + ":" + 8 + ":1:877;XXXZZZZYY"));
		verify(skuCheckFacade, times(threshold)).addSku(new SkuCode(barCode));
		verify(skuCheckFacade, times(1)).querySkus();

	}

	@Test
	public void onSkuInfoWithAggregationWithUnreads() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(4);
		when(brsPlcNotificationCntVarDesc.getValue()).thenReturn(5);

		String barCode = "38684168";
		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, barCode + ":" + 2 + ":1:876;XXXZZZZYY"));
		verifyZeroInteractions(skuCheckFacade);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, Brs.UNREAD + ":" + 8 + ":1:877;XXXZZZZYY"));
		verify(skuCheckFacade, times(2)).addSku(new SkuCode(barCode));
		verify(skuCheckFacade, times(3)).addUnread();
		verify(skuCheckFacade, times(1)).querySkus();

	}

	@Test
	public void onSkuInfoWithPartialAggregationWithoutUnreads() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(5);
		when(brsPlcNotificationCntVarDesc.getValue()).thenReturn(5);

		String barCode = "38684168";
		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, barCode + ":" + 2 + ":1:876;XXXZZZZYY"));
		verifyZeroInteractions(skuCheckFacade);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, "init:0:1:877;"));

		verify(skuCheckFacade, times(2)).addSku(new SkuCode(barCode));
		verify(skuCheckFacade, times(1)).querySkus();

	}

	@Test
	public void onSkuInfoWithPartialAggregationWithUnreads() throws Exception {

		when(brsPlcLineTypeVarDesc.getValue()).thenReturn(4);
		when(brsPlcNotificationCntVarDesc.getValue()).thenReturn(5);

		String barCode = "38684168";
		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, barCode + ":" + 2 + ":1:876;XXXZZZZYY"));
		verifyZeroInteractions(skuCheckFacade);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, Brs.UNREAD + ":" + 6 + ":1:877;XXXZZZZYY"));
		verifyZeroInteractions(skuCheckFacade);

		brs.onPlcEvent(new PlcEvent(NTF_BRS_SKU_INFO, "init:0:1:878;"));
		verify(skuCheckFacade, times(2)).addSku(new SkuCode(barCode));
		verify(skuCheckFacade, times(2)).addUnread();
		verify(skuCheckFacade, times(1)).querySkus();

	}
}
