package com.sicpa.standard.sasscl.business.activation.impl;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.utils.LogUtils;
import com.sicpa.standard.sasscl.business.activation.IActivationBehavior;
import com.sicpa.standard.sasscl.business.activation.NewProductEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraAdaptor;
import com.sicpa.standard.sasscl.devices.camera.CameraBadCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.CameraGoodCodeEvent;
import com.sicpa.standard.sasscl.devices.camera.ICameraAdaptor;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.provider.impl.ActivationBehaviorProvider;
import com.sicpa.standard.sasscl.provider.impl.ProductionBatchProvider;

public class ActivationTest {

	Activation activation;
	String productionBatchId;
	ProductCount productsRepository;
	ActivationBehaviorProvider activationBehaviorProvider = new ActivationBehaviorProvider();

	// create source, have to setup the conveyor info
	private ICameraAdaptor cameraAdaptor = new CameraAdaptor();

	@Before
	public void setUp() throws Exception {

		LogUtils.initLogger();

		IActivationBehavior behavior = mock(IActivationBehavior.class);
		when(behavior.receiveCode(Mockito.<Code> any(), eq(true))).thenReturn(new Product());
		when(behavior.receiveCode(Mockito.<Code> any(), eq(false))).thenReturn(null);

		when(behavior.receiveCode(Mockito.any(Code.class), Mockito.anyBoolean())).thenReturn(new Product());

		activation = new Activation();
		activation.setActivationBehaviorProvider(activationBehaviorProvider);
		activationBehaviorProvider.set(behavior);
		EventBusService.register(activation);

		ProductionBatchProvider productionBatchProvider = mock(ProductionBatchProvider.class);
		when(productionBatchProvider.get()).thenReturn(productionBatchId = String.valueOf(System.currentTimeMillis()));

		activation.setProductionBatchProvider(productionBatchProvider);

		productsRepository = new ProductCount();

		Object productionCatcher = new Object() {
			@Subscribe
			public void notifyNewProduct(final NewProductEvent evt) {
				productsRepository.add(evt.getProduct());
			}

		};
		EventBusService.register(productionCatcher);
		cameraAdaptor.setName("camera-sas");
	}

	private static class ProductCount {

		final List<Product> store = new ArrayList<Product>();
		int count;

		void add(Product product) {

			count++;
			store.add(product);
		}

		Product pop() {

			return store.remove(0);
		}
	}

	@Test
	public void receiveCodeTest() {

		activation.receiveCameraCode(new CameraGoodCodeEvent(new Code("000"), cameraAdaptor));

		// good code
		Assert.assertThat(productsRepository.count, IsEqual.equalTo(1));
		Assert.assertThat(productsRepository.pop(), IsNull.notNullValue());

		// bad code
		activation.receiveCameraCodeError(new CameraBadCodeEvent(new Code("001"), cameraAdaptor));
		Assert.assertThat(productsRepository.count, IsEqual.equalTo(2));
		Assert.assertThat(productsRepository.pop(), IsNull.notNullValue());
	}

	@Test
	public void updateProductionBatchTest() {

		activation.receiveCameraCode(new CameraGoodCodeEvent(new Code("000"), cameraAdaptor));

		String batchId = productsRepository.pop().getProductionBatchId();
		Assert.assertThat(batchId, IsNull.notNullValue());
		Assert.assertThat(batchId, IsEqual.equalTo(productionBatchId));
	}
}
