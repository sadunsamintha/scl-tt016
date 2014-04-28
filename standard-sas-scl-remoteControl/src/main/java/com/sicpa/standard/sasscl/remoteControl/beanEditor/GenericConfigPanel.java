package com.sicpa.standard.sasscl.remoteControl.beanEditor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.client.common.descriptor.IPropertyDescriptor;
import com.sicpa.standard.client.common.descriptor.renderer.IParameterRenderer;
import com.sicpa.standard.client.common.exception.WarningRuntimeException;
import com.sicpa.standard.common.log.StdLogger;
import com.sicpa.standard.gui.utils.ReflectionUtils;
import com.sicpa.standard.sasscl.messages.MessageEventKey;

public class GenericConfigPanel extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private StdLogger logger = new StdLogger(GenericConfigPanel.class);

	protected List<IPropertyDescriptor> descriptors;
	protected List<IParameterRenderer> renderes;
	protected Object bean;

	public GenericConfigPanel(final Object bean, final List<IPropertyDescriptor> descriptors) {
		this.bean = bean;
		this.descriptors = descriptors;
		this.renderes = new ArrayList<IParameterRenderer>();
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout(""));
		for (IPropertyDescriptor desc : this.descriptors) {
			add(new JLabel(desc.getPropertyDescription()));
			IParameterRenderer renderer = createRenderer(desc);
			this.renderes.add(renderer);
			add(renderer.getRendererComponent(), "h 50, pushx,wrap");
		}
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
						this.logger.info("Saving bean property {0} value={1}", evt.getPropertyName(), newValue);
						m.invoke(this.bean, newValue);
						break;
					}
				}
			}
		} catch (Exception e) {
			throw new WarningRuntimeException(MessageEventKey.ModelEditing.ERROR_FAILED_TO_SAVE_BEAN_PROPERTY, e,
					evt.getPropertyName());
		}
		if (!methodFound) {
			throw new RuntimeException(evt.getPropertyName() + " not found");
		}
	}
}
