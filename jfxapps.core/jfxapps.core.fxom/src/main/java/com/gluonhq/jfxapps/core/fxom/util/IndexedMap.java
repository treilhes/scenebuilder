package com.gluonhq.jfxapps.core.fxom.util;

import java.util.Map;

public interface IndexedMap<K, V> extends Map<K, V> {

    int indexOfKey(Object key);

    int indexOfValue(Object value);

    K getKey(int index);

    V getValue(int index);

    Entry<K, V> get(int index);

    V putAt(int index, K key, V value);

    V removeAt(int index);

}
