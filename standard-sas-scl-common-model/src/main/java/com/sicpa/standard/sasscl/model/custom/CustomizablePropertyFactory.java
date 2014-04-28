package com.sicpa.standard.sasscl.model.custom;

public class CustomizablePropertyFactory {

	private static ICustomizablePropertyDefinition customizablePropertyDefinition;

	public static ICustomizablePropertyDefinition getCustomizablePropertyDefinition() {
		return customizablePropertyDefinition;
	}

	public static void setCustomizablePropertyDefinition(ICustomizablePropertyDefinition customizablePropertyDefinition) {
		CustomizablePropertyFactory.customizablePropertyDefinition = customizablePropertyDefinition;
	}

	public static boolean isPropertyAvailable(Class<? extends ICustomizable> clazz, ICustomProperty<?> customProperty) {
		return customizablePropertyDefinition.isPropertyAvailable(clazz, customProperty);
	}

}
