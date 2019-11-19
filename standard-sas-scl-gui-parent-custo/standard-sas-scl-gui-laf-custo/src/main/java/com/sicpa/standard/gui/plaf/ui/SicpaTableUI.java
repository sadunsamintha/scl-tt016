package com.sicpa.standard.gui.plaf.ui;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.internal.ui.SubstanceTableUI;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;

import com.sicpa.standard.gui.components.renderers.SicpaTableCellRenderer;
import com.sicpa.standard.gui.components.renderers.SicpaTableCellRenderer.BooleanRenderer;
import com.sicpa.standard.gui.plaf.SicpaSkin;

public class SicpaTableUI extends SubstanceTableUI
{
	public static ComponentUI createUI(final JComponent comp)
	{
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaTableUI();
	}

	@Override
	protected void installDefaults()
	{
		super.installDefaults();
		
		this.installRenderer(Object.class, new SicpaTableCellRenderer());
//		 this.installRendererIfNecessary(Icon.class, new SicpaTableCellRenderer());
//		 this.installRendererIfNecessary(ImageIcon.class, new SicpaTableCellRenderer());
//		 this.installRendererIfNecessary(Number.class, new SubstanceDefaultTableCellRenderer.NumberRenderer());
//		 this.installRendererIfNecessary(Float.class, new SubstanceDefaultTableCellRenderer.DoubleRenderer());
//		 this.installRendererIfNecessary(Double.class, new SubstanceDefaultTableCellRenderer.DoubleRenderer());
//		 this.installRendererIfNecessary(Date.class, new SubstanceDefaultTableCellRenderer.DateRenderer());
//		this.installRenderer(Boolean.class, new BooleanRenderer());

		if (this.table instanceof JXTable)
		{
			SubstanceColorScheme cs = SubstanceColorSchemeUtilities.getColorScheme(this.table,SicpaSkin.TABLE_DECORATION_AREA_TYPE, ComponentState.DEFAULT);			
			((JXTable) this.table).addHighlighter(new ColorHighlighter(HighlightPredicate.ODD, cs.getDarkColor(),cs.getMidColor(),cs.getDarkColor(),cs.getExtraLightColor()));
			((JXTable) this.table).addHighlighter(new ColorHighlighter(HighlightPredicate.EVEN, cs.getLightColor(),cs.getMidColor(),cs.getLightColor(), cs.getExtraLightColor()));
		}
		this.table.setDefaultEditor(Boolean.class, new SicpaTableCellRenderer.BooleanEditor());
		
		installRenderer(boolean.class, new BooleanRenderer());
		installRenderer(Boolean.class, new BooleanRenderer());
	}

	protected void installRenderer(final Class<?> clazz, final TableCellRenderer renderer)
	{
		this.table.setDefaultRenderer(clazz, renderer);
	}
}
