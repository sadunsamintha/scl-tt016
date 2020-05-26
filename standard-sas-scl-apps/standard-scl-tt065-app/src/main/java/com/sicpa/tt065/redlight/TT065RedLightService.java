package com.sicpa.tt065.redlight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sicpa.standard.client.common.utils.TaskExecutor;

/**
 * Specialized service class to handle Red Lights. It will work as a main point to start and stop a red light.
 * This class will also keep track of the System's Red Light state.
 */
@Service
public class TT065RedLightService {
  private static final Logger LOGGER = LoggerFactory.getLogger(TT065RedLightService.class);
  private volatile RedLight redLight = null;
  private ITT065RemoteServer remoteServer;

  public void setRemoteServer(final ITT065RemoteServer itt065RemoteServer) {
    this.remoteServer = itt065RemoteServer;
  }

  /**
   * Start a Red Light. This will put the system on Red Light and send notifications to monitoring tools.
   * @param redLightReason The Reason of the red light, usually the error message that set the system into this state
   */
  public synchronized void startARedLight(final String redLightReason){
    if(isInRedLightState()){
      throw new IllegalStateException("Red Light already started");
    }
    this.redLight = RedLight.start(redLightReason);
    this.registerRedLightStateChange(redLight);
  }

  /**
   * Stops the Red Light. This will clean the red light and send notifications to monitoring tools.
   * Should be called only when the system is in Red Light, or it will throw an {@link IllegalStateException}
   */
  public void stopARedLight(){
    if(!isInRedLightState()){
      throw new IllegalStateException("Unable to stop RedLight. System is not in Red Light state");
    }
    final RedLight clonedRedLight = RedLight.of(this.redLight);
    this.redLight = null;
    clonedRedLight.toggleOff();
    this.registerRedLightStateChange(clonedRedLight);
  }

  private void registerRedLightStateChange(final RedLight redLight) {
    try{
      if(this.remoteServer.isConnected()) {
        TaskExecutor.execute(() -> this.remoteServer.sendRedLightInfoToGlobalMonitoringTool(redLight));
      }
    }finally {
      this.logRedLight(redLight);
    }
  }

  private void logRedLight(final RedLight redLight) {
    final String message = (redLight.isOn()) ?
        "System Halt. Reason: " + redLight.getDescription() :
        "System Recovered. Red Light total time (in millis): " + redLight.totalTime();
    final String status = redLight.isOn() ? "ON" : "OFF";
    LOGGER.error("[RED LIGHT {}] [{}] {}", status, redLight.getUid(), message);
  }

  /**
   * Responds whether the system is in Red light state or not.
   * @return TRUE if it's in Red Light state, FALSE otherwise
   */
  public boolean isInRedLightState(){
    return this.redLight != null;
  }
}
