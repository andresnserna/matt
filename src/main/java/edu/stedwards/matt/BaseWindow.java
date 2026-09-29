/**
 * Architecture layer: view
 * JavaFX window shell used to display a scene inside a Stage.
 */
package edu.stedwards.matt;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Owns the window shell: the Stage, Scene, size, and CSS setup.
 * This class is called by application code when a new window or dialog needs to be created,
 * and it is responsible for wiring a JavaFX layout root into a Stage.
 * It is not responsible for business logic, database access, or screen-specific behavior.
 */
public class BaseWindow {
    private final Stage stage;
    private final Scene scene;

    public BaseWindow(String title, double width, double height) {
        this(title, width, height, new javafx.scene.layout.StackPane());
    }

    public BaseWindow(String title, double width, double height, Parent root) {
        this.stage = new Stage();
        this.stage.setTitle(title);
        this.scene = new Scene(root, width, height);
        AppStyles.apply(this.scene);
        this.stage.setScene(this.scene);
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    public void show() {
        stage.show();
    }

    public void showAndWait() {
        stage.showAndWait();
    }
}
