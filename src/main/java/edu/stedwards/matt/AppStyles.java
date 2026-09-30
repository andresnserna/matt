/**
 * Architecture layer: view
 * Applies shared styling resources to JavaFX scenes.
 */
package edu.stedwards.matt;

import java.util.List;
import java.util.Locale;
import java.net.URL;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.Scene;

/**
 * TODO: (LONG description of this class)
 */

public final class AppStyles {
    public static final String APP_CSS = "/styles/app.css";
    private static final List<String> SF_PRO_FAMILIES = List.of(
            "SF Pro", "SF Pro Text", "SF Pro Display");

    private AppStyles() {
    }

    public static void apply(Scene scene) {
        URL css = AppStyles.class.getResource(APP_CSS);
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        applyPlatformFont(scene.getRoot());
        scene.rootProperty().addListener((observable, oldRoot, newRoot) -> applyPlatformFont(newRoot));
    }

    private static void applyPlatformFont(Node root) {
        root.getStyleClass().removeAll("app-font-sf-pro", "app-font-sf-pro-text", "app-font-sf-pro-display");

        String osName = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (!osName.startsWith("mac")) {
            return;
        }

        for (String family : SF_PRO_FAMILIES) {
            if (!Font.getFamilies().contains(family)) {
                continue;
            }

            String styleClass = switch (family) {
                case "SF Pro Text" -> "app-font-sf-pro-text";
                case "SF Pro Display" -> "app-font-sf-pro-display";
                default -> "app-font-sf-pro";
            };
            root.getStyleClass().add(styleClass);
            return;
        }
    }
}
