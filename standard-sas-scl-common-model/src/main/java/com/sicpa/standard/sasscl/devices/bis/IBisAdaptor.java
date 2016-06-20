package com.sicpa.standard.sasscl.devices.bis;

import com.sicpa.standard.sasscl.devices.IStartableDevice;

public interface IBisAdaptor extends IStartableDevice {

	void sendCredential(String user, String password);

}
