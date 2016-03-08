package com.sicpa.standard.sasscl.model;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyDefinition;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyFactory;
import com.sicpa.standard.sasscl.model.custom.StringCustomProperty;

public class CustomizableTest {

	public static final CustomProperty<Boolean> SKU_IS_EXPORT = new CustomProperty<>("isExport", Boolean
			.class, true);
	public static final CustomProperty<PackType> SKU_PACK_TYPE = new CustomProperty<>("packType",
			PackType.class, new PackType("defaultPackType"));

	@Before
	public void setup() {
		CustomizablePropertyDefinition definition = new CustomizablePropertyDefinition();
		definition.addProperty(SKU.class, CustomizableTest.SKU_IS_EXPORT);
		definition.addProperty(SKU.class, CustomizableTest.SKU_PACK_TYPE);
		CustomizablePropertyFactory.setCustomizablePropertyDefinition(definition);
	}

	@Test
	public void testCustomProperty() {

		SKU sku = new SKU();
		Code code = new Code();
		CustomProperty<String> customProperty = new StringCustomProperty("customProperty1",
				"defaultStringCustomProperty");

		try {
			code.setProperty(customProperty, "test");
			Assert.fail();
		} catch (RuntimeException e) {
		}

		// test to set invalid property (property that is not defined)
		try {
			sku.setProperty(customProperty, "test");
			Assert.fail();
		} catch (RuntimeException e) {
		}

		// test to get invalid property (property that is not defined)
		try {
			sku.getProperty(customProperty);
			Assert.fail();
		} catch (RuntimeException e) {
		}

		sku.setProperty(CustomizableTest.SKU_IS_EXPORT, true);
		sku.setProperty(CustomizableTest.SKU_PACK_TYPE, new PackType("packType101"));

		SKU clone = sku.copySkuForProductionData();

		Assert.assertEquals(Boolean.TRUE, clone.getProperty(CustomizableTest.SKU_IS_EXPORT));

		PackType packType = clone.getProperty(CustomizableTest.SKU_PACK_TYPE);
		Assert.assertEquals("packType101", packType.getDescription());

		clone.setProperty(CustomizableTest.SKU_IS_EXPORT, false);
		Assert.assertEquals(Boolean.TRUE, sku.getProperty(CustomizableTest.SKU_IS_EXPORT));

		sku.clearProperty(CustomizableTest.SKU_IS_EXPORT);
		Assert.assertNull(sku.getProperty(CustomizableTest.SKU_IS_EXPORT));

	}

	public static class PackType {

		private String description;

		public PackType(String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}

}
