/**
 * Architecture layer: view
 * JavaFX layout for the home/dashboard screen.
 */
package edu.stedwards.matt.ui.layouts.other;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.layouts.application.ApplicationHome;
import edu.stedwards.matt.ui.layouts.template.TemplatesHome;
import edu.stedwards.matt.ui.layouts.myinfo.MyInfoLayout;


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
        setLeft(visualElements.mainSideBar(this));
        setCenter(buildMainHomeLayout());
    }

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
