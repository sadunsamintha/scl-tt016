package com.sicpa.standard.sasscl.model;

import java.io.Serializable;

public class ProductionMode implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final ProductionMode STANDARD = new ProductionMode(1, "productionmode.standard", true);
    public static final ProductionMode EXPORT = new ProductionMode(2, "productionmode.export", false);
    public static final ProductionMode MAINTENANCE = new ProductionMode(3, "productionmode.maintenance", false);
    public static final ProductionMode COUNTING = new ProductionMode(4, "productionmode.counting", false);
    public static final ProductionMode REFEED_NORMAL = new ProductionMode(5, "productionmode.refeed.normal", true);
    public static final ProductionMode REFEED_CORRECTION = new ProductionMode(6, "productionmode.refeed.correction",
            true);
    public static final ProductionMode OFFLINE = new ProductionMode(7, "productionmode.offline", false);
    public static final ProductionMode EXPORT_CODING = new ProductionMode(8, "productionmode.export.coding", true);
    public static final ProductionMode ALL = new ProductionMode(0, "productionmode.all", false);

    protected final int id;
    protected String description;
    protected boolean withSicpaData;

    public ProductionMode() {
        this.id = -1;
        this.description = "";
    }

    public ProductionMode(int id) {
        this.id = id;
        this.description = "";
    }

    public ProductionMode(final int id, final String description, boolean withSicpaData) {
        this.id = id;
        this.description = description;
        this.withSicpaData = withSicpaData;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProductionMode other = (ProductionMode) obj;
        if (id != other.id)
            return false;
        return true;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isWithSicpaData() {
        return withSicpaData;
    }

    public void setWithSicpaData(boolean withSicpaData) {
        this.withSicpaData = withSicpaData;
    }
}
