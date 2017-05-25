package com.sicpa.tt065.view.sku.batchId;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.ProductBatchIdProvider;
import com.sicpa.tt065.model.TT065ProductionMode;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * New view of the BatchId associated to the ProductionParameters variable
 *
 * @author mjimenez
 *
 */
@SuppressWarnings("serial")
public class BatchIdSkuView extends AbstractView<IBatchIdSkuListener, BatchIdSkuModel> implements ProductBatchIdProvider {

	protected BatchIdSkuPanel batchIdSkuPanel;
	private ProductionParameters productionParameters;
	private VirtualKeyboardPanel keyboard;

	public BatchIdSkuView(){

	}

	public BatchIdSkuView(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
		initGUI();
	}


	private void initGUI() {
		setLayout(new MigLayout("ltr,fill"));
		String strSKU = "";
		if (!(productionParameters.getProductionMode().equals(ProductionMode.MAINTENANCE) && productionParameters.getSku()==null)) {
			strSKU = productionParameters.getSku().getDescription();
		}
		add(new JLabel(Messages.get("sku.batch.id.title")+" "+strSKU));
		add(new JSeparator(), "growx, pushx, wrap");
		add(getBatchIdSkuPanel(), "span, split 2, pushy, growx, growy");
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
		frame.setContentPane(new BatchIdSkuView(new ProductionParameters()));
		frame.setVisible(true);
	}

	private class BatchIdSkuPanel extends JPanel{

		private JButton saveBatchIdButton;
		private JPanel contentPanel;
		private JLabel batchIdLabel;
		private JTextField batchIdText;
		private JLabel creditNoteLabel;
		private JTextField creditNoteText;

		public BatchIdSkuPanel() {
			initGui();
		}

		private void initGui() {
			setLayout(new MigLayout("ltr,fill"));
			/*add(getStopReasonButton(PRODUCT_CHANGE), "growx, growy");
			add(getStopReasonButton(END_OF_PRODUCTION), "growx, growy, wrap");
			add(getStopReasonButton(PURGE_PRODUCTION), "growx, growy");
			add(getStopReasonButton(PREVENTIVE_MAINTENANCE), "growx, growy, wrap");
			add(getStopReasonButton(CORRECTIVE_MAINTENANCE), "growx, growy");*/
			add(getContentPanel(),"growx, align center, wrap");
			add(getSaveBatchIdButton(),"align center");
		}


		private JButton getSaveBatchIdButton() {
			if (saveBatchIdButton==null) {
				saveBatchIdButton = new JButton(Messages.get("sku.batch.id.button.save.label"));
				saveBatchIdButton.addActionListener(e -> saveBatchIdSelected(batchIdText.getText(),creditNoteText == null?null:creditNoteText.getText()));
			}
			return saveBatchIdButton;
		}

		public JLabel getBatchIdLabel() {
			if (batchIdLabel == null) {
				batchIdLabel = new JLabel(Messages.get("sku.batch.id.label"));
				batchIdLabel.setMinimumSize(new Dimension(70,40));
				batchIdLabel.setForeground(SicpaColor.BLUE_DARK);
			}
			return batchIdLabel;
		}

		public JLabel getCreditNoteIdLabel() {
			if (creditNoteLabel == null) {
				creditNoteLabel = new JLabel(Messages.get("sku.credit.note.label"));
				creditNoteLabel.setMinimumSize(new Dimension(70,40));
				creditNoteLabel.setForeground(SicpaColor.BLUE_DARK);
			}
			return creditNoteLabel;
		}

		public JTextField getBatchIdText() {
			if (batchIdText == null) {
				batchIdText = new JTextField(Integer.parseInt(Messages.get("sku.batch.id.maximum.length")));
				batchIdText.setMinimumSize(new Dimension(200,40));
				batchIdText.setMaximumSize(new Dimension(200,40));
				batchIdText.setBackground(SicpaColor.BLUE_ULTRA_LIGHT);
				batchIdText.setText(productionParameters.getProperty(productionBatchId));
				//to associate a virtual keyboard to the BatchId Text Field
				VirtualKeyboardPanel virtualKeyboardPanel = VirtualKeyboardPanel.getDefaultKeyboard(batchIdText);
				String[] defaultLayout = new String[]{"1234567890", "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM", "-{del}"};
				virtualKeyboardPanel.setDefaultLayout(defaultLayout);
				VirtualKeyboardPanel.attachKeyboardDialog(batchIdText, virtualKeyboardPanel);
			}
			return batchIdText;
		}

		public JTextField getCreditNoteText() {
			if (creditNoteText == null) {
				creditNoteText = new JTextField(Integer.parseInt(Messages.get("sku.credit.note.id.maximum.length")));
				creditNoteText.setMinimumSize(new Dimension(200,40));
				creditNoteText.setMaximumSize(new Dimension(200,40));
				creditNoteText.setBackground(SicpaColor.BLUE_ULTRA_LIGHT);
//				creditNoteText.setText(productionParameters.getProperty(productionCreditNoteId));
				//to associate a virtual keyboard to the BatchId Text Field
				VirtualKeyboardPanel virtualKeyboardPanel = VirtualKeyboardPanel.getDefaultKeyboard(creditNoteText);
				String[] defaultLayout = new String[]{"1234567890", "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM", "-{del}"};
				virtualKeyboardPanel.setDefaultLayout(defaultLayout);
				VirtualKeyboardPanel.attachKeyboardDialog(creditNoteText, virtualKeyboardPanel);
			}
			return creditNoteText;
		}

		public JPanel getContentPanel() {
			if (contentPanel == null) {
				contentPanel = new JPanel();
				contentPanel.setLayout(new MigLayout("ltr,fill"));
				contentPanel.setForeground(SicpaColor.BLUE_DARK);
				contentPanel.add(getBatchIdLabel(),"align right");
				contentPanel.add(getBatchIdText(),"align left");
				//Just for REFEED MODE
				if (productionParameters.getProductionMode().equals(TT065ProductionMode.REFEED_STOCK)) {
					contentPanel.add(getCreditNoteIdLabel(), "align right");
					contentPanel.add(getCreditNoteText(), "align left");
				}
			}
			return contentPanel;
		}
	}

	private void saveBatchIdSelected(String strBatchId,String strCreditNoteId) {
		for (IBatchIdSkuListener listener : listeners) {
			listener.saveBatchId(strBatchId,strCreditNoteId);
		}
	}
}
