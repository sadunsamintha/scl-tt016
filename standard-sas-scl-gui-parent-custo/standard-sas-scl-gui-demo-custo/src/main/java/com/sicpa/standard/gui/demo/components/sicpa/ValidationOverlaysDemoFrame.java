package com.sicpa.standard.gui.demo.components.sicpa;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;

import com.jidesoft.swing.DefaultOverlayable;
import com.sicpa.standard.gui.components.layeredComponents.validationOverlay.TextValidationOverlayFactory;
import com.sicpa.standard.gui.components.layeredComponents.validationOverlay.ValidationOverlay;
import com.sicpa.standard.gui.components.layeredComponents.validationOverlay.ValidationOverlayFactory;
import com.sicpa.standard.gui.components.layeredComponents.validationOverlay.ValidationPaint;
import com.sicpa.standard.gui.components.layeredComponents.validationOverlay.Validator;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.PaintUtils;

public class ValidationOverlaysDemoFrame extends javax.swing.JFrame {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				final ValidationOverlaysDemoFrame inst = new ValidationOverlaysDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public ValidationOverlaysDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {

			getContentPane().setLayout(new MigLayout("wrap 1,fill"));

			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

			final ArrayList<Validator<JTextComponent>> checkers = new ArrayList<Validator<JTextComponent>>();
			checkers.add(new Validator<JTextComponent>() {

				@Override
				public String validate(final JTextComponent text) {
					if (!text.getText().startsWith("A")) {
						return "Must begin with an A";
					}
					return null;
				}
			});

			checkers.add(new Validator<JTextComponent>() {

				@Override
				public String validate(final JTextComponent text) {
					if (text.getText().length() < 5) {
						return "Must have at least 5 characters";
					} else {
						System.out.println(">5");
					}
					return null;
				}
			});

			JTextComponent t = new JTextField();
			getContentPane().add(TextValidationOverlayFactory.createBlinking(t, checkers), "growx");

			t = new JTextField();
			getContentPane().add(TextValidationOverlayFactory.createErrorOverlayedIcon(t, checkers), "growx");

			final JTextArea ta = new JTextArea();
			ValidationOverlay vo = TextValidationOverlayFactory.createBlinkingAndIconComponent(ta, checkers);
			final ValidationPaint paint = new ValidationPaint() {
				@Override
				public void paint(final Graphics2D g, final List<String> msg, final JXLayer<? extends JComponent> layer,
						final float animProgress) {
					g.setColor(Color.ORANGE);
					g.setComposite(AlphaComposite.SrcOver.derive(0.3f));
					g.fillRect(0, 0, layer.getWidth(), layer.getHeight());

					g.setComposite(AlphaComposite.SrcOver.derive(0.7f));
					final StringBuilder sb = new StringBuilder();
					if (!msg.isEmpty()) {
						for (final String s : msg) {
							sb.append(s).append("\n");
						}
						PaintUtils.drawMultiLineHighLightText(g, ta, sb.toString(), Color.red, Color.BLACK);
					}
				}
			};
			vo.setPaint(paint);

			getContentPane().add(vo, "grow, h 100");

			getContentPane().add(new JLabel("Must begin with an \"A\" and size >= 5 "), "");
			getContentPane().add(new JSeparator(JSeparator.HORIZONTAL), "growx");

			final Validator<JSpinner> c = new Validator<JSpinner>() {
				@Override
				public String validate(final JSpinner comp) {
					if ((Integer) comp.getValue() == 0) {
						return "cannot be equal to 0";
					}
					return null;
				}
			};

			final JSpinner spinner = new JSpinner();
			spinner.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(final ChangeEvent e) {
					ValidationOverlayFactory.check(spinner);
				}
			});
			vo = ValidationOverlayFactory.createBlinkingAndIcon(spinner, c);
			vo.setIconLocation(0, 20, DefaultOverlayable.NORTH);
			getContentPane().add(vo, "growx,h 100");

			final JSpinner spinner2 = new JSpinner();
			spinner2.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(final ChangeEvent e) {
					ValidationOverlayFactory.check(spinner2);
				}
			});
			vo = ValidationOverlayFactory.createErrorOverlayedIcon(spinner2, c);
			vo.setIconLocation(-20, 20, DefaultOverlayable.NORTH_EAST);
			getContentPane().add(vo, "growx,h 100");

			getContentPane().add(new JLabel("Must be !=0"), "newline");

			setSize(400, 650);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
