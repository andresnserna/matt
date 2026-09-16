package edu.stedwards.matt;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Matt extends Application {

    @Override
    public void start(Stage stage) {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        Label title = new Label("JavaFX app");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Running on Java " + javaVersion + " with JavaFX " + javafxVersion + ".");
        subtitle.getStyleClass().add("subtitle-label");

        Button openWindow = new Button("Open reusable window");
        openWindow.getStyleClass().add("primary-button");
        openWindow.setOnAction(event -> {
            BaseWindow window = new BaseWindow("Child window", 420, 260);
            window.show();
        });

        VBox root = new VBox(16, title, subtitle, openWindow);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("app-root");

        stage.setTitle("Matt");
        stage.setScene(new javafx.scene.Scene(root, 640, 480));
        AppStyles.apply(stage.getScene());
        
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}