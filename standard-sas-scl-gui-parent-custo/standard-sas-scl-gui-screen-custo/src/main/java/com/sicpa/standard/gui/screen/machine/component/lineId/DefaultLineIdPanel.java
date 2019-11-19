package com.sicpa.standard.gui.screen.machine.component.lineId;

import java.beans.PropertyChangeEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.plaf.SicpaFont;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DefaultLineIdPanel extends AbstractLineIdPanel {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().setLayout(new MigLayout("fill"));

				DefaultLineIdPanel p = new DefaultLineIdPanel();
				p.getModel().setLineID("LINE ID");
				f.getContentPane().add(p, "grow");
				f.setSize(300, 300);
				f.setVisible(true);
			}
		});
	}

	private JLabel labelLineId;

	public DefaultLineIdPanel() {
		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("fill,inset 0 0 0 0"));
		add(getLabelLineId(), "grow");
		SicpaLookAndFeelCusto.flagAsHeaderOrFooter(this);
	}

	public JLabel getLabelLineId() {
		if (this.labelLineId == null) {
			this.labelLineId = new JLabel();
			this.labelLineId.setFont(SicpaFont.getFont(30));
			this.labelLineId.setName("lineIDLabel");
		}
		return this.labelLineId;
	}

	@Override
	protected void modelPropertyChanged(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("lineId")) {
			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					getLabelLineId().setText(evt.getNewValue() + "");
				}
			});
		}
	}

	@Override
	public void setModel(final LineIdModel model) {
		super.setModel(model);
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getLabelLineId().setText(getModel().getLineId());
			}
		});
	}
}
