package com.sicpa.standard.gui.demo.components.jxlayer;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.effect.BufferedImageOpEffect;
import org.jdesktop.jxlayer.plaf.ext.LockableUI;
import org.jdesktop.swingx.image.StackBlurFilter;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.PaintUtils;

public class JXlayerLockUIDemoFrame extends javax.swing.JFrame {

	private static final long serialVersionUID = 1L;
	private JXLayer<JComponent> panel;
	private LockableUI lockui;
	private JToggleButton toggleButton;
	private JToggleButton toggleButton2;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				JXlayerLockUIDemoFrame inst = new JXlayerLockUIDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public JXlayerLockUIDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getPanel(), BorderLayout.CENTER);
		getContentPane().add(getToggleButton(), BorderLayout.SOUTH);
		getContentPane().add(getToggleButton2(), BorderLayout.NORTH);
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
			this.lockui = new MyLockLayer();
			this.lockui.setLocked(false);

			this.panel.setUI(this.lockui);

			panel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (lockui.isLocked()) {
						System.out.println("click on layer while blocked");
					}
				}
			});
		}
		return this.panel;
	}

	public static class MyLockLayer extends LockableUI {
		Font f = new Font("arial", Font.BOLD, 45);

		public MyLockLayer() {

		}

		@Override
		protected void paintLayer(final Graphics2D g, final JXLayer<? extends JComponent> comp) {
			super.paintLayer(g, comp);

			Graphics2D g2 = (Graphics2D) g.create();

			PaintUtils.turnOnQualityRendering(g2);

			g2.setFont(this.f);

			int w = comp.getWidth();
			int h = comp.getHeight();

			String lock = "Layer locked";
			int sw = SwingUtilities.computeStringWidth(g2.getFontMetrics(), lock);

			int x = w / 2 - sw / 2;
			int y = h / 2 - 20;

			g2.setColor(Color.red);
			g2.drawString(lock, x, y);

			g2.setStroke(new BasicStroke(5f));
			g2.drawRect(0, 0, w - 2, h - 2);

			g2.dispose();
		}
	}

	public JToggleButton getToggleButton() {
		if (this.toggleButton == null) {
			this.toggleButton = new JToggleButton("lock");
			this.toggleButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					JXlayerLockUIDemoFrame.this.lockui.setLocked(JXlayerLockUIDemoFrame.this.toggleButton.isSelected());

				}
			});
		}
		return this.toggleButton;
	}

	public JToggleButton getToggleButton2() {
		if (this.toggleButton2 == null) {
			this.toggleButton2 = new JToggleButton("with blur effect");
			this.toggleButton2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					if (!JXlayerLockUIDemoFrame.this.toggleButton2.isSelected()) {
						JXlayerLockUIDemoFrame.this.lockui.setLockedEffects();
					} else {
						// wrap the filter with the jxlayer's
						// BufferedImageOpEffect
						BufferedImageOpEffect effect = new BufferedImageOpEffect(new StackBlurFilter());
						// set it as the locked effect
						JXlayerLockUIDemoFrame.this.lockui.setLockedEffects(effect);
					}
				}
			});
		}
		return this.toggleButton2;
	}

}
