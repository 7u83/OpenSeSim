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

import java.awt.Font;
import java.awt.Toolkit;
import java.util.Enumeration;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public final class HiDPIScaler {
    public static void autoScale() {
        double scale = Toolkit.getDefaultToolkit().getScreenResolution() / 96.0;
        if (scale <= 1.05) {
            return; // kein HiDPI
        }

        UIDefaults ui = UIManager.getLookAndFeelDefaults();

        // Basisfont prüfen (sicherstellen, dass wir nicht doppelt skalieren)
        Font baseFont = ui.getFont("defaultFont");
        if (baseFont == null) {
            baseFont = UIManager.getFont("Label.font");
        }
        if (baseFont != null && baseFont.getSize() > 13) {
            return; // LaF skaliert bereits
        }

        // Durch alle Keys iterieren (Java 8-kompatibel)
        for (Enumeration<Object> e = ui.keys(); e.hasMoreElements(); ) {
            Object key = e.nextElement();
            Object value = ui.get(key);
            if (value instanceof Font) {
                Font f = (Font) value;
                float newSize = (float) (f.getSize2D() * scale);
                // Verwende FontUIResource, damit UIManager den Font als Resource erkennt
                ui.put(key, new FontUIResource(f.deriveFont(newSize)));
            }
        }
    }
}