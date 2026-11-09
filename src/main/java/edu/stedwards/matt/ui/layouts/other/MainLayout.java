/**
 * Architecture layer: view
 * JavaFX layout for the home/dashboard screen.
 */
package edu.stedwards.matt.ui.layouts.other;

// import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import edu.stedwards.matt.ui.interaction.VisualElements;


/**
 * Builds the main home layout after a user has entered the app.
 * This layout is created by the app navigation layer or main controller when the user
 * reaches the home/dashboard view, and it is attached to a BaseWindow for display.
 * It is not responsible for fetching data directly from the database or handling window creation.
 */

public class MainLayout extends BorderPane {
    private final VisualElements visualElements = new VisualElements();
    private Node currentStep;

    public void showStep(Node step) {
        currentStep = step;
        setCenter(currentStep);
    }
    
    /**
     * building the home page for the app, this encompasses many elements, but can
     * be swapped with different layouts so that main owns the sidebar, and the
     * content to the right of it changes based on the menu item
     */
    public BorderPane buildMainHomeLayout(){
        BorderPane mainHomeLayout = new BorderPane();

        //this is where whatever page is selected from the sidebar will show

        return mainHomeLayout;
    }

    public MainLayout(){
        setLeft(visualElements.mainSideBar());
        setCenter(buildMainHomeLayout());
    }

    /** Show the home screen layout, which is currently an empty placeholder. */
    public void handleHomeNavigation() {
        // Instantiate your Home view and set it to the center
        showStep(new edu.stedwards.matt.ui.layouts.application.ApplicationHome());
    }

    /** Navigate to Main - Templates - Home. */
    public void handleTemplatesNavigation() {
        BorderPane templatesPlaceholder = new BorderPane();
        templatesPlaceholder.setCenter(new Label("Templates"));
        showStep(templatesPlaceholder);
    }

    /** Navigate to Main - Applications - Home. */
    public void handleApplicationsNavigation() {
        // Use the actual applications layout class if available in this package.
        // If not, fall back to an empty placeholder so the app still compiles.
        showStep(new BorderPane());
    }

    /** Navigate to the My Info home placeholder screen. */
    public void handleMyInfoNavigation() {
        BorderPane myInfoPlaceholder = new BorderPane();
        myInfoPlaceholder.setCenter(new Label("My Info"));
        showStep(myInfoPlaceholder);
    }

    /** Navigate to the Settings placeholder screen. */
    public void handleSettingsNavigation() {
        BorderPane settingsPlaceholder = new BorderPane();
        settingsPlaceholder.setCenter(new Label("Settings"));
        showStep(settingsPlaceholder);
    }
}
