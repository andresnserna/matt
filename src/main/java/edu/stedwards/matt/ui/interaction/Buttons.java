package edu.stedwards.matt.ui.interaction;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import edu.stedwards.matt.ui.labels.OnboardingScreenText;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
// import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class Buttons {

   /**
    * text only button
    */
	public static Button button(String text, EventHandler<ActionEvent> onAction, double width, String... styleClasses) {
		Button button = new Button(text);

		if (onAction != null) {
			button.setOnAction(onAction);
		}

      button.setPrefWidth(width);
      button.setMinWidth(width);
      button.setMaxWidth(width);

		button.getStyleClass().addAll(styleClasses);
		return button;
	}

   /**
    * text and icon button
    */
   public static Button button(FontAwesomeSolid iconType, String text, EventHandler<ActionEvent> onAction, double width, String... styleClasses) {
      Button button = new Button(text);

      if (onAction != null) {
         button.setOnAction(onAction);
      }

      FontIcon icon = new FontIcon(iconType);
      // icon.setIconSize(16);
      // icon.setIconColor(Color.web("#006D77")); // < decided in the styleclass
      icon.getStyleClass().addAll(styleClasses);

      button.setGraphic(icon);
      button.setPrefWidth(width);
      button.setMinWidth(width);
      button.setMaxWidth(width);

      button.getStyleClass().addAll(styleClasses);
      return button;
   }

   /**
    * icon only button
    */
   public static Button button(FontAwesomeSolid iconType, EventHandler<ActionEvent> onAction, String... styleClasses) {
      Button button = new Button();

      if (onAction != null) {
         button.setOnAction(onAction);
      }

      FontIcon icon = new FontIcon(iconType);
      // icon.setIconSize(16);
      // icon.setIconColor(Color.web("#006D77")); // < decided in the styleclass
      icon.getStyleClass().addAll(styleClasses);
      button.setGraphic(icon);

      button.getStyleClass().addAll(styleClasses);
      return button;
   }

	public HBox buildNextButton() {
      Button next = Buttons.button(
            FontAwesomeSolid.CHEVRON_RIGHT,
            OnboardingScreenText.BUTTON_NEXT,
            null,
            Region.USE_COMPUTED_SIZE,
            "onboarding-next-button");
      HBox footer = new HBox(next);
      footer.setAlignment(Pos.CENTER_RIGHT);
      return footer;
   }

   public HBox buildUploadButtonGroup(int width, int spacing, EventHandler<ActionEvent> onUpload) {
      Button upload = Buttons.button("Upload", onUpload, width, "resume-upload-button");

      FontIcon uploadIcon = new FontIcon(FontAwesomeSolid.UPLOAD);
      uploadIcon.setIconSize(10);
      uploadIcon.setIconColor(Color.web("#668A8C"));
      upload.setGraphic(uploadIcon);
      upload.setContentDisplay(ContentDisplay.LEFT);
      upload.setGraphicTextGap(8);
      upload.setMaxWidth(Double.MAX_VALUE);

      Label supportedFormats = new Label(OnboardingScreenText.UPLOAD_BUTTON_SUBTITLE);
      supportedFormats.getStyleClass().add("resume-upload-supported-formats");
      supportedFormats.setTextAlignment(TextAlignment.CENTER);

      VBox controls = new VBox(spacing, upload, supportedFormats);
      controls.setAlignment(Pos.CENTER);
      controls.setMaxWidth(Double.MAX_VALUE);

      HBox group = new HBox(controls);
      group.setAlignment(Pos.CENTER);
      group.setMaxWidth(Double.MAX_VALUE);
      HBox.setHgrow(controls, Priority.ALWAYS);
      return group;
   }
}
