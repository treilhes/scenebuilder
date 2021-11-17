package com.oracle.javafx.scenebuilder.metadata.finder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Jar {
    
    /**
     * List the content of the given jar
     * @param jarPath
     * @return
     * @throws IOException
     */
    public static Set<String> listClasses(Path path) throws IOException{
        Set<String> content = new TreeSet<String>();
      JarFile jarFile = new JarFile(path.toFile());
      Enumeration<JarEntry> e = jarFile.entries();
      
      while (e.hasMoreElements()) {
        JarEntry entry = e.nextElement();
        String name = entry.getName();
        if (!entry.isDirectory() && !name.startsWith("META-INF")) {
            name = name.replace("\\", "/");
            if (name.endsWith(".class") && name.contains("/")) {
                String className = name.substring(0, name.lastIndexOf(".class"))
                        .replace("/", ".")
                        .replace("\\", ".");
                if (!className.contains("$")) {
                    content.add(className);
                }
                
            }
            
        }
        
      }
      jarFile.close();
      return content;
    }
}
