package com.sicpa.standard.gui.demo.layout.miglayout;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

public class MigLayoutDemoFrame extends JFrame {

	private JPanel panel;

	public MigLayoutDemoFrame() {
		initGUI();
	}

	private void initGUI() {

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getPanel(), BorderLayout.CENTER);
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MigLayoutDemoFrame test = new MigLayoutDemoFrame();
				test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				test.setSize(800, 600);
				test.setVisible(true);
			}
		});
	}

	public JPanel getPanel() {
		if (this.panel == null) {
			this.panel = new JPanel();

			// growprio 0 for first column => the first column won t grow
			this.panel.setLayout(new MigLayout("fill,debug", "", ""));

			// 1st line
			this.panel.add(new JLabel("First name"));
			this.panel.add(new JTextField(), "growx,pushx");

			this.panel.add(new JLabel("Surname"));
			this.panel.add(new JTextField(), "wrap,growx,pushx");

			// -----2nd line

			this.panel.add(new JLabel("Email"));
			this.panel.add(new JTextField(), "growx");

			this.panel.add(new JLabel("Country"));
			this.panel.add(new JTextField(), "growx,wrap");

			// --------3rd line

			this.panel.add(new JLabel("Address"));
			this.panel.add(new JScrollPane(new JTextArea()), "spanx, grow, h 15%,wrap");

			// 4th line
			this.panel.add(new JButton("ok"), "span, split 2 ,pushy ,top,right,sizegroup button");
			this.panel.add(new JButton("Cancel"), "top ,sizegroup button,wrap");

			// ----- docking
			this.panel.add(new JButton("Miglayout.north"), "north, top , gap 5 5 5 5");
			this.panel.add(new JButton("Miglayout.south"), "south");

			this.panel.add(new JButton("Miglayout.east"), "east,gap left 15, gap top 50 , gap bottom 25");
			this.panel.add(new JButton("Miglayout.south"), "south");

			this.panel.add(new JButton("Miglayout.east"), "east");
			this.panel.add(new JButton("Miglayout.north"), "north");

		}
		return this.panel;
	}
}
