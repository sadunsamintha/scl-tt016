package com.sicpa.standard.gui.demo.EDT;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.utils.ThreadUtils;

public class EDTRepaintDemoFrame extends javax.swing.JFrame {

	private JProgressBar progress;
	private JButton startButtonWrong;
	private JButton startButtonWrong2;
	private JButton startButtonRight;

	public static void main(final String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				EDTRepaintDemoFrame inst = new EDTRepaintDemoFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public EDTRepaintDemoFrame() {
		super();
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			getContentPane().setLayout(new MigLayout("fill,wrap 1"));
			getContentPane().add(getProgress());
			getContentPane().add(getStartButtonWrong(), "sg 1");
			getContentPane().add(getStartButtonWrong2(), "sg 1");
			getContentPane().add(getStartButtonRight(), "sg 1");
			setSize(800, 200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JProgressBar getProgress() {
		if (this.progress == null) {
			this.progress = new JProgressBar();
			this.progress.setStringPainted(true);
		}
		return this.progress;
	}

	public JButton getStartButtonWrong() {
		if (this.startButtonWrong == null) {
			this.startButtonWrong = new JButton("start(BAD!)");
			this.startButtonWrong.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					startButtonWrongActionPerformed();

				}
			});
		}
		return this.startButtonWrong;
	}

	public JButton getStartButtonRight() {
		if (this.startButtonRight == null) {
			this.startButtonRight = new JButton("start(GOOD!)");
			this.startButtonRight.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					startButtonRightActionPerformed();

				}
			});
		}
		return this.startButtonRight;
	}

	public JButton getStartButtonWrong2() {
		if (this.startButtonWrong2 == null) {
			this.startButtonWrong2 = new JButton("start(STILL BAD!)");
			this.startButtonWrong2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					startButtonWrong2ActionPerformed();

				}
			});
		}
		return this.startButtonWrong2;
	}

	private void startButtonWrongActionPerformed() {
		// --- THIS CODE IS CALLED BY THE EDT (we are indeed in a swing event
		// handling method)

		// all this code will be executed before the next repaint will occur=>
		// the screen become frozen
		for (int i = 0; i <= 100; i++) {
			this.progress.setValue(i);
			ThreadUtils.sleepQuietly(25);
		}
	}

	private void startButtonWrong2ActionPerformed() {
		// --- THIS CODE IS CALLED BY THE EDT
		enableButton(false);
		Runnable task = new Runnable() {
			int i = 0;

			@Override
			public void run() {

				while (this.i <= 100) {

					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							// setValue(i) trigger a repaint of the progress bar
							// BUT
							// the repaint is put in the EDT queue
							// and it will only be executed when all the code in
							// this runnable
							// has been executed
							// so all the values won't be visible
							EDTRepaintDemoFrame.this.progress.setValue(i);// this value is not visible
							ThreadUtils.sleepQuietly(50);
							i++;
							EDTRepaintDemoFrame.this.progress.setValue(i);// this value is not visible
							ThreadUtils.sleepQuietly(50);
							i++;
							EDTRepaintDemoFrame.this.progress.setValue(i);

						}
					});

					ThreadUtils.sleepQuietly(200);
					this.i++;
				}
				enableButton(true);
			}
		};
		new Thread(task).start();
	}

	private void startButtonRightActionPerformed() {
		// --- THIS CODE IS CALLED BY THE EDT
		enableButton(false);
		Runnable task = new Runnable() {
			int i = 0;

			@Override
			public void run() {
				while (this.i <= 100) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							EDTRepaintDemoFrame.this.progress.setValue(i);
						}
					});
					// do something
					ThreadUtils.sleepQuietly(50);
					this.i++;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							EDTRepaintDemoFrame.this.progress.setValue(i);
						}
					});
					// do something
					ThreadUtils.sleepQuietly(50);
					this.i++;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							EDTRepaintDemoFrame.this.progress.setValue(i);
						}
					});
					// do something longer
					ThreadUtils.sleepQuietly(200);
					this.i++;
				}
				enableButton(true);

			}
		};
		new Thread(task).start();
	}

	private void enableButton(final boolean flag) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				EDTRepaintDemoFrame.this.startButtonRight.setEnabled(flag);
				EDTRepaintDemoFrame.this.startButtonWrong.setEnabled(flag);
				EDTRepaintDemoFrame.this.startButtonWrong2.setEnabled(flag);
			}
		});
	}
}
