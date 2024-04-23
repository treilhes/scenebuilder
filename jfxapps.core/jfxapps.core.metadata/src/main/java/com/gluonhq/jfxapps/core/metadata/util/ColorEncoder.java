/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.gluonhq.jfxapps.core.metadata.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javafx.scene.paint.Color;

/**
 *
 */
public class ColorEncoder {
    
    private static Map<String, Color> standardColors;
    private static Map<Color, String> standardColorNames;
    
    public static String encodeColor(Color color) {
        final String colorName = getStandardColorNames().get(color);
        final String result;
        
        if (colorName != null) {
            result = colorName;
        } else {
            result = makeColorEncoding(color);
        }
        
        return result;
    }

    public static synchronized Map<String, Color> getStandardColors() {
        
        if (standardColors == null) {
            standardColors = new HashMap<>();
            
            standardColors.put("ALICEBLUE", Color.ALICEBLUE); //NOCHECK
            standardColors.put("ANTIQUEWHITE", Color.ANTIQUEWHITE); //NOCHECK
            standardColors.put("AQUA", Color.AQUA); //NOCHECK
            standardColors.put("AQUAMARINE", Color.AQUAMARINE); //NOCHECK
            standardColors.put("AZURE", Color.AZURE); //NOCHECK
            standardColors.put("BEIGE", Color.BEIGE); //NOCHECK
            standardColors.put("BISQUE", Color.BISQUE); //NOCHECK
            standardColors.put("BLACK", Color.BLACK); //NOCHECK
            standardColors.put("BLANCHEDALMOND", Color.BLANCHEDALMOND); //NOCHECK
            standardColors.put("BLUE", Color.BLUE); //NOCHECK
            standardColors.put("BLUEVIOLET", Color.BLUEVIOLET); //NOCHECK
            standardColors.put("BROWN", Color.BROWN); //NOCHECK
            standardColors.put("BURLYWOOD", Color.BURLYWOOD); //NOCHECK
            standardColors.put("CADETBLUE", Color.CADETBLUE); //NOCHECK
            standardColors.put("CHARTREUSE", Color.CHARTREUSE); //NOCHECK
            standardColors.put("CHOCOLATE", Color.CHOCOLATE); //NOCHECK
            standardColors.put("CORAL", Color.CORAL); //NOCHECK
            standardColors.put("CORNFLOWERBLUE", Color.CORNFLOWERBLUE); //NOCHECK
            standardColors.put("CORNSILK", Color.CORNSILK); //NOCHECK
            standardColors.put("CRIMSON", Color.CRIMSON); //NOCHECK
            standardColors.put("CYAN", Color.CYAN); //NOCHECK
            standardColors.put("DARKBLUE", Color.DARKBLUE); //NOCHECK
            standardColors.put("DARKCYAN", Color.DARKCYAN); //NOCHECK
            standardColors.put("DARKGOLDENROD", Color.DARKGOLDENROD); //NOCHECK
            standardColors.put("DARKGRAY", Color.DARKGRAY); //NOCHECK
            standardColors.put("DARKGREEN", Color.DARKGREEN); //NOCHECK
            standardColors.put("DARKGREY", Color.DARKGREY); //NOCHECK
            standardColors.put("DARKKHAKI", Color.DARKKHAKI); //NOCHECK
            standardColors.put("DARKMAGENTA", Color.DARKMAGENTA); //NOCHECK
            standardColors.put("DARKOLIVEGREEN", Color.DARKOLIVEGREEN); //NOCHECK
            standardColors.put("DARKORANGE", Color.DARKORANGE); //NOCHECK
            standardColors.put("DARKORCHID", Color.DARKORCHID); //NOCHECK
            standardColors.put("DARKRED", Color.DARKRED); //NOCHECK
            standardColors.put("DARKSALMON", Color.DARKSALMON); //NOCHECK
            standardColors.put("DARKSEAGREEN", Color.DARKSEAGREEN); //NOCHECK
            standardColors.put("DARKSLATEBLUE", Color.DARKSLATEBLUE); //NOCHECK
            standardColors.put("DARKSLATEGRAY", Color.DARKSLATEGRAY); //NOCHECK
            standardColors.put("DARKSLATEGREY", Color.DARKSLATEGREY); //NOCHECK
            standardColors.put("DARKTURQUOISE", Color.DARKTURQUOISE); //NOCHECK
            standardColors.put("DARKVIOLET", Color.DARKVIOLET); //NOCHECK
            standardColors.put("DEEPPINK", Color.DEEPPINK); //NOCHECK
            standardColors.put("DEEPSKYBLUE", Color.DEEPSKYBLUE); //NOCHECK
            standardColors.put("DIMGRAY", Color.DIMGRAY); //NOCHECK
            standardColors.put("DIMGREY", Color.DIMGREY); //NOCHECK
            standardColors.put("DODGERBLUE", Color.DODGERBLUE); //NOCHECK
            standardColors.put("FIREBRICK", Color.FIREBRICK); //NOCHECK
            standardColors.put("FLORALWHITE", Color.FLORALWHITE); //NOCHECK
            standardColors.put("FORESTGREEN", Color.FORESTGREEN); //NOCHECK
            standardColors.put("FUCHSIA", Color.FUCHSIA); //NOCHECK
            standardColors.put("GAINSBORO", Color.GAINSBORO); //NOCHECK
            standardColors.put("GHOSTWHITE", Color.GHOSTWHITE); //NOCHECK
            standardColors.put("GOLD", Color.GOLD); //NOCHECK
            standardColors.put("GOLDENROD", Color.GOLDENROD); //NOCHECK
            standardColors.put("GRAY", Color.GRAY); //NOCHECK
            standardColors.put("GREEN", Color.GREEN); //NOCHECK
            standardColors.put("GREENYELLOW", Color.GREENYELLOW); //NOCHECK
            standardColors.put("GREY", Color.GREY); //NOCHECK
            standardColors.put("HONEYDEW", Color.HONEYDEW); //NOCHECK
            standardColors.put("HOTPINK", Color.HOTPINK); //NOCHECK
            standardColors.put("INDIANRED", Color.INDIANRED); //NOCHECK
            standardColors.put("INDIGO", Color.INDIGO); //NOCHECK
            standardColors.put("IVORY", Color.IVORY); //NOCHECK
            standardColors.put("KHAKI", Color.KHAKI); //NOCHECK
            standardColors.put("LAVENDER", Color.LAVENDER); //NOCHECK
            standardColors.put("LAVENDERBLUSH", Color.LAVENDERBLUSH); //NOCHECK
            standardColors.put("LAWNGREEN", Color.LAWNGREEN); //NOCHECK
            standardColors.put("LEMONCHIFFON", Color.LEMONCHIFFON); //NOCHECK
            standardColors.put("LIGHTBLUE", Color.LIGHTBLUE); //NOCHECK
            standardColors.put("LIGHTCORAL", Color.LIGHTCORAL); //NOCHECK
            standardColors.put("LIGHTCYAN", Color.LIGHTCYAN); //NOCHECK
            standardColors.put("LIGHTGOLDENRODYELLOW", Color.LIGHTGOLDENRODYELLOW); //NOCHECK
            standardColors.put("LIGHTGRAY", Color.LIGHTGRAY); //NOCHECK
            standardColors.put("LIGHTGREEN", Color.LIGHTGREEN); //NOCHECK
            standardColors.put("LIGHTGREY", Color.LIGHTGREY); //NOCHECK
            standardColors.put("LIGHTPINK", Color.LIGHTPINK); //NOCHECK
            standardColors.put("LIGHTSALMON", Color.LIGHTSALMON); //NOCHECK
            standardColors.put("LIGHTSEAGREEN", Color.LIGHTSEAGREEN); //NOCHECK
            standardColors.put("LIGHTSKYBLUE", Color.LIGHTSKYBLUE); //NOCHECK
            standardColors.put("LIGHTSLATEGRAY", Color.LIGHTSLATEGRAY); //NOCHECK
            standardColors.put("LIGHTSLATEGREY", Color.LIGHTSLATEGREY); //NOCHECK
            standardColors.put("LIGHTSTEELBLUE", Color.LIGHTSTEELBLUE); //NOCHECK
            standardColors.put("LIGHTYELLOW", Color.LIGHTYELLOW); //NOCHECK
            standardColors.put("LIME", Color.LIME); //NOCHECK
            standardColors.put("LIMEGREEN", Color.LIMEGREEN); //NOCHECK
            standardColors.put("LINEN", Color.LINEN); //NOCHECK
            standardColors.put("MAGENTA", Color.MAGENTA); //NOCHECK
            standardColors.put("MAROON", Color.MAROON); //NOCHECK
            standardColors.put("MEDIUMAQUAMARINE", Color.MEDIUMAQUAMARINE); //NOCHECK
            standardColors.put("MEDIUMBLUE", Color.MEDIUMBLUE); //NOCHECK
            standardColors.put("MEDIUMORCHID", Color.MEDIUMORCHID); //NOCHECK
            standardColors.put("MEDIUMPURPLE", Color.MEDIUMPURPLE); //NOCHECK
            standardColors.put("MEDIUMSEAGREEN", Color.MEDIUMSEAGREEN); //NOCHECK
            standardColors.put("MEDIUMSLATEBLUE", Color.MEDIUMSLATEBLUE); //NOCHECK
            standardColors.put("MEDIUMSPRINGGREEN", Color.MEDIUMSPRINGGREEN); //NOCHECK
            standardColors.put("MEDIUMTURQUOISE", Color.MEDIUMTURQUOISE); //NOCHECK
            standardColors.put("MEDIUMVIOLETRED", Color.MEDIUMVIOLETRED); //NOCHECK
            standardColors.put("MIDNIGHTBLUE", Color.MIDNIGHTBLUE); //NOCHECK
            standardColors.put("MINTCREAM", Color.MINTCREAM); //NOCHECK
            standardColors.put("MISTYROSE", Color.MISTYROSE); //NOCHECK
            standardColors.put("MOCCASIN", Color.MOCCASIN); //NOCHECK
            standardColors.put("NAVAJOWHITE", Color.NAVAJOWHITE); //NOCHECK
            standardColors.put("NAVY", Color.NAVY); //NOCHECK
            standardColors.put("OLDLACE", Color.OLDLACE); //NOCHECK
            standardColors.put("OLIVE", Color.OLIVE); //NOCHECK
            standardColors.put("OLIVEDRAB", Color.OLIVEDRAB); //NOCHECK
            standardColors.put("ORANGE", Color.ORANGE); //NOCHECK
            standardColors.put("ORANGERED", Color.ORANGERED); //NOCHECK
            standardColors.put("ORCHID", Color.ORCHID); //NOCHECK
            standardColors.put("PALEGOLDENROD", Color.PALEGOLDENROD); //NOCHECK
            standardColors.put("PALEGREEN", Color.PALEGREEN); //NOCHECK
            standardColors.put("PALETURQUOISE", Color.PALETURQUOISE); //NOCHECK
            standardColors.put("PALEVIOLETRED", Color.PALEVIOLETRED); //NOCHECK
            standardColors.put("PAPAYAWHIP", Color.PAPAYAWHIP); //NOCHECK
            standardColors.put("PEACHPUFF", Color.PEACHPUFF); //NOCHECK
            standardColors.put("PERU", Color.PERU); //NOCHECK
            standardColors.put("PINK", Color.PINK); //NOCHECK
            standardColors.put("PLUM", Color.PLUM); //NOCHECK
            standardColors.put("POWDERBLUE", Color.POWDERBLUE); //NOCHECK
            standardColors.put("PURPLE", Color.PURPLE); //NOCHECK
            standardColors.put("RED", Color.RED); //NOCHECK
            standardColors.put("ROSYBROWN", Color.ROSYBROWN); //NOCHECK
            standardColors.put("ROYALBLUE", Color.ROYALBLUE); //NOCHECK
            standardColors.put("SADDLEBROWN", Color.SADDLEBROWN); //NOCHECK
            standardColors.put("SALMON", Color.SALMON); //NOCHECK
            standardColors.put("SANDYBROWN", Color.SANDYBROWN); //NOCHECK
            standardColors.put("SEAGREEN", Color.SEAGREEN); //NOCHECK
            standardColors.put("SEASHELL", Color.SEASHELL); //NOCHECK
            standardColors.put("SIENNA", Color.SIENNA); //NOCHECK
            standardColors.put("SILVER", Color.SILVER); //NOCHECK
            standardColors.put("SKYBLUE", Color.SKYBLUE); //NOCHECK
            standardColors.put("SLATEBLUE", Color.SLATEBLUE); //NOCHECK
            standardColors.put("SLATEGRAY", Color.SLATEGRAY); //NOCHECK
            standardColors.put("SLATEGREY", Color.SLATEGREY); //NOCHECK
            standardColors.put("SNOW", Color.SNOW); //NOCHECK
            standardColors.put("SPRINGGREEN", Color.SPRINGGREEN); //NOCHECK
            standardColors.put("STEELBLUE", Color.STEELBLUE); //NOCHECK
            standardColors.put("TAN", Color.TAN); //NOCHECK
            standardColors.put("TEAL", Color.TEAL); //NOCHECK
            standardColors.put("THISTLE", Color.THISTLE); //NOCHECK
            standardColors.put("TOMATO", Color.TOMATO); //NOCHECK
            standardColors.put("TRANSPARENT", Color.TRANSPARENT); //NOCHECK
            standardColors.put("TURQUOISE", Color.TURQUOISE); //NOCHECK
            standardColors.put("VIOLET", Color.VIOLET); //NOCHECK
            standardColors.put("WHEAT", Color.WHEAT); //NOCHECK
            standardColors.put("WHITE", Color.WHITE); //NOCHECK
            standardColors.put("WHITESMOKE", Color.WHITESMOKE); //NOCHECK
            standardColors.put("YELLOW", Color.YELLOW); //NOCHECK
            standardColors.put("YELLOWGREEN", Color.YELLOWGREEN); //NOCHECK

            standardColors = Collections.unmodifiableMap(standardColors);
        }
        
        return standardColors;
    }
    
