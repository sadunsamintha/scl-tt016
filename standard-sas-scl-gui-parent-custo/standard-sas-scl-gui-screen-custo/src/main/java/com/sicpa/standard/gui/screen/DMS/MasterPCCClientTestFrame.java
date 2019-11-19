package com.sicpa.standard.gui.screen.DMS;

import java.util.Properties;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.screen.DMS.log.LogScreen;
import com.sicpa.standard.gui.screen.DMS.log.UserLog;
import com.sicpa.standard.gui.screen.DMS.log.UserLog.EStatusLog;

public class MasterPCCClientTestFrame extends MasterPCCClientFrame {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SicpaLookAndFeelCusto.install();
				MasterPCCClientFrame inst = new MasterPCCClientTestFrame();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public MasterPCCClientTestFrame() {
		initGUI();
		setApplicationName("MY APPLICATION");
		setStatusBarLog(new UserLog("P00000000", "operation", "result", EStatusLog.failure));
	}

	private void initGUI() {

		JMenuBar bar = new JMenuBar();
		{
			JMenu menu = new JMenu("menu 1");
			JMenuItem item1 = new JMenuItem("item1");
			JMenuItem item2 = new JMenuItem("item2");
			menu.add(item1);
			menu.addSeparator();
			menu.add(item2);
			menu.addSeparator();

			JCheckBoxMenuItem i = new JCheckBoxMenuItem("check");
			menu.add(i);

			JRadioButtonMenuItem o = new JRadioButtonMenuItem("radio");
			menu.add(o);

			bar.add(menu);
		}
		{
			JMenu menu = new JMenu("menu 1");
			JMenuItem item1 = new JMenuItem("item1");
			JMenuItem item2 = new JMenuItem("item2");
			menu.add(item1);
			menu.add(item2);
			bar.add(menu);
		}
		{
			JMenu menu = new JMenu("menu 1");
			JMenuItem item1 = new JMenuItem("item1");
			JMenuItem item2 = new JMenuItem("item2");
			menu.add(item1);
			menu.add(item2);
			bar.add(menu);
		}
		{
			JMenu menu = new JMenu("menu 1");
			JMenuItem item1 = new JMenuItem("item1");
			JMenuItem item2 = new JMenuItem("item2");
			menu.add(item1);
			menu.add(item2);
			bar.add(menu);
		}

		setJMenuBar(bar);
		//
	}

	@Override
	public LogScreen getLogScreen() {
		return null;
	}

	@Override
	public Properties getLanguageProperties() {
		return null;
	}

	@Override
	public Properties getConfigProperties() {
		return null;
	}
}
