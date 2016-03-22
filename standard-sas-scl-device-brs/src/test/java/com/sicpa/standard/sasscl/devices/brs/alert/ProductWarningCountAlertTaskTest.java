package com.sicpa.standard.sasscl.devices.brs.alert;

import com.sicpa.standard.sasscl.messages.MessageEventKey;
import junit.framework.Assert;
import org.junit.Test;

public class ProductWarningCountAlertTaskTest extends ProductCountAlertTaskAbstractTest {

	@Override
	public AbstractBrsProductCountAlertTask instantiateAlertTask() {
		return new ProductWarningCountAlertTask();
	}

	@Override
	public void setUnreadBarcodesThreshold(int threshold) {
		task.setUnreadBarcodesThreshold(threshold);
	}

	@Test
	public void getAlertMessage() {
		Assert.assertEquals(MessageEventKey.BRS.BRS_TOO_MANY_UNREAD_BARCODES_WARNING,
				((ProductWarningCountAlertTask) task).getAlertMessage().getKey());
	}

	@Test
	public void getAlertName() {
		Assert.assertEquals("Brs Product Warning Count Alert", task.getAlertName());

	}
}
