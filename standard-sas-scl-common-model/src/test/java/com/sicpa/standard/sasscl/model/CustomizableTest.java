package com.sicpa.standard.sasscl.model;

import com.sicpa.standard.sasscl.model.custom.CustomProperty;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyDefinition;
import com.sicpa.standard.sasscl.model.custom.CustomizablePropertyFactory;
import com.sicpa.standard.sasscl.model.custom.StringCustomProperty;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class CustomizableTest {

	static final PackType DEFAULT_VAL = new PackType("defaultPackType");

	static final CustomProperty<PackType> SKU_PACK_TYPE = new CustomProperty<>("packType", PackType.class, DEFAULT_VAL);

	@Before
	public void setup() {
		CustomizablePropertyDefinition definition = new CustomizablePropertyDefinition();
		definition.addProperty(SKU.class, CustomizableTest.SKU_PACK_TYPE);
		CustomizablePropertyFactory.setCustomizablePropertyDefinition(definition);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnkownProperty() {
		Code code = new Code();
		CustomProperty<String> customProperty = new StringCustomProperty("customProperty1",
				"defaultStringCustomProperty");
		code.setProperty(customProperty, "test");
	}

	@Test
	public void testSetCustomProperty() {

		SKU sku = new SKU();
		PackType pack = new PackType("packType101");

		sku.setProperty(CustomizableTest.SKU_PACK_TYPE, pack);
		Assert.assertEquals(pack, sku.getProperty(SKU_PACK_TYPE));

		sku.clearProperty(CustomizableTest.SKU_PACK_TYPE);
		Assert.assertEquals(DEFAULT_VAL, sku.getProperty(SKU_PACK_TYPE));

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
