package com.sicpa.standard.sasscl.devices.plc.variable.renderer;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.text.MultiLineLabel;
import com.sicpa.standard.sasscl.devices.plc.variable.descriptor.PlcVariableDescriptor;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

import net.miginfocom.swing.MigLayout;

public class PlcVariableInfoRenderer extends JLabel {

	private static final long serialVersionUID = 1L;
	
	private PlcVariableDescriptor desc;
	private JDialog dialog;
	private JTextArea infoTextArea;
	
	private static final Logger logger = LoggerFactory.getLogger(PlcVariableInfoRenderer.class);

	public PlcVariableInfoRenderer(final PlcVariableDescriptor desc) {
		super(getInfoIcon());
		this.desc = desc;
		initGUI();
	}

	private void initGUI() {
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				displayInfoDialog();
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				displayInfoDialog();
			}
		});
	}
	
	private static ImageIcon getInfoIcon() {
		String resourceName = "info.png";
		ImageIcon licenceIcon = null;
		URL url = ClassLoader.getSystemResource(resourceName);
		if (url != null) {
			try {
				licenceIcon = new ImageIcon((GraphicsUtilities.loadCompatibleImage(url)));
			} catch (IOException e) {
				logger.error("", e);
			}
		}
		return licenceIcon;
	}
	
	private void displayInfoDialog() {
		putInfoContent();
		getDialog().setVisible(true);
	}
	
	private void putInfoContent() {
		getInfoTextArea().setText(Messages.get(desc.getVarName()));
		getDialog().pack();
	}
	
	public JDialog getDialog() {
		if (dialog == null) {
			dialog = new JDialog();
			dialog.setLayout(new MigLayout("fill"));
			dialog.setMinimumSize(new Dimension(650, 300));
			dialog.getContentPane().add(infoTextArea, "push,grow");

		}
		return dialog;
	}
	
	public JTextArea getInfoTextArea() {
		if (infoTextArea == null) {
			infoTextArea = new MultiLineLabel();
		}
		return infoTextArea;
	}
	
	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		getInfoTextArea().setText(Messages.get(desc.getVarName()));
	}
}
