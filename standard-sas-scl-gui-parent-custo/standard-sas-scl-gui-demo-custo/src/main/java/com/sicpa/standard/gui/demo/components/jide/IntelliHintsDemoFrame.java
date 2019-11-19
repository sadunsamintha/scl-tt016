package com.sicpa.standard.gui.demo.components.jide;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import com.jidesoft.hints.ListDataIntelliHints;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class IntelliHintsDemoFrame extends javax.swing.JFrame {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				IntelliHintsDemoFrame inst = new IntelliHintsDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private JTextField field;

	public IntelliHintsDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill"));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		add(getField(), "growx");
		setSize(400, 300);
	}

	public JTextField getField() {
		if (this.field == null) {
			this.field = new JTextField("http://");
			List<String> urls = new ArrayList<String>();
			urls.add("http://weblogs.java.net");
			urls.add("http://www.pushing-pixels.org");
			urls.add("http://swinglabs.org");
			urls.add("http://www.jidesoft.com");
			urls.add("http://www.jhlabs.com");
			urls.add("http://substance.dev.java.net");
			urls.add("http://timingframework.dev.java.net");

			ListDataIntelliHints intellihints = new ListDataIntelliHints(this.field, urls);
			intellihints.setCaseSensitive(false);
		}
		return this.field;
	}

}
