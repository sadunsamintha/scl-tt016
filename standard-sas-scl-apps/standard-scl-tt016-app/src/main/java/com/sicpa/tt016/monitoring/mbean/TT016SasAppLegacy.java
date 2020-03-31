package com.sicpa.tt016.monitoring.mbean;

import com.sicpa.standard.sasscl.devices.plc.PlcJmxInfo;
import com.sicpa.standard.sasscl.model.ProductionMode;
import com.sicpa.tt016.monitoring.mbean.mapping.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TT016SasAppLegacy implements TT016SasAppLegacyMBean {

    private TT016SasApp tt016SasApp;

    @Override
    public int getIsInProduction() {
        return tt016SasApp.getIsInProduction();
    }

    @Override
    public int getLineId() {
        return Math.toIntExact(tt016SasApp.getSubsystem());
    }

    @Override
    public int getNbValidProducts() {
        return tt016SasApp.getNbValidProducts();
    }

    @Override
    public int getNbInvalidProducts() {
        return tt016SasApp.getNbInkDetectedProducts();
    }

    @Override
    public int getNbInvalidProductsProducer() {
        return tt016SasApp.getNbProducerEjectedProducts();
    }

    @Override
    public int getNbInvalidProductsSicpa() {
        return tt016SasApp.getNbInvalidProducts();
    }

    @Override
    public String getStartTime() {
        return tt016SasApp.getApplicationLastRunningStartDate();
    }

    @Override
    public String getBatchIds() {
        return tt016SasApp.getEncoderID();
    }

    @Override
    public String getSku() {
        return tt016SasApp.getSKU();
    }

    @Override
    public int getProductionMode() {
        ProductionMode productionMode = tt016SasApp.getStats().getProductionParameters().getProductionMode();

        return productionMode != null
                ? LegacyToProductionModeIdProvider.getProductionModeId(productionMode.getId())
                : LegacyToProductionModeIdProvider.PRODUCTION_MODE_NOT_AVAILABLE;
    }

    @Override
    public String getWarnings() {
        return tt016SasApp.getWarnings();
    }

    @Override
    public String getErrors() {
        return tt016SasApp.getErrors();
    }

    @Override
    public String getDevicePrinterStatus() {
        String devicePrinterStatuses = tt016SasApp.getDevicePrinterStatus();

        return devicePrinterStatuses != null & !devicePrinterStatuses.isEmpty()
                ? convertDeviceStatusesToLegacy(tt016SasApp.getDevicePrinterStatus()) : "";
    }

    @Override
    public String getDeviceCameraStatus() {
        String deviceCameraStatuses = tt016SasApp.getDeviceCameraStatus();

        return deviceCameraStatuses != null && !deviceCameraStatuses.isEmpty()
                ? convertDeviceStatusesToLegacy(tt016SasApp.getDeviceCameraStatus()) : "";
    }

    @Override
    public byte getDevicePlcStatus() {
        return (byte) LegacyDevicesStatusIdProvider.getDeviceStatusId(tt016SasApp.getDevicePlcStatus());
    }

    @Override
    public byte getDeviceMasterStatus() {
        return (byte) LegacyDevicesStatusIdProvider.getDeviceStatusId(tt016SasApp.getDeviceMasterStatus());
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
        return tt016SasApp.getLastSucessfullSynchronisationWithRemoteServerDate();
    }

    @Override
    public Integer getLastSuccessfulSendingNumberOfProducts() {
        String lastSuccessfulSendingNumberOfProducts = tt016SasApp.getLastSucessfullSynchronisationWithRemoteServerProduct();

        return !lastSuccessfulSendingNumberOfProducts.isEmpty()
                ? Integer.valueOf(tt016SasApp.getLastSucessfullSynchronisationWithRemoteServerProduct().trim()) : 0;
    }

    @Override
    public String getLastSendingDate() {
        return tt016SasApp.getLastSynchronisationWithRemoteServerDate();
    }

    @Override
    public Integer getLastSendingNumberOfProducts() {
        String lastSendingNumberOfProducts = tt016SasApp.getLastSendingNumberOfProducts();

        return !lastSendingNumberOfProducts.isEmpty()
                ? Integer.valueOf(tt016SasApp.getLastSendingNumberOfProducts().trim()) : 0;
    }

    @Override
    public int getLastSendingStatus() {
        return LegacyRemoteServerSendingStatusIdProvider.getSendingStatusId(
                tt016SasApp.getLastSynchronisationWithRemoteServerStatus());
    }

    @Override
    public byte getApplicationStatus() {
        return (byte) LegacyApplicationStatusIdProvider.getApplicationStatusId(tt016SasApp.getApplicationStatus());
    }

    @Override
    public long getLastProductScanned() {
        return tt016SasApp.getLastProductScanned();
    }

    @Override
    public String getStopTime() {
        return tt016SasApp.getApplicationLastRunningStopDate();
    }
    
    @Override
    public String getSizeOfPackagedFolder() {
        return tt016SasApp.getSizeOfPackagedFolder();
    }
    
    @Override
    public String getSizeOfSentFolder() {
        return tt016SasApp.getSizeOfSentFolder();
    }
    
    @Override
    public String getSizeOfBufferFolder() {
        return tt016SasApp.getSizeOfBufferFolder();
    }

    @Override
    public String getSizeOfReleasedFolder() {
        return tt016SasApp.getSizeOfPackagedFolder();
    }

    @Override
    public String getReleasedFolderOldestFile() {
        return tt016SasApp.getPackagedFolderOldestFileDate();
    }

    @Override
    public int getNumberOfQuarantineProductionFile() {
        return tt016SasApp.getNumberOfQuarantineProductionFile();
    }

    @Override
    public String getSASSoftVersion() {
        return tt016SasApp.getAppVersion();
    }

    @Override
    public String getPLCSoftVersion() {
        return tt016SasApp.getPlcVersion();
    }

    @Override
    public int getApplicationInMaintenanceMode() {
        return isApplicationInMaintenanceMode() ? 1 : 0;
    }

    @Override
    public String getTrilightStatus() {
        String trilightStatuses = tt016SasApp.getTrilightStatus();

        return trilightStatuses != null && !trilightStatuses.isEmpty()
                && !trilightStatuses.equals(PlcJmxInfo.PLC_NOT_CONNECTED)
                ? convertTrilightStatusesToLegacy(tt016SasApp.getTrilightStatus()) : "";
    }

    public void setTt016SasApp(TT016SasApp tt016SasApp) {
		this.tt016SasApp = tt016SasApp;
	}

	private boolean isApplicationInMaintenanceMode() {
        ProductionMode productionMode = tt016SasApp.getStats().getProductionParameters().getProductionMode();

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
                .map(TT016SasAppLegacy::replaceDeviceStatusWithLegacyDeviceStatus)
                .collect(Collectors.joining("|"));
    }

    private static String replaceDeviceStatusWithLegacyDeviceStatus(String deviceStatus) {
        int deviceStatusValueStartIndex = deviceStatus.indexOf(":") + 1;
        String deviceStatusValue = deviceStatus.substring(deviceStatusValueStartIndex);

        return deviceStatus.substring(0, deviceStatusValueStartIndex)
                + LegacyDevicesStatusIdProvider.getDeviceStatusId(Integer.valueOf(deviceStatusValue));
    }
}
