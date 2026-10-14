/**
 * Architecture layer: controller
 * Bootstrap class that starts the JavaFX application and coordinates the app flow.
 */
package edu.stedwards.matt;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.List;
import java.util.function.Supplier;
import edu.stedwards.matt.ui.layouts.application.ApplicationHomeStep;
import edu.stedwards.matt.ui.layouts.onboarding.FinalOverviewStep;
import edu.stedwards.matt.ui.layouts.onboarding.OnboardingLayout;
import edu.stedwards.matt.ui.layouts.onboarding.ResumeUploadStep;
import edu.stedwards.matt.ui.layouts.onboarding.WelcomeStep;
import edu.stedwards.matt.ui.layouts.other.LoginLayout;
import edu.stedwards.matt.ui.layouts.other.MainLayout;
import edu.stedwards.matt.ui.layouts.other.StartLayout;
import fr.brouillard.oss.cssfx.CSSFX;

public class Matt extends Application {
    private static final boolean PREVIEW_LAYOUTS = true;

    private record PreviewScreen(String name, Supplier<Node> factory) {
    }

    @Override
    public void start(Stage stage) {
        if (PREVIEW_LAYOUTS) {
            startLayoutPreview(stage);
        } else {
            startNormalApplication(stage);
        }
    }

    private void startNormalApplication(Stage stage) {
        OnboardingLayout onboardingLayout = new OnboardingLayout();
        onboardingLayout.showStep(new WelcomeStep());
        StartLayout startLayout = new StartLayout("application",
                () -> stage.getScene().setRoot(onboardingLayout));

        stage.setTitle("Matt");
        stage.setScene(new Scene(startLayout, StyleConstants.WINDOW_WIDTH, StyleConstants.WINDOW_HEIGHT));
        AppStyles.apply(stage.getScene());

        stage.show();
        CSSFX.start();
    }

    private void startLayoutPreview(Stage stage) {
        List<PreviewScreen> screens = List.of(
                new PreviewScreen("Start", () -> new StartLayout("application")),
                new PreviewScreen("Welcome", WelcomeStep::new),
                new PreviewScreen("Resume Upload", ResumeUploadStep::new),
                new PreviewScreen("Final Overview", FinalOverviewStep::new),
                new PreviewScreen("Login", LoginLayout::new),
                new PreviewScreen("Main", MainLayout::new),
                new PreviewScreen("Application Home", ApplicationHomeStep::new));

        int[] currentIndex = { 0 };
        BorderPane preview = new BorderPane();
        Label screenCount = new Label();
        Button previous = new Button("←");
        Button next = new Button("→");
        previous.getStyleClass().add("layout-preview-arrow");
        next.getStyleClass().add("layout-preview-arrow");
        screenCount.getStyleClass().add("layout-preview-count");

        Runnable showCurrentScreen = () -> {
            PreviewScreen screen = screens.get(currentIndex[0]);
            preview.setCenter(screen.factory().get());
            screenCount.setText((currentIndex[0] + 1) + " / " + screens.size() + "  " + screen.name());
        };
        previous.setOnAction(event -> {
            currentIndex[0] = (currentIndex[0] - 1 + screens.size()) % screens.size();
            showCurrentScreen.run();
        });
        next.setOnAction(event -> {
            currentIndex[0] = (currentIndex[0] + 1) % screens.size();
            showCurrentScreen.run();
        });

        HBox navigation = new HBox(8, previous, screenCount, next);
        navigation.setAlignment(Pos.CENTER_LEFT);
        navigation.setPadding(new Insets(8));
        navigation.getStyleClass().add("layout-preview-controls");
        preview.setBottom(navigation);
        showCurrentScreen.run();

        stage.setTitle("Matt - Layout Preview");
        stage.setScene(new Scene(preview, StyleConstants.WINDOW_WIDTH, StyleConstants.WINDOW_HEIGHT));
        AppStyles.apply(stage.getScene());
        stage.show();
        CSSFX.start();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}