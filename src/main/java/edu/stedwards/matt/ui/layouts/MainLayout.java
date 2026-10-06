/**
 * Architecture layer: view
 * JavaFX layout for the home/dashboard screen.
 */
package edu.stedwards.matt.ui.layouts;

import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import edu.stedwards.matt.ui.interaction.VisualElements;


/**
 * Builds the main home layout after a user has entered the app.
 * This layout is created by the app navigation layer or main controller when the user
 * reaches the home/dashboard view, and it is attached to a BaseWindow for display.
 * It is not responsible for fetching data directly from the database or handling window creation.
 */

public class MainLayout extends BorderPane {
    private Node currentStep;

    public void showStep(Node step) {
        getChildren().clear();
        currentStep = step;
        getChildren().add(currentStep);
    }

    public BorderPane buildMainHomeLayout(){
        BorderPane mainHomeLayout = new BorderPane();
        //building the home page for the app, this encompasses many elements, but can be 
        // swapped with different layouts so that main owns the sidebar, and the content 
        // to the right of it changes based on the menu item

        return mainHomeLayout;
    }

    public MainLayout(){
        getStyleClass().add("");
        setLeft(new VisualElements().mainSideBar());
        setCenter(new VisualElements().collapsableDrawer("Title", null));
    }
}
