/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.api.i18n;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CombinedResourceBundle extends ResourceBundle
{
	private Map<String, String> combinedResources = new HashMap<>();
    private List<ResourceBundle> bundles;
    private final boolean allowUnresolvedKeys;

    public CombinedResourceBundle(List<ResourceBundle> bundles, boolean allowUnresolvedKeys)
    {
        this.bundles = bundles;
        this.allowUnresolvedKeys = allowUnresolvedKeys;
        load();
    }

    public void load()
    {
    	bundles.forEach(bundle ->
        {
            Enumeration<String> keysEnumeration = bundle.getKeys();
            ArrayList<String> keysList = Collections.list(keysEnumeration);
            keysList.forEach(key -> combinedResources.put(key, bundle.getString(key)));
        });
    }

    @Override
    public boolean containsKey(String key) {
        if (allowUnresolvedKeys) {
            return true;
        }

        return super.containsKey(key);
    }

    @Override
    public Object handleGetObject(String key)
    {
        Object value = combinedResources.get(key);

        if (allowUnresolvedKeys && value == null) {
            return key;
        }

        return value;
    }

    @Override
    public Enumeration<String> getKeys()
    {
        return Collections.enumeration(combinedResources.keySet());
    }
}