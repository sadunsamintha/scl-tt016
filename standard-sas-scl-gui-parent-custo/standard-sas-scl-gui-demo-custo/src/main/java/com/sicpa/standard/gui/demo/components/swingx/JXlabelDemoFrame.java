package com.sicpa.standard.gui.demo.components.swingx;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.MattePainter;

import com.jhlabs.image.CausticsFilter;
import com.jhlabs.image.ChromeFilter;
import com.jhlabs.image.ShadowFilter;
import com.jhlabs.image.SparkleFilter;

public class JXlabelDemoFrame extends javax.swing.JFrame {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JXlabelDemoFrame inst = new JXlabelDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public JXlabelDemoFrame() {
		super();
		initGUI();
	}

	private JXLabel label;
	private JXPanel panel;

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		getContentPane().add(getPanel(), BorderLayout.CENTER);

		setSize(600, 300);
	}

	public JXLabel getLabel() {
		if (this.label == null) {
			this.label = new JXLabel("Hello World");
			this.label.setFont(new Font("Arial", Font.BOLD, 100));
			AbstractPainter<JXLabel> fg = (AbstractPainter<JXLabel>) this.label.getForegroundPainter();
			fg.setFilters(new ChromeFilter(), new ShadowFilter(5, 5, -2, .7f));
		}
		return this.label;
	}

	public JXPanel getPanel() {
		if (this.panel == null) {
			this.panel = new JXPanel();
			MattePainter background = new MattePainter();
			background.setFilters(new CausticsFilter(), new SparkleFilter());
			this.panel.setBackgroundPainter(background);
			this.panel.add(getLabel());
		}
		return this.panel;
	}
}
