package com.sicpa.standard.sasscl.devices.plc.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import org.hamcrest.core.IsEqual;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sicpa.standard.plc.controller.PlcException;
import com.sicpa.standard.plc.controller.actions.PlcAction;
import com.sicpa.standard.plc.controller.internal.PlcNotificationImpl;
import com.sicpa.standard.plc.driver.event.PlcEventCode;
import com.sicpa.standard.plc.value.IPlcVariable;
import com.sicpa.standard.plc.value.PlcVariable;

@SuppressWarnings("unchecked")
@Ignore
public class PlcSimulatorViewTest {

	private static final String WRITE_FAILED_VARIABLE_NOT_FOUND = "Write Failed. Variable not found.";

	private static final String WRITTEN_TO_PLC = "Value is written to PLC.";

	private static final String VARIABLE_NOT_FOUND = "Variable not found.";

	private static final String FALSE = "FALSE";

	private static final String TRUE = "TRUE";

	private static final String ITEM1 = "ITEM1";

	private static final IPlcVariable<Boolean> PLC_VARIABLE_BOOLEAN = PlcVariable.createBooleanVar("ITEM1");

	private static final IPlcVariable<Integer> PLC_VARIABLE_INTEGER = PlcVariable.createInt32Var("VARIABLE_KEY");

	private PlcSimulatorView plcSimulatorView;

	private PlcSimulatorController plcSimulatorController;

	private JPanel connectDisconnectButtonsPanel;
	private JPanel readWritePlcVariablePanel;
	private JPanel sendNotificationPanel;

	@Before
	public void setUp() throws Exception {
		plcSimulatorController = mock(PlcSimulatorController.class);
		plcSimulatorView = new PlcSimulatorView(plcSimulatorController);
	}

	@Test
	public void testInitGUI() throws Exception {
		setJPanelsNull();
		getJPanels();

		assertNull(connectDisconnectButtonsPanel);
		assertNull(readWritePlcVariablePanel);
		assertNull(sendNotificationPanel);

		plcSimulatorView.initGUI();

		getJPanels();
		// assertEquals(7, plcSimulatorView.getComponentCount());

		assertNotNull(connectDisconnectButtonsPanel);
		assertNotNull(readWritePlcVariablePanel);
		assertNotNull(sendNotificationPanel);
	}

	@Test
	public void testButtonConnectActionPerformed() {
		plcSimulatorView.buttonConnectActionPerformed();
		verify(plcSimulatorController).fireStatusChanged(PlcEventCode.CONNECTED);
	}

	@Test
	public void testButtonDisconnectActionPerformed() {
		plcSimulatorView.buttonDisconnectActionPerformed();
		verify(plcSimulatorController).fireStatusChanged(PlcEventCode.DISCONNECTED);
	}

	@Test
	public void testHideProductionPanel() throws Exception {
		setJPanelsVisible();

		plcSimulatorView.hideProductionPanel();

		assertFalse(connectDisconnectButtonsPanel.isVisible());
		assertTrue(readWritePlcVariablePanel.isVisible());
		assertFalse(sendNotificationPanel.isVisible());
	}

	@Test
	public void testSetupNotificationComboBox() throws Exception {

		setPlcNotificationVariableComboBox(null);

		when(plcSimulatorController.getRegisteredNotifications()).thenReturn(createPlcNotificationMap());

		plcSimulatorView.setupNotificationComboBox();

		Assert.assertThat(getPlcNotificationVariableComboBox().getItemListeners().length, IsEqual.equalTo(1));
		Assert.assertThat(getPlcNotificationVariableComboBox().getActionListeners().length, IsEqual.equalTo(1));
	}

	@Test
	public void testPlcNotificationVariableComboBoxItemListenerBoolean() throws Exception {
		checkActionListener(getPlcNotificationVariableComboBox(), PLC_VARIABLE_BOOLEAN, FALSE,
				getNotificationValueSpinner());
	}

	@Test
	public void testPlcNotificationVariableComboBoxItemListenerInteger() throws Exception {
		checkItemListener(getPlcNotificationVariableComboBox(), PLC_VARIABLE_INTEGER, 0, getNotificationValueSpinner());
	}

	@Test
	public void testPlcVariableComboBoxItemListenerBoolean() throws Exception {
		checkItemListener(getPlcVariableComboBox(), PLC_VARIABLE_BOOLEAN, FALSE, getVariableValueSpinner());
	}

	@Test
	public void testPlcVariableComboBoxItemListenerInteger() throws Exception {
		checkItemListener(getPlcVariableComboBox(), PLC_VARIABLE_INTEGER, 0, getVariableValueSpinner());
	}

