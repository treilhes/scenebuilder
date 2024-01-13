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
package com.oracle.javafx.scenebuilder.core.fxom.util;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;

@SetSystemProperty(key = "javafx.allowjs", value = "true")
class IndexedHashMapTest {

    private final static String K1 = "key01";
    private final static String K2 = "key2";
    private final static String K3 = "k3";

    private final static String V1 = "val01";
    private final static String V2 = "val2";
    private final static String V3 = "v3";

    @Test
    void indexed_map_keep_the_insertion_order_for_keys_and_values() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);
        map.put(K3, V3);

        assertEquals(List.of(K1, K2, K3), new ArrayList<>(map.keySet()));
        assertEquals(List.of(V1, V2, V3), new ArrayList<>(map.values()));

        Map<String, String> standard = new HashMap<>(); // hash keys
        assertNotEquals(List.of(K1, K2, K3), new ArrayList<>(standard.keySet()));
        assertNotEquals(List.of(V1, V2, V3), new ArrayList<>(standard.values()));

        standard = new TreeMap<>(standard); // natural string ordering
        assertNotEquals(List.of(K1, K2, K3), new ArrayList<>(standard.keySet()));
        assertNotEquals(List.of(V1, V2, V3), new ArrayList<>(standard.values()));


    }

    @Test
    void indexed_map_data_is_accessible_using_index() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);
        map.put(K3, V3);

        assertEquals(K1, map.get(0).getKey());
        assertEquals(K2, map.get(1).getKey());
        assertEquals(K3, map.get(2).getKey());

        assertEquals(V1, map.get(0).getValue());
        assertEquals(V2, map.get(1).getValue());
        assertEquals(V3, map.get(2).getValue());
    }

    @Test
    void indexed_map_data_return_correct_indexes() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);
        map.put(K3, V3);

        assertEquals(0, map.indexOfKey(K1));
        assertEquals(1, map.indexOfKey(K2));
        assertEquals(2, map.indexOfKey(K3));

        assertEquals(0, map.indexOfValue(V1));
        assertEquals(1, map.indexOfValue(V2));
        assertEquals(2, map.indexOfValue(V3));
    }

    @Test
    void indexed_map_data_order_is_kept_after_updates() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);
        map.put(K3, V3);

        map.put(K1, V3);
        map.put(K2, V2);
        map.put(K3, V1);

        assertEquals(K1, map.get(0).getKey());
        assertEquals(K2, map.get(1).getKey());
        assertEquals(K3, map.get(2).getKey());
        assertEquals(List.of(K1, K2, K3), new ArrayList<>(map.keySet()));

        assertEquals(V3, map.get(0).getValue());
        assertEquals(V2, map.get(1).getValue());
        assertEquals(V1, map.get(2).getValue());
        assertEquals(List.of(V3, V2, V1), new ArrayList<>(map.values()));
    }

    @Test
    void indexed_map_data_order_is_updated_after_remove_by_key() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);
        map.put(K3, V3);

        map.remove(K2);

        assertEquals(K1, map.get(0).getKey());
        assertEquals(K3, map.get(1).getKey());
        assertEquals(List.of(K1, K3), new ArrayList<>(map.keySet()));

        assertEquals(V1, map.get(0).getValue());
        assertEquals(V3, map.get(1).getValue());
        assertEquals(List.of(V1, V3), new ArrayList<>(map.values()));
    }

    @Test
    void indexed_map_data_order_is_updated_after_remove_by_index() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);
        map.put(K3, V3);

        map.removeAt(1);

        assertEquals(K1, map.get(0).getKey());
        assertEquals(K3, map.get(1).getKey());
        assertEquals(List.of(K1, K3), new ArrayList<>(map.keySet()));

        assertEquals(V1, map.get(0).getValue());
        assertEquals(V3, map.get(1).getValue());
        assertEquals(List.of(V1, V3), new ArrayList<>(map.values()));
    }

    @Test
    void indexed_map_data_order_is_updated_after_put_at_index() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);

        map.putAt(1, K3, V3);

        assertEquals(K1, map.get(0).getKey());
        assertEquals(K3, map.get(1).getKey());
        assertEquals(K2, map.get(2).getKey());
        assertEquals(List.of(K1, K3, K2), new ArrayList<>(map.keySet()));

        assertEquals(V1, map.get(0).getValue());
        assertEquals(V3, map.get(1).getValue());
        assertEquals(V2, map.get(2).getValue());
        assertEquals(List.of(V1, V3, V2), new ArrayList<>(map.values()));
    }

    @Test
    void indexed_map_data_order_is_updated_after_put_at_index_existing_key() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);
        map.put(K3, V3);

        map.putAt(1, K3, V3);

        assertEquals(K1, map.get(0).getKey());
        assertEquals(K3, map.get(1).getKey());
        assertEquals(K2, map.get(2).getKey());
        assertEquals(List.of(K1, K3, K2), new ArrayList<>(map.keySet()));

        assertEquals(V1, map.get(0).getValue());
        assertEquals(V3, map.get(1).getValue());
        assertEquals(V2, map.get(2).getValue());
        assertEquals(List.of(V1, V3, V2), new ArrayList<>(map.values()));
    }

    @Test
    void indexed_map_throw_exception_after_put_at_index_out_of_bounds() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);
        map.put(K3, V3);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            map.putAt(3, K3, V3);
        });

        map.putAt(2, K1, V3);
        System.out.println();

        assertThrows(IndexOutOfBoundsException.class, () -> {
            map.putAt(3, K2, V3);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            map.putAt(4, "XX", V3);
        });

        assertEquals(List.of(K2, K3, K1), new ArrayList<>(map.keySet()));
        assertEquals(List.of(V2, V3, V3), new ArrayList<>(map.values()));
    }

    @Test
    void indexed_map_put_all_preserve_order() {
        IndexedHashMap<String, String> source = new IndexedHashMap<>();
        source.put(K1, V1);
        source.put(K2, V2);
        source.put(K3, V3);

        IndexedHashMap<String, String> map = new IndexedHashMap<>();
        map.putAll(source);

        assertEquals(List.of(K1, K2, K3), new ArrayList<>(map.keySet()));
        assertEquals(List.of(V1, V2, V3), new ArrayList<>(map.values()));
    }

    @Test
    void indexed_map_remove_by_key_value() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);
        map.put(K3, V3);

        map.remove(K2, V3); // nothing muste be done

        assertEquals(3, map.size());
        assertEquals(List.of(K1, K2, K3), new ArrayList<>(map.keySet()));
        assertEquals(List.of(V1, V2, V3), new ArrayList<>(map.values()));

        map.remove(K2, V2);

        assertEquals(2, map.size());
        assertEquals(List.of(K1, K3), new ArrayList<>(map.keySet()));
        assertEquals(List.of(V1, V3), new ArrayList<>(map.values()));
    }

    @Test
    void indexed_map_put_if_absent() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);

        Object v = map.putIfAbsent(K2, V3); // already present

        assertEquals(2, map.size());
        assertEquals(V2, v); // current value
        assertEquals(List.of(K1, K2), new ArrayList<>(map.keySet()));
        assertEquals(List.of(V1, V2), new ArrayList<>(map.values()));

        v = map.putIfAbsent(K3, V3); // absent

        assertEquals(3, map.size());
        assertTrue(v == null);
        assertEquals(List.of(K1, K2, K3), new ArrayList<>(map.keySet()));
        assertEquals(List.of(V1, V2, V3), new ArrayList<>(map.values()));
    }

    @Test
    void indexed_map_is_cleared() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();
        map.put(K1, V1);
        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    void indexed_map_foreach_is_ordered() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);
        map.put(K3, V3);

        final List<String> keylist = new ArrayList<>();
        final List<String> valuelist = new ArrayList<>();

        map.forEach((k,v) -> {
            keylist.add(k);
            valuelist.add(v);
        });

        assertEquals(List.of(K1, K2, K3), keylist);
        assertEquals(List.of(V1, V2, V3), valuelist);
    }

    @Test
    void indexed_map_data_order_is_kept_on_merge() {

        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);

        map.merge(K3, V3, (o, n) -> n); // put K3 V3
        map.merge(K2, "N/A", (o, n) -> V3); // update K2 with V3
        map.merge(K1, "N/A", (o, n) -> null); // remove K1

        assertEquals(List.of(K2, K3), new ArrayList<>(map.keySet()));
        assertEquals(List.of(V3, V3), new ArrayList<>(map.values()));
    }

    @Test
    void indexed_map_data_order_is_kept_on_compute() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);

        map.compute(K3, (o, n) -> V3); // put K3 V3
        map.compute(K2, (o, n) -> V3); // update K2 with V3
        map.compute(K1, (o, n) -> null); // remove K1

        assertEquals(List.of(K2, K3), new ArrayList<>(map.keySet()));
        assertEquals(List.of(V3, V3), new ArrayList<>(map.values()));
    }

    @Test
    void indexed_map_data_order_is_kept_on_compute_if_present() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);

        map.computeIfPresent(K3, (o, n) -> V3); // does not put K3 V3
        map.computeIfPresent(K2, (o, n) -> V3); // update K2 with V3
        map.computeIfPresent(K1, (o, n) -> null); // remove K1

        assertEquals(List.of(K2), new ArrayList<>(map.keySet()));
        assertEquals(List.of(V3), new ArrayList<>(map.values()));
    }

    @Test
    void indexed_map_data_order_is_kept_on_compute_if_absent() {
        IndexedHashMap<String, String> map = new IndexedHashMap<>();

        map.put(K1, V1);
        map.put(K2, V2);

        map.computeIfAbsent(K3, (v) -> V3); // put K3 V3
        map.computeIfAbsent(K2, (v) -> V3); // does not update K2 with V3

        assertEquals(List.of(K1, K2, K3), new ArrayList<>(map.keySet()));
        assertEquals(List.of(V1, V2, V3), new ArrayList<>(map.values()));
    }


}
