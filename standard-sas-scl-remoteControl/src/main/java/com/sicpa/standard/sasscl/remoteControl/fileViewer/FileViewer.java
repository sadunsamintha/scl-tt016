package com.sicpa.standard.sasscl.remoteControl.fileViewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.io.FileUtils;

import com.jidesoft.comparator.NumberComparator;
import com.sicpa.standard.common.log.StdLogger;
import com.sicpa.standard.gui.components.renderers.SicpaTableCellRenderer;
import com.sicpa.standard.gui.components.table.BeanReaderJTable;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.monitoring.mbean.sas.RemoteControlSasMBean;
import com.sicpa.standard.sasscl.monitoring.utils.FileInfo;
import com.sicpa.standard.sasscl.remoteControl.BackgroundTask;

public class FileViewer extends JPanel {

	private static StdLogger logger = new StdLogger(FileViewer.class);

	private static final long serialVersionUID = 1L;
	protected BeanReaderJTable<FileInfo> table;
	protected JButton buttonRefresh;
	protected JButton buttonShowParent;
	protected JLabel labelCurrentFolder;

	protected JPopupMenu popup;
	protected JMenuItem itemSaveContent;

	protected RemoteControlSasMBean controlBean;

	protected String currentFolder;

	public FileViewer(final String folder) {
		this.currentFolder = folder;
		initGUI();
	}

	public void setControlBean(final RemoteControlSasMBean controlBean) {
		this.controlBean = controlBean;
	}

	protected void initGUI() {
		setLayout(new MigLayout("fill"));
		add(getButtonRefresh(), "split 2");
		add(getButtonShowParent(), "wrap");
		add(getLabelCurrentFolder(), "wrap");
		add(new JScrollPane(getTable()), "spanx,grow,push");
	}

