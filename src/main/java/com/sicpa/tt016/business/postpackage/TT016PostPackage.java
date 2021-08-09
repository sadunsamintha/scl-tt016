package com.sicpa.tt016.business.postpackage;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sicpa.standard.gui.utils.Pair;
import com.sicpa.standard.sasscl.business.postPackage.IPostPackageBehavior;
import com.sicpa.standard.sasscl.business.postPackage.PostPackage;
import com.sicpa.standard.sasscl.devices.IStartableDevice;

public class TT016PostPackage extends PostPackage {
    private static final Logger logger = LoggerFactory.getLogger(TT016PostPackage.class);

    @Override
    public void provideCodePair(final List<Pair<String, String>> codes, Object requestor) {
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

    private List<String> pruneSCLCodes(List<Pair<String, String>> codes) {
        List<String> res = new ArrayList<String>();

        for (Pair<String, String> c : codes) {
        	res.add(c.getValue1());
        }

        return res;
    }
}
