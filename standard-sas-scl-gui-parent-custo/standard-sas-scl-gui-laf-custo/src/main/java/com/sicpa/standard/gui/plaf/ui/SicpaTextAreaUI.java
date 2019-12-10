package com.sicpa.standard.gui.plaf.ui;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;

import org.pushingpixels.substance.internal.ui.SubstanceTextAreaUI;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceSizeUtils;

import com.sicpa.standard.gui.components.border.SicpaTextComponentBorder;
import com.sicpa.standard.gui.plaf.ui.utils.SicpaTextPaintUtils;

public class SicpaTextAreaUI extends SubstanceTextAreaUI {
	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaTextAreaUI(comp);
	}

	public SicpaTextAreaUI(final JComponent comp) {
		super(comp);
	}

	@Override
	protected void paintBackground(final Graphics g) {
		SicpaTextPaintUtils.paintTextFieldBackground(g, this.textArea);
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		Border b = this.textArea.getBorder();

		SicpaTextComponentBorder border = new SicpaTextComponentBorder(SubstanceSizeUtils
				.getTextBorderInsets(SubstanceSizeUtils.getComponentFontSize(this.textArea)));
		if (b == null || b instanceof UIResource) {
			Border newB = new BorderUIResource.CompoundBorderUIResource(border, new BasicBorders.MarginBorder());

			this.textArea.setBorder(newB);
		}
	}

}
