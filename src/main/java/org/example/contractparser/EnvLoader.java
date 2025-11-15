package org.example.contractparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader {

    public static void main(String[] args) {
        Map<String, String> env = loadEnvFromJarDirectory(".env", true);

        // Example usage
        System.out.println("AWS_ACCESS from map: " + env.get("AWS_ACCESS_KEY_ID"));
        System.out.println("AWS_SECRET System.getProperty: " + System.getProperty("AWS_SECRET_ACCESS_KEY"));
    }

    /**
     * Load a .env file from the directory where the running JAR is located.
     *
     * @param envFileName    Name of the .env file (e.g. ".env")
     * @param setSystemProps If true, also set System properties for each key/value
     * @return Map of loaded environment variables
     */
    public static Map<String, String> loadEnvFromJarDirectory(String envFileName, boolean setSystemProps) {
        Map<String, String> envMap = new HashMap<>();

        File jarDir = getJarDirectory();
        if (jarDir == null) {
            System.err.println("Could not determine JAR directory; skipping .env load.");
            return envMap;
        }

        File envFile = new File(jarDir, envFileName);
        if (!envFile.exists()) {
            System.err.println(".env file not found at: " + envFile.getAbsolutePath());
            return envMap;
        }

        try (BufferedReader reader = new BufferedReader(
                new FileReader(envFile, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int eqIndex = line.indexOf('=');
                if (eqIndex <= 0) {
                    // no '=' or key is empty -> skip
                    continue;
                }

                String key = line.substring(0, eqIndex).trim();
                String value = line.substring(eqIndex + 1).trim();

                // Optional: remove surrounding quotes
                value = stripQuotes(value);

                envMap.put(key, value);

                if (setSystemProps && !key.isEmpty()) {
                    System.setProperty(key, value);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading .env file: " + e.getMessage());
        }

        return envMap;
    }


    private static File getJarDirectory() {
        try {
            // This assumes this class is loaded from the same JAR as your main app
            // You can also use Main.class instead of EnvLoader.class
            File jarFile = new File(
                    EnvLoader.class
                            .getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
            );

            // If running from IDE (not jar), jarFile may be a directory (build/classes...)
            File dir = jarFile.isDirectory() ? jarFile : jarFile.getParentFile();
            return dir;

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static String stripQuotes(String s) {
        if (s.length() >= 2) {
            char first = s.charAt(0);
            char last = s.charAt(s.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return s.substring(1, s.length() - 1);
            }
        }
        return s;
    }
}
