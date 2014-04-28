package com.sicpa.standard.sasscl.devices.plc;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.sicpa.standard.plc.controller.IPlcController;
import com.sicpa.standard.plc.controller.actions.IPlcAction;
import com.sicpa.standard.plc.controller.request.IPlcRequest;

public class AbstractPlcRequestExecutorTest {

	private PlcRequestExecutorImpl executorImpl;
	private IPlcController<?> controller;
	private IPlcRequest plcRequest;
	
	@Before
	public void setUp() throws Exception {
		executorImpl = new PlcRequestExecutorImpl();
		controller = mock(IPlcController.class);
		plcRequest = mock(IPlcRequest.class);
	}

	@Test
	public void testExecuteActions() throws  Exception {
		when(controller.createRequest((IPlcAction[])null)).thenReturn(plcRequest);
		plcRequest.execute();
		
		executorImpl.executeActions(controller);
	}

	@Test
	public void testSetGetPlcActions() {
		IPlcAction[] plcActions = new IPlcAction[]{};
		executorImpl.setPlcActions(plcActions);
		assertArrayEquals(plcActions,executorImpl.getPlcActions());
	}


	private static class PlcRequestExecutorImpl extends AbstractPlcRequestExecutor{

		@Override
		public void execute(IPlcController<?> plcController)
				throws PlcAdaptorException {
		}
		
	}

}
