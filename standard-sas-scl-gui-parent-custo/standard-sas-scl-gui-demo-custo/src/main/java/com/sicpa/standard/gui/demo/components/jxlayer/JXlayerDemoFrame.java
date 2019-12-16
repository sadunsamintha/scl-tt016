package com.sicpa.standard.gui.demo.components.jxlayer;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.PaintUtils;

public class JXlayerDemoFrame extends javax.swing.JFrame {

	private static final long serialVersionUID = 1L;
	private JXLayer<JComponent> panel;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				JXlayerDemoFrame inst = new JXlayerDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public JXlayerDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getPanel(), BorderLayout.CENTER);
		setSize(500, 400);
	}

	public JXLayer<JComponent> getPanel() {
		if (this.panel == null) {
			JPanel p = new JPanel();
			p.setLayout(new MigLayout());

			JButton b = new JButton("button 1");
			p.add(b);
			b = new JButton("button 2");
			p.add(b);
			b = new JButton("button 2");
			p.add(b, "wrap");

			JTextField t = new JTextField();
			p.add(t, "span,growx");

			this.panel = new JXLayer<JComponent>(p);

			this.panel.setUI(new MyLayerUI());

		}
		return this.panel;
	}

	public static class MyLayerUI extends AbstractLayerUI<JComponent> {
		Font f;

		public MyLayerUI() {
			this.f = new Font("arial", Font.BOLD, 45);
		}

		@Override
		protected void paintLayer(final Graphics2D g, final JXLayer<? extends JComponent> comp) {
			super.paintLayer(g, comp);

			Graphics2D g2 = (Graphics2D) g.create();
			
			PaintUtils.turnOnQualityRendering(g2);

			g2.setFont(this.f);

			int w = comp.getWidth();
			int h = comp.getHeight();

			g2.setColor(Color.red);
			g2.setComposite(AlphaComposite.SrcOver.derive(0.3f));
			g2.fillRect(0, 0, w, h);

			String text = "the JXLayer is here";
			int sw = SwingUtilities.computeStringWidth(g2.getFontMetrics(), text);

			int x = (w - sw) / 2;
			int y = h / 2 - 20;
			g2.setComposite(AlphaComposite.SrcOver);
			g2.drawString(text, x, y);

			g2.dispose();
		}
	}
}
