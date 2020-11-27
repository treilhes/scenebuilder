package com.oracle.javafx.scenebuilder.api.settings;

import java.io.File;

import org.springframework.stereotype.Component;

@Component
public class MavenSetting extends AbstractSetting {

	public MavenSetting() {}
	
	public String getUserM2Repository() {
        String m2Path = System.getProperty("user.home") + File.separator +
                ".m2" + File.separator + "repository"; //NOI18N

        // TODO: Allow custom path for .m2

        assert m2Path != null;

        return m2Path;
    }

    public String getTempM2Repository() {
        String m2Path = System.getProperty("java.io.tmpdir") + File.separator + "m2Tmp"; //NOI18N

        assert m2Path != null;

        return m2Path;
    }

}
