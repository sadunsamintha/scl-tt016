package com.sicpa.standard.sasscl.devices.bis;

import com.sicpa.std.bis2.core.messages.RemoteMessages.LifeCheck;
import com.sicpa.std.bis2.core.messages.RemoteMessages.SkusMessage;

public interface IBisController {

	void connect() throws BisAdaptorException;

	void disconnect();

	void sendSkuList(SkusMessage skus);

	void sendLifeCheck();

	void sendAutoSave();

	void receiveLifeCheckResponce(LifeCheck lifeCheckResponce);

	boolean isConnected();

	void onLifeCheckFailed();

	void onLifeCheckSucceed();

	void addListener(IBisControllerListener listener);

	void removeListener(IBisControllerListener listener);

	void sendUnknownSave();

	void sendCredential(String user, String password);

	void sendDomesticMode();
	
	void sendExportMode();

}
