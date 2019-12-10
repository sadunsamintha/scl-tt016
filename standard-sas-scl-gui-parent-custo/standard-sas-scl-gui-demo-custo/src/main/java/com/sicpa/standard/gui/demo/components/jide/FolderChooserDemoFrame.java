package com.sicpa.standard.gui.demo.components.jide;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.jidesoft.swing.FolderChooser;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class FolderChooserDemoFrame extends javax.swing.JFrame {

	private JButton openButton;
	private FolderChooser folderChooser;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				FolderChooserDemoFrame inst = new FolderChooserDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public FolderChooserDemoFrame() {
		super();
		initGUI();
		this.folderChooser = new FolderChooser();

		ArrayList<String> recentFiles = new ArrayList<String>();
		recentFiles.add("c:/");
		recentFiles.add("c:/Program Files");
		recentFiles.add("c:/Documents and Settings");
		this.folderChooser.setRecentList(recentFiles);
	}

	private void initGUI() {
		SicpaLookAndFeelCusto.install();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new FlowLayout());
		getContentPane().add(getOpenButton());
		setSize(400, 300);
	}

	public JButton getOpenButton() {
		if (this.openButton == null) {
			this.openButton = new JButton("Browse...");
			this.openButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					FolderChooserDemoFrame.this.folderChooser.showOpenDialog(FolderChooserDemoFrame.this);

				}
			});
		}
		return this.openButton;
	}

}
