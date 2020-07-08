package com.sicpa.ttth.scl;

import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;

import static com.sicpa.standard.sasscl.messages.ActionMessageType.WARNING;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.BARCODE_VERIFIED;
import static com.sicpa.ttth.messages.TTTHMessageEventKey.SKUSELECTION.BARCODE_VERIFIED_MSG_CODE;

public class TTTHBootstrap extends Bootstrap{

	@Override
	public void executeSpringInitTasks(){
		super.executeSpringInitTasks();
		addSkuSelectionMessages();
	}

	private void addSkuSelectionMessages() {
		CustoBuilder.addMessage(BARCODE_VERIFIED, BARCODE_VERIFIED_MSG_CODE, WARNING);
	}
}
