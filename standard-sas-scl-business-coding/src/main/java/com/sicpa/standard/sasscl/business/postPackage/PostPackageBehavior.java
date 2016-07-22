package com.sicpa.standard.sasscl.business.postPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.sicpa.standard.sasscl.devices.camera.blobDetection.BlobDetectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.client.common.provider.IProviderGetter;
import com.sicpa.standard.sasscl.model.Code;
import com.sicpa.standard.sasscl.model.DecodedCameraCode;
import com.sicpa.standard.sasscl.model.Product;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.provider.impl.AuthenticatorModeProvider;
import com.sicpa.standard.sasscl.provider.impl.ProductionBatchProvider;
import com.sicpa.standard.sasscl.provider.impl.SubsystemIdProvider;
import com.sicpa.standard.sasscl.sicpadata.CryptographyException;
import com.sicpa.standard.sasscl.sicpadata.reader.IAuthenticator;

public class PostPackageBehavior implements IPostPackageBehavior {
	private static final Logger logger = LoggerFactory.getLogger(PostPackageBehavior.class);

	protected final List<Code> codes = Collections.synchronizedList(new LinkedList<>());
	protected ProductionBatchProvider batchIdProvider;
	protected SubsystemIdProvider subsystemIdProvider;
	protected ProductionParameters productionParameters;
	protected int indexCodeThreshold = 100;

	protected AuthenticatorModeProvider authenticatorModeProvider;

	protected IProviderGetter<IAuthenticator> authenticatorProvider;

	protected String assosiatedCamera;

	protected BlobDetectionUtils blobDetectionUtils;

	public PostPackageBehavior() {
	}

	/**
	 * called by the coding module to notify which codes have been sent to the printer
	 */
	@Override
	public void addCodes(List<String> codes) {
		logger.debug("Adding to the list {} the following codes {} ",this.codes.toString(), codes.toString());
		for (String aCode : codes) {
			this.codes.add(new Code(aCode));
		}
	}

	/**
	 * find the index of the given code in our list of code<br>
	 * it stops after the index x, because there is no way there could be x bad code in a row without an alert being
	 * triggered that will stop the production
	 *
	 */
	protected int indexOfCode(final String code) {
		for (int i = 0; i < codes.size(); i++) {
//			if (i > indexCodeThreshold) {
//				// do not check after x bad codes
//				return -1;
//			}
			if(code.equals(codes.get(i).getStringCode())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * decode encrypted code
	 *
	 * @param code
	 * @return
	 */
	protected DecodedCameraCode decode(Code code) {
		try {
			return (DecodedCameraCode) authenticatorProvider.get().decode(authenticatorModeProvider.get(),
					code.getStringCode());
		} catch (CryptographyException e) {
			logger.error("Failed to decode : " + e.getMessage(), e);
		}
		return null;
	}

	protected List<Product> generateBadProducts(final List<Code> badCodes, ProductStatus status) {

		List<Product> products = new ArrayList<Product>();
		for (Code code : badCodes) {
			Product p = createProduct();
			p.setCode(code);
			p.setStatus(status);
			p.setQc(assosiatedCamera);

			DecodedCameraCode decodedCameraCode = decode(code);
			if (decodedCameraCode != null) {
				setProductInfoWithCryptoResult(p, decodedCameraCode);
			}
			logger.debug("Generating product from post package status = {} , code = {}", status, code);
			products.add(p);
		}
		return products;
	}

	protected void setProductInfoWithCryptoResult(final Product product, final DecodedCameraCode code) {
		product.getCode().setCodeType(code.getCodeType());
		product.getCode().setEncoderId(code.getBatchId());
		product.getCode().setSequence(code.getSequence());
		product.getCode().setMode(code.getMode());;
		product.getCode().setVersion(code.getVersion());
	}

	protected Product createProduct() {
		Product p = new Product();
		p.setActivationDate(new Date());
		p.setProductionBatchId(batchIdProvider.get());
		p.setSubsystem(subsystemIdProvider.get());
		p.setSku(productionParameters.getSku());
		p.setPrinted(true);
		return p;
	}

	@Override
	public List<Product> handleBadCode(Code code) {
		synchronized (codes) {
			if (!codes.isEmpty()) {
				List<Code> badCode = new ArrayList<Code>(1);
				logger.debug("handling the current bad code {}. The code removes is {} " , code, codes.get(0));

				badCode.add(codes.remove(0));
				ProductStatus productStatus = blobDetectionUtils.isBlobDetected(code) ? ProductStatus.INK_DETECTED :
						ProductStatus.SENT_TO_PRINTER_UNREAD;
				return generateBadProducts(badCode, productStatus);
			}
		}
		return Collections.emptyList();
	}

	@Override
	public List<Product> handleGoodCode(Code code) {
		synchronized (codes) {
			if (!codes.isEmpty()) {
				int index = indexOfCode(code.getStringCode());
				if (index != -1) {
					logger.debug("handeling the current good code {}. The code removes is {} " , code, codes.get(index));
					codes.remove(index);
					if (index != 0) {
						List<Code> subList = codes.subList(0, index);
						logger.debug("removing the following codes from the list {}. printer wasted " , codes.toString());

						List<Code> badCodes = new ArrayList<Code>(subList);
						subList.clear();
						return generateBadProducts(badCodes, ProductStatus.SENT_TO_PRINTER_WASTED);
					}
				} else {
					logger.warn("Code not found in buffer {}", code.getStringCode());
				}
			}
		}
		return Collections.emptyList();
	}

	public ProductionBatchProvider getBatchIdProvider() {
		return this.batchIdProvider;
	}

	public void setBatchIdProvider(final ProductionBatchProvider batchIdProvider) {
		this.batchIdProvider = batchIdProvider;
	}

	public void setSubsystemIdProvider(SubsystemIdProvider subsystemIdProvider) {
		this.subsystemIdProvider = subsystemIdProvider;
	}

	public void setProductionParameters(ProductionParameters productionParameters) {
		this.productionParameters = productionParameters;
	}

	public void setIndexCodeThreshold(int indexCodeThreshold) {
		this.indexCodeThreshold = indexCodeThreshold;
	}

	public IProviderGetter<IAuthenticator> getAuthenticatorProvider() {
		return authenticatorProvider;
	}

	public void setAuthenticatorProvider(IProviderGetter<IAuthenticator> authenticatorProvider) {
		this.authenticatorProvider = authenticatorProvider;
	}

	@Override
	public List<Product> notifyProductionStopped() {
		synchronized (codes) {
			List<Product> wastedProducts = generateBadProducts(codes, ProductStatus.SENT_TO_PRINTER_WASTED);
			logger.debug("notifyProductionStopped . clearing the follwoing codes {} " , codes.toString());

			codes.clear();
			return wastedProducts;
		}
	}

	public void setAuthenticatorModeProvider(AuthenticatorModeProvider authenticatorModeProvider) {
		this.authenticatorModeProvider = authenticatorModeProvider;
	}

	@Override
	public void setAssosiatedCamera(String cameraName) {
		this.assosiatedCamera = cameraName;

	}

	public void setBlobDetectionUtils(BlobDetectionUtils blobDetectionUtils) {
		this.blobDetectionUtils = blobDetectionUtils;
	}
}
