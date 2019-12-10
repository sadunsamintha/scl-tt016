package com.sicpa.standard.gui.demo.components.sicpa.propertySheet;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.propertySheet.PropertySheetPage;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class PropertySheetDemoFrameCustomBeanInfo extends javax.swing.JFrame {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				PropertySheetDemoFrameCustomBeanInfo inst = new PropertySheetDemoFrameCustomBeanInfo();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private PanelDemoPropertySheet panel;
	private PropertySheetPage page;

	public PropertySheetDemoFrameCustomBeanInfo() {
		super();
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		setLayout(new MigLayout("fill"));

		getContentPane().add(getPanel(), "grow,wrap");
		getContentPane().add(getPage(), "grow,push");
		setSize(500, 650);
	}

	public PropertySheetPage getPage() {
		if (this.page == null) {
			this.page = new PropertySheetPage();
			this.page.setBean(getPanel(), "my bean");

			this.page.setSortingProperties(true);
			this.page.setDescriptionVisible(true);
			this.page.setShowCategorie(true);
		}
		return this.page;
	}

	public PanelDemoPropertySheet getPanel() {
		if (this.panel == null) {
			this.panel = new PanelDemoPropertySheet();
		}
		return this.panel;
	}
}
