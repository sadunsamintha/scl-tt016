package com.sicpa.standard.sasscl.devices.plc.variable;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sicpa.standard.client.common.descriptor.validator.ValidatorException;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcIntegerVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcPulseVariableDescriptor;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.unit.PlcUnit;
import com.sicpa.standard.sasscl.devices.plc.variable.renderer.IPlcVariableDescriptorListener;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValueWithUnit;
import com.sicpa.standard.sasscl.devices.plc.variable.serialisation.PlcValuesForAllVar;
import com.sicpa.standard.sasscl.provider.impl.PlcProvider;

public class PlcDescriptorTest {

	private static final int BIG_VALUE = 888;
	private static final int SMALL_VALUE = 5;
	private static final String OUT_OF_RANGE_VAR_NAME = "out.of.range.var.name";
	private static final String IN_RANGE_VAR_NAME = "in.range.var.name";
	private static final String UNIT_VAR_NAME = "unit.var.name";
	public PlcPulseVariableDescriptor descCompare;
	public PlcPulseVariableDescriptor desc;

	@Before
	public void setUp() {

		PlcProvider provider = new PlcProvider();

		PlcUnit pulseUnit = PlcUnit.PULSE;

		descCompare = new PlcPulseVariableDescriptor();
		descCompare.setPlcProvider(provider);
		descCompare.setVariable(PlcVariable.createInt32Var(IN_RANGE_VAR_NAME));
		IPlcVariable<Boolean> varForUnit = PlcVariable.createBooleanVar(UNIT_VAR_NAME);
		descCompare.setUnitPlcVar(varForUnit);
		descCompare.setMinPulse(2);
		descCompare.setMaxPulse(1502);

		desc = new PlcPulseVariableDescriptor();
		desc.setPlcProvider(provider);
		desc.setVariable(PlcVariable.createInt32Var(OUT_OF_RANGE_VAR_NAME));
		desc.setUnitPlcVar(varForUnit);
		desc.setMinPulse(1);
		desc.setMaxPulse(501);

		PlcValuesForAllVar allValues = new PlcValuesForAllVar();
		allValues.add(new PlcValueWithUnit(BIG_VALUE, OUT_OF_RANGE_VAR_NAME, pulseUnit, UNIT_VAR_NAME));
		allValues.add(new PlcValueWithUnit(SMALL_VALUE, IN_RANGE_VAR_NAME, pulseUnit, UNIT_VAR_NAME));

		new EditablePlcVariables(allValues, desc, descCompare);
	}

	@Test(expected = ValidatorException.class)
	public void testIntegerDesc() throws Exception {

		PlcIntegerVariableDescriptor desc = new PlcIntegerVariableDescriptor();
		desc.setVariable(PlcVariable.createInt32Var(OUT_OF_RANGE_VAR_NAME));
		IPlcVariableDescriptorListener descListener = mock(IPlcVariableDescriptorListener.class);
		desc.addListener(descListener);

		desc.setMin(1);
		desc.setMax(150);
		desc.setValue(SMALL_VALUE);

		desc.validate();

		desc.setValue(500);

		try {
			desc.validate();
		} finally {
			verify(descListener, times(2)).valueChanged();
		}
	}

	@Test(expected = ValidatorException.class)
	public void rangeException() throws Exception {

		assertThat((Integer) desc.getValue(), equalTo(BIG_VALUE));
		assertThat((Integer) descCompare.getValue(), equalTo(SMALL_VALUE));

		desc.validate();
	}

	@Test
	public void inRange() throws Exception {

		// mocked executor service will swallow an exception thrown in the subsequent line
		TaskExecutor.setExecutor(Mockito.mock(ExecutorService.class));

		descCompare.setCurrentUnit(PlcUnit.PULSE);

		desc.setValue(SMALL_VALUE);
		desc.validate();
	}

	@Test(expected = ValidatorException.class)
	public void greaterThanLimit() throws Exception {

		// mocked executor service will swallow an exception thrown in the subsequent line
		TaskExecutor.setExecutor(Mockito.mock(ExecutorService.class));
		descCompare.setCurrentUnit(PlcUnit.PULSE);
		desc.setValue(SMALL_VALUE);

		desc.setGreaterThan(new PlcPulseVariableDescriptor[] { descCompare });
		desc.validate();

		descCompare.setValue(999);
		desc.validate();
	}

}
