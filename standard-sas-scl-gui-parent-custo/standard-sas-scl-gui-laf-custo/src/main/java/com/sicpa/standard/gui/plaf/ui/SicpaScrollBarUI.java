package com.sicpa.standard.gui.plaf.ui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.internal.ui.SubstanceScrollBarUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class SicpaScrollBarUI extends SubstanceScrollBarUI {

	public static final String UNIT_INCREMENT_AUTO = "com.sicpa.standard.gui.plaf.ui.SicpaScrollBarUI.UNIT_INCREMENT_AUTO";

	// private int animDuration;
	// private float animProgress;

	private JScrollBar scrollbar;

	// private Animator animator;

	public SicpaScrollBarUI(final JComponent comp) {
		super(comp);
		// animDuration = 400;
		// this.animProgress = 0;
		this.scrollbar = (JScrollBar) comp;
	}

	public static ComponentUI createUI(final JComponent c) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(c);
		return new SicpaScrollBarUI(c);
	}

	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);
		c.setOpaque(false);
	}

	// public void setAnimProgress(final float animProgress)
	// {
	// this.animProgress = animProgress;
	// this.scrollbar.repaint();
	// }

	@Override
	protected void installListeners() {
		super.installListeners();
		this.scrollbar.getModel().addChangeListener(this.unitIncrementSetter);
	}

	// public float getAnimProgress()
	// {
	// return this.animProgress;
	// }

	private ChangeListener unitIncrementSetter = new ChangeListener() {
		@Override
		public void stateChanged(final ChangeEvent evt) {
			boolean auto = true;
			boolean clientPropSet = false;
			Object o = SicpaScrollBarUI.this.scrollbar.getClientProperty(UNIT_INCREMENT_AUTO);
			if (o != null) {
				if (o instanceof Boolean) {
					auto = (Boolean) o;
					clientPropSet = true;
				}
			}
			if (!clientPropSet) {
				o = UIManager.get(UNIT_INCREMENT_AUTO);
				if (o != null) {
					if (o instanceof Boolean) {
						auto = (Boolean) o;
					}
				}
			}
			if (auto) {
				int unit = SicpaScrollBarUI.this.scrollbar.getMaximum() / 40;
				SicpaScrollBarUI.this.scrollbar.setUnitIncrement(unit);
				SicpaScrollBarUI.this.scrollbar.setBlockIncrement(unit);
			}
		}
	};

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(500, 500);
				JScrollBar s = new JScrollBar();
				s.setMaximum(20);
				f.getContentPane().add(s, BorderLayout.EAST);

				s = new JScrollBar(JScrollBar.HORIZONTAL);
				s.setMaximum(20);
				f.getContentPane().add(s, BorderLayout.SOUTH);
				f.setVisible(true);
			}
		});
	}
}
