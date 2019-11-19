package com.sicpa.standard.gui.demo.fx;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.animation.transitions.ScreenTransition;
import org.jdesktop.animation.transitions.TransitionTarget;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class ScreenTransitionDemo extends javax.swing.JFrame implements TransitionTarget {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				ScreenTransitionDemo inst = new ScreenTransitionDemo();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private JButton buttonNext;
	private JPanel panel1;
	private JPanel panel2;

	public ScreenTransitionDemo() {
		super();
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill"));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		getContentPane().add(getPanel1(), "grow,push");
		getPanel2();

		getContentPane().add(getButtonNext(), "south");

		pack();
		setSize(400, 300);
	}

	public JButton getButtonNext() {
		if (this.buttonNext == null) {
			this.buttonNext = new JButton("Next");
			this.buttonNext.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonNextActionPerformed();
				}
			});
		}
		return this.buttonNext;
	}

	private void buttonNextActionPerformed() {
		ScreenTransition st = new ScreenTransition((JPanel) getContentPane(), this, 2000);
		st.start();
	}

	@Override
	public void setupNextScreen() {
		if (getPanel1().isShowing()) {
			getContentPane().remove(getPanel1());
			getContentPane().add(getPanel2(), "grow,push");
		} else {
			getContentPane().remove(getPanel2());
			getContentPane().add(getPanel1(), "grow,push");
		}
	}

	public JPanel getPanel1() {
		if (this.panel1 == null) {
			this.panel1 = new JPanel(new MigLayout("fill"));
			this.panel1.add(new JTree(), "grow,push");
			this.panel1.setOpaque(true);
			this.panel1.setBackground(Color.RED);
		}
		return this.panel1;
	}

	public JPanel getPanel2() {
		if (this.panel2 == null) {
			this.panel2 = new JPanel(new MigLayout("fill"));
			this.panel2.setOpaque(true);
			this.panel2.setBackground(Color.BLUE);
		}
		return this.panel2;
	}
}
