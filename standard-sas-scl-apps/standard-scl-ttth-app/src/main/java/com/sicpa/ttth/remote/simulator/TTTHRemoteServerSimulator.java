package com.sicpa.ttth.remote.simulator;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sicpa.gssd.ttth.server.common.dto.DailyBatchRequestDto;
import com.sicpa.standard.client.common.eventbus.service.EventBusService;
import com.sicpa.standard.sasscl.devices.remote.RemoteServerException;
import com.sicpa.standard.sasscl.devices.remote.impl.dtoConverter.DailyBatchRequestRepository;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulator;
import com.sicpa.standard.sasscl.devices.remote.simulator.RemoteServerSimulatorModel;
import com.sicpa.standard.sasscl.model.CodeType;
import com.sicpa.standard.sasscl.productionParameterSelection.node.impl.ProductionParameterRootNode;
import com.sicpa.std.common.api.staticdata.sku.dto.SkuProductDto;

public class TTTHRemoteServerSimulator extends RemoteServerSimulator {

    private DailyBatchRequestRepository dailyBatchRequestRepository;

    private String lineID;

    public TTTHRemoteServerSimulator(RemoteServerSimulatorModel model) {
        super(model);
    }

    public TTTHRemoteServerSimulator(String configFile) throws RemoteServerException {
        super(configFile);
    }

    @Override
    public ProductionParameterRootNode getTreeProductionParameters() throws RemoteServerException {
        if (simulatorModel == null) {
            throw new RemoteServerException("Remote server model is not set");
        }

        CodeType codeType = new CodeType(1);
        codeType.setDescription("ct: " + codeType.getId() + " - ");
        dailyBatchRequestRepository.setCodeType(codeType);

        for (int i = 0; i < 3; i++) {
            DailyBatchRequestDto dailyBatchRequestDto = new DailyBatchRequestDto();
            dailyBatchRequestDto.setId((long) i);
            dailyBatchRequestDto.setQuantity((long) 1000);

            Calendar now = Calendar.getInstance();
            dailyBatchRequestDto.setProductionStartDate(now.getTime());
            now.add(Calendar.HOUR, 24);
            dailyBatchRequestDto.setProductionStopDate(now.getTime());
            now.add(Calendar.YEAR, 543);
            now.add(Calendar.HOUR, -24);
            dailyBatchRequestDto.setBatchJobId("SIM-" + lineID+ "-"
                + new SimpleDateFormat("ddMMyy").format(now.getTime())
            + "-" + "000" + i + "A" );

            SkuProductDto skuProductDto = new SkuProductDto();
            skuProductDto.setId(Long.valueOf(("000" + i)));
            skuProductDto.setInternalDescription(codeType.getDescription() + "skuid: 000" + i);
            skuProductDto.setSkuBarcode("000" + i);
            dailyBatchRequestDto.setSkuProductDto(skuProductDto);

            dailyBatchRequestRepository.addDailyBatchRequest(dailyBatchRequestDto);
        }

        return simulatorModel.getProductionParameters();
    }

    public void setDailyBatchRequestRepository(DailyBatchRequestRepository dailyBatchRequestRepository) {
        this.dailyBatchRequestRepository = dailyBatchRequestRepository;
    }

    public void setLineID(String lineID) {
        this.lineID = lineID;
    }
}
