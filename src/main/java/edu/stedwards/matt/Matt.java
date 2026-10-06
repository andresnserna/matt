/**
 * Architecture layer: controller
 * Bootstrap class that starts the JavaFX application and coordinates the app flow.
 */
package edu.stedwards.matt;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import edu.stedwards.matt.ui.layouts.onboarding.OnboardingLayout;
import edu.stedwards.matt.ui.layouts.onboarding.WelcomeStep;
import edu.stedwards.matt.ui.layouts.other.StartLayout;

public class Matt extends Application {

    @Override
    public void start(Stage stage) {
        OnboardingLayout onboardingLayout = new OnboardingLayout();
        onboardingLayout.showStep(new WelcomeStep());
        StartLayout startLayout = new StartLayout("application",
                () -> stage.getScene().setRoot(onboardingLayout));

        stage.setTitle("Matt");
        stage.setScene(new Scene(startLayout, StyleConstants.WINDOW_WIDTH, StyleConstants.WINDOW_HEIGHT));
        AppStyles.apply(stage.getScene());

        stage.show();
        // CSSFX.start();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}