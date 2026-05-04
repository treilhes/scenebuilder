package org.scenebuilder.ext.script.preference.global;

import com.treilhes.emc4j.boot.api.context.annotation.ApplicationSingleton;
import com.treilhes.jfxplace.core.api.preference.DefaultValueProvider;
import com.treilhes.jfxplace.core.api.preference.ManagedGlobalPreference;
import com.treilhes.jfxplace.core.api.preference.Preference;
import com.treilhes.jfxplace.core.api.preference.PreferenceContext;

@ApplicationSingleton
@PreferenceContext(id = "18e02c42-8a76-4c22-8c4f-7bc4be53d131", // NO CHECK
        name = StaticLoadPreference.PREFERENCE_KEY,
        defaultValueProvider = StaticLoadPreference.DefaultProvider.class)
public interface StaticLoadPreference extends Preference<Boolean>, ManagedGlobalPreference {

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "STATIC_LOAD"; //NOCHECK
    public static final boolean PREFERENCE_DEFAULT_VALUE = true;
    public static class DefaultProvider implements DefaultValueProvider<Boolean> {
        @Override
        public Boolean get() {
            return PREFERENCE_DEFAULT_VALUE;
        }
    }
}
