package com.sicpa.standard.sasscl.devices.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Test;

import com.sicpa.standard.sasscl.config.GlobalBean;

/**
 * The class <code>RemoteServerLogCallAspectTest</code> contains tests for the class
 * <code>{@link RemoteServerTimeoutAspect}</code>.
 * @generatedBy CodePro at 12/11/10 15:54
 * @author JAguililla
 */
public class RemoteServerLogCallAspectTest {
	/**
	 * Run the RemoteServerLogCallAspect() constructor test.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testRemoteServerLogCallAspect() { assertNotNull(new RemoteServerTimeoutAspect()); }

	/**
	 * Run the Object log(ProceedingJoinPoint) method test.
	 * @throws Throwable Thown if call.proceed fails.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testLog() throws Throwable {
		RemoteServerTimeoutAspect fixture = new RemoteServerTimeoutAspect();
		fixture.setConfig(new GlobalBean());

		Signature signature = mock(Signature.class);
		when (signature.toShortString()).thenReturn ("signature");

		ProceedingJoinPoint call = mock(ProceedingJoinPoint.class);
		when (call.getSignature()).thenReturn(signature);
		when (call.proceed()).thenReturn("ok");

		Object result = fixture.log(call);

		assertEquals("ok", result);
	}

	/**
	 * Run the Object log(ProceedingJoinPoint) method test.
	 * @throws Throwable Thown if call.proceed fails.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test(expected = java.lang.NullPointerException.class)
	public void testLogNullCall() throws Throwable {
		RemoteServerTimeoutAspect fixture = new RemoteServerTimeoutAspect();

		fixture.log(null);
	}
}