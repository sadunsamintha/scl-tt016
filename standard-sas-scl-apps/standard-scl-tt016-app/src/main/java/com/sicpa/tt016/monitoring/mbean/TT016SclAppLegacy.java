package com.sicpa.tt016.monitoring.mbean;

import com.sicpa.standard.sasscl.devices.plc.PlcJmxInfo;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.tt016.monitoring.mbean.mapping.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TT016SclAppLegacy implements TT016SclAppLegacyMBean {

    private TT016SclApp tt016SclApp;

    @Override
    public int getIsInProduction() {
        return tt016SclApp.getIsInProduction();
    }

    @Override
    public int getLineId() {
        return Math.toIntExact(tt016SclApp.getSubsystem());
    }

    @Override
    public int getNbValidProducts() {
        return tt016SclApp.getNbValidProducts();
    }

    @Override
    public int getNbInvalidProducts() {
        return tt016SclApp.getNbInkDetectedProducts();
    }

    @Override
    public int getNbInvalidProductsProducer() {
        return tt016SclApp.getNbProducerEjectedProducts();
    }

    @Override
    public int getNbInvalidProductsSicpa() {
        return tt016SclApp.getNbInvalidProducts();
    }

    @Override
    public String getStartTime() {
        return tt016SclApp.getApplicationLastRunningStartDate();
    }

    @Override
    public String getBatchIds() {
        return tt016SclApp.getEncoderID();
    }

    @Override
    public String getSku() {
        return tt016SclApp.getSKU();
    }

    @Override
    public int getProductionMode() {
        ProductionMode productionMode = tt016SclApp.getStats().getProductionParameters().getProductionMode();

        return productionMode != null
                ? LegacyToProductionModeIdProvider.getProductionModeId(productionMode.getId())
                : LegacyToProductionModeIdProvider.PRODUCTION_MODE_NOT_AVAILABLE;
    }

    @Override
    public String getWarnings() {
        return tt016SclApp.getWarnings();
    }

    @Override
    public String getErrors() {
        return tt016SclApp.getErrors();
    }

    @Override
    public String getDevicePrinterStatus() {
        String devicePrinterStatuses = tt016SclApp.getDevicePrinterStatus();

        return devicePrinterStatuses != null & !devicePrinterStatuses.isEmpty()
                ? convertDeviceStatusesToLegacy(tt016SclApp.getDevicePrinterStatus()) : "";
    }

    @Override
    public String getDeviceCameraStatus() {
        String deviceCameraStatuses = tt016SclApp.getDeviceCameraStatus();

        return deviceCameraStatuses != null && !deviceCameraStatuses.isEmpty()
                ? convertDeviceStatusesToLegacy(tt016SclApp.getDeviceCameraStatus()) : "";
    }

    @Override
    public byte getDevicePlcStatus() {
        return (byte) LegacyDevicesStatusIdProvider.getDeviceStatusId(tt016SclApp.getDevicePlcStatus());
    }

    @Override
    public byte getDeviceMasterStatus() {
        return (byte) LegacyDevicesStatusIdProvider.getDeviceStatusId(tt016SclApp.getDeviceMasterStatus());
    }

    @Override
    public byte getDeviceBisStatus() {
        return 0;
    }

    @Override
    public byte getDeviceBisCameraRightStatus() {
        return 0;
    }

    @Override
    public byte getDeviceBisCameraLeftStatus() {
        return 0;
    }

    @Override
    public byte getDeviceBisPlcStatus() {
        return 0;
    }

    @Override
    public String getLastBisUnexpectedProductionChangeWarningTime() {
        return "";
    }

    @Override
    public String getBisUnknownSKUSinceWarning() {
        return "";
    }

    @Override
    public String getBisTooManyUnknownSKUWarning() {
        return "";
    }

    @Override
    public String getLastSuccessfulSendingDate() {
        return tt016SclApp.getLastSucessfullSynchronisationWithRemoteServerDate();
    }

    @Override
    public Integer getLastSuccessfulSendingNumberOfProducts() {
        String lastSuccessfulSendingNumberOfProducts = tt016SclApp.getLastSucessfullSynchronisationWithRemoteServerProduct();

        return !lastSuccessfulSendingNumberOfProducts.isEmpty()
                ? Integer.valueOf(tt016SclApp.getLastSucessfullSynchronisationWithRemoteServerProduct().trim()) : 0;
    }

    @Override
    public String getLastSendingDate() {
        return tt016SclApp.getLastSynchronisationWithRemoteServerDate();
    }

    @Override
    public Integer getLastSendingNumberOfProducts() {
        String lastSendingNumberOfProducts = tt016SclApp.getLastSendingNumberOfProducts();

        return !lastSendingNumberOfProducts.isEmpty()
                ? Integer.valueOf(tt016SclApp.getLastSendingNumberOfProducts().trim()) : 0;
    }

    @Override
    public int getLastSendingStatus() {
        return LegacyRemoteServerSendingStatusIdProvider.getSendingStatusId(
                tt016SclApp.getLastSynchronisationWithRemoteServerStatus());
    }

    @Override
    public byte getApplicationStatus() {
        return (byte) LegacyApplicationStatusIdProvider.getApplicationStatusId(tt016SclApp.getApplicationStatus());
    }

    @Override
    public long getLastProductScanned() {
        return tt016SclApp.getLastProductScanned();
    }

    @Override
    public String getStopTime() {
        return tt016SclApp.getApplicationLastRunningStopDate();
    }

    @Override
    public String getSizeOfReleasedFolder() {
        return tt016SclApp.getSizeOfPackagedFolder();
    }

    @Override
    public String getReleasedFolderOldestFile() {
        return tt016SclApp.getPackagedFolderOldestFileDate();
    }

    @Override
    public int getNumberOfQuarantineProductionFile() {
        return tt016SclApp.getNumberOfQuarantineProductionFile();
    }

    @Override
    public String getSoftSCLVersion() {
        return tt016SclApp.getAppVersion();
    }

    @Override
    public String getPLCSoftVersion() {
        return tt016SclApp.getPlcVersion();
    }

    @Override
    public int getApplicationInMaintenanceMode() {
        return isApplicationInMaintenanceMode() ? 1 : 0;
    }

    @Override
    public String getTrilightStatus() {
        String trilightStatuses = tt016SclApp.getTrilightStatus();

        return trilightStatuses != null && !trilightStatuses.isEmpty()
                && !trilightStatuses.equals(PlcJmxInfo.PLC_NOT_CONNECTED)
                ? convertTrilightStatusesToLegacy(tt016SclApp.getTrilightStatus()) : "";
    }

    public void setTt016SclApp(TT016SclApp tt016SclApp) {
        this.tt016SclApp = tt016SclApp;
    }

    private boolean isApplicationInMaintenanceMode() {
        ProductionMode productionMode = tt016SclApp.getStats().getProductionParameters().getProductionMode();

        return productionMode != null && productionMode.getId() == ProductionMode.MAINTENANCE.getId();
    }

    private static String convertTrilightStatusesToLegacy(String trilightStatuses) {
        return Stream.of(trilightStatuses.split("\\|"))
                .map(trilightStatusByLine -> trilightStatusByLine.replaceAll("\\[.*\\]",
                        ":" + getLegacyTrilightStatus(trilightStatusByLine)))
                .collect(Collectors.joining("|"));
    }

    private static String getLegacyTrilightStatus(String trilightStatusesLine) {
        String[] statuses = trilightStatusesLine.split(",");
        return String.valueOf(LegacyTrilightStatusIdProvider.getTrilightStatus(
                Character.getNumericValue(statuses[0].charAt(statuses[0].indexOf(":") + 1)),
                Character.getNumericValue(statuses[1].charAt(statuses[1].indexOf(":") + 1)),
                Character.getNumericValue(statuses[2].charAt(statuses[2].indexOf(":") + 1))));
    }

    private String convertDeviceStatusesToLegacy(String deviceStatuses) {
        return Stream.of(deviceStatuses.split("\\|"))
                .map(TT016SclAppLegacy::replaceDeviceStatusWithLegacyDeviceStatus)
                .collect(Collectors.joining("|"));
    }

    private static String replaceDeviceStatusWithLegacyDeviceStatus(String deviceStatus) {
        int deviceStatusValueStartIndex = deviceStatus.indexOf(":") + 1;
        String deviceStatusValue = deviceStatus.substring(deviceStatusValueStartIndex);

        return deviceStatus.substring(0, deviceStatusValueStartIndex)
                + LegacyDevicesStatusIdProvider.getDeviceStatusId(Integer.valueOf(deviceStatusValue));
    }
}
