package com.sicpa.standard.sasscl.skucheck.simulator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.model.SkuCode;
import com.sicpa.standard.sasscl.skucheck.SkuCheckFacade;
import com.sicpa.standard.sasscl.skucheck.acquisition.AcquisitionGroupCompleteEvent;
import com.sicpa.standard.sasscl.skucheck.acquisition.IAcquisitionListener;
import com.sicpa.standard.sasscl.skucheck.checking.CheckingResultEvent;
import com.sicpa.standard.sasscl.skucheck.checking.ICheckingResultListener;

public class AcquisitionSimulator {

	private static final Logger logger = LoggerFactory.getLogger(AcquisitionSimulator.class);

	protected final SkuCheckFacade facade;
	protected JTextField text;

	protected JFrame frame;

	public AcquisitionSimulator(SkuCheckFacade facade) {
		this.facade = facade;
	}

	public void start(final int devicesCount) {

		facade.addAcquisitionListener(new IAcquisitionListener() {
			@Override
			public void groupAcquisitionComplete(AcquisitionGroupCompleteEvent evt) {
				logger.debug(evt.toString());
			}
		});

		facade.addCheckingListener(new ICheckingResultListener() {
			@Override
			public void checkComplete(CheckingResultEvent evt) {
				logger.debug(evt.toString());
			}
		});

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				if (frame == null) {
					frame = new JFrame();
					frame.getContentPane().setLayout(new MigLayout(""));
					text = new JTextField();
					frame.getContentPane().add(text, "spanx,growx");
					for (int i = 0; i < devicesCount; i++) {
						frame.getContentPane().add(new OneRowPanel(i), "spanx,grow");
					}
					frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					frame.pack();
				}
				frame.setVisible(true);
			}
		});
	}

	protected class OneRowPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		public OneRowPanel(final int i) {
			setLayout(new MigLayout());
			JButton button = new JButton("send " + i);
			JButton unread = new JButton("unread " + i);

			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					facade.addSku(i, new SkuCode(text.getText()));
				}
			});

			unread.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					facade.addUnread(i);
				}
			});

			add(button);
			add(unread);
		}
	}
}
