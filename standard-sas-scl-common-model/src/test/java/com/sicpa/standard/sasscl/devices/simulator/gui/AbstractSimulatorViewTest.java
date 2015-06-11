package com.sicpa.standard.sasscl.devices.simulator.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.junit.Before;
import org.junit.Test;

public class AbstractSimulatorViewTest {

	private AbstractSimulatorViewImpl simulatorViewImpl;
	
	@Before
	public void setUp() throws Exception {
		simulatorViewImpl = new AbstractSimulatorViewImpl();
	}

	@Test
	public void testInitGUI() {
		simulatorViewImpl.initGUI();
		
		assertEquals(2,simulatorViewImpl.getComponents().length);
		
		assertButtons();
	}



	@Test
	public void testGetButtonConnect() {
		JButton buttonConnect = simulatorViewImpl.getButtonConnect();
		
		ActionListener[] actionListeners = buttonConnect.getActionListeners();
		assertEquals(1,actionListeners.length);
		
		assertButtonConnectActionAction(actionListeners);
	}



	@Test
	public void testGetButtonDisconnect() {
		JButton disconectButton = simulatorViewImpl.getButtonDisconnect();
		
		ActionListener[] actionListeners = disconectButton.getActionListeners();
		assertEquals(1,actionListeners.length);
		
		assertButtonDisconnectActionAction(actionListeners);
	}


	
	private static class AbstractSimulatorViewImpl extends AbstractSimulatorView{

		private static final long serialVersionUID = 6099531133203259363L;
		public boolean buttonConnectActionPerformedCalled;
		public boolean buttonDisconnectActionPerformedCalled;

		@Override
		protected void buttonConnectActionPerformed() {
			buttonConnectActionPerformedCalled = true;
			
		}

		@Override
		protected void buttonDisconnectActionPerformed() {
			buttonDisconnectActionPerformedCalled = true;
			
		}
		
	}
	
	private void assertButtons() {
		assertEquals(simulatorViewImpl.getButtonConnect(),simulatorViewImpl.getComponents()[0]);
		assertEquals(simulatorViewImpl.getButtonDisconnect(),simulatorViewImpl.getComponents()[1]);
	}
	
	private void assertButtonConnectActionAction(ActionListener[] actionListeners) {
		actionListeners[0].actionPerformed(null);
		assertTrue(simulatorViewImpl.buttonConnectActionPerformedCalled);
	}
	
	private void assertButtonDisconnectActionAction(ActionListener[] actionListeners) {
		actionListeners[0].actionPerformed(null);
		assertTrue(simulatorViewImpl.buttonDisconnectActionPerformedCalled);
	}

}
