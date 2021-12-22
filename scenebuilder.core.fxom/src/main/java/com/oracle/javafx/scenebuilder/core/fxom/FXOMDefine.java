package com.oracle.javafx.scenebuilder.core.fxom;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueElement;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;

public class FXOMDefine extends FXOMVirtual {

    private final List<FXOMObject> items = new ArrayList<>();

    FXOMDefine(FXOMDocument fxomDocument, GlueElement glueElement, List<FXOMObject> children) {
        super(fxomDocument, glueElement, null);

        if (children != null) {
            items.addAll(children);
        }

    }

    public List<FXOMObject> getItems() {
        return Collections.unmodifiableList(items);
    }

    /* Reserved to FXOMObject.addToParentCollection() private use */
    void addValue(int index, FXOMObject item) {
        assert item != null;
        assert item.getParentDefine() == this;
        assert items.contains(item) == false;
        if (index == -1) {
            items.add(item);
        } else {
            items.add(index, item);
        }
    }

    /* Reserved to FXOMObject.removeFromParentCollection() private use */
    void removeValue(FXOMObject item) {
        assert item != null;
        assert item.getParentProperty() == null;
        assert items.contains(item);
        items.remove(item);
    }

    @Override
    protected void collectScripts(String source, List<FXOMScript> result) {
        for (FXOMObject i : getChildObjects()) {
            i.collectScripts(source, result);
        }
    }

    @Override
    protected void collectComments(List<FXOMComment> result) {
        for (FXOMObject i : getChildObjects()) {
            i.collectComments(result);
        }
    }

    /*
     * FXOMObject
     */

    @Override
    public List<FXOMObject> getChildObjects() {
        return Collections.unmodifiableList(items);
    }


    @Override
    public FXOMObject searchWithSceneGraphObject(Object sceneGraphObject) {
        FXOMObject result;

        result = super.searchWithSceneGraphObject(sceneGraphObject);
        if (result == null) {
            final Iterator<FXOMObject> it = items.iterator();
            while ((result == null) && it.hasNext()) {
                final FXOMObject item = it.next();
                result = item.searchWithSceneGraphObject(sceneGraphObject);
            }
        }

        return result;
    }

    @Override
    public FXOMObject searchWithFxId(String fxId) {
        FXOMObject result;

        result = super.searchWithFxId(fxId);
        if (result == null) {
            final Iterator<FXOMObject> it = items.iterator();
            while ((result == null) && it.hasNext()) {
                final FXOMObject item = it.next();
                result = item.searchWithFxId(fxId);
            }
        }

        return result;
    }

    @Override
    protected void collectDeclaredClasses(Set<Class<?>> result) {
        assert result != null;

        for (FXOMObject i : items) {
            i.collectDeclaredClasses(result);
        }
    }

    @Override
    protected void collectProperties(PropertyName propertyName, List<FXOMProperty> result) {
        assert propertyName != null;
        assert result != null;

        for (FXOMObject i : items) {
            i.collectProperties(propertyName, result);
        }
    }

    @Override
    protected void collectNullProperties(List<FXOMPropertyT> result) {
        assert result != null;

        for (FXOMObject i : items) {
            i.collectNullProperties(result);
        }
    }

    @Override
    protected void collectPropertiesT(List<FXOMPropertyT> result) {
        assert result != null;

        for (FXOMObject i : items) {
            i.collectPropertiesT(result);
        }
    }

    @Override
    protected void collectReferences(String source, List<FXOMIntrinsic> result) {
        for (FXOMObject i : items) {
            i.collectReferences(source, result);
        }
    }

    @Override
    protected void collectReferences(String source, FXOMObject scope, List<FXOMNode> result) {
        if ((scope == null) || (scope != this)) {
            for (FXOMObject i : items) {
                i.collectReferences(source, scope, result);
            }
        }
    }

    @Override
    protected void collectIncludes(String source, List<FXOMIntrinsic> result) {
        for (FXOMObject i : items) {
            i.collectIncludes(source, result);
        }
    }

    @Override
    protected void collectFxIds(Map<String, FXOMObject> result) {
        final String fxId = getFxId();
        if (fxId != null) {
            result.put(fxId, this);
        }

        for (FXOMObject i : items) {
            i.collectFxIds(result);
        }
    }

    @Override
    protected void collectObjectWithSceneGraphObjectClass(Class<?> sceneGraphObjectClass, List<FXOMObject> result) {
        if (getSceneGraphObject() != null) {
            if (getSceneGraphObject().getClass() == sceneGraphObjectClass) {
                result.add(this);
            }
            for (FXOMObject i : items) {
                i.collectObjectWithSceneGraphObjectClass(sceneGraphObjectClass, result);
            }
        }
    }

    @Override
    protected void collectEventHandlers(List<FXOMPropertyT> result) {
        if (getSceneGraphObject() != null) {
            for (FXOMObject i : items) {
                i.collectEventHandlers(result);
            }
        }
    }

    /*
     * FXOMNode
     */

    @Override
    protected void changeFxomDocument(FXOMDocument destination) {

        super.changeFxomDocument(destination);
        for (FXOMObject i : items) {
            i.changeFxomDocument(destination);
        }
    }

    @Override
    public void documentLocationWillChange(URL newLocation) {
        for (FXOMObject i : items) {
            i.documentLocationWillChange(newLocation);
        }
    }

}