	@Test
	public void testSetupButtonReadInteger() throws Exception {
		checkSetupButtonReadInteger(0);
	}

	@Test
	public void testSetupButtonReadIntegerString() throws Exception {
		checkSetupButtonReadInteger("0");
	}

	@Test
	public void testSetupButtonReadBooleanFalse() throws Exception {
		checkSetupButtonBoolean(FALSE);
	}

	@Test
	public void testSetupButtonReadBooleanTrue() throws Exception {
		checkSetupButtonBoolean(TRUE);
	}

	@Test
	public void testSetupButtonReadStringFalse() throws Exception {

		fixturePlcVariableComboBox(PLC_VARIABLE_BOOLEAN, FALSE);

		plcSimulatorView.setupButtonRead();

		ActionListener actionListener = fixtureActionListener(getReadButton());

		getPlcVariableComboBox().setSelectedIndex(0);

		checkItemListener(getPlcVariableComboBox(), PLC_VARIABLE_BOOLEAN, FALSE, getVariableValueSpinner());

		actionListener.actionPerformed(createAction(getReadButton()));

		assertEquals(FALSE, getVariableValueSpinner().getValue());
	}

	@Test
	public void testSetupButtonReadVariableNotFound() throws Exception {

		fixturePlcVariableComboBox(PLC_VARIABLE_BOOLEAN, FALSE);

		plcSimulatorView.setupButtonRead();

		ActionListener actionListener = fixtureActionListener(getReadButton());

		getPlcVariableComboBox().setSelectedIndex(0);

		when(plcSimulatorController.getPlcVariableMap()).thenReturn(createMapPlcVariables(null));

		actionListener.actionPerformed(createAction(getReadButton()));

		assertEquals(VARIABLE_NOT_FOUND, getStatusLabel().getText());
	}

	@Test
	public void testSetupWriteButtonReadInteger() throws Exception {
		checkSetupWriteButtonRead(PLC_VARIABLE_INTEGER, 0);
	}

	@Test
	public void testSetupWriteButtonReadIntegerString() throws Exception {
		checkSetupWriteButtonRead(PLC_VARIABLE_INTEGER, "0");
	}

	@Test
	public void testSetupWriteButtonStringFalse() throws Exception {
		checkSetupWriteButtonRead(PLC_VARIABLE_BOOLEAN, FALSE);
	}

	@Test
	public void testSetupWriteButtonStringTrue() throws Exception {
		checkSetupWriteButtonRead(PLC_VARIABLE_BOOLEAN, TRUE);
	}

	@Test
	public void testSetupWriteButtonVariableNotFound() throws Exception {

		fixturePlcVariableComboBox(PLC_VARIABLE_BOOLEAN, FALSE);

		plcSimulatorView.setupWriteButton();

		ActionListener actionListener = fixtureActionListener(getWriteButton());

		when(plcSimulatorController.getPlcVariableMap()).thenReturn(createMapPlcVariables(null));

		actionListener.actionPerformed(createAction(getReadButton()));

		assertEquals(WRITE_FAILED_VARIABLE_NOT_FOUND, getStatusLabel().getText());
	}

	@Test
	public void testSetupSendButtonBooleanFalse() throws Exception {
		checkSetupSendButton(PLC_VARIABLE_BOOLEAN, FALSE);
	}

	private void checkSetupSendButton(IPlcVariable<?> variable, Object value) throws Exception {
		fixturePlcNotificationVariableComboBox(variable, TRUE);
		plcSimulatorView.setupSendButton();
		when(plcSimulatorController.getPlcVariableMap()).thenReturn(createMapPlcVariables(variable));
		when(plcSimulatorController.getRegisteredNotifications()).thenReturn(createPlcNotificationMap());
		ActionListener actionListener = fixtureActionListener(getSendButton());
		getPlcNotificationVariableComboBox().setModel(new ListComboBoxModel(Arrays.asList("ITEM1")));
		getPlcNotificationVariableComboBox().setSelectedItem("ITEM1");

		if (variable == PLC_VARIABLE_BOOLEAN) {
			checkItemListener(getPlcNotificationVariableComboBox(), PLC_VARIABLE_BOOLEAN, FALSE,
					getNotificationValueSpinner());
			getNotificationValueSpinner().setValue(value);
		}

		actionListener.actionPerformed(createAction(getSendButton()));
		Assert.assertThat(getStatusLabel().getText(),
				IsEqual.equalTo("Request ITEM1 sent to PLC #" + value.toString().toLowerCase()));
	}

