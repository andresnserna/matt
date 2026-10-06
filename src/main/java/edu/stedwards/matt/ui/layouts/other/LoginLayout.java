/**
 * Architecture layer: view
 * JavaFX layout for the login screen.
 */
package edu.stedwards.matt.ui.layouts.other;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import edu.stedwards.matt.ui.labels.LoginScreenText;

/**
 * Builds the login layout for user sign-in and authentication entry.
 * This layout is created by the app flow when navigation reaches the login view,
 * and it is handed to a BaseWindow to display in a stage.
 * It is not responsible for validating credentials against the database directly;
 * that belongs to the service and DAO layers.
 */
public class LoginLayout extends VBox {
    public LoginLayout() {
        setSpacing(12);
        setAlignment(Pos.CENTER);

        Label title = new Label(LoginScreenText.LOGIN_TITLE);
        title.getStyleClass().add("title-label");

        Button loginButton = new Button(LoginScreenText.LOGIN_BUTTON);
        loginButton.getStyleClass().add("primary-button");

        getChildren().addAll(title, loginButton);
    }
}
