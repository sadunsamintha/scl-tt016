package com.sicpa.standard.gui.demo.components.sicpa;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.jxlayer.JXLayer;

import com.sicpa.standard.gui.components.layeredComponents.NotifiableComponent;

public class NotifiablecomponentDemoFrame extends javax.swing.JFrame {

	private JButton alert;
	private JXLayer<JComponent> tree;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				NotifiablecomponentDemoFrame inst = new NotifiablecomponentDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public NotifiablecomponentDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(getTree(), BorderLayout.CENTER);
			getContentPane().add(getAlert(), BorderLayout.SOUTH);

			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			setSize(400, 300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JXLayer<JComponent> getTree() {
		if (this.tree == null) {
			JTree t = new JTree();
			this.tree = NotifiableComponent.getComponentWithNotification(t);
		}
		return this.tree;
	}

	public JButton getAlert() {
		if (this.alert == null) {
			this.alert = new JButton("alert");
			this.alert.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					alertActionPerformed();
				}
			});

		}
		return this.alert;
	}

	private void alertActionPerformed() {
		NotifiableComponent.alert(this.tree);
	}
}
