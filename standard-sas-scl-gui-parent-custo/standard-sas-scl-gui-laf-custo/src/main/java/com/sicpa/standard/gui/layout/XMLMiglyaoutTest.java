package com.sicpa.standard.gui.layout;

import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class XMLMiglyaoutTest extends JFrame {

	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				new XMLMiglyaoutTest().setVisible(true);
			}
		});
	}

	public XMLMiglyaoutTest() {
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {
			XMLMiglayout layout = new XMLMiglayout(getContentPane(), new File("C:/temp/testXMLmiglayout.xml").toURI()
					.toURL(), this);
			layout.applyLayout();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		setSize(800, 600);
	}

	private JButton b1;
	private JButton b2;

	public JButton getB1() {
		if (this.b1 == null) {
			this.b1 = new JButton("1111");
		}
		return this.b1;
	}

	public JButton getB2() {
		if (this.b2 == null) {
			this.b2 = new JButton("2222");
		}
		return this.b2;
	};

	public static JButton getB3() {

		return new JButton("33333");
	}
}
