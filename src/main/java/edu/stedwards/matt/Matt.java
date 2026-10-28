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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.collections.FXCollections;
import edu.stedwards.matt.models.entity.AwardsCerts;
import edu.stedwards.matt.models.entity.Education;
import edu.stedwards.matt.models.entity.Experience;
import edu.stedwards.matt.models.entity.Projects;
import edu.stedwards.matt.models.entity.Skill;
import edu.stedwards.matt.models.entity.Websites;
import edu.stedwards.matt.ui.layouts.application.ApplicationConfirmFields;
import edu.stedwards.matt.ui.layouts.application.ApplicationGenerateMaterials;
import edu.stedwards.matt.ui.layouts.application.ApplicationHome;
import edu.stedwards.matt.ui.layouts.application.ApplicationUploadJob;
import edu.stedwards.matt.ui.layouts.onboarding.OnboardingEnterInfo;
import edu.stedwards.matt.ui.layouts.onboarding.OnboardingOverviewStep;
import edu.stedwards.matt.ui.layouts.onboarding.OnboardingStepLayout;
import edu.stedwards.matt.ui.layouts.onboarding.OnboardingUploadStep;
import edu.stedwards.matt.ui.layouts.onboarding.OnboardingWelcome;
import edu.stedwards.matt.ui.layouts.other.LoginLayout;
import edu.stedwards.matt.ui.layouts.other.MainLayout;
import edu.stedwards.matt.ui.layouts.other.StartLayout;
import edu.stedwards.matt.ui.layouts.myinfo.MyInfoEditingStep;
import edu.stedwards.matt.ui.layouts.myinfo.MyInfoHomeStep;
import edu.stedwards.matt.ui.layouts.other.SettingsLayout;
import edu.stedwards.matt.ui.layouts.template.TemplatesHome;
import edu.stedwards.matt.ui.layouts.template.TemplatesEditor;
import edu.stedwards.matt.ui.layouts.template.TemplatesUploadOrNew;
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
        OnboardingStepLayout onboardingLayout = new OnboardingStepLayout();
        onboardingLayout.showStep(new OnboardingWelcome());
        StartLayout startLayout = new StartLayout("application",
                () -> stage.getScene().setRoot(onboardingLayout));

        stage.setTitle("Matt");
        stage.setScene(new Scene(startLayout, StyleConstants.WINDOW_WIDTH, StyleConstants.WINDOW_HEIGHT));
        AppStyle.apply(stage.getScene());

        stage.show();
        CSSFX.start();
    }

    private void startLayoutPreview(Stage stage) {
        List<PreviewScreen> screens = List.of(
                new PreviewScreen("Start", () -> new StartLayout("application")),
                new PreviewScreen("Login", LoginLayout::new),
                new PreviewScreen("Onboarding - Welcome", OnboardingWelcome::new),
                new PreviewScreen("Onboarding - Resume Upload", OnboardingUploadStep::new),
                new PreviewScreen("Onboarding - Personal Info", OnboardingEnterInfo::new),
                new PreviewScreen("Onboarding - Education", () -> onboardingStep(
                        layout -> layout.showEducationStep(FXCollections.observableArrayList(new Education())))),
                new PreviewScreen("Onboarding - Experience", () -> onboardingStep(
                        layout -> layout.showExperienceStep(FXCollections.observableArrayList(new Experience())))),
                new PreviewScreen("Onboarding - Projects", () -> onboardingStep(
                        layout -> layout.showProjectsStep(FXCollections.observableArrayList(new Projects())))),
                new PreviewScreen("Onboarding - Skills", () -> onboardingStep(
                        layout -> layout.showSkillsStep(FXCollections.observableArrayList(new Skill())))),
                new PreviewScreen("Onboarding - Awards/Certs", () -> onboardingStep(
                        layout -> layout.showAwardsStep(FXCollections.observableArrayList(new AwardsCerts())))),
                new PreviewScreen("Onboarding - Websites", () -> onboardingStep(
                        layout -> layout.showWebsitesStep(FXCollections.observableArrayList(new Websites())))),
                new PreviewScreen("Onboarding - Final Overview", OnboardingOverviewStep::new),
                new PreviewScreen("Main", MainLayout::new),
                new PreviewScreen("Main - Home", () -> mainAreaScreen("Home")),
                new PreviewScreen("Main - Templates", () -> mainAreaScreen("Templates")),
                new PreviewScreen("Main - Templates - Home", () -> mainShell(() -> {
                    new TemplatesHome();
                    return placeholderScreen("Templates Home");
                })),
                new PreviewScreen("Main - Templates - Upload or New",
                        () -> mainShell(TemplatesUploadOrNew::new)),
                new PreviewScreen("Main - Templates - Template Creator",
                        () -> mainShell(TemplatesEditor::new)),
                new PreviewScreen("Main - Applications", () -> mainAreaScreen("Applications")),
                new PreviewScreen("Main - Applications - Home", () -> mainShell(ApplicationHome::new)),
                new PreviewScreen("Main - Applications - Upload", () -> mainShell(ApplicationUploadJob::new)),
                new PreviewScreen("Main - Applications - Confirm Fields",
                        () -> mainShell(ApplicationConfirmFields::new)),
                new PreviewScreen("Main - Applications - Final Overview / Application Page",
                        () -> mainShell(ApplicationGenerateMaterials::new)),
                new PreviewScreen("Main - My Info", () -> mainAreaScreen("My Info")),
                new PreviewScreen("Main - My Info - Home", () -> mainShell(() -> {
                    new MyInfoHomeStep();
                    return placeholderScreen("My Info Home");
                })),
                new PreviewScreen("Main - My Info - Edit Fields", () -> mainShell(() -> {
                    new MyInfoEditingStep();
                    return placeholderScreen("Edit My Info");
                })),
                new PreviewScreen("Main - Settings", () -> mainShell(() -> {
                    new SettingsLayout();
                    return placeholderScreen("Settings");
                })));

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

        HBox navigation = new HBox(8, previous, next, screenCount);
        navigation.setAlignment(Pos.CENTER_LEFT);
        navigation.setPadding(new Insets(8));
        navigation.getStyleClass().add("layout-preview-controls");
        preview.setBottom(navigation);
        showCurrentScreen.run();

        stage.setTitle("Matt - Layout Preview");
        stage.setScene(new Scene(preview, StyleConstants.WINDOW_WIDTH, StyleConstants.WINDOW_HEIGHT));
        AppStyle.apply(stage.getScene());
        stage.show();
        CSSFX.start();
    }

    private Node onboardingStep(Consumer<OnboardingStepLayout> setup) {
        OnboardingStepLayout layout = new OnboardingStepLayout();
        setup.accept(layout);
        return layout;
    }

    private Node mainAreaScreen(String section) {
        return mainShell(() -> placeholderScreen("Main - " + section));
    }

    private Node mainShell(Supplier<Node> contentFactory) {
        MainLayout layout = new MainLayout();
        layout.showStep(contentFactory.get());
        return layout;
    }

    private Node placeholderScreen(String title) {
        Label label = new Label(title + " preview");
        label.getStyleClass().add("screen-title");
        StackPane placeholder = new StackPane(label);
        placeholder.getStyleClass().add("screen-padding");
        return placeholder;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