	@Test
	public void testSetupSendButtonBooleanTrue() throws Exception {
		checkSetupSendButton(PLC_VARIABLE_BOOLEAN, TRUE);
	}

	@Test
	public void testSetupSendButtonInteger() throws Exception {
		checkSetupSendButton(PLC_VARIABLE_INTEGER, 0);

	}

	private <T> T getPrivateField(Class<?> clazz, String name, Object instance, Class<T> type) throws Exception {
		Field declaredField = clazz.getDeclaredField(name);
		declaredField.setAccessible(true);
		return (T) declaredField.get(instance);
	}

	private void setPrivateField(Class<?> clazz, String name, Object instance, Object value) throws Exception {
		Field declaredField = clazz.getDeclaredField(name);
		declaredField.setAccessible(true);
		declaredField.set(instance, value);
	}

	private void getJPanels() throws Exception {
		connectDisconnectButtonsPanel = getPrivateField(PlcSimulatorView.class, "connectDisconnectButtonsPanel",
				plcSimulatorView, JPanel.class);
		readWritePlcVariablePanel = getPrivateField(PlcSimulatorView.class, "readWritePlcVariablePanel",
				plcSimulatorView, JPanel.class);
		sendNotificationPanel = getPrivateField(PlcSimulatorView.class, "sendNotificationPanel", plcSimulatorView,
				JPanel.class);
	}

	private void setJPanelsNull() throws Exception {
		setPrivateField(PlcSimulatorView.class, "connectDisconnectButtonsPanel", plcSimulatorView, null);
		setPrivateField(PlcSimulatorView.class, "readWritePlcVariablePanel", plcSimulatorView, null);
		setPrivateField(PlcSimulatorView.class, "sendNotificationPanel", plcSimulatorView, null);
	}

	private void setJPanelsVisible() throws Exception {
		getJPanels();
		connectDisconnectButtonsPanel.setVisible(true);
		readWritePlcVariablePanel.setVisible(true);
		sendNotificationPanel.setVisible(true);
	}

	@SuppressWarnings("rawtypes")
	private Map<String, PlcNotificationImpl<?>> createPlcNotificationMap() {
		HashMap<String, PlcNotificationImpl<?>> variable = new HashMap<String, PlcNotificationImpl<?>>();
		variable.put(ITEM1, new PlcNotificationImpl(PLC_VARIABLE_BOOLEAN, PlcAction.request("Action")));
		return variable;
	}

	private HashMap<String, IPlcVariable<?>> createMapPlcVariables(IPlcVariable<?> plcVariable) {
		HashMap<String, IPlcVariable<?>> map = new HashMap<String, IPlcVariable<?>>();
		map.put(ITEM1, plcVariable);
		return map;
	}

	private JComboBox getPlcNotificationVariableComboBox() throws Exception {
		return getPrivateField(PlcSimulatorView.class, "plcNotificationVariableComboBox", plcSimulatorView,
				JComboBox.class);
	}

	private JComboBox getPlcVariableComboBox() throws Exception {
		return getPrivateField(PlcSimulatorView.class, "plcVariableComboBox", plcSimulatorView, JComboBox.class);
	}

	private void setPlcNotificationVariableComboBox(JComboBox comboBox) throws Exception {
		setPrivateField(PlcSimulatorView.class, "plcNotificationVariableComboBox", plcSimulatorView, comboBox);
	}

	private void setPlcVariableComboBox(JComboBox comboBox) throws Exception {
		setPrivateField(PlcSimulatorView.class, "plcVariableComboBox", plcSimulatorView, comboBox);
	}

	private JSpinner getNotificationValueSpinner() throws Exception {
		return getPrivateField(PlcSimulatorView.class, "notificationValueSpinner", plcSimulatorView, JSpinner.class);
	}

	private JSpinner getVariableValueSpinner() throws Exception {
		return getPrivateField(PlcSimulatorView.class, "variableValueSpinner", plcSimulatorView, JSpinner.class);
	}

	private JButton getReadButton() throws Exception {
		return getPrivateField(PlcSimulatorView.class, "readButton", plcSimulatorView, JButton.class);
	}

	private JButton getWriteButton() throws Exception {
		return getPrivateField(PlcSimulatorView.class, "writeButton", plcSimulatorView, JButton.class);
	}

	private JButton getSendButton() throws Exception {
		return getPrivateField(PlcSimulatorView.class, "sendButton", plcSimulatorView, JButton.class);
	}

	private JLabel getStatusLabel() throws Exception {
		return getPrivateField(PlcSimulatorView.class, "statusLabel", plcSimulatorView, JLabel.class);
	}

