package com.sicpa.standard.sasscl.view.config;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.descriptor.IPropertyDescriptor;
import com.sicpa.standard.client.common.descriptor.renderer.IParameterRenderer;
import com.sicpa.standard.client.common.descriptor.validator.ValidatorException;
import com.sicpa.standard.client.common.descriptor.validator.ValidatorsException;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.utils.ConfigUtils;
import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.components.scroll.SmoothScrolling;
import com.sicpa.standard.gui.utils.ReflectionUtils;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.view.MainFrame;

public class GenericConfigPanel extends JPanel implements PropertyChangeListener {

	// public static void main(final String[] args) {
	// SwingUtilities.invokeLater(new Runnable() {
	//
	// @Override
	// public void run() {
	// SicpaLookAndFeel.install();
	//
	// CameraSimulatorConfig bean = new CameraSimulatorConfig();
	// bean.setPercentageBadCode(16);
	// bean.setReadCodeInterval(12);
	// bean.setCodeGetMethod(CodeGetMethod.requested);
	//
	// List<IPropertyDescriptor> descriptors = new ArrayList<IPropertyDescriptor>();
	//
	// RangeIntegerPropertyDescriptor rangeDesc = new RangeIntegerPropertyDescriptor();
	// rangeDesc.setMax(100);
	// rangeDesc.setMin(0);
	// rangeDesc.setStep(1);
	// rangeDesc.setProperty("percentageBadCode");
	// rangeDesc.setPropertyDescription("percentageBadCode");
	// descriptors.add(rangeDesc);
	//
	// rangeDesc = new RangeIntegerPropertyDescriptor();
	// rangeDesc.setMax(99999);
	// rangeDesc.setMin(0);
	// rangeDesc.setStep(1);
	// rangeDesc.setProperty("readCodeInterval");
	// rangeDesc.setPropertyDescription("readCodeInterval");
	// descriptors.add(rangeDesc);
	//
	// ConstantPropertyDescriptor constant = new ConstantPropertyDescriptor(CodeGetMethod.values());
	// constant.setProperty("codeGetMethod");
	// constant.setPropertyDescription("get code method");
	// descriptors.add(constant);
	//
	// JFrame f = new JFrame();
	// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// GenericConfigPanel panel = new GenericConfigPanel(bean, descriptors);
	//
	// f.getContentPane().add(panel);
	//
	// f.setSize(800, 600);
	// f.setVisible(true);
	//
	// }
	// });
	// }

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(GenericConfigPanel.class);

	protected List<IPropertyDescriptor> descriptors;
	protected List<IParameterRenderer> renderes;
	protected Object bean;
	protected JScrollPane scroll;
	protected JPanel mainPanel;

	public GenericConfigPanel(final Object bean, final List<IPropertyDescriptor> descriptors) {
		this.bean = bean;
		this.descriptors = descriptors;
		this.renderes = new ArrayList<IParameterRenderer>();
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		add(SmallScrollBar.createLayerSmallScrollBar(getScroll()), BorderLayout.CENTER);
	}

	public JScrollPane getScroll() {
		if (this.scroll == null) {
			this.scroll = new JScrollPane(getMainPanel());
			SmoothScrolling.enableFullScrolling(this.scroll);
		}
		return this.scroll;
	}

	public JPanel getMainPanel() {
		if (this.mainPanel == null) {
			this.mainPanel = new JPanel(new MigLayout("ltr"));
			for (IPropertyDescriptor desc : this.descriptors) {
				this.mainPanel.add(new JLabel(desc.getPropertyDescription()));
				IParameterRenderer renderer = createRenderer(desc);
				this.renderes.add(renderer);
				this.mainPanel.add(renderer.getRendererComponent(), "pushx,wrap");
			}
		}
		return this.mainPanel;
	}

	protected IParameterRenderer createRenderer(final IPropertyDescriptor desc) {
		IParameterRenderer res = null;
		ReflectionUtils reflec = new ReflectionUtils();

		try {
			Object currentValue = reflec.getValue(desc.getProperty(), this.bean);

			res = desc.getRenderer();
			if (res != null) {
				res.setDescriptor(desc);
				res.setPrevisouValue(currentValue);
				res.addPropertyChangeListener(this);
			}
		} catch (Exception e) {
			this.logger.error("", e);
		}

		return res;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		boolean methodFound = false;
		try {
			Object newValue = evt.getNewValue();
			String methodName = evt.getPropertyName();
			methodName = "set" + (methodName.charAt(0) + "").toUpperCase()
					+ methodName.substring(1, methodName.length());

			for (Method m : this.bean.getClass().getMethods()) {
				if (m.getName().equals(methodName)) {
					if (m.getParameterTypes().length == 1) {
						methodFound = true;
						OperatorLogger.log("{} - {} = {}", new Object[] { this.bean.getClass().getSimpleName(),
								methodName, newValue });
						this.logger.info("Saving bean property {} value={}", evt.getPropertyName(), newValue);
						m.invoke(this.bean, newValue);
						if (getMainFrame().getController().getBeanValidators() != null) {
							getMainFrame().getController().getBeanValidators().validate(this.bean);
						}
						ConfigUtils.save(this.bean);
						break;
					}
				}
			}
		} catch (final ValidatorsException e) {
			for (final ValidatorException ve : e.getValidatorExceptions()) {
				EventBusService.post(new MessageEvent(this, ve.getLangKey(), ve.getSource(), ve, ve.getParams()));
			}
		} catch (Exception e) {
			logger.error("", e);
			EventBusService.post(new MessageEvent(this,
					MessageEventKey.ModelEditing.ERROR_FAILED_TO_SAVE_BEAN_PROPERTY, evt.getPropertyName()));
		}
		if (!methodFound) {
			throw new RuntimeException(evt.getPropertyName() + " not found");
		}
	}

	protected MainFrame getMainFrame() {
		return (MainFrame) SwingUtilities.getWindowAncestor(this);
	}

	@Subscribe
	public void processStateChanged(final ApplicationFlowStateChangedEvent evt) {
		for (IParameterRenderer renderer : this.renderes) {
			renderer.setProductionRunning(evt.getCurrentState() == ApplicationFlowState.STT_STARTED);
		}
	}
}
