package com.sicpa.tt065.remote.remoteServer;

import com.sicpa.standard.sasscl.devices.remote.mapping.RemoteServerProductStatusMapping;
import com.sicpa.standard.sasscl.model.ProductStatus;
import com.sicpa.std.common.api.activation.dto.productionData.ProcessedProductsStatusDto;

public class TT065RemoteServerProductStatusMapping extends RemoteServerProductStatusMapping{
        /**
         * TODO, to be replaced by ProcessedProductsStatusDto.ACTIVATED_BLOB_DETECTION
         */
        private static final int ACTIVATED_BLOB_DETECTION = 100;


        public TT065RemoteServerProductStatusMapping() {

            //acti
            add(ProductStatus.AUTHENTICATED, ProcessedProductsStatusDto.AUTHENTICATED_ACTIVATED);//2 =>1
            add(ProductStatus.SENT_TO_PRINTER_WASTED, ProcessedProductsStatusDto.AUTHENTICATED_INTERNAL_SENTPRINTER_WASTED);//6 => 15
            add(ProductStatus.SENT_TO_PRINTER_UNREAD, ProcessedProductsStatusDto.AUTHENTICATED_INTERNAL_SENTPRINTER_NOCAMERA);//7 =>16
            add(ProductStatus.TYPE_MISMATCH, ProcessedProductsStatusDto.AUTHENTICATED_ACTIVATED_TYPEMISSMATCH);//3 => 2
            add(ProductStatus.REFEED, ProcessedProductsStatusDto.AUTHENTICATED_REFEED_AUTHORIZED);//11=> 23

            //count
            add(ProductStatus.EXPORT, ProcessedProductsStatusDto.COUNTED_ACTIVATED_EXPORT);//4=>11
            add(ProductStatus.MAINTENANCE, ProcessedProductsStatusDto.COUNTED_MAINTENANCE);//5=>17
            add(ProductStatus.COUNTING, ProcessedProductsStatusDto.COUNTED_ACTIVATED_COUNTING);//8=>6

            //eject
            add(ProductStatus.UNREAD, ProcessedProductsStatusDto.COUNTED_EJECTED_UNREAD);//0 =>12
            add(ProductStatus.NOT_AUTHENTICATED, ProcessedProductsStatusDto.COUNTED_EJECTED_AUTHENTICATIONFAIL);//1=>14

            //blob
            add(ProductStatus.INK_DETECTED, ACTIVATED_BLOB_DETECTION); //100=>100

        }

}
