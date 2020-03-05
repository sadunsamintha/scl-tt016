package com.sicpa.tt016.monitoring.mbean;

public interface TT016SclAppLegacyMBean {

    /**
     * Return the current Application status.
     *
     * @return int<br>
     * <b>1</b> In production<br>
     * <b>0</b> Stopped<br>
     */
    int getIsInProduction();

    /**
     * Return the line identification. This identification is used by the client
     * for the Master authentication. It's although useful for the master to
     * know which line asked encoder/decoder/sku...
     *
     * @return int
     */
    int getLineId();

    /**
     * Return the current number of valid products scanned by the SCL
     * Application.
     *
     * @return int
     */
    int getNbValidProducts();

    /**
     * Return the current number of invalid products scanned by the SCL
     * Application.
     *
     * @return int
     */
    int getNbInvalidProducts();

    /**
     * Return the current number of invalid products (for the producer).
     *
     * @return int
     */
    int getNbInvalidProductsProducer();

    /**
     * Return the current number of invalid products. An invalid product is a
     * product without any ink on its cap.
     *
     * @return int
     */
    int getNbInvalidProductsSicpa();

    /**
     * Return the starting production date.
     *
     * @return String
     */
    String getStartTime();

    /**
     * Return the list of batchId used during the production (Usually max 2).
     *
     * @return String [batchId_1, BatchId_2]
     */
    String getBatchIds();

    /**
     * Return the sku description. A sku described the product currently scanned
     * by the SCL Application. It's unique for each product.
     *
     * @return String
     */
    String getSku();

    /**
     * Return the Production Mode.
     *
     * @return int<br>
     * <b>0</b> DOMESTIC : Products will be sold within the country<br>
     * <b>1</b> EXPORT : Products will be deliver outside the country<br>
     * <b>2</b> MAINTENANCE<br>
     * <b>7</b> REFEED<br>
     */
    int getProductionMode();

    /**
     * Return the list of warnings occured during the production. Warning format
     * : Each warning is surrounded by hook [WARNING1][WARNING1].
     *
     * @return String
     */
    String getWarnings();

    /**
     * Return the list of errors occured during the production Error format :
     * Each error is surrounded by hook [ERROR1][ERROR1].
     *
     * @return String
     */
    String getErrors();

    /**
     * Return the statuses of the printers.
     *
     * @return String
     * <p>
     * format: printer_id:status| <-- delimiter
     * <p>
     * example:
     * printer1:1|printer2:4
     * <p>
     * status value:<br>
     * <b>0</b> UNKNOWN status<br>
     * <b>1</b> Printer is CONNECTED<br>
     * <b>4</b> Printer is DISCONNECTED<br>
     */
    String getDevicePrinterStatus();

    /**
     * Return the statuses of the cameras.
     *
     * @return String
     * <p>
     * format: camera_id:status| <-- delimiter
     * <p>
     * example:
     * camera1:1|camera2:4
     * <p>
     * status value:<br>
     * <b>0</b> UNKNOWN status<br>
     * <b>1</b> Camera is CONNECTED<br>
     * <b>4</b> Camera is DISCONNECTED<br>
     */
    String getDeviceCameraStatus();

    /**
     * Return the status of the Plc.
     *
     * @return byte<br>
     * <b>0</b> Plc is UNKNOWN<br>
     * <b>1</b> Plc is OK<br>
     * <b>4</b> Plc is DISCONNECTED<br>
     */
    byte getDevicePlcStatus();

    /**
     * Return the status of the Master.
     *
     * @return byte<br>
     * <b>0</b> Master is UNKNOWN<br>
     * <b>1</b> Master is OK<br>
     * <b>4</b> Master is DISCONNECTED<br>
     */
    byte getDeviceMasterStatus();

    /**
     * Return the status of the BIS.
     *
     * @return byte<br>
     * <b>0</b> BIS is UNKNOWN<br>
     * <b>1</b> BIS is OK<br>
     * <b>4</b> BIS is DISCONNECTED<br>
     */
    byte getDeviceBisStatus();

    byte getDeviceBisCameraRightStatus();

