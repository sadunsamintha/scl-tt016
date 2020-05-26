package com.sicpa.tt065.business;

import com.sicpa.standard.sasscl.business.postPackage.IPostPackageBehavior;
import com.sicpa.standard.sasscl.business.postPackage.PostPackage;
import com.sicpa.standard.sasscl.devices.IStartableDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.sicpa.tt065.remote.impl.sicpadata.TT065SicpaDataGeneratorWrapper.BLOCK_SEPARATOR;


public class TT065PostPackage extends PostPackage {
    private static final Logger logger = LoggerFactory.getLogger(TT065PostPackage.class);

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
        List<String> res = new ArrayList<String>();

        for(String c : codes) {
            int i = c.lastIndexOf(BLOCK_SEPARATOR);
            if(i > 0) {
                res.add(c.substring(0, i));
            } else {
                res.add(c);
            }
        }

        return res;
    }
}
