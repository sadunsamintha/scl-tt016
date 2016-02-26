package com.sicpa.standard.sasscl.view.licence;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.gui.components.text.MultiLineLabel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;

@SuppressWarnings("serial")
public class LicencePanel extends JPanel {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeel.install();
				JFrame f = new JFrame();
				LicencePanel panel = new LicencePanel();
				f.getContentPane().add(panel);
				f.setVisible(true);
				f.setSize(500, 500);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
	}

	private static final Logger logger = LoggerFactory.getLogger(LicencePanel.class);

	private JDialog dialog;
	private String firstMessage = null;
	private String custoMessage = null;
	private JLabel headLabel;
	private JTextArea mainLabel;

	public LicencePanel() {
		setLayout(new MigLayout("fill"));
		setBackground(SicpaColor.BLUE_DARK);
		add(getInfoLicenseLabel(), "east");
	}

	private JLabel getInfoLicenseLabel() {

		JLabel licenseIcon = new JLabel(getLicenseIcon());
		licenseIcon.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				displayLicenceDialog();
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				displayLicenceDialog();
			}
		});
		return licenseIcon;
	}

	public ImageIcon getLicenseIcon() {
		String resourceName = "licenseIcon.png";
		ImageIcon licenceIcon = null;
		URL url = ClassLoader.getSystemResource(resourceName);
		if (url != null) {
			try {
				licenceIcon = new ImageIcon((GraphicsUtilities.loadCompatibleImage(url)));
			} catch (IOException e) {
				logger.error("", e);
			}
		}
		return licenceIcon;
	}

	private void loadLicenceFile() {
		Scanner scanner = null;
		try {
			URL url = ClassLoader.getSystemResource("license");
			scanner = new Scanner(new FileInputStream(new File(url.toURI())), "UTF-8");
		} catch (Exception e) {
			logger.error("", e);
		}

		if (scanner != null) {
			firstMessage = scanner.nextLine();
			custoMessage = scanner.nextLine();
		}
	}

	private void displayLicenceDialog() {
		loadLicenceFile();
		updateLicenceContent();
		getDialog().setVisible(true);
	}

	private void updateLicenceContent() {
		getHeadLabel().setText(firstMessage);
		getMainLabel().setText(custoMessage);
		getDialog().pack();
	}

	public JDialog getDialog() {
		if (dialog == null) {
			dialog = new JDialog();
			dialog.setLayout(new MigLayout("fill"));
			dialog.setMinimumSize(new Dimension(650, 300));
			dialog.getContentPane().add(headLabel, "wrap");
			dialog.getContentPane().add(mainLabel, "push,grow");

		}
		return dialog;
	}

	public JLabel getHeadLabel() {
		if (headLabel == null) {
			headLabel = new JLabel();
			headLabel.setForeground(SicpaColor.BLUE_DARK);
		}
		return headLabel;
	}

	public JTextArea getMainLabel() {
		if (mainLabel == null) {
			mainLabel = new MultiLineLabel();
			mainLabel.setForeground(SicpaColor.BLUE_DARK);
		}
		return mainLabel;
	}
}
