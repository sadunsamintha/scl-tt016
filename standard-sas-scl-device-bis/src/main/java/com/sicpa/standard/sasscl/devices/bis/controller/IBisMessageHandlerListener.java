package com.sicpa.standard.sasscl.devices.bis.controller;

import com.sicpa.std.bis2.core.messages.RemoteMessages.Alert;
import com.sicpa.std.bis2.core.messages.RemoteMessages.LifeCheck;
import com.sicpa.std.bis2.core.messages.RemoteMessages.RecognitionResultMessage;

public interface IBisMessageHandlerListener {

	void onConnected();

	void onDisconnected();

	void lifeCheckReceived(LifeCheck lifeCheckResponse);

	void alertReceived(Alert command);

	void recognitionResultReceived(RecognitionResultMessage result);

	void onOtherMessageReceived(Object otherMessage);

}
