package com.gluonhq.jfxapps.registry.plugin;

import static com.soebes.itf.extension.assertj.MavenExecutionResultAssert.assertThat;

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

@MavenJupiterExtension
public class JfxAppsRegistryMojoIT {


    static {
        System.setProperty("maven.home", "C:\\Users\\ptreilhes\\adns\\tools\\apache-maven-3.6.3");
    }

    @MavenTest
//    @MavenOptions({
//        @MavenOption("-X"), //debug
//        @MavenOption("-U") //debug
//    })
    public void generate_registry_test_case(MavenExecutionResult result) {
        assertThat(result).isSuccessful();
    }

}
