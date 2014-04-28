package com.sicpa.standard.sasscl.remoteControl.beanEditor;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.utils.ThreadUtils;
import com.thoughtworks.xstream.XStream;

public class PanelEditXMLBean extends JPanel {

	private static final long serialVersionUID = 1L;

	protected JTextArea areaXML;

	public PanelEditXMLBean() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill"));
		add((getAreaXML()), "grow");
	}

	public JTextArea getAreaXML() {
		if (this.areaXML == null) {
			this.areaXML = new JTextArea();
			this.areaXML.setLineWrap(false);
		}
		return this.areaXML;
	}

	public void setBean(final Object bean) {
		XStream xstream = new XStream();
		final String xml = xstream.toXML(bean);
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getAreaXML().setText(xml);
			}
		});
	}

	public Object getBean() {
		XStream xstream = new XStream();
		return xstream.fromXML(getAreaXML().getText());
	}
}
