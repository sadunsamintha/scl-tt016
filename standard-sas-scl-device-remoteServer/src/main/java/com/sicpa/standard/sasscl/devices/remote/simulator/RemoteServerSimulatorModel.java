package com.sicpa.standard.sasscl.devices.remote.simulator;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.sasscl.devices.remote.model.IRemoteServerModel;
import com.sicpa.standard.sasscl.productionParameterSelection.node.AbstractProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.IProductionParametersNode;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;

/**
 * 
 * Model used to setup the RemoteServerSimulator
 * 
 * @see RemoteServerSimulator
 * 
 */
public class RemoteServerSimulatorModel implements IRemoteServerModel, Serializable {

	private static final long serialVersionUID = 1L;

	protected transient boolean build = false;

	protected final static Logger logger = LoggerFactory.getLogger(RemoteServerSimulatorModel.class);

	protected ProductionParameterRootNode productionParameters;

	protected int requestNumberOfCodes;

	protected boolean useCrypto;

	public void setProductionParameters(final ProductionParameterRootNode productionParameters) {
		this.productionParameters = productionParameters;
	}

	protected void rebuildProductionParameter(final IProductionParametersNode root) {
		if (root instanceof AbstractProductionParametersNode<?>) {
			AbstractProductionParametersNode<?> node = ((AbstractProductionParametersNode<?>) root);
			String sFile = node.getFileImage();
			if (sFile != null) {
				URL url = ClassLoader.getSystemResource(sFile);
				if (url != null) {
					try {
						ImageIcon image = new ImageIcon((GraphicsUtilities.loadCompatibleImage(url)));
						node.setImage(image);
						node.setFileImage(null);
					} catch (IOException e) {
						logger.error("Failed to load image for production parameters", e);
					}
				}
			}
		}

		if (root.getChildren() != null) {
			for (IProductionParametersNode node : root.getChildren()) {
				rebuildProductionParameter(node);
			}
		}
	}

	public ProductionParameterRootNode getProductionParameters() {
		if (!this.build) {
			rebuildProductionParameter(this.productionParameters);
		}
		return this.productionParameters;
	}

	public int getRequestNumberOfCodes() {
		return this.requestNumberOfCodes;
	}

	public void setRequestNumberOfCodes(final int requestNumberOfCodes) {
		this.requestNumberOfCodes = requestNumberOfCodes;
	}

	public void setUseCrypto(boolean useCrypto) {
		this.useCrypto = useCrypto;
	}

	public boolean isUseCrypto() {
		return useCrypto;
	}
}
