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
package sesim.util;

import java.util.function.Function;

/**
 *
 * @author tube
 */
public class Math {

    public static long toLong(double v) {
        if (v >= 0.0) {
            return (long) (v + 0.5);
        } else {
            return (long) (v - 0.5);
        }
    }
    
    public static long toFixedLong(double v, double f) {
        return toLong(v*f);
    }

    /**
     * Berechnet die rechte Grenze für Preistabilität bei gegebener linker Grenze
     * @param leftBound Linke Grenze als Dezimalzahl (z.B. -0.02 für -2%)
     * @return Rechte Grenze für Stabilität als Dezimalzahl
     */
    public static double calculateEquilibriumPoint(double leftBound) {
        // Überprüfe Eingabe
        if (leftBound >= 0) {
            throw new IllegalArgumentException("Linke Grenze muss negativ sein");
        }
        
        // Die Funktion, deren Nullstelle wir suchen
        Function<Double, Double> equation = x -> {
            double term1 = (1 + x) * Math.log(1 + x) - (1 + x);
            double term2 = (1 + leftBound) * Math.log(1 + leftBound) - (1 + leftBound);
            return term1 - term2;
        };
        
        // Startwerte für die numerische Lösung
        double lower = -leftBound;  // Symmetrischer Startwert
        double upper = -leftBound + 0.01;  // Etwas höher
        
        // Sicherstellen, dass wir die Nullstelle einschließen
        while (equation.apply(lower) * equation.apply(upper) > 0) {
            upper += 0.001;
        }
        
        // Bisektionsverfahren (Binary Search)
        final double PRECISION = 1e-8;
        double mid = 0;
        
        for (int i = 0; i < 100; i++) { // Max 100 Iterationen
            mid = (lower + upper) / 2;
            double result = equation.apply(mid);
            
            if (Math.abs(result) < PRECISION) {
                break;
            }
            
            if (result * equation.apply(lower) < 0) {
                upper = mid;
            } else {
                lower = mid;
            }
        }
        
        return mid;
    }
    
    /**
     * Vereinfachte Version mit Näherungsformel (für kleine Werte ausreichend genau)
     */
    public static double calculateEquilibriumPointApprox(double leftBound) {
        if (leftBound >= 0) {
            throw new IllegalArgumentException("Linke Grenze muss negativ sein");
        }
        // Näherungsformel: x ≈ |a| + a²/2
        double a = Math.abs(leftBound);
        return a + (a * a) / 2;
    }
    
    // Test und Beispiel
    public static void main(String[] args) {
        // Test für verschiedene Werte
        double[] testValues = {-0.02, -0.03, -0.05, -0.10};
        
        System.out.println("Linke Grenze | Exakte rechte Grenze | Näherung");
        System.out.println("-------------|----------------------|----------");
        
        for (double left : testValues) {
            double exact = calculateEquilibriumPoint(left);
            double approx = calculateEquilibriumPointApprox(left);
            
            System.out.printf("%.2f%%       | %.4f (%.4f%%)    | %.4f (%.4f%%)\n", 
                left * 100, exact, exact * 100, approx, approx * 100);
        }
    }

    private static double abs(double leftBound) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private static double log(double d) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
