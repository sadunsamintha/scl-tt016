package com.sicpa.tt016.scl.remote.remoteservices;

import java.util.List;

import com.sicpa.tt016.common.dto.CodingActivationSessionDTO;
import com.sicpa.tt016.common.dto.EncoderInfoDTO;
import com.sicpa.tt016.common.dto.EncoderInfoResultDTO;
import com.sicpa.tt016.common.dto.EncoderSclDTO;
import com.sicpa.tt016.common.dto.ExportSessionDTO;
import com.sicpa.tt016.common.dto.IEjectionDTO;
import com.sicpa.tt016.common.dto.MaintenanceSessionDTO;
import com.sicpa.tt016.common.dto.NonCompliantSessionDTO;
import com.sicpa.tt016.common.dto.OfflineSessionDTO;
import com.sicpa.tt016.common.dto.SkuDTO;
import com.sicpa.tt016.common.security.authenticator.IMoroccoAuthenticator;
import com.sicpa.tt016.master.scl.exceptions.InternalException;
import com.sicpa.tt016.scl.remote.IDecoderDTO;

/**
 * facade for the EJB call
 */
public interface IRemoteServices {

	void login() throws Exception;

	boolean isAlive();

	String getBisUserPassword(String user);

	List<SkuDTO> getSkuList() throws InternalException;

	boolean isRefeedEnabled() throws InternalException;

	IMoroccoAuthenticator getDecoder();

	EncoderInfoResultDTO sendEncoderInfo(List<EncoderInfoDTO> info) throws InternalException;

	List<EncoderSclDTO> getRemoteEncoders(int encoderQty, int codeTypeId) throws InternalException;

	void sendOfflineProduction(OfflineSessionDTO data) throws InternalException;

	void sendExportProduction(ExportSessionDTO data) throws InternalException;

	void sendMaintenanceProduction(MaintenanceSessionDTO data) throws InternalException;

	void sendActivationData(int productionMode, CodingActivationSessionDTO activSession, List<IEjectionDTO> ejected)
			throws InternalException;

	void sendNonCompliantSession(List<NonCompliantSessionDTO> sessionList);

	long getSubsystemId();

}
