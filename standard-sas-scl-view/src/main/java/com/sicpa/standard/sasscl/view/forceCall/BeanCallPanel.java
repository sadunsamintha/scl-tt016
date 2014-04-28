package com.sicpa.standard.sasscl.view.forceCall;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;

import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.gui.components.layeredComponents.lock.ui.LockUI;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeel;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;

public class BeanCallPanel extends JPanel {

	public static void main(String[] args) {
		SicpaLookAndFeel.install();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				f.setSize(500, 500);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				List<IBeanCall> beancalls = new ArrayList<IBeanCall>();
				beancalls.add(new BeanCall(new Object() {
					@Override
					public String toString() {
						ThreadUtils.sleepQuietly(1000);
						return super.toString();
					}
				}, "toString", "toString"));
				beancalls.add(new BeanCall(new Object(), "hashCode", "hashCode"));
				BeanCallPanel panel = new BeanCallPanel(beancalls);

				f.getContentPane().add(panel);

				f.setVisible(true);
			}
		});
	}

	private static final long serialVersionUID = 1L;
	protected List<IBeanCall> beancalls;
	protected JXLayer<JComponent> lockPanel;
	protected JPanel mainPanel;
	protected LockUI lockui;

	public BeanCallPanel(List<IBeanCall> beancalls) {
		this.beancalls = beancalls;
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		add(getLockPanel());

	}

	public JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel(new MigLayout("gap 15 15 15 15,wrap 2"));
			for (IBeanCall call : beancalls) {
				mainPanel.add(new ButtonCall(call), "grow");
			}
		}
		return mainPanel;
	}

	public JXLayer<JComponent> getLockPanel() {
		if (lockPanel == null) {
			lockPanel = new JXLayer<JComponent>(getMainPanel());
			lockui = new LockUI(lockPanel);
			lockPanel.setUI(lockui);
		}
		return lockPanel;
	}

	private class ButtonCall extends JButton {

		private static final long serialVersionUID = 1L;
		IBeanCall call;

		public ButtonCall(IBeanCall _call) {
			super(_call.getDescription());
			this.call = _call;

			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					OperatorLogger.log("Force Call Action - {}", call.getDescription());
					lockui.lock();
					TaskExecutor.execute(new Runnable() {
						@Override
						public void run() {
							call.run();
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									lockui.unLock();
								}
							});
						}
					});
				}
			});
		}
	}
}
