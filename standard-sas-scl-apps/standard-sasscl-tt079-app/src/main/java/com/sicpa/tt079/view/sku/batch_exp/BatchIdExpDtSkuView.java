package com.sicpa.tt079.view.sku.batch_exp;

import com.google.common.eventbus.Subscribe;
import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.gui.components.text.MultiLineLabel;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * New view of the BatchId associated to the ProductionParameters variable
 *
 */
@SuppressWarnings("serial")
public class BatchIdExpDtSkuView extends AbstractView<IBatchIdExpDtSkuListener, BatchIdExpDtSkuModel> {

	protected BatchIdSkuPanel batchIdSkuPanel;
	private ProductionParameters productionParameters;
	private int batchIdSize;
	private int expiryDateMaxBound;

	private final int MAX_EXPIRY_DATE_BOUND = 12;

	public BatchIdExpDtSkuView(){

	}

	public BatchIdExpDtSkuView(ProductionParameters productionParameters) {

	}

	private void initGUI() {
		setLayout(new MigLayout("ltr,fill"));
		String strSKU = "";
		if (productionParameters != null) {
			if (productionParameters.getProductionMode() != null
					&& !(productionParameters.getProductionMode()
					.equals(ProductionMode.MAINTENANCE) && productionParameters.getSku() == null)) {
				strSKU = productionParameters.getSku().getDescription();
			}
		}
		add(new MultiLineLabel(Messages.get("sku.batch.id.title")+" "+strSKU), "grow, spanx");
		add(new JSeparator(), "growx, pushx, wrap");
		add(getBatchIdSkuPanel(), "span, split 2, pushy, growx, growy");
	}

	public void refresh() {
		removeAll();
		this.batchIdSkuPanel = null;
		initGUI();
	}

	public BatchIdSkuPanel getBatchIdSkuPanel() {
		if (batchIdSkuPanel == null) {
			batchIdSkuPanel = new BatchIdSkuPanel();
		}
		return batchIdSkuPanel;
	}

	@Override
	public void modelChanged() {

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(650, 300);
		frame.setContentPane(new BatchIdExpDtSkuView(new ProductionParameters()));
		frame.setVisible(true);
	}


	public int getBatchIdSize() {
		return batchIdSize;
	}

	public void setBatchIdSize(int batchIdSize) {
		this.batchIdSize = batchIdSize;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	@Subscribe
	public void handleLanguageSwitch(LanguageSwitchEvent evt) {
		refresh();
	}

	public void setExpiryDateMaxBound(int expiryDateMaxBound) {
		this.expiryDateMaxBound = expiryDateMaxBound;
	}

	private class BatchIdSkuPanel extends JPanel{

		private JButton saveButton;
		private JPanel contentPanel;
		private JLabel batchIdLabel;
		private JTextField batchIdText;
		private JLabel expDtLabel;
		private JXDatePicker expDt;
		private JLabel invalidDateLabel;

		public BatchIdSkuPanel() {
			initGui();
		}

		private void initGui() {
			setLayout(new MigLayout("ltr,fill"));
			add(getContentPanel(),"growx, align center, wrap");
			add(getSaveButton(),"align center");
		}


		private JButton getSaveButton() {
			if (saveButton==null) {
				saveButton = new JButton(Messages.get("sku.button.save.label"));
				saveButton.addActionListener(e -> saveSelected(batchIdText.getText(),expDt.getDate()));
			}
			return saveButton;
		}

		public JLabel getBatchIdLabel() {
			if (batchIdLabel == null) {
				batchIdLabel = new JLabel(Messages.get("sku.batch.id.label"));
				batchIdLabel.setMinimumSize(new Dimension(70,40));
				batchIdLabel.setForeground(SicpaColor.BLUE_DARK);
			}
			return batchIdLabel;
		}

		public JLabel getExpDateLabel() {
			if (expDtLabel == null) {
				expDtLabel = new JLabel(Messages.get("sku.expdt.id.label"));
				expDtLabel.setMinimumSize(new Dimension(70,40));
				expDtLabel.setForeground(SicpaColor.BLUE_DARK);
			}
			return expDtLabel;
		}

		public JTextField getBatchIdText() {
			if (batchIdText == null) {
				batchIdText = new JTextField(getBatchIdSize());
				batchIdText.setMinimumSize(new Dimension(200,40));
				batchIdText.setMaximumSize(new Dimension(200,40));
				batchIdText.setBackground(SicpaColor.BLUE_ULTRA_LIGHT);
				//to associate a virtual keyboard to the BatchId Text Field
				VirtualKeyboardPanel virtualKeyboardPanel = VirtualKeyboardPanel.getDefaultKeyboard(batchIdText);
				String[] defaultLayout = new String[]{"1234567890", "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM", "-{del}"};
				virtualKeyboardPanel.setDefaultLayout(defaultLayout);
				VirtualKeyboardPanel.attachKeyboardDialog(batchIdText, virtualKeyboardPanel);
			}
			return batchIdText;
		}

		public JXDatePicker getExpDate() {
			if (this.expDt == null) {
				this.expDt = new JXDatePicker();
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				this.expDt.setFormats(dateFormat);
				this.expDt.setDate(new Date());
				this.expDt.setMinimumSize(new Dimension(200,40));
				this.expDt.setMaximumSize(new Dimension(200,40));
				this.expDt.setBackground(SicpaColor.BLUE_DARK);

				Calendar calendar = Calendar.getInstance();
				this.expDt.getMonthView().setLowerBound(calendar.getTime());

				calendar.add(Calendar.MONTH, 6);
				this.expDt.setDate(calendar.getTime());

				calendar.setTime(new Date());
				if (expiryDateMaxBound <= MAX_EXPIRY_DATE_BOUND) {
					calendar.add(Calendar.MONTH, expiryDateMaxBound);
				} else {
					calendar.add(Calendar.MONTH, MAX_EXPIRY_DATE_BOUND);
				}

				this.expDt.getMonthView().setUpperBound(calendar.getTime());

				this.expDt.addActionListener(e -> {
					if (expDt.getDate() == null) {
						getInvalidDateLabel().setVisible(true);
					} else {
						getInvalidDateLabel().setVisible(false);
					}
				});
			}
			return this.expDt;
		}

		public JLabel getInvalidDateLabel() {
			if (this.invalidDateLabel == null) {
				this.invalidDateLabel = new JLabel( Messages.get("sku.expdt.selection.invalid"));
				this.invalidDateLabel.setForeground(SicpaColor.RED);
				this.invalidDateLabel.setVisible(false);
			}
			return this.invalidDateLabel;
		}

		public JPanel getContentPanel() {
			if (contentPanel == null) {
				contentPanel = new JPanel();
				contentPanel.setLayout(new MigLayout("ltr,fill,wrap 2"));
				contentPanel.setForeground(SicpaColor.BLUE_DARK);
				contentPanel.add(getBatchIdLabel(),"align right");
				contentPanel.add(getBatchIdText(),"align left");
				contentPanel.add(getExpDateLabel(), "align right");
				contentPanel.add(getExpDate(), "align left");
				contentPanel.add(Box.createRigidArea((new Dimension(2, 0))), "align right");
				contentPanel.add(getInvalidDateLabel(), "align left");
			}
			return contentPanel;
		}
	}

	private void saveSelected(String strBatchId,Date expDt) {
		for (IBatchIdExpDtSkuListener listener : listeners) {
			listener.saveBatchIdAndExpDt(strBatchId,expDt);
		}
	}
}