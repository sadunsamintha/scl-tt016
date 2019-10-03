package com.sicpa.tt080.scl;

import java.util.List;

import com.sicpa.standard.sasscl.MainAppWithProfile;

public class TT080MainAppWithProfile extends MainAppWithProfile {

  @Override
  protected List<String> createSpringFilesToLoad() {
    List<String> config =  super.createSpringFilesToLoad();
    config.remove("spring/plc/plc-import.groovy");
    return config;
  }

}
