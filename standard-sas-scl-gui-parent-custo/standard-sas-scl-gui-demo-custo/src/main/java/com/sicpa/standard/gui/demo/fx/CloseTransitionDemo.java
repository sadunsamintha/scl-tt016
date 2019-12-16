package com.sicpa.standard.gui.demo.fx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.transition.CloseTransition;
import com.sicpa.standard.gui.components.transition.impl.CircleTransition;
import com.sicpa.standard.gui.components.transition.impl.FadeOutTransition;
import com.sicpa.standard.gui.components.transition.impl.PinchTransition;
import com.sicpa.standard.gui.components.transition.impl.RectanglesTransition;
import com.sicpa.standard.gui.components.transition.impl.RotateTransition;
import com.sicpa.standard.gui.components.transition.impl.ShrinkTransition;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;

@SuppressWarnings("serial")
public class CloseTransitionDemo extends JFrame {

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				SicpaLookAndFeelCusto.install();

				final CloseTransitionDemo inst = new CloseTransitionDemo();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private JFrame frameTransition;

	public CloseTransitionDemo() {
		super();
		setTitle("close transition demo");
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		getContentPane().setLayout(new MigLayout("fill,wrap 3"));
		JButton b = new JButton("RectanglesTransition");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				startCloseTransition(new RectanglesTransition(getFrameTransition()));
			}
		});
		getContentPane().add(b, "growx");

		b = new JButton("CircleTransition");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				startCloseTransition(new CircleTransition(getFrameTransition()));
			}
		});
		getContentPane().add(b, "growx");

		b = new JButton("FadeOutTransition");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				startCloseTransition(new FadeOutTransition(getFrameTransition()));
			}
		});
		getContentPane().add(b, "growx");

		b = new JButton("PinchTransition");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				startCloseTransition(new PinchTransition(getFrameTransition()));
			}
		});
		getContentPane().add(b, "growx");

		b = new JButton("RotateTransition");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				startCloseTransition(new RotateTransition(getFrameTransition()));
			}
		});
		getContentPane().add(b, "growx");

		b = new JButton("ShrinkTransition");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				startCloseTransition(new ShrinkTransition(getFrameTransition()));
			}
		});
		getContentPane().add(b, "growx");
		setSize(550, 300);
	}

	public JFrame getFrameTransition() {
		if (this.frameTransition == null) {
			this.frameTransition = new JFrame();
			this.frameTransition.getContentPane().setLayout(new MigLayout("fill"));

			this.frameTransition.getContentPane().add(new JScrollPane(new JTree()), "grow");
			this.frameTransition.getContentPane().add(new JScrollPane(new JTextArea()), "grow,wrap");
			this.frameTransition.getContentPane().add(new JLabel("field 1"), "split 2");
			this.frameTransition.getContentPane().add(new JTextField(), "growx,wrap");
			this.frameTransition.getContentPane().add(new JLabel("field 2"), "split 2");
			this.frameTransition.getContentPane().add(new JTextField(), "growx");
			this.frameTransition.getContentPane().add(new JButton("Cancel"), "right,split 2");
			this.frameTransition.getContentPane().add(new JButton("OK"), "");
			this.frameTransition.setBounds(100, 100, 800, 650);
		}
		return this.frameTransition;
	}

	private void startCloseTransition(final CloseTransition transition) {

		transition.setAnimDuration(500);
		transition.setEndAction(new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
			}
		});
		this.frameTransition.setVisible(true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				ThreadUtils.sleepQuietly(500);
				transition.startCloseTransition();
			}
		}).start();
	}
}
