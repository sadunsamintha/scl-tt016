package com.sicpa.standard.sasscl.devices.plc;

public class BrsPlcRequest {

	public static final PlcRequest LINE_ENABLE = new PlcRequest("line enable");
	public static final PlcRequest LINE_DISABLE = new PlcRequest("line disable");
	public static final PlcRequest RESET_EXPECTED_SKU = new PlcRequest("reset expected SKU");
	public static final PlcRequest SET_SKU_CHECK_MODE = new PlcRequest("set sku button mode");
	public static final PlcRequest RESET_SKU_CHECK_MODE = new PlcRequest("reset sku button mode");
	public static final PlcRequest SET_EXPECTED_SKU = new PlcRequest("set expected SKU");

}
