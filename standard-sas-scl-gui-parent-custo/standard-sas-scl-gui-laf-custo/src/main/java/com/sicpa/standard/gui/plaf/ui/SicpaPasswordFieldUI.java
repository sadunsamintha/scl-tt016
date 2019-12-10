package com.sicpa.standard.gui.plaf.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.FieldView;
import javax.swing.text.Position;
import javax.swing.text.View;

import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.internal.ui.SubstancePasswordFieldUI;
import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceColorUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceSizeUtils;

import com.sicpa.standard.gui.components.border.SicpaTextComponentBorder;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.plaf.ui.utils.SicpaTextPaintUtils;

public class SicpaPasswordFieldUI extends SubstancePasswordFieldUI {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();

				JPasswordField p = new JPasswordField();
				SicpaLookAndFeelCusto.turnOnPasswordFieldDisplayLastCharacter();
				f.getContentPane().add(p);
				f.setSize(200, 200);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setVisible(true);

			}
		});
	}

	public static final String DISPLAY_LAST_CHAR = "SicpaPasswordFieldUI.DISPLAY_LAST_CHAR";
	public static int lastCharDisplayTime = 2000;

	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaPasswordFieldUI(comp);
	}

	public SicpaPasswordFieldUI(final JComponent c) {
		super(c);
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		Border b = this.passwordField.getBorder();
		if (b == null || b instanceof UIResource) {
			Border newB = new BorderUIResource.CompoundBorderUIResource(

			new SicpaTextComponentBorder(SubstanceSizeUtils.getTextBorderInsets(SubstanceSizeUtils
					.getComponentFontSize(this.passwordField))),

			new BasicBorders.MarginBorder()

			);

			this.passwordField.setBorder(newB);
		}
	}

	@Override
	protected void paintBackground(final Graphics g) {
		SicpaTextPaintUtils.paintTextFieldBackground(g, this.passwordField);
	}

	private static class SubstancePasswordView extends FieldView {
		/**
		 * The associated password field.
		 */
		private JPasswordField field;

		/**
		 * Simple constructor.
		 * 
		 * @param field
		 *            The associated password field.
		 * @param element
		 *            The element
		 */
		public SubstancePasswordView(JPasswordField field, Element element) {
			super(element);
			this.field = field;
		}

		protected DocumentListener docListener;

		// to control that the last char remain visble only x ms
		protected boolean lastCharTimerExpired = false;
		protected Timer timerLastChar;

		protected void installDocListnerForLastChar() {
			docListener = new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent e) {
					changedUpdate(e);
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					changedUpdate(e);
				}


				@Override
				public void changedUpdate(DocumentEvent e) {
					if (timerLastChar == null) {
						timerLastChar = createTimerLastChar();
					}
					timerLastChar.restart();
					lastCharTimerExpired = false;
				}
			};
			field.getDocument().addDocumentListener(docListener);
		}

		protected Timer createTimerLastChar() {
			return new Timer(lastCharDisplayTime, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					lastCharTimerExpired = true;
					field.repaint();
					timerLastChar.stop();
				}
			});
		}

		protected boolean isLastCharDisplayed() {
			if (isLastCharConfiguredToBeDisplayed()) {
				if (docListener == null) {
					installDocListnerForLastChar();
				}

				return !lastCharTimerExpired;
			}
			return false;
		}

		protected boolean isLastCharConfiguredToBeDisplayed() {

			Object b = field.getClientProperty(DISPLAY_LAST_CHAR);
			if (b == null) {
				b = UIManager.get(DISPLAY_LAST_CHAR);
				if (b == null) {
					return false;
				}
			}
			if (b instanceof Boolean) {
				return (Boolean) b;
			} else {
				return Boolean.parseBoolean(b.toString());
			}
		}

		/**
		 * Draws the echo character(s) for a single password field character. The number of echo characters is defined
		 * by {@link SubstanceLookAndFeel#PASSWORD_ECHO_PER_CHAR} client property.
		 * 
		 * @param g
		 *            Graphics context
		 * @param x
		 *            X coordinate of the first echo character to draw.
		 * @param y
		 *            Y coordinate of the first echo character to draw.
		 * @param c
		 *            Password field.
		 * @param isSelected
		 *            Indicates whether the password field character is selected.
		 * @return The X location of the next echo character.
		 * @see SubstanceLookAndFeel#PASSWORD_ECHO_PER_CHAR
		 */
		protected int drawEchoCharacter(Graphics g, int x, int y, char c, boolean isSelected, boolean last) {
			Container container = this.getContainer();

			Graphics2D graphics = (Graphics2D) g;
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			JPasswordField field = (JPasswordField) container;

			int fontSize = SubstanceSizeUtils.getComponentFontSize(this.field);
			int dotDiameter = SubstanceSizeUtils.getPasswordDotDiameter(fontSize);
			int dotGap = SubstanceSizeUtils.getPasswordDotGap(fontSize);
			ComponentState state = // isSelected ? ComponentState.SELECTED
			(field.isEnabled() ? ComponentState.ENABLED : ComponentState.DISABLED_UNSELECTED);
			SubstanceColorScheme scheme = SubstanceColorSchemeUtilities.getColorScheme(field, state);
			Color topColor = isSelected ? scheme.getSelectionForegroundColor() : SubstanceColorUtilities
					.getForegroundColor(scheme);
			Color bottomColor = topColor.brighter();
			graphics.setPaint(new GradientPaint(x, y - dotDiameter, topColor, x, y, bottomColor));
			int echoPerChar = SubstanceCoreUtilities.getEchoPerChar(field);
			if (last && isLastCharDisplayed()) {
				graphics.drawString(String.valueOf(c), x + dotGap / 2, y);
			} else {
				for (int i = 0; i < echoPerChar; i++) {
					graphics.fillOval(x + dotGap / 2, y - dotDiameter, dotDiameter, dotDiameter);
					x += (dotDiameter + dotGap);
				}
			}
			return x;
		}

		/**
		 * Returns the advance of a single password field character. The advance is the pixel distance between first
		 * echo characters of consecutive password field characters. The
		 * {@link SubstanceLookAndFeel#PASSWORD_ECHO_PER_CHAR} can be used to specify that more than one echo character
		 * is used for each password field character.
		 * 
		 * @return The advance of a single password field character
		 */
		protected int getEchoCharAdvance() {
			int fontSize = SubstanceSizeUtils.getComponentFontSize(this.field);
			int dotDiameter = SubstanceSizeUtils.getPasswordDotDiameter(fontSize);
			int dotGap = SubstanceSizeUtils.getPasswordDotGap(fontSize);
			int echoPerChar = SubstanceCoreUtilities.getEchoPerChar(field);
			return echoPerChar * (dotDiameter + dotGap);
		}

		@Override
		protected int drawSelectedText(Graphics g, final int x, final int y, int p0, int p1)
				throws BadLocationException {
			Container c = getContainer();
			if (c instanceof JPasswordField) {
				JPasswordField f = (JPasswordField) c;
				if (!f.echoCharIsSet()) {
					return super.drawSelectedText(g, x, y, p0, p1);
				}
				int n = p1 - p0;
				int currPos = x;
				for (int i = 0; i < n; i++) {
					currPos = drawEchoCharacter(g, currPos, y, f.getPassword()[i], true, i == n - 1);
				}
				return x + n * getEchoCharAdvance();
			}
			return x;
		}

		protected int previousSize = 0;

		boolean displayLastChar = false;

		@Override
		protected int drawUnselectedText(Graphics g, final int x, final int y, int p0, int p1)
				throws BadLocationException {

			Container c = getContainer();
			if (c instanceof JPasswordField) {
				JPasswordField f = (JPasswordField) c;
				if (!f.echoCharIsSet()) {
					return super.drawUnselectedText(g, x, y, p0, p1);
				}
				int n = p1 - p0;

				int currPos = x;
				if (n == previousSize) {
				} else if (n > previousSize && previousSize != 0) {
					displayLastChar = true;
					previousSize = n;
				} else {
					displayLastChar = false;
					previousSize = n;
				}

				boolean lastCharacter = false;
				for (int i = 0; i < n; i++) {
					lastCharacter = i == n - 1;
					currPos = drawEchoCharacter(g, currPos, y, f.getPassword()[i], false, lastCharacter
							&& displayLastChar);
				}
				return x + n * getEchoCharAdvance();
			}
			return x;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.text.View#modelToView(int, java.awt.Shape, javax.swing.text.Position.Bias)
		 */
		@Override
		public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
			Container c = this.getContainer();
			if (c instanceof JPasswordField) {
				JPasswordField f = (JPasswordField) c;
				if (!f.echoCharIsSet()) {
					return super.modelToView(pos, a, b);
				}

				Rectangle alloc = this.adjustAllocation(a).getBounds();
				int echoPerChar = SubstanceCoreUtilities.getEchoPerChar(f);
				int fontSize = SubstanceSizeUtils.getComponentFontSize(this.field);
				int dotWidth = SubstanceSizeUtils.getPasswordDotDiameter(fontSize)
						+ SubstanceSizeUtils.getPasswordDotGap(fontSize);

				int dx = (pos - this.getStartOffset()) * echoPerChar * dotWidth;
				alloc.x += dx;
				alloc.width = 1;
				return alloc;
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.text.View#viewToModel(float, float, java.awt.Shape, javax.swing.text.Position.Bias[])
		 */
		@Override
		public int viewToModel(float fx, float fy, Shape a, Position.Bias[] bias) {
			bias[0] = Position.Bias.Forward;
			int n = 0;
			Container c = this.getContainer();
			if (c instanceof JPasswordField) {
				JPasswordField f = (JPasswordField) c;
				if (!f.echoCharIsSet()) {
					return super.viewToModel(fx, fy, a, bias);
				}
				a = this.adjustAllocation(a);
				Rectangle alloc = (a instanceof Rectangle) ? (Rectangle) a : a.getBounds();
				int echoPerChar = SubstanceCoreUtilities.getEchoPerChar(f);
				int fontSize = SubstanceSizeUtils.getComponentFontSize(this.field);
				int dotWidth = SubstanceSizeUtils.getPasswordDotDiameter(fontSize)
						+ SubstanceSizeUtils.getPasswordDotGap(fontSize);
				n = ((int) fx - alloc.x) / (echoPerChar * dotWidth);
				if (n < 0) {
					n = 0;
				} else {
					if (n > (this.getStartOffset() + this.getDocument().getLength())) {
						n = this.getDocument().getLength() - this.getStartOffset();
					}
				}
			}
			return this.getStartOffset() + n;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.text.View#getPreferredSpan(int)
		 */
		@Override
		public float getPreferredSpan(int axis) {
			switch (axis) {
			case View.X_AXIS:
				Container c = this.getContainer();
				if (c instanceof JPasswordField) {
					JPasswordField f = (JPasswordField) c;
					if (f.echoCharIsSet()) {
						int echoPerChar = SubstanceCoreUtilities.getEchoPerChar(f);
						int fontSize = SubstanceSizeUtils.getComponentFontSize(this.field);
						int dotWidth = SubstanceSizeUtils.getPasswordDotDiameter(fontSize)
								+ SubstanceSizeUtils.getPasswordDotGap(fontSize);
						return echoPerChar * dotWidth * this.getDocument().getLength();
					}
				}
			}
			return super.getPreferredSpan(axis);
		}

	}

	@Override
	public View create(Element elem) {
		return new SubstancePasswordView(this.passwordField, elem);
	}
}
