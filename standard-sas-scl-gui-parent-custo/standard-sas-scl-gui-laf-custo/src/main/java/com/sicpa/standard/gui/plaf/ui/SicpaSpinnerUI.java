package com.sicpa.standard.gui.plaf.ui;

import com.sicpa.standard.gui.components.buttons.shape.spinner.SpinnerLeftButton;
import com.sicpa.standard.gui.components.buttons.shape.spinner.SpinnerRightButton;
import com.sicpa.standard.gui.components.format.LocaleFormatterFactory;
import com.sicpa.standard.gui.components.virtualKeyboard.ISpinnerDialogCallback;
import com.sicpa.standard.gui.components.virtualKeyboard.SpinnerNumericVirtualKeyboard;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.LocaleUtils;
import org.pushingpixels.lafwidget.LafWidget;
import org.pushingpixels.substance.api.ColorSchemeAssociationKind;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceSizeUtils;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;

public class SicpaSpinnerUI extends BasicSpinnerUI {
	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				{
					SicpaLookAndFeelCusto.install();
					final JFrame f = new JFrame();

					SicpaLookAndFeelCusto.flagAsWorkArea((JComponent) f.getContentPane());
					f.setSize(300, 150);
					f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					f.getContentPane().setLayout(new MigLayout("fill"));

					SpinnerModel model = new SpinnerNumberModel(1, 0, 60555, 1);
					JSpinner s = new JSpinner();
					s.setValue(0);
					s.setModel(model);

					f.getContentPane().add(s);

					f.setVisible(true);
					f.getContentPane().add(s, "grow");
				}
			}
		});
	}

	public static final String VIRTUAL_NUMERIC_KEYBOARD = "com.sicpa.standard.gui.plaf.ui.SicpaSpinnerUI.VIRTUAL_NUMERIC_KEYBOARD";
	private static final String DEFAULT_LANG = "en";


	protected JButton nextButton;
	protected JButton prevButton;

	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaSpinnerUI();
	}

	@Override
	protected Component createNextButton() {
		this.nextButton = new SpinnerRightButton();
		this.nextButton.setFont(this.spinner.getFont());
		this.nextButton.setName("Spinner.nextButton");

		this.installNextButtonListeners(this.nextButton);

		this.nextButton.setOpaque(false);

		this.nextButton.setPreferredSize(new Dimension(40, 50));

		return this.nextButton;
	}

	@Override
	protected Component createPreviousButton() {
		this.prevButton = new SpinnerLeftButton();
		this.prevButton.setFont(this.spinner.getFont());
		this.prevButton.setName("Spinner.previousButton");

		this.installPreviousButtonListeners(this.prevButton);

		this.prevButton.setOpaque(false);

		this.prevButton.setPreferredSize(new Dimension(40, 50));

		return this.prevButton;
	}

	private boolean opaqueDefault;
	private boolean editableDefault;

	@Override
	protected void installDefaults() {
		super.installDefaults();
		spinner.setLocale(getLocale());
		JComponent editor = this.spinner.getEditor();

		if ((editor != null) && (editor instanceof JSpinner.DefaultEditor)) {
			editor.setLocale(getLocale());
			final JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
			tf.setFocusLostBehavior(JFormattedTextField.PERSIST);
			tf.setFormatterFactory(new LocaleFormatterFactory(getLocale(),spinner.getModel().getValue().getClass()));
			tf.setLocale(getLocale());

			if (tf != null) {
				tf.setColumns(6);
				tf.setFont(this.spinner.getFont());
				tf.putClientProperty(LafWidget.HAS_LOCK_ICON, Boolean.FALSE);
				this.editableDefault = tf.isEditable();
				this.opaqueDefault = tf.isOpaque();
				tf.setOpaque(false);
				tf.setEditable(false);
				int strokeWidth = (int) SubstanceSizeUtils.getArrowStrokeWidth(SubstanceSizeUtils.getComponentFontSize(tf));
				Color borderColor = SubstanceColorSchemeUtilities.getColorScheme(tf, ColorSchemeAssociationKind.BORDER, ComponentState.DEFAULT).getDarkColor();
				tf.setBorder(BorderFactory.createLineBorder(borderColor, strokeWidth));
			}
		}
		this.spinner.setOpaque(false);
		this.spinner.setBorder(BorderFactory.createEmptyBorder());
		this.spinner.addPropertyChangeListener("editor", this.editorChangeListener);
		this.spinner.addPropertyChangeListener("enabled",this.propertyChangeListener);



	}

	private PropertyChangeListener editorChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			final JComponent editor = SicpaSpinnerUI.this.spinner.getEditor();
			if ((editor != null) && (editor instanceof JSpinner.DefaultEditor)) {
				final JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
				// tf.setEditable(true);
				if (tf != null) {
					tf.setColumns(6);
					tf.setFont(SicpaSpinnerUI.this.spinner.getFont());
					tf.putClientProperty(LafWidget.HAS_LOCK_ICON, Boolean.FALSE);
					SicpaSpinnerUI.this.editableDefault = tf.isEditable();
					SicpaSpinnerUI.this.opaqueDefault = tf.isOpaque();
					tf.setOpaque(false);
					tf.setEditable(false);

					int strokeWidth = (int) SubstanceSizeUtils.getArrowStrokeWidth(SubstanceSizeUtils.getComponentFontSize(tf));
					Color borderColor = SubstanceColorSchemeUtilities.getColorScheme(tf, ColorSchemeAssociationKind.BORDER, ComponentState.DEFAULT).getDarkColor();

					tf.setBorder(BorderFactory.createLineBorder(borderColor, strokeWidth));
				}
			}
		}
	};

	@Override
	protected LayoutManager createLayout() {
		return new SpinnerLayout();
	}

	private static class SpinnerLayout implements LayoutManager {
		private LayoutManager2 layout = new MigLayout("ltr,fill,insets 0 0 0 0, gap 0 0 0 0", "", "");

		@Override
		public void addLayoutComponent(final String name, final Component c) {
			if ("Next".equals(name)) {
				this.layout.addLayoutComponent(c, "cell 2 0,grow 2");
			} else if ("Previous".equals(name)) {
				this.layout.addLayoutComponent(c, "cell 0 0,grow 2");
			} else if ("Editor".equals(name)) {
				this.layout.addLayoutComponent(c, "cell 1 0,grow 3");
			}
		}

		@Override
		public void removeLayoutComponent(final Component c) {
			this.layout.removeLayoutComponent(c);
		}

		@Override
		public Dimension preferredLayoutSize(final Container parent) {
			return this.layout.preferredLayoutSize(parent);
		}

		@Override
		public Dimension minimumLayoutSize(final Container parent) {
			return this.layout.preferredLayoutSize(parent);
		}

		@Override
		public void layoutContainer(final Container parent) {
			this.layout.layoutContainer(parent);
		}
	}

	@Override
	protected void installPreviousButtonListeners(final Component c) {
		this.prevButton.addMouseListener(this.buttonMouseListener);
	}

	@Override
	protected void installNextButtonListeners(final Component c) {
		this.nextButton.addMouseListener(this.buttonMouseListener);
	}

	private Locale getLocale() {
		String jvmLang = Optional.ofNullable(System.getProperty("local.language")).orElse(DEFAULT_LANG);
		return LocaleUtils.availableLocaleList().stream()
				.filter(l -> l.getLanguage().equals(jvmLang) && l.getCountry().equals(""))
				.findFirst().orElse(Locale.getDefault());

	}

	private MouseListener thisMouselistener = new MouseAdapter() {
		@Override
		public void mouseReleased(final MouseEvent e) {
			if (SicpaSpinnerUI.this.spinner.isEnabled() && SicpaSpinnerUI.this.spinner.getEditor().isEnabled()) {
				boolean kbVisible = true;
				boolean clientPropSet = false;
				Object o = SicpaSpinnerUI.this.spinner.getClientProperty(VIRTUAL_NUMERIC_KEYBOARD);
				if (o != null) {
					if (o instanceof Boolean) {
						kbVisible = (Boolean) o;
						clientPropSet = true;
					}
				}
				if (!clientPropSet) {
					o = UIManager.get(VIRTUAL_NUMERIC_KEYBOARD);
					if (o != null) {
						if (o instanceof Boolean) {
							kbVisible = (Boolean) o;
						}
					}
				}
				if (kbVisible) {
					SpinnerNumericVirtualKeyboard.setCallback(new ISpinnerDialogCallback() {
						@Override
						public boolean accept() {
							String value = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().getText();
							try {
								NumberFormat f = NumberFormat.getInstance(getLocale());
								Number n = f.parse(value);
								acceptValue(n.doubleValue());
								spinner.setValue(castAsNumericTypeFromModel(spinner,n));
								((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setText(value);
								return true;
							} catch (ParseException ex) {
								return false;
							}
						}
					});
					SpinnerNumericVirtualKeyboard.getKeyboard().setVisible(((JSpinner.DefaultEditor) SicpaSpinnerUI.this.spinner.getEditor()).getTextField(),(SpinnerNumberModel) spinner.getModel());
				}
			}
		}
	};

	private Object castAsNumericTypeFromModel(JSpinner spinner, Number n) {
		Object ret = null;
		if (spinner.getValue() instanceof Double){
			ret = n.doubleValue();
		}
		if (spinner.getValue() instanceof Float){
			return n.floatValue();
		}
		if (spinner.getValue() instanceof Long){
			return n.longValue();
		}
		if (spinner.getValue() instanceof Integer){
			return n.intValue();
		}
		if (spinner.getValue() instanceof Short){
			return n.shortValue();
		}
		if (spinner.getValue() instanceof Byte ){
			return n.byteValue();
		}
		return ret;
	}

	protected void acceptValue(double d) throws ParseException{
		SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
		if (d < ((Number) model.getMinimum()).doubleValue() || d > ((Number) model.getMaximum()).doubleValue()) {
			throw new ParseException("value does not correspond to constraints,minimumValue="+model.getMinimum()+",maximumValue=" + model.getMaximum(),String.valueOf(d).length());
		}
	}

	private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals("enabled")) {
				SicpaSpinnerUI.this.prevButton.setEnabled(((Boolean) evt.getNewValue()).booleanValue());
				SicpaSpinnerUI.this.nextButton.setEnabled(((Boolean) evt.getNewValue()).booleanValue());
			} else if (evt.getPropertyName().equals("editor")) {
				((JSpinner.DefaultEditor) SicpaSpinnerUI.this.spinner.getEditor()).getTextField().addMouseListener(SicpaSpinnerUI.this.thisMouselistener);
				JComponent editor = SicpaSpinnerUI.this.spinner.getEditor();
				if ((editor != null) && (editor instanceof JSpinner.DefaultEditor)) {
					final JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
					if (tf != null) {
						tf.setColumns(6);
						tf.setFont(SicpaSpinnerUI.this.spinner.getFont());
						tf.putClientProperty(LafWidget.HAS_LOCK_ICON, Boolean.FALSE);
						SicpaSpinnerUI.this.editableDefault = tf.isEditable();
						SicpaSpinnerUI.this.opaqueDefault = tf.isOpaque();
						tf.setOpaque(false);
						tf.setEditable(false);
					}
				}
			}
		}
	};

	@Override
	protected void installListeners() {
		super.installListeners();
		((JSpinner.DefaultEditor) this.spinner.getEditor()).getTextField().addMouseListener(this.thisMouselistener);
		this.spinner.addPropertyChangeListener("enabled", this.propertyChangeListener);
		this.spinner.addPropertyChangeListener("editor", this.propertyChangeListener);
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();
		this.spinner.removePropertyChangeListener(this.propertyChangeListener);
		((JSpinner.DefaultEditor) this.spinner.getEditor()).getTextField().removeMouseListener(this.thisMouselistener);
		JComponent editor = this.spinner.getEditor();
		if ((editor != null) && (editor instanceof JSpinner.DefaultEditor)) {
			final JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
			if (tf != null) {
				tf.setOpaque(this.opaqueDefault);
				tf.setEditable(this.editableDefault);
			}
		}
		this.spinner.removePropertyChangeListener("editor", this.editorChangeListener);
	}

	private class ToggleButtons implements Runnable{
		private final boolean next;

		public ToggleButtons(boolean next){
			this.next = next;
		}

		@Override
		public void run() {
			Object value = next	? spinner.getModel().getNextValue() : spinner.getModel().getPreviousValue();

			if (value != null) {
				spinner.getModel().setValue(value);
			}
		}
	}

	// the what is happening when clicking on the arrows buttons
	private MouseListener buttonMouseListener = new MouseAdapter() {

		private int sleepTime = 1000;
		private long clickedTime;

		// used to be sure if it s actually the same click operation going on,
		// and not a fast clicks on it
		// if instead we use a boolean while having fast click on a button =>
		// multiple threads running
		private long i = 0;

		@Override
		public void mousePressed(final MouseEvent mouseEvent) {
			if (!SicpaSpinnerUI.this.spinner.isEnabled()) {
				return;
			}

			this.clickedTime = System.currentTimeMillis();
			final long num = this.i;

			Runnable changeValue = new Runnable() {
				@Override
				public void run() {
					while (num == i) {
						if (mouseEvent.getSource() == SicpaSpinnerUI.this.prevButton) {
							ThreadUtils.invokeLater(new ToggleButtons(false));
						}
						if (mouseEvent.getSource() == SicpaSpinnerUI.this.nextButton) {
							ThreadUtils.invokeLater(new ToggleButtons(true));
						}
						long delta = System.currentTimeMillis() - clickedTime;
						if (delta < 500) {
							sleepTime = 1000;
						} else if (delta < 750) {
							sleepTime = 60;
						} else if (delta < 1000) {
							sleepTime = 50;
						} else if (delta < 1500) {
							sleepTime = 40;
						} else if (delta < 2000) {
							sleepTime = 50;
						} else if (delta < 3000) {
							sleepTime = 30;
						} else if (delta < 4000) {
							sleepTime = 10;
						} else if (delta < 5000) {
							sleepTime = 5;
						} else if (delta < 7000) {
							sleepTime = 1;
						}
						ThreadUtils.sleepQuietly(sleepTime);
					}
				}
			};
			new Thread(changeValue).start();
		}

		@Override
		public void mouseReleased(final MouseEvent mouseEvent) {
			this.i++;
		}
	};

}
