package edu.stedwards.matt.ui.layouts.template;

import edu.stedwards.matt.ui.TextStyles;
import edu.stedwards.matt.ui.interaction.Buttons;
import edu.stedwards.matt.ui.labels.TemplatesScreenText;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextFlow;

public class TemplatesUploadOrNew extends BorderPane {

   public TemplatesUploadOrNew(){
      getStyleClass().addAll("screen-padding");
      
      TextFlow title = TextStyles.buildTitle(TemplatesScreenText.PLACEHOLDER_TITLE, "wizard-title");
      setTop(title);
      
      // setCenter(buildWelcomeMessage());
      
      setBottom(new Buttons().buildNextButton());
   }

}
