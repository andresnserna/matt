package edu.stedwards.matt.ui.screens;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import edu.stedwards.matt.ui.labels.UIText;

/**
 * Builds the initial landing layout for the app.
 * This layout is created by the application bootstrap code or a navigation controller,
 * then passed into a BaseWindow as the root content for the starting view.
 * It is not responsible for database access or window lifecycle management.
 */
public class StartLayout extends VBox {
    public StartLayout() {
        setSpacing(16);
        setAlignment(Pos.CENTER);

        Label title = new Label(UIText.START_TITLE);
        title.getStyleClass().add("title-label");

        Label subtitle = new Label(UIText.START_SUBTITLE);
        subtitle.getStyleClass().add("subtitle-label");

        Button openWindow = new Button(UIText.OPEN_WINDOW_BUTTON);
        openWindow.getStyleClass().add("primary-button");

        getChildren().addAll(title, subtitle, openWindow);
    }
}
