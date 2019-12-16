package com.sicpa.standard.gui.demo.components.sicpa;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.dialog.dropShadow.DialogWithShadowAndFlip;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class FlippablePanelDemo {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				SicpaLookAndFeelCusto.install();

				final DialogWithShadowAndFlip d = new DialogWithShadowAndFlip(null);
				d.setSize(300, 300);

				JPanel p = new JPanel(new MigLayout("fill"));
				p.add(new JScrollPane(new JTree()), "span,grow");
				JButton b = new JButton("go");
				b.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						JButton button = new JButton(System.currentTimeMillis() + "");
						button.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(final ActionEvent e) {
								d.flip();
							}
						});
						d.flip(button);
					}
				});
				p.add(b);
				d.setFront(p);
				d.startShowAnim();
			}
		});
	}
}
