package com.sicpa.standard.sasscl.custoBuilder;

import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyFactory;
import com.thoughtworks.xstream.XStream;

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
}
