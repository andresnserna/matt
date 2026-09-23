/**
 * Architecture layer: view
 * Applies shared styling resources to JavaFX scenes.
 */
package edu.stedwards.matt;

import java.net.URL;

import javafx.scene.Scene;

public final class AppStyles {
    public static final String APP_CSS = "/styles/app.css";

    private AppStyles() {
    }

    public static void apply(Scene scene) {
        URL css = AppStyles.class.getResource(APP_CSS);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
    }
}
