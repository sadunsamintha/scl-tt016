package com.sicpa.standard.gui.screen.machine.impl.SPL;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.components.dialog.dropShadow.DialogWithDropShadow;
import com.sicpa.standard.gui.listener.Draggable;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.machine.component.devicesStatus.DeviceStatus;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class PreviewSpl extends AbstractSplFrame {

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				PreviewSpl frame = new PreviewSpl();
				frame.setVisible(true);
				frame.setSize(1024, 768);
			}
		});
	}

	public void createAndShowDialog() {
		DialogWithDropShadow d = new DialogWithDropShadow(this, true, false);
		d.setTitle("Action available");
		d.getContentPane().setLayout(new MigLayout());
		// ----------------------------------------------
		JButton b = new JButton("add camera");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().addDevice("cam", "CAMERA");
			}
		});
		d.getContentPane().add(b, "");
		b = new JButton("add dms");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().addDevice("dms", "DMS");
			}
		});
		d.getContentPane().add(b, "");
		b = new JButton("add printer");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().addDevice("print", "PRINTER");
			}
		});
		d.getContentPane().add(b, "wrap");
		// -----------------------------------------------
		b = new JButton("camera ok");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().setDeviceStatus("cam", DeviceStatus.OK);
			}
		});
		d.getContentPane().add(b, "");
		b = new JButton("camera warning");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().setDeviceStatus("cam", DeviceStatus.WARNING);
			}
		});
		d.getContentPane().add(b, "");
		b = new JButton("camera error");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().setDeviceStatus("cam", DeviceStatus.ERROR);
			}
		});
		d.getContentPane().add(b, "wrap");

		// ---------------------------------
		b = new JButton("dms ok");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().setDeviceStatus("dms", DeviceStatus.OK);
			}
		});
		d.getContentPane().add(b, "");
		b = new JButton("dms warning");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().setDeviceStatus("dms", DeviceStatus.WARNING);
			}
		});
		d.getContentPane().add(b, "");
		b = new JButton("dms error");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().setDeviceStatus("dms", DeviceStatus.ERROR);
			}
		});
		d.getContentPane().add(b, "wrap");
		// ---------------------------------
		b = new JButton("printer ok");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().setDeviceStatus("print", DeviceStatus.OK);
			}
		});
		d.getContentPane().add(b, "");
		b = new JButton("printer warning");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().setDeviceStatus("print", DeviceStatus.WARNING);
			}
		});
		d.getContentPane().add(b, "");
		b = new JButton("printer error");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().setDeviceStatus("print", DeviceStatus.ERROR);
			}
		});
		d.getContentPane().add(b, "wrap");
		// ---------------------------------------

		b = new JButton("add warning");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().addWarning(System.currentTimeMillis() + "", "message", true);
			}
		});
		d.getContentPane().add(b, "");
		b = new JButton("add error");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				getController().addErrorMainPanel("key","text");
			}
		});
		d.getContentPane().add(b, "wrap");
		// ----------------------------------
		b = new JButton("system verison");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				int version = 0;
				while (version == 0) {
					version = (int) (Math.random() * 10);
				}

				getController().setVersion("0.0." + version + "-SNAPSHOT");
			}
		});
		d.getContentPane().add(b, "wrap");

		// ----
		d.pack();
		d.setVisible(true);

	}

	public PreviewSpl() {
		super(new SplViewController());

		Draggable.makeDraggable(this);
		initGUI();

		createAndShowDialog();
	}


	@Override
	protected JPanel[] getConfigPanels() {
		return new JPanel[] {};
	}

	@Override
	protected String[] getConfigPanelsTitle() {
		return new String[] {};
	}

	public boolean running = false;

	@Override
	protected AbstractAction getStartAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				PreviewSpl.this.running = true;

				new Thread(new Runnable() {
					@Override
					public void run() {
						while (PreviewSpl.this.running) {
							ThreadUtils.sleepQuietly((long) (Math.random() * 10));
							getController().addValidCodes();
						}
					}
				}).start();
				new Thread(new Runnable() {
					@Override
					public void run() {
						while (PreviewSpl.this.running) {
							ThreadUtils.sleepQuietly((long) (Math.random() * 50));
							getController().addInvalidCodes();
						}
					}
				}).start();
				new Thread(new Runnable() {
					@Override
					public void run() {
						while (PreviewSpl.this.running) {
							ThreadUtils.sleepQuietly((long) (Math.random() * 1000));
							getController().setLineSpeed((int) (Math.random() * 100));
						}
					}
				}).start();
			}
		};
	}

	@Override
	protected AbstractAction getStopAction() {
		return new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				PreviewSpl.this.running = false;
			}
		};
	}
}
