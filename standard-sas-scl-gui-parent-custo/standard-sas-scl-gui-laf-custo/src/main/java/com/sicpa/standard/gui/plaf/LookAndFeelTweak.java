package com.sicpa.standard.gui.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.buttons.toggleButtons.ToggleImageAndTextButton;
import com.sicpa.standard.gui.listener.Draggable;
import com.sicpa.standard.gui.utils.WindowsUtils;

public class LookAndFeelTweak extends JFrame {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				LookAndFeelTweak f = new LookAndFeelTweak();
				f.setVisible(true);
			}
		});
	}

	public LookAndFeelTweak() {
		initGUI();
		
	}

	JSpinner spinnerPulseSource;
	JSpinner spinnerToggleButtonAnim;
	JSpinner spinnerFlowViewAnim;
	JToggleButton checkFlowViewWithAnim;

	private void initGUI() {
		getContentPane().setLayout(new MigLayout("fill,inset 0 0 0 0"));

		getContentPane().add(new JLabel("pulse source delay (ms)"));
		getContentPane().add(getSpinnerPulseSource(), "h 50!,wrap");

		getContentPane().add(new JLabel("selection flow view animation duration (ms)"));
		getContentPane().add(getSpinnerFlowViewAnim(), "h 50!,wrap");

		getContentPane().add(new JLabel("toggle button animation duration (ms)"));
		getContentPane().add(getSpinnerToggleButtonAnim(), "h 50!,wrap");

		getContentPane().add(getCheckFlowViewWithAnim(),"spanx");

		((JPanel) getContentPane()).setBorder(new LineBorder(Color.black));

		for (Component c : getContentPane().getComponents()) {
			c.setFont(SicpaFont.getFont(12));
		}

		setAlwaysOnTop(true);
		pack();
		
		setResizable(false);

		Draggable.makeDraggable(this);
	}

	public JSpinner getSpinnerFlowViewAnim() {
		if (this.spinnerFlowViewAnim == null) {
			this.spinnerFlowViewAnim = new JSpinner(new SpinnerNumberModel(500, 1, 9999, 1));
			this.spinnerFlowViewAnim.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent e) {
					UIManager
							.put("transitionUI.animDuration", LookAndFeelTweak.this.spinnerToggleButtonAnim.getValue());
				}
			});
		}
		return this.spinnerFlowViewAnim;
	}

	public JSpinner getSpinnerPulseSource() {
		if (this.spinnerPulseSource == null) {
			this.spinnerPulseSource = new JSpinner(new SpinnerNumberModel(SicpaLookAndFeelCusto.SicpaPulseSource.DELAY, 1,
					9999, 1));
			this.spinnerPulseSource.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(final ChangeEvent e) {
					SicpaLookAndFeelCusto.SicpaPulseSource.DELAY = (Integer) LookAndFeelTweak.this.spinnerPulseSource
							.getValue();
				}
			});
		}
		return this.spinnerPulseSource;
	}

	public JSpinner getSpinnerToggleButtonAnim() {
		this.spinnerToggleButtonAnim = new JSpinner(new SpinnerNumberModel(500, 1, 9999, 1));
		this.spinnerToggleButtonAnim.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				UIManager.put("toggleImageButton.animDuration", LookAndFeelTweak.this.spinnerToggleButtonAnim
						.getValue());
			}
		});
		return this.spinnerToggleButtonAnim;
	}

	public JToggleButton getCheckFlowViewWithAnim() {
		this.checkFlowViewWithAnim = new ToggleImageAndTextButton("selection view with animation");
		this.checkFlowViewWithAnim.setSelected(true);
		this.checkFlowViewWithAnim.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				UIManager.put("selectionFlowView.withAnimation", LookAndFeelTweak.this.checkFlowViewWithAnim
						.isSelected());
			}
		});
		return this.checkFlowViewWithAnim;
	}
}
