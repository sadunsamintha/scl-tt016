package com.sicpa.tt065.view;

import com.sicpa.standard.client.common.view.mvc.IView;
import com.sicpa.standard.gui.screen.machine.AbstractMachineFrame;
import com.sicpa.standard.sasscl.view.AbstractViewFlowController;
import com.sicpa.standard.sasscl.view.MainFrame;

import javax.swing.*;
import java.awt.*;

public abstract class TT065AbstractViewFlowController extends AbstractViewFlowController {

	protected TT065MainFrameController viewController;

	protected void displayView() {
		try {
			viewController.replaceMainPanel(getComponent());
		} catch (java.awt.HeadlessException e) {
		}
	}

	public void setViewController(TT065MainFrameController viewController) {
		this.viewController = viewController;
	}

}
