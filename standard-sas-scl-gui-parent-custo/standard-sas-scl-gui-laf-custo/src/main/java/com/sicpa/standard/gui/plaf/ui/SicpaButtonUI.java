package com.sicpa.standard.gui.plaf.ui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.AbstractButton;
import javax.swing.DefaultButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.lafwidget.animation.AnimationConfigurationManager;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.internal.utils.ButtonBackgroundDelegate;
import org.pushingpixels.substance.internal.utils.SubstanceCoreUtilities;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.ease.Spline;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.plaf.ui.utils.SubstanceButtonUI;
import com.sicpa.standard.gui.utils.ImageUtils;
import com.sicpa.standard.gui.utils.PaintUtils;

public class SicpaButtonUI extends SubstanceButtonUI {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// try {
				// UIManager.setLookAndFeel(new SubstanceMagellanLookAndFeel());
				// JFrame.setDefaultLookAndFeelDecorated(true);
				// } catch (final UnsupportedLookAndFeelException e1) {
				// }

				SicpaLookAndFeelCusto.install();
				SicpaLookAndFeelCusto.turnOffButtonStateTracking();
				UIManager.put(SicpaButtonUI.ROLLOVER_EFFECT, Boolean.FALSE);

				final JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().setLayout(new MigLayout("fill"));

				f.getContentPane().add(new JLabel("CustomUI"), "spanx,split 2");
				f.getContentPane().add(new JSeparator(), "growx");

				JButton b = new JButton("Do this\nand that");
				b.setIcon(new ImageIcon(ImageUtils.createRandomColorCirlceImage(50)));
				b.setUI(new SicpaButtonUI(b));
				f.getContentPane().add(b, "");

				b = new JButton("Do this\nand that");
				b.setUI(new SicpaButtonUI(b));
				f.getContentPane().add(b, "");

				b = new JButton("Do something");
				b.setIcon(new ImageIcon(ImageUtils.createRandomColorCirlceImage(50)));
				b.setHorizontalTextPosition(SwingConstants.CENTER);
				b.setVerticalTextPosition(SwingConstants.BOTTOM);
				f.getContentPane().add(b, "wrap");

				f.getContentPane().add(new JLabel("Basic HTML"), "spanx,split 2");
				f.getContentPane().add(new JSeparator(), "growx");

				b = new JButton("<html>Do this<br>and that");
				b.setIcon(new ImageIcon(ImageUtils.createRandomColorCirlceImage(50)));
				f.getContentPane().add(b, "grow");

				b = new JButton("<html>Do this<br>and that");
				f.getContentPane().add(b, "");

