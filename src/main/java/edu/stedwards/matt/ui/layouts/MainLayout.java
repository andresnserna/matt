/**
 * Architecture layer: view
 * JavaFX layout for the home/dashboard screen.
 */
package edu.stedwards.matt.ui.layouts;

import javafx.scene.layout.VBox;
import javafx.scene.Node;


/**
 * Builds the main home layout after a user has entered the app.
 * This layout is created by the app navigation layer or main controller when the user
 * reaches the home/dashboard view, and it is attached to a BaseWindow for display.
 * It is not responsible for fetching data directly from the database or handling window creation.
 */

public class MainLayout extends VBox {
    private Node currentStep;

    public void showStep(Node step) {
        getChildren().clear();
        currentStep = step;
        getChildren().add(currentStep);
    }
}
