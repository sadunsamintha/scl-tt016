package com.sicpa.standard.sasscl;

import com.sicpa.tt065.scl.TT065MainAppWithProfile;

/**
 * Created by wvieira on 15/09/2016.
 */
public class Main {
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        new TT065MainAppWithProfile().selectProfile();
    }
}
