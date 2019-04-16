package com.sicpa.tt065.redlight;

import com.google.common.eventbus.Subscribe;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.i18n.Messages;
import com.sicpa.standard.client.common.messages.IMessageCodeMapper;
import com.sicpa.standard.gui.utils.ThreadUtils;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowState;
import com.sicpa.standard.sasscl.controller.flow.ApplicationFlowStateChangedEvent;
import com.sicpa.standard.sasscl.controller.flow.IFlowControl;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatus;
import com.sicpa.standard.sasscl.controller.hardware.HardwareControllerStatusEvent;
import com.sicpa.standard.sasscl.controller.view.event.ErrorViewEvent;

/**
 * This Controller will be responsible to check for errors that may stop the application to be on a "<i>ready state</i>", like device errors, etc.
 * It contains methods that subscribes to Hardware and View events which can be used to ascertain that the application stopped.
 * It'll write a red light log, counting the total amount of time which the application stayed inoperant, because of an error.
 */
public class TT065RedLightController {
  private IMessageCodeMapper messageCodeMapper;
  private IFlowControl flowControl;
  private Temporal errorTimePoint = null;
  private volatile RedLightMode currentMode = RedLightMode.OFF;

  private final static Logger LOGGER = LoggerFactory.getLogger(TT065RedLightController.class);
  private enum RedLightMode{ ON, OFF }

  public void setMessageCodeMapper(final IMessageCodeMapper messageCodeMapper) {
    this.messageCodeMapper = messageCodeMapper;
  }

  public void setFlowControl(IFlowControl flowControl) {
    this.flowControl = flowControl;
  }

  protected void initializeRedLight(final IEventHandler eventHandler){
    currentMode = RedLightMode.ON;
    errorTimePoint = LocalDateTime.now();
    final String errorMessage = eventHandler.getEventReason();
    logRedLight("System Halt. Reason: "+errorMessage);
  }


  /**
   * Process {@link HardwareControllerStatusEvent} filtering for events related to Hardware issues,
   * that set the application into recovering mode.
   * @param evt Hardware event to be analyzed
   */
  @Subscribe
  public void processHardwareEvent(final HardwareControllerStatusEvent evt) {
    ThreadUtils.invokeLater(() -> {
      try {
        if(currentMode.equals(RedLightMode.OFF) && hardwareWithIssues(evt)) {
          initializeRedLight(()->String.join(", ", evt.getErrors()));
        }
      } catch (Exception e) {
        LOGGER.error("Unable to process hardware event", e);
      }
    });
  }

  private boolean hardwareWithIssues(HardwareControllerStatusEvent evt) {
    final ApplicationFlowState currentFlowState = flowControl.getCurrentState();
    final HardwareControllerStatus hardwareStatus = evt.getStatus();

    return  !evt.getErrors().isEmpty() &&
            hardwareStatus.equals(HardwareControllerStatus.CONNECTING) &&
            (currentFlowState.equals(ApplicationFlowState.STT_RECOVERING)) || currentFlowState.equals(ApplicationFlowState.STT_CONNECTING);
  }

  /**
   * Process {@link ErrorViewEvent} that may occur during the execution, which will be displayed at SystemInfoView.
   * @param evt Event that represents an error/exception
   */
  @Subscribe
  public void processErrorEvent(final ErrorViewEvent evt) {
    ThreadUtils.invokeLater(() -> {
      try {
        if(currentMode.equals(RedLightMode.OFF)){
          initializeRedLight(()->{
            final StringBuilder fullMessage = new StringBuilder();
            if(evt.getCodeExt() !=null){
              fullMessage.append(" ");
              fullMessage.append(evt.getCodeExt());
            }
            fullMessage.append(this.messageCodeMapper.getMessageCode(evt.getMessageLangKey()));
            fullMessage.append(" - ");
            fullMessage.append(Messages.format(evt.getMessageLangKey(), evt.getParams()));
            return fullMessage.toString();
          });
        }
      } catch (Exception e) {
        LOGGER.error("Unable to process error event: " + evt.getMessageLangKey(), e);
      }
    });
  }

  /**
   * Process {@link ApplicationFlowStateChangedEvent} looking for events that represents
   * a connected state. This indicates the system has recovered from an issue and its ready to work again.
   * @param evt flow state change to be analyzed
   */
  @Subscribe
  public void processStateChanged(final ApplicationFlowStateChangedEvent evt) {
    ThreadUtils.invokeLater(() -> {
      final ApplicationFlowState currentState = evt.getCurrentState();
      if(recoveredAfterARedLight(currentState)){
        final LocalDateTime currentTimePoint = LocalDateTime.now();
        final Duration between = Duration.between(errorTimePoint, currentTimePoint);

        this.errorTimePoint = null;
        this.currentMode = RedLightMode.OFF;
        logRedLight("System Recovered. Red Light total time (in seconds): "+ between.getSeconds());
      }
    });
  }

  private boolean recoveredAfterARedLight(final ApplicationFlowState currentState){
    return currentState.equals(ApplicationFlowState.STT_CONNECTED) && currentMode.equals(RedLightMode.ON);
  }

  private void logRedLight(final String message) {
    LOGGER.error("[RED LIGHT "+currentMode.toString()+"] "+message);
  }
}