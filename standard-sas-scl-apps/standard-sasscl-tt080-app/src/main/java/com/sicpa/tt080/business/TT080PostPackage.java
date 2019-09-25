package com.sicpa.tt080.business;

import com.sicpa.standard.sasscl.business.postPackage.IPostPackageBehavior;
import com.sicpa.standard.sasscl.business.postPackage.PostPackage;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.sicpa.tt080.remote.impl.sicpadata.TT080SicpaDataGeneratorWrapper.PRINTER_SPACE_REPRESENTATION;


public class TT080PostPackage extends PostPackage {
    private static final Logger logger = LoggerFactory.getLogger(TT080PostPackage.class);

    @Override
    public void provideCode(final List<String> codes, Object requestor) {
        if (!isEnabled()) {
            return;
        }

        IPostPackageBehavior behavior = getModule(((IStartableDevice) requestor).getName());

        if (behavior != null) {
            behavior.addCodes(pruneSCLCodes(codes));
        } else {
            logger.error("no postpackage behavior found for {}", requestor);
        }
    }

    private List<String> pruneSCLCodes(List<String> codes) {
        List<String> res = new ArrayList<>();

        for(String c : codes) {
            int i = c.lastIndexOf(PRINTER_SPACE_REPRESENTATION);
            if(i > 0) {
                res.add(c.substring(0, i));
            } else {
                res.add(c);
            }
        }

        return res;
    }
}
