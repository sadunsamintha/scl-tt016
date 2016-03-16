package com.sicpa.standard.sasscl.custoBuilder;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.sasscl.model.SKU;
import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyDefinition;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyFactory;
import com.thoughtworks.xstream.XStream;

public class CustoBuilderCustomizableTest {

	SKU sku;
	CustomProperty<Boolean> isCompliant = new CustomProperty<>("compliant", Boolean.class, Boolean.TRUE);

	@Before
	public void before() {
		sku = new SKU();
		CustomizablePropertyFactory.setCustomizablePropertyDefinition(new CustomizablePropertyDefinition());
	}

	@Test
	public void testModelAddCustomPropertySuccess() {
		CustoBuilder.addPropertyToClass(SKU.class, isCompliant);
		sku.setProperty(isCompliant, true);

		Assert.assertEquals(true, sku.getProperty(isCompliant).booleanValue());
	}

	@Test(expected = RuntimeException.class)
	public void testModelAddCustomPropertyFail() {
		sku.setProperty(isCompliant, true);
	}

	@Test
	public void testModelAddCustomPropertySerializationXStream() {
		CustoBuilder.addPropertyToClass(SKU.class, isCompliant);
		sku.setProperty(isCompliant, true);

		XStream xStream = new XStream();
		String skuStr = xStream.toXML(sku);
		SKU skuFromXML = (SKU) xStream.fromXML(skuStr);

		Assert.assertEquals(true, skuFromXML.getProperty(isCompliant).booleanValue());
	}

	@Test
	public void testModelGetDefaultValue() {
		CustoBuilder.addPropertyToClass(SKU.class, isCompliant);

		Assert.assertEquals(true, sku.getProperty(isCompliant).booleanValue());
	}

	@Test
	public void testModelOverwriteDefaultValue() {
		CustoBuilder.addPropertyToClass(SKU.class, isCompliant);
		sku.setProperty(isCompliant, false);

		Assert.assertEquals(false, sku.getProperty(isCompliant).booleanValue());
	}
}