				f.pack();
				f.setVisible(true);
			}
		});
	}

	private float animProgress;
	private Timeline timeline;

	private ArrayList<BufferedImage> buffersText;

	public static final String ROLLOVER_EFFECT = "com.sicpa.standard.gui.plaf.ui.SicpaButtonUI.ROLLOVER_EFFECT";

	/**
	 * Painting delegate.
	 */
	private ButtonBackgroundDelegate delegate;

	public static ComponentUI createUI(final JComponent comp) {
		SubstanceCoreUtilities.testComponentCreationThreadingViolation(comp);
		return new SicpaButtonUI((JButton) comp);
	}

	// cache the lines
	private String[] splittedText = null;

	/**
	 * Simple constructor.
	 */
	public SicpaButtonUI(final JButton button) {
		super(button);
		this.delegate = new ButtonBackgroundDelegate();
		this.buffersText = new ArrayList<BufferedImage>();

		this.timeline = new Timeline(this);
		this.timeline.addPropertyToInterpolate("animProgress", 0f, 1f);
		this.timeline.setEase(new Spline(0.8f));
		this.timeline.setDuration(AnimationConfigurationManager.getInstance().getTimelineDuration());
	}

	@Override
	protected void installListeners(final AbstractButton b) {
		super.installListeners(b);

		this.textChangeListener = new TextChangeListener();
		b.addPropertyChangeListener("text", this.textChangeListener);
		b.addComponentListener(this.componentAdapter);

		boolean previousState = b.getModel().isEnabled();

		if (isWithRollOverEffect()) {
			b.addMouseListener(this.animTrigger);
		} else {
			b.setModel(new DefaultButtonModel() {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isRollover() {
					return false;
				}
			});
			b.setEnabled(previousState);
		}
	}

	private boolean withRollOverEffect = true;
	private boolean withRollOverEffectSet = false;;

	private boolean isWithRollOverEffect() {
		if (!this.withRollOverEffectSet) {
			Object o = UIManager.get(ROLLOVER_EFFECT);
			if (o instanceof Boolean) {
				this.withRollOverEffect = (Boolean) o;
			}
			this.withRollOverEffectSet = true;
		}

		return this.withRollOverEffect;
	}

	private TextChangeListener textChangeListener;

	private class TextChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			if (evt.getNewValue() != null && !evt.getNewValue().toString().isEmpty()) {
				SicpaButtonUI.this.splittedText = evt.getNewValue().toString().split("\n");
			} else {
				SicpaButtonUI.this.splittedText = null;
			}
			// the text change compute again the shadow of the text
			SicpaButtonUI.this.buffersText.clear();
			SicpaButtonUI.this.button.repaint();
		}
	};

	@Override
	protected void uninstallListeners(final AbstractButton b) {
		super.uninstallListeners(b);
		b.removePropertyChangeListener(this.textChangeListener);
		this.textChangeListener = null;
		b.removeMouseListener(this.animTrigger);
		b.removeComponentListener(this.componentAdapter);
	}

	private boolean smallButton = false;

	public void setSmallButton(final boolean smallButton) {
		this.smallButton = smallButton;
	}

	@Override
	public void paint(final Graphics g, final JComponent c) {
		if (!SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			return;
		}

		final AbstractButton b = (AbstractButton) c;

		FontMetrics fm = g.getFontMetrics();

		Insets i = c.getInsets();

		Rectangle viewRect = new Rectangle();
		Rectangle iconRect = new Rectangle();
		Rectangle trueIconRect = new Rectangle();
		Rectangle textRect = new Rectangle();

		viewRect.x = i.left;
		viewRect.y = i.top;
		viewRect.width = b.getWidth() - (i.right + viewRect.x);
		viewRect.height = b.getHeight() - (i.bottom + viewRect.y);

		textRect.x = textRect.y = textRect.width = textRect.height = 0;
		iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

		Font f = c.getFont();

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setFont(f);

		this.delegate.updateBackground(g2, b);

		String message = b.getText();

		if (this.splittedText == null && b.getText() != null && !b.getText().isEmpty()) {
			this.splittedText = b.getText().split("\n");
		}

		if (this.splittedText != null) {
			int lineNumber = 0;
			if (this.splittedText.length > 0 && message.length() > 0) {
				View v = (View) c.getClientProperty(BasicHTML.propertyKey);
				if (v != null) {// HTML paint
					v.paint(g2, textRect);
				} else {
					for (String s : this.splittedText) {
						viewRect.x = i.left;
						viewRect.y = i.top;
						viewRect.width = b.getWidth() - (i.right + viewRect.x);
						viewRect.height = b.getHeight() - (i.bottom + viewRect.y);

						textRect.x = textRect.y = textRect.width = textRect.height = 0;
						iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

						// layout the text and icon
						String text = SwingUtilities.layoutCompoundLabel(c, fm, s, b.getIcon(), b
								.getVerticalAlignment(), b.getHorizontalAlignment(), b.getVerticalTextPosition(), b
								.getHorizontalTextPosition(), viewRect, iconRect, textRect,
								b.getText() == null ? 0 : b.getIconTextGap());
						if (this.smallButton) {
							text = s;
							Rectangle2D r = fm.getStringBounds(text, g2);
							final int w = (int) r.getWidth();
							final int h = (int) r.getHeight();
							textRect = new Rectangle(b.getWidth() / 2 - w / 2, b.getHeight() / 2 - h / 2, 0, 0);
						}

						// set icon location
						if (b.getIcon() != null) {
							if (trueIconRect.x != 0) {
								trueIconRect.x = Math.min(trueIconRect.x, iconRect.x);
							} else {
								trueIconRect.x = iconRect.x;
							}
							trueIconRect.y = iconRect.y;
							trueIconRect.width = iconRect.width;
							trueIconRect.height = iconRect.height;
						}

						{
							// position the text
							if (this.splittedText.length > 1) {
								final double part = fm.getStringBounds(text, g2).getHeight();
								double y = 0.5 * this.button.getHeight();
								y -= part * 0.5 * this.splittedText.length;
								y += part * lineNumber;
								textRect.y = (int) y;
							}
						}
						this.paintButtonText(g2, b, textRect, text);
						lineNumber++;
					}
				}
			}
		}
		// Paint the Icon
		if (b.getIcon() != null) {
			if (trueIconRect.x == 0 && trueIconRect.y == 0 && trueIconRect.width == 0 && trueIconRect.height == 0) {// if
				// no
				// text
				SwingUtilities.layoutCompoundLabel(c, fm, "", b.getIcon(), b.getVerticalAlignment(),
						b.getHorizontalAlignment(), b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
						viewRect, trueIconRect, textRect, b.getText() == null ? 0 : b.getIconTextGap());
			}
			paintIcon(g2, c, trueIconRect);
		}
		// ---- light effect
		if (this.button.isContentAreaFilled()) {
			if (this.button.isEnabled() && isWithRollOverEffect()) {
				PaintUtils.addShadowAndLightEffect(g2, this.button, this.animProgress);
			} else {
				PaintUtils.addShadowAndLightEffect(g2, this.button, 0);
			}
		}
		g2.dispose();
	}

	@Override
	public Dimension getPreferredSize(final JComponent c) {
		if (this.smallButton) {
			return new Dimension();
		}
		final AbstractButton b = (AbstractButton) c;

		View v = (View) c.getClientProperty(BasicHTML.propertyKey);
		if (v != null) {
			return super.getPreferredSize(c);
		}

		if (this.splittedText == null && b.getText() != null && !b.getText().isEmpty()) {
			this.splittedText = b.getText().split("\n");
		}

		String text = "";
		final Font font = b.getFont();
		final FontMetrics fm = b.getFontMetrics(font);
		int maxWidth = 0;
		// find the max width of the text
		if (this.splittedText != null && this.splittedText.length > 0) {
			for (final String s : this.splittedText) {
				final int width = SwingUtilities.computeStringWidth(fm, s);
				if (width > maxWidth) {
					text = s;
					maxWidth = width;
				}
			}
		}

		final Rectangle iconR = new Rectangle();
		final Rectangle textR = new Rectangle();
		final Rectangle viewR = new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);
		SwingUtilities.layoutCompoundLabel(b, fm, text, b.getIcon(), b.getVerticalAlignment(),
				b.getHorizontalAlignment(), b.getVerticalTextPosition(), b.getHorizontalTextPosition(), viewR, iconR,
				textR, (text == null ? 0 : b.getIconTextGap()));

		// The preferred size of the button is the size of the text and icon rectangles plus the buttons insets.
		if (this.splittedText != null && this.splittedText.length > 0) {
			textR.height = (textR.height) * this.splittedText.length;
		}

		final Rectangle r = iconR.union(textR);
		final Insets insets = b.getInsets();
		r.width += insets.left + insets.right;
		r.height += insets.top + insets.bottom;
		return r.getSize();
	}

	private MouseAdapter animTrigger = new MouseAdapter() {
		@Override
		public void mouseEntered(final MouseEvent e) {
			SicpaButtonUI.this.timeline.play();
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			SicpaButtonUI.this.timeline.playReverse();
		}
	};

	public void setAnimProgress(final float animProgress) {
		this.animProgress = animProgress;
		this.button.repaint();
	}

	public float getAnimProgress() {
		return this.animProgress;
	}

	// clear the buffer when the button is resize
	private ComponentAdapter componentAdapter = new ComponentAdapter() {
		@Override
		public void componentResized(final ComponentEvent e) {
			SicpaButtonUI.this.buffersText.clear();
			SicpaButtonUI.this.button.repaint();
		}
	};

	@Override
	protected void installDefaults(final AbstractButton b) {
		super.installDefaults(b);
		SicpaLookAndFeelCusto.flagAsButton(b);
	}
}
