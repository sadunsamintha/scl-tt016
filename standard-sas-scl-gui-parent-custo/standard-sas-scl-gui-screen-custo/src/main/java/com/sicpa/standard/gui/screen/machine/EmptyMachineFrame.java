package com.sicpa.standard.gui.screen.machine;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class EmptyMachineFrame extends AbstractMachineFrame {

	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				EmptyMachineFrame frame = new EmptyMachineFrame();
				frame.setVisible(true);
				frame.getController().addError("E-1", "error message");
				frame.getController().addWarning("W-1", "warning message",false);
				frame.getController().addErrorMainPanel("key", "Camera disconnected");
			}
		});
	}

	public EmptyMachineFrame() {
		super(new MachineViewController());
		initGUI();
		getConfigPanel().getExitButton().setVisible(false);
	}

	@Override
	protected void buildFooter() {
		addFillerToFooter();
		// addToFooter(getSelectProductButton());
	}

	@Override
	protected boolean isConfigCancellable(final int index) {
		return false;
	}

	@Override
	protected boolean isConfigValidateable(final int index) {
		return false;
	}

	@Override
	protected void buildLayeredLeftPanel() {
	}

	@Override
	protected void buildRightPanel() {

	}

	@Override
	protected JPanel[] getConfigPanels() {

		return new JPanel[] { new JPanel(), new JPanel(), new JPanel(), new JPanel(), new JPanel(), new JPanel(),
				new JPanel(), new JPanel(), new JPanel() };
	}

	@Override
	protected String[] getConfigPanelsTitle() {
		return new String[] { "", "", "", "", "", "", "", "", "", };
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
