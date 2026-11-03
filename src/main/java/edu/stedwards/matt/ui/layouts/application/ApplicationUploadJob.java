package edu.stedwards.matt.ui.layouts.application;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.ApplicationScreenText;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class ApplicationUploadJob extends BorderPane {

   public ApplicationUploadJob(){
      getStyleClass().addAll("screen-padding");
      
      TextFlow title = TextStyles.buildTitle(ApplicationScreenText.NEW_APPLICATION_TITLE, "screen-title");
      setTop(title);
      
      Label instruction = new Label(ApplicationScreenText.NEW_APPLICATION_UPLOAD_MESSAGE);
      instruction.getStyleClass().add("onboarding-wizard-subtitle");
      instruction.setWrapText(true);
      instruction.setMaxWidth(Double.MAX_VALUE);

      VBox uploadOptions = VisualElements.buildUploadMethodGroup3(800);
      uploadOptions.prefWidthProperty().bind(
            Bindings.min(800, Bindings.max(0, widthProperty().subtract(100))));
      uploadOptions.maxWidthProperty().bind(uploadOptions.prefWidthProperty());
      VBox content = new VBox(44, instruction, uploadOptions);
      content.setFillWidth(true);
      content.setMaxWidth(Double.MAX_VALUE);
      content.setPadding(new Insets(105, 0, 24, 0));

      var scrollContent = VisualElements.wizardScrollable(content);
      setCenter(scrollContent);
      BorderPane.setMargin(scrollContent, new Insets(0, 0, 24, 0));
      
      setBottom(new Buttons().buildNextButton());
   }

   /** Open the file chooser, validate the selected file type, and prepare the file for parsing. */
   public void handleUpload() {
      // TODO: javi implement this button
   }

   /** Retain the entered job URL and send it for parsing only when Next is pressed. */
   public void handleJobUrlEntry() {
      // TODO: javi implement this button
   }

   /** Retain the entered job-post text and send it for parsing only when Next is pressed. */
   public void handleJobPostTextEntry() {
      // TODO: javi implement this button
   }

   /** Send the selected job posting to the parser and navigate to Main - Applications - Confirm Fields with parsed data. */
   public void handleNext() {
      // TODO: javi implement this button
   }

}
