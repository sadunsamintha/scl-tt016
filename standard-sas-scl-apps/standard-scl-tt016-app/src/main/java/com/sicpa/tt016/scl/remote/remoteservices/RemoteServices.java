package com.sicpa.tt016.scl.remote.remoteservices;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.tt016.common.dto.CodingActivationSessionDTO;
import com.sicpa.tt016.common.dto.EncoderInfoDTO;
import com.sicpa.tt016.common.dto.EncoderInfoResultDTO;
import com.sicpa.tt016.common.dto.EncoderSclDTO;
import com.sicpa.tt016.common.dto.ExportSessionDTO;
import com.sicpa.tt016.common.dto.IEjectionDTO;
import com.sicpa.tt016.common.dto.MaintenanceSessionDTO;
import com.sicpa.tt016.common.dto.NonActivationSessionDTO;
import com.sicpa.tt016.common.dto.NonCompliantSessionDTO;
import com.sicpa.tt016.common.dto.OfflineSessionDTO;
import com.sicpa.tt016.common.dto.SkuDTO;
import com.sicpa.tt016.common.model.CodeType;
import com.sicpa.tt016.common.security.CustomPrincipal;
import com.sicpa.tt016.common.security.authenticator.IMoroccoAuthenticator;
import com.sicpa.tt016.master.scl.business.interfaces.IBisUserManagerRemote;
import com.sicpa.tt016.master.scl.business.interfaces.ICodingActivationRemote;
import com.sicpa.tt016.master.scl.exceptions.InternalException;
import com.sicpa.tt016.scl.remote.TT016RemoteServer;
import com.sicpa.tt016.scl.remote.IDecoderDTO;
import com.sicpa.tt016.scl.remote.IEncoderDTO;
import com.sicpa.tt016.scl.remote.dao.ProductionModeDAO;
import com.sicpa.tt016.scl.remote.dto.DecoderDTO;

public class RemoteServices implements IRemoteServices {

	private static final Logger logger = LoggerFactory.getLogger(RemoteServices.class);
	private static final String BIS_SERVICE_NAME = "BisUserManagerBean";
	private static final String CODING_SERVICE_NAME = "CodingActivationBean/remote";

	private IBisUserManagerRemote bisUserManager;
	private ICodingActivationRemote codingActivation;

	private String userMachine;
	private String passwordMachine;
	private int subsystemId;
	// java.naming.provider.url=jnp://master-scl:1099
	private String ip;

	private Context context;

	@Override
	public void login() throws Exception {
		logger.info("connecting to mpcc using:" + userMachine);
		loadInitialContext();
		loadRemoteService();
	}

	private void loadRemoteService() throws NamingException {
		bisUserManager = (IBisUserManagerRemote) locateService(BIS_SERVICE_NAME);
		codingActivation = (ICodingActivationRemote) locateService(CODING_SERVICE_NAME);
	}

	@Override
	public long getSubsystemId() {
		return subsystemId;
	}

	@Override
	public boolean isAlive() {
		return codingActivation.isAlive(subsystemId);
	}

	@Override
	public String getBisUserPassword(String user) {
		return bisUserManager.getPassword(subsystemId, user);
	}

	@Override
	public List<SkuDTO> getSkuList() throws InternalException {
		return codingActivation.getSKU(subsystemId);
	}

	@Override
	public boolean isRefeedEnabled() throws InternalException {
		return codingActivation.isRefeedMode(subsystemId);
	}

	@Override
	public IMoroccoAuthenticator getDecoder() {
		return codingActivation.getAuthenticator(subsystemId);
	}

	@Override
	public EncoderInfoResultDTO sendEncoderInfo(List<EncoderInfoDTO> info) throws InternalException {
		EncoderInfoResultDTO res = codingActivation.sendEncodersInfo(subsystemId, info);
		return res;
	}

	@Override
	public List<EncoderSclDTO> getRemoteEncoders(int encoderQty, int codeTypeId) throws InternalException {
		List<EncoderSclDTO> wrappers = null;
		wrappers = codingActivation.getSclEncoders(encoderQty, new CodeType(codeTypeId), subsystemId);
		return wrappers;
	}

	@Override
	public void sendOfflineProduction(OfflineSessionDTO data) throws InternalException {
		sendCountProduction(data);
	}

	@Override
	public void sendMaintenanceProduction(MaintenanceSessionDTO data) throws InternalException {
		sendCountProduction(data);
	}

	@Override
	public void sendExportProduction(ExportSessionDTO data) throws InternalException {
		sendCountProduction(data);
	}

	private void sendCountProduction(NonActivationSessionDTO data) throws InternalException {
		codingActivation.sendProductionQty(data, subsystemId);
	}

	@Override
	public void sendActivationData(int productionMode, CodingActivationSessionDTO activSession,
			List<IEjectionDTO> ejected) throws InternalException {
		codingActivation.sendProductionData(productionMode, activSession, ejected, subsystemId);
	}

	@Override
	public void sendNonCompliantSession(List<NonCompliantSessionDTO> sessionList) {
		codingActivation.saveNonCompliantData(sessionList);
	}

	private Object locateService(String name) throws NamingException {
		return context.lookup(name);
	}

	public void loadInitialContext() throws NamingException {
		Properties jndiProperties = new Properties();
		jndiProperties.put("java.naming.security.principal", new CustomPrincipal(userMachine, subsystemId));
		jndiProperties.put("java.naming.security.credentials", passwordMachine);
		jndiProperties.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
		jndiProperties.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
		jndiProperties.put("java.naming.provider.url", ip);
		context = new InitialContext(jndiProperties);
	}

}
