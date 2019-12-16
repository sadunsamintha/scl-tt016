package com.sicpa.standard.gui.plaf.ui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

import org.pushingpixels.substance.internal.ui.SubstanceOptionPaneUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class SicpaOptionPaneUI extends SubstanceOptionPaneUI {

	public static final String CHAR_BY_LINE = "com.sicpa.standard.gui.plaf.ui.SicpaOptionPaneUI.charByLine";
	public static final String MIN_SIZE = "com.sicpa.standard.gui.plaf.ui.SicpaOptionPaneUI.minSize";
	public static final String SIZE = "com.sicpa.standard.gui.plaf.ui.SicpaOptionPaneUI.size";
	public static final String WIDTH = "com.sicpa.standard.gui.plaf.ui.SicpaOptionPaneUI.width";

	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaOptionPaneUI();
	}

	public SicpaOptionPaneUI() {
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
	}

	@Override
	protected void installComponents() {
		super.installComponents();
		flag(this.optionPane);
	}

	private void flag(final JComponent cont) {
		SicpaLookAndFeelCusto.flagAsDefaultArea(cont);
		for (Component comp : cont.getComponents()) {
			if (comp instanceof JPanel) {
				flag((JComponent) comp);
			}
		}
	}

	@Override
	protected int getMaxCharactersPerLineCount() {
		int max = UIManager.getInt(CHAR_BY_LINE);
		if (max <= 0) {
			max = 50;
		}

		return max;
	}

	@Override
	public Dimension getMinimumOptionPaneSize() {
		Dimension min = UIManager.getDimension(MIN_SIZE);
		if (min != null) {
			return min;
		}
		return super.getMinimumOptionPaneSize();
	}

	@Override
	public Dimension getPreferredSize(final JComponent c) {

		int w = UIManager.getInt(WIDTH);
		if (w > 0) {
			Dimension d = super.getPreferredSize(c);
			d.width = w;
			return d;
		}

		Dimension pref = UIManager.getDimension(SIZE);
		if (pref != null) {
			return pref;
		}
		return super.getPreferredSize(c);
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				UIManager.put(CHAR_BY_LINE, 25);
				// UIManager.put(SicpaOptionPaneUI.MIN_SIZE, new Dimension(0, 500));
				// UIManager.put(SicpaOptionPaneUI.SIZE, new Dimension(180, 250));
				// UIManager.put(SicpaOptionPaneUI.WIDTH, 55);
				SicpaLookAndFeelCusto.setFontSize(10);
				String msg = "mmmmm mmm mmmmmm mmm mmmm mm mmmmmm mmmmmmm mm mmm mmmmmm mmmm mmmmm mmmmm mm mmmmmmmm mmmmmmmm mmmmmm mmmm mmmm mmm";
				JOptionPane.showMessageDialog(null, msg);
			}
		});

	}
}
