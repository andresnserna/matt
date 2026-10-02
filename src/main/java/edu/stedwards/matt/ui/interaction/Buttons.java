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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public final class Buttons {

	public static Button button(String text, EventHandler<ActionEvent> onAction, String... styleClasses) {
		Button button = new Button(text);

		if (onAction != null) {
			button.setOnAction(onAction);
		}

		button.getStyleClass().addAll(styleClasses);
		return button;
	}

	public HBox buildNextButton() {
      Button next = Buttons.button(OnboardingScreenText.BUTTON_NEXT, null, "onboarding-next-button");
      next.setGraphic(new Label("›"));
      next.setContentDisplay(ContentDisplay.RIGHT);
      next.setGraphicTextGap(8);

      HBox footer = new HBox(next);
      footer.setAlignment(Pos.CENTER_RIGHT);
      return footer;
   }

   public HBox buildUploadButtonGroup(int width, int spacing, EventHandler<ActionEvent> onUpload) {
      Button upload = Buttons.button("Upload", onUpload, "resume-upload-button");
      FontIcon uploadIcon = new FontIcon(FontAwesomeSolid.UPLOAD);
      uploadIcon.setIconSize(10);
      uploadIcon.setIconColor(Color.web("#668A8C"));
      upload.setGraphic(uploadIcon);
      upload.setContentDisplay(ContentDisplay.LEFT);
      upload.setGraphicTextGap(8);
      upload.setPrefWidth(width);
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
