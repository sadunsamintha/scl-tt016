package com.sicpa.standard.gui.components.renderers;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JList;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultComboBoxRenderer;

import com.sicpa.standard.gui.utils.ReflectionUtils;

public class ComboBoxFieldReaderRenderer extends SubstanceDefaultComboBoxRenderer {

	private static final long serialVersionUID = 1L;
	private String fieldToRead;

	private ReflectionUtils reflUtils;

	public ComboBoxFieldReaderRenderer(final JComboBox combo, final String fieldToRead) {
		super(combo);
		this.fieldToRead = fieldToRead;
		this.reflUtils = new ReflectionUtils();
	}

	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
			final boolean isSelected, final boolean cellHasFocus) {
		try {
			return super.getListCellRendererComponent(list, (String) this.reflUtils.getValue(this.fieldToRead, value),
					index, isSelected, cellHasFocus);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
