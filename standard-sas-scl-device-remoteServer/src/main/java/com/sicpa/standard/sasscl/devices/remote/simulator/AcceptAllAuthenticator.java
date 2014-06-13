package com.sicpa.standard.sasscl.devices.remote.simulator;

import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;
import com.sicpa.standard.sasscl.sicpadata.reader.IDecodedResult;

public class AcceptAllAuthenticator implements IAuthenticator {

	private static final long serialVersionUID = 1L;

	protected transient ProductionParameters productionParameters;

    protected long counter = 0;

	public AcceptAllAuthenticator() {
	}

	@Override
	public IDecodedResult decode(String mode, String encryptedCode) throws CryptographyException {

		DecodedCameraCode res = new DecodedCameraCode();
		res.setCodeType(productionParameters.getSku().getCodeType());
		res.setBatchId(-1);
		res.setAuthenticated(true);
        try {
            res.setSequence(Long.parseLong(encryptedCode.trim()));
        } catch (Exception e) {
            res.setSequence(counter++);
        }

		return res;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}
}
