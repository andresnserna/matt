package edu.stedwards.matt.ui.layouts.application;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.labels.ApplicationScreenText;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextFlow;

public class ApplicationUploadJob extends BorderPane {

   public ApplicationUploadJob(){
      getStyleClass().addAll("screen-padding");
      
      TextFlow title = TextStyles.buildTitle(ApplicationScreenText.PLACEHOLDER_TITLE, "wizard-title");
      setTop(title);
      
      // setCenter(buildWelcomeMessage());
      
      setBottom(new Buttons().buildNextButton());
   }

}
