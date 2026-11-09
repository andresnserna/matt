/**
 * Architecture layer: view
 * JavaFX layout for the login screen.
 */
package edu.stedwards.matt.ui.layouts.other;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import edu.stedwards.matt.dao.UserDAO;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.layouts.onboarding.OnboardingWelcome;

/**
 * Builds the login layout for user sign-in and authentication entry.
 * This layout is created by the app flow when navigation reaches the login view,
 * and it is handed to a BaseWindow to display in a stage.
 * It is not responsible for validating credentials against the database directly;
 * that belongs to the service and DAO layers.
 */
public class LoginLayout extends VBox {
    
    private final Label errorLabel = new Label();

    public LoginLayout() {
        setSpacing(12);
        setAlignment(Pos.CENTER);
        getStyleClass().add("login-background");

        errorLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");

        // Pass our handleLogin and handleSignup methods directly into the builder
        getChildren().add(VisualElements.buildLoginBox(
            event -> handleLogin("", ""), 
            event -> handleSignup(), 
            342, 206, 246
        ));

        getChildren().add(errorLabel);
    }

    /** When Remember Me is selected, save the username in user settings for the next logged-out startup. */
    public void handleRememberMe() {
        // Typically involves saving a boolean to a local properties file or java.util.prefs.Preferences
        System.out.println("Remember Me toggled.");
    }

    /** Log the user in and start an app session with that user's data from the database. */
    public void handleLogin(String username, String password) {
        errorLabel.setText("");
        
        // 1. Check the database
        boolean isAuthenticated = new UserDAO().authenticateUser(username, password);
        
        if (isAuthenticated) {
            // 2. Success: route the user straight to the main app dashboard
            MainLayout mainLayout = new MainLayout();
            mainLayout.showStep(new edu.stedwards.matt.ui.layouts.application.ApplicationHome());
            getScene().setRoot(mainLayout);
        } else {
            // 3. Failure: notify the user on the UI
            errorLabel.setText("Invalid username or password.");
        }
    }

    /** Open the sign-up flow. */
    public void handleSignup() {
        // Route brand new users directly into the onboarding wizard!
        OnboardingWelcome welcomeScreen = new OnboardingWelcome();
        getScene().setRoot(welcomeScreen);
    }
}
