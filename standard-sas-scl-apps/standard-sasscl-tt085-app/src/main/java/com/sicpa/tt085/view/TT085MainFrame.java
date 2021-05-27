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

import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.client.common.messages.MessageEvent;
import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.messages.MessageEventKey;
import com.sicpa.standard.sasscl.productionParameterSelection.selectionmodel.DefaultSelectionModel;
import com.sicpa.standard.sasscl.provider.impl.SkuListProvider;
import com.sicpa.standard.sasscl.security.SasSclPermission;
import com.sicpa.standard.sasscl.view.MainFrame;
import com.sicpa.standard.sasscl.view.MainFrameController;
import com.sicpa.tt085.productionParameterSelection.selectionmodel.TT085DefaultSelectionModel;

import net.miginfocom.swing.MigLayout;

import static com.sicpa.standard.client.common.security.SecurityService.hasPermission;

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

	@Override
	protected void userChanged() {

		this.dataSelectionModel = new TT085DefaultSelectionModel(skuListProvider.get());
		Permission p = dataSelectionModel.getPermissions().get(getMainFrameController().getProductionMode());
		if(p!=null && !hasPermission(p)) {
			if (skuListProvider.get() == null) {
				EventBusService.post(new MessageEvent(MessageEventKey.ProductionParameters.NONE_AVAILABLE));
			} else {
				flowControl.notifyEnterSelectionScreen();
			}
		}

		resetAndRebuildAccessiblePanel();

		// set the button visible according to the user credentials
		changeSelectionView.setVisible(hasPermission(SasSclPermission.PRODUCTION_CHANGE_PARAMETERS));
		exitView.setVisible(hasPermission(SasSclPermission.EXIT));
		snapshotView.setVisible(hasPermission(SasSclPermission.SCREENSHOT));
		startStopView.setVisible(hasPermission(SasSclPermission.PRODUCTION_START)
				&& hasPermission(SasSclPermission.PRODUCTION_STOP));
	}

}
