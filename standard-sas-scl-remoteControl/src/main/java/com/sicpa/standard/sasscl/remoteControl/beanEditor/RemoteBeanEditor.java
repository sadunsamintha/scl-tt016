package com.sicpa.standard.sasscl.remoteControl.beanEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.springframework.beans.BeanUtils;

import com.sicpa.standard.client.common.descriptor.IPropertyDescriptor;
import com.sicpa.standard.client.common.descriptor.impl.BooleanPropertyDescriptor;
import com.sicpa.standard.client.common.descriptor.impl.FreeTextDescriptor;
import com.sicpa.standard.client.common.descriptor.impl.RangeDoublePropertyDescriptor;
import com.sicpa.standard.client.common.descriptor.impl.RangeIntegerPropertyDescriptor;
import com.sicpa.standard.client.common.ioc.BeanProvider;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.monitoring.mbean.sas.RemoteControlSasMBean;
import com.sicpa.standard.sasscl.remoteControl.BackgroundTask;
import com.sicpa.standard.sasscl.remoteControl.beanViewer.RemoteBeanViewer;
import com.sicpa.standard.sasscl.remoteControl.ioc.RemoteControlBeansName;
import com.thoughtworks.xstream.XStream;

public class RemoteBeanEditor extends JPanel {

	private static final long serialVersionUID = 1L;

	protected Object bean;
	protected JCheckBox jcbEditing;
	protected JCheckBox jcbXML;
	protected JScrollPane scroll;
	protected JButton buttonSave;
	protected RemoteControlSasMBean controlBean;
	protected String beanName;
	protected PanelEditXMLBean xmlPanel;

	public RemoteBeanEditor() {
		initGUI();
	}

	public RemoteBeanEditor(final Object bean, final String beanName) {
		initGUI();
		this.bean = bean;
		this.controlBean = BeanProvider.getBean(RemoteControlBeansName.CONTROL_BEAN);
		this.beanName = beanName;
		jcbEditingActionPerformed();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill"));
		add(getJcbEditing(), "spanx , split 3");
		add(getJcbXML(), "");
		add(getButtonSave(), "");
		add(getScroll(), "grow,push,wrap");

	}

	protected void buildEditor() {

		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(this.bean.getClass());
		List<IPropertyDescriptor> descs = new ArrayList<IPropertyDescriptor>();

		for (PropertyDescriptor desc : descriptors) {

			if (desc.getPropertyType().equals(int.class)) {
				RangeIntegerPropertyDescriptor r = new RangeIntegerPropertyDescriptor();
				r.setMin(0);
				r.setMax(9999999);
				r.setProperty(desc.getName());
				r.setStep(1);
				r.setPropertyDescription(desc.getName());
				descs.add(r);
			} else if (desc.getPropertyType().equals(double.class)) {
				RangeDoublePropertyDescriptor r = new RangeDoublePropertyDescriptor();
				r.setMin(0d);
				r.setMax(9999999d);
				r.setProperty(desc.getName());
				r.setStep(0.01);
				r.setPropertyDescription(desc.getName());
				descs.add(r);
			} else if (desc.getPropertyType().equals(boolean.class)) {
				BooleanPropertyDescriptor r = new BooleanPropertyDescriptor();
				r.setProperty(desc.getName());
				r.setPropertyDescription(desc.getName());
				descs.add(r);
			} else if (desc.getPropertyType().equals(String.class)) {
				FreeTextDescriptor r = new FreeTextDescriptor();
				r.setProperty(desc.getName());
				r.setPropertyDescription(desc.getName());
				descs.add(r);
			}
		}

		GenericConfigPanel panel = new GenericConfigPanel(this.bean, descs);
		getScroll().setViewportView(panel);
	}

	protected void buildViewer() {
		RemoteBeanViewer viewer = new RemoteBeanViewer();
		viewer.setBean(this.bean);
		getScroll().setViewportView(viewer);
	}

	public JCheckBox getJcbEditing() {
		if (this.jcbEditing == null) {
			this.jcbEditing = new JCheckBox("Editing");
			this.jcbEditing.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					jcbEditingActionPerformed();
				}
			});
		}
		return this.jcbEditing;
	}

	protected void jcbEditingActionPerformed() {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {

				getJcbXML().setSelected(false);
				if (getJcbEditing().isSelected()) {
					buildEditor();
					getButtonSave().setVisible(true);
					getJcbXML().setVisible(true);
				} else {
					buildViewer();
					getButtonSave().setVisible(false);
					getJcbXML().setVisible(false);
				}
				// revalidate();
			}
		});
	}

	public JScrollPane getScroll() {
		if (this.scroll == null) {
			this.scroll = new JScrollPane();
		}
		return this.scroll;
	}

	public JButton getButtonSave() {
		if (this.buttonSave == null) {
			this.buttonSave = new JButton("Save");
			this.buttonSave.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonSaveActionPerformed();
				}
			});
		}
		return this.buttonSave;
	}

	protected void buttonSaveActionPerformed() {
		new BackgroundTask(new Runnable() {
			@Override
			public void run() {
				if (RemoteBeanEditor.this.jcbXML.isSelected()) {
					RemoteBeanEditor.this.bean = getXmlPanel().getBean();
				}
				XStream xstream = new XStream();
				RemoteBeanEditor.this.controlBean.saveXMLBean(RemoteBeanEditor.this.beanName,
						xstream.toXML(RemoteBeanEditor.this.bean));
			}
		}).start();
	}

	public JCheckBox getJcbXML() {
		if (this.jcbXML == null) {
			this.jcbXML = new JCheckBox("show xml");
			this.jcbXML.setSelected(false);
			this.jcbXML.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					jcbXMLActionPerformed();
				}
			});
		}
		return this.jcbXML;
	}

	protected void jcbXMLActionPerformed() {
		if (this.jcbXML.isSelected()) {
			getScroll().setViewportView(getXmlPanel());
			getXmlPanel().setBean(this.bean);
		} else {
			this.bean = getXmlPanel().getBean();
			buildEditor();
		}
	}

	public PanelEditXMLBean getXmlPanel() {
		if (this.xmlPanel == null) {
			this.xmlPanel = new PanelEditXMLBean();
		}
		return this.xmlPanel;
	}
}
