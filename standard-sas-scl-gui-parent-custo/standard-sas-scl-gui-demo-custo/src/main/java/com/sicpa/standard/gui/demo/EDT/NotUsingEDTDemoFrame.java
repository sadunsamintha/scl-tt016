package com.sicpa.standard.gui.demo.EDT;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class NotUsingEDTDemoFrame extends javax.swing.JFrame {

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				NotUsingEDTDemoFrame inst = new NotUsingEDTDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	private JScrollPane scroll;
	private JTextArea text;
	private JButton buttonAddOusideEDT;
	private JButton buttonAddInsideEDT;

	public NotUsingEDTDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getScroll(), BorderLayout.CENTER);
		getContentPane().add(getbuttonAddOusideEDT(), BorderLayout.SOUTH);
		getContentPane().add(getButtonAddInsideEDT(), BorderLayout.NORTH);

		setSize(400, 300);
	}

	public JTextArea getText() {
		if (this.text == null) {
			this.text = new JTextArea();
		}
		return this.text;
	}

	public JScrollPane getScroll() {
		if (this.scroll == null) {
			this.scroll = new JScrollPane(getText());
		}
		return this.scroll;
	}

	public JButton getbuttonAddOusideEDT() {
		if (this.buttonAddOusideEDT == null) {
			this.buttonAddOusideEDT = new JButton("add text OUT OF EDT");
			this.buttonAddOusideEDT.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonAddOusideEDTActionPerformed();
				}
			});
		}
		return this.buttonAddOusideEDT;
	}

	private void buttonAddOusideEDTActionPerformed() {
		new Thread(new Runnable() {// outside the EDT
					@Override
					public void run() {
						addText();
						// we can't tell where the V scrollbar will be
					}
				}).start();
	}

	public JButton getButtonAddInsideEDT() {
		if (this.buttonAddInsideEDT == null) {
			this.buttonAddInsideEDT = new JButton("add text in EDT");
			this.buttonAddInsideEDT.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonAddInsideEDTActionPerformed();
				}
			});
		}
		return this.buttonAddInsideEDT;
	}

	private void buttonAddInsideEDTActionPerformed() {// inside the EDT because it's
		// on a mouse click
		addText();
		// the V scrollbar will always be at the max value
	}

	private void addText() {
		this.text.setText("");
		Random ran = new Random();
		for (int i = 0; i < 100; i++) {
			int num = ran.nextInt();
			if (SwingUtilities.isEventDispatchThread()) {
				this.text.append("Text added INSIDE EDT");
			} else {
				this.text.append("Text added OUTSIDE EDT");
			}
			this.text.append(String.valueOf(num));
			this.text.append("\n");
		}
	}
}
