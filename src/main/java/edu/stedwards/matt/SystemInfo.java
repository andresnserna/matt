/**
 * Architecture layer: service
 * Runtime utility service that exposes Java and JavaFX environment details.
 */
package edu.stedwards.matt;

/**
 * TODO: (LONG description of this class)
 */

public class SystemInfo {

    public static String javaVersion() {
        return System.getProperty("java.version");
    }

    public static String javafxVersion() {
        return System.getProperty("javafx.version");
    }

    public static String systemOSVersion() {
        return System.getProperty("os.version");
    }

    public static String systemOSPlatform() {
        return System.getProperty("os.name");
    }

    public static String projectVersion() {
        String version = SystemInfo.class.getPackage().getImplementationVersion();
        return version == null ? "Development" : version;
    }

}