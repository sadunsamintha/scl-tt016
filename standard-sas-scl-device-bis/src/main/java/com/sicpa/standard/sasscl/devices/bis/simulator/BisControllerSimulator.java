package com.sicpa.standard.sasscl.devices.bis.simulator;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.bis.IBisController;
import com.sicpa.standard.sasscl.devices.bis.IBisControllerListener;
import com.sicpa.std.bis2.core.messages.RemoteMessages.LifeCheck;
import com.sicpa.std.bis2.core.messages.RemoteMessages.RecognitionResultMessage;
import com.sicpa.std.bis2.core.messages.RemoteMessages.SkuMessage;
import com.sicpa.std.bis2.core.messages.RemoteMessages.SkusMessage;

public class BisControllerSimulator implements IBisController {

	private static final Logger logger = LoggerFactory.getLogger(BisControllerSimulator.class);

	private final List<IBisControllerListener> listeners = new ArrayList<>();
	private volatile boolean connected;

	public void skuRecognized(int skuId) {
		if (!isConnected()) {
			return;
		}

		SkuMessage skuMsg = SkuMessage.newBuilder().setConfidence(1).setId(skuId).build();
		RecognitionResultMessage recognitionMsg = RecognitionResultMessage.newBuilder().setHeightCategory(0)
				.setConfidence(skuMsg).build();

		listeners.forEach(l -> l.recognitionResultReceived(recognitionMsg));
	}

	@Override
	public void connect() {
		connected = true;
		onConnected();
	}

	@Override
	public void disconnect() {
		connected = false;
		onDisconnected();
	}

	public void onConnected() {
		listeners.forEach(l -> l.onConnection());
	}

	public void onDisconnected() {
		listeners.forEach(l -> l.onDisconnection());
	}

	@Override
	public void sendSkuList(SkusMessage skus) {
		skus.getSkuList().forEach(s -> logger.info("sku sent:" + s));
	}

	@Override
	public void sendLifeCheck() {
		logger.info("lifecheck");
	}

	@Override
	public void sendAutoSave() {
		logger.info("sendAutoSave");
	}

	@Override
	public void receiveLifeCheckResponce(LifeCheck lifeCheckResponce) {
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public void onLifeCheckFailed() {
	}

	@Override
	public void onLifeCheckSucceed() {
	}

	@Override
	public void addListener(IBisControllerListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(IBisControllerListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void sendUnknownSave() {
		logger.info("sendUnknownSave");
	}

	@Override
	public void sendCredential(String user, String password) {
		logger.info("sendCredential:" + user + "/" + password);
	}

	@Override
	public void sendDomesticMode() {
		logger.info("send domestic mode");
	}
}
