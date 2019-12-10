package com.sicpa.standard.gui.demo.components.sicpa.propertySheet;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.sicpa.standard.gui.components.propertySheet.PropertySheetPage;

public class PropertySheetDemoFrameJButton extends javax.swing.JFrame {

	// add C:\Program Files\Java\jdk1.6.0_10\lib\dt.jar to the build path
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PropertySheetDemoFrameJButton inst = new PropertySheetDemoFrameJButton();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private JButton button;
	private PropertySheetPage page;

	public PropertySheetDemoFrameJButton() {
		super();
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(400, 500);

		getContentPane().add(getPage());
		getContentPane().add(getButton(), BorderLayout.NORTH);

	}

	public JButton getButton() {
		if (this.button == null) {
			this.button = new JButton("some text");
			this.button.addPropertyChangeListener(new PropertyChangeListener() {

				@Override
				public void propertyChange(final PropertyChangeEvent evt) {
					System.out.println(evt.getPropertyName() + "=>" + evt.getNewValue());
				}
			});
		}
		return this.button;
	}

	public PropertySheetPage getPage() {
		if (this.page == null) {
			this.page = new PropertySheetPage();
			this.page.setScanParentClass(true);
			this.page.setBean(getButton(), "button");
		}
		return this.page;
	}
}
