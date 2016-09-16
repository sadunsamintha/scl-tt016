package com.sicpa.standard.sasscl.business.activation.offline;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.business.activation.offline.impl.PlcOfflineCounting;
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
import java.util.List;

import static com.sicpa.standard.plc.value.PlcVariable.createBooleanVar;
import static com.sicpa.standard.plc.value.PlcVariable.createInt32Var;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.addLineIndex;
import static com.sicpa.standard.sasscl.devices.plc.PlcLineHelper.replaceLinePlaceholder;

public class PlcOfflineCountingTest {

	private PlcOfflineCounting poc;

	private PlcProvider plcProvider;

	private IPlcAdaptor plc;

	private final String QUANTITY_VAR_NAME = "com.stLine[#x].stOff_Counter.nProductsCounterOFF";
	private final String LAST_STOP_TIME_VAR_NAME = "com.stLine[#x].stOff_Counter.'nSecondsWhenStopped";
	private final String LAST_PRODUCT_TIME_VAR_NAME = "com.stLine[#x].stOff_Counter.'nSecondsLastOFFProduct";
	private final String RESET_COUNTERS_VAR_NAME = "com.stLine[#x].stOff_Counter.'bResetOffCounters";

	private IPlcVariable<Integer> qtyVar;
	private IPlcVariable<Integer> lastStopVar;
	private IPlcVariable<Integer> lastProductVar;
	private IPlcVariable<Boolean> resetCountersVar;

	private SKU sku;

	private ProductionParameters productionParameters;

	@Before
	public void setup() throws Exception {
		addLineIndex(1);

		qtyVar = createInt32Var(replaceLinePlaceholder(QUANTITY_VAR_NAME, 1));
		lastStopVar = createInt32Var(replaceLinePlaceholder(LAST_STOP_TIME_VAR_NAME, 1));
		lastProductVar = createInt32Var(replaceLinePlaceholder(LAST_PRODUCT_TIME_VAR_NAME, 1));
		resetCountersVar = createBooleanVar(replaceLinePlaceholder(RESET_COUNTERS_VAR_NAME, 1));

		plc = Mockito.mock(IPlcAdaptor.class);

		plcProvider = new PlcProvider();
		plcProvider.set(plc);

		productionParameters = Mockito.mock(ProductionParameters.class);
		poc = getPlcOfflineCounting();
		sku = getSku();
	}

	@Test
	public void test() throws PlcAdaptorException {
		int lastStop = (int) (System.currentTimeMillis() / 1000);
		int lastProduct = lastStop + 3600 * 2;// 2 hours of down time
		final int productsCount = 100;
		int deltaProduct = ((lastProduct - lastStop) / productsCount) * 1000;

		Mockito.when(plc.read(qtyVar)).thenReturn(productsCount);
		Mockito.when(plc.read(lastStopVar)).thenReturn(lastStop);
		Mockito.when(plc.read(lastProductVar)).thenReturn(lastProduct);

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
			Assert.assertEquals((1000l * lastStop + i * deltaProduct), p.getActivationDate().getTime());
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

		poc.setQuantityVarName(QUANTITY_VAR_NAME);
		poc.setLastStopTimeVarName(LAST_STOP_TIME_VAR_NAME);
		poc.setLastProductTimeVarName(LAST_PRODUCT_TIME_VAR_NAME);
		poc.setResetCountersVarName(RESET_COUNTERS_VAR_NAME);
		poc.setPlcProvider(plcProvider);
		poc.setProductionParameters(productionParameters);
		poc.setSubsystemIdProvider(new SubsystemIdProvider(0));

		return poc;
	}
}
