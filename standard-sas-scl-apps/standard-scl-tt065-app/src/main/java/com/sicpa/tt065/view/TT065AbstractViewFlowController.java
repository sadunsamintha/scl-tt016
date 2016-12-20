package com.sicpa.tt065.view;

import com.sicpa.standard.sasscl.view.AbstractViewFlowController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Overwritting AbstractViewFlowController to manage the overwritting MainFrameController to the TT065 Profile
 *
 * @author mjimenez
 *
 */
public abstract class TT065AbstractViewFlowController extends AbstractViewFlowController {

	private static Logger logger = LoggerFactory.getLogger(TT065AbstractViewFlowController.class);
	protected TT065MainFrameController viewController;
	private JOptionPane messagePanel;

	protected void displayView() {
		try {
			viewController.replaceMainPanel(getComponent());
		} catch (java.awt.HeadlessException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void setViewController(TT065MainFrameController viewController) {
		this.viewController = viewController;
	}

	public void showMessageDialog(String message){
		messagePanel.showMessageDialog(null, message);
	}
}
