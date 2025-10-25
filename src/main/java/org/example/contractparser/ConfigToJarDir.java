package org.example.contractparser;// java
import java.io.*;
import java.net.*;

public class ConfigToJarDir {
    public static File getJarDir() throws URISyntaxException {
        URL url = ConfigToJarDir.class.getProtectionDomain().getCodeSource().getLocation();
        File jarOrDir = new File(url.toURI());
        return jarOrDir.isFile() ? jarOrDir.getParentFile() : jarOrDir;
    }

    public static void main(String[] args) throws Exception {
        File jarDir = getJarDir();
        File config = new File(jarDir, "config.yml");
        try (PrintWriter pw = new PrintWriter(new FileWriter(config))) {
            pw.println("salary: 4800");
        }
        System.out.println("Wrote config to: " + config.getAbsolutePath());
        System.out.println("Current working dir (user.dir): " + System.getProperty("user.dir"));
    }
}
