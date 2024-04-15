package com.gluonhq.jfxapps.metadata.plugin;

import static com.soebes.itf.extension.assertj.MavenExecutionResultAssert.assertThat;

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

@MavenJupiterExtension
public class MetadataResourceIT {


    @MavenTest
//    @MavenOptions({
//        @MavenOption("-X"), //debug
//        @MavenOption("-U") //debug
//    })
    public void the_first_test_case(MavenExecutionResult result) {
        assertThat(result).isSuccessful();
    }

}
