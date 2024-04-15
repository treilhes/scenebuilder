package com.gluonhq.jfxapps.metadata.plugin.data;

public class ParameterOverride {
    String cls;
    String overridedBy;
    public String getCls() {
        return cls;
    }
    public void setCls(String cls) {
        this.cls = cls;
    }
    public String getOverridedBy() {
        return overridedBy;
    }
    public void setOverridedBy(String overridedBy) {
        this.overridedBy = overridedBy;
    }
    @Override
    public String toString() {
        return "ParameterOverride [cls=" + cls + ", overridedBy=" + overridedBy + "]";
    }


}
