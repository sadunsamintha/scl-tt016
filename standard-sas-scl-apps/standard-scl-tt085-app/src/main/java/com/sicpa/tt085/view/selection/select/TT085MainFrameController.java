package com.sicpa.tt085.view.selection.select;

import com.sicpa.standard.sasscl.custoBuilder.CustoBuilder;
import com.sicpa.standard.sasscl.model.ProductionParameters;
import com.sicpa.standard.sasscl.view.MainFrameController;
import com.sicpa.tt085.model.TT085Country;
import com.sicpa.tt085.model.provider.CountryProvider;


public class TT085MainFrameController extends MainFrameController implements CountryProvider{
	
	public void setCountry(TT085Country countryObj) {
		CustoBuilder.addPropertyToClass(ProductionParameters.class, country);
		productionParameters.setProperty(country, countryObj);
	}

}