    public static synchronized Map<Color, String> getStandardColorNames() {
        
        if (standardColorNames == null) {
            standardColorNames = new HashMap<>();
            for (Map.Entry<String, Color> e : getStandardColors().entrySet()) {
                standardColorNames.put(e.getValue(), e.getKey());
            }
            standardColorNames = Collections.unmodifiableMap(standardColorNames);
        }
        
        return standardColorNames;
    }
    
    
    public static String encodeColorToRGBA(Color color) {
        final String result;
        if (color == null) {
            result = "null";//NOCHECK
        } else {
            final int red = (int) (color.getRed() * 255);
            final int green = (int) (color.getGreen() * 255);
            final int blue = (int) (color.getBlue() * 255);
            result = "rgba("+red+","+green+","+blue +","+color.getOpacity()+")";//NOCHECK
        }
        return result;    
    }

    /*
     * Private
     */
    private static String makeColorEncoding(Color c) {
        final int red, green, blue, alpha;
        final String result;
        
        red   = (int) Math.round(c.getRed() * 255.0);
        green = (int) Math.round(c.getGreen() * 255.0);
        blue  = (int) Math.round(c.getBlue() * 255.0);
        alpha = (int) Math.round(c.getOpacity() * 255.0);
        if (alpha == 255) {
            result = String.format((Locale)null, "#%02x%02x%02x", red, green, blue); //NOCHECK
        } else {
            result = String.format((Locale)null, "#%02x%02x%02x%02x", red, green, blue, alpha); //NOCHECK
        }
        
        return result;
    }
    
}
