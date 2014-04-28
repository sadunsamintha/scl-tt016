package com.sicpa.standard.sasscl.devices.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * The class <code>RemoteServerExceptionTest</code> contains tests for the class
 * <code>{@link RemoteServerException}</code>.
 * @generatedBy CodePro at 12/11/10 15:54
 * @author JAguililla
 */
public class RemoteServerExceptionTest {
	/**
	 * Run the RemoteServerException(String) constructor test.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testRemoteServerException () {
		RemoteServerException result = new RemoteServerException();

		assertNotNull(result);
		assertEquals(null, result.getCause());
		assertEquals("com.sicpa.standard.sasscl.devices.remote.RemoteServerException", result.toString());
		assertNull(result.getMessage());
		assertNull(result.getLocalizedMessage());
	}

	/**
	 * Run the RemoteServerException(String) constructor test.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testRemoteServerExceptionMessage () {
		String message = "";

		RemoteServerException result = new RemoteServerException(message);

		assertNotNull(result);
		assertEquals(null, result.getCause());
		assertEquals("com.sicpa.standard.sasscl.devices.remote.RemoteServerException: ", result.toString());
		assertEquals("", result.getMessage());
		assertEquals("", result.getLocalizedMessage());
	}

	/**
	 * Run the RemoteServerException(Throwable) constructor test.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testRemoteServerExceptionCause() {
		Throwable cause = new Throwable();

		RemoteServerException result = new RemoteServerException(cause);

		assertNotNull(result);
		assertEquals("com.sicpa.standard.sasscl.devices.remote.RemoteServerException: java.lang.Throwable", result.toString());
		assertEquals("java.lang.Throwable", result.getMessage());
		assertEquals("java.lang.Throwable", result.getLocalizedMessage());
	}

	/**
	 * Run the RemoteServerException(String,Throwable) constructor test.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testRemoteServerExceptionMessageCause() {
		String message = "";
		Throwable cause = new Throwable();

		RemoteServerException result = new RemoteServerException(message, cause);

		assertNotNull(result);
		assertEquals("com.sicpa.standard.sasscl.devices.remote.RemoteServerException: ", result.toString());
		assertEquals("", result.getMessage());
		assertEquals("", result.getLocalizedMessage());
	}

	/**
	 * Run the RemoteServerException(Throwable,String,Object[]) constructor test.
	 * @generatedBy CodePro at 12/11/10 15:54
	 */
	@Test
	public void testRemoteServerExceptionCauseMessageKey() {
		Throwable cause = new Throwable();
		String msgKey = "";

		RemoteServerException result = new RemoteServerException(cause, msgKey);

		assertNotNull(result);
		assertEquals("com.sicpa.standard.sasscl.devices.remote.RemoteServerException: ", result.toString());
		assertEquals("", result.getMessage());
		assertEquals("", result.getLocalizedMessage());
	}
}