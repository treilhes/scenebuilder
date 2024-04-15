package com.gluonhq.jfxapps.metadata.finder;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.metadata.model.Descriptor;

public class JarFinder {

    private static Logger logger = LoggerFactory.getLogger(JarFinder.class);

    public static Set<Path> listJarsInClasspath(List<Pattern> inclusionPatterns, List<Descriptor> descriptorsHolder) {
        List<File> list = getFiles(System.getProperty("java.class.path"));
        return listJarsInClasspath(list, inclusionPatterns, descriptorsHolder);
    }

    public static Set<Path> listJarsInClasspath(List<File> classpath, List<Pattern> inclusionPatterns, List<Descriptor> descriptorsHolder) {

        Set<Path> jarPathes = new HashSet<>();
        for (File file: classpath) {
            if (Descriptor.hasDescriptor(file)) {
                Descriptor descriptor = Descriptor.load(file);
                if (descriptor != null) {
                    logger.info("Descriptor found in jar file : {}", file.getAbsolutePath());
                    descriptorsHolder.add(descriptor);
                }
            }
            logger.debug("Checking jar file : {}", file.getAbsolutePath());
            if (inclusionPatterns == null || inclusionPatterns.size() == 0) {
                jarPathes.add(file.toPath());
                logger.info("No filter, so Jar file selected : {}", file.getAbsolutePath());
            } else {
                for (Pattern p:inclusionPatterns) {
                    Matcher m = p.matcher(file.getPath());
                    if (m.matches()) {
                        jarPathes.add(file.toPath());
                        logger.info("Jar file selected : {}", file.getAbsolutePath());
                        break;
                    }
                }
            }
        }
        return jarPathes;
    }

    /**
     * list files in the given directory and subdirs (with recursion)
     * @param paths
     * @return
     */
    private static List<File> getFiles(String paths) {
      List<File> filesList = new ArrayList<File>();
      for (final String path : paths.split(File.pathSeparator)) {
        final File file = new File(path);
        if( file.isDirectory()) {
           recurse(filesList, file);
        }
        else {
            if (file.getName().toLowerCase().endsWith(".jar")) {
                filesList.add(file);
            }
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
            if (file.getName().toLowerCase().endsWith(".jar")) {
                filesList.add(file);
            }
        }
      }
    }
}
