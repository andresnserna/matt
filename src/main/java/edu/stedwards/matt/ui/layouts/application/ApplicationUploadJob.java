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

}
