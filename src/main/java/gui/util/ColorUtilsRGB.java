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

/**
 *
 * @author tube
 */
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public final class ColorUtilsRGB {

    private ColorUtilsRGB() {}

    // Cache für Performance
    private static final Map<String, Color> CACHE = new HashMap<>();

    // Schlüssel für Cache erzeugen
    private static String key(Color base, Color bg) {
        return base.getRGB() + ":" + bg.getRGB();
    }

    // Öffentliche Hilfsmethoden
    public static Color readableRed(Color background) {
        return readable(new Color(255, 0, 0), background);
    }

    public static Color readableGreen(Color background) {
        // Grün wirkt bei vielen Dark-Themes zu dunkel – deshalb etwas heller starten
        return readable(new Color(0, 180, 0), background);
    }

    /**
     *  Erzeugt eine zum Hintergrund gut lesbare Variante der Basisfarbe.
     *  Funktioniert für Light & Dark Themes ohne jede Spezialsauce.
     */
    public static Color readable(Color base, Color background) {
        double minct=1.9;
        
        String k = key(base, background);
        if (CACHE.containsKey(k))
            return CACHE.get(k);

        // Wenn Kontrast bereits OK ist → fertig
        if (contrast(base, background) >= minct) {
            //CACHE.put(k, base);
            return base;
        }

        // Wir probieren: zuerst aufhellen, dann abdunkeln
        Color best = base;
        double bestContrast = contrast(base, background);

        // Aufhellen
        for (int i = 0; i <= 255; i += 1) {
            Color c = lighten(base, i);
            double ct = contrast(c, background);
            if (ct >= minct) {
//                CACHE.put(k, c);
                return c;
            }
            if (ct > bestContrast) {
                best = c;
                bestContrast = ct;
            }
        }

        // Abdunkeln
        for (int i = 0; i <= 255; i += 1) {
            Color c = darken(base, i);
            double ct = contrast(c, background);
            if (ct >= minct) {
          //      CACHE.put(k, c);
                return c;
            }
            if (ct > bestContrast) {
                best = c;
                bestContrast = ct;
            }
        }

        // Beste gefundene Farbe zurückgeben
        //CACHE.put(k, best);
        return best;
    }

    // -----------------------------------------------------------
    // Hilfsfunktionen
    // -----------------------------------------------------------

    // W3C-Luminanzberechnung (Rec. 709)
    private static double luminance(Color c) {
        double r = c.getRed()   / 255.0;
        double g = c.getGreen() / 255.0;
        double b = c.getBlue()  / 255.0;

        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    // W3C-Kontrast
    private static double contrast(Color a, Color b) {
        double l1 = luminance(a);
        double l2 = luminance(b);
        return (Math.max(l1, l2) + 0.05) / (Math.min(l1, l2) + 0.05);
    }

    // Farbe um einen Wert aufhellen
    private static Color lighten(Color c, int amount) {
        return new Color(
                clamp(c.getRed()   + amount),
                clamp(c.getGreen() + amount),
                clamp(c.getBlue()  + amount)
        );
    }

    // Farbe um einen Wert abdunkeln
    private static Color darken(Color c, int amount) {
        return new Color(
                clamp(c.getRed()   - amount),
                clamp(c.getGreen() - amount),
                clamp(c.getBlue()  - amount)
        );
    }

    // Begrenzung 0–255
    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }
}
