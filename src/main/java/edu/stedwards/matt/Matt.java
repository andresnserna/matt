/**
 * Architecture layer: controller
 * Bootstrap class that starts the JavaFX application and coordinates the app flow.
 */
package edu.stedwards.matt;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import edu.stedwards.matt.ui.layouts.OnboardingLayout;
import edu.stedwards.matt.ui.layouts.StartLayout;
import edu.stedwards.matt.ui.layouts.onboarding.WelcomeStep;

public class Matt extends Application {

    @Override
    public void start(Stage stage) {
        OnboardingLayout onboardingLayout = new OnboardingLayout();
        onboardingLayout.showStep(new WelcomeStep());
        StartLayout startLayout = new StartLayout("application",
                () -> stage.getScene().setRoot(onboardingLayout));

        stage.setTitle("Matt");
        stage.setScene(new Scene(startLayout, 640, 480));
        AppStyles.apply(stage.getScene());

        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}