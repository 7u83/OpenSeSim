/*
 * Copyright (c) 2017, 2025 7u83 <7u83@mail.ru>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package gui.util;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tube
 */
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class ColorUtils {

    private ColorUtils() {}

    private static final Map<String, Color> CACHE = new HashMap<>();

    private static String key(Color base, Color bg) {
        return base.getRGB() + ":" + bg.getRGB();
    }

    // -----------------------------------------------------------
    // Convenience-Methoden
    // -----------------------------------------------------------
    public static Color readableRed(Color background) {
        return readable(new Color(255, 0, 0), background);
    }

    public static Color readableGreen(Color background) {
        return readable(new Color(0, 180, 0), background);
    }

    public static Color readableGrey(Color background) {
        return readable(new Color(128, 128, 128), background);
    }

    // -----------------------------------------------------------
    // Generische Methode: beliebige Farbe
    // -----------------------------------------------------------
    public static Color readable(Color base, Color background) {
        String k = key(base, background);
        if (CACHE.containsKey(k))
            return CACHE.get(k);

        Color result;
        if (isGray(base)) {
            // Graustufen → nur Helligkeit anpassen
            result = adjustGray(base, background);
        } else {
            // Farbige Farben → Helligkeit anpassen, Sättigung hoch halten
            result = adjustColored(base, background);
        }

        CACHE.put(k, result);
        return result;
    }

    // -----------------------------------------------------------
    // Graue Farbe erkennen
    // -----------------------------------------------------------
    private static boolean isGray(Color c) {
        int max = Math.max(c.getRed(), Math.max(c.getGreen(), c.getBlue()));
        int min = Math.min(c.getRed(), Math.min(c.getGreen(), c.getBlue()));
        return (max - min) < 16; // kleine Schwankungen tolerieren
    }

    // -----------------------------------------------------------
    // Graue Farbe → nur Helligkeit anpassen
    // -----------------------------------------------------------
    private static Color adjustGray(Color base, Color background) {
        float bgLum = (float) luminance(background);
        float l = rgbToHsl(base)[2];

        if (bgLum < 0.5f)
            l = Math.max(l, 0.8f); // dunkel → helles Grau
        else
            l = Math.min(l, 0.2f); // hell → dunkles Grau

        return hslToRgb(new float[]{0f, 0f, l});
    }

    // -----------------------------------------------------------
    // Farbige Farbe → Helligkeit anpassen, Sättigung hoch halten
    // -----------------------------------------------------------
    private static Color adjustColored(Color base, Color background) {
        float[] hsl = rgbToHsl(base);
        float h = hsl[0];
        float s = Math.max(hsl[1], 0.6f); // Mindest-Sättigung
        float l = hsl[2];

        float bgLum = (float) luminance(background);

        // Ziel-Luminanz
        float targetL;
        if (bgLum < 0.5f) {
            targetL = Math.min(0.8f, l + 0.3f); // dunkel → etwas heller
        } else {
            targetL = Math.max(0.2f, l - 0.3f); // hell → etwas dunkler
        }

        return hslToRgb(new float[]{h, s, targetL});
    }

    // -----------------------------------------------------------
    // Luminanz / Kontrast
    // -----------------------------------------------------------
    private static double luminance(Color c) {
        double r = c.getRed() / 255.0;
        double g = c.getGreen() / 255.0;
        double b = c.getBlue() / 255.0;
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    // -----------------------------------------------------------
    // RGB <-> HSL
    // -----------------------------------------------------------
    private static float[] rgbToHsl(Color c) {
        float r = c.getRed() / 255f;
        float g = c.getGreen() / 255f;
        float b = c.getBlue() / 255f;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float h, s, l;
        l = (max + min) / 2f;

        if (max == min) {
            h = s = 0f; // achromatic
        } else {
            float d = max - min;
            s = l > 0.5f ? d / (2f - max - min) : d / (max + min);

            if (max == r) h = (g - b) / d + (g < b ? 6f : 0f);
            else if (max == g) h = (b - r) / d + 2f;
            else h = (r - g) / d + 4f;

            h /= 6f;
        }

        return new float[]{h, s, l};
    }

    private static Color hslToRgb(float[] hsl) {
        float r, g, b;
        float h = hsl[0], s = hsl[1], l = hsl[2];

        if (s == 0f) {
            r = g = b = l; // achromatic
        } else {
            float q = l < 0.5f ? l * (1f + s) : l + s - l * s;
            float p = 2f * l - q;
            r = hueToRgb(p, q, h + 1f / 3f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1f / 3f);
        }

        return new Color(clamp(r), clamp(g), clamp(b));
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0f) t += 1f;
        if (t > 1f) t -= 1f;
        if (t < 1f / 6f) return p + (q - p) * 6f * t;
        if (t < 1f / 2f) return q;
        if (t < 2f / 3f) return p + (q - p) * (2f / 3f - t) * 6f;
        return p;
    }

    private static int clamp(float v) {
        return Math.max(0, Math.min(255, Math.round(v * 255)));
    }
}
