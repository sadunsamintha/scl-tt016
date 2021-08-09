package com.sicpa.tt016.scl.remote.remoteservices;

import com.sicpa.tt016.common.dto.ActivationSessionDTO;
import com.sicpa.tt016.common.dto.AgedWineSessionDTO;
import com.sicpa.tt016.common.dto.ExportSessionDTO;
import com.sicpa.tt016.common.dto.IEjectionDTO;
import com.sicpa.tt016.common.dto.MaintenanceSessionDTO;
import com.sicpa.tt016.common.dto.OfflineSessionDTO;
import com.sicpa.tt016.common.dto.SkuDTO;
import com.sicpa.tt016.common.security.authenticator.IMoroccoAuthenticator;
import com.sicpa.tt016.master.scl.exceptions.InternalException;

import java.util.List;

public interface ITT016RemoteServicesMSASLegacy {

	int PRODUCTION_MODE_STANDARD = 0;
	int PRODUCTION_MODE_REFEED = 7;

	void login() throws Exception;

	boolean isAlive();

	List<SkuDTO> getSkuList() throws InternalException;

	IMoroccoAuthenticator getDecoder();

	void sendOfflineProduction(OfflineSessionDTO data) throws InternalException;

	void sendExportProduction(ExportSessionDTO data) throws InternalException;
	
	void sendExportAgingProduction(AgedWineSessionDTO data) throws InternalException;

	void sendMaintenanceProduction(MaintenanceSessionDTO data) throws InternalException;

	void sendEjectedProduction(IEjectionDTO ejected) throws InternalException;

	void sendDomesticProduction(ActivationSessionDTO activSession) throws InternalException;

	void sendRefeedProduction(ExportSessionDTO activSession) throws InternalException;
	
	String getBisTrainerPassword(String user);

	int getSubsystemId();

}
