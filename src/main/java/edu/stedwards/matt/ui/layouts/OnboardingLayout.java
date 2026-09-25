/**
 * Architecture layer: view
 * JavaFX layout for the onboarding screen.
 */
package edu.stedwards.matt.ui.layouts;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import edu.stedwards.matt.ui.labels.UIText;

/**
 * Builds the onboarding layout for first-time or introductory user flow.
 * This layout is created by the startup flow when the app needs to guide the user through
 * setup, and the resulting node is attached to a BaseWindow for display.
 * It is not responsible for database writes, application state management, or stage construction.
 */

public class OnboardingLayout extends VBox {
    private Node currentStep;

    public void showStep(Node step) {
        getChildren().clear();
        currentStep = step;
        getChildren().add(currentStep);
    }

}
