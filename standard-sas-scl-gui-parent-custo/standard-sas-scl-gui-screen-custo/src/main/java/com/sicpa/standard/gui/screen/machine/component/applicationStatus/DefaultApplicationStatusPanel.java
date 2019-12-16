package com.sicpa.standard.gui.screen.machine.component.applicationStatus;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.pushingpixels.trident.Timeline;

import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.component.light.Light;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DefaultApplicationStatusPanel extends AbstractApplicationStatusPanel {

	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().setLayout(new MigLayout("fill"));
				final ApplicationStatusModel model = new ApplicationStatusModel();

				DefaultApplicationStatusPanel p = new DefaultApplicationStatusPanel(model);

				JButton button = new JButton("red");
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						model.setApplicationStatus(ApplicationStatus.STOP);
					}
				});
				f.getContentPane().add(button, "south");

				button = new JButton("green");
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						model.setApplicationStatus(ApplicationStatus.RUNNING);
					}
				});
				f.getContentPane().add(button, "south");

				button = new JButton("orange");
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						model.setApplicationStatus(new ApplicationStatus(SicpaColor.ORANGE, "warning"));
					}
				});
				f.getContentPane().add(button, "south");

				f.getContentPane().add(p, "grow");
				f.setSize(300, 300);
				f.setVisible(true);
			}
		});
	}

	private Light light;
	private JLabel labelStatus;

	public DefaultApplicationStatusPanel(final ApplicationStatusModel model) {
		super(model);
		initGUI();
	}

	public DefaultApplicationStatusPanel() {
		this(new ApplicationStatusModel());
	}

	private void initGUI() {
		setLayout(new MigLayout("fill,inset 0 0 0 0"));
		add(getLight(), "h 65! , w 75!,spanx,split 2");
		add(getLabelStatus(),"grow");
	}

	@Override
	protected void modelApplicationStatusChanged(final PropertyChangeEvent evt) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ApplicationStatus status = (ApplicationStatus) evt.getNewValue();
				getLight().showColor(status.getColor());
				changeLabelStatusColor(status.getColor());
				getLabelStatus().setText(status.getLabelStatus());
			}
		});

	}

	public Light getLight() {
		if (this.light == null) {
			this.light = new Light(SicpaColor.GRAY);
			this.light.setName("statusLightPanel");
			this.light.setAnimDuration(this.animDuration);
		}

		return this.light;
	}

	public JLabel getLabelStatus() {
		if (this.labelStatus == null) {
			this.labelStatus = new JLabel();
			this.labelStatus.setName("statusLabel");
			this.labelStatus.setFont(SicpaFont.getFont(45));
		}
		return this.labelStatus;
	}

	@Override
	public void setModel(final ApplicationStatusModel model) {
		super.setModel(model);
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getLight().showColor(model.getApplicationStatus().getColor());
				changeLabelStatusColor(model.getApplicationStatus().getColor());
				getLabelStatus().setText(model.getApplicationStatus().getLabelStatus());
			}
		});
	}

	Timeline changeStatusColorAnimator;

	private int animDuration = 400;

	public void setAnimDuration(final int animDuration) {
		this.animDuration = animDuration;
		getLight().setAnimDuration(animDuration);
	}

	protected void changeLabelStatusColor(final Color c) {
		if (this.changeStatusColorAnimator != null) {
			this.changeStatusColorAnimator.abort();
		}
		this.changeStatusColorAnimator = new Timeline(getLabelStatus());
		this.changeStatusColorAnimator.setDuration(this.animDuration);
		this.changeStatusColorAnimator.addPropertyToInterpolate("foreground", getLabelStatus().getForeground(), c);
		this.changeStatusColorAnimator.play();
	}
}
