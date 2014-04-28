package com.sicpa.standard.sasscl.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallTimeAspect {
	private static Logger logger= LoggerFactory.getLogger(CallTimeAspect.class);

	public Object showExecutionTime(final ProceedingJoinPoint call) throws Throwable {
		Object res = null;
		long t1 = System.nanoTime();
		res = call.proceed();
		long t2 = System.nanoTime();
		logger.debug("{} ms for {1}", (t2 - t1) / 1000000, call.getSignature().toLongString());
		return res;
	}
}
