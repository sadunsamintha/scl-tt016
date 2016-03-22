package com.sicpa.standard.sasscl.controller.productionconfig.xstream;

import com.sicpa.standard.sasscl.controller.productionconfig.config.BrsConfig;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


public class BrsConfigConverter extends AbstractLayoutConfigConverter {

    public BrsConfigConverter() {
        super(BrsConfig.class);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        BrsConfig config = (BrsConfig) source;
        writer.addAttribute("id", config.getId());
        writeConfig(writer, config.getProperties());
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        BrsConfig config = new BrsConfig();
        config.setId(reader.getAttribute("id"));
        readConfig(reader, config.getProperties());
        return config;
    }

}

