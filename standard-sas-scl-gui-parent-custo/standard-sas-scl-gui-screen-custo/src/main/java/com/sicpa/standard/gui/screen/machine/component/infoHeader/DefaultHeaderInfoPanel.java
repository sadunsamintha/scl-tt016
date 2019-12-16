package com.sicpa.standard.gui.screen.machine.component.infoHeader;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.gui.model.BasicMapLikeModel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.gui.plaf.SicpaLookAndFeelCusto;
import com.sicpa.standard.gui.utils.TextUtils;
import com.sicpa.standard.gui.utils.ThreadUtils;

public class DefaultHeaderInfoPanel extends AbstractHeaderInfoPanel {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SicpaLookAndFeelCusto.install();
				JFrame f = new JFrame();
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				DefaultHeaderInfoPanel p = new DefaultHeaderInfoPanel();
				p.setLabels(new HeaderLabel[][] { { new HeaderLabel("prop1", "prop1:") },
						{ new HeaderLabel("prop2", "prop2:") } });
				p.getModel().setProperty("prop1", "PROP1VALUE");
				p.getModel().setProperty("prop2", "PROP2VALUE");

				f.getContentPane().add(p);
				f.pack();
				f.setVisible(true);
			}
		});
	}

	private HeaderLabel[][] labels;

	private Map<String, JLabel> mapLabelVal;

	public DefaultHeaderInfoPanel(final BasicMapLikeModel model, final HeaderLabel[][] labels) {
		super(model);
		this.labels = labels;
		this.mapLabelVal = new HashMap<String, JLabel>();
		initGUI();
	}

	public DefaultHeaderInfoPanel() {
		this(null, new HeaderLabel[][] {});
	}

	private void initGUI() {
		setLayout(new MigLayout("fill,inset 0 0 0 0"));
		addLabels();
		refreshLabelsVal();
	}

	private void addLabels() {
		if (this.labels == null) {
			return;
		}
		removeAll();
		boolean newLine = true;
		for (HeaderLabel[] labelTab : this.labels) {
			for (HeaderLabel label : labelTab) {
				JLabel val = new JLabel("-");
				val.setForeground(SicpaColor.BLUE_LIGHT);
				val.setName(label.getKey());
				JLabel text = new JLabel(label.getText());

				this.mapLabelVal.put(label.getKey(), val);

				add(text, (newLine ? "newline,spanx,split " + (labelTab.length * 2) + "" : ""));
				add(val, "");
				newLine = false;
			}
			newLine = true;
		}
	}

	public void setLabels(final HeaderLabel[][] labels) {
		this.labels = labels;

		for (HeaderLabel[] tab : labels) {
			for (HeaderLabel label : tab) {
				getModel().addAvailableProperty(label.getKey());
			}
		}

		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				addLabels();
				refreshLabelsVal();
			}
		});
	}

	@Override
	protected void modelPropertyChanged(final PropertyChangeEvent evt) {
		ThreadUtils.invokeLater(new Runnable() {
			@Override
			public void run() {
				JLabel label = DefaultHeaderInfoPanel.this.mapLabelVal.get(evt.getPropertyName());
				if (label != null) {
					label.setText(evt.getNewValue() + "");
				}
			}
		});
	}

	@Override
	public void setModel(final BasicMapLikeModel model) {
		super.setModel(model);
		refreshLabelsVal();
	}

	private void refreshLabelsVal() {
		if (this.mapLabelVal != null && this.mapLabelVal.size() > 0) {
			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					Map<String, Object> map = getModel().getAllProperties();
					for (Entry<String, Object> entry : map.entrySet()) {
						JLabel label = DefaultHeaderInfoPanel.this.mapLabelVal.get(entry.getKey());
						if (label != null) {
							label.setText(TextUtils.objectToString(entry.getValue()));
						}
					}
				}
			});
		}
	}
}
