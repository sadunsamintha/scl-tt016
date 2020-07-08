package com.sicpa.ttth.view.sku.batch;

import com.google.common.eventbus.Subscribe;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

public class BatchIdSkuView extends AbstractView<IBatchIdSkuListener, BatchIdSKUModel> {

    protected BatchIdSkuPanel batchIdSkuPanel;
    private int batchIdSize;

    public BatchIdSkuView() { }

    private void initGUI() {
        setLayout(new MigLayout("ltr,fill"));
        String strSKU = "";
        add(new JLabel(Messages.get("sku.batch.id.title") + " " + strSKU));
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
    public void modelChanged() { }

    public int getBatchIdSize() {
        return batchIdSize;
    }

    public void setBatchIdSize(int batchIdSize) {
        this.batchIdSize = batchIdSize;
    }

    @Subscribe
    public void handleLanguageSwitch(LanguageSwitchEvent evt) {
        refresh();
    }

    private class BatchIdSkuPanel extends JPanel {

        private JButton saveButton;
        private JPanel batchIdPanel;
        private JPanel buttonPanel;
        private JLabel batchIdLabel;
        private JTextField batchIdText;
        private DirectionButton buttonBack;

        public BatchIdSkuPanel() {
            initGui();
        }

        private void initGui() {
            setLayout(new MigLayout("ltr,fill"));
            add(getBatchIdPanel(), "push, grow, align center, wrap");
            add(getButtonsPanel(), "newline push, grow, align center, wrap");
        }

        private JButton getSaveButton() {
            if (saveButton == null) {
                saveButton = new JButton(Messages.get("sku.button.save.label"));
                saveButton.addActionListener(e -> saveSelected(batchIdText.getText()));
            }
            return saveButton;
        }

        public JButton getButtonBack() {
            if (this.buttonBack == null) {
                this.buttonBack = new DirectionButton(DirectionButton.Direction.LEFT);
                this.buttonBack.addActionListener(e -> returnToSelection());
                this.buttonBack.setMinimumSize(new Dimension(100, 40));
                this.buttonBack.setPreferredSize(new Dimension(100, 40));
                this.buttonBack.setMaximumSize(new Dimension(100, 40));
                this.buttonBack.setName("buttonBack");
            }

            return this.buttonBack;
        }

        public JLabel getBatchIdLabel() {
            if (batchIdLabel == null) {
                batchIdLabel = new JLabel(Messages.get("sku.batch.id.label"));
                batchIdLabel.setMinimumSize(new Dimension(70, 40));
                batchIdLabel.setForeground(SicpaColor.BLUE_DARK);
            }
            return batchIdLabel;
        }

        public JTextField getBatchIdText() {
            if (batchIdText == null) {
                batchIdText = new JTextField(getBatchIdSize());
                batchIdText.setMinimumSize(new Dimension(200, 40));
                batchIdText.setMaximumSize(new Dimension(200, 40));
                batchIdText.setBackground(SicpaColor.BLUE_ULTRA_LIGHT);
                VirtualKeyboardPanel virtualKeyboardPanel = VirtualKeyboardPanel.getNumericKeyboard(batchIdText);
                VirtualKeyboardPanel.attachKeyboardDialog(batchIdText, virtualKeyboardPanel);
            }
            return batchIdText;
        }

        public JPanel getBatchIdPanel() {
            if (batchIdPanel == null) {
                batchIdPanel = new JPanel();
                batchIdPanel.setLayout(new MigLayout("ltr,fill,wrap 2"));
                batchIdPanel.add(getBatchIdLabel(), "push, align right");
                batchIdPanel.add(getBatchIdText(), "push, align left");
            }
            return batchIdPanel;
        }

        public JPanel getButtonsPanel() {
            if (buttonPanel == null) {
                buttonPanel = new JPanel();
                buttonPanel.setLayout(new MigLayout("ltr,fill,wrap 2"));
                buttonPanel.add(getButtonBack(), "align left");
                buttonPanel.add(getSaveButton(), "align right");
            }
            return buttonPanel;
        }
    }

    private void saveSelected(String strBatchId) {
        for (IBatchIdSkuListener listener : listeners) {
            listener.saveBatchId(strBatchId);
        }
    }

    private void returnToSelection() {
        for (IBatchIdSkuListener listener : listeners) {
            listener.returnToSelection();
        }
    }
}