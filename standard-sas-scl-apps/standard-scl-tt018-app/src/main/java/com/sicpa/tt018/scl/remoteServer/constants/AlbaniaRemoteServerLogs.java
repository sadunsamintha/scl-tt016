package com.sicpa.tt018.scl.remoteServer.constants;

// --------------------------------------------------------------------------- //
/**
 * Class AlbaniaRemoteServerLogs.
 * 
 * @author RMathieu
 */
public class AlbaniaRemoteServerLogs {

	public static final String PACKAGE_PRODUCTS_DTO = "PackagedProductsDTO = {0}";
	public static final String COUNTED_PRODUCTS_DTO = "CountedProductsDTO = {0}";
	public static final String EJECTED_PRODUCTS_DTO = "EjectedProductsDTO = {0}";

	public static final String INFO_PRODUCT_IN_ERROR_IN_COUNTING_MODE = "Error product in Counting mode - NOT SEND TO DMS - Product = {0}.";
	public static final String INFO_SENDING_COUNTING_PRODUCTION_ONLY_ERRORS = "Sending {0} production with only product in error - PackagedProducts = {1}.";

	public static final String ERROR_SKU_ALREADY_EXIST = "SKU already exist in production tree, brand = {0} , varinat = {1} , description = {2}.";
	public static final String RECIEVED_AUTHENTICATOR = "Authenticator recieved [class = {0}]";

	public static final String SENDING_DOMESTIC_PRODUCTION = "Sending domestic production data to master....";
	public static final String SENDING_DOMESTIC_EJECTED_PACKAGED_PRODUCTS = "Sending ejected production data for packagedProducts = {0} in file = {1}.";

	public static final String SENDING_COUNTING_PRODUCTION = "Sending counting production data to master....";

	public static final String ERROR_CONNEXION_FAIL = "Error trying to connect Master SCL";

	public static final String ERROR_WHILE_GETTING_SKU = "Error during SKUs receptionfrom master = {0}.";
	public static final String ERROR_WHILE_SENDING_DATA = "Error while sending production data to master = {0}.";
	public static final String ERROR_INVALID_PRODUCTION_TYPE = "Invalid production data type !!! = {0}.";

	public static final String NULL_PACKAGED_PRODUCTS = "Production data are empty.";

	public static final String ERROR_VALIDATING_DATA_TO_SEND = "Error during data to send validation = {0}.";
	public static final String ERROR_VALIDATING_MARKET_TYPE_DTO = "Error during MarketTypeDTO validation = {0}. Exception = {1}";
	public static final String ERROR_VALIDATING_PRODUCTION_DATA = "Error during production data validation = {0}, Exception = {1}.";
	public static final String ERROR_VALIDATING_COUNTING_DATA = "Error during counting data validation = {0}, Exception = {1}.";

	public static final String ERROR_PACKAGE_PRODUCT_TYPE_NULL = "Packaged product type is NULL {0}.";

	public static final String ERROR_EMPTY_MARKET_TYPE = "MarketTypeDTO is empty  = {0}";
	public static final String ERROR_EMPTY_PACKAGE_PRODUCTS = "PackagedProducts is empty  = {0}";
	public static final String ERROR_BATCH_ID_NULL = "Invalid product Creation. Batch ID is NULL = {0}";
	public static final String ERROR_SKU_NULL = "Invalid product Creation. SKU is NULL = {0}";
	public static final String ERROR_CODE_NULL = "Invalid product Creation. Code is NULL or empty = {0}";
	public static final String ERROR_PRODUCT_NULL = "Invalid product Creation. Product is NULL";
	public static final String ERROR_SKU_PRODUCT_DTO_INCOMPLETE = "SkuProduct DTO is null or uncomplete = {0}";
	public static final String ERROR_CODE_TYPE_DTO_INCOMPLETE = "CodeType DTO is null or uncomplete = {0}";
	public static final String ERROR_PRODUCTION_MODE_NULL = "Production Mode is NULL";
	public static final String ERROR_PRODUCT_STATUS_NULL = "Product status is NULL";

	public static final String ERROR_UNSUPPORTED_MARKET_TYPE = "Unsupported Market type ID recieved from Master = {0}.";
	public static final String ERROR_UNSUPPORTED_PRODUCT_STATUS = "Unsupported product status = {0}.";
	public static final String ERROR_UNSUPPORTED_PRODUCTION_MODE = "Unsupported production mode = {0}.";

	public static final String ERROR_PRODUCT_CODE_TYPE_NULL = "Invalid product Creation. Product Code Type is NULL or empty = {0}.";
	public static final String ERROR_SKU_CODE_TYPE_NULL = "Invalid product Creation. SKU Code Type is NULL or empty = {0}.";
	public static final String ERROR_PRODUCT_AND_SKU_CODE_TYPE_NOT_MATCH = "Invalid product Creation. SKU and Prroduct Code Type not match product = {1}, sku = {0}.";

	public static final String ERROR_CONVERT_EJECTED_PRODUCTS_NOT_SUPPORTED = "Error convertEjectedProduction is not implemented on SCL.";
}
