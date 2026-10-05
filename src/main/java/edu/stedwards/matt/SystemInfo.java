/**
 * Architecture layer: service
 * Runtime utility service that exposes Java and JavaFX environment details.
 */
package edu.stedwards.matt;

public class SystemInfo {

    public static String javaVersion() {
        return System.getProperty("java.version");
    }

    public static String javafxVersion() {
        return System.getProperty("javafx.version");
    }

}