package com.gluonhq.jfxapps.registry.plugin;

import static com.soebes.itf.extension.assertj.MavenExecutionResultAssert.assertThat;

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

/**
 * Integration test for the JfxAppsRegistryMojo.
 * maven.home must be set to a valid maven installation
 * internal eclipse maven installation is not supported
 * create a Preferences /Maven / Installations with a valid maven installation and use it as default
 */
@MavenJupiterExtension
public class JfxAppsRegistryMojoIT {

    @MavenTest
//    @MavenOptions({
//        @MavenOption("-X"), //debug
//        @MavenOption("-U") //debug
//    })
    public void generate_registry_test_case(MavenExecutionResult result) {
        assertThat(result).isSuccessful();
    }

}
