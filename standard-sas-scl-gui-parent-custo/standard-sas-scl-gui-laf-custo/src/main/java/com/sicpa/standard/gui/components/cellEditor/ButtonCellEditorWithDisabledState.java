package com.sicpa.standard.gui.components.cellEditor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.CellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;

import com.sicpa.standard.gui.plaf.SicpaSkin;

public abstract class ButtonCellEditorWithDisabledState extends SubstanceDefaultTableCellRenderer implements CellEditor,
		TableCellEditor, TableCellRenderer {
	private static final long serialVersionUID = 1L;
	protected JPanel panelEditor;
	protected JPanel panelRenderer;
	protected JPanel panelRenderer2;
	protected JButton editor;
	protected JButton rendererEnabled;
	protected JButton rendererDisabled;
	private int currentRow;
	private JTable currentTable;

	public ButtonCellEditorWithDisabledState() {
		editor = new JButton("editor");
		panelEditor = new JPanel(new MigLayout("fill,inset 0 0 0 0"));
		panelEditor.add(editor, "grow,w 80%! , h 80%! , center");
		editor.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				buttonActionPerformed(currentRow, currentTable);
				stopCellEditing();
			}
		});
		rendererEnabled = createEnabledButtonRenderer();
		rendererDisabled = createDisabledButtonRenderer();

		panelRenderer = new JPanel(new MigLayout("fill,inset 0 0 0 0,"));
		panelRenderer.add(rendererEnabled, "grow, w 80%! , h 80%!  ,center");

		panelRenderer2 = new JPanel(new MigLayout("fill,inset 0 0 0 0,"));
		panelRenderer2.add(rendererDisabled, "grow, w 80%! , h 80%!  ,center");

		// workaround the render doesn't show the correct foreground color
		if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			Color fg = SubstanceColorSchemeUtilities.getColorScheme(this.rendererEnabled, ComponentState.SELECTED)
					.getForegroundColor();
			rendererEnabled.setForeground(fg);
			rendererDisabled.setForeground(fg);
		}
	}

	protected JButton createEnabledButtonRenderer() {
		return new JButton("renderer enabled");
	}

	protected JButton createDisabledButtonRenderer() {
		JButton button = new JButton("renderer disabled");
		button.setEnabled(false);
		return button;
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
			final int row, final int column) {
		currentRow = row;
		currentTable = table;
		prepareButton(editor, table, value, row, column);
		prepareRenderer(panelEditor, table, row, isSelected);
		return panelEditor;
	}

	@Override
	public Object getCellEditorValue() {
		return "";
	}

	protected abstract void buttonActionPerformed(int row, JTable table);

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		if (isEditorEnable(table, value, row, column)) {
			prepareButton(rendererEnabled, table, value, row, column);
			prepareRenderer(panelRenderer, table, row, isSelected);
			return panelRenderer;
		} else {
			prepareButton(rendererDisabled, table, value, row, column);
			prepareRenderer(panelRenderer2, table, row, isSelected);
			return panelRenderer2;
		}

	}

	protected boolean isEditorEnable(final JTable table, final Object value, final int row, final int column) {
		return table.getModel().isCellEditable(row, column);
	}

	/**
	 * @return the text on the button
	 * */
	protected abstract String getText(JTable table, int row);

	protected void prepareButton(final JButton button, final JTable table, final Object value, final int row,
			final int column) {
		button.setText(getText(table, row));
	}

	/**
	 * do the row stripping
	 */
	private static void prepareRenderer(final Component comp, final JTable table, final int row,
			final boolean isSelected) {
		SubstanceColorScheme cs = SubstanceColorSchemeUtilities.getColorScheme(table,
				SicpaSkin.TABLE_DECORATION_AREA_TYPE, ComponentState.DEFAULT);
		if (isSelected) {
			comp.setForeground(cs.getExtraLightColor());
		} else {
			comp.setForeground(cs.getMidColor());
		}
		comp.setFont(table.getFont());

		if (row % 2 == 0) {
			comp.setBackground(cs.getLightColor());
		} else {
			comp.setBackground(cs.getDarkColor());
		}
	}

	// ------------------- code pasted from abstract cell editor
	// -------------------- can't extends abstractCellEditor because to have the
	// highlight effect the renderer must
	// extend substanceDefaultCellRenderer
	// protected EventListenerList listenerList = new EventListenerList();
	transient protected ChangeEvent changeEvent = null;

	// Force this to be implemented.
	// public Object getCellEditorValue()

	/**
	 * Returns true.
	 * 
	 * @param e
	 *            an event object
	 * @return true
	 */
	public boolean isCellEditable(final EventObject e) {
		return true;
	}

	/**
	 * Returns true.
	 * 
	 * @param anEvent
	 *            an event object
	 * @return true
	 */
	public boolean shouldSelectCell(final EventObject anEvent) {
		return true;
	}

	/**
	 * Calls <code>fireEditingStopped</code> and returns true.
	 * 
	 * @return true
	 */
	public boolean stopCellEditing() {
		fireEditingStopped();
		return true;
	}

	/**
	 * Calls <code>fireEditingCanceled</code>.
	 */
	public void cancelCellEditing() {
		fireEditingCanceled();
	}

	/**
	 * Adds a <code>CellEditorListener</code> to the listener list.
	 * 
	 * @param l
	 *            the new listener to be added
	 */
	public void addCellEditorListener(final CellEditorListener l) {
		this.listenerList.add(CellEditorListener.class, l);
	}

	/**
	 * Removes a <code>CellEditorListener</code> from the listener list.
	 * 
	 * @param l
	 *            the listener to be removed
	 */
	public void removeCellEditorListener(final CellEditorListener l) {
		this.listenerList.remove(CellEditorListener.class, l);
	}

	/**
	 * Returns an array of all the <code>CellEditorListener</code>s added to this AbstractCellEditor with
	 * addCellEditorListener().
	 * 
	 * @return all of the <code>CellEditorListener</code>s added or an empty array if no listeners have been added
	 * @since 1.4
	 */
	public CellEditorListener[] getCellEditorListeners() {
		return (CellEditorListener[]) this.listenerList.getListeners(CellEditorListener.class);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type. The event instance is
	 * created lazily.
	 * 
	 * @see EventListenerList
	 */
	protected void fireEditingStopped() {
		// Guaranteed to return a non-null array
		Object[] listeners = this.listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CellEditorListener.class) {
				// Lazily create the event:
				if (this.changeEvent == null)
					this.changeEvent = new ChangeEvent(this);
				((CellEditorListener) listeners[i + 1]).editingStopped(this.changeEvent);
			}
		}
	}

	/**
	 * Notifies all listeners that have registered interest for notification on this event type. The event instance is
	 * created lazily.
	 * 
	 * @see EventListenerList
	 */
	protected void fireEditingCanceled() {
		// Guaranteed to return a non-null array
		Object[] listeners = this.listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CellEditorListener.class) {
				// Lazily create the event:
				if (this.changeEvent == null)
					this.changeEvent = new ChangeEvent(this);
				((CellEditorListener) listeners[i + 1]).editingCanceled(this.changeEvent);
			}
		}
	}
}
