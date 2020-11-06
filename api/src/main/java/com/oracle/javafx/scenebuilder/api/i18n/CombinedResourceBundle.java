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

    public CombinedResourceBundle(List<ResourceBundle> bundles)
    {
        this.bundles = bundles;
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
    public Object handleGetObject(String key)
    {
        return combinedResources.get(key);
    }

    @Override
    public Enumeration<String> getKeys()
    {
        return Collections.enumeration(combinedResources.keySet());
    }
}