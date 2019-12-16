package com.sicpa.standard.gui.components.preview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import com.sicpa.standard.gui.utils.ThreadUtils;

public class PreviewDialog extends JDialog {
	private PreviewPager pager;

	public PreviewDialog() {
		initGUI();
	}

	public PreviewDialog(final Component parent, final PreviewPager pager) {
		this.pager = pager;
		initGUI();
		Point p = new Point(parent.getLocation());
		SwingUtilities.convertPointToScreen(p, parent);
		setBounds(p.x, p.y, parent.getWidth(), parent.getHeight());
	}

	private void initGUI() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getPager());

		addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(final FocusEvent e) {
				thisFocusLost();
			}
		});
	}

	private void thisFocusLost() {
		this.pager = null;
		this.pcs.clear();
		dispose();
	}

	public PreviewPager getPager() {
		return this.pager;
	}

	public void setComponentsForPreview(final List<JComponent> comps, final List<String> titles) {
		ArrayList<Preview> previews = new ArrayList<Preview>();

		Preview p;
		int i = 0;
		for (JComponent c : comps) {
			p = new Preview();
			previews.add(p);
			p.setComp(c);
			p.addMouseListener(new PreviewMouseListener(p, this));
			p.setTitle(titles.get(i));
			i++;
		}
		this.pager.setPreviews(previews);
	}

	protected void previewMouseClicked(final Preview preview) {
		setVisible(false);
		for (PreviewCallback pc : this.pcs) {
			pc.callback(preview.getComp());
		}
		this.pager = null;
		this.pcs.clear();
		dispose();
	}

	List<PreviewCallback> pcs;

	public void addSelectionCallBack(final PreviewCallback pc) {
		if (this.pcs == null) {
			this.pcs = new ArrayList<PreviewCallback>();
		}
		this.pcs.add(pc);
	}

	private static class PreviewMouseListener extends MouseAdapter {
		Preview p;
		PreviewDialog dialog;

		public PreviewMouseListener(final Preview p, final PreviewDialog dialog) {
			this.p = p;
			this.dialog = dialog;
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			this.dialog.previewMouseClicked(this.p);
		}
	}

	public static void showPreviewDialog(final Component parent, final List<JComponent> comps,
			final List<String> titles, final PreviewCallback pc, final PreviewPager pager) {
		PreviewDialog p = new PreviewDialog(parent, pager);
		p.addSelectionCallBack(pc);
		p.setComponentsForPreview(comps, titles);
		p.setVisible(true);
		p.createPreview();
	}

	public static void showPreviewDialog(final Component parent, final List<JComponent> comps,
			final List<String> titles, final PreviewCallback pc) {
		showPreviewDialog(parent, comps, titles, pc, new GridPreviewPager());
	}

	private void createPreview() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (Preview p : PreviewDialog.this.pager.previews) {
					final Preview preview=p;
					ThreadUtils.invokeLater(new Runnable() {
						@Override
						public void run() {
//							preview.setSize(200, 200);
							preview.refreshPreview();
							preview.repaint();
						}
					});

					ThreadUtils.sleepQuietly(100);
				}
			}
		}).start();
	}
}
