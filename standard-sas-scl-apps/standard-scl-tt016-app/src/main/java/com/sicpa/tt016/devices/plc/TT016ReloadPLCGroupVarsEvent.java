package com.sicpa.tt016.devices.plc;

public class TT016ReloadPLCGroupVarsEvent {
  private final String requester;

  public TT016ReloadPLCGroupVarsEvent(String requester) {
    this.requester = requester;
  }

  public String getRequester() {
    return requester;
  }
}