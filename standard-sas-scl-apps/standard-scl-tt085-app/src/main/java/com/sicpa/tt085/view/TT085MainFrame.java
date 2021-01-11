package com.sicpa.tt085.view;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.view.MainFrame;
import com.sicpa.standard.sasscl.view.MainFrameController;
import com.sicpa.standard.sasscl.view.licence.LicencePanel;

import net.miginfocom.swing.MigLayout;

public class TT085MainFrame extends MainFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TT085MainFrame(MainFrameController controller, JComponent startStopView, JComponent changeSelectionView,
			JComponent exitView, JComponent optionsView, JComponent messagesView, JComponent mainPanel,
			JComponent snapshotView, SkuListProvider skuListProvider, IFlowControl flowControl) {
		super(controller, startStopView, changeSelectionView, exitView, optionsView, messagesView, mainPanel,
				snapshotView, skuListProvider, flowControl);
	}
	
	@Override
	public JComponent getHeader() {
		if (header == null) {
			header = new JPanel(new MigLayout(",fill,inset 0 0 0 0, hidemode 3,gap 0 0 0 0"));
			header.add(getHeaderInfoPanel(), "south");
			header.add(getLineIdPanel(), "growx, wrap");
			header.add(getApplicationStatusPanel(), "grow");
			JLabel brandLabel = new JLabel("D-TRACE");
			Font font = new Font(brandLabel.getFont().getName(), Font.BOLD, 30);
			brandLabel.setFont(font);
			header.add(brandLabel, "east, growx, gapright 30");
			header.add(getConfigPasswordPanel(), "wrap,growx");
			getConfigPasswordPanel().setVisible(false);	
		}
		return header;
	}

	@Override
	public JLabel getPowerByLabel() {
		if (this.powerByLabel == null) {
			this.powerByLabel = new JLabel();
			BufferedImage img;
			try {
				URL url = getClass().getClassLoader().getResource("darphaneLogo.png");
				img = GraphicsUtilities.loadCompatibleImage(url);
			} catch (IOException e) {
				e.printStackTrace();
				img = GraphicsUtilities.createCompatibleTranslucentImage(1, 1);
			}
			this.powerByLabel.setIcon(new ImageIcon(img));
		}
		return this.powerByLabel;
	}

}
