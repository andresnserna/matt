/**
 * Architecture layer: view
 * JavaFX layout for the Start screen.
 */
package edu.stedwards.matt.ui.layouts.other;

import com.github.weisj.jsvg.parser.SVGLoader;
import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.ui.jfx.FXSVGCanvas;
import java.net.URL;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import edu.stedwards.matt.ui.labels.StartScreenText;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Builds the initial start layout for the app upon launch
 * This layout is created by the application bootstrap code or a navigation
 * controller,
 * then passed into a BaseWindow as the root content for the starting view.
 * It is not responsible for database access or window lifecycle management.
 */

public class StartLayout extends BorderPane {
    private final ProgressBar loadingProgress = new ProgressBar(0);
    private final Label loadingPercentage = new Label("0%");
    private Timeline startupAnimation;

    private FXSVGCanvas startScreenMattLogo() {
        URL logoResource = getClass().getResource("/images/matt-logo-1.svg");
        if (logoResource == null) {
            System.err.println("[StartLayout] ERROR: /images/matt-logo-1.svg was not found on the classpath.");
            throw new IllegalStateException("Matt logo resource not found");
        }

        SVGDocument document;
        try {
            document = new SVGLoader().load(logoResource);
        } catch (RuntimeException exception) {
            System.err.println("[StartLayout] ERROR: JSVG failed to parse the Matt logo.");
            exception.printStackTrace();
            throw exception;
        }
        if (document == null) {
            System.err.println("[StartLayout] ERROR: JSVG returned no document for the Matt logo.");
            throw new IllegalStateException("JSVG returned no document for the Matt logo");
        }

        FXSVGCanvas logo = new FXSVGCanvas();
        logo.setRenderBackend(FXSVGCanvas.RenderBackend.AWT);
        logo.setDocument(document);
        logo.setShowTransparentPattern(false);
        logo.getStyleClass().add("start-screen-logo");
        logo.setMinSize(220, 157);
        logo.setPrefSize(220, 157);
        logo.setMaxSize(220, 157);
        System.out.println("[StartLayout] JSVG parsed the Matt logo and attached it to the AWT renderer.");
        reportLogoRender(logo);

        return logo;
    }

    private void reportLogoRender(FXSVGCanvas logo) {
        logo.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene == null) {
                return;
            }

            Platform.runLater(() -> {
                try {
                    logo.applyCss();
                    logo.layout();
                    WritableImage snapshot = logo.snapshot(new SnapshotParameters(), null);
                    PixelReader pixels = snapshot.getPixelReader();
                    boolean containsLogoPixels = false;

                    for (int y = 0; y < snapshot.getHeight() && !containsLogoPixels; y++) {
                        for (int x = 0; x < snapshot.getWidth(); x++) {
                            Color color = pixels.getColor(x, y);
                            if (color.getOpacity() > 0.2 && color.getBrightness() < 0.95) {
                                containsLogoPixels = true;
                                break;
                            }
                        }
                    }

                    if (containsLogoPixels) {
                        System.out.println("[StartLayout] Logo rendered by JSVG: visible pixels found in canvas snapshot.");
                    } else {
                        System.err.printf("[StartLayout] ERROR: JSVG canvas snapshot is blank (%s x %s).%n",
                                snapshot.getWidth(), snapshot.getHeight());
                    }
                } catch (RuntimeException exception) {
                    System.err.println("[StartLayout] ERROR: Could not verify the JSVG logo render.");
                    exception.printStackTrace();
                }
            });
        });
    }

    private HBox buildLogoRow() {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);

        FXSVGCanvas logo = startScreenMattLogo();
        row.getChildren().add(logo);

        return row;
    }

    private HBox buildLoadingText(String loadingItem) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);

        Label prefix = new Label(StartScreenText.SUBTITLE_LOADING);
        prefix.getStyleClass().add("start-screen-loading-prefix");

        Label item = new Label(loadingItem);
        item.getStyleClass().add("start-screen-loading-item");

        row.getChildren().addAll(prefix, item);
        return row;
    }

    private HBox buildLoadingBar() {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER);

        loadingProgress.setPrefWidth(260);
        loadingProgress.getStyleClass().add("start-screen-progress");
        loadingPercentage.getStyleClass().add("start-screen-loading-percentage");
        loadingProgress.progressProperty()
            .addListener((observable, oldProgress, newProgress) ->
                loadingPercentage.setText(Math.round(newProgress.doubleValue() * 100) + "%"));

        startupAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(loadingProgress.progressProperty(), 0)),
                new KeyFrame(Duration.millis(100), new KeyValue(loadingProgress.progressProperty(), 0.01)),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(loadingProgress.progressProperty(), 1)));
        row.getChildren().addAll(loadingProgress, loadingPercentage);

        return row;
    }

    private HBox buildCreatedBy() {
        Label label = new Label(StartScreenText.CREATED_BY);
        label.getStyleClass().add("start-screen-created-by");

        HBox footer = new HBox(label);
        footer.setAlignment(Pos.CENTER);
        return footer;
    }

    private VBox buildStartupContent(String loadingItem) {
        VBox content = new VBox(20,
                buildLogoRow(),
                buildLoadingText(loadingItem),
                buildLoadingBar());
        content.setAlignment(Pos.CENTER);
        return content;
    }
    
    public StartLayout(String loadingItem) {
        this(loadingItem, () -> { });
    }

    public StartLayout(String loadingItem, Runnable onFinished) {
        getStyleClass().add("start-screen");
        setCenter(buildStartupContent(loadingItem));
        setBottom(buildCreatedBy());
        startupAnimation.setOnFinished(event -> onFinished.run());
        startupAnimation.play();
    }

    public void updateLoadingProgress(double progress) {
        double boundedProgress = Math.max(0, Math.min(1, progress));
        Runnable update = () -> {
            startupAnimation.stop();
            loadingProgress.setProgress(boundedProgress);
        };

        if (Platform.isFxApplicationThread()) {
            update.run();
        } else {
            Platform.runLater(update);
        }
    }
}