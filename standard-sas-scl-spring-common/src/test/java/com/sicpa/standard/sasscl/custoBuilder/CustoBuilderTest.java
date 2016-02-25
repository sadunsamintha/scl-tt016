package com.sicpa.standard.sasscl.custoBuilder;

import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyFactory;
import com.thoughtworks.xstream.XStream;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class CustoBuilderTest {

	SKU sku;
	CustomProperty<Boolean> isCompliant = new CustomProperty<>("compliant", Boolean.class);

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
}
