package com.sicpa.standard.sasscl.view.productionSending;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.common.util.Messages;
import com.sicpa.standard.gui.components.buttons.PaddedButton;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;

public class ProductionSendingView extends AbstractView<IProductionSendingViewListener, ProductionSendingViewModel> {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		SicpaLookAndFeel.install();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				JFrame f = new JFrame();
				f.setSize(800, 600);
				ProductionSendingView p = new ProductionSendingView();
				p.setModel(new ProductionSendingViewModel());
				f.getContentPane().add(p);

				f.setVisible(true);
			}
		});

	}

	protected JProgressBar bar;
	protected JButton buttonCancel;

	public ProductionSendingView() {
		this(new ProductionSendingViewModel());
	}

	public ProductionSendingView(ProductionSendingViewModel model) {
		setModel(model);
		initGui();
	}

	protected void initGui() {

		setLayout(new MigLayout("fill"));

		add(new JLabel(" "), " pushy,spanx");
		JLabel label = new JLabel(Messages.get("exit.production.sending"));
		label.setName("exit.production.sending");

		add(label, "wrap");
		add(getBar(), "pushx , grow, h 50,wrap");
		add(new PaddedButton(getButtonCancel()), "right, h 60!");
		add(new JLabel(" "), " pushy,spanx");
	}

	@Override
	public void modelChanged() {
		getBar().setMaximum(model.getBatchCount());
		getBar().setValue(model.getCurrentBatch());
		getBar().setString(model.getCurrentBatch() + "/" + model.getBatchCount());

	}

	public JProgressBar getBar() {
		if (bar == null) {
			bar = new JProgressBar();
			bar.setStringPainted(true);
			bar.setString("0/..");
		}
		return bar;
	}

	public JButton getButtonCancel() {
		if (buttonCancel == null) {
			buttonCancel = new JButton(Messages.get("exit.sending.production.cancelandexit"));
			buttonCancel.setName("exit.sending.production.cancelandexit");
			buttonCancel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonCancelActionPerformed();
				}
			});
		}
		return buttonCancel;
	}

	protected void buttonCancelActionPerformed() {
		fireCancelRequest();
	}

	protected void fireCancelRequest() {
		for (IProductionSendingViewListener l : listeners) {
			l.requestCancel();
		}
	}
}
