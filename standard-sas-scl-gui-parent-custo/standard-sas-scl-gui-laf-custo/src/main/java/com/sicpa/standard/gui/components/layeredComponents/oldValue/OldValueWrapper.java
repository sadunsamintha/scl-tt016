package com.sicpa.standard.gui.components.layeredComponents.oldValue;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OldValueWrapper extends JPanel {
	private static final Class[] SUPPORTED = new Class[] { JSpinner.class, JToggleButton.class, JSlider.class,
			JComboBox.class };

	private JComponent comp;
	private Object oldValue;
	private OldValueFormater formater;
	private boolean changed;

	public OldValueWrapper(final JComponent comp) {
		if (!isSupported(comp)) {
			throw new RuntimeException("the component is not supported by the  showOldValue manager");
		}
		this.comp = comp;
		this.changed = false;
		setLayout(new BorderLayout());
		add(comp, BorderLayout.CENTER);
		this.formater = new DefaultImplOldValueFormater();
		initListener();
	}

	private void initListener() {
		if (this.comp instanceof JSpinner) {
			((JSpinner) this.comp).addChangeListener(new ChangeValueListener());
		} else if (this.comp instanceof JSlider) {
			((JSlider) this.comp).addChangeListener(new ChangeValueListener());
		} else if (this.comp instanceof JToggleButton) {
			((JToggleButton) this.comp).addChangeListener(new ChangeValueListener());
		} else if (this.comp instanceof JComboBox) {
			((JComboBox) this.comp).addActionListener(new ChangeValueListener());
		}
	}

	private Object getInternalOldValue() {
		if (this.comp instanceof JSpinner) {
			return ((JSpinner) this.comp).getValue();
		} else if (this.comp instanceof JSlider) {
			return ((JSlider) this.comp).getValue();
		} else if (this.comp instanceof JToggleButton) {
			return Boolean.valueOf(((JToggleButton) this.comp).isSelected());
		} else if (this.comp instanceof JComboBox) {
			return ((JComboBox) (this.comp)).getSelectedItem();
		} else {
			return null;
		}
	}

	public void setOldValue() {
		this.oldValue = getInternalOldValue();
		this.changed = false;
	}

	private static boolean isSupported(final Object o) {
		for (Class cc : SUPPORTED) {
			if (cc.isInstance(o)) {
				return true;
			}
		}
		return false;
	}

	public Object getOldValue() {
		return this.oldValue;
	}

	public void setFormater(final OldValueFormater formater) {
		this.formater = formater;
	}

	public String getFormatedValue() {
		return this.formater.getFormatedValue(this.oldValue);
	}

	private class ChangeValueListener implements ChangeListener, ActionListener {
		@Override
		public void stateChanged(final ChangeEvent e) {
			changed();
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			changed();

		}

		private void changed() {
			// repaint the root comp to show the old value
			Component root = SwingUtilities.getRoot(OldValueWrapper.this.comp);
			if (root != null && !OldValueWrapper.this.changed) {
				root.repaint();
			}
			OldValueWrapper.this.changed = true;
		}
	}

	public boolean isChanged() {
		return this.changed;
	}
}
