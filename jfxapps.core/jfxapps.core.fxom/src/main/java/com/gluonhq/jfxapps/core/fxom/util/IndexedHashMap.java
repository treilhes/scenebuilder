package com.gluonhq.jfxapps.core.fxom.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class IndexedHashMap<K, V> extends HashMap<K, V> implements IndexedMap<K, V> {

    private final List<K> keys = new ArrayList<>();
    private final List<V> values = new ArrayList<>();

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    @Override
    public int indexOfKey(Object key) {
        return keys.indexOf(key);
    }

    @Override
    public int indexOfValue(Object value) {
        return values.indexOf(value);
    }

    @Override
    public K getKey(int index) {
        return keys.get(index);
    }

    @Override
    public V getValue(int index) {
        return values.get(index);
    }

    @Override
    public Map.Entry<K, V> get(int index) {
        final K k = getKey(index);
        final V v = getValue(index);

        return new Map.Entry<K, V>() {

            @Override
            public K getKey() {
                return k;
            }

            @Override
            public V getValue() {
                return v;
            }

            @Override
            public V setValue(V value) {
                return put(k, value);
            }
        };
    }

    @Override
    public V put(K key, V value) {
        final boolean isUpdate = containsKey(key);

        final V oldValue = super.put(key, value);

        if (isUpdate) {
            final int index = keys.indexOf(key);
            values.set(index, value);
        } else {
            keys.add(key);
            values.add(value);
        }

        return oldValue;
    }

    @Override
    public V putAt(int index, K key, V value) {
        assert size() == keys.size();
        assert size() == values.size();

        final boolean isUpdate = containsKey(key);

        if (isUpdate) {
            if (index < 0 || index > size() - 1) {
                throw new IndexOutOfBoundsException(index);
            }

            final V oldValue = remove(key);
            super.put(key, value);
            keys.add(index, key);
            values.add(index, value);
            return oldValue;
        } else {
            if (index < 0 || index > size()) {
                throw new IndexOutOfBoundsException(index);
            }

            keys.add(index, key);
            values.add(index, value);
            return null;
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        if (m != null) {
            m.entrySet().forEach(e -> put(e.getKey(), e.getValue()));
        }
    }

    @Override
    public V remove(Object key) {
        final boolean isUpdate = containsKey(key);

        if (isUpdate) {
            final V oldValue = super.remove(key);
            final int index = keys.indexOf(key);
            keys.remove(index);
            values.remove(index);
            return oldValue;
        }

        return null;
    }

    @Override
    public V removeAt(int index) {
        final K key = keys.get(index);
        return remove(key);
    }

    @Override
    public void clear() {
        super.clear();
        keys.clear();
        values.clear();
    }

    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(keys));
    }

    @Override
    public Collection<V> values() {
        return Collections.unmodifiableList(values);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> orderedSet = new LinkedHashSet<>(keys.size());
        for (int i = 0; i < keys.size(); i++) {
            orderedSet.add(get(i));
        }
        return Collections.unmodifiableSet(orderedSet);
//        return super.entrySet();
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            return put(key, value);
        }
        return get(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        Object v = get(key);

        if (v == value) {
            remove(key);
            return true;
        }
        return false;
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        final boolean isUpdate = containsKey(key);
        final V value = super.computeIfAbsent(key, mappingFunction);

        if (isUpdate) {
            final int index = keys.indexOf(key);
            values.set(index, value);
        } else if (value != null) { // If the mapping function returns null, no mapping is recorded
            keys.add(key);
            values.add(value);
        }

        return value;
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        final V oldValue = get(key);

        if (oldValue != null) {
            // If the value for the specified key is present and non-null,
            // attempts to compute a new mapping given the key and its current mapped value.

            final V value = super.computeIfPresent(key, remappingFunction);

            final int index = keys.indexOf(key);
            if (value != null) {
                values.set(index, value);
            } else { // If the remapping function returns null, the mapping is removed.
                keys.remove(index);
                values.remove(index);
            }

            return value;
        }

        return null;
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {

        final boolean isUpdate = containsKey(key);
        final V value = super.compute(key, remappingFunction);

        if (isUpdate && value != null) {
            final int index = keys.indexOf(key);
            values.set(index, value);
        } else if (isUpdate && value == null) {
            final int index = keys.indexOf(key);
            keys.remove(index);
            values.remove(index);
        } else if (value != null) {
            keys.add(key);
            values.add(value);
        }

        return value;
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        final boolean isUpdate = containsKey(key);
        final V newValue = super.merge(key, value, remappingFunction);

        if (isUpdate && newValue != null) {
            final int index = keys.indexOf(key);
            values.set(index, newValue);
        } else if (isUpdate && newValue == null) {
            final int index = keys.indexOf(key);
            keys.remove(index);
            values.remove(index);
        } else if (newValue != null) {
            keys.add(key);
            values.add(value);
        }

        return newValue;
        //return super.merge(key, value, remappingFunction);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        entrySet().forEach(e -> action.accept(e.getKey(), e.getValue()));
    }
}
