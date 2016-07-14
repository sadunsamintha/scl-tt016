package com.sicpa.tt016.refeed;

/**
 * Class responsible to know if the refeed {@link com.sicpa.standard.sasscl.model.ProductionMode} is available in the system.
 */
public class TT016RefeedAvailabilityProvider {

    private boolean isRefeedAvailableInRemoteServer = false;

    // Ejection system
    private boolean isHeuftSystem = false;

    public boolean isRefeedAvailable() {
        return isRefeedAvailableInRemoteServer && isHeuftSystem;
    }

    public void setIsRefeedAvailableInRemoteServer(boolean isRefeedAvailableInRemoteServer) {
        this.isRefeedAvailableInRemoteServer = isRefeedAvailableInRemoteServer;
    }

    public void setIsHeuftSystem(boolean isHeuftSystem) {
        this.isHeuftSystem = isHeuftSystem;
    }


}
