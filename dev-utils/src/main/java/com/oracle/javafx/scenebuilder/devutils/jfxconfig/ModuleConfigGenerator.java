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
package com.oracle.javafx.scenebuilder.devutils.jfxconfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Create the java modular configuration file needed to open all javafx packages
 * @author treilhes
 *
 */
public class ModuleConfigGenerator {


    /**
     * list files in the given directory and subdirs (with recursion)
     * @param paths
     * @return
     */
    public static List<File> getFiles(String paths) {
      List<File> filesList = new ArrayList<File>();
      for (final String path : paths.split(File.pathSeparator)) {
        final File file = new File(path);
        if( file.isDirectory()) {
           recurse(filesList, file);
        }
        else {
          filesList.add(file);
        }
      }
      return filesList;
    }

    private static void recurse(List<File> filesList, File f) { 
      File list[] = f.listFiles();
      for (File file : list) {
        if (file.isDirectory()) {
           recurse(filesList, file);
        }
        else {
          filesList.add(file);
        }
      }
    }
    
    /**
     * List the content of the given jar
     * @param jarPath
     * @return
     * @throws IOException
     */
    public static Set<String> getJarPackagesWithContent(String jarPath) throws IOException{
        Set<String> content = new TreeSet<String>();
      JarFile jarFile = new JarFile(jarPath);
      Enumeration<JarEntry> e = jarFile.entries();
      
      while (e.hasMoreElements()) {
        JarEntry entry = e.nextElement();
        String name = entry.getName();
        if (!entry.isDirectory() && !name.startsWith("META-INF")) {
            name = name.replace("\\", "/");
            if (name.endsWith(".class") && name.contains("/")) {
                content.add(name.substring(0, name.lastIndexOf("/")));
            }
            
        }
        
      }
      jarFile.close();
      return content;
    }
    
    
    public static void main(String args[]) throws Exception {
      
      Pattern javafxModulePattern = Pattern.compile(".*[/\\\\](javafx-(.*?))[/\\\\].*");
      List<File> list = ModuleConfigGenerator.getFiles(System.getProperty("java.class.path"));
      
      StringBuilder builder = new StringBuilder();
      
      Set<String> javafxModules = new HashSet<>();
      for (File file: list) {
        
        Matcher m = javafxModulePattern.matcher(file.getPath());
        if (m.matches()) {
            String module = m.group(1).replace("-", ".");
            javafxModules.add(module);
            
            Set<String> cnt = getJarPackagesWithContent(file.getPath());
            cnt.stream()
                .map(s -> s.replace("/", "."))
                .forEach(s -> builder.append(
                        String.format("--add-opens %s/%s=ALL-UNNAMED\n", module, s)));
        }
      }
      builder.append("# End of generated part\n");
      
      String moduleList = javafxModules.stream().collect(Collectors.joining(","));
      builder.insert(0, String.format("--add-modules %s\n", moduleList));
      builder.insert(0, String.format("# Generated by %s %s\n", ModuleConfigGenerator.class.getName(), new Date()));
      
      System.out.println(builder.toString());
    }  

}
