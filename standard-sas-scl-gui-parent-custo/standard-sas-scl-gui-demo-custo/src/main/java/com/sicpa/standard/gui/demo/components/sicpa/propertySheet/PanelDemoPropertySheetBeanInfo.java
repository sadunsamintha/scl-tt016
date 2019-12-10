package com.sicpa.standard.gui.demo.components.sicpa.propertySheet;

import com.l2fprod.common.beans.BaseBeanInfo;

public class PanelDemoPropertySheetBeanInfo extends BaseBeanInfo {

	public PanelDemoPropertySheetBeanInfo() {
		super(PanelDemoPropertySheet.class);

		addProperty("name").setCategory("general");
		getPropertyDescriptor(0).setDisplayName("NAME");
		getPropertyDescriptor(0).setShortDescription("NAME description here");

		addProperty("phoneNumber").setCategory("general");

		getPropertyDescriptor(1).setDisplayName("PHONENUMBER");
		getPropertyDescriptor(1).setShortDescription("PHONENUMBER description here");

		addProperty("adress").setCategory("general");

		getPropertyDescriptor(2).setDisplayName("ADRESS");
		getPropertyDescriptor(2).setShortDescription("ADRESS description here");

		addProperty("color").setCategory("general");

		getPropertyDescriptor(3).setDisplayName("COLOR");
		getPropertyDescriptor(3).setShortDescription("COLOR description here");

		addProperty("valid").setCategory("advanced");

		getPropertyDescriptor(4).setDisplayName("VALID");
		getPropertyDescriptor(4).setShortDescription("VALID description here");
	}
}
