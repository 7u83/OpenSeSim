/*
 * Copyright (c) 2018, 7u83 <7u83@mail.ru>
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
package opensesim.gui.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;
import org.json.JSONObject;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Json {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    public static @interface Export {

        public String value() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    public static @interface Import {

        public String value() default "";
    }

    /**
     * Get fields from JSON Object
     *
     * @param o Object to get fields from
     * @return the created JSONObject
     */
    public static JSONObject get(Object o) {
        Field[] fields = o.getClass().getFields();
        JSONObject jo = new JSONObject();

        for (Field f : fields) {

            Export exp = f.getAnnotation(Export.class);
            if (exp == null) {
                continue;
            }

            Class cls = f.getType();
            if (JTextField.class.isAssignableFrom(cls)) {
                try {
                    JTextField tf = (JTextField) f.get(o);
                    String name = null == exp.value() ? f.getName() : exp.value();
                    jo.put(name, tf.getText());
                } catch (IllegalArgumentException | IllegalAccessException ex1) {
                    Logger.getLogger(Json.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }

        }
        return jo;
    }

    /**
     * Inverse to get
     *
     * @param o Object
     * @param jo JSONObject
     */
    public static void put(Object o, JSONObject jo) {
        Field[] fields = o.getClass().getFields();
        for (Field f : fields) {
            Import imp = f.getAnnotation(Import.class);
            if (imp == null) {
                continue;
            }

            Class cls = f.getType();
            if (JTextField.class.isAssignableFrom(cls)) {
                try {
                    JTextField tf = (JTextField) f.get(o);
                    String name = null == imp.value() ? f.getName() : imp.value();
                    tf.setText(jo.optString(name));
                } catch (IllegalArgumentException | IllegalAccessException ex1) {
                    Logger.getLogger(Json.class.getName()).log(Level.SEVERE, null, ex1);
                }
                continue;
            }
        }

        Method[] methods = o.getClass().getMethods();
        for (Method m : methods) {
            Import imp = m.getAnnotation(Import.class);
            if (imp == null) {
                continue;
            }

            if (m.getParameterCount() != 1) {
                Logger.getLogger(Json.class.getName()).log(Level.SEVERE, null, "Wrong pcouunt");
                continue;
            }

            String name = null == imp.value() ? m.getName() : imp.value();
            
            Class p0 = m.getParameterTypes()[0];
            if (String.class.isAssignableFrom(p0)){
                String param = jo.optString(name, "");
                try {
                    m.invoke(o, param);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(Json.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
    }

}
