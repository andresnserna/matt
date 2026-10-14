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
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.TextStyles;

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

    /**
     * ?
     */
    private HBox buildLogoRow() {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);
        FXSVGCanvas logo = VisualElements.startScreenMattLogo();
        row.getChildren().add(logo);
        return row;
    }

    /**
     * ?
     */
    private HBox buildLoadingText(String loadingItem) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);
        Label prefix = TextStyles.buildLabel(StartScreenText.SUBTITLE_LOADING, "start-screen-loading-prefix");
        Label item = TextStyles.buildLabel(loadingItem, "start-screen-loading-item");
        row.getChildren().addAll(prefix, item);
        return row;
    }

    /**
     * ?
     */
    private HBox buildCreatedBy() {
        Label label = TextStyles.buildLabel(StartScreenText.CREATED_BY, "start-screen-created-by");
        HBox footer = new HBox(label);
        footer.setAlignment(Pos.CENTER);
        return footer;
    }

    /**
     * ?
     */
    private VBox buildStartupContent(String loadingItem) {
        VBox content = new VBox(20,
                buildLogoRow(),
                buildLoadingText(loadingItem),
                VisualElements.buildLoadingBar(loadingProgress, loadingPercentage));
        content.setAlignment(Pos.CENTER);
        return content;
    }

    private Timeline buildStartupAnimation() {
        return new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(loadingProgress.progressProperty(), 0)),
                new KeyFrame(Duration.millis(100), new KeyValue(loadingProgress.progressProperty(), 0.01)),
                new KeyFrame(Duration.seconds(1.5), new KeyValue(loadingProgress.progressProperty(), 1)));
    }
    
    /**
     * ?
     */
    public StartLayout(String loadingItem) {
        this(loadingItem, () -> { });
    }

    /**
     * ?
     */
    public StartLayout(String loadingItem, Runnable onFinished) {
        getStyleClass().add("start-screen");
        setCenter(buildStartupContent(loadingItem));
        setBottom(buildCreatedBy());
        startupAnimation = buildStartupAnimation();
        startupAnimation.setOnFinished(event -> onFinished.run());
        startupAnimation.play();
    }

    /**
     * ?
     */
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