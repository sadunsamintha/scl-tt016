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
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class PlcOfflineCountingTest {

	@SuppressWarnings("unchecked")
	@Test
	public void test() throws PlcAdaptorException {
		PlcOfflineCounting poc = new PlcOfflineCounting();

		IPlcVariable<Integer> qtyVar = Mockito.mock(IPlcVariable.class);
		IPlcVariable<Integer> lastStopVar = Mockito.mock(IPlcVariable.class);
		IPlcVariable<Integer> lastProductVar = Mockito.mock(IPlcVariable.class);

		ProductionParameters productionParameters = Mockito.mock(ProductionParameters.class);
		poc.setProductionParameters(productionParameters);

		PlcProvider plcProvider = new PlcProvider();
		IPlcAdaptor plc = Mockito.mock(IPlcAdaptor.class);

		int lastStop = (int) (System.currentTimeMillis() / 1000);
		int lastProduct = lastStop + 3600 * 2;// 2 hours of down time
		final int productsCount = 100;
		int deltaProduct = ((lastProduct - lastStop) / productsCount) * 1000;

		CodeType ct = new CodeType(4321);

		SKU sku = new SKU();
		sku.setId(1234);
		sku.setDescription("TEST");
		sku.setCodeType(ct);

		Mockito.when(plc.read(qtyVar)).thenReturn(productsCount);
		Mockito.when(plc.read(lastStopVar)).thenReturn(lastStop);
		Mockito.when(plc.read(lastProductVar)).thenReturn(lastProduct);

		Mockito.when(productionParameters.getSku()).thenReturn(sku);



		poc.setPlcProvider(plcProvider);
		poc.setSubsystemIdProvider(new SubsystemIdProvider(0));
		plcProvider.set(plc);

		final List<Product> products = new ArrayList<Product>();

		Object productionCatcher = new Object() {
			@Subscribe
			public void notifyNewProduct(NewProductEvent evt) {
				products.add(evt.getProduct());
			}
		};
		EventBusService.register(productionCatcher);

		poc.processOfflineCounting(qtyVar, lastStopVar, lastProductVar);
		Assert.assertEquals(productsCount, products.size());
		int i = 0;

		for (Product p : products) {
			Assert.assertEquals(sku.getId(), p.getSku().getId());
			Assert.assertEquals(null, p.getCode());
			Assert.assertEquals(sku.getCodeType(), p.getSku().getCodeType());
			Assert.assertEquals((1000l * lastStop + i * deltaProduct), p.getActivationDate().getTime());
			i++;
		}

		Mockito.verify(plc, Mockito.times(1)).write(qtyVar);
		Mockito.verify(plc, Mockito.times(1)).write(lastProductVar);
		Mockito.verify(plc, Mockito.times(1)).write(lastStopVar);

		ArgumentCaptor<Integer> argument = ArgumentCaptor.forClass(Integer.class);
		Mockito.verify(qtyVar).setValue(argument.capture());
		Assert.assertEquals(0, argument.getValue().intValue());

		Mockito.verify(lastProductVar).setValue(argument.capture());
		Assert.assertEquals(0, argument.getValue().intValue());

		Mockito.verify(lastStopVar).setValue(argument.capture());
		Assert.assertEquals(0, argument.getValue().intValue());
	}
}
