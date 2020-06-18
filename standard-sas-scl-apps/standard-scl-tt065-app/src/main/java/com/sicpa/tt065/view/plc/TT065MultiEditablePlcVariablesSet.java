package com.sicpa.tt065.view.plc;

import com.google.common.eventbus.Subscribe;
import javax.swing.JButton;
import net.miginfocom.swing.MigLayout;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.gui.components.scroll.SmallScrollBar;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.devices.plc.event.PlcVarValueChangeEvent;
import com.sicpa.standard.sasscl.view.config.plc.MultiEditablePlcVariablesSet;

/**
 * Specialization of Standard Set. This set will work with a controller to
 * handle field value changes. Also save and discard buttons were added to
 * control when to send updated values to PLC
 */
public class TT065MultiEditablePlcVariablesSet extends MultiEditablePlcVariablesSet {
  protected JButton saveModificationsButton;
  protected JButton cancelModificationsButton;
  protected TT065MultiEditablePlcVariablesController editablePlcVariablesController;

  public void setEditablePlcVariablesController(final TT065MultiEditablePlcVariablesController editablePlcVariablesController) {
    this.editablePlcVariablesController = editablePlcVariablesController;
  }

  /**
   * Listen for {@link PlcVarValueChangeEvent} events. This will be used to set the main config interface
   * on a state where changes have been made on it's child fields.
   *
   * @param evt
   */
  @Subscribe
  public void handlePlcVarValueChangeEvent(final PlcVarValueChangeEvent evt) {
    this.cancelModificationsButton.setEnabled(true);
    this.saveModificationsButton.setEnabled(true);
  }

  protected void initGUI() {
    setLayout(new MigLayout("fill, inset 0 0 0 0, gap 0 0 0 0"));
    add(getButtonShowCameraImage(), "center, wrap, h 50");
    add(SmallScrollBar.createLayerSmallScrollBar(getScroll()), "push, grow, wrap");
    add(getSaveButton(), "split 2, center, h 50");
    add(getCancelButton(), "h 50");
  }

  protected JButton getSaveButton() {
    if (saveModificationsButton == null) {
      saveModificationsButton = new JButton(Messages.get("plc.config.buttons.save")); //""
      saveModificationsButton.addActionListener((evt) -> notifyPLCChanges());
      saveModificationsButton.setEnabled(false);
    }
    return this.saveModificationsButton;
  }

  protected JButton getCancelButton() {
    if (cancelModificationsButton == null) {
      cancelModificationsButton = new JButton(Messages.get("plc.config.buttons.discard")); //""
      cancelModificationsButton.addActionListener((evt) -> discardPLCChanges());
      cancelModificationsButton.setEnabled(false);
    }
    return this.cancelModificationsButton;
  }

  protected void notifyPLCChanges(){
    disableAllConfigButtons();
    editablePlcVariablesController.pushChangesToPLC();

    ThreadUtils.invokeLater(() -> {
      panelsLines.clear();
      handleLanguageSwitch(null);
      this.editablePlcVariablesController.reload();
    });
  }

  /**
   * Disable and schedule an interface and buffer clear.
   * This must be done as a invokeLater, running it immediately set
   * the interface on a broken state.
   */
  protected void discardPLCChanges(){
    disableAllConfigButtons();
    ThreadUtils.invokeLater(() -> {
      panelsLines.clear();
      handleLanguageSwitch(null);
      this.editablePlcVariablesController.clearChangesAndReload();
    });
  }

  protected void disableAllConfigButtons() {
    this.cancelModificationsButton.setEnabled(false);
    this.saveModificationsButton.setEnabled(false);
  }
}
