package com.sicpa.tt065.redlight;

import com.sicpa.standard.sasscl.devices.remote.IRemoteServer;

public interface ITT065RemoteServer extends IRemoteServer {
  /**
   * @param redLight Red Light to be sent to the Remote Server Monitoring Tool
   */
  void sendRedLightInfoToGlobalMonitoringTool(final RedLight redLight);
}
