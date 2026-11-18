/**
 * Architecture layer: view
 * JavaFX layout for the home/dashboard screen.
 */
package edu.stedwards.matt.ui.layouts.other;

import edu.stedwards.matt.services.OllamaService;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.layouts.application.ApplicationHome;
import edu.stedwards.matt.ui.layouts.myinfo.MyInfoLayout;
import edu.stedwards.matt.ui.layouts.template.TemplatesHome;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import edu.stedwards.matt.ui.layouts.myinfo.MyInfoLayout;


/**
 * Builds the main home layout after a user has entered the app.
 * This layout is created by the app navigation layer or main controller when the user
 * reaches the home/dashboard view, and it is attached to a BaseWindow for display.
 * It is not responsible for fetching data directly from the database or handling window creation.
 */

public class MainLayout extends BorderPane {
    private final VisualElements visualElements = new VisualElements();
    // private final OllamaService ollamaService = new OllamaService();
    private final BorderPane contentWrapper = new BorderPane();

    private Node currentStep;
    // private Circle ollamaStatusDot;
    // private Label ollamaStatusLabel;
    // private Button retryOllamaButton;

    public void showStep(Node step) {
        currentStep = step;
        contentWrapper.setCenter(currentStep);
        // refreshOllamaStatusAsync(false);
    }

    /**
     * Building the home page for the app, this encompasses many elements, but can
     * be swapped with different layouts so that main owns the sidebar, and the
     * content to the right of it changes based on the menu item.
     */
    public BorderPane buildMainHomeLayout() {
        return new BorderPane();
    }

    public MainLayout() {
        setLeft(visualElements.mainSideBar(this));

        // contentWrapper.setTop(buildOllamaStatusBar());
        contentWrapper.setCenter(buildMainHomeLayout());
        setCenter(contentWrapper);

        // Check Ollama status on a background thread when the dashboard loads
        // refreshOllamaStatusAsync(false);
    }

    // /**
    //  * Days 1-2 View Task: Builds the live Ollama status indicator bar.
    //  */
    // private HBox buildOllamaStatusBar() {
    //     ollamaStatusDot = new Circle(5, Color.GRAY);
    //     ollamaStatusLabel = new Label("Ollama: Checking...");
    //     ollamaStatusLabel.getStyleClass().add("input-label");

    //     retryOllamaButton = new Button("Start / Retry");
    //     retryOllamaButton.setVisible(false);
    //     retryOllamaButton.setManaged(false);
    //     retryOllamaButton.setOnAction(event -> refreshOllamaStatusAsync(true));

    //     Region spacer = new Region();
    //     HBox.setHgrow(spacer, Priority.ALWAYS);

    //     HBox statusBar = new HBox(8, spacer, ollamaStatusDot, ollamaStatusLabel, retryOllamaButton);
    //     statusBar.setAlignment(Pos.CENTER_RIGHT);
    //     statusBar.setPadding(new Insets(10, 20, 4, 20));
    //     return statusBar;
    // }

    // /**
    //  * Checks or starts Ollama on a background daemon thread and updates the status indicator on the JavaFX thread.
    //  */
    // public void refreshOllamaStatusAsync(boolean attemptStart) {
    //     if (ollamaStatusLabel != null) {
    //         Platform.runLater(() -> {
    //             ollamaStatusDot.setFill(Color.ORANGE);
    //             ollamaStatusLabel.setText(attemptStart ? "Ollama: Starting..." : "Ollama: Checking...");
    //             retryOllamaButton.setDisable(true);
    //         });
    //     }

    //     Thread checkThread = new Thread(() -> {
    //         boolean running = attemptStart ? ollamaService.ensureRunning() : ollamaService.isRunning();
    //         Platform.runLater(() -> updateOllamaIndicator(running));
    //     }, "ollama-status-check-thread");
    //     checkThread.setDaemon(true);
    //     checkThread.start();
    // }

    // private void updateOllamaIndicator(boolean running) {
    //     if (running) {
    //         ollamaStatusDot.setFill(Color.web("#2ecc71")); // Green
    //         ollamaStatusLabel.setText("Ollama: Connected");
    //         retryOllamaButton.setVisible(false);
    //         retryOllamaButton.setManaged(false);
    //     } else {
    //         ollamaStatusDot.setFill(Color.web("#e74c3c")); // Red
    //         ollamaStatusLabel.setText("Ollama: Offline");
    //         retryOllamaButton.setDisable(false);
    //         retryOllamaButton.setVisible(true);
    //         retryOllamaButton.setManaged(true);
    //     }
    // }

    /** Navigate to the application home screen. */
    public void handleHomeNavigation() {
        showStep(new ApplicationHome());
    }

    /** Navigate to Main - Templates - Home. */
    public void handleTemplatesNavigation() {
        showStep(new TemplatesHome());
    }

    /** Navigate to Main - Applications - Home. */
    public void handleApplicationsNavigation() {
        showStep(new ApplicationHome());
    }

    /** Navigate to the read-only My Info screen. */
    public void handleMyInfoNavigation() {
        showStep(new MyInfoLayout());
    }

    /** Navigate to the Settings screen. */
    public void handleSettingsNavigation() {
        showStep(new SettingsLayout());
    }
}
