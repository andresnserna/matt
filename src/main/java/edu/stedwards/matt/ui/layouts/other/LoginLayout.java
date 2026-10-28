/**
 * Architecture layer: view
 * JavaFX layout for the login screen.
 */
package edu.stedwards.matt.ui.layouts.other;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import edu.stedwards.matt.ui.interaction.VisualElements;

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
        getStyleClass().add("login-background");

        //logo up 
        getChildren().add(VisualElements.buildLoginBox(null, null, 342, 206, 246));
    }
}
