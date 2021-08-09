package com.sicpa.tt016.view.resetBeam;


import com.sicpa.standard.client.common.view.mvc.AbstractObservableModel;

public class ResetBeamViewModel extends AbstractObservableModel {

    private boolean enable = true;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

}
