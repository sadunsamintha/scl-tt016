package com.sicpa.standard.gui.demo.components.sicpa;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.sicpa.standard.gui.components.layeredComponents.HintManager;
import com.sicpa.standard.gui.components.layeredComponents.HintManager.eViewHint;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class HintManagerDemoFrame extends javax.swing.JFrame {

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				SicpaLookAndFeelCusto.setFont(SicpaFont.getFont(20));
				SicpaLookAndFeelCusto.setSmallFont(SicpaFont.getFont(15));
				HintManagerDemoFrame inst = new HintManagerDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public HintManagerDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			setLayout(new FlowLayout());

			JScrollPane jsp = new JScrollPane(new JTree());
			jsp.setPreferredSize(new Dimension(100, 100));
			getContentPane().add(HintManager.registerHint(jsp, eViewHint.layer, "HINT MSG"));

			JButton button = new JButton("action");
			button.setPreferredSize(new Dimension(150, 25));
			getContentPane().add(HintManager.registerHint(button, eViewHint.icon, "HINT MSG"));

			button = new JButton("action");
			button.setPreferredSize(new Dimension(150, 25));
			getContentPane().add(HintManager.registerHint(button, eViewHint.layer, "HINT MSG"));

			final JTextArea jta = new JTextArea();
			jta.setPreferredSize(new Dimension(200, 200));
			getContentPane().add(HintManager.registerHint(jta, eViewHint.layer, "Your message here"));

			final JToggleButton b = new JToggleButton("toggle hint");
			getContentPane().add(b);

			b.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					HintManager.setHintVisible(b.isSelected());
					HintManager.setHintMessage("message here" + new Random().nextInt(), jta);
				}
			});

			setVisible(true);

			setSize(600, 400);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
