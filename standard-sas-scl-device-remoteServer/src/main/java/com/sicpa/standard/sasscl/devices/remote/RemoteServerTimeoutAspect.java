package com.sicpa.standard.sasscl.devices.remote;

import java.util.concurrent.Callable;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.utils.TaskTimeoutExecutor;
import com.sicpa.standard.sasscl.monitoring.MonitoringService;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventLevel;
import com.sicpa.standard.sasscl.monitoring.system.SystemEventType;
import com.sicpa.standard.sasscl.monitoring.system.event.BasicSystemEvent;

public class RemoteServerTimeoutAspect {
	private static final Logger logger = LoggerFactory.getLogger(RemoteServerTimeoutAspect.class);

	protected int remoteServerTimeoutCall_sec;

	public Object log(final ProceedingJoinPoint call) throws Throwable {

		logger.debug("calling remote server {}", call.getSignature().toShortString());
		MonitoringService.addSystemEvent(new BasicSystemEvent(SystemEventLevel.INFO,
				SystemEventType.REMOTE_SERVER_CALL, call.getSignature().toShortString()));
		int timeout = remoteServerTimeoutCall_sec;
		if (timeout <= 0) {
			try {
				return call.proceed();
			} catch (Throwable e) {
				throw e;
			}
		} else {
			final Throwable[] t = new Throwable[1];
			Object res = TaskTimeoutExecutor.execute(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					Object res = null;
					try {
						res = call.proceed();
					} catch (Throwable e) {
						t[0] = e;
					}
					return res;
				}
			}, timeout);
			if (t[0] == null) {
				return res;
			} else {
				throw t[0];
			}
		}
	}

	public void setRemoteServerTimeoutCall_sec(int remoteServerTimeoutCall_sec) {
		this.remoteServerTimeoutCall_sec = remoteServerTimeoutCall_sec;
	}
}
