package com.sicpa.standard.sasscl.business.activation.impl.beforeActivationAction;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;

public class FilterDuplicatedCodeActionTest {
	FilterDuplicatedCodeAction filter;

	@Before
	public void setup() {
		ProductionParameters productionParameters = new ProductionParameters();
		productionParameters.setProductionMode(ProductionMode.STANDARD);
		filter = new FilterDuplicatedCodeAction();
		filter.setProductionParameters(productionParameters);
	}

	@Test
	public void testValidCode() {

		String stringCode = "123";

		BeforeActivationResult res = filter.receiveCode(new Code(stringCode), true, "camera-sas");

		Assert.assertTrue(res.isValid());
		Assert.assertFalse(res.isFiltered());
		Assert.assertEquals(new Code(stringCode), res.getCode());

		// 2nd code should be filtered
		res = filter.receiveCode(new Code(stringCode), true, "camera-sas");
		Assert.assertTrue(res.isValid());
		Assert.assertTrue(res.isFiltered());
		Assert.assertEquals(new Code(stringCode), res.getCode());
	}

	@Test
	public void testInValidCode() {

		BeforeActivationResult res = filter.receiveCode(new Code(""), false, "camera-sas");

		Assert.assertFalse(res.isValid());
		Assert.assertFalse(res.isFiltered());
		Assert.assertEquals(new Code(""), res.getCode());

		// 2nd bad code should not be filtered
		res = filter.receiveCode(new Code(""), false, "camera-sas");
		Assert.assertFalse(res.isValid());
		Assert.assertFalse(res.isFiltered());
		Assert.assertEquals(new Code(""), res.getCode());
	}

	@Test
	public void testNextAction() {
		AbstractBeforeActivationAction action = Mockito.mock(AbstractBeforeActivationAction.class);
		filter.setNextAction(action);
		filter.receiveCode(new Code(""), true, "camera-sas");
		Mockito.verify(action, Mockito.times(1)).receiveCode(new Code(""), true, "camera-sas");
	}
}