    byte getDeviceBisCameraLeftStatus();

    byte getDeviceBisPlcStatus();

    String getLastBisUnexpectedProductionChangeWarningTime();

    String getBisUnknownSKUSinceWarning();

    String getBisTooManyUnknownSKUWarning();

    /**
     * Return the date of the last successful client sending.
     *
     * @return String
     */
    String getLastSuccessfulSendingDate();

    /**
     * Return the number of products of the last successful client sending.
     *
     * @return Integer
     */
    Integer getLastSuccessfulSendingNumberOfProducts();

    /**
     * Return the date of the last client sending.
     *
     * @return String
     */
    String getLastSendingDate();

    /**
     * Return the number of products of the last client sending.
     *
     * @return Integer
     */
    Integer getLastSendingNumberOfProducts();

    /**
     * Return the status of the last client sending.
     *
     * @return int<br>
     * <b>0</b> SUCCESS<br>
     * <b>1</b> FAILED<br>
     */
    int getLastSendingStatus();

    /**
     * Return the global status of the SCL Application.
     *
     * @return byte<br>
     * <p>
     * <b>0</b> INIT - Application is started but cannot start production
     * (SKU is not selected)<br>
     * <b>3</b> READY - Application is connected to devices and production can start<br>
     * <b>4</b> READY_BLOCKED - SKU is selected but Application cannot start production because
     * there's probably a device problem<br>
     * <b>7</b> RUNNING - Application is in production<br>
     * <b>8</b> SELECTION - SKU selection in progress. Application displays sku panel<br>
     * <b>11</b> CONNECTING - SKU selection is done. Application is connecting to devices<br>
     * <b>12</b> STARTING - Starting production in progress<br>
     * <b>13</b> STOPPING - Stopping production in progress<br>
     * <b>14</b> SELECTION_DONE_DISCONNECTING_DEVICES - SKU selection is done, disconnecting and reconnecting
     * devices<br>
     * <b>15</b> EXIT - Application is exiting<br>
     */
    byte getApplicationStatus();

    /**
     * Return the number of ms since the last product scanned.
     *
     * @return long
     */
    long getLastProductScanned();

    /**
     * Return time of production stopped.
     *
     * @return String
     */
    String getStopTime();
    
    /**
     * Return the size in byte of the packaged folder. This folder contains the
     * packaged production for sending
     *
     * @return String
     */
    String getSizeOfPackagedFolder();
    
    /**
     * Return the size in byte of the sent folder. This folder contains the
     * sent production
     *
     * @return String
     */
    String getSizeOfSentFolder();
    
    /**
     * Return the size in byte of the buffer folder. This folder contains the
     * production in buffer for packaging and sending
     *
     * @return String
     */
    String getSizeOfBufferFolder();
    
    /**
     * Return the size in byte of the release folder. This folder contains the
     * not-sent production
     *
     * @return String
     */
    String getSizeOfReleasedFolder();

    /**
     * Return the modification date of the oldest file from release folder.
     *
     * @return String
     */
    String getReleasedFolderOldestFile();

    /**
     * Return the number of files contained in the Quarantine folder.
     *
     * @return integer
     */
    int getNumberOfQuarantineProductionFile();

    /**
     * Return the SCL Software version
     *
     * @return String
     */
    String getSoftSCLVersion();

    /**
     * Return the PLC Software version
     *
     * @return String
     */
    String getPLCSoftVersion();

    /**
     * Variable to hold the state of app. in maintenance mode.
     *
     * @return String
     */
    int getApplicationInMaintenanceMode();

    /**
     * Returns the statuses of the tri-light for all the lines. <br>
     * <br>
     * 0 - All lights off<br>
     * 1 - Green<br>
     * 2 - Red<br>
     * 3 - Yellow<br>
     * 4 - Green and yellow<br>
     * 5 - Green blinking and yellow blinking<br>
     * 6 - Red and yellow<br>
     * 7 - Red blinking and yellow blinking<br>
     * <br>
     * Example: line1:1|line2:2
     */
    String getTrilightStatus();


}
