package com.sicpa.standard.sasscl.devices.barcode.simulator;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.ColorTintFilter;
import org.jdesktop.swingx.painter.AbstractPainter;

import com.jhlabs.image.ChromeFilter;
import com.jhlabs.image.ShadowFilter;
import com.sicpa.standard.gui.components.dialog.dropShadow.DialogWithDropShadow;
import com.sicpa.standard.gui.listener.Draggable;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;

public class BarcodeReaderSimulatorScreen extends DialogWithDropShadow {

	private static final long serialVersionUID = 413934676410491800L;

	public BarcodeReaderSimulatorScreen() {
		super(null);
		setTitle("Barcode Simulator");
		setModal(false);
		setAlwaysOnTop(true);
		Draggable.makeDraggable(this);
		initGUI();
	}

	protected JTextField textReply;
	protected JTextField textBarcode;
	protected JButton buttonSend;
	protected JXLabel labelTitle;

	protected void initGUI() {
		setIconImage(getIconBarcode());
		getContentPane().setLayout(new MigLayout("fill"));
		getContentPane().add(getLabelTitle(), "grow,span");
		getContentPane().add(getTextBarcode(), "growx,pushx,wrap");
		getContentPane().add(getButtonSend(), "wrap");

		SicpaLookAndFeel.flagAsHeaderOrFooter(getTextBarcode());
		getContentPane().setBackground(SicpaColor.BLUE_DARK);

		setSize(450, 200);
	}

	public JTextField getTextReply() {
		if (this.textReply == null) {
			this.textReply = new JTextField();
			this.textReply.setEnabled(false);
		}
		return this.textReply;
	}

	public JButton getButtonSend() {
		if (this.buttonSend == null) {
			this.buttonSend = new JButton("Send");
		}
		return this.buttonSend;
	}

	public JTextField getTextBarcode() {
		if (this.textBarcode == null) {
			this.textBarcode = new JTextField();
			this.textBarcode.setText("001");
			this.textBarcode.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					getButtonSend().doClick(1);
				}
			});
		}
		return this.textBarcode;
	}

	@SuppressWarnings("unchecked")
	public JXLabel getLabelTitle() {
		if (this.labelTitle == null) {
			this.labelTitle = new JXLabel("Barcode Simulator");
			this.labelTitle.setFont(this.labelTitle.getFont().deriveFont(30f).deriveFont(Font.BOLD));
			AbstractPainter<JXLabel> fg = (AbstractPainter<JXLabel>) this.labelTitle.getForegroundPainter();
			fg.setFilters(new ChromeFilter(), new ColorTintFilter(SicpaColor.BLUE_MEDIUM, 0.5f), new ShadowFilter(5, 5,
					-2, .7f));
		}
		return this.labelTitle;
	}

	protected BufferedImage getIconBarcode() {
		int w = 20;
		int h = 15;
		BufferedImage img = GraphicsUtilities.createCompatibleImage(w, h);
		Graphics2D g = img.createGraphics();
		int test;
		Random r = new Random();
		for (int i = 0; i < w; i++) {
			test = r.nextInt(2);
			if (test == 0) {
				g.setColor(Color.BLACK);
			} else {
				g.setColor(Color.WHITE);
			}
			g.drawLine(i, 0, i, h);
		}
		g.dispose();

		return img;
	}
}