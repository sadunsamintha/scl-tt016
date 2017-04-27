package com.sicpa.standard.sasscl.business.activation.offline;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.activation.offline.impl.PlcOfflineCounting;
import com.sicpa.standard.sasscl.business.activation.offline.impl.PlcOfflineCountingValuesProvider;
import com.sicpa.standard.sasscl.devices.plc.IPlcAdaptor;
import com.sicpa.standard.sasscl.devices.plc.PlcAdaptorException;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.sicpa.standard.plc.value.PlcVariable.createBooleanVar;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.addLineIndex;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.replaceLinePlaceholder;

public class PlcOfflineCountingTest {

	private final String RESET_COUNTERS_VAR_NAME = "com.stLine[#x].stOff_Counter.'bResetOffCounters";

	private PlcOfflineCounting poc;

	private PlcProvider plcProvider;

	private IPlcAdaptor plc;

	private IPlcVariable<Boolean> resetCountersVar;

	private SKU sku;

	private ProductionParameters productionParameters;

	private PlcOfflineCountingValuesProvider plcOfflineCountingValuesProvider;

	@Before
	public void setup() throws Exception {
		addLineIndex(1);

		resetCountersVar = createBooleanVar(replaceLinePlaceholder(RESET_COUNTERS_VAR_NAME, 1));

		plc = Mockito.mock(IPlcAdaptor.class);

		plcProvider = new PlcProvider();
		plcProvider.set(plc);

		plcOfflineCountingValuesProvider = Mockito.mock(PlcOfflineCountingValuesProvider.class);

		productionParameters = Mockito.mock(ProductionParameters.class);
		poc = getPlcOfflineCounting();
		sku = getSku();
	}

	@Test
	public void test() throws PlcAdaptorException {
		final int productsCount = 100;

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);

		Date lastStop = calendar.getTime();

		calendar.add(Calendar.HOUR_OF_DAY, 2);

		Date lastProduct = calendar.getTime();

		long deltaProduct = ((lastProduct.getTime() - lastStop.getTime()) / productsCount);

		Mockito.when(plcOfflineCountingValuesProvider.getQuantityProducts(1)).thenReturn(productsCount);
		Mockito.when(plcOfflineCountingValuesProvider.getLastStopDateTime(1)).thenReturn(lastStop);
		Mockito.when(plcOfflineCountingValuesProvider.getLastProductDateTime(1)).thenReturn(lastProduct);

		Mockito.doNothing().when(plc).write(resetCountersVar);

		Mockito.when(productionParameters.getSku()).thenReturn(sku);

		final List<Product> products = new ArrayList<>();
		Object productionCatcher = new Object() {
			@Subscribe
			public void notifyNewProduct(NewProductEvent evt) {
				products.add(evt.getProduct());
			}
		};
		EventBusService.register(productionCatcher);

		poc.processOfflineCounting();
		Assert.assertEquals(productsCount, products.size());
		Mockito.verify(plc, Mockito.times(1)).write(Mockito.any());

		int i = 0;
		for (Product p : products) {
			Assert.assertEquals(sku.getId(), p.getSku().getId());
			Assert.assertEquals(null, p.getCode());
			Assert.assertEquals(sku.getCodeType(), p.getSku().getCodeType());
			Assert.assertEquals((lastStop.getTime() + i * deltaProduct), p.getActivationDate().getTime());
			i++;
		}
	}

	private SKU getSku() {
		SKU sku = new SKU();

		sku.setId(1234);
		sku.setDescription("TEST");
		sku.setCodeType(new CodeType(4321));

		return sku;
	}

	private PlcOfflineCounting getPlcOfflineCounting() {
		PlcOfflineCounting poc = new PlcOfflineCounting();

		poc.setPlcOfflineCountingValuesProvider(plcOfflineCountingValuesProvider);
		poc.setResetCountersVarName(RESET_COUNTERS_VAR_NAME);
		poc.setPlcProvider(plcProvider);
		poc.setProductionParameters(productionParameters);
		poc.setSubsystemIdProvider(new SubsystemIdProvider(0));

		return poc;
	}
}
