package com.sicpa.standard.sasscl.devices.bis;

import static com.sicpa.standard.client.common.utils.TaskExecutor.scheduleAtFixedRate;
import static java.util.concurrent.TimeUnit.MINUTES;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.provider.impl.BisProvider;

public class BisUserSender {
	private static final Logger logger = LoggerFactory.getLogger(BisUserSender.class);

	private BisProvider bisProvider;
	private IBisCredentialProvider credentialProvider;
	private int sendUserCredentialPeriodMinutes;

	public void init() {
		if (sendUserCredentialPeriodMinutes > 0) {
			scheduleAtFixedRate(() -> sendCredentialToBis(), 0, sendUserCredentialPeriodMinutes, MINUTES,
					"sendCredentialToBis");
		}
	}

	private void sendCredentialToBis() {
		try {
			IBisAdaptor bis = bisProvider.get();
			if (bis != null && bis.isConnected()) {
				String user = credentialProvider.getUserName();
				String pwd = credentialProvider.getPassword();
				bis.sendCredential(user, pwd);
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void setBisProvider(BisProvider bisProvider) {
		this.bisProvider = bisProvider;
	}

	public void setSendUserCredentialPeriodMinutes(int sendUserCredentialPeriodMinutes) {
		this.sendUserCredentialPeriodMinutes = sendUserCredentialPeriodMinutes;
	}

	public void setCredentialProvider(IBisCredentialProvider credentialProvider) {
		this.credentialProvider = credentialProvider;
	}
}
