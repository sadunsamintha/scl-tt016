package com.sicpa.standard.gui.plaf.ui;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;

import org.pushingpixels.substance.internal.ui.SubstanceFormattedTextFieldUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceSizeUtils;

import com.sicpa.standard.gui.components.border.SicpaTextComponentBorder;
import com.sicpa.standard.gui.plaf.ui.utils.SicpaTextPaintUtils;

public class SicpaFormattedTextFieldUI extends SubstanceFormattedTextFieldUI {

	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaFormattedTextFieldUI(comp);
	}

	public SicpaFormattedTextFieldUI(final JComponent comp) {
		super(comp);
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		// this.textField.setForeground(Color.WHITE);
		Border b = this.textField.getBorder();
		if (b == null || b instanceof UIResource) {
			Border newB = new BorderUIResource.CompoundBorderUIResource(

			new SicpaTextComponentBorder(SubstanceSizeUtils.getTextBorderInsets(SubstanceSizeUtils
					.getComponentFontSize(this.textField))),

			new BasicBorders.MarginBorder()

			);

			this.textField.setBorder(newB);
		}
	}

	@Override
	protected void paintBackground(final Graphics g) {
		SicpaTextPaintUtils.paintTextFieldBackground(g, this.textField);
	}

	// @Override
	// protected void paintSafely(final Graphics g)
	// {
	// DecorationAreaType areaType =
	// SubstanceLookAndFeel.getDecorationType(this.textField);
	// if (this.textField.getForeground() instanceof UIResource)
	// {
	// if (areaType == SicpaSkin.WORK_DECORATION_AREA_TYPE)
	// {
	// this.textField.setForeground(SicpaColor.BLUE_DARK);
	// }
	// else
	// {
	// this.textField.setForeground(Color.WHITE);
	// }
	// }
	// super.paintSafely(g);
	// }
}
