package com.sicpa.tt018.scl.remoteServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.gui.utils.ThreadUtils;

public class RemoteServerLifeChecker {
	private static final Logger logger = LoggerFactory.getLogger(RemoteServerLifeChecker.class);

	private RemoteServer target;
	private int lifeCheckSleep = 20;
	private Thread lifeCheker;

	public void setTarget(RemoteServer target) {
		this.target = target;
	}

	public void setLifeCheckSleep(int lifeCheckSleep) {
		this.lifeCheckSleep = lifeCheckSleep;
	}

	protected void start() {
		if (lifeCheker == null && target != null) {
			lifeCheker = new Thread(new Runnable() {
				@Override
				public void run() {
					// is a daemon + server is supposed to be up at all time
					// => while true is ok
					while (true) {
						try {
							target.lifeCheckTick();
						} catch (Exception e) {
							logger.error("Remote Server Life Check Tick Failed", e);
							target.disconnect();
						}
						if (lifeCheckSleep > 0) {
							ThreadUtils.sleepQuietly(1000L * lifeCheckSleep);
						} else {
							ThreadUtils.sleepQuietly(10 * 1000);
						}
					}
				}
			});
			lifeCheker.setDaemon(true);
			lifeCheker.setName("RemoteServerLifeChecker");
			lifeCheker.start();
		}
	}

	public boolean isAlive() {
		if (lifeCheker != null && lifeCheker.isAlive()) {
			return true;
		} else {
			return false;
		}
	}
}
