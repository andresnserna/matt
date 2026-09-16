package edu.stedwards.matt.ui.screens;

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
    public OnboardingLayout() {
        setSpacing(12);
        setAlignment(Pos.CENTER);

        Label title = new Label(UIText.ONBOARDING_TITLE);
        title.getStyleClass().add("title-label");

        Button continueButton = new Button(UIText.CONTINUE_BUTTON);
        continueButton.getStyleClass().add("primary-button");

        getChildren().addAll(title, continueButton);
    }
}
