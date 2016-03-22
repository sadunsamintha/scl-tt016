package com.sicpa.tt018;

import static com.sicpa.standard.sasscl.messages.ActionMessageType.ERROR;
import static com.sicpa.tt018.scl.business.activation.constants.AlbaniaSCLActivationMessages.EXCEPTION_CODE_IN_SOFT_DRINK;
import static com.sicpa.tt018.scl.model.productionParameters.AlbaniaProductionMode.SOFT_DRINK;

import com.sicpa.standard.sasscl.Bootstrap;
import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.statistics.StatisticsKey;
import com.sicpa.tt018.scl.model.AlbaniaProductStatus;

public class TT018Bootstrap extends Bootstrap {

	@Override
	public void executeSpringInitTasks() {
		super.executeSpringInitTasks();
		messageCusto();
		productionModeCusto();
		statisticsCusto();
	}

	private void messageCusto() {
		CustoBuilder.addMessage(EXCEPTION_CODE_IN_SOFT_DRINK, "[ACT_05]", ERROR);
	}

	private void productionModeCusto() {
		CustoBuilder.addProductionMode(SOFT_DRINK, "softdrink", -1);
	}

	private void statisticsCusto() {
		CustoBuilder.addToStatisticsMapper(AlbaniaProductStatus.SENT_TO_PRINTER_BLOB, StatisticsKey.GOOD);
		CustoBuilder.addToStatisticsMapper(AlbaniaProductStatus.SOFT_DRINK, StatisticsKey.GOOD);
	}

}
