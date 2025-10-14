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
package opensesim.util;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Files {
    
    /**
     * Converts a list of file objects to a list of URL objects
     * @param files list of file objects
     * @return list of url objects
     */
    static public ArrayList<URL> buildUrlList( ArrayList<File> files){
        ArrayList<URL> urllist;
        urllist = new ArrayList<>();
        URL url;
        for (File file : files) {
            try {
                url = file.toURI().toURL();
            } catch (MalformedURLException ex) {
                Logger.getLogger(XClassLoader.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            urllist.add(url);
        }
        return urllist;
    }

    /**
     * Get a list of all files in a given directory and its sub-directories.
     * The files are filtered by a filter.
     * 
     * @param path Directory to list
     * @param filter a Filter to filter files
     * @return List of files
     */
    public static ArrayList<File> listFiles(String path, FilenameFilter filter) {
        ArrayList<File> files = new ArrayList<>();
        File fp = new File(path);
        if (!fp.isDirectory()) {
            files.add(fp);
            return files;
        }
        File[] fList = new File(path).listFiles(filter);
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                files.addAll(listFiles(file.getAbsolutePath(), filter));
            }
        }
        return files;
    }

    /**
     * Get a list of all files in a given directory and its sub-directories.
     * @param path Path to list files in
     * @return list of files
     */
    public static ArrayList<File> listFiles(String path) {
        FilenameFilter f = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        };
        return Files.listFiles(path, f);
    }

    /**
     * Get a list of all files in a given directory and its sub-directories, 
     * filtered by extension
     
     * @param path Path to list files in
     * @param ext extension to filter
     * @return list of files
     */
    public static ArrayList<File> listFiles(String path, String ext) {
        FilenameFilter f;
        f = (File dir, String name) -> name.toLowerCase().endsWith(ext) || dir.isDirectory();
        return Files.listFiles(path, f);
    }
}
