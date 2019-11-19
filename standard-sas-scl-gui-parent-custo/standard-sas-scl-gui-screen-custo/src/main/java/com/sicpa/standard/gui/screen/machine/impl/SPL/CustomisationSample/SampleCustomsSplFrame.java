package com.sicpa.standard.gui.screen.machine.impl.SPL.CustomisationSample;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.impl.SPL.AbstractSplFrame;

public class SampleCustomsSplFrame extends AbstractSplFrame {

	@Override
	public SampleCustomViewController getController() {
		return (SampleCustomViewController) super.getController();
	}
	
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();

				SampleCustomsSplFrame.mapPanelClasses.put(SampleCustomsSplFrame.KEY_STATISTICS_PANEL,
						SampleCustomStatsPanel.class);

				SampleCustomsSplFrame frame = new SampleCustomsSplFrame();
				frame.getController().addPrinterWarning(50);
				frame.getController().setValidCodes(10);
				frame.getController().setInvalidCodes(5);
								
				frame.setVisible(true);
			}
		});
	}

	public SampleCustomsSplFrame() {
		super(new SampleCustomViewController());
		initGUI();
		initController();
	}

	@Override
	protected JPanel[] getConfigPanels() {
		return new JPanel[] {};
	}

	@Override
	protected String[] getConfigPanelsTitle() {
		return new String[] {};
	}

	@Override
	protected AbstractAction getStartAction() {
		return null;
	}

	@Override
	protected AbstractAction getStopAction() {
		return null;
	}
}
