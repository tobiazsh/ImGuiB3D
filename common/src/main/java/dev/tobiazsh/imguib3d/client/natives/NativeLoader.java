package dev.tobiazsh.imguib3d.client.natives;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class NativeLoader {

    private static final String nativeDirectory = "/natives"; // Inside resources
    private static final TargetSystem targetSystem;

    static {
        targetSystem = TargetSystem.detect();
    }

    public static Set<String> loaded = new HashSet<>();

    public static void load(String libName) {
        if (loaded.contains(libName))
            return; // Do not load twice

        String folderName = targetSystem.getConstruct();
        String libFileName = targetSystem.os.getLibFileName(libName);
        Path filePath = Path.of(nativeDirectory, folderName, libFileName);

        try (InputStream is = NativeLoader.class.getResourceAsStream(filePath.toString())) {
            if (is == null)
                throw new IOException("Failed to load native library: resource not found: " + filePath);

            File tempFile = File.createTempFile(libName, targetSystem.os.suffix);

            try (OutputStream os = Files.newOutputStream(tempFile.toPath())) {
                is.transferTo(os);
            }

            System.load(tempFile.getAbsolutePath());

            if (!tempFile.delete()) {
                tempFile.deleteOnExit(); // Fallback in case deletion does not work
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load native library: " + libName, e);
        }
        
        loaded.add(libName);
    }

    private enum OS {
        WINDOWS("", ".dll", "windows"),
        LINUX("lib", ".so", "linux"),
        MACOS("lib", ".dylib", "macos"),;

        private final String prefix;
        private final String suffix;
        private final String name;

        OS(String prefix, String suffix, String name) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.name = name;
        }

        public String getLibFileName(String libName) {
            return prefix + libName + suffix;
        }

        public String getName() {
            return name;
        }

        public static OS fromString(String str) {
            if (str.contains("darwin") || str.contains("macos"))
                return MACOS;

            if (str.contains("win"))
                return WINDOWS;

            if (str.contains("linux"))
                return LINUX;

            throw new IllegalStateException("Unexpected value or unsupported OS: " + str.toLowerCase());
        }
    }

    private enum ARCHITECTURE {
        AARCH64("aarch64"),
        X86_64("x86_64"),;

        private final String name;

        ARCHITECTURE(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static ARCHITECTURE fromString(String str) {
            return switch (str.toLowerCase()) {
                case "aarch64", "arm64" -> AARCH64;
                case "x86_64", "amd64", "x86-64" -> X86_64;
                default -> throw new IllegalStateException("Unexpected value or unsupported architecture: " + str.toLowerCase());
            };
        }
    }

    private record TargetSystem(OS os, ARCHITECTURE architecture) {
        public static TargetSystem detect() {
            String os = System.getProperty("os.name").toLowerCase();
            String arch = System.getProperty("os.arch").toLowerCase();

            return new TargetSystem(
                    OS.fromString(os),
                    ARCHITECTURE.fromString(arch)
            );
        }

        /**
         * Returns the string construct of the system's architecture and OS.
         * <p>For example, Linux on x86_64 returns "linux-x86_64"
         */
        private String getConstruct() {
            return os.getName() + "-" + architecture.getName();
        }
    }
}
