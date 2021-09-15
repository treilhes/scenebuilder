package com.oracle.javafx.scenebuilder.controls.fxom;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.ext.FileLoader;
import com.oracle.javafx.scenebuilder.core.fxom.util.DesignImage;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ImagePropertyMetadata;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageFileLoader implements FileLoader {

    @Override
    public boolean canLoad(File file) {
        try {
            return new Image(file.toURI().toURL().toString()).isError() == false;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    @Override
    public FXOMObject loadInto(FXOMDocument targetDocument, File file) throws IOException {
        FXOMObject result = null;
        final String fileURL = file.toURI().toURL().toString();
        final Image image = new Image(fileURL);
        
        if (image.isError() == false) {
            final FXOMDocument transientDoc
                    = makeFxomDocumentFromImageURL(image, 200.0);
            result = transientDoc.getFxomRoot();
            if (result != null) {
                result.moveToFxomDocument(targetDocument);
            }
        }
        return result;
    }
    
    private static FXOMDocument makeFxomDocumentFromImageURL(
            Image image, double fitSize) throws IOException {

        assert image != null;
        assert fitSize > 0.0;

        final double imageWidth = image.getWidth();
        final double imageHeight = image.getHeight();

        final double fitWidth, fitHeight;
        final double imageSize = Math.max(imageWidth, imageHeight);
        if (imageSize < fitSize) {
            fitWidth = 0;
            fitHeight = 0;
        } else {
            final double widthScale  = fitSize / imageSize;
            final double heightScale = fitSize / imageHeight;
            final double scale = Math.min(widthScale, heightScale);
            fitWidth = Math.floor(imageWidth * scale);
            fitHeight = Math.floor(imageHeight * scale);
        }

        return makeFxomDocumentFromImageURL(image, fitWidth, fitHeight);
    }

    private static FXOMDocument makeFxomDocumentFromImageURL(
            Image image, double fitWidth, double fitHeight) {

        final FXOMDocument result = new FXOMDocument();
        final FXOMInstance imageView = new FXOMInstance(result, ImageView.class);

        final PropertyName imageName = new PropertyName("image"); //NOCHECK
        final PropertyName fitWidthName = new PropertyName("fitWidth"); //NOCHECK
        final PropertyName fitHeightName = new PropertyName("fitHeight"); //NOCHECK

        final ComponentClassMetadata<?> imageViewMeta
                = Metadata.getMetadata().queryComponentMetadata(ImageView.class);
        final PropertyMetadata imagePropMeta
                = imageViewMeta.lookupProperty(imageName);
        final PropertyMetadata fitWidthPropMeta
                = imageViewMeta.lookupProperty(fitWidthName);
        final PropertyMetadata fitHeightPropMeta
                = imageViewMeta.lookupProperty(fitHeightName);

        assert imagePropMeta instanceof ImagePropertyMetadata;
        assert fitWidthPropMeta instanceof DoublePropertyMetadata;
        assert fitHeightPropMeta instanceof DoublePropertyMetadata;

        final ImagePropertyMetadata imageMeta
                = (ImagePropertyMetadata) imagePropMeta;
        final DoublePropertyMetadata fitWidthMeta
                = (DoublePropertyMetadata) fitWidthPropMeta;
        final DoublePropertyMetadata fitHeightMeta
                = (DoublePropertyMetadata) fitHeightPropMeta;

        imageMeta.setValue(imageView, new DesignImage(image));
        fitWidthMeta.setValue(imageView, fitWidth);
        fitHeightMeta.setValue(imageView, fitHeight);

        result.setFxomRoot(imageView);

        return result;
    }
}
