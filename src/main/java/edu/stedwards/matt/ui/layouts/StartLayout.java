/**
 * Architecture layer: view
 * JavaFX layout for the Start screen.
 */
package edu.stedwards.matt.ui.layouts;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import edu.stedwards.matt.ui.labels.StartScreenText;
import edu.stedwards.matt.ui.labels.TextStyles;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Builds the initial start layout for the app upon launch
 * This layout is created by the application bootstrap code or a navigation
 * controller,
 * then passed into a BaseWindow as the root content for the starting view.
 * It is not responsible for database access or window lifecycle management.
 */

public class StartLayout extends HBox {
    private ImageView startScreenMattLogo() {
        Image image = new Image(
                getClass().getResource("src/main/resources/images/matt-logo-1.svg").toExternalForm());

        ImageView logo = new ImageView(image);
        logo.getStyleClass().add("start-screen-logo");
        logo.setPreserveRatio(true);
        logo.setFitWidth(220);

        return logo;
    }

    private HBox buildLogoRow() {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);

        ImageView logo = startScreenMattLogo();
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

    private HBox buildLoadingBar() { //TODO: what object does an animated icon take in?
        HBox row = new HBox();

        return row;
    }

    private Label createdBy() {
        Label label = new Label(StartScreenText.CREATED_BY);
        label.getStyleClass().add("instruction-text");
        return label;
    }
    
    public StartLayout(String loadingItem) {
        getChildren().addAll(
            // matt logo
            buildLogoRow(),
            // loading text + loading item
            buildLoadingText("ITEM_TO_LOAD"),
            // loading bar (animated)
            buildLoadingBar(),
            // created by
            createdBy()
        );
    }
}