	public BeanReaderJTable<FileInfo> getTable() {
		if (this.table == null) {
			this.table = new BeanReaderJTable<FileInfo>(new String[] { "name", "size", "time", "directory" });
			this.table.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					tableMouseClicked(e);
				}
			});
			((DefaultRowSorter<?, ?>) this.table.getRowSorter()).setComparator(1, NumberComparator.getInstance());
			((DefaultRowSorter<?, ?>) this.table.getRowSorter()).setComparator(2, NumberComparator.getInstance());
			this.table.getColumnModel().getColumn(1).setCellRenderer(new FileSizeCellRenderer());
			this.table.getColumnModel().getColumn(2).setCellRenderer(new DateCellRenderer());

			this.table.getColumnModel().getColumn(0).setPreferredWidth(310);
			this.table.getColumnModel().getColumn(1).setPreferredWidth(40);
			this.table.getColumnModel().getColumn(2).setPreferredWidth(140);
			this.table.getColumnModel().getColumn(3).setPreferredWidth(45);
		}
		return this.table;
	}

	protected void tableMouseClicked(final MouseEvent evt) {
		if (SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2) {
			FileInfo info = getTable().getSelectedObject();
			String fileName = info.getParent() + File.separator + info.getName();
			if (info.isDirectory()) {
				queryFolderInfo(fileName);
			} else {
				queryFileContent(fileName);
			}

		}
		if (SwingUtilities.isRightMouseButton(evt)) {

			int row = getTable().rowAtPoint(evt.getPoint());
			getTable().getSelectionModel().setSelectionInterval(row, row);
			if (row != -1) {
				FileInfo fileInfo = getTable().getSelectedObject();
				if (!fileInfo.isDirectory()) {
					getPopup().show(getTable(), evt.getX(), evt.getY());
				}
			}
		}
	}

	public JButton getButtonRefresh() {
		if (this.buttonRefresh == null) {
			this.buttonRefresh = new JButton("refresh");
			this.buttonRefresh.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonRefreshActionPerformed();
				}
			});
		}
		return this.buttonRefresh;
	}

	protected void buttonRefreshActionPerformed() {
		queryFolderInfo(this.currentFolder);
	}

	protected void displayFolderInfo(final List<FileInfo> infos) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				getTable().clear();
				getTable().addRow(infos);
				getButtonShowParent().setVisible(new File(FileViewer.this.currentFolder).getParent() != null);
				getLabelCurrentFolder().setText(FileViewer.this.currentFolder);
			}
		});
	}

	protected void queryFolderInfo(final String fileName) {
		this.currentFolder = fileName;
		System.out.println(fileName);
		new BackgroundTask(new Runnable() {
			@Override
			public void run() {
				displayFolderInfo(FileViewer.this.controlBean.getFolderInfo(fileName));
			}
		}).start();
	}

	protected void queryFileContent(final String fileName) {
		new BackgroundTask(new Runnable() {
			@Override
			public void run() {
				final String content = new String(FileViewer.this.controlBean.getFileContent(fileName));
				displayFileContent(content, fileName);
			}
		}).start();
	}

	protected void displayFileContent(final String content, final String title) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JFrame f = new JFrame();
				f.getContentPane().setLayout(new BorderLayout());
				JTextArea area = new JTextArea();
				area.setText(content);
				f.getContentPane().add(new JScrollPane(area));
				f.setSize(800, 600);
				f.setTitle(title);
				f.setVisible(true);
			}
		});
	}

	public JButton getButtonShowParent() {
		if (this.buttonShowParent == null) {
			this.buttonShowParent = new JButton("Parent Folder");
			this.buttonShowParent.setVisible(false);
			this.buttonShowParent.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					buttonShowParentActionPerformed();
				}
			});
		}
		return this.buttonShowParent;
	}

	protected void buttonShowParentActionPerformed() {
		File parent = new File(this.currentFolder).getParentFile();
		if (parent != null) {
			queryFolderInfo(parent.getPath());
		}
	}

	public JLabel getLabelCurrentFolder() {
		if (this.labelCurrentFolder == null) {
			this.labelCurrentFolder = new JLabel(this.currentFolder);
		}
		return this.labelCurrentFolder;
	}

	protected static class FileSizeCellRenderer extends SicpaTableCellRenderer {
		protected static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(final JTable arg0, final Object value, final boolean arg2,
				final boolean arg3, final int arg4, final int arg5) {
			String size = FileUtils.byteCountToDisplaySize((Long) value);
			return super.getTableCellRendererComponent(arg0, size, arg2, arg3, arg4, arg5);
		}
	}

	protected static class DateCellRenderer extends SicpaTableCellRenderer {
		protected static final long serialVersionUID = 1L;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		@Override
		public Component getTableCellRendererComponent(final JTable arg0, final Object value, final boolean arg2,
				final boolean arg3, final int arg4, final int arg5) {
			String date = this.format.format(new Date((Long) value));
			return super.getTableCellRendererComponent(arg0, date, arg2, arg3, arg4, arg5);
		}
	}

	public JPopupMenu getPopup() {
		if (this.popup == null) {
			this.popup = new JPopupMenu();
			this.popup.add(getItemSaveContent());
		}
		return this.popup;
	}

	public JMenuItem getItemSaveContent() {
		if (this.itemSaveContent == null) {
			this.itemSaveContent = new JMenuItem("Download and save content");
			this.itemSaveContent.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					itemSaveContentActionPerformed();
				}
			});
		}
		return this.itemSaveContent;
	}

	protected JFileChooser chooser;

	protected void itemSaveContentActionPerformed() {
		if (this.chooser == null) {
			this.chooser = new JFileChooser(".");
		}
		if (this.chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			final File f = this.chooser.getSelectedFile();
			final String name = this.currentFolder + "/" + getTable().getSelectedObject().getName();
			new BackgroundTask(new Runnable() {
				@Override
				public void run() {
					byte[] content = FileViewer.this.controlBean.getFileContent(name);
					try {
						FileUtils.writeByteArrayToFile(f, content);
					} catch (IOException e) {
						logger.error("", e);
					}
				}
			}).start();
		}
	}
}
