package com.sicpa.standard.gui.components.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class ComponentTitledBorder implements Border, MouseListener, SwingConstants {
	int offset = 5;

	JComponent comp;
	JComponent container;
	Rectangle rect;
	Border border;

	public ComponentTitledBorder(final JComponent comp, final JComponent container, final Border border) {
		this.comp = comp;
		comp.setOpaque(true);
		this.container = container;
		this.border = border;
		container.addMouseListener(this);
	}

	public boolean isBorderOpaque() {
		return true;
	}

	public void paintBorder(final Component _container, final Graphics g, final int x, final int y, final int width,
			final int height) {
		Insets borderInsets = this.border.getBorderInsets(this.container);
		Insets insets = getBorderInsets(this.container);
		int temp = (insets.top - borderInsets.top) / 2;
		this.border.paintBorder(this.container, g, x, y + temp, width, height - temp);
		Dimension size = this.comp.getPreferredSize();
		this.rect = new Rectangle(this.offset, 0, size.width, size.height);
		SwingUtilities.paintComponent(g, this.comp, this.container, this.rect);
	}

	public Insets getBorderInsets(final Component c) {
		Dimension size = this.comp.getPreferredSize();
		Insets insets = this.border.getBorderInsets(c);
		insets.top = Math.max(insets.top, size.height);
		return insets;
	}

	private void dispatchEvent(final MouseEvent me) {
		if (this.rect != null && this.rect.contains(me.getX(), me.getY())) {
			Point pt = me.getPoint();
			pt.translate(-this.offset, 0);
			this.comp.setBounds(this.rect);
			this.comp.dispatchEvent(new MouseEvent(this.comp, me.getID(), me.getWhen(), me.getModifiers(), pt.x, pt.y,
					me.getClickCount(), me.isPopupTrigger(), me.getButton()));
			if (!this.comp.isValid())
				this.container.repaint();
		}
	}

	public void mouseClicked(final MouseEvent me) {
		dispatchEvent(me);
	}

	public void mouseEntered(final MouseEvent me) {
		dispatchEvent(me);
	}

	public void mouseExited(final MouseEvent me) {
		dispatchEvent(me);
	}

	public void mousePressed(final MouseEvent me) {
		dispatchEvent(me);
	}

	public void mouseReleased(final MouseEvent me) {
		dispatchEvent(me);
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				final JPanel proxyPanel = new JPanel(new MigLayout("fill"));
				proxyPanel.add(new JLabel("Proxy Host: "));
				proxyPanel.add(new JTextField("proxy.xyz.com"));
				proxyPanel.add(new JLabel("  Proxy Port"));
				proxyPanel.add(new JTextField("8080"));
				final JCheckBox checkBox = new JCheckBox("Use Proxy", true);
				checkBox.setFocusPainted(false);
				ComponentTitledBorder componentBorder = new ComponentTitledBorder(checkBox, proxyPanel, BorderFactory
						.createLineBorder(Color.BLACK));
				checkBox.addActionListener(new ActionListener() {
					public void actionPerformed(final ActionEvent e) {
						boolean enable = checkBox.isSelected();
						Component comp[] = proxyPanel.getComponents();
						for (int i = 0; i < comp.length; i++) {
							comp[i].setEnabled(enable);
						}
					}
				});
				proxyPanel.setBorder(componentBorder);

				JFrame frame = new JFrame();
				frame.getContentPane().add(proxyPanel);
				frame.pack();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}

	public void setOffset(final int offset) {
		this.offset = offset;
	}
}
