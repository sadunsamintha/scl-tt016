package com.sicpa.standard.gui.screen.machine.component.light;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ColorUIResource;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;

public class LightPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel label;
	private Light light;

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setSize(400, 400);
				f.getContentPane().setLayout(new MigLayout());
				f.getContentPane().add(new LightPanel(SicpaColor.RED, 20, " label"), "wrap");
				f.getContentPane().add(new LightPanel(SicpaColor.YELLOW, 25, " label"), "wrap");
				f.getContentPane().add(new LightPanel(SicpaColor.GREEN_DARK, 50, " label"), "wrap");

				f.setVisible(true);
			}
		});
	}

	public LightPanel(final String text, final int width) {
		this(Color.red, 25, text);
	}

	public LightPanel(final Color color, final int width, final String text) {
		setLayout(new MigLayout());

		getLight().setColor(color);
		add(getLight(), "spany, w " + width + " ,h " + width);
		add(Box.createVerticalStrut(width / 4), "wrap");
		getLabel().setText(text);
		add(getLabel(), "wrap");
	}

	private Light getLight() {
		if (this.light == null) {
			this.light = new Light();
			this.light.setName("light");
		}
		return this.light;
	}

	private JLabel getLabel() {
		if (this.label == null) {
			this.label = new JLabel() {
				@Override
				public void setForeground(final Color fg) {
					if (getForeground() != null) {
						if ((!(getForeground() instanceof ColorUIResource)) && fg instanceof ColorUIResource) {
							System.out.println(getForeground() + " =>" + fg + "  :" + getText());
							System.out.println("BOOM");
						}
					}

					super.setForeground(fg);
				}
			};
			this.label.setForeground(SicpaColor.BLUE_DARK);
			this.label.setName("lightLabel");
		}
		return this.label;
	}

	public void showGreenColor() {
		this.light.showGreenColor();
	}

	public void showRedColor() {
		this.light.showRedColor();
	}

	public void showYellowtColor() {
		this.light.showYellowColor();
	}

	public void showColor(final Color color) {
		this.light.showColor(color);
	}

	public void setText(final String text) {
		this.label.setText(text);
	}

	public int getAnimDuration() {
		return this.light.getAnimDuration();
	}

	public void setAnimDuration(final int animDuration) {
		this.light.setAnimDuration(animDuration);
	}

	@Override
	public void setForeground(final Color fg) {
		if (this.label != null) {
			if (this.label.getForeground() != null) {
				if (!(this.label.getForeground() instanceof ColorUIResource) && fg instanceof ColorUIResource) {
					//LAF does not override the fg
				} else {
					this.label.setForeground(fg);
				}
			}
		}
	}

	@Override
	public void setFont(final Font font) {
		super.setFont(font);
		if (this.label != null) {
			this.label.setFont(font);
		}
	}
}
