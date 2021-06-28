package com.sicpa.tt080.scl.view.sku.batchId;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.gui.components.text.MultiLineLabel;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.ProductBatchIdProvider;

import com.sicpa.standard.sasscl.utils.ConfigUtilEx;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.miginfocom.swing.MigLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * New view of the BatchId associated to the ProductionParameters variable
 *
 */
@SuppressWarnings("serial")
@NoArgsConstructor
public class BatchIdSkuView extends AbstractView<IBatchIdSkuListener, BatchIdSkuModel> implements ProductBatchIdProvider {

	protected BatchIdSkuPanel batchIdSkuPanel;
	private ProductionParameters productionParameters;
	private VirtualKeyboardPanel keyboard;
	public final static Logger logger = LoggerFactory.getLogger(BatchIdSkuView.class);

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
		final MultiLineLabel skuLabel = new MultiLineLabel(Messages.get("sku.batch.id.title") + " " + strSKU);
		skuLabel.setMaximumSize(new Dimension(650, 40));
		add(skuLabel, "grow, spanx");
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

	public class BatchIdSkuPanel extends JPanel{

		private JButton saveBatchIdButton;
		private JPanel contentPanel;
		private JLabel batchIdLabel;
		@Getter
		private JTextField batchIdText;
		private JLabel creditNoteLabel;
		private JTextField creditNoteText;

		public BatchIdSkuPanel() {
			initGui();
		}

		private void initGui() {
			setLayout(new MigLayout("ltr,fill"));
			add(getContentPanel(),"growx, align center, wrap");
			add(getSaveBatchIdButton(),"align center");
		}


		public JButton getSaveBatchIdButton() {
			if (saveBatchIdButton==null) {
				saveBatchIdButton = new JButton(Messages.get("sku.batch.id.button.save.label"));
				saveBatchIdButton.addActionListener(e -> saveBatchIdSelected(batchIdText.getText()));
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

				Properties properties = new Properties();
				FileInputStream fin = null;
				String messageSize = "";
				try {
					File globalPropertiesFile = new ClassPathResource(ConfigUtilEx.GLOBAL_PROPERTIES_PATH).getFile();
					fin = new FileInputStream(globalPropertiesFile);
					properties.load(fin);
					messageSize = properties.getProperty("sku.batch.job.id.maximum.length");
				} catch (IOException e) {

					logger.error("Could not open global.proprieties file %s", e);

				} finally {

					if (fin != null) {
						try {
							fin.close();
						} catch (IOException e) {
						}
					}
				}

				batchIdText = new JTextField(Integer.parseInt(messageSize));
				batchIdText.setMinimumSize(new Dimension(200,40));
				batchIdText.setMaximumSize(new Dimension(200,40));
				batchIdText.setBackground(SicpaColor.BLUE_ULTRA_LIGHT);
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
				contentPanel.setLayout(new MigLayout("ltr,fill,wrap 2"));
				contentPanel.setForeground(SicpaColor.BLUE_DARK);
				contentPanel.add(getBatchIdLabel(),"align right");
				contentPanel.add(getBatchIdText(),"align left");
			}
			return contentPanel;
		}
	}

	private void saveBatchIdSelected(final String strBatchId) {
		listeners.stream().forEach(listener -> listener.saveBatchId(strBatchId));
	}
}
