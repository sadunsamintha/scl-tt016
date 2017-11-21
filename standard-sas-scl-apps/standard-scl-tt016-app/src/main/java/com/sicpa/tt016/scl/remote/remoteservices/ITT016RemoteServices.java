package com.sicpa.tt016.scl.remote.remoteservices;

import com.sicpa.tt016.common.dto.*;
import com.sicpa.tt016.common.security.authenticator.IMoroccoAuthenticator;
import com.sicpa.tt016.master.scl.exceptions.InternalException;

import java.util.List;

public interface ITT016RemoteServices {

	int PRODUCTION_MODE_STANDARD = 0;
	int PRODUCTION_MODE_REFEED = 7;

	void login() throws Exception;

	boolean isAlive();

	List<SkuDTO> getSkuList() throws InternalException;

	IMoroccoAuthenticator getDecoder();

	EncoderInfoResultDTO sendEncoderInfo(List<EncoderInfoDTO> info) throws InternalException;

	List<EncoderSclDTO> getRemoteEncoders(int encoderQty, int codeTypeId) throws InternalException;

	void sendOfflineProduction(OfflineSessionDTO data) throws InternalException;

	void sendExportProduction(ExportSessionDTO data) throws InternalException;

	void sendMaintenanceProduction(MaintenanceSessionDTO data) throws InternalException;

	void sendEjectedProduction(IEjectionDTO ejected) throws InternalException;

	void sendDomesticProduction(CodingActivationSessionDTO activSession) throws InternalException;

	void sendRefeedProduction(CodingActivationSessionDTO activSession) throws InternalException;
	
	String getBisTrainerPassword(String user);

	int getSubsystemId();

	/**TODO
	 * 
	 * @param data
	 * @throws InternalException
	 */
	void sendRefeedProduction(RefeedSessionDTO data) throws InternalException;

	/**
	 *  TODO
	 * @param ejected
	 * @throws InternalException
	 */
	void sendRefeedEjectedProduction(IEjectionDTO ejected)
			throws InternalException;
}
