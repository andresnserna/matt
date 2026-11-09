package edu.stedwards.matt.controllers;

import edu.stedwards.matt.dao.UserDAO;
import java.util.prefs.Preferences;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckBox;
    
    // Initialize Java Preferences for this specific class
    private final Preferences prefs = Preferences.userNodeForPackage(LoginController.class);
    private static final String PREF_USERNAME = "rememberedUsername";

    @FXML
    public void initialize() {
        // Auto-fill from remembered OS settings
        String lastUser = prefs.get(PREF_USERNAME, null);
        if (lastUser != null && !lastUser.isEmpty()) {
            usernameField.setText(lastUser);
            rememberMeCheckBox.setSelected(true);
        }
    }

    @FXML
    public void handleLoginButton(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        UserDAO userDAO = new UserDAO();
        if (userDAO.authenticateUser(username, password)) {
            
            // Save or clear the username in OS Preferences based on the checkbox
            if (rememberMeCheckBox.isSelected()) {
                prefs.put(PREF_USERNAME, username);
            } else {
                prefs.remove(PREF_USERNAME);
            }
            
            // Navigate to main application home
            navigateToMainApp(event);
        } else {
            System.err.println("Invalid login credentials.");
        }
    }

    @FXML
    public void handleSignUpButton() {
        // Opens the sign up dialog window
        openDialog("signUpWindow.fxml");
    }
    
    private void navigateToMainApp(ActionEvent event) { 
        edu.stedwards.matt.ui.layouts.other.MainLayout mainLayout = new edu.stedwards.matt.ui.layouts.other.MainLayout();
        mainLayout.showStep(new edu.stedwards.matt.ui.layouts.application.ApplicationHome());
        ((Node) event.getSource()).getScene().setRoot(mainLayout);
    }
    
    private void openDialog(String fxmlFile) { /* Implementation */ }
}