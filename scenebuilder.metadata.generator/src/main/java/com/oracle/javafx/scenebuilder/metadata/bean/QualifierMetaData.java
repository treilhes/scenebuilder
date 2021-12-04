package com.oracle.javafx.scenebuilder.metadata.bean;

import com.oracle.javafx.scenebuilder.metadata.util.Resources;

public class QualifierMetaData extends AbstractMetaData {

    private Class<?> beanClass;

    protected QualifierMetaData(Class<?> beanClass, Resources resource, String name) {
        super(resource, name);
        this.beanClass = beanClass;
    }

    public String getFxml() {
        return getBundleValue(beanClass, BundleValues.FXML, null);
    }

    public String getDisplayName() {
        return getBundleValue(beanClass, BundleValues.DISPLAY_NAME, null);
    }

    public String getImage() {
        return getBundleValue(beanClass, BundleValues.IMAGE, null);
    }

    public String getImageX2() {
        return getBundleValue(beanClass, BundleValues.IMAGE_X2, null);
    }

    public String getLambdaCheck() {
        return getBundleValue(beanClass, BundleValues.LAMBDA_CHECK, null);
    }

    public String getLabel() {
        return getBundleValue(beanClass, BundleValues.LABEL, null);
    }

    @Override
    public String getBundleValue(Class<?> beanClass, String name, String defaultValue) {
        return super.getBundleValue(beanClass, beanClass.getSimpleName(), name, defaultValue);
    }

    @Override
    public void setBundleValue(Class<?> beanClass, String name, String value) {
        super.setBundleValue(beanClass, beanClass.getSimpleName(), name, value);
    }
}
