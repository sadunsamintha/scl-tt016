package com.sicpa.tt085.model;

public class TT085Country {
	
    private Long id;

    private String countryIso;

    private String displayDescription;
    
    public TT085Country(Long id, String countryIso, String description) {
        this.id = id;
        this.countryIso = countryIso;
        this.displayDescription = description;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCountryIso() {
		return countryIso;
	}

	public void setCountryIso(String countryIso) {
		this.countryIso = countryIso;
	}

	public String getDisplayDescription() {
		return displayDescription;
	}

	public void setDisplayDescription(String displayDescription) {
		this.displayDescription = displayDescription;
	}

}
