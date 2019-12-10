package com.sicpa.standard.gui.screen.machine.impl.SPL;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.listener.Draggable;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class EmptySplPreview extends AbstractSplFrame {

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				PreviewSpl frame = new PreviewSpl();
				frame.setVisible(true);
				frame.setSize(1024, 768);
			}
		});
	}

	public EmptySplPreview() {
		super(new SplViewController());

		Draggable.makeDraggable(this);
		initGUI();
	}

	@Override
	protected JPanel[] getConfigPanels() {
		return new JPanel[] {};
	}

	@Override
	protected String[] getConfigPanelsTitle() {
		return new String[] {};
	}

	boolean running = false;

	@Override
	protected AbstractAction getStartAction() {

		return null;
	}

	@Override
	protected AbstractAction getStopAction() {
		return null;
	}
}
