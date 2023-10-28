package com.gluonhq.jfxapps.boot.loader.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FolderSync {

    private static final Logger logger = LoggerFactory.getLogger(FolderSync.class);

    public static void syncDirectories(Path sourceDir, Path targetDir) throws IOException {
        // Check if the source directory exists and is a directory
        if (!Files.exists(sourceDir) || !Files.isDirectory(sourceDir)) {
            throw new IllegalArgumentException("Source directory does not exist or is not a directory.");
        }

        // Check if the target directory exists and is a directory
        if (!Files.exists(targetDir) || !Files.isDirectory(targetDir)) {
            throw new IllegalArgumentException("Target directory does not exist or is not a directory.");
        }

        // Copy source directory to target directory
        Files.walkFileTree(sourceDir, new RecursiveCopyVisitor(sourceDir, targetDir));

        // Delete files and directories in target directory that do not exist in source directory
        Files.walkFileTree(targetDir, new RecursiveDeleteVisitor(sourceDir, targetDir));
    }

    private static class RecursiveCopyVisitor extends SimpleFileVisitor<Path> {
        private final Path sourceDir;
        private final Path targetDir;

        public RecursiveCopyVisitor(Path sourceDir, Path targetDir) {
            this.sourceDir = sourceDir;
            this.targetDir = targetDir;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            // Create the corresponding directory in target directory
            Path targetSubDir = targetDir.resolve(sourceDir.relativize(dir));
            Files.createDirectories(targetSubDir);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            // Copy the file to target directory if it is not up to date
            Path targetFile = targetDir.resolve(sourceDir.relativize(file));
            if (!Files.exists(targetFile) || !(Files.mismatch(file, targetFile) < 0)) {
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                logger.debug("Updated file : {}", targetFile);
            }
            return FileVisitResult.CONTINUE;
        }
    }

    private static class RecursiveDeleteVisitor extends SimpleFileVisitor<Path> {
        private final Path sourceDir;
        private final Path targetDir;

        public RecursiveDeleteVisitor(Path sourceDir, Path targetDir) {
            this.sourceDir = sourceDir;
            this.targetDir = targetDir;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            // Delete the file if it does not exist in source directory
            Path sourceFile = sourceDir.resolve(targetDir.relativize(file));
            if (!Files.exists(sourceFile)) {
                Files.delete(file);
                logger.debug("Deletd : {}", file);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            // Delete the directory if it is empty and does not exist in source directory
            Path sourceSubDir = sourceDir.resolve(targetDir.relativize(dir));
            if (!Files.exists(sourceSubDir)) {
                Files.delete(dir);
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
