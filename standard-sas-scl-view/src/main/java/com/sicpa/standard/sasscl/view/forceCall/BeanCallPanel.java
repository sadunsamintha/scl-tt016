package com.sicpa.standard.sasscl.view.forceCall;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.jxlayer.JXLayer;

import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.utils.TaskExecutor;
import com.sicpa.standard.client.common.view.ISecuredComponentGetter;
import com.sicpa.standard.gui.components.layeredComponents.lock.ui.LockUI;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.common.log.OperatorLogger;
import com.sicpa.standard.sasscl.security.SasSclPermission;

public class BeanCallPanel extends JPanel implements ISecuredComponentGetter {

	private static final long serialVersionUID = 1L;
	protected List<IBeanCall> beancalls;
	protected JXLayer<JComponent> lockPanel;
	protected JPanel mainPanel;
	protected LockUI lockui;

	public BeanCallPanel() {
	}

	public void setBeancalls(List<IBeanCall> beancalls) {
		this.beancalls = beancalls;
	}

	public void init() {
		ThreadUtils.invokeAndWait(() -> {
			setLayout(new BorderLayout());
			add(getLockPanel());
		});
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

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Permission getPermission() {
		return SasSclPermission.BEAN_CALL;
	}

	@Override
	public String getTitle() {
		return "label.beanCall.title";
	}
}
