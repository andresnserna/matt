package edu.stedwards.matt.ui.layouts.application;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.interaction.VisualElements;
import edu.stedwards.matt.ui.labels.ApplicationScreenText;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class ApplicationUploadJob extends BorderPane {

   public ApplicationUploadJob(){
      getStyleClass().addAll("screen-padding");
      
      TextFlow title = TextStyles.buildTitle(ApplicationScreenText.PLACEHOLDER_TITLE, "screen-title");
      setTop(title);
      
      var scrollContent = VisualElements.wizardScrollable(new VBox());
      setCenter(scrollContent);
      BorderPane.setMargin(scrollContent, new Insets(0, 0, 24, 0));
      
      setBottom(new Buttons().buildNextButton());
   }

}
