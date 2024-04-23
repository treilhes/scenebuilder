package com.oracle.javafx.scenebuilder.controls.fxom;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.fxom.ext.FileLoader;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;

import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class MediaFileLoader implements FileLoader {

    @Override
    public boolean canLoad(File file) {
        try {
            return new Media(file.toURI().toURL().toString()).getError() == null;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    @Override
    public FXOMObject loadInto(FXOMDocument targetDocument, File file) throws IOException {
        FXOMObject result = null;
        final String fileURL = file.toURI().toURL().toString();
        
        try {
            final Media media = new Media(fileURL);
            if (media.getError() == null) {
                final FXOMDocument transientDoc
                        = makeFxomDocumentFromMedia(media, 200.0);
                result = transientDoc.getFxomRoot();
                if (result != null) {
                    result.moveToFxomDocument(targetDocument);
                }
            } else {
                throw new IOException(media.getError());
            }
        } catch(MediaException x) {
            throw new IOException(x);
        }
        
        return result;
    }
    

    private static FXOMDocument makeFxomDocumentFromMedia(
            Media media, double fitSize) throws IOException {

        assert media != null;
        assert fitSize > 0.0;

        final double mediaWidth = media.getWidth();
        final double mediaHeight = media.getHeight();

        final double fitWidth, fitHeight;
        final double mediaSize = Math.max(mediaWidth, mediaHeight);
        if (mediaSize < fitSize) {
            fitWidth = 0;
            fitHeight = 0;
        } else {
            final double widthScale  = fitSize / mediaSize;
            final double heightScale = fitSize / mediaHeight;
            final double scale = Math.min(widthScale, heightScale);
            fitWidth = Math.floor(mediaWidth * scale);
            fitHeight = Math.floor(mediaHeight * scale);
        }

        return makeFxomDocumentFromMedia(media, fitWidth, fitHeight);
    }

    private static FXOMDocument makeFxomDocumentFromMedia(
            Media media, double fitWidth, double fitHeight) {

        /*
         * <MediaView fitWidth="200" fitHeight="2003 >
         *   <mediaPlayer>
         *     <MediaPlayer cycleCount="-1">
         *       <media>
         *         <Media>
         *           <source>
         *              <URL value="file:/Users/elp/Dekstop/blah.flv" />
         *           </source>
         *         <Media/>
         *       </media>
         *     </MediaPlayer>
         *   </mediaPlayer>
         * </MediaView>
         */

        final FXOMDocument result = new FXOMDocument();

        /*
         * URL
         */
        final PropertyName valueName
                = new PropertyName("value"); //NOCHECK
        final FXOMPropertyT valueProperty
                = new FXOMPropertyT(result, valueName, media.getSource());
        final FXOMInstance urlInstance
                = new FXOMInstance(result, URL.class);
        valueProperty.addToParentInstance(-1, urlInstance);

        /*
         * Media
         */
        final PropertyName sourceName
                = new PropertyName("source"); //NOCHECK
        final FXOMPropertyC sourceProperty
                = new FXOMPropertyC(result, sourceName, urlInstance);
        final FXOMInstance mediaInstance
                = new FXOMInstance(result, Media.class);
        sourceProperty.addToParentInstance(-1, mediaInstance);

        /*
         * MediaPlayer
         */
        final PropertyName mediaName
                = new PropertyName("media"); //NOCHECK
        final FXOMPropertyC mediaProperty
                = new FXOMPropertyC(result, mediaName, mediaInstance);
        final FXOMInstance mediaPlayerInstance
                = new FXOMInstance(result, MediaPlayer.class);
        mediaProperty.addToParentInstance(-1, mediaPlayerInstance);

        /*
         * MediaView
         */
        final PropertyName mediaPlayerName
                = new PropertyName("mediaPlayer"); //NOCHECK
        final FXOMPropertyC mediaPlayerProperty
                = new FXOMPropertyC(result, mediaPlayerName, mediaPlayerInstance);
        final PropertyName fitWidthName
                = new PropertyName("fitWidth"); //NOCHECK
        final FXOMPropertyT fitWidthProperty
                = new FXOMPropertyT(result, fitWidthName, String.valueOf(fitWidth));
        final PropertyName fitHeightName
                = new PropertyName("fitHeight"); //NOCHECK
        final FXOMPropertyT fitHeightProperty
                = new FXOMPropertyT(result, fitHeightName, String.valueOf(fitHeight));
        final FXOMInstance mediaView
                = new FXOMInstance(result, MediaView.class);
        mediaPlayerProperty.addToParentInstance(-1, mediaView);
        fitWidthProperty.addToParentInstance(-1, mediaView);
        fitHeightProperty.addToParentInstance(-1, mediaView);

        result.setFxomRoot(mediaView);

        return result;
    }

}
