/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.javafx.scenebuilder.ext.theme;

import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.api.theme.ThemeContext;
import com.oracle.javafx.scenebuilder.api.theme.ThemeGroup;
import com.oracle.javafx.scenebuilder.api.theme.ThemeGroupContext;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationSingleton;

public class DefaultThemesList {

    @ApplicationSingleton
    @ThemeGroupContext(id = "9aaac893-061c-4456-8a87-13ed51e3cea8", name = "theme.group.caspian")
    public interface CaspianGroup extends ThemeGroup {
    }

    @ApplicationSingleton
    @ThemeGroupContext(id = "f654a53a-98b4-4069-8ce2-5a5be01cb675", name = "theme.group.modena")
    public interface ModenaGroup extends ThemeGroup {
    }

    @ApplicationSingleton
    @ThemeContext(id = "34d4539f-ff7c-4e6d-acc8-f656317710ff", name = "title.theme.modena", userAgentStylesheet = "com/sun/javafx/scene/control/skin/modena/modena.bss", groupClass = ModenaGroup.class)
    public interface Modena extends Theme {
    }

    @ApplicationSingleton
    @ThemeContext(id = "d4e2e628-e7ec-4e16-9c10-918bd4269d08", name = "title.theme.modena_touch", userAgentStylesheet = "com/oracle/javafx/scenebuilder/ext/theme/modena/modena-touch.css", groupClass = ModenaGroup.class)
    public interface ModenaTouch extends Theme {
    }

    @ApplicationSingleton
    @ThemeContext(id = "109771fc-eb8b-4a27-9f1e-85a7dd7c19b7", name = "title.theme.modena_high_contrast_black_on_white", userAgentStylesheet = "com/oracle/javafx/scenebuilder/ext/theme/modena/modena-highContrast-blackOnWhite.css", groupClass = ModenaGroup.class)
    public interface ModenaHighContrastBlackOnWhite extends Theme {
    }

    @ApplicationSingleton
    @ThemeContext(id = "0047b276-4344-4459-b9ba-4e62cff055ad", name = "title.theme.modena_high_contrast_white_on_black", userAgentStylesheet = "com/oracle/javafx/scenebuilder/ext/theme/modena/modena-highContrast-whiteOnBlack.css", groupClass = ModenaGroup.class)
    public interface ModenaHighContrastWhiteOnBlack extends Theme {
    }

    @ApplicationSingleton
    @ThemeContext(id = "93d103d0-de66-4e25-ab47-f75afb19cf18", name = "title.theme.modena_high_contrast_yellow_on_black", userAgentStylesheet = "com/oracle/javafx/scenebuilder/ext/theme/modena/modena-highContrast-yellowOnBlack.css", groupClass = ModenaGroup.class)
    public interface ModenaHighContrastYellowOnBlack extends Theme {
    }

    @ApplicationSingleton
    @ThemeContext(id = "f44574a4-6080-4b1b-828a-6d1721cfb412", name = "title.theme.modena_touch_high_contrast_black_on_white", userAgentStylesheet = "com/oracle/javafx/scenebuilder/ext/theme/modena/modena-touch-highContrast-blackOnWhite.css", groupClass = ModenaGroup.class)
    public interface ModenaTouchHighContrastBlackOnWhite extends Theme {
    }

    @ApplicationSingleton
    @ThemeContext(id = "ab82d1da-e42b-44de-8eb2-b94fadd3c3dc", name = "title.theme.modena_touch_high_contrast_white_on_black", userAgentStylesheet = "com/oracle/javafx/scenebuilder/ext/theme/modena/modena-touch-highContrast-whiteOnBlack.css", groupClass = ModenaGroup.class)
    public interface ModenaTouchHighContrastWhiteOnBlack extends Theme {
    }

    @ApplicationSingleton
    @ThemeContext(id = "87a1de36-7ef9-404c-9273-9f1c1ffcc114", name = "title.theme.modena_touch_high_contrast_yellow_on_black", userAgentStylesheet = "com/oracle/javafx/scenebuilder/ext/theme/modena/modena-touch-highContrast-yellowOnBlack.css", groupClass = ModenaGroup.class)
    public interface ModenaTouchHighContrastYellowOnBlack extends Theme {
    }

    @ApplicationSingleton
    @ThemeContext(id = "3a7f89a0-1f7c-4946-a981-eb43a5d2f123", name = "title.theme.caspian", userAgentStylesheet = "com/sun/javafx/scene/control/skin/caspian/caspian.bss", groupClass = CaspianGroup.class)
    public interface Caspian extends Theme {
    }

    @ApplicationSingleton
    @ThemeContext(id = "02993989-b7fe-4783-b6bb-105ee3c222b9", name = "title.theme.caspian_high_contrast", userAgentStylesheet = "com/oracle/javafx/scenebuilder/ext/theme/caspian/caspian-highContrast.css", groupClass = CaspianGroup.class)
    public interface CaspianHighContrast extends Theme {
    }

    @ApplicationSingleton
    @ThemeContext(id = "2b26d2ef-8381-470b-bb7d-6f727b855ffc", name = "title.theme.caspian_embedded", userAgentStylesheet = "com/oracle/javafx/scenebuilder/ext/theme/caspian/caspian-embedded.css", groupClass = CaspianGroup.class)
    public interface CaspianEmbedded extends Theme {
    }

    @ApplicationSingleton
    @ThemeContext(id = "860717c4-7ce2-46b8-b4b8-c86b716533b7", name = "title.theme.caspian_embedded_high_contrast", userAgentStylesheet = "com/oracle/javafx/scenebuilder/ext/theme/caspian/caspian-embedded-highContrast.css", groupClass = CaspianGroup.class)
    public interface CaspianEmbeddedHighContrast extends Theme {
    }

    @ApplicationSingleton
    @ThemeContext(id = "da76cb2c-bfac-4cf8-8033-2d7d0fc4235b", name = "title.theme.caspian_embedded_qvga", userAgentStylesheet = "com/oracle/javafx/scenebuilder/ext/theme/caspian/caspian-embedded-qvga.css", groupClass = CaspianGroup.class)
    public interface CaspianEmbeddedQvga extends Theme {
    }

    @ApplicationSingleton
    @ThemeContext(id = "90c0c8cb-f591-46f2-bc65-085e8a1c1603", name = "title.theme.caspian_embedded_qvga_high_contrast", userAgentStylesheet = "com/oracle/javafx/scenebuilder/ext/theme/caspian/caspian-embedded-qvga-highContrast.css", groupClass = CaspianGroup.class)
    public interface CaspianEmbeddedQvgaHighContrast extends Theme {
    }

}
