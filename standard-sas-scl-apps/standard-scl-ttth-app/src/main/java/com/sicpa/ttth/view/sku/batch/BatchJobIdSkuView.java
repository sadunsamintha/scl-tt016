package com.sicpa.ttth.view.sku.batch;

import com.google.common.eventbus.Subscribe;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.security.Permission;
import com.sicpa.standard.client.common.security.SecurityService;
import com.sicpa.standard.client.common.view.mvc.AbstractView;
import com.sicpa.standard.gui.components.buttons.shape.DirectionButton;
import com.sicpa.standard.gui.components.text.MultiLineLabel;
import com.sicpa.standard.gui.components.virtualKeyboard.VirtualKeyboardPanel;
import com.sicpa.standard.gui.plaf.SicpaColor;
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.DailyBatchRequestRepository;
import com.sicpa.standard.sasscl.model.BatchJobHistory;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.view.LanguageSwitchEvent;

public class BatchJobIdSkuView extends AbstractView<IBatchJobIdSkuListener, BatchJobIdSKUModel> {

    private static final Permission MANUAL_BATCH_ENTRY = new Permission("MANUAL_BATCH_ENTRY");

    private static final int textFieldWidthSizeS = 30;

    private static final int textFieldWidthSizeM = 75;

    private static final int textFieldWidthSizeL = 105;

    private BatchJobIdSkuPanel batchJobIdSkuPanel;
    private DailyBatchRequestRepository dailyBatchRequestRepository;
    private ProductionParameters productionParameters;

    private String batchJobId;

    public BatchJobIdSkuView() { }

