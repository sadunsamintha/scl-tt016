package com.sicpa.tt065.devices.plc;

public class ReloadPLCGroupVarsEvent {
  private final String requester;

  public ReloadPLCGroupVarsEvent(String requester) {
    this.requester = requester;
  }

  public String getRequester() {
    return requester;
  }
}