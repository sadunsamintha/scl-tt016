package com.sicpa.standard.gui.screen.loader;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXImagePanel;
import org.jdesktop.swingx.JXImagePanel.Style;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.image.ColorTintFilter;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.Timeline.RepeatBehavior;

import com.jhlabs.image.GaussianFilter;
import com.sicpa.standard.gui.components.dialog.dropShadow.DialogWithDropShadow;
import com.sicpa.standard.gui.components.transition.CloseTransition;
import com.sicpa.standard.gui.components.transition.impl.RectanglesTransition;
import com.sicpa.standard.gui.listener.Draggable;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.EmptyMachineFrame;
import com.sicpa.standard.gui.utils.PaintUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.gui.utils.WindowsUtils;

public class LoadApplicationScreen extends DialogWithDropShadow {

	public static boolean DOUBLE_BUFFERING_OFF = true;

	public static void main(final String[] args) {
		final AbstractApplicationLoader loader = new AbstractApplicationLoader(true) {
			@Override
			protected void loadApplication() {
				for (int i = 0; i <= 10; i++) {
					setProgress(i * 10);
					setProgressText("loading module #" + i);
					ThreadUtils.sleepQuietly(2500);
				}
			}

			@Override
			protected void done() {
				EmptyMachineFrame t = new EmptyMachineFrame();
				t.setVisible(true);
			}
		};
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				LoadApplicationScreen loading = new LoadApplicationScreen(loader);
				Draggable.makeDraggable(loading);
				loading.setSize(400, 350);
				loading.setText("APPLICATION\nNAME");
				loading.start();
			}
		});
	}

	private static final long serialVersionUID = 1L;
	private float animProgress;

	private BufferedImage image;
	private JProgressBar progressBar;
	private AbstractApplicationLoader loader;

	private JXImagePanel imagePanel;
	private JLabel label;

	private JPanel panelProgress;

	private BufferedImage buffer;

	private boolean withPulse;
	private String text;

	private Font textFont;
	private Timeline timeline;

	public LoadApplicationScreen(final AbstractApplicationLoader loader, final boolean withPulse) {
		super(null, false, false);
		WindowsUtils.hideDecoration(this);
		if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
			setContentPaneBackground(SubstanceLookAndFeel.getCurrentSkin()
					.getBackgroundColorScheme(DecorationAreaType.NONE).getDarkColor());
		} else {
			setContentPaneBackground(SicpaColor.BLUE_DARK);
		}
		setAlwaysOnTop(false);
		this.loader = loader;
		this.textFont = SicpaFont.getFont(45);
		this.withPulse = withPulse;
		loader.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				progressChanged(evt);
			}
		});
		initGUI();
	}

	public LoadApplicationScreen(final AbstractApplicationLoader loader) {
		this(loader, true);
	}

	public void start() {
		setLocationRelativeTo(null);
		setVisible(true);
		if (this.text == null || this.text.length() == 0) {
			this.image = null;
			return;
		}

		initImage();

		this.loader.execute();
		if (this.withPulse) {
			setupAnimatiom();
		}
	}

	public void initImage() {
		BufferedImage img = GraphicsUtilities.createCompatibleTranslucentImage(getImagePanel().getWidth(),
				getImagePanel().getHeight());
		Graphics2D g2 = (Graphics2D) img.getGraphics();

		g2.setFont(LoadApplicationScreen.this.textFont);
		PaintUtils.drawMultiLineHighLightText(g2, new Rectangle(0, 0, getImagePanel().getWidth(), getImagePanel()
				.getHeight()), 3, 0.85f, LoadApplicationScreen.this.text, 0, 0, SicpaColor.BLUE_MEDIUM,
				SicpaColor.BLUE_LIGHT, true, true);
		g2.dispose();
		LoadApplicationScreen.this.setImage(img, Style.CENTERED);

		this.buffer = null;
	}

	private void progressChanged(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("progress")) {
			if (this.progressBar != null) {
				LoadApplicationScreen.this.progressBar.setValue((Integer) evt.getNewValue());
			}
		} else if (evt.getPropertyName().equals("state")) {
			if (evt.getNewValue().toString().equals("DONE")) {
				CloseTransition t = new RectanglesTransition(this);
				t.setEndAction(new AbstractAction() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						setVisible(false);
					}
				});
				t.startCloseTransition();
			}
		} else if (evt.getPropertyName().equals("text")) {
			if (this.label != null) {
				this.label.setText(evt.getNewValue() + "");
			}
		}
	}

	private void initGUI() {
		if (DOUBLE_BUFFERING_OFF) {
			RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
		}
		setSize(640, 480);

		getContentPane().setLayout(new MigLayout("fill , inset 15 15 15 15"));

		getContentPane().add(getImagePanel(), "grow");
		if (this.loader.isWithProgress()) {
			getContentPane().add(getPanelProgress(), "newline,growx");
		}
	}

	public JPanel getPanelProgress() {
		if (this.panelProgress == null) {
			this.panelProgress = new JPanel(new MigLayout("wrap,fill"));

			this.panelProgress.add(getLabel());
			this.panelProgress.add(getProgressBar(), "grow x,h 1.5cm");
			this.panelProgress.setOpaque(false);
		}
		return this.panelProgress;
	}

	public JProgressBar getProgressBar() {
		if (this.progressBar == null) {
			this.progressBar = new JProgressBar();
			this.progressBar.setMinimum(0);
			this.progressBar.setValue(this.progressBar.getMinimum());
			this.progressBar.setStringPainted(true);
		}
		return this.progressBar;
	}

	public JLabel getLabel() {
		if (this.label == null) {
			this.label = new JLabel();
		}
		return this.label;
	}

	public JXImagePanel getImagePanel() {
		if (this.imagePanel == null) {
			this.imagePanel = new JXImagePanel();
			this.imagePanel.setOpaque(false);
		}
		return this.imagePanel;
	}

	private void setImage(final BufferedImage img, final Style style) {
		if (img == null) {
			throw new IllegalArgumentException("The BufferedImage can not be null");
		}
		getImagePanel().setImage(img);
		getImagePanel().setStyle(style);
		// if (!this.loader.isWithProgress()) {
		// setSize(img.getWidth(this), img.getHeight(this));
		// }
		this.image = img;
	}

	public void setAnimProgress(final float animProgress) {

		this.animProgress = animProgress;
		if (this.image == null) {
			return;
		}
		float radius = 10;
		if (this.buffer == null) {
			this.buffer = GraphicsUtilities.createCompatibleTranslucentImage(
					(int) (this.image.getWidth() + radius * 2), (int) (this.image.getHeight() + radius * 2));
			Graphics2D g2 = (Graphics2D) this.buffer.getGraphics();

			g2.drawImage(this.image, (int) radius, (int) radius, null);
			g2.dispose();

			new ColorTintFilter(Color.WHITE, 1f).filter(this.buffer, this.buffer);
			// now we have a white image

			new GaussianFilter(radius).filter(this.buffer, this.buffer);
			// now the image is blurred
		}

		BufferedImage newbuffer = GraphicsUtilities.createCompatibleTranslucentImage(this.buffer.getWidth(),
				this.buffer.getHeight());
		Graphics2D g2 = (Graphics2D) newbuffer.getGraphics();

		g2.setComposite(AlphaComposite.SrcOver.derive(this.animProgress));
		g2.drawImage(this.buffer, 0, 0, null);
		g2.setComposite(AlphaComposite.SrcOver.derive(1f));
		g2.drawImage(this.image, (int) radius, (int) radius, null);
		g2.dispose();

		this.imagePanel.setImage(newbuffer);
	}

	private void setupAnimatiom() {
		this.timeline = new Timeline(this);
		this.timeline.setDuration(1000);
		this.timeline.addPropertyToInterpolate("animProgress", 0f, 1f);
		this.timeline.playLoop(RepeatBehavior.REVERSE);
	}

	public void setText(final String text) {
		this.text = text;
	}

	public void setTextFont(final Font textFont) {
		this.textFont = textFont;
	}

	@Override
	public void setVisible(final boolean b) {
		super.setVisible(b);
		if (!b) {
			if (this.timeline != null) {
				this.timeline.cancel();
			}
			if (DOUBLE_BUFFERING_OFF) {
				RepaintManager.currentManager(this).setDoubleBufferingEnabled(true);
			}
		}
	}
}
