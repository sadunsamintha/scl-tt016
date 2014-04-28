package com.sicpa.standard.sasscl.devices.brs;

import com.sicpa.standard.sasscl.devices.plc.PlcRequest;

@SuppressWarnings("serial")
public class BrsPlcRequest extends PlcRequest {

	public final static BrsPlcRequest LINE_ENABLE = new BrsPlcRequest(4, "line enable");
	public final static BrsPlcRequest LINE_DISABLE = new BrsPlcRequest(5, "line disable");
	public static final BrsPlcRequest RESET_EXPECTED_SKU = new BrsPlcRequest(8, "reset expected SKU");
	public static final BrsPlcRequest SET_SKU_CHECK_MODE = new BrsPlcRequest(9, "set sku button mode");
	public static final BrsPlcRequest RESET_SKU_CHECK_MODE = new BrsPlcRequest(10, "reset sku button mode");
	public static final BrsPlcRequest SET_EXPECTED_SKU = new BrsPlcRequest(11, "set expected SKU");

	public BrsPlcRequest(int id, String description) {
		super(id, description);
	}
}
