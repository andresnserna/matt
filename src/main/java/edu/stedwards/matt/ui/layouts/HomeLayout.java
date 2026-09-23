/**
 * Architecture layer: view
 * JavaFX layout for the home/dashboard screen.
 */
package edu.stedwards.matt.ui.layouts;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import edu.stedwards.matt.ui.labels.UIText;

/**
 * Builds the main home layout after a user has entered the app.
 * This layout is created by the app navigation layer or main controller when the user
 * reaches the home/dashboard view, and it is attached to a BaseWindow for display.
 * It is not responsible for fetching data directly from the database or handling window creation.
 */
public class HomeLayout extends VBox {
    public HomeLayout() {
        setSpacing(12);
        setAlignment(Pos.CENTER);

        Label title = new Label(UIText.HOME_TITLE);
        title.getStyleClass().add("title-label");

        getChildren().add(title);
    }
}
