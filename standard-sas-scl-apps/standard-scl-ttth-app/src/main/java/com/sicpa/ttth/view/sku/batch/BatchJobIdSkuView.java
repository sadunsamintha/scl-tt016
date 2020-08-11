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

public class BatchJobIdSkuView extends AbstractView<IBatchJobIdSkuListener, BatchJobIdSKUModel> {

    protected BatchJobIdSkuPanel batchJobIdSkuPanel;
    private int batchJobIdSize;

    public BatchJobIdSkuView() { }

    private void initGUI() {
        setLayout(new MigLayout("ltr,fill"));
        String strSKU = "";
        add(new JLabel(Messages.get("sku.batch.id.title") + " " + strSKU));
        add(new JSeparator(), "growx, pushx, wrap");
        add(getBatchJobIdSkuPanel(), "span, split 2, pushy, growx, growy");
    }

    public void refresh() {
        removeAll();
        this.batchJobIdSkuPanel = null;
        initGUI();
    }

    public BatchJobIdSkuPanel getBatchJobIdSkuPanel() {
        if (batchJobIdSkuPanel == null) {
            batchJobIdSkuPanel = new BatchJobIdSkuPanel();
        }
        return batchJobIdSkuPanel;
    }

    @Override
    public void modelChanged() { }

    public int getBatchJobIdSize() {
        return batchJobIdSize;
    }

    public void setBatchJobIdSize(int batchJobIdSize) {
        this.batchJobIdSize = batchJobIdSize;
    }

    @Subscribe
    public void handleLanguageSwitch(LanguageSwitchEvent evt) {
        refresh();
    }

    private class BatchJobIdSkuPanel extends JPanel {

        private JButton saveButton;
        private JPanel batchJobIdPanel;
        private JPanel buttonPanel;
        private JLabel batchJobIdLabel;
        private JTextField batchJobIdText;
        private DirectionButton buttonBack;

        public BatchJobIdSkuPanel() {
            initGui();
        }

        private void initGui() {
            setLayout(new MigLayout("ltr,fill"));
            add(getBatchJobIdPanel(), "push, grow, align center, wrap");
            add(getButtonsPanel(), "newline push, grow, align center, wrap");
        }

        private JButton getSaveButton() {
            if (saveButton == null) {
                saveButton = new JButton(Messages.get("sku.button.save.label"));
                saveButton.addActionListener(e -> saveSelected(batchJobIdText.getText()));
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

        public JLabel getBatchJobIdLabel() {
            if (batchJobIdLabel == null) {
                batchJobIdLabel = new JLabel(Messages.get("sku.batch.id.label"));
                batchJobIdLabel.setMinimumSize(new Dimension(70, 40));
                batchJobIdLabel.setForeground(SicpaColor.BLUE_DARK);
            }
            return batchJobIdLabel;
        }

        public JTextField getBatchJobIdText() {
            if (batchJobIdText == null) {
                batchJobIdText = new JTextField(getBatchJobIdSize());
                batchJobIdText.setMinimumSize(new Dimension(200, 40));
                batchJobIdText.setMaximumSize(new Dimension(200, 40));
                batchJobIdText.setBackground(SicpaColor.BLUE_ULTRA_LIGHT);
                VirtualKeyboardPanel virtualKeyboardPanel = VirtualKeyboardPanel.getNumericKeyboard(batchJobIdText);
                VirtualKeyboardPanel.attachKeyboardDialog(batchJobIdText, virtualKeyboardPanel);
            }
            return batchJobIdText;
        }

        public JPanel getBatchJobIdPanel() {
            if (batchJobIdPanel == null) {
                batchJobIdPanel = new JPanel();
                batchJobIdPanel.setLayout(new MigLayout("ltr,fill,wrap 2"));
                batchJobIdPanel.add(getBatchJobIdLabel(), "push, align right");
                batchJobIdPanel.add(getBatchJobIdText(), "push, align left");
            }
            return batchJobIdPanel;
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

    private void saveSelected(String strBatchJobId) {
        for (IBatchJobIdSkuListener listener : listeners) {
            listener.saveBatchJobId(strBatchJobId);
        }
    }

    private void returnToSelection() {
        for (IBatchJobIdSkuListener listener : listeners) {
            listener.returnToSelection();
        }
    }
}