	private void checkItemListener(JComboBox comboBox, IPlcVariable<?> variable, Object valueExpected, JSpinner spinner)
			throws Exception {

		ItemListener itemListener = comboBox.getItemListeners()[0];

		when(plcSimulatorController.getPlcVariableMap()).thenReturn(createMapPlcVariables(variable));

		itemListener.itemStateChanged(new ItemEvent(comboBox, ItemEvent.ITEM_STATE_CHANGED, ITEM1, 0));

		assertEquals(valueExpected, spinner.getValue());
	}

	private void checkActionListener(JComboBox comboBox, IPlcVariable<?> variable, Object valueExpected,
			JSpinner spinner) throws Exception {

		ActionListener itemListener = comboBox.getActionListeners()[0];

		when(plcSimulatorController.getPlcVariableMap()).thenReturn(createMapPlcVariables(variable));

		comboBox.setModel(new ListComboBoxModel(Arrays.asList(ITEM1)));

		comboBox.setSelectedItem(ITEM1);

		Assert.assertThat(comboBox.getSelectedItem().toString(), IsEqual.equalTo(ITEM1));

		itemListener.actionPerformed(new ActionEvent(comboBox, ActionEvent.ACTION_PERFORMED, ITEM1, 0));

		assertEquals(valueExpected, spinner.getValue());
	}

	private ActionEvent createAction(Object component) throws Exception {
		return new ActionEvent(component, 0, "");
	}

	private ActionListener fixtureActionListener(JButton jButton) throws Exception {
		ActionListener[] actionListeners = jButton.getActionListeners();

		assertEquals(1, actionListeners.length);

		return actionListeners[0];
	}

	private <T> T fixturePlcVariableComboBox(IPlcVariable<T> variable, Object value) throws Exception, PlcException {
		setPlcVariableComboBox(null);

		when(plcSimulatorController.getPlcVariableMap()).thenReturn(createMapPlcVariables(variable));

		plcSimulatorView.initGUI();

		when(plcSimulatorController.getPlcValue(variable)).thenReturn((T) value);

		return (T) value;
	}

	private <T> T fixturePlcNotificationVariableComboBox(IPlcVariable<T> variable, Object value) throws Exception,
			PlcException {
		setPlcNotificationVariableComboBox(null);

		when(plcSimulatorController.getRegisteredNotifications()).thenReturn(createPlcNotificationMap());

		plcSimulatorView.setupNotificationComboBox();

		when(plcSimulatorController.getPlcValue(variable)).thenReturn((T) value);

		return (T) value;
	}

	private void checkSetupButtonBoolean(String value) throws Exception, PlcException {
		fixturePlcVariableComboBox(PLC_VARIABLE_BOOLEAN, Boolean.valueOf(value));

		plcSimulatorView.setupButtonRead();

		ActionListener actionListener = fixtureActionListener(getReadButton());

		getPlcVariableComboBox().setSelectedIndex(0);

		checkItemListener(getPlcVariableComboBox(), PLC_VARIABLE_BOOLEAN, FALSE, getVariableValueSpinner());

		actionListener.actionPerformed(createAction(getReadButton()));

		assertEquals(value, getVariableValueSpinner().getValue());
	}

	private void checkSetupButtonReadInteger(Object value) throws Exception, PlcException {
		fixturePlcVariableComboBox(PLC_VARIABLE_INTEGER, value);

		plcSimulatorView.setupButtonRead();

		ActionListener actionListener = fixtureActionListener(getReadButton());

		getPlcVariableComboBox().setSelectedIndex(0);

		actionListener.actionPerformed(createAction(getReadButton()));

		Object expectedValue = null;
		if (value instanceof String) {
			expectedValue = Integer.valueOf((String) value);
		} else {
			expectedValue = value;
		}
		assertEquals(expectedValue, getVariableValueSpinner().getValue());
	}

	private void checkSetupWriteButtonRead(IPlcVariable<?> variable, Object value) throws Exception, PlcException {
		fixturePlcVariableComboBox(variable, value);

		if (variable.equals(PLC_VARIABLE_BOOLEAN)) {
			checkItemListener(getPlcVariableComboBox(), variable, FALSE, getVariableValueSpinner());
			getVariableValueSpinner().setValue(value);
		}

		plcSimulatorView.setupWriteButton();

		ActionListener actionListener = fixtureActionListener(getWriteButton());

		getPlcVariableComboBox().setSelectedIndex(0);

		actionListener.actionPerformed(createAction(getWriteButton()));

		assertEquals(WRITTEN_TO_PLC, getStatusLabel().getText());
	}

}
