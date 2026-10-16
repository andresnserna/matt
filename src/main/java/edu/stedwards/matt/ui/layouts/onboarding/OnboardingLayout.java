/**
 * Architecture layer: view
 * JavaFX layout for the onboarding screen.
 */
package edu.stedwards.matt.ui.layouts.onboarding;

import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.labels.OnboardingScreenText;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;

/**
 * Builds the onboarding layout for first-time or introductory user flow.
 * This layout is created by the startup flow when the app needs to guide the user through
 * setup, and the resulting node is attached to a BaseWindow for display.
 * It is not responsible for database writes, application state management, or stage construction.
 */

public class OnboardingLayout extends BorderPane {
    private Node currentStep;

    public void showStep(Node step) {
        currentStep = step;
        VBox.setVgrow(currentStep, Priority.ALWAYS);
        setCenter(currentStep);
        BorderPane.setAlignment(currentStep, Pos.CENTER_LEFT);
    } 

    public OnboardingLayout(){
        getStyleClass().addAll("wizard-background", "screen-padding");
        
        TextFlow title = TextStyles.buildTitle(OnboardingScreenText.PLACEHOLDER_TITLE, "wizard-title");
        setTop(title);
        
        // setCenter(showStep());
        
        setBottom(new Buttons().buildNextButton());
    }
   
}
