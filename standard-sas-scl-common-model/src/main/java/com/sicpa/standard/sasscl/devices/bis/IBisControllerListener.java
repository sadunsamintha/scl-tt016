package com.sicpa.standard.sasscl.devices.bis;

import com.sicpa.std.bis2.core.messages.RemoteMessages.Alert;
import com.sicpa.std.bis2.core.messages.RemoteMessages.LifeCheck;
import com.sicpa.std.bis2.core.messages.RemoteMessages.RecognitionResultMessage;

public interface IBisControllerListener {

	void onConnection();

	void onDisconnection();

	void lifeCheckReceived(LifeCheck lifeCheckResponse);

	void alertReceived(Alert command);

	void recognitionResultReceived(RecognitionResultMessage result);

	void otherMessageReceived(Object result);

	void onLifeCheckFailed();

}
