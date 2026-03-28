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
package org.fxml.serializer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMDocumentFactory;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.transform.DefaultFxmlSerializer;
import com.gluonhq.jfxapps.core.metadata.property.ComponentPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.PropertyGroupMetadata;
import com.gluonhq.jfxapps.core.metadata.property.PropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.metadata.javafx.JavafxMetadataExtension;

public class FXMLSerializer{

    public static Metadata metadata = loadMetadata();

    public void serialize(Object object, File target) {

    }
    public String serialize(Object object) {
        FXOMDocument document = FXOMDocumentFactory.DEFAULT.newDocument();
        FXOMInstance instance = createInstance(object, document);
        document.setFxomRoot(instance);
        DefaultFxmlSerializer serializer = new DefaultFxmlSerializer();
        return serializer.serialize(document);
    }
    private FXOMInstance createInstance(Object object, FXOMDocument document) {

        Class<?> declaredClass = object.getClass();

        var cmp = metadata.queryProperties(declaredClass);
        Set<PropertyMetadata> properties = metadata.queryProperties(declaredClass);

        FXOMInstance instance = new FXOMInstance(document, declaredClass);
        instance.setSceneGraphObject(object);

        for (PropertyMetadata<?> property : properties) {
            if (property instanceof ValuePropertyMetadata<?> vpm) {

                if (!vpm.isReadWrite() || vpm.isTransient()) {
                    continue;
                }

                if (vpm.isGroup() && vpm instanceof PropertyGroupMetadata<?> pgm) {
                    pgm.getPropertiesMap().values().forEach(p -> attachPropertyT(document, instance, p));
                } else {
                    attachPropertyT(document, instance, vpm);
                }

            } else if (property instanceof ComponentPropertyMetadata cpm) {

                if (cpm.isCollection()) {
                    final Collection<?> values = (Collection<?>)cpm.getName().getValue(object);

                    if (values.isEmpty()) {
                        continue;
                    }

                    var children = values.stream().map(o -> createInstance(o, document))
                            .map(FXOMObject.class::cast)
                            .toList();
                    final FXOMPropertyC collection = new FXOMPropertyC(document, cpm.getName(), children);
                    collection.addToParentInstance(-1, instance);
                } else {
                    final Object value = cpm.getName().getValue(object);

                    if (value == null) {
                        continue;
                    }

                    var fxomObject = createInstance(value, document);
                    final FXOMPropertyC collection = new FXOMPropertyC(document, cpm.getName(), fxomObject);
                    collection.addToParentInstance(-1, instance);
                }
            }


        }
        return instance;
    }
    private void attachPropertyT(FXOMDocument document, FXOMInstance instance, PropertyMetadata<?> property) {
        ValuePropertyMetadata<?> vpm = (ValuePropertyMetadata<?>)property;
        Object value = vpm.getValueInSceneGraphObject(instance);
        if (value != null) {
            vpm.setValueObject(instance, value);
        }
    }



    private static Metadata loadMetadata() {
        JavafxMetadataExtension ext = new JavafxMetadataExtension();

        List<Class<?>> classes = new ArrayList<>(ext.exportedContextClasses());
        classes.add(Metadata.class);

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(classes.toArray(new Class<?>[0]));

        return context.getBean(Metadata.class);
    }
}
