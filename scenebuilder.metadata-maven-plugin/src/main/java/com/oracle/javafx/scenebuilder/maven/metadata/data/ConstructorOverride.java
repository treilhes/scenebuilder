package com.oracle.javafx.scenebuilder.maven.metadata.data;

import java.util.List;

public class ConstructorOverride {

    String cls;

    List<ParameterOverride> parameterOverrides;

    public String getCls() {
        return cls;
    }
    public void setCls(String cls) {
        this.cls = cls;
    }

    public List<ParameterOverride> getParameterOverrides() {
        return parameterOverrides;
    }
    public void setParameterOverrides(List<ParameterOverride> parameterOverrides) {
        this.parameterOverrides = parameterOverrides;
    }
    @Override
    public String toString() {
        return "ConstructorOverride [cls=" + cls + ", parameters=" + parameterOverrides + "]";
    }


}
