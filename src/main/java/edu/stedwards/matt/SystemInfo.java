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

    // TODO: find the key for this property
    public static String systemOSVersion() {
        return System.getProperty("op");
    }

    // TODO: find the key for this property
    public static String systemOSPlatform() {
        return System.getProperty("op");
    }

}