    private void initGUI() {
        setLayout(new MigLayout("ltr,fill"));
        String strSKU = "";
        add(new MultiLineLabel(Messages.get("sku.batch.id.title") + " " + strSKU), "grow, spanx");
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

    public void setDailyBatchRequestRepository(DailyBatchRequestRepository dailyBatchRequestRepository) {
        this.dailyBatchRequestRepository = dailyBatchRequestRepository;
    }

    public void setProductionParameters(ProductionParameters productionParameters) {
        this.productionParameters = productionParameters;
    }

    @Subscribe
    public void handleLanguageSwitch(LanguageSwitchEvent evt) {
        refresh();
    }

    private class BatchJobIdSkuPanel extends JPanel {

        private JPanel batchJobIdPanel;
        private JPanel manualBatchJobEntryPanel;
        private JPanel buttonPanel;
        private JButton offlineBatchJobIdButton;
        private JButton manualBatchJobIdButton;
        private ArrayList<JTextField> batchJobIdTextFields;
        private JButton autoBatchJobIdButton;
        private DirectionButton buttonBack;

        BatchJobIdSkuPanel() {
            initGui();
        }

        void initGui() {
            setLayout(new MigLayout("ltr,fill"));
            add(getBatchJobIdPanel(), "push, grow, align center, wrap");
            add(getNavButtonPanel(), "newline push, grow, align center, wrap");
            UIManager.put("OptionPane.cancelButtonText", Messages.get("std.gui.spinner.virtualKeyboard.cancel"));
            UIManager.put("OptionPane.okButtonText", Messages.get("std.gui.spinner.virtualKeyboard.ok"));
        }

        JButton getButtonBack() {
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

        ArrayList<JTextField> getBatchJobIdTextFields() {
            if (batchJobIdTextFields == null) {
                batchJobIdTextFields = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    JTextField batchSegTextField = new JTextField();
                    if (i == 0) {
                        //Prod Plan
                        batchSegTextField.setPreferredSize(new Dimension(textFieldWidthSizeL, 40));
                    } else if(i == 1 || i == 4) {
                        //Batch Job Seq || SKU ID
                        setTextFieldLimit(batchSegTextField, 4);
                        batchSegTextField.setPreferredSize(new Dimension(textFieldWidthSizeM, 40));
                    } else  if (i==2 || i ==6) {
                        //Line ID
                        setTextFieldLimit(batchSegTextField, 3);
                        batchSegTextField.setPreferredSize(new Dimension(textFieldWidthSizeM, 40));
                    } else if (i == 3) {
                        //Date
                        setTextFieldLimit(batchSegTextField, 6);
                        batchSegTextField.setPreferredSize(new Dimension(textFieldWidthSizeM, 40));
                    }  else {
                        //Mode
                        setTextFieldLimit(batchSegTextField, 1);
                        batchSegTextField.setPreferredSize(new Dimension(textFieldWidthSizeS, 40));
                    }

                    if (i == 0 || i == 5) {
                        VirtualKeyboardPanel virtualKeyboardPanel = VirtualKeyboardPanel.getQWERTYKeyboard(batchSegTextField);
                        VirtualKeyboardPanel.attachKeyboardDialog(batchSegTextField, virtualKeyboardPanel);
                    } else {
                        VirtualKeyboardPanel virtualKeyboardPanel = VirtualKeyboardPanel.getNumericKeyboard(batchSegTextField);
                        VirtualKeyboardPanel.attachKeyboardDialog(batchSegTextField, virtualKeyboardPanel);
                    }

                    batchSegTextField.setBackground(SicpaColor.BLUE_ULTRA_LIGHT);
                    batchJobIdTextFields.add(batchSegTextField);
                }
            }
            return batchJobIdTextFields;
        }

        JPanel getBatchJobEntryPanel() {
            if (manualBatchJobEntryPanel == null) {
                manualBatchJobEntryPanel = new JPanel();
                batchJobIdPanel.setLayout(new GridLayout(1, 5));
                getBatchJobIdTextFields().forEach(e -> manualBatchJobEntryPanel.add(e));
            }
            return manualBatchJobEntryPanel;
        }

        JButton getAutoBatchJobIdButton() {
            if (autoBatchJobIdButton == null) {
                autoBatchJobIdButton = new JButton(Messages.get("sku.button.batch.auto.mode"));
                if (productionParameters.getProductionMode().equals(ProductionMode.STANDARD)) {
                    if (!dailyBatchRequestRepository.getDailyBatchRequests().isEmpty()) {
                        ArrayList<String> choices = new ArrayList<>();
                        dailyBatchRequestRepository
                            .getDailyBatchRequests()
                            .forEach(e -> choices.add(e.getBatchJobId()));
                        autoBatchJobIdButton.addActionListener(e -> {
                            JOptionPane pane = getBatchSelectionPane(Messages.get("sku.button.batch.auto.mode"), choices.toArray());
                            JDialog dialog = pane.createDialog(this, Messages.get("sku.button.batch.auto.mode"));
                            dialog.setSize(380, 180);
                            dialog.setVisible(true);
                            dialog.dispose();
                            batchJobId = (String) pane.getInputValue();
                            if (batchJobId != null && !batchJobId.equals("uninitializedValue")) {
                                saveSelected(batchJobId);
                            }
                        });
                    } else {
                        autoBatchJobIdButton.addActionListener(e ->
                            JOptionPane.showMessageDialog(this, Messages.get("sku.batch.job.unavailable")));
                    }
                } else {
                    if (!dailyBatchRequestRepository.getBatchJobHistory().getDailyBatchHistory().isEmpty()) {
                        BatchJobHistory jobHistory = dailyBatchRequestRepository.getBatchJobHistory();
                        jobHistory.sortHistory();
                        ArrayList<String> choices = new ArrayList<>(jobHistory
                            .getDailyBatchHistory()
                            .keySet());
                        Collections.reverse(choices);
                        autoBatchJobIdButton.addActionListener(e -> {
                            JOptionPane pane = getBatchSelectionPane(Messages.get("sku.button.batch.auto.mode"), choices.toArray());
                            JDialog dialog = pane.createDialog(this, Messages.get("sku.button.batch.auto.mode.prompt"));
                            dialog.setSize(380, 180);
                            dialog.setVisible(true);
                            dialog.dispose();
                            batchJobId = (String) pane.getInputValue();
                            if (batchJobId != null && !batchJobId.equals("uninitializedValue")) {
                                if (productionParameters.getProductionMode().equals(ProductionMode.REFEED_CORRECTION)){
                                    //Manual flow for correction refeed.
                                    saveSelectedHist(batchJobId);
                                } else {
                                    //Normal flow for normal refeed.
                                    saveSelected(batchJobId);
                                }
                            }
                        });
                    } else {
                        autoBatchJobIdButton.addActionListener(e ->
                            JOptionPane.showMessageDialog(this, Messages.get("sku.batch.history.unavailable")));
                    }
                }

                autoBatchJobIdButton.setPreferredSize(new Dimension(180, 50));
            }
            return autoBatchJobIdButton;
        }

        JButton getOfflineBatchJobIDButton() {
            if (offlineBatchJobIdButton == null) {
                offlineBatchJobIdButton = new JButton(Messages.get("sku.button.batch.offline.mode"));
                offlineBatchJobIdButton.setPreferredSize(new Dimension(180, 50));
                offlineBatchJobIdButton.addActionListener(e -> {
                    getBatchJobIdTextFields().forEach(f -> f.setText(""));
                    setTextFieldLimit(getBatchJobIdTextFields().get(0), 8);
                    int option = JOptionPane.showConfirmDialog(this, getBatchJobEntryPanel(),
                        Messages.get("sku.button.batch.offline.mode"),
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
                    if (option == JOptionPane.YES_OPTION) {
                        acceptBatchInput(false);
                    }
                });
            }
            return offlineBatchJobIdButton;
        }

        JButton getManualBatchJobIdButton() {
            if (manualBatchJobIdButton == null) {
                manualBatchJobIdButton = new JButton(Messages.get("sku.button.batch.manual.mode"));
                manualBatchJobIdButton.setPreferredSize(new Dimension(180, 50));
                manualBatchJobIdButton.addActionListener(e -> {
                    getBatchJobIdTextFields().forEach(f -> f.setText(""));
                    setTextFieldLimit(getBatchJobIdTextFields().get(0), 5);
                    int option = JOptionPane.showConfirmDialog(this, getBatchJobEntryPanel(),
                        Messages.get("sku.button.batch.manual.mode"),
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
                    if (option == JOptionPane.YES_OPTION) {
                        acceptBatchInput(true);
                    }
                });

            }

            return manualBatchJobIdButton;
        }

        public JPanel getBatchJobIdPanel() {
            if (batchJobIdPanel == null) {
                batchJobIdPanel = new JPanel();
                batchJobIdPanel.setLayout(new MigLayout("ltr,fill,wrap 2"));
                batchJobIdPanel.add(Box.createRigidArea(new Dimension(0, 28)), "newline push, align center");
                batchJobIdPanel.add(getAutoBatchJobIdButton(), "newline push, align center");
                if (productionParameters.getProductionMode().equals(ProductionMode.STANDARD)) {
                    batchJobIdPanel.add(Box.createRigidArea(new Dimension(0, 18)), "newline push, align center");
                    batchJobIdPanel.add(getOfflineBatchJobIDButton(), "newline push, align center");
                    if (SecurityService.hasPermission(MANUAL_BATCH_ENTRY)) {
                        batchJobIdPanel.add(Box.createRigidArea(new Dimension(0, 18)), "newline push, align center");
                        batchJobIdPanel.add(getManualBatchJobIdButton(), "newline push, align center");
                    }
                }
            }
            return batchJobIdPanel;
        }

        JPanel getNavButtonPanel() {
            if (buttonPanel == null) {
                buttonPanel = new JPanel();
                buttonPanel.setLayout(new MigLayout("ltr,fill,wrap 2"));
                buttonPanel.add(getButtonBack(), "align left");
            }
            return buttonPanel;
        }

        JOptionPane getBatchSelectionPane(String title, Object[] choices) {
            JOptionPane pane = new JOptionPane(title, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION, null,
                null, null);
            pane.setWantsInput(true);
            pane.setSelectionValues(choices);
            pane.setInitialSelectionValue(choices[0]);
            pane.selectInitialValue();

            return pane;
        }

        void setTextFieldLimit(JTextField textField, int limit) {
            textField.setDocument(new BatchJobSegLimit(limit));
        }

        void acceptBatchInput(boolean isManual) {
            StringBuilder batchJobId = new StringBuilder();
            if (getBatchJobIdTextFields().stream().anyMatch(f -> f.getText().isEmpty())) {
                JOptionPane.showMessageDialog(this, Messages.get("sku.batch.id.validation.blank"));
                return;
            }
            //Prevent manual batches being used for offline mode.
            if (!isManual && getBatchJobIdTextFields().get(0).getText().length() < 7) {
                JOptionPane.showMessageDialog(this, Messages.get("sku.daily.batch.checksum.invalid"));
                return;
            }

            getBatchJobIdTextFields().forEach(f -> {
                batchJobId.append(f.getText());
                batchJobId.append("-");
            });
            batchJobId.deleteCharAt(batchJobId.length() -1);
            generateBatchJobId(batchJobId.toString());
        }
    }

    private class BatchJobSegLimit extends PlainDocument {
        private int limit;

        BatchJobSegLimit(int limit) {
            super();
            this.limit = limit;
        }

        public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
            if (str == null) return;

            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }

    private void saveSelected(String strBatchJobId) {
        for (IBatchJobIdSkuListener listener : listeners) {
            listener.saveBatchJobId(strBatchJobId);
        }
    }

    private void saveSelectedHist(String strBatchJobId) {
        for (IBatchJobIdSkuListener listener : listeners) {
            listener.saveBatchJobHist(strBatchJobId);
        }
    }

    private void generateBatchJobId(String batchJobId) {
        for (IBatchJobIdSkuListener listener : listeners) {
            listener.generateBatchJobId(batchJobId);
        }
    }

    private void returnToSelection() {
        for (IBatchJobIdSkuListener listener : listeners) {
            listener.returnToSelection();
        }
    